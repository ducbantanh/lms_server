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
| `student01` | `student12345` |

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
└── student/           P6  API dashboard sinh viên
src/main/resources/
├── db/migration/      P1  V1__core_schema.sql, V2__seed_roles_permissions.sql
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
| GET | `/student/dashboard` | `courses.view` | Profile + khóa học (giai đoạn 2) |

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

## 7. Việc tiếp theo (gợi ý)

- [ ] P5/P6: build frontend vào `src/main/resources/static/` rồi xóa `dev-console.html`.
- [ ] Integration test với Testcontainers MySQL (login, revoke, permission boundary).
- [ ] Rate limit cho endpoint import.
- [ ] Giai đoạn 2: module `courses/`, `content/`, `submissions/`.
