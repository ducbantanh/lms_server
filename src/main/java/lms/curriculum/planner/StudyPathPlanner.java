package lms.curriculum.planner;

import java.util.*;

import org.springframework.stereotype.Service;

import lms.catalog.CourseCurriculumCatalog;

/**
 * Bộ lập kế hoạch học tập thông minh dựa trên giải thuật sắp xếp tô-pô (Topological Sort)
 * và phân tích đồ thị có hướng không chu trình (DAG) của các môn học tiên quyết.
 */
@Service
public class StudyPathPlanner {

    private static final Map<String, List<String>> PREREQUISITES = new HashMap<>();

    static {
        PREREQUISITES.put("INT1340", List.of()); // DSA
        PREREQUISITES.put("INT1343", List.of()); // Computer Architecture
        PREREQUISITES.put("INT1341", List.of("INT1343")); // OS <- Arch
        PREREQUISITES.put("INT1339", List.of("INT1340")); // Networks <- DSA
        PREREQUISITES.put("INT1342", List.of("INT1340")); // Databases <- DSA
        PREREQUISITES.put("INT1344", List.of("INT1340")); // SE <- DSA
        PREREQUISITES.put("INT1345", List.of("INT1340")); // AI/ML <- DSA
        PREREQUISITES.put("INT1346", List.of("INT1339", "INT1341")); // Security <- Net + OS
        PREREQUISITES.put("INT1347", List.of("INT1339", "INT1341")); // Distributed <- Net + OS
        PREREQUISITES.put("INT1348", List.of("INT1340", "INT1343")); // Compiler <- DSA + Arch
        PREREQUISITES.put("INT1349", List.of("INT1339", "INT1342")); // Web <- Net + DB
        PREREQUISITES.put("INT1350", List.of("INT1344")); // Mobile <- SE
        PREREQUISITES.put("INT1351", List.of("INT1342", "INT1347")); // BigData <- DB + Dist
        PREREQUISITES.put("INT1352", List.of("INT1340")); // Graphics <- DSA
        PREREQUISITES.put("INT1353", List.of("INT1344")); // HCI <- SE
    }

    public StudyPlanResult planForTrack(CareerTrack track, int maxCreditsPerSemester, Set<String> completedCourseCodes) {
        List<String> targetCourses = track.getRecommendedCourses();
        return generatePlan(track.getDescription(), targetCourses, maxCreditsPerSemester, completedCourseCodes);
    }

    public StudyPlanResult generatePlan(String trackName, List<String> targetCourses, int maxCredits, Set<String> completedCodes) {
        int maxCreditsPerTerm = Math.max(9, Math.min(25, maxCredits <= 0 ? 16 : maxCredits));
        Set<String> completed = completedCodes == null ? new HashSet<>() : new HashSet<>(completedCodes);

        // 1. Mở rộng tập môn học bao gồm tất cả các môn tiên quyết bắc cầu
        Set<String> allRequired = new LinkedHashSet<>();
        for (String target : targetCourses) {
            collectAllPrerequisites(target, allRequired);
        }

        // Loại bỏ các môn sinh viên đã tích lũy thành công
        Set<String> remainingCourses = new LinkedHashSet<>(allRequired);
        remainingCourses.removeAll(completed);

        // 2. Tính số lượng môn phụ thuộc (out-degree) để xác định bottleneck courses
        Map<String, Integer> dependentCounts = new HashMap<>();
        for (String c : allRequired) {
            int count = 0;
            for (String other : allRequired) {
                if (getPrerequisites(other).contains(c)) {
                    count++;
                }
            }
            dependentCounts.put(c, count);
        }

        List<String> bottleneckCourses = dependentCounts.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .limit(3)
                .toList();

        // 3. Xếp thời khóa biểu theo từng học kỳ (Kahn's Topological Batching)
        List<StudyPlanResult.SemesterSchedule> schedules = new ArrayList<>();
        Set<String> passedInPlan = new HashSet<>(completed);
        int semester = 1;
        int totalCreditsAcc = 0;

        while (!remainingCourses.isEmpty()) {
            List<String> availableThisTerm = new ArrayList<>();

            for (String code : remainingCourses) {
                List<String> prereqs = getPrerequisites(code);
                if (passedInPlan.containsAll(prereqs)) {
                    availableThisTerm.add(code);
                }
            }

            if (availableThisTerm.isEmpty()) {
                // Có chu trình tiên quyết hoặc môn học không hợp lệ
                break;
            }

            // Ưu tiên xếp các môn là nút thắt (bottleneck) lên trước
            availableThisTerm.sort((c1, c2) -> {
                int p1 = dependentCounts.getOrDefault(c1, 0);
                int p2 = dependentCounts.getOrDefault(c2, 0);
                return Integer.compare(p2, p1);
            });

            List<StudyPlanResult.CourseScheduleItem> termItems = new ArrayList<>();
            int termCredits = 0;
            List<String> enrolledThisTerm = new ArrayList<>();

            for (String code : availableThisTerm) {
                int cr = getCredits(code);
                if (termCredits + cr <= maxCreditsPerTerm || termItems.isEmpty()) {
                    termCredits += cr;
                    termItems.add(new StudyPlanResult.CourseScheduleItem(
                            code,
                            getCourseName(code),
                            cr,
                            getPrerequisites(code)
                    ));
                    enrolledThisTerm.add(code);
                }
            }

            totalCreditsAcc += termCredits;
            schedules.add(new StudyPlanResult.SemesterSchedule(semester++, termCredits, termItems));
            passedInPlan.addAll(enrolledThisTerm);
            remainingCourses.removeAll(enrolledThisTerm);
        }

        Map<String, List<String>> graphView = new LinkedHashMap<>();
        for (String c : allRequired) {
            graphView.put(c, getPrerequisites(c));
        }

        return new StudyPlanResult(
                trackName,
                schedules.size(),
                totalCreditsAcc,
                schedules,
                bottleneckCourses,
                graphView
        );
    }

    private void collectAllPrerequisites(String courseCode, Set<String> collected) {
        if (collected.contains(courseCode)) return;
        List<String> prereqs = getPrerequisites(courseCode);
        for (String p : prereqs) {
            collectAllPrerequisites(p, collected);
        }
        collected.add(courseCode);
    }

    public List<String> getPrerequisites(String courseCode) {
        return PREREQUISITES.getOrDefault(courseCode, List.of());
    }

    private int getCredits(String courseCode) {
        return CourseCurriculumCatalog.findByCourseCode(courseCode)
                .map(CourseCurriculumCatalog.CurriculumItem::credits)
                .orElse(3);
    }

    private String getCourseName(String courseCode) {
        return CourseCurriculumCatalog.findByCourseCode(courseCode)
                .map(CourseCurriculumCatalog.CurriculumItem::courseName)
                .orElse(courseCode);
    }
}
