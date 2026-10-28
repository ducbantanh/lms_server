package lms.proctor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Danh sách IP / dải CIDR được vào thi, vd. {@code "192.168.10.0/24, 10.0.0.5, 2001:db8::/32"}: ngăn bằng dấu
 * phẩy, chấm phẩy, khoảng trắng hoặc xuống dòng. Chỉ nhận địa chỉ dạng số (không phân giải tên miền). IPv4 chỉ khớp
 * IPv4, IPv6 chỉ khớp IPv6. Java thuần để test không cần Spring.
 */
public final class IpRanges {

    public static final int MAX_ENTRIES = 20;
    private static final Pattern SEPARATORS = Pattern.compile("[,;\\s]+");
    private static final Pattern IPV4 = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3}");
    private static final Pattern IPV6 = Pattern.compile("[0-9A-Fa-f:.]*:[0-9A-Fa-f:.]*");

    private record Range(byte[] network, int prefix) {

        boolean contains(byte[] address) {
            if (address.length != network.length) {
                return false;
            }
            int full = prefix / 8;
            for (int i = 0; i < full; i++) {
                if (address[i] != network[i]) {
                    return false;
                }
            }
            int rest = prefix % 8;
            if (rest == 0) {
                return true;
            }
            int mask = (0xFF << (8 - rest)) & 0xFF;
            return (address[full] & mask) == (network[full] & mask);
        }
    }

    private final List<Range> ranges;

    private IpRanges(List<Range> ranges) {
        this.ranges = ranges;
    }

    /** Danh sách rỗng (text null/trống) = không giới hạn mạng. */
    public static IpRanges parse(String text) {
        List<Range> list = new ArrayList<>();
        for (String entry : entries(text)) {
            Range r = range(entry);
            if (r == null) {
                throw new IllegalArgumentException("Invalid IP range: " + entry);
            }
            list.add(r);
        }
        return new IpRanges(List.copyOf(list));
    }

    /** Các mục viết sai (để báo lại cho giảng viên); rỗng = hợp lệ. */
    public static List<String> invalidEntries(String text) {
        return entries(text).stream().filter(e -> range(e) == null).toList();
    }

    /** Dạng lưu DB: các mục ngăn bằng ", "; null nếu không có mục nào. */
    public static String normalize(String text) {
        List<String> list = entries(text);
        return list.isEmpty() ? null : String.join(", ", list);
    }

    public static List<String> entries(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return List.of(SEPARATORS.split(text.strip()));
    }

    public boolean isEmpty() {
        return ranges.isEmpty();
    }

    /** Không giới hạn mạng thì mọi IP đều được; IP không đọc được thì không khớp. */
    public boolean allows(String ip) {
        if (ranges.isEmpty()) {
            return true;
        }
        byte[] address = literal(ip);
        return address != null && ranges.stream().anyMatch(r -> r.contains(address));
    }

    private static Range range(String entry) {
        int slash = entry.indexOf('/');
        String host = slash < 0 ? entry : entry.substring(0, slash);
        byte[] address = literal(host);
        if (address == null) {
            return null;
        }
        int max = address.length * 8;
        int prefix = max;
        if (slash >= 0) {
            String bits = entry.substring(slash + 1);
            if (!bits.matches("\\d{1,3}")) {
                return null;
            }
            prefix = Integer.parseInt(bits);
            if (prefix > max) {
                return null;
            }
        }
        return new Range(address, prefix);
    }

    /** Byte của địa chỉ dạng số; null nếu không phải IP (không bao giờ tra DNS). */
    static byte[] literal(String text) {
        if (text == null || text.isEmpty() || text.length() > 45) {
            return null;
        }
        if (IPV4.matcher(text).matches()) {
            String[] parts = text.split("\\.");
            byte[] out = new byte[4];
            for (int i = 0; i < 4; i++) {
                int v = Integer.parseInt(parts[i]);
                if (v > 255) {
                    return null;
                }
                out[i] = (byte) v;
            }
            return out;
        }
        if (!IPV6.matcher(text).matches()) {
            return null;
        }
        try {
            // Chuỗi chỉ gồm số hex, ':' và '.' nên InetAddress đọc như địa chỉ, không phân giải tên
            return InetAddress.getByName(text).getAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
