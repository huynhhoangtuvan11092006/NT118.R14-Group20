# 🚀 Hướng Dẫn Cài Đặt & Chạy Dự Án (Dành Cho Thành Viên)

Tài liệu này hướng dẫn chi tiết các bước để thành viên trong nhóm thiết lập môi trường, cơ sở dữ liệu và kết nối máy ảo Android với Backend khi pull code về máy cá nhân.

---

### 📌 Yêu Cầu Cài Sẵn Trên Máy:
1. **Docker Desktop** (Bật sẵn để chạy PostgreSQL).
2. **Node.js** (Khuyên dùng v20 hoặc v22).
3. **Android Studio** (Đã tạo sẵn máy ảo Android Emulator).

---

### 🔹 BƯỚC 1: CẬP NHẬT CODE MỚI NHẤT
Mở Git Bash hoặc Terminal trong thư mục dự án và chạy:
```bash
git checkout feature/integrate-auth-api
git pull origin feature/integrate-auth-api
```

---

### 🔹 BƯỚC 2: THIẾT LẬP FILE MÔI TRƯỜNG (.env)
1. Mở Terminal và di chuyển vào thư mục `backend`:
   ```bash
   cd backend
   ```
2. Copy từ file mẫu `.env.example` thành file `.env`:
   * **Trên Windows (PowerShell / CMD)**:
     ```bash
     copy .env.example .env
     ```
   * **Trên macOS / Linux / Git Bash**:
     ```bash
     cp .env.example .env
     ```

3. **Tự sinh 2 mã bí mật JWT riêng biệt**:
   * Chạy lệnh này để lấy chuỗi ngẫu nhiên thứ nhất:
     ```bash
     node -e "console.log(require('crypto').randomBytes(48).toString('base64url'))"
     ```
     👉 Copy chuỗi vừa in ra và dán vào dòng `JWT_ACCESS_SECRET=...` trong file `.env`.

   * Chạy lại lệnh đó một lần nữa để lấy chuỗi thứ hai:
     ```bash
     node -e "console.log(require('crypto').randomBytes(48).toString('base64url'))"
     ```
     👉 Copy chuỗi thứ hai dán vào dòng `JWT_REFRESH_SECRET=...` trong file `.env`.

> 💡 *File `.env` hoàn chỉnh sẽ có dạng:*
> ```ini
> NODE_ENV=development
> PORT=3000
> DATABASE_URL="postgresql://bandup:local_dev_only_change_me@127.0.0.1:5432/bandup?schema=public"
> POSTGRES_USER=bandup
> POSTGRES_PASSWORD=local_dev_only_change_me
> POSTGRES_DB=bandup
> POSTGRES_PORT=5432
> CORS_ORIGIN=
> JWT_ACCESS_SECRET=chuoi_ngau_nhien_thu_nhat_vua_sinh_ra
> JWT_REFRESH_SECRET=chuoi_ngau_nhien_thu_hai_vua_sinh_ra
> ```

---

### 🔹 BƯỚC 3: KHỞI ĐỘNG DATABASE & SERVER
1. Cài đặt các thư viện Node.js:
   ```bash
   npm install
   ```
2. Bật Docker Desktop lên, sau đó khởi động Database container:
   ```bash
   docker compose up -d
   ```
3. Tạo và đồng bộ các bảng dữ liệu vào PostgreSQL:
   ```bash
   npx prisma generate
   npx prisma migrate dev
   ```
4. Khởi động Server Backend:
   ```bash
   npm run dev
   ```
   *(Khi terminal xuất hiện dòng thông báo `Ready on port 3000` là server đã sẵn sàng đón nhận request).*

---

### 🔹 BƯỚC 4: KẾT NỐI MÁY ẢO ANDROID (RẤT QUAN TRỌNG ⚠️)
1. Bật máy ảo Android trong **Android Studio**.
2. Mở một cửa sổ Terminal mới trên máy tính và chạy lệnh:
   ```bash
   adb reverse tcp:3000 tcp:3000
   ```
   *(Lệnh này giúp máy ảo kết nối trực tiếp vào Backend trên máy tính qua cầu nối ADB mà không bị Windows Defender / Firewall chặn).*

---

### 🔍 Công Cụ Hỗ Trợ Phát Triển & Kiểm Thử:
* **Prisma Studio (Xem cơ sở dữ liệu trên web)**:
  ```bash
  npx prisma studio
  ```
  *(Mở trang web quản lý dữ liệu tại `http://localhost:5555`).*
* **Tài liệu API Swagger**:
  Mở trình duyệt vào địa chỉ: `http://localhost:3000/docs/`.

---

# 🧪 CÁC USE CASES KIỂM THỬ DÀNH CHO THÀNH VIÊN

Sau khi đã bật Server và kết nối máy ảo, các thành viên hãy kiểm thử theo các kịch bản (Use Cases) sau để đảm bảo app hoạt động hoàn hảo:

| Mã UC | Tên Kịch Bản | Thao Tác Thực Hiện | Kết Quả Kỳ Vọng |
| :--- | :--- | :--- | :--- |
| **UC-01** | **Đăng ký tài khoản mới** | 1. Mở màn hình Đăng ký.<br>2. Nhập Họ tên, Email mới (VD: `linh@gmail.com`), Mật khẩu $\ge 8$ ký tự.<br>3. Tích chọn Điều khoản.<br>4. Bấm *"Tạo tài khoản ngay"*. | • Nút hiện *"Đang tạo tài khoản..."*.<br>• Báo Toast đăng ký thành công.<br>• Tự động chuyển sang màn hình chọn Target Band.<br>• User được lưu vào DB PostgreSQL. |
| **UC-02** | **Validation lỗi khi Đăng ký** | • Thử nhập Mật khẩu $< 8$ ký tự.<br>• Thử nhập 2 mật khẩu không khớp nhau.<br>• Thử đăng ký lại bằng Email đã tồn tại.<br>• Thử bỏ tích ô Điều khoản. | • Báo lỗi: *"Mật khẩu phải từ 8 ký tự trở lên"*; hoặc<br>• *"Mật khẩu xác nhận không khớp"*; hoặc<br>• *"Email này đã được đăng ký trong hệ thống!"*; hoặc<br>• Báo Toast bắt buộc đồng ý điều khoản. |
| **UC-03** | **Khảo sát Target Band** | 1. Vuốt chọn Band mục tiêu (VD: `7.5`).<br>2. Bấm đổi ngày thi (chọn 1 ngày trong tương lai).<br>3. Chọn mức học: *"30 phút/ngày"*.<br>4. Bấm *"Tiếp tục"*. | • Báo Toast: *"Đã lưu mục tiêu Band 7.5 lên máy chủ!"*.<br>• Cột `target_band` trong Database cập nhật giá trị `7.5`.<br>• Chuyển sang màn hình Hồ sơ cá nhân (`ProfileActivity`). |
| **UC-04** | **Thiết lập Hồ sơ học tập** | 1. Kiểm tra Họ tên được tự động điền sẵn.<br>2. Chọn Trình độ (A2/B1/B2/C1).<br>3. Chọn Kỹ năng trọng tâm (Writing, Speaking...).<br>4. Bấm *"Bắt đầu hành trình học ngay"*. | • Nút đổi sang *"Đang đồng bộ hồ sơ..."*.<br>• Server cập nhật `expectedExamDate` và `dailyStudyMinutes`.<br>• Chuyển thẳng vào **Trang chủ Dashboard (MainActivity)**. |
| **UC-05** | **Hiển thị Dashboard thời gian thực** | Quan sát giao diện Trang chủ (`MainActivity`). | • Lời chào hiển thị đúng tên thật (VD: *"Chào Linh! 👋"*).<br>• Chữ cái Avatar hiển thị đúng chữ cái đầu (`L`).<br>• Đồng hồ đo hiển thị đúng: **"MỤC TIÊU 7.5"**.<br>• Ngày thi hiển thị đúng ngày vừa chọn.<br>• Bấm nút Lửa: Báo chuỗi học streak từ máy chủ. |
| **UC-06** | **Kiểm tra Đăng xuất bảo mật** | 1. Tại Trang chủ, bấm vào **Avatar tròn** góc trên bên phải.<br>2. Chọn *"Đăng xuất"*. | • Server thu hồi Refresh Token (`revoked_at != null`).<br>• Máy xóa sạch Token và cờ đăng nhập.<br>• Chuyển về màn hình chào mừng (`WelcomeActivity`). |
| **UC-07** | **Đăng nhập với mật khẩu sai** | 1. Vào màn hình Đăng nhập.<br>2. Nhập email đúng, mật khẩu gõ sai bừa bãi.<br>3. Bấm *"Đăng nhập ngay"*. | • Báo lỗi đỏ: *"Tài khoản hoặc mật khẩu không chính xác!"*.<br>• **Tuyệt đối không cho vào Trang chủ**. |
| **UC-08** | **Đăng nhập thành công & Ghi nhớ** | 1. Nhập đúng email và mật khẩu.<br>2. **Tích vào ô "Ghi nhớ đăng nhập"**.<br>3. Bấm *"Đăng nhập ngay"*. | • Đăng nhập thành công vào thẳng Dashboard.<br>• Nếu sau này Đăng xuất ra ngoài, ô Email sẽ **tự động được điền sẵn**. |
| **UC-09** | **Tự động đăng nhập lại (Auto-Login)** | 1. Đang ở Trang chủ, gạt tắt hẳn ứng dụng IELTS đi.<br>2. Bấm vào icon app để mở lại từ đầu. | • Màn hình Splash chạy xong sẽ **tự động bay thẳng vào Trang chủ**, không bắt đăng nhập lại. |
| **UC-10** | **Quên mật khẩu & Đặt lại mật khẩu bằng OTP** | 1. Tại Đăng nhập, bấm *"Quên mật khẩu?"*.<br>2. Nhập email tài khoản (VD: `test@gmail.com`) -> Bấm *"Gửi mã xác nhận"**.<br>3. Kiểm tra hộp thư Email (hoặc Terminal Backend) để lấy mã OTP 6 số.<br>4. Nhập mã OTP, nhập Mật khẩu mới $\ge 8$ ký tự và Xác nhận mật khẩu.<br>5. Bấm *"Xác nhận đổi mật khẩu"*. | • Gửi mã: Server gửi email thật chứa mã OTP (hiệu lực 10 phút).<br>• Giao diện mở bước 2 với đồng hồ đếm ngược 60s.<br>• Đổi mật khẩu: Server cập nhật mật khẩu mới (Argon2id) và thu hồi mọi session cũ.<br>• Tự động quay về màn hình Đăng nhập với email điền sẵn.<br>• Đăng nhập ngay bằng mật khẩu mới thành công 100%. |


