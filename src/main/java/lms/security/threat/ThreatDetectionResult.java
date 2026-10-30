package lms.security.threat;

import java.time.Instant;
import java.util.List;

public record ThreatDetectionResult(
        boolean isThreatDetected,
        ThreatSeverity maxSeverity,
        List<ThreatIncident> incidents,
        Instant inspectedAt
) {
    public record ThreatIncident(
            ThreatType threatType,
            ThreatSeverity severity,
            String matchedSnippet,
            String parameterName
    ) {}
}
