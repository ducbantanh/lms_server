package lms.catalog;

import java.util.*;

/**
 * Danh mục Đề cương Chương trình đào tạo Chi tiết (15 khóa học chuẩn Đại học).
 * Mỗi khóa học được cấu trúc chi tiết qua 15 tuần học tập toàn diện.
 */
public final class CourseCurriculumCatalog {

    public record WeeklyModule(
            int week,
            String topic,
            String description,
            List<String> learningObjectives,
            List<String> readings,
            List<String> labExercises,
            String assignment
    ) {}

    public record CurriculumItem(
            String courseCode,
            String courseName,
            int credits,
            String semester,
            String department,
            String overview,
            List<WeeklyModule> weeklySchedule,
            Map<String, Integer> gradingPolicy
    ) {}

    private static final Map<String, CurriculumItem> CURRICULA = new LinkedHashMap<>();

    static {
        registerCurriculum(new CurriculumItem(
                "INT1339",
                "Lập trình mạng",
                3,
                "Học kỳ 1",
                "Khoa Công nghệ Thông tin",
                "Môn học cung cấp kiến thức nền tảng và nâng cao về giao thức mạng TCP/IP, lập trình Socket (TCP, UDP), kiến trúc Client-Server, HTTP/1.1, HTTP/2, WebSocket và lập trình ứng dụng mạng phân tán trên nền tảng Java/Spring Boot.",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Lập trình mạng",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình mạng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình mạng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1340",
                "Cấu trúc dữ liệu và giải thuật",
                4,
                "Học kỳ 2",
                "Khoa Khoa học Máy tính",
                "Môn học trang bị tư duy giải thuật, phân tích độ phức tạp thời gian/không gian, các cấu trúc dữ liệu kinh điển (Mảng, Danh sách liên kết, Cây nhị phân, Đồ thị, Bảng băm) và các kỹ thuật thiết kế thuật toán (Chia để trị, Quy hoạch động, Tham lam).",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Cấu trúc dữ liệu và giải thuật",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cấu trúc dữ liệu và giải thuật.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cấu trúc dữ liệu và giải thuật"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1341",
                "Hệ điều hành",
                3,
                "Học kỳ 1",
                "Khoa Kỹ thuật Máy tính",
                "Khảo sát các nguyên lý cốt lõi của hệ điều hành: Quản lý tiến trình (Process), Đa luồng (Threading), Đồng bộ hóa và Deadlock, Quản lý bộ nhớ ảo (Paging, Segmentation), Hệ thống tập tin và cơ chế I/O trong nhân Linux.",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Hệ điều hành",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ điều hành.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ điều hành"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1342",
                "Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                3,
                "Học kỳ 2",
                "Khoa Hệ thống Thông tin",
                "Nghiên cứu mô hình thực thể liên kết E-R, đại số quan hệ, chuẩn hóa dữ liệu (1NF đến BCNF), tối ưu hóa câu truy vấn SQL, cấu trúc chỉ mục B-Tree, cơ chế kiểm soát đồng thời ACID và giao dịch trong PostgreSQL/MySQL.",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Cơ sở dữ liệu & Kỹ nghệ dữ liệu.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Cơ sở dữ liệu & Kỹ nghệ dữ liệu"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1343",
                "Kiến trúc máy tính & Vi xử lý",
                3,
                "Học kỳ 1",
                "Khoa Kỹ thuật Máy tính",
                "Kiến trúc tập lệnh RISC-V/MIPS, thiết kế đường truyền dữ liệu (Datapath) và điều khiển, kỹ thuật Pipelining, cấu trúc phân cấp bộ nhớ Cache và kiến trúc xử lý song song SIMD.",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Kiến trúc máy tính & Vi xử lý",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kiến trúc máy tính & Vi xử lý.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kiến trúc máy tính & Vi xử lý"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1344",
                "Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                3,
                "Học kỳ 2",
                "Khoa Kỹ thuật Phần mềm",
                "Phương pháp phát triển Agile/Scrum, phân tích thiết kế hướng đối tượng với UML, các nguyên lý SOLID, mẫu thiết kế GoF, kiến trúc Microservices, CI/CD và đảm bảo chất lượng phần mềm.",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Kỹ nghệ phần mềm & Kiến trúc hệ thống",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Kỹ nghệ phần mềm & Kiến trúc hệ thống.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Kỹ nghệ phần mềm & Kiến trúc hệ thống"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1345",
                "Trí tuệ nhân tạo & Học máy",
                3,
                "Học kỳ 1",
                "Khoa Trí tuệ Nhân tạo",
                "Tìm kiếm không gian trạng thái (A*, Minimax), thuật toán học máy có giám sát (Linear/Logistic Regression, Decision Trees, SVM), học máy không giám sát (K-Means, PCA) và mạng nơ-ron tích chập Deep Learning.",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Trí tuệ nhân tạo & Học máy",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trí tuệ nhân tạo & Học máy.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trí tuệ nhân tạo & Học máy"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1346",
                "An toàn thông tin & Mật mã ứng dụng",
                3,
                "Học kỳ 2",
                "Khoa An toàn Thông tin",
                "Hệ mật mã đối xứng AES, bất đối xứng RSA/ECC, hàm băm SHA-256/SHA-3, chữ ký số, chứng chỉ số X.509, giao thức bảo mật TLS 1.3 và phòng chống các lỗ hổng OWASP Top 10.",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn An toàn thông tin & Mật mã ứng dụng",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn An toàn thông tin & Mật mã ứng dụng.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến An toàn thông tin & Mật mã ứng dụng"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1347",
                "Hệ thống phân tán & Điện toán đám mây",
                3,
                "Học kỳ 1",
                "Khoa Công nghệ Thông tin",
                "Các mô hình hệ thống phân tán, định lý CAP, đồng thuận phân tán Raft/Paxos, giao tiếp RPC (gRPC, Protocol Buffers), kiến trúc hướng sự kiện với Kafka, và dịch vụ đám mây AWS/GCP (Docker, Kubernetes).",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Hệ thống phân tán & Điện toán đám mây",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Hệ thống phân tán & Điện toán đám mây.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Hệ thống phân tán & Điện toán đám mây"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1348",
                "Trình biên dịch & Ngôn ngữ hình thức",
                3,
                "Học kỳ 2",
                "Khoa Khoa học Máy tính",
                "Otomat hữu hạn, biểu thức chính quy, phân tích từ vựng (Lexer), văn phạm phi ngữ cảnh, phân tích cú pháp (LL, LR Parser), cây cú pháp trừu tượng AST, kiểm tra kiểu tĩnh và sinh mã đích LLVM IR.",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Trình biên dịch & Ngôn ngữ hình thức",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Trình biên dịch & Ngôn ngữ hình thức.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Trình biên dịch & Ngôn ngữ hình thức"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1349",
                "Phát triển ứng dụng Web toàn diện",
                3,
                "Học kỳ 1",
                "Khoa Công nghệ Phần mềm",
                "Thiết kế RESTful Web API với Spring Boot, bảo mật JWT/OAuth2, Frontend hiện đại với React/Next.js, TailwindCSS, State Management và tối ưu SEO / Server-Side Rendering.",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Phát triển ứng dụng Web toàn diện",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Phát triển ứng dụng Web toàn diện.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Phát triển ứng dụng Web toàn diện"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1350",
                "Lập trình ứng dụng Di động",
                3,
                "Học kỳ 2",
                "Khoa Công nghệ Phần mềm",
                "Lập trình đa nền tảng với Flutter và Android Native (Kotlin), kiến trúc MVVM/Clean Architecture, quản lý vòng đời Activity/Widget, tương tác REST API và lưu trữ cục bộ Room/SQLite.",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Lập trình ứng dụng Di động",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Lập trình ứng dụng Di động.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Lập trình ứng dụng Di động"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1351",
                "Xử lý dữ liệu lớn (Big Data Engineering)",
                3,
                "Học kỳ 1",
                "Khoa Khoa học Dữ liệu",
                "Mô hình lập trình MapReduce, hệ sinh thái Apache Spark (RDD, Spark SQL, Spark Streaming), lưu trữ phân tán HDFS/Data Lakehouse, truyền thông điệp thời gian thực Apache Kafka và NoSQL Cassandra/MongoDB.",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Xử lý dữ liệu lớn (Big Data Engineering)",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Xử lý dữ liệu lớn (Big Data Engineering).",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Xử lý dữ liệu lớn (Big Data Engineering)"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1352",
                "Đồ họa máy tính & Công nghệ Game",
                3,
                "Học kỳ 2",
                "Khoa Khoa học Máy tính",
                "Toán học đồ họa 3D (Ma trận biến đổi không gian, Quaternions), đồ họa tương tác OpenGL/Vulkan, đường ống dựng hình GPU (Shaders, Lighting, Ray Tracing) và vật lý trong trò chơi.",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Đồ họa máy tính & Công nghệ Game",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Đồ họa máy tính & Công nghệ Game.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Đồ họa máy tính & Công nghệ Game"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

        registerCurriculum(new CurriculumItem(
                "INT1353",
                "Tương tác người - máy & Thiết kế UX/UI",
                3,
                "Học kỳ 1",
                "Khoa Kỹ thuật Phần mềm",
                "Tâm lý học nhận thức người dùng, quy trình thiết kế hướng người dùng (Design Thinking), tạo mẫu Wireframe/Prototype trên Figma, kiểm thử khả năng sử dụng (Usability Testing) và khả năng tiếp cận WCAG.",
                List.of(
                        new WeeklyModule(
                                1,
                                "Tuần 1: Chuyên đề nâng cao phần 1 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 1: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 1",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 1",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 1",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 1.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 1.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 1 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                2,
                                "Tuần 2: Chuyên đề nâng cao phần 2 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 2: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 2",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 2",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 2",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 2.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 2.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 2 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                3,
                                "Tuần 3: Chuyên đề nâng cao phần 3 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 3: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 3",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 3",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 3",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 3.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 3.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 3 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                4,
                                "Tuần 4: Chuyên đề nâng cao phần 4 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 4: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 4",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 4",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 4",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 4.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 4.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 4 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                5,
                                "Tuần 5: Chuyên đề nâng cao phần 5 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 5: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 5",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 5",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 5",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 5.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 5.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 5 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                6,
                                "Tuần 6: Chuyên đề nâng cao phần 6 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 6: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 6",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 6",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 6",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 6.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 6.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 6 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                7,
                                "Tuần 7: Chuyên đề nâng cao phần 7 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 7: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 7",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 7",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 7",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 7.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 7.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 7 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                8,
                                "Tuần 8: Chuyên đề nâng cao phần 8 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 8: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 8",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 8",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 8",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 8.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 8.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 8 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                9,
                                "Tuần 9: Chuyên đề nâng cao phần 9 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 9: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 9",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 9",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 9",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 9.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 9.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 9 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                10,
                                "Tuần 10: Chuyên đề nâng cao phần 10 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 10: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 10",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 10",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 10",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 10.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 10.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 10 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                11,
                                "Tuần 11: Chuyên đề nâng cao phần 11 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 11: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 11",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 11",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 11",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 11.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 11.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 11 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                12,
                                "Tuần 12: Chuyên đề nâng cao phần 12 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 12: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 12",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 12",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 12",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 12.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 12.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 12 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                13,
                                "Tuần 13: Chuyên đề nâng cao phần 13 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 13: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 13",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 13",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 13",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 13.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 13.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 13 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                14,
                                "Tuần 14: Chuyên đề nâng cao phần 14 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 14: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 14",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 14",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 14",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 14.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 14.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 14 lên hệ thống LMS."
                        ),
                        new WeeklyModule(
                                15,
                                "Tuần 15: Chuyên đề nâng cao phần 15 môn Tương tác người - máy & Thiết kế UX/UI",
                                "Nội dung trọng tâm tuần 15: Nghiên cứu chi tiết các lý thuyết, giải thuật và bài thực hành của môn Tương tác người - máy & Thiết kế UX/UI.",
                                List.of(
                                        "Nắm vững nguyên lý và phương pháp luận của tuần 15",
                                        "Có khả năng hiện thực hóa giải pháp phần mềm cho nội dung tuần 15",
                                        "Phân tích và tối ưu hóa hiệu năng ứng dụng trong các kịch bản thực tế"
                                ),
                                List.of(
                                        "Tài liệu giáo trình chính - Chương 15",
                                        "Tài liệu tham khảo tiêu chuẩn quốc tế IEEE/ACM liên quan đến Tương tác người - máy & Thiết kế UX/UI"
                                ),
                                List.of(
                                        "Bài tập thực hành phòng Lab 15.1: Cài đặt và đo đạc thông số",
                                        "Bài tập thực hành phòng Lab 15.2: Xây dựng mini-project tích hợp"
                                ),
                                "Báo cáo bài tập lớn và nộp mã nguồn tuần 15 lên hệ thống LMS."
                        )
                ),
                Map.of(
                        "ChuyenCan", 10,
                        "ThucHanhLab", 20,
                        "KiemTraGiuaKy", 20,
                        "ThiCuoiKy", 50
                )
        ));

    }

    private static void registerCurriculum(CurriculumItem c) {
        CURRICULA.put(c.courseCode(), c);
    }

    public static List<CurriculumItem> getAllCurricula() {
        return List.copyOf(CURRICULA.values());
    }

    public static Optional<CurriculumItem> findByCourseCode(String code) {
        return Optional.ofNullable(CURRICULA.get(code));
    }

    public static List<CurriculumItem> findByDepartment(String dept) {
        return CURRICULA.values().stream()
                .filter(c -> c.department().equalsIgnoreCase(dept))
                .toList();
    }

    public static List<CurriculumItem> search(String query) {
        if (query == null || query.isBlank()) {
            return getAllCurricula();
        }
        String q = query.toLowerCase();
        return CURRICULA.values().stream()
                .filter(c -> c.courseName().toLowerCase().contains(q) || c.courseCode().toLowerCase().contains(q))
                .toList();
    }

    public static int getTotalCourseCount() {
        return CURRICULA.size();
    }
}
