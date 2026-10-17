package lms.course;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lms.auth.Role;
import lms.config.AppProperties;
import lms.user.User;
import lms.user.UserRepository;

/**
 * Profile dev: tạo 2 giảng viên demo và vài khóa học (theo tuần, có sẵn 3 tuần), ghi danh student01.
 * Chỉ chạy khi DB chưa có khóa học nào; chạy sau BootstrapDataInitializer (đã tạo student01).
 */
@Component
@Order(2)
public class DemoCourseInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoCourseInitializer.class);
    private static final String DEMO_TEACHER_PASSWORD = "teacher12345";

    /** username giảng viên, họ tên, các khóa học {mã, tên}. */
    private record DemoTeacher(String username, String fullName, List<String[]> courses) {
    }

    private static final List<DemoTeacher> DEMO = List.of(
            new DemoTeacher("teacher01", "Trần Minh Đức", List.of(
                    new String[] {"INT1340", "Lập trình mạng"},
                    new String[] {"INT1339", "Lập trình Java"},
                    new String[] {"INT1434", "Phát triển ứng dụng Web"})),
            new DemoTeacher("teacher02", "Lê Thu Hà", List.of(
                    new String[] {"INT1313", "Cơ sở dữ liệu"},
                    new String[] {"INT1306", "Cấu trúc dữ liệu và giải thuật"},
                    new String[] {"INT1319", "Hệ điều hành"})));

    private final CourseRepository courses;
    private final EnrollmentRepository enrollments;
    private final CourseSectionRepository sections;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final boolean enabled;

    public DemoCourseInitializer(CourseRepository courses, EnrollmentRepository enrollments,
            CourseSectionRepository sections, UserRepository users, PasswordEncoder encoder, AppProperties props) {
        this.courses = courses;
        this.enrollments = enrollments;
        this.sections = sections;
        this.users = users;
        this.encoder = encoder;
        this.enabled = props.bootstrap().createDemoCourses();
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled || courses.count() > 0) {
            return;
        }
        User student = users.findByUsername("student01").orElse(null);
        int created = 0;
        for (DemoTeacher demo : DEMO) {
            User teacher = users.findByUsername(demo.username()).orElseGet(() -> users.save(
                    new User(demo.username(), demo.fullName(), encoder.encode(DEMO_TEACHER_PASSWORD),
                            Role.TEACHER)));
            for (String[] item : demo.courses()) {
                Course course = new Course(item[0], item[1], teacher);
                course.setFormat(Course.FORMAT_WEEKS);
                courses.save(course);
                // Ba tuần đầu để trang giảng viên/sinh viên có sẵn khung nội dung
                for (int week = 1; week <= 3; week++) {
                    sections.save(new CourseSection(course.getId(), week, "Tuần " + week));
                }
                if (student != null) {
                    enrollments.save(new Enrollment(course.getId(), student.getId(), Enrollment.METHOD_PASTE));
                }
                created++;
            }
        }
        log.info("Created {} demo courses (dev profile only){}", created,
                student == null ? "; student01 not found, no enrollments" : " and enrolled 'student01'");
    }
}
