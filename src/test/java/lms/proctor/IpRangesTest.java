package lms.proctor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class IpRangesTest {

    @Test
    void emptyListAllowsEveryNetwork() {
        assertTrue(IpRanges.parse(null).allows("8.8.8.8"));
        assertTrue(IpRanges.parse("  ").isEmpty());
        assertNull(IpRanges.normalize(" \n "));
    }

    @Test
    void cidrAndSingleAddresses() {
        IpRanges r = IpRanges.parse("192.168.10.0/24\n10.0.0.5; 172.16.0.0/12");
        assertTrue(r.allows("192.168.10.1"));
        assertTrue(r.allows("192.168.10.254"));
        assertFalse(r.allows("192.168.11.1"));
        assertTrue(r.allows("10.0.0.5"));
        assertFalse(r.allows("10.0.0.6"));
        assertTrue(r.allows("172.31.255.1"));
        assertFalse(r.allows("172.32.0.1"));
    }

    @Test
    void prefixNotOnByteBoundary() {
        IpRanges r = IpRanges.parse("10.1.4.0/22");
        assertTrue(r.allows("10.1.7.200"));
        assertFalse(r.allows("10.1.8.1"));
    }

    @Test
    void ipv6RangesMatchOnlyIpv6() {
        IpRanges r = IpRanges.parse("2001:db8::/32, ::1");
        assertTrue(r.allows("2001:db8:0:0:0:0:0:7"));
        assertTrue(r.allows("0:0:0:0:0:0:0:1"));
        assertFalse(r.allows("2001:db9::1"));
        assertFalse(r.allows("127.0.0.1"));
    }

    @Test
    void unreadableAddressNeverMatches() {
        IpRanges r = IpRanges.parse("0.0.0.0/0");
        assertTrue(r.allows("1.2.3.4"));
        assertFalse(r.allows(null));
        assertFalse(r.allows("example.com"));
    }

    @Test
    void invalidEntriesAreReported() {
        assertEquals(List.of("300.1.1.1", "10.0.0.0/33", "lab-a", "1.2.3.4/x"),
                IpRanges.invalidEntries("10.0.0.0/8, 300.1.1.1, 10.0.0.0/33, lab-a, 1.2.3.4/x"));
        assertThrows(IllegalArgumentException.class, () -> IpRanges.parse("phong-may-1"));
    }

    @Test
    void normalizeJoinsEntries() {
        assertEquals("192.168.1.0/24, 10.0.0.5", IpRanges.normalize(" 192.168.1.0/24 ;\n 10.0.0.5 "));
    }
}
