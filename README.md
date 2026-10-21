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
└── notification/          Thông báo: tự động khi tạo bài, tùy chọn khi thêm nội dung, giảng viên soạn; /notifications/**, /teacher/.../notifications
src/main/resources/
├── db/migration/      P1  V1 core schema, V2 role/permission, V3 courses, V4 quản lý khóa học, V5 bài học, V6 đề riêng của bài, V7 bài nộp, V8 chấm điểm, V9 thông báo
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
| PUT | `/teacher/courses/{id}/assessments/{assessmentId}/submissions/{studentId}/grade` | `courses.manage` + `submissions.manage` | `{score, feedback}`: thang 0–10, tối đa 2 chữ số thập phân; `score: null` xóa điểm. Chỉ chấm bài đã chốt (đã nộp, hết giờ, không nộp), còn lại `SUBMISSION_NOT_FINAL`; ghi audit `submission.graded` |
| GET | `/notifications?category=&unread=&page=&size=` | `courses.view` | Thông báo của mình, mới nhất trước; `category`: `exam` / `lab` / `practice` / `content` / `announcement` |
| GET | `/notifications/summary` | `courses.view` | `{unread, byCategory}`: số chưa đọc tổng và theo loại |
| POST | `/notifications/{id}/read`, `/notifications/read-all` | `courses.view` | Đánh dấu đã đọc một / tất cả |
| GET, POST | `/teacher/courses/{id}/notifications` | `courses.manage` | Lịch sử thông báo của khóa học kèm số đã đọc / gửi `{title, body, studentIds?}` (bỏ trống `studentIds` = cả lớp) |
| GET | `/submission-files/{id}` | `courses.view` | Tải file bài làm (luôn tải về); chỉ chủ bài nộp hoặc người quản lý khóa học |
| GET | `/files/{id}` | `courses.view` | Tải tài liệu; PDF trả inline, DOCX/PPTX tải về; kiểm tra quyền mỗi lần tải |
| GET, POST | `/teacher/courses` | `courses.manage` | Danh sách khóa học mình phụ trách (admin: tất cả); tạo `{code, title, description, format}` |
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
| POST, DELETE | `/teacher/courses/{id}/assessments[/{assessmentId}]` | `courses.manage` | Tạo `{type, title, description, paperFileId, opensAt, closesAt, notifyStudents}` (bài kiểm tra, bài thực hành luôn thông báo; bài tập chỉ khi `notifyStudents`) / xóa; bài thi bắt buộc có đề PDF và đề tự bị ẩn; bài thi đã kết thúc (quá `closesAt`) không xóa được → `ASSESSMENT_CLOSED` |
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
| `session.revoked` | các socket của session bị thu hồi | `{reason}`: `logout` / `forced` / `locked` / `deleted` / `password_changed` / `expired` |
| `notification.created` | người nhận thông báo | `{id, category, title, body, course, assessmentId, senderName, createdAt, read}` |
| `pong` | trả lời `{"type":"ping"}` | `null` |

Một số quy tắc cho phía client:

- Socket bị đóng với **mã 4001** nghĩa là session đã bị thu hồi. Client phải quay về trang login và **không tự reconnect**.
- Đóng với mã khác thì reconnect có backoff (1s, 2s, 4s, ... tối đa 30s).
- "Online" nghĩa là có ít nhất một socket đang mở, không phải "session còn hạn".
- Server gửi WebSocket ping mỗi 30 giây và tự đóng socket nếu session hết hạn.

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
