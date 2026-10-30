package lms.security.threat;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import lms.common.ApiResponse;

/**
 * REST API giám sát và phát hiện các nguy cơ bảo mật, payload độc hại.
 */
@RestController
@RequestMapping("/api/v1/security/threats")
public class SecurityThreatController {

    private final ThreatDetectionService threatService;

    public SecurityThreatController(ThreatDetectionService threatService) {
        this.threatService = threatService;
    }

    public record InspectPayloadRequest(String parameterName, String payload) {}

    @PostMapping("/inspect")
    public ApiResponse<ThreatDetectionResult> inspectPayload(@RequestBody InspectPayloadRequest req) {
        String param = req != null && req.parameterName() != null ? req.parameterName() : "input";
        String payload = req != null ? req.payload() : "";
        return ApiResponse.ok(threatService.inspectText(param, payload));
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Long>> getThreatStats() {
        return ApiResponse.ok(threatService.getThreatStatistics());
    }

    @GetMapping("/recent")
    public ApiResponse<List<ThreatDetectionResult.ThreatIncident>> getRecentIncidents() {
        return ApiResponse.ok(threatService.getRecentIncidents());
    }
}
