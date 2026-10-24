# LMS Server — Spring Boot

Server cho hệ thống LMS chạy trong LAN, giai đoạn 1. Kiến trúc là **modular monolith**: một process Spring Boot, một database MySQL. Code chia theo miền nghiệp vụ (auth, user, realtime, ...), không chia theo loại file.

Server đã có sẵn:

- Login với role dạng số, **opaque session** lưu trong MySQL, cookie `HttpOnly`, CSRF token.
- Phân quyền theo permission (`@RequirePermission`), deny by default.
- WebSocket `/ws`: theo dõi presence realtime, ép logout tức thì.
- Admin quản lý user: tìm kiếm, khóa/mở khóa, xóa, ép logout, import sinh viên (text hoặc `.txt`).
- Audit log cho login và các thao tác admin.
- Flyway migration, profile dev/prod, file `.env`.

## 1. Chạy lần đầu

Yêu cầu: JDK 17+ (khuyên dùng 21), Maven 3.9+ (hoặc IntelliJ IDEA), Docker (để chạy MySQL).

```bash
# 1. Khởi động MySQL
docker compose up -d

# 2. Tạo file cấu hình
cp .env.example .env

# 3. Chạy server (profile dev)
mvn spring-boot:run
```

Mở http://localhost:8080/dev-console.html để test nhanh khi chưa có frontend.

| Tài khoản (profile dev) | Mật khẩu |
|---|---|
| `admin` | `admin12345` (hoặc giá trị `APP_ADMIN_PASSWORD`) |
| `student01` | `student12345` (được ghi danh 6 khóa học demo) |
| `teacher01`, `teacher02` | `teacher12345` (giảng viên demo, phụ trách các khóa học demo) |

Khóa học demo do `lms.course.DemoCourseInitializer` tạo một lần khi DB chưa có khóa học nào
(`app.bootstrap.create-demo-courses`, chỉ bật ở profile dev).

Ở profile prod, nếu `APP_ADMIN_PASSWORD` để trống, server tự sinh mật khẩu admin ngẫu nhiên và in ra log **một lần duy nhất**.

Chạy test: `mvn test`

## 2. Cấu trúc source và người phụ trách

```
src/main/java/lms/
├── LmsServerApplication.java
├── config/            P1  AppProperties, WebMvcConfig, PasswordConfig, seed admin, DevConsoleGuard
├── common/            P1  ApiResponse, ErrorCode, AppException, GlobalExceptionHandler, RandomTokens
├── auth/              P2  login/logout/me/change-password, AuthInterceptor, Role, PermissionService
│   └── session/       P2  SessionEntity, SessionService (opaque token), SessionCookies
├── realtime/          P3  WebSocketConfig, handshake auth, RealtimeHub (presence), sweeper
├── user/              P4  User, AdminUserController, UserAdminService
│   └── importing/     P4  StudentImportParser (Java thuần), StudentImportService
├── audit/             P1  AuditService, AuditController
├── course/                Khóa học: mục/tuần, bài học, tài liệu (FileStorage), bài kiểm tra, ghi danh, /teacher/**, /files/**
├── student/           P6  API sinh viên: dashboard, nội dung khóa học, tham gia bằng mã
├── submission/            Sinh viên làm bài (xem đề, nộp file) và giảng viên chấm điểm; /student/assessments/**, /teacher/.../submissions/**, /submission-files/**
├── attendance/            Điểm danh theo buổi: mã QR đổi mỗi 10 giây (AttendanceQr), điểm danh thủ công; /teacher/.../attendance/**, /student/attendance/**
├── code/                  Bài lập trình nhiều câu, không điểm (kết quả = số test đúng): mỗi câu đề + bộ test (test mẫu / ẩn, soạn tay hoặc ZIP), sinh viên chạy thử / nộp nhiều lần, hàng đợi trong DB, CodeJudgeWorker chấm nền trong sandbox (LocalSandbox / DockerSandbox); /teacher/.../code-assessments, /student/assessments/{id}/code/**
├── quiz/                  Bài trắc nghiệm tạo từ ngân hàng (chép câu hỏi sang bài), sinh viên làm và server chấm (QuizGrader), QuizSweeper chốt bài hết giờ; /teacher/.../quizzes, /student/assessments/{id}/quiz/**
├── question/              Ngân hàng câu hỏi trắc nghiệm của giảng viên, nhập câu hỏi từ văn bản (QuestionImportParser); /teacher/question-banks/**
└── notification/          Thông báo: tự động khi tạo bài, tùy chọn khi thêm nội dung, giảng viên soạn; /notifications/**, /teacher/.../notifications
src/main/resources/
├── db/migration/      P1  V1 core schema, V2 role/permission, V3 courses, V4 quản lý khóa học, V5 bài học, V6 đề riêng của bài, V7 bài nộp, V8 chấm điểm, V9 thông báo, …, V14 điểm danh, V15–V16 ngân hàng câu hỏi theo môn học, V17 bài trắc nghiệm, V18 công bố điểm sau giờ đóng, V19–V21 bài lập trình (bộ test ZIP, nhiều câu)
└── static/            P5, P6  frontend build (AdminLTE / Next export) đặt ở đây
```

## 3. Quy ước chung (cả nhóm phải theo)

**Role dạng số** (bảng `roles`, enum `Role`):

| Mã | Role | Mã | Role |
|---|---|---|---|
| 0 | ADMIN | 4 | PARENT |
| 1 | TEACHER | 5 | STUDENT |
| 2 | MENTOR | 6 | VISITOR |
| 3 | SUPERVISOR | | |

**Response envelope** cho mọi API:

```json
{ "success": true, "data": { } }
{ "success": false, "error": { "code": "FORBIDDEN", "message": "Permission denied" } }
```

Frontend hiển thị thông báo dựa vào `error.code`. Không hiển thị `message`, JSON thô hay stacktrace lên giao diện.

**CSRF.** Login và `GET /auth/me` trả về `csrfToken`. Frontend giữ token này trong biến JS (không lưu `localStorage`) và gửi qua header `X-CSRF-Token` cho mọi request POST/PUT/PATCH/DELETE.

**ID.** API chỉ dùng `id` dạng chuỗi ngẫu nhiên 22 ký tự (`public_id`), không lộ id số tự tăng.

**Mật khẩu ban đầu.** Tài khoản import có mật khẩu là ngày sinh, nên bị gắn cờ `mustChangePassword`. Khi cờ này bật, user chỉ gọi được `/api/v1/auth/**` và nhận lỗi `PASSWORD_CHANGE_REQUIRED` cho mọi API khác. Frontend cần chuyển user sang màn hình đổi mật khẩu.

**Thêm API mới:**

1. Tạo package theo miền nghiệp vụ.
2. Controller chỉ nhận request và gọi service. Logic nghiệp vụ đặt trong service.
3. Gắn `@RequirePermission(...)`. Mặc định mọi `/api/**` đều cần đăng nhập. Chỉ endpoint có `@PublicEndpoint` mới mở công khai.
4. Lỗi thì ném `AppException(ErrorCode.X)`.
5. Thao tác thay đổi dữ liệu quan trọng thì ghi `auditService.record(...)`.

**Đổi schema.** Tạo file migration mới `V3__mo_ta.sql`. **Không sửa** file migration đã chạy.

## 4. HTTP API (`/api/v1`)

| Method | Path | Quyền | Mô tả |
|---|---|---|---|
| POST | `/auth/login` | public | `{username, password}` → set cookie `SID`, trả `{user, csrfToken}` |
| POST | `/auth/logout` | login | Thu hồi session, đóng socket |
| GET | `/auth/me` | login | `{user, csrfToken}` (frontend gọi khi load trang) |
| POST | `/auth/change-password` | login | `{currentPassword, newPassword}`; thu hồi các session khác |
| GET | `/admin/users?q=&role=&status=&page=&size=` | `users.manage` | Danh sách có phân trang |
| POST | `/admin/users/{id}/lock` | `users.manage` | Khóa tài khoản + đá ra ngay |
| POST | `/admin/users/{id}/unlock` | `users.manage` | Mở khóa |
| DELETE | `/admin/users/{id}` | `users.manage` | Xóa tài khoản |
| POST | `/admin/users/{id}/force-logout` | `users.manage` + `sessions.manage` | Ép logout mọi thiết bị |
| POST | `/admin/users/import` | `users.manage` | JSON `{content}` |
| POST | `/admin/users/import-file` | `users.manage` | multipart, field `file` (.txt UTF-8) |
| GET | `/admin/presence` | `presence.view` | Danh sách user đang online |
| GET, PUT | `/admin/settings/idle-timeout` | `sessions.manage` | `{studentMinutes, teacherMinutes, minMinutes, maxMinutes}` / `{studentMinutes, teacherMinutes}`: thời gian tự đăng xuất khi không hoạt động (1 phút tới thời hạn tuyệt đối 12 giờ); áp dụng ngay cho phiên đang mở, ghi audit `session.idle_timeout_updated` |
| GET | `/admin/audit?page=&size=` | `audit.view` | Audit log |
| GET | `/student/dashboard` | `courses.view` | Profile + khóa học đã ghi danh `{id, code, title, teacherName}` |
| GET | `/student/courses/{id}` | `courses.view` | Nội dung khóa học đã ghi danh; không có bài học/file bị ẩn (kể cả file của bài bị ẩn) và đề của bài chưa mở |
| POST | `/student/courses/join` | `courses.view` | `{key}`: tự ghi danh bằng mã tham gia; sai mã → `INVALID_ENROLL_KEY` |
| GET | `/student/assessments?course={courseId}` | `submissions.create` | `[{assessmentId, status, submittedAt, score}]`: trạng thái và điểm của mình ở mọi bài của khóa học đã ghi danh (cột phải trang khóa học) |
| GET | `/student/assessments/{id}` | `submissions.create` | `{course, assessment, submission, serverTime}`; đề chỉ có khi bài mở; `submission.status`: `not_started` / `in_progress` / `submitted` / `auto_submitted` (hết giờ, file đã tải tính là bài nộp) / `missed` |
| POST | `/student/assessments/{id}/files` | `submissions.create` | multipart `file`: PDF/DOCX/PPTX/ZIP ≤ 20 MB, tối đa 10 file; chỉ khi bài đang mở và chưa nộp (`ASSESSMENT_NOT_OPEN` / `SUBMISSION_LOCKED`) |
| DELETE | `/student/assessments/{id}/files/{fileId}` | `submissions.create` | Xóa file bài làm (cùng điều kiện như trên) |
| POST | `/student/assessments/{id}/submit` | `submissions.create` | Nộp bài (cần ≥ 1 file), sau đó khóa bài làm; ghi audit `submission.submitted` |
| GET | `/teacher/courses/{id}/assessments/{assessmentId}/submissions` | `courses.manage` + `submissions.manage` | `{course, assessment, summary, rows}`: mọi sinh viên đã ghi danh kèm trạng thái, file, điểm; `summary` = số sinh viên / đã nộp / đã chấm / điểm trung bình |
| PUT | `/teacher/courses/{id}/assessments/{assessmentId}/submissions/{studentId}/grade` | `courses.manage` + `submissions.manage` | `{score, feedback}` (bài lập trình không chấm điểm → `BAD_REQUEST`): thang 0–10, tối đa 2 chữ số thập phân; `score: null` xóa điểm. Chỉ chấm bài đã chốt (đã nộp, hết giờ, không nộp), còn lại `SUBMISSION_NOT_FINAL`; ghi audit `submission.graded` |
| GET | `/notifications?category=&unread=&page=&size=` | `courses.view` | Thông báo của mình, mới nhất trước; `category`: `exam` / `lab` / `practice` / `content` / `announcement` |
| GET | `/notifications/summary` | `courses.view` | `{unread, byCategory}`: số chưa đọc tổng và theo loại |
| POST | `/notifications/{id}/read`, `/notifications/read-all` | `courses.view` | Đánh dấu đã đọc một / tất cả |
| GET, POST | `/teacher/courses/{id}/notifications` | `courses.manage` | Lịch sử thông báo của khóa học kèm số đã đọc / gửi `{title, body, studentIds?}` (bỏ trống `studentIds` = cả lớp) |
| GET, POST | `/teacher/courses/{id}/attendance` | `courses.manage` | Danh sách buổi `{students, sessions}` kèm số có mặt / đi muộn / có phép (vắng = sĩ số − ba số đó) / tạo buổi `{title?, qr}` (trống = "Buổi N"; `qr` mở điểm danh QR ngay). Sửa, mở/dừng QR, điểm danh: chỉ giảng viên phụ trách (`TEACHER_ONLY`) |
| GET, PUT, DELETE | `/teacher/courses/{id}/attendance/{sessionId}` | `courses.manage` | `{session, students, rows}`: mọi sinh viên đang ghi danh, `status` 0 có mặt / 1 đi muộn / 2 có phép / `null` vắng, `method` 0 quét QR / 1 thủ công, `ip` lúc quét / đổi tên `{title}` / xóa buổi |
| POST | `/teacher/courses/{id}/attendance/{sessionId}/qr/open`, `/qr/close` | `courses.manage` | Mở / dừng điểm danh bằng QR; dừng thì mã đang chiếu hết dùng được ngay |
| GET | `/teacher/courses/{id}/attendance/{sessionId}/qr` | `courses.manage` | `{course, session, token, expiresAt, serverTime, periodSeconds, checkedIn, students}` cho màn chiếu; gọi lại khi tới `expiresAt` (~10 giây). Buổi đã dừng QR → `ATTENDANCE_CLOSED` |
| PUT | `/teacher/courses/{id}/attendance/{sessionId}/records` | `courses.manage` | `{studentIds, status}`: điểm danh thủ công một hoặc nhiều sinh viên (`status: null` = vắng); ghi audit `attendance.marked` |
| GET | `/teacher/courses/{id}/attendance.csv` | `courses.manage` | Bảng điểm danh: mỗi sinh viên một dòng, mỗi buổi một cột, cuối là tổng theo trạng thái |
| POST | `/student/attendance/check-in` | `courses.view` | `{token}` (phần `?t=` của link `/attend/?t=...` trong QR) → `{courseId, course, courseTitle, session, status, checkedAt, alreadyRecorded}`. Mã chỉ nhận trong chu kỳ 10 giây hiện tại và chu kỳ ngay trước; lỗi `ATTENDANCE_CODE_INVALID` / `ATTENDANCE_CLOSED` / `NOT_ENROLLED`. Quét lại không đổi trạng thái đã có |
| GET | `/student/courses/{id}/attendance` | `courses.view` | `[{sessionId, title, createdAt, status, checkedAt}]`: điểm danh của mình, buổi cũ trước |
| GET, POST | `/teacher/question-banks` | `courses.manage` | Ngân hàng câu hỏi **của mình** (sửa gần nhất trước) `[{id, name, description, subjectCode, subjectName, questionCount, createdAt, updatedAt}]` / tạo `{name, description?, subjectCode?}`. Ngân hàng gắn với môn học (mã học phần, dùng chung cho các nhóm), không gắn khóa học; `subjectCode` trống = không gắn môn, môn không có trong `/subjects` → `VALIDATION_FAILED` (`UNKNOWN_SUBJECT`). Ngân hàng của người khác (kể cả với admin) → `NOT_FOUND` |
| GET | `/teacher/question-banks/subjects` | `courses.manage` | `[{code, name}]`: môn chọn được cho ngân hàng = học phần của các khóa mình phụ trách, theo mã; tên lấy từ khóa mới nhất. Sửa ngân hàng mà giữ nguyên môn đang gắn thì được cả khi đã thôi dạy |
| GET, PUT, DELETE | `/teacher/question-banks/{id}` | `courses.manage` | `{bank, questions}` (câu theo thứ tự thêm vào) / sửa `{name, description?, subjectCode?}` như khi tạo / xóa cả câu hỏi bên trong |
| POST | `/teacher/question-banks/{id}/questions` | `courses.manage` | Thêm câu `{body, multiple, difficulty, explanation?, options: [{body, correct}]}`: `difficulty` 0 dễ / 1 trung bình / 2 khó; 2–10 đáp án, ít nhất 1 đáp án đúng, câu chọn một (`multiple: false`) đúng 1 đáp án đúng (sai → `VALIDATION_FAILED`, message là lý do như `SINGLE_MANY_CORRECT`). Tối đa 2000 câu mỗi ngân hàng (`QUESTION_LIMIT`) |
| PUT, DELETE | `/teacher/question-banks/{id}/questions/{questionId}` | `courses.manage` | Sửa (cùng body như thêm, thay toàn bộ đáp án) / xóa một câu |
| POST | `/teacher/question-banks/{id}/questions/delete` | `courses.manage` | `{ids}`: xóa nhiều câu; có id không thuộc ngân hàng → `NOT_FOUND`, không xóa câu nào |
| POST | `/teacher/question-banks/{id}/import` | `courses.manage` | `{content}`: văn bản dán từ Word (định dạng Aiken mở rộng, xem `QuestionImportParser`) → `{added, errors: [{line, reason}]}`. Có câu lỗi thì không thêm câu nào; tối đa 500 câu mỗi lần (`QUESTION_LIMIT`) |
| POST | `/teacher/courses/{id}/quizzes` | `courses.manage` | Tạo bài trắc nghiệm `{type (1 thực hành / 2 kiểm tra), title, description?, opensAt?, closesAt?, timeLimitMinutes? (1–600, trống = làm tới giờ đóng), shuffle, revealMode (0 không cho xem đáp án / 1 sau khi nộp / 2 sau giờ đóng), scoreRelease (0 sinh viên thấy điểm ngay / 1 sau giờ đóng, cần `closesAt` / 2 không cho xem điểm; điểm chưa công bố thì cũng không xem được đáp án), questionIds}` → bài như `/teacher/courses/{id}`. `questionIds` (1–200) là câu trong ngân hàng **của mình**, theo thứ tự trong đề; câu hỏi được chép sang bài nên sửa ngân hàng sau đó không đổi bài. Chỉ giảng viên phụ trách; luôn thông báo cho sinh viên |
| GET | `/teacher/courses/{id}/assessments/{aid}/quiz` | `courses.manage` | `{questions, stats: [{questionId, answered, correct}], finished}`: đề kèm đáp án và số sinh viên làm đúng từng câu trên các bài đã chốt |
| GET | `/teacher/courses/{id}/assessments/{aid}/submissions/{studentId}/quiz` | `courses.manage` | Bài làm của một sinh viên `{startedAt, submittedAt, result: {correct, total, score}, questions}` (thứ tự gốc, `chosen` = đáp án đã chọn, kèm đáp án đúng) |
| GET | `/student/assessments/{id}/quiz` | `submissions.create` | `{phase (not_started / in_progress / finished), questionCount, timeLimitMinutes, startedAt, deadline, submittedAt, revealed, questions, result, serverTime}`. `questions` chỉ có khi đang làm (không kèm đáp án đúng) hoặc đã xong mà được xem đáp án; thứ tự câu và đáp án xáo theo từng sinh viên nếu bài bật `shuffle`, `position` là vị trí gốc của đáp án |
| POST | `/student/assessments/{id}/quiz/start` | `submissions.create` | Bắt đầu làm (một lần; gọi lại thì trả trạng thái hiện tại). Hạn = giờ đóng, hoặc giờ bắt đầu + `timeLimitMinutes` nếu sớm hơn. Chưa mở → `ASSESSMENT_NOT_OPEN`; đã đóng/đã nộp → `SUBMISSION_LOCKED` |
| PUT | `/student/assessments/{id}/quiz/answers/{questionId}` | `submissions.create` | `{chosen: [vị trí]}` thay câu trả lời một câu (rỗng = bỏ chọn); câu chọn một chỉ 1 đáp án. Quá hạn thì server chốt bài, trả `SUBMISSION_LOCKED` |
| POST | `/student/assessments/{id}/quiz/submit` | `submissions.create` | Nộp và chấm ngay: một câu đúng khi chọn đúng khít các đáp án đúng; điểm = số câu đúng / tổng số câu × 10 (lưu vào `score` và `auto_score`; giảng viên sửa `score` bằng API chấm điểm như bài nộp file). Hết hạn mà chưa nộp thì `QuizSweeper` (30 giây/lần) chốt với giờ nộp = hạn. Bài trắc nghiệm không nhận `/files` (`BAD_REQUEST`) |
| POST | `/teacher/courses/{id}/code-assessments` | `courses.manage` | Tạo bài lập trình **nhiều câu, không có điểm** `{type (0 bài tập / 1 thực hành / 2 kiểm tra), title, description?, opensAt?, closesAt?, scoreRelease (lúc sinh viên xem được kết quả nộp bài: 0 ngay / 1 sau giờ đóng / 2 không), notifyStudents, code: {languages ⊂ [c, cpp, java, python], problems}}`; mỗi câu `{title, statement (≤ 20000 ký tự), timeLimitMs (100–10000, mỗi test; Java ×2, Python ×3), memoryLimitMb (16–1024), maxAttempts? (1–100), tests: [{input, expected, sample}], archive? (tên part chứa ZIP bộ test của câu)}`. Tối đa 20 câu, mỗi câu 200 test / 64 MB; test mẫu tối đa 8 KB mỗi ô. Multipart: part `data` (JSON như trên) + mỗi file ZIP một part (test trong ZIP thêm sau `tests`, là test ẩn). Chỉ giảng viên phụ trách |
| POST | `/teacher/courses/{id}/code-test-archive` | `courses.manage` | Multipart part `tests`: đọc thử ZIP bộ test, không lưu gì → `{tests: [{name, inputSize, expectedSize}], problems: [{name, reason}], ignored}`; `reason`: `MISSING_INPUT` / `MISSING_OUTPUT` / `DUPLICATE` / `FILE_TOO_LARGE` (file > 8 MB) / `TOO_LARGE` (> 64 MB) / `TOO_MANY` / `NOT_ZIP` / `EMPTY`. Cặp file nhận được: `1.in`+`1.out` (input .in .inp .input; output .out .ans .ok .output .sol), `input01.txt`+`output01.txt`, kiểu Themis `Test01/BAI.INP`+`Test01/BAI.OUT`; test xếp theo tên kiểu tự nhiên, file khác bị bỏ qua (`TestArchive`) |
| GET, PUT | `/teacher/courses/{id}/assessments/{aid}/code` | `courses.manage` | `{languages, problems: [{id, title, statement, timeLimitMs, memoryLimitMb, maxAttempts, tests: [{id, name, input, expected, inputTruncated, expectedTruncated, inputSize, expectedSize, sample}]}], pending, editable}` (test > 8 KB chỉ có 300 ký tự đầu) / sửa `code` như khi tạo (JSON hoặc multipart kèm ZIP): câu có `id` được giữ, câu không có trong danh sách bị xóa cùng các lần nộp; mỗi test `{id, sample}` để giữ test đang có, hoặc có `input`/`expected` để thêm / thay nội dung. Câu có bộ test hoặc giới hạn đổi thì mọi lần nộp của câu được **chấm lại**; bài kiểm tra đã kết thúc → `ASSESSMENT_CLOSED`; ghi audit `assessment.updated` |
| GET | `/teacher/courses/{id}/assessments/{aid}/code/results` | `courses.manage` | Bảng sinh viên × câu `{problems: [{id, title}], rows: [{studentId, username, fullName, cells: [{problemId, status, best: {verdict, passed, total}, attempts}]}]}`; `status`: `none` chưa nộp / `pending` đang chấm / `accepted` có lần nộp AC / `rejected` đã nộp, chưa AC; `best` = lần nộp AC, không thì lần đúng nhiều test nhất |
| GET | `/teacher/courses/{id}/assessments/{aid}/code/problems/{pid}/tests.zip` | `courses.manage` | Tải bộ test một câu `01.in`/`01.out`… theo thứ tự (nạp lại được bằng ZIP) |
| POST | `/teacher/courses/{id}/assessments/{aid}/code/rejudge` | `courses.manage` | Chấm lại mọi lần nộp (vd. sau lỗi máy chấm `SE`), kể cả bài đã kết thúc; ghi audit `submission.code_rejudged` |
| GET | `/teacher/courses/{id}/assessments/{aid}/submissions/{studentId}/code` | `courses.manage` | `{studentId, username, fullName, attempts}`: các lần chạy/nộp ở mọi câu (có `problemId`), mới nhất trước, không kèm mã nguồn |
| GET | `/teacher/courses/{id}/assessments/{aid}/code/attempts/{attemptId}` | `courses.manage` | Một lần chạy/nộp: `source`, `compileOutput`, `results: [{index, sample, verdict, timeMs, output, error}]` của mọi test (output chỉ giữ khi sai hoặc là test mẫu, ≤ 2000 ký tự) |
| GET | `/student/assessments/{id}/code` | `submissions.create` | Danh sách câu `{languages: [{code, label, timeFactor}], problems: [{id, number, title, timeLimitMs, memoryLimitMb, maxAttempts, attemptsUsed, testCount, status, best}], resultsHidden, open, serverTime}`; `status` như bảng của giảng viên, thêm `submitted` (đã nộp nhưng kết quả chưa công bố: không lộ đúng/sai). Bài chưa mở → `ASSESSMENT_NOT_OPEN` |
| GET | `/student/assessments/{id}/code/problems/{pid}` | `submissions.create` | Một câu `{problem, statement, samples: [{input, expected}], attempts, languages, resultsHidden, open, serverTime}`; `attempts[]` = `{id, problemId, kind (run / submit), language, status (queued / running / done), verdict (AC / WA / TLE / RE / OLE / CE / SE), passed, total, createdAt, judgedAt}`, kết quả chưa công bố thì `verdict`, `passed`, `total` là `null` |
| POST | `/student/assessments/{id}/code/problems/{pid}/run`, `/submit` | `submissions.create` | `{language, source (≤ 65536 ký tự)}` → lần chạy `queued`; hỏi lại tới khi `done`. `run` chỉ chạy test mẫu (giữ 20 lần gần nhất mỗi câu). `submit` chạy mọi test, kết quả là số test đúng / tổng (**không có điểm**); lần nộp đầu tạo bài nộp `submitted`, ghi audit `submission.submitted`. Mỗi câu một lần chờ chấm mỗi lúc (`JUDGE_BUSY`); hết lượt nộp → `ATTEMPT_LIMIT`; ngoài giờ → `ASSESSMENT_NOT_OPEN` / `SUBMISSION_LOCKED`. Lỗi máy chấm (`SE`) không tính lượt |
| GET | `/student/assessments/{id}/code/attempts/{attemptId}` | `submissions.create` | Một lần chạy/nộp của mình kèm mã nguồn. Chạy thử: kết quả từng test mẫu kèm output; nộp bài: chỉ kết quả chung (`AC 5/5`), lỗi biên dịch khi CE, **không có kết quả từng test** |
| GET | `/submission-files/{id}` | `courses.view` | Tải file bài làm (luôn tải về); chỉ chủ bài nộp hoặc người quản lý khóa học |
| GET | `/files/{id}` | `courses.view` | Tải tài liệu; PDF trả inline, DOCX/PPTX tải về; kiểm tra quyền mỗi lần tải |
| GET, POST | `/teacher/courses` | `courses.manage` | Danh sách khóa học mình phụ trách (admin: tất cả); tạo `{code, group?, title, description, format, teacherId}` (chỉ admin). `group`: nhóm của học phần (≤ 16 ký tự, bỏ trống = không chia nhóm); trùng mã + nhóm → `COURSE_GROUP_TAKEN`. Sửa bằng `PUT /teacher/courses/{id}` cùng các trường (trừ `teacherId`). Nơi chỉ có một chuỗi mã (thông báo, bài nộp, nhật ký, tên file) hiện dạng `INT1340 · Nhóm 01` |
| GET, DELETE | `/teacher/courses/{id}` | `courses.manage` | Chi tiết `{course, sections, lessons, files, assessments}`; xóa cả file trên đĩa |
| POST, DELETE | `/teacher/courses/{id}/sections[/{sectionId}]` | `courses.manage` | Thêm `{title}` / xóa mục (bài học và file trong mục chuyển về "chưa phân mục") |
| POST | `/teacher/courses/{id}/lessons` | `courses.manage` | Tạo bài `{sectionId?, title, body?, hidden, notifyStudents}`; bài mới xếp cuối mục; khóa học "chỉ danh sách file" không có bài |
| PUT, DELETE | `/teacher/courses/{id}/lessons/{lessonId}` | `courses.manage` | Ghi đè bài (đổi mục thì xuống cuối mục mới, file đính kèm đi theo) / xóa bài (file đính kèm giữ lại trong mục) |
| POST | `/teacher/courses/{id}/files` | `courses.manage` | multipart `file` + `sectionId?` + `lessonId?` + `hidden` + `notifyStudents`; có `lessonId` thì file đính kèm bài và nằm trong mục của bài; chỉ PDF/DOCX/PPTX ≤ 20 MB |
| PATCH, DELETE | `/teacher/courses/{id}/files/{fileId}` | `courses.manage` | `{hidden}` ẩn/hiện; xóa tài liệu |
| GET | `/teacher/students?q=` | `courses.manage` | Tìm tài khoản sinh viên để ghi danh |
| GET | `/teacher/courses/{id}/students` | `courses.manage` | Danh sách ghi danh kèm cách thêm (`method` 0 dán, 1 chọn, 2 mã) |
| POST | `/teacher/courses/{id}/students/paste` | `courses.manage` | `{content}`: MSSV ngăn bởi xuống dòng, dấu phẩy, chấm phẩy hoặc khoảng trắng |
| POST | `/teacher/courses/{id}/students` | `courses.manage` | `{userIds}`: ghi danh theo danh sách đã chọn |
| DELETE | `/teacher/courses/{id}/students/{userId}` | `courses.manage` | Bỏ ghi danh |
| POST, DELETE | `/teacher/courses/{id}/enroll-key` | `courses.manage` | Tạo mã mới (mã cũ hết hiệu lực) / tắt mã |
| POST, DELETE | `/teacher/courses/{id}/assessments[/{assessmentId}]` | `courses.manage` | Tạo `{type, title, description, paperFileId, opensAt, closesAt, notifyStudents, scoreRelease?}` (bài kiểm tra, bài thực hành luôn thông báo; bài tập chỉ khi `notifyStudents`; `scoreRelease` 1 = sinh viên chỉ thấy điểm, nhận xét sau `closesAt`, 2 = không bao giờ thấy, mặc định 0 = thấy ngay) / xóa; bài thi bắt buộc có đề PDF và đề tự bị ẩn; bài thi đã kết thúc (quá `closesAt`) không xóa được → `ASSESSMENT_CLOSED` |
| POST | `/teacher/courses/{id}/assessments` (multipart) | `courses.manage` | Tạo bài kèm đề từ máy: part `data` (JSON như trên, `paperFileId` bị bỏ qua) + part `paper` (PDF). Đề chỉ thuộc bài này: không có trong `files`, không sửa/xóa qua API tài liệu, không dùng lại cho bài khác, xóa bài thì xóa đề |

**Định dạng import:** mỗi dòng `student_id|ddmmyyyy|Full name`. Dòng trống và dòng bắt đầu bằng `#` bị bỏ qua. Kết quả trả về chia 3 nhóm:

- `created`: tài khoản đã tạo.
- `duplicates`: `ALREADY_EXISTS` hoặc `DUPLICATE_IN_INPUT`.
- `errors`: `INVALID_FORMAT`, `INVALID_STUDENT_ID`, `INVALID_DATE` hoặc `INVALID_NAME`, kèm số dòng.

Server không bao giờ ghi đè tài khoản đã có.

## 5. WebSocket `/ws`

Client mở socket **sau khi login**, cùng origin. Cookie được trình duyệt tự gửi kèm. Không có session hợp lệ thì handshake bị từ chối với mã 401.

```js
const ws = new WebSocket(`${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}/ws`);
```

Sự kiện server gửi về có dạng `{ "type": "...", "data": {...}, "ts": "..." }`:

| type | Ai nhận | data |
|---|---|---|
| `connected` | chính socket đó | `{userId}` |
| `presence.changed` | user có `presence.view` | `{userId, username, fullName, role, online, connections}` |
| `session.revoked` | các socket của session bị thu hồi | `{reason}`: `logout` / `forced` / `locked` / `deleted` / `password_changed` / `idle` (không hoạt động quá thời gian chờ của vai trò) / `expired` |
| `notification.created` | người nhận thông báo | `{id, category, title, body, course, assessmentId, senderName, createdAt, read}` |
| `attendance.checked_in` | giảng viên phụ trách khóa học | `{courseId, sessionId, row}`: sinh viên vừa quét QR điểm danh (`row` như một dòng của chi tiết buổi) |
| `pong` | trả lời `{"type":"ping"}` | `null` |

Một số quy tắc cho phía client:

- Socket bị đóng với **mã 4001** nghĩa là session đã bị thu hồi. Client phải quay về trang login và **không tự reconnect**.
- Đóng với mã khác thì reconnect có backoff (1s, 2s, 4s, ... tối đa 30s).
- "Online" nghĩa là có ít nhất một socket đang mở, không phải "session còn hạn".
- Server gửi WebSocket ping mỗi 30 giây và tự đóng socket nếu session hết hạn.
- Khi người dùng thao tác trên trang (chuột, phím, cuộn, chạm), client gửi `{"type":"activity"}`, tối đa 1 lần/15 giây.
  Server coi đó là hoạt động và gia hạn phiên như một request API, để người đang đọc bài không bị đăng xuất.

**Tự đăng xuất khi không hoạt động.** Thời gian chờ đặt theo vai trò trong cột `roles.idle_timeout_minutes`
(mặc định: sinh viên 5 phút, giảng viên 10 phút; vai trò khác `NULL` = `app.session.idle-timeout`), admin chỉnh ở
trang Cài đặt qua `/admin/settings/idle-timeout`. Đổi xong áp dụng ngay cho các phiên đang mở; phiên quá thời gian
chờ mới bị đóng trong vòng 30 giây với lý do `idle`.
Người **đang thi** (ghi danh khóa có bài kiểm tra đang mở, chưa bấm nộp) và còn mở trang thì không bị tự đăng xuất:
`RealtimeSweeper` gia hạn phiên của họ mỗi 30 giây. Nộp bài hoặc hết giờ thì thời gian chờ bình thường tính lại.

## 6. Triển khai sau reverse proxy

```
Browser ── HTTPS ── Nginx/Caddy ── 127.0.0.1:8080 ── Spring Boot
                               └── /ws (Upgrade)
```

Đặt các biến sau trong `.env`:

```
APP_PROFILE=prod
APP_SECURE_COOKIE=true
APP_FORWARD_HEADERS=native
```

Nginx cần chuyển tiếp header `Upgrade` và `Connection` cho `/ws`, và đặt `proxy_read_timeout` lớn hơn 60s.

**Máy chấm bài lập trình.** Profile prod mặc định `app.judge.sandbox=docker`: mỗi lần chấm một container
không mạng, giới hạn RAM/CPU/số tiến trình, chạy bằng uid của chủ thư mục `data/judge`. Cần Docker trên máy
server và dựng image một lần:

```
docker build -t lms-judge:1 judge
```

Tài khoản chạy server phải gọi được `docker`. Sandbox `local` (mặc định ở dev) chạy thẳng bằng
gcc/g++/javac/java/python trên máy server, chỉ giới hạn thời gian và output: chương trình của sinh viên đọc/ghi
được mọi file mà server đọc/ghi được, **không dùng khi triển khai thật**.

## 7. Backup & restore

Dữ liệu gồm hai phần phải đi cùng nhau: database MySQL và thư mục file `data/files` (tài liệu, đề, bài nộp; DB chỉ giữ `storage_key`). Script trong `scripts/` (PowerShell, Windows) đọc kết nối DB và thư mục file từ `.env` giống server. MySQL chạy trong Docker thì thêm `-DockerContainer lms-mysql`.

Nếu PowerShell chặn chạy script, gọi qua `powershell -ExecutionPolicy Bypass -File <script>`.

| Script | Việc |
|---|---|
| `backup.ps1` | Dump DB (`--single-transaction`, không cần dừng server) rồi copy file → `backups/lms-backup-<thời điểm>.zip` kèm `manifest.json` (version schema, sha256). Giữ 14 bản gần nhất (`-Keep`). |
| `restore.ps1 -BackupFile <zip>` | Kiểm tra checksum, từ chối nếu schema backup mới hơn code, yêu cầu server đã dừng, tự backup trạng thái hiện tại (`-pre-restore`), thay DB + thư mục file, xóa bảng `sessions`. |
| `verify.ps1` | Đối chiếu `storage_key` trong DB với file trên đĩa. Thiếu file → exit code 1. |
| `test-restore.ps1` | Khôi phục bản mới nhất vào database tạm `lms_restore_test` + thư mục tạm, kiểm tra, rồi xóa. Không đụng hệ thống đang dùng. |

Mỗi lần backup, restore và test-restore đều ghi một dòng vào `audit_logs` (`system.backup`, `system.restored`, `system.restore_tested`).

**Chuẩn bị một lần** (bằng root): cấp quyền cho database tạm để chạy `test-restore.ps1`.

```sql
CREATE DATABASE lms_restore_test;
GRANT ALL ON lms_restore_test.* TO 'lms'@'localhost';
```

**Lịch chạy** (Task Scheduler, chạy khi user đang đăng nhập):

```
schtasks /Create /TN "LMS backup" /SC DAILY /ST 23:00 /TR "powershell -NoProfile -ExecutionPolicy Bypass -File F:\LMS\lms_server\scripts\backup.ps1 -OutDir D:\lms-backups"
schtasks /Create /TN "LMS restore test" /SC WEEKLY /D SUN /ST 23:30 /TR "powershell -NoProfile -ExecutionPolicy Bypass -File F:\LMS\lms_server\scripts\test-restore.ps1 -BackupDir D:\lms-backups"
```

**Khôi phục khi có sự cố:**

1. Dừng server.
2. `.\scripts\restore.ps1 -BackupFile D:\lms-backups\lms-backup-<thời điểm>.zip` rồi gõ tên database để xác nhận.
3. Khởi động server. Nếu code mới hơn backup, Flyway tự migrate tiếp. Mọi người phải đăng nhập lại.

**Lưu ý:**

- Backup chứa hash mật khẩu, thông tin sinh viên và bài nộp. Đặt `-OutDir` ở ổ/máy khác, giới hạn quyền đọc, không đặt trong thư mục web.
- `.env` không nằm trong backup: cất riêng.
- Code cần thêm bảng có file trên đĩa thì cập nhật truy vấn trong `Get-StorageReport` (`scripts/lms-common.ps1`).

## 8. Việc tiếp theo (gợi ý)

- [ ] P5/P6: build frontend vào `src/main/resources/static/` rồi xóa `dev-console.html`.
- [ ] Integration test với Testcontainers MySQL (login, revoke, permission boundary).
- [ ] Rate limit cho endpoint import.
- [x] Giai đoạn 2: module `course/` (V4): khóa học, mục/tuần, tài liệu, bài kiểm tra, ghi danh 3 cách.
- [x] Giai đoạn 2: bài học (V5): nội dung văn bản + tài liệu đính kèm trong từng mục/tuần.
- [x] Giai đoạn 2: sửa thông tin khóa học, đổi tên/sắp xếp mục, sắp xếp bài học, sửa bài (trừ bài thi đã kết thúc).
- [x] Giai đoạn 2: backup/restore (`scripts/`): DB + thư mục file, kiểm tra checksum. Còn chạy thử `test-restore.ps1`.
- [x] Giai đoạn 2: `submission/` (V7): sinh viên làm bài kiểm tra, nộp file.
- [x] Giai đoạn 2: chấm điểm thủ công (V8): giảng viên xem bài nộp, chấm điểm + nhận xét; sinh viên xem điểm.
- [x] Giai đoạn 2: thông báo (V9), tiến độ học (V10, `progress/`).
- [x] Giai đoạn 2: `report/`: bảng điểm CSV (`GET /teacher/courses/{id}/gradebook.csv`), ZIP bài đã nộp
  (`GET /teacher/courses/{id}/assessments/{aid}/submissions.zip`).
- [x] Giai đoạn 2: audit mọi thao tác thay đổi dữ liệu (trừ đánh dấu đã học, đã đọc thông báo);
  `GET /admin/audit?category=&q=` và trang `/admin/audit/`.
