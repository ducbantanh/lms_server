package lms.proctor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import lms.auth.AuthPrincipal;
import lms.auth.Role;
import lms.common.RequestInfo;

/**
 * Chạy sau AuthInterceptor, chỉ với sinh viên:
 * <ol>
 * <li>Đang trong giờ thi có giám sát (ProctorService.inExam): chỉ cho gọi /auth/**, API của bài đang thi, đề của
 * bài và file bài làm của bài; còn lại PROCTOR_IN_EXAM (không mở tài liệu, bài khác, thông báo... trong giờ thi).</li>
 * <li>API làm bài /student/assessments/{id}/quiz|code|files|submit của bài có giám sát: phải qua phiên thi
 * (ProctorService.checkWork). Xem bài (GET /student/assessments/{id}) và /proctor/** không chặn: trang làm bài cần
 * chúng để hiện màn vào phòng thi.</li>
 * </ol>
 */
@Component
public class ProctorInterceptor implements HandlerInterceptor {

    private static final String AUTH_PREFIX = "/api/v1/auth/";
    private static final Pattern STUDENT_ASSESSMENT =
            Pattern.compile("^/api/v1/student/assessments/([A-Za-z0-9_-]{22})(/.*)?$");
    private static final Pattern COURSE_FILE = Pattern.compile("^/api/v1/files/([A-Za-z0-9_-]{22})$");
    private static final Pattern SUBMISSION_FILE = Pattern.compile("^/api/v1/submission-files/([A-Za-z0-9_-]{22})$");

    private final ProctorService service;

    public ProctorInterceptor(ProctorService service) {
        this.service = service;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)
                || !(request.getAttribute(AuthPrincipal.REQUEST_ATTRIBUTE) instanceof AuthPrincipal me)
                || me.role() != Role.STUDENT) {
            return true;
        }
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (path.startsWith(AUTH_PREFIX)) {
            return true;
        }
        ProctorService.ActiveExam exam = service.inExam(me.userId()).orElse(null);

        Matcher m = STUDENT_ASSESSMENT.matcher(path);
        if (m.matches()) {
            String assessmentId = m.group(1);
            String rest = m.group(2);
            if (exam != null && !exam.publicId().equals(assessmentId)) {
                throw ProctorService.inExamError(exam);
            }
            if (rest != null && !rest.startsWith("/proctor")) {
                service.checkWork(assessmentId, me, RequestInfo.clientIp(request), "GET".equals(request.getMethod()));
            }
            return true;
        }
        Matcher file = COURSE_FILE.matcher(path);
        if (file.matches()) {
            service.checkCourseFile(file.group(1), me, exam);
            return true;
        }
        if (exam == null) {
            return true;
        }
        Matcher own = SUBMISSION_FILE.matcher(path);
        if (own.matches()) {
            service.checkSubmissionFile(own.group(1), exam);
            return true;
        }
        throw ProctorService.inExamError(exam);
    }
}
