package lms.auth;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Nạp mapping role -> permission từ DB vào bộ nhớ (ít thay đổi, đọc rất nhiều).
 * Gọi reload() nếu sau này có màn hình sửa phân quyền.
 */
@Service
public class PermissionService {

    private static final Logger log = LoggerFactory.getLogger(PermissionService.class);

    private final JdbcTemplate jdbc;
    private volatile Map<Role, Set<String>> cache = Map.of();

    public PermissionService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void reload() {
        Map<Role, Set<String>> map = new EnumMap<>(Role.class);
        jdbc.query("""
                SELECT rp.role_id, p.code
                FROM role_permissions rp
                JOIN permissions p ON p.id = rp.permission_id
                """, rs -> {
            Role role = Role.fromCode(rs.getInt("role_id"));
            map.computeIfAbsent(role, r -> new HashSet<>()).add(rs.getString("code"));
        });
        Map<Role, Set<String>> frozen = new EnumMap<>(Role.class);
        map.forEach((role, codes) -> frozen.put(role, Set.copyOf(codes)));
        this.cache = Collections.unmodifiableMap(frozen);
        log.info("Loaded permissions for {} roles", frozen.size());
    }

    public boolean has(Role role, String permission) {
        return permissionsOf(role).contains(permission);
    }

    public Set<String> permissionsOf(Role role) {
        return cache.getOrDefault(role, Set.of());
    }
}
