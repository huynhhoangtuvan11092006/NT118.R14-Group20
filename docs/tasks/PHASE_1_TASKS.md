# PHASE 1: KHỞI TẠO NỀN TẢNG & AUTHENTICATION
> **Milestone trên GitHub:** `Phase 1 - Khởi tạo & Base`

---

## 📌 Parent Issue #1: `[Phase 1][Base] Thiết kế Hệ thống & Authentication`
* **Mô tả:** Xây dựng khung kiến trúc cơ bản cho cả Backend và Mobile App, kèm hệ thống Đăng ký, Đăng nhập và Profile.
* **Thời gian dự kiến:** Tuần 1 - Tuần 2

---

### [Sub-issue 1A] Thiết kế Database Schema & Khởi tạo Backend Server
* **Tiêu đề GitHub:** `[Phase 1][Backend][Sub-issue A] Thiết kế Database Schema & Khởi tạo Backend Server`
* **Người phụ trách (Assignee):** Thành viên 1 (Backend)
* **Parent Issue:** #1

#### Nội dung Issue:
```markdown
## Overview
Khởi tạo project Backend (Node.js/FastAPI), cấu hình kết nối Database (PostgreSQL/MongoDB) và viết các migration tạo bảng dữ liệu ban đầu.

## Reference
* **Parent issue**: #1
* **Architecture Docs**: Thiết kế hệ thống tổng thể & ERD

## Target Files (Quy định các file làm việc)
* `backend/prisma/schema.prisma` (hoặc `models/` nếu dùng MongoDB/SQLAlchemy)
* `backend/src/config/database.ts`
* `backend/.env.example`
* `backend/docker-compose.yml`

## Scope
* Khởi tạo dự án backend, cài đặt linter, docker-compose cơ bản.
* Thiết kế schema và tạo migration cho các bảng: `users`, `tests`, `questions`, `submissions`, `vocabularies`.
* Cấu hình kết nối cơ sở dữ liệu an toàn qua biến môi trường (.env).

## Acceptance Criteria
- [ ] Server backend chạy local thành công không có lỗi
- [ ] Database migration chạy mượt mà, tạo đúng các bảng và khóa ngoại
- [ ] Có file `.env.example` và hướng dẫn chạy trong README backend
```

---

### [Sub-issue 1B] Xây dựng Authentication & Profile APIs
* **Tiêu đề GitHub:** `[Phase 1][Backend][Sub-issue B] Xây dựng Authentication & Profile APIs`
* **Người phụ trách (Assignee):** Thành viên 1 (Backend)
* **Parent Issue:** #1

#### Nội dung Issue:
```markdown
## Overview
Xây dựng các RESTful API cho phép người dùng đăng ký, đăng nhập (JWT), cập nhật thông tin cá nhân và thiết lập mục tiêu điểm (Target Band).

## Reference
* **Parent issue**: #1
* **API Spec**: `POST /auth/register`, `POST /auth/login`, `GET /users/me`, `PUT /users/target-band`

## Target Files (Quy định các file làm việc)
* `backend/src/controllers/auth.controller.ts`
* `backend/src/routes/auth.routes.ts`
* `backend/src/middlewares/auth.middleware.ts`
* `backend/src/services/auth.service.ts`

## Scope
* Mã hóa mật khẩu người dùng (bcrypt / argon2).
* Tạo JWT Token (Access token, Refresh token).
* API cập nhật Target Band và thông tin cá nhân.
* Viết middleware xác thực Bearer Token cho các private routes.

## Acceptance Criteria
- [ ] Test API đăng ký / đăng nhập thành công qua Postman
- [ ] Mật khẩu được hash an toàn trong database
- [ ] Chặn các request không có JWT token hợp lệ
```

---

### [Sub-issue 1C] Thiết lập Base Architecture & Network Client trên Android
* **Tiêu đề GitHub:** `[Phase 1][Android][Sub-issue C] Thiết lập Base Architecture & Network Client trên Android`
* **Người phụ trách (Assignee):** Thành viên 2 (Mobile)
* **Parent Issue:** #1

#### Nội dung Issue:
```markdown
## Overview
Thiết lập kiến trúc Clean Architecture / MVVM cho ứng dụng Android, cấu hình Retrofit kết hợp Interceptor lưu Token và cài đặt Room Database.

## Reference
* **Parent issue**: #1
* **Mã nguồn**: Android Studio Project

## Target Files (Quy định các file làm việc)
* `.github/workflows/android_ci.yml` (Quy trình CI kiểm tra build tự động khi mở PR)
* `app/build.gradle.kts` (thêm thư viện Retrofit, Room, OkHttp, Coroutines)
* `app/src/main/java/com/example/ielts/data/remote/ApiClient.kt`
* `app/src/main/java/com/example/ielts/data/remote/IeltsApiService.kt`
* `app/src/main/java/com/example/ielts/data/remote/dto/AuthDto.kt`
* `app/src/main/java/com/example/ielts/domain/model/Models.kt`
* `app/src/main/java/com/example/ielts/utils/Resource.kt`
* `app/src/main/java/com/example/ielts/utils/Constants.kt`

## Scope
* Cài đặt Retrofit 2, OkHttp Logging, Room DB vào gradle.
* Viết `AuthInterceptor` để tự động đính kèm Bearer Token vào header mỗi request.
* Viết Base ViewModel, Base NetworkResult wrapper.
* Cấu hình GitHub Actions CI chạy build tự động khi có Pull Request gộp vào main.

## Acceptance Criteria
- [ ] Build project Android thành công không lỗi Gradle
- [ ] Cấu hình GitHub Actions CI tự động kiểm tra code khi mở PR (hiện tích xanh)
- [ ] Gọi thử nghiệm được một GET API từ backend và parse JSON thành công
- [ ] Lưu và đọc được Token từ DataStore/SharedPreferences
```

---

### [Sub-issue 1D] Giao diện Auth Flow, Onboarding, Khảo sát Target Band & Dashboard
* **Tiêu đề GitHub:** `[Phase 1][Android][Sub-issue D] Hoàn thiện toàn diện Giao diện Auth Flow, Khảo sát Target Band & Dashboard`
* **Người phụ trách (Assignee):** Lê Nguyễn Phương Vy (`@PVL-cyber`)
* **Parent Issue:** #1
* **Branch làm việc:** `5-phase-1androidsub-issue-d-giao-diện-onboarding-đăng-nhập-khảo-sát-target-band` $\rightarrow$ Gộp vào `feature/integrate-auth-api`

#### Nội dung Issue:
```markdown
## Overview
Xây dựng và tích hợp trọn vẹn luồng trải nghiệm người dùng Phase 1 từ lúc mở ứng dụng (Splash, Onboarding), Đăng ký / Đăng nhập, Khảo sát mục tiêu Band, Thiết lập hồ sơ cá nhân hóa cho đến hiển thị dữ liệu thời gian thực trên Trang chủ (Dashboard) và Đăng xuất an toàn.

## Reference
* **Parent issue**: #1
* **Branch**: `feature/integrate-auth-api`
* **API Endpoints**: `POST /auth/register`, `POST /auth/login`, `PUT /users/target-band`, `PUT /users/me`, `GET /users/me`, `POST /auth/logout`

## Target Files (Danh sách các file thực hiện)
* `app/src/main/java/com/example/ielts/ui/auth/SplashActivity.kt` & `activity_splash.xml`
* `app/src/main/java/com/example/ielts/ui/auth/WelcomeActivity.kt` & `activity_welcome.xml`
* `app/src/main/java/com/example/ielts/ui/auth/RegisterActivity.kt` & `activity_register.xml`
* `app/src/main/java/com/example/ielts/ui/auth/LoginActivity.kt` & `activity_login.xml`
* `app/src/main/java/com/example/ielts/ui/auth/ForgotPasswordActivity.kt` & `activity_forgot_password.xml`
* `app/src/main/java/com/example/ielts/ui/auth/TargetBandActivity.kt` & `activity_target_band.xml`
* `app/src/main/java/com/example/ielts/ui/auth/ProfileActivity.kt` & `activity_profile.xml`
* `app/src/main/java/com/example/ielts/MainActivity.kt` & `activity_main.xml`

## Scope (Phạm vi công việc chi tiết)
1. **Splash & Onboarding (`SplashActivity`, `WelcomeActivity`)**:
   - Màn hình Splash với hiệu ứng nạp mượt mà trong 2.0s, tự động kiểm tra cờ đăng nhập (`IS_LOGGED_IN`) để điều hướng thông minh (Auto-Login).
   - Màn hình Chào mừng (Welcome) phân luồng người dùng mới ("Bắt đầu ngay" -> Đăng ký) và người dùng cũ ("Tôi đã có tài khoản" -> Đăng nhập).

2. **Xác thực tài khoản (`RegisterActivity`, `LoginActivity`)**:
   - Form Đăng ký: Validate định dạng Email, mật khẩu tối thiểu 8 ký tự, kiểm tra mật khẩu xác nhận khớp nhau, checkbox Điều khoản; gọi API `POST /auth/register`, xử lý bắt lỗi Email trùng (409 Conflict).
   - Form Đăng nhập: Nút ẩn/hiện mật khẩu, tính năng checkbox "Ghi nhớ đăng nhập" (tự lưu email vào máy); gọi API `POST /auth/login`, xử lý bắt lỗi sai tài khoản/mật khẩu (401 Unauthorized).

3. **Khảo sát mục tiêu & Hồ sơ học tập (`TargetBandActivity`, `ProfileActivity`)**:
   - Dải chọn Band điểm IELTS từ 5.0 đến 8.5 cuộn ngang mượt mà, tự động căn giữa nút đang chọn; DatePickerDialog chọn ngày thi dự kiến; chọn mức học 15/30/60 phút/ngày; gọi API `PUT /users/target-band`.
   - Màn hình Hồ sơ: Điền sẵn họ tên người dùng từ bước đăng ký; chọn Trình độ (A2/B1/B2/C1), chọn Kỹ năng trọng tâm (Multi-choice chip), chọn Mục tiêu học tập; gọi API `PUT /users/me`.

4. **Trang chủ Dashboard & Đăng xuất (`MainActivity`)**:
   - Tự động gọi `GET /users/me` khi mở màn hình để nạp thông tin thật từ Database: Cập nhật lời chào theo tên thật, chữ cái Avatar, đồng hồ đo mục tiêu Band thật, số Band còn thiếu, ngày thi dự kiến và chuỗi học Streak.
   - Hộp thoại popup khi bấm vào Avatar: Chuyển sang chỉnh sửa hồ sơ học tập hoặc Đăng xuất; gọi API `POST /auth/logout` để thu hồi token, xóa sạch session và đưa về màn hình Welcome.

## Acceptance Criteria (Tiêu chí nghiệm thu)
- [x] UI/UX hiện đại, chuẩn thiết kế, responsive tốt trên các kích thước màn hình Android (không bị tràn chữ/vỡ layout).
- [x] Toàn bộ luồng đăng ký -> khảo sát band -> hồ sơ -> dashboard hoạt động trơn tru không bị gián đoạn hay crash app.
- [x] Đăng nhập kiểm tra mật khẩu thật qua Backend (chấm dứt tình trạng gõ bừa vẫn vào được trang chủ).
- [x] Trạng thái nút bấm có loading ("Đang tạo tài khoản...", "Đang đăng nhập...") và chặn bấm lặp nhiều lần.
- [x] Dữ liệu hiển thị trên Dashboard phản ánh đúng 100% dữ liệu thực tế từ cơ sở dữ liệu PostgreSQL.
- [x] Đăng xuất xóa sạch Token và cờ đăng nhập trong `SharedPreferences`.
```
