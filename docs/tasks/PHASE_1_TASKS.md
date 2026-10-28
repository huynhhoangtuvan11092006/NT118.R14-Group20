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

### [Sub-issue 1D] Giao diện Onboarding, Đăng nhập & Khảo sát Target Band
* **Tiêu đề GitHub:** `[Phase 1][Android][Sub-issue D] Giao diện Onboarding, Đăng nhập & Khảo sát Target Band`
* **Người phụ trách (Assignee):** Thành viên 3 (Mobile)
* **Parent Issue:** #1

#### Nội dung Issue:
```markdown
## Overview
Xây dựng các màn hình chào mừng (Splash, Onboarding), màn hình Đăng nhập / Đăng ký và màn hình chọn mục tiêu điểm IELTS (Target Band).

## Reference
* **Parent issue**: #1
* **Figma UI**: Màn hình Auth Flow

## Target Files (Quy định các file làm việc)
* `app/src/main/java/com/example/ielts/ui/auth/LoginActivity.kt`
* `app/src/main/java/com/example/ielts/ui/auth/RegisterActivity.kt`
* `app/src/main/java/com/example/ielts/ui/auth/TargetBandActivity.kt`
* `app/src/main/res/layout/activity_login.xml`
* `app/src/main/res/layout/activity_register.xml`
* `app/src/main/res/layout/activity_target_band.xml`

## Scope
* Màn hình Splash kiểm tra trạng thái đăng nhập.
* Form đăng nhập, đăng ký có validate dữ liệu (email đúng định dạng, mật khẩu >= 6 ký tự).
* Màn hình chọn Target Band (slider hoặc chip từ 4.0 - 9.0).

## Acceptance Criteria
- [ ] Chuyển màn hình mượt mà, lưu thông tin Target Band vào local và server
- [ ] Báo lỗi rõ ràng khi người dùng nhập sai email/mật khẩu
- [ ] Layout responsive, không bị tràn màn hình trên các kích thước điện thoại khác nhau
```
