package lms.curriculum.planner;

import java.util.List;

/**
 * Định hướng nghề nghiệp và các học phần cốt lõi tương ứng trong chương trình đào tạo CNTT.
 */
public enum CareerTrack {
    SOFTWARE_ENGINEERING(
            "Kỹ sư Phần mềm (Full-stack / Enterprise)",
            List.of("INT1340", "INT1342", "INT1344", "INT1339", "INT1349", "INT1347")
    ),
    DATA_ENGINEERING_AI(
            "Kỹ sư Dữ liệu & Trí tuệ nhân tạo",
            List.of("INT1340", "INT1342", "INT1345", "INT1351", "INT1347")
    ),
    CYBER_SECURITY(
            "Chuyên gia An toàn Thông tin",
            List.of("INT1339", "INT1341", "INT1343", "INT1346", "INT1347")
    ),
    SYSTEMS_ARCHITECTURE(
            "Kiến trúc sư Hệ thống & Điện toán đám mây",
            List.of("INT1341", "INT1343", "INT1339", "INT1347", "INT1344")
    );

    private final String description;
    private final List<String> recommendedCourses;

    CareerTrack(String description, List<String> recommendedCourses) {
        this.description = description;
        this.recommendedCourses = recommendedCourses;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRecommendedCourses() {
        return recommendedCourses;
    }
}
