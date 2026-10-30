package lms.security.threat;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Động cơ phát hiện và ngăn chặn các mối đe dọa an ninh mạng Web Application Firewall (WAF)
 * dựa trên chữ ký luật nhận diện OWASP Top 10.
 */
@Service
public class ThreatDetectionService {

    private static final Logger log = LoggerFactory.getLogger(ThreatDetectionService.class);

    private static final Pattern SQLI_PATTERN = Pattern.compile(
            "(?i)(--|\\bunion\\s+(all\\s+)?select\\b|\\bselect\\b.*\\bfrom\\b|\\bdrop\\s+table\\b|'\\s*or\\s*['\"0-9a-z]+=|<=>|;\\s*shutdown\\b|\\binsert\\s+into\\b.*\\bvalues\\b)"
    );

    private static final Pattern XSS_PATTERN = Pattern.compile(
            "(?i)(<script\\b|javascript:|onerror\\s*=|onload\\s*=|onclick\\s*=|document\\.cookie|<iframe\\b|<svg\\b.*\\bonload\\b|<img\\b[^>]*src\\s*=\\s*['\"]?javascript)"
    );

    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
            "(?i)(\\.\\./|\\.\\.\\\\|%2e%2e%2f|%2e%2e\\\\|%2e%2e%252f|\\.\\.%2f|/etc/passwd|c:\\\\windows)"
    );

    private static final Pattern CMD_INJECTION_PATTERN = Pattern.compile(
            "(?i)(;\\s*(cat|ls|rm|wget|curl|whoami|id|bash|sh|powershell)\\b|\\|\\s*(cat|ls|rm|curl|powershell)\\b|`[^`]+`)"
    );

    private static final Pattern SCANNER_PATTERN = Pattern.compile(
            "(?i)(sqlmap|nikto|dirbuster|acunetix|nessus|gobuster|wpscan|masscan|zgrab)"
    );

    private final Map<ThreatType, AtomicLong> threatCounters = new ConcurrentHashMap<>();
    private final Deque<ThreatDetectionResult.ThreatIncident> recentIncidents = new ConcurrentLinkedDeque<>();
    private static final int MAX_RECENT_INCIDENTS = 100;

    public ThreatDetectionService() {
        for (ThreatType type : ThreatType.values()) {
            threatCounters.put(type, new AtomicLong(0));
        }
    }

    public ThreatDetectionResult inspectText(String paramName, String input) {
        if (input == null || input.isBlank()) {
            return new ThreatDetectionResult(false, null, List.of(), Instant.now());
        }

        List<ThreatDetectionResult.ThreatIncident> incidents = new ArrayList<>();

        checkPattern(SQLI_PATTERN, input, paramName, ThreatType.SQL_INJECTION, incidents);
        checkPattern(XSS_PATTERN, input, paramName, ThreatType.CROSS_SITE_SCRIPTING, incidents);
        checkPattern(PATH_TRAVERSAL_PATTERN, input, paramName, ThreatType.PATH_TRAVERSAL, incidents);
        checkPattern(CMD_INJECTION_PATTERN, input, paramName, ThreatType.COMMAND_INJECTION, incidents);

        return buildResult(incidents);
    }

    public ThreatDetectionResult inspectParameters(Map<String, String[]> parameterMap) {
        if (parameterMap == null || parameterMap.isEmpty()) {
            return new ThreatDetectionResult(false, null, List.of(), Instant.now());
        }

        List<ThreatDetectionResult.ThreatIncident> incidents = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String paramName = entry.getKey();
            String[] values = entry.getValue();
            if (values == null) continue;

            for (String val : values) {
                if (val == null || val.isBlank()) continue;
                checkPattern(SQLI_PATTERN, val, paramName, ThreatType.SQL_INJECTION, incidents);
                checkPattern(XSS_PATTERN, val, paramName, ThreatType.CROSS_SITE_SCRIPTING, incidents);
                checkPattern(PATH_TRAVERSAL_PATTERN, val, paramName, ThreatType.PATH_TRAVERSAL, incidents);
                checkPattern(CMD_INJECTION_PATTERN, val, paramName, ThreatType.COMMAND_INJECTION, incidents);
            }
        }

        return buildResult(incidents);
    }

    public ThreatDetectionResult inspectUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return new ThreatDetectionResult(false, null, List.of(), Instant.now());
        }

        List<ThreatDetectionResult.ThreatIncident> incidents = new ArrayList<>();
        checkPattern(SCANNER_PATTERN, userAgent, "User-Agent", ThreatType.SUSPICIOUS_SCANNER, incidents);
        return buildResult(incidents);
    }

    private void checkPattern(
            Pattern pattern,
            String text,
            String paramName,
            ThreatType type,
            List<ThreatDetectionResult.ThreatIncident> incidents
    ) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String match = matcher.group();
            var incident = new ThreatDetectionResult.ThreatIncident(
                    type,
                    type.getDefaultSeverity(),
                    match,
                    paramName
            );
            incidents.add(incident);
            recordIncident(incident);
        }
    }

    private void recordIncident(ThreatDetectionResult.ThreatIncident incident) {
        threatCounters.computeIfAbsent(incident.threatType(), t -> new AtomicLong(0)).incrementAndGet();

        while (recentIncidents.size() >= MAX_RECENT_INCIDENTS) {
            recentIncidents.pollLast();
        }
        recentIncidents.offerFirst(incident);

        log.warn("Cảnh báo mối đe dọa [{}]: Phát hiện mẫu '{}' trong tham số '{}'",
                incident.threatType(), incident.matchedSnippet(), incident.parameterName());
    }

    private ThreatDetectionResult buildResult(List<ThreatDetectionResult.ThreatIncident> incidents) {
        if (incidents.isEmpty()) {
            return new ThreatDetectionResult(false, null, List.of(), Instant.now());
        }

        ThreatSeverity maxSeverity = incidents.stream()
                .map(ThreatDetectionResult.ThreatIncident::severity)
                .max(Comparator.comparingInt(Enum::ordinal))
                .orElse(ThreatSeverity.LOW);

        return new ThreatDetectionResult(true, maxSeverity, incidents, Instant.now());
    }

    public Map<String, Long> getThreatStatistics() {
        Map<String, Long> stats = new LinkedHashMap<>();
        for (ThreatType type : ThreatType.values()) {
            stats.put(type.name(), threatCounters.getOrDefault(type, new AtomicLong(0)).get());
        }
        return stats;
    }

    public List<ThreatDetectionResult.ThreatIncident> getRecentIncidents() {
        return List.copyOf(recentIncidents);
    }
}
