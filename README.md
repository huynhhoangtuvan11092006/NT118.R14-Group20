# 📱 IELTS Master - Ứng Dụng Luyện Thi IELTS 4 Kỹ Năng Tích Hợp AI

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Google%20Gemini-8E75C2?style=for-the-badge&logo=google&logoColor=white" alt="Google Gemini" />
  <img src="https://img.shields.io/badge/Room%20DB-003B57?style=for-the-badge&logo=sqlite&logoColor=white" alt="Room DB" />
  <img src="https://img.shields.io/badge/Media3%20ExoPlayer-FF6F00?style=for-the-badge" alt="ExoPlayer" />
  <img src="https://img.shields.io/badge/Retrofit%202-0052CC?style=for-the-badge" alt="Retrofit" />
  <img src="https://img.shields.io/badge/MPAndroidChart-EA4335?style=for-the-badge" alt="MPAndroidChart" />
</p>

> **Môn học:** NT118 - Phát triển ứng dụng trên thiết bị di động (Lớp NT118.R14)  
> **Nhóm thực hiện:** Nhóm 20  
> **Repository:** [https://github.com/qan333/NT118.R14-Group20](https://github.com/qan333/NT118.R14-Group20)  
> **Project Management Board:** [IELTS Sprint Management Board](https://github.com/users/huynhhoangtuvan11092006/projects/1)

---

## 📖 1. Giới thiệu Dự án

**IELTS Master** là ứng dụng di động hỗ trợ người học tự ôn luyện và thi thử trọn vẹn cả **4 kỹ năng IELTS (Listening, Reading, Writing, Speaking)** theo chuẩn Academic. 

Điểm nổi bật của ứng dụng là việc kết hợp các công nghệ di động tiên tiến (Audio Streaming, Sound Recording, Room DB Offline) với **Trí tuệ nhân tạo (AI - Google Gemini & Speech-to-Text)** nhằm giải quyết bài toán khó khăn nhất của người tự học: **Chấm điểm và sửa lỗi chi tiết cho 2 kỹ năng Writing và Speaking**.

---

## ✨ 2. Các Tính năng Cốt lõi (4 Kỹ Năng)

### 📖 Reading Module
* **Giao diện Split-View:** Chia đôi màn hình bài đọc (Passage) và câu hỏi (Questions), cuộn độc lập mượt mà.
* **Highlight thông minh:** Cho phép người dùng bôi đen văn bản để lưu dấu từ khóa quan trọng.
* **Đồng hồ đếm ngược (Countdown Timer 60 phút):** Tự động thu bài khi hết giờ và đối chiếu đáp án, quy đổi sang thang Band 9.0.

### 🎧 Listening Module
* **Trình phát Media3 ExoPlayer chuyên dụng:** Hỗ trợ tua nhanh/chậm (`0.75x`, `1.0x`, `1.25x`), tua lùi/tiến 5s để nghe lại đoạn khó.
* **Transcript đồng bộ:** Hiển thị transcript hỗ trợ người học dò lại nội dung bài nghe sau khi nộp bài.

### ✍️ Writing Module & AI Evaluation
* **Trình soạn thảo chuẩn thi:** Đếm số từ theo thời gian thực (`Word Count`), cảnh báo thiếu số từ tối thiểu (Task 1: 150 từ, Task 2: 250 từ).
* **AI Writing Grader (Google Gemini API):** Chấm điểm chi tiết dựa trên 4 tiêu chí chuẩn IELTS (*Task Achievement, Coherence & Cohesion, Lexical Resource, Grammatical Range & Accuracy*), trích xuất câu sai ngữ pháp và đưa ra đoạn văn viết lại mẫu Band 8.0+.
* **Lưu nháp an toàn (Auto-save):** Tự động lưu bài viết vào Room Database để tránh mất dữ liệu khi thoát app.

### 🎙️ Speaking Module & AI STT
* **Giao diện Cue Card chuẩn:** Đếm ngược 1 phút chuẩn bị và 2 phút ghi âm bài nói.
* **Ghi âm trực tiếp (Audio Recorder):** Hiển thị visual sóng âm và hỗ trợ nghe lại trước khi nộp.
* **AI Speaking Evaluation:** Ứng dụng STT (Speech-to-Text) trích xuất transcript bài nói và phân tích từ vựng, độ trôi chảy, ngữ pháp.

### 📚 Hệ thống Từ vựng Flashcards (SRS)
* **Thuật toán Spaced Repetition (Lặp lại ngắt quãng):** Tính toán chu kỳ ôn tập thông minh (1 ngày, 3 ngày, 7 ngày) giúp ghi nhớ từ vựng dài hạn.
* **Lật thẻ 2 mặt (3D Flip Card):** Hiển thị từ, phiên âm IPA, nghĩa tiếng Việt và câu ví dụ ngữ cảnh.

### 📊 Dashboard & Study Reminder
* **Biểu đồ tiến bộ (MPAndroidChart):** Trực quan hóa điểm số các bài test qua thời gian.
* **Chuỗi ngày học liên tục (Streak):** Thúc đẩy thói quen học tập hàng ngày.
* **Nhắc nhở học tập (Android WorkManager):** Lập lịch thông báo đẩy định kỳ kể cả khi đóng ứng dụng.

---

## 👥 3. Phân công Công việc Nhóm (3 Thành viên)

| Thành viên | Tài khoản GitHub | Vai trò chính | Nhiệm vụ kỹ thuật cụ thể |
| :--- | :---: | :--- | :--- |
| **Võ Minh Quân** | `@qan333` | **Backend & AI Engineer** | • Thiết kế Database Schema & viết RESTful APIs.<br>• Tích hợp Gemini LLM chấm Writing & Speech-to-Text chấm Speaking.<br>• Quản trị Cloud Storage lưu trữ audio. |
| **Huỳnh Hoàng Tứ Văn** | `@huynhhoangtuvan11092006` | **Mobile Core Developer** | • Xây dựng module **Reading** (Split-view, Highlight, Timer).<br>• Xây dựng module **Listening** (Tích hợp Media3 ExoPlayer).<br>• Xây dựng module **Speaking** (Ghi âm Audio Recorder, runtime permission). |
| **Lê Nguyễn Phương Vy** | `@PVL-cyber` | **Mobile UI/UX & Services** | • Xây dựng luồng **Auth** (Login/Register, chọn Target Band).<br>• Xây dựng module **Writing Editor** & Giao diện AI Feedback.<br>• Xây dựng module **Flashcard SRS** (Room DB) & **Dashboard** (MPAndroidChart).<br>• Tích hợp **WorkManager** gửi thông báo nhắc học. |

---

## 🏗️ 4. Kiến trúc Hệ thống (Clean Architecture)

Ứng dụng Android được thiết kế theo mô hình **MVVM (Model - View - ViewModel)** kết hợp nguyên lý **Clean Architecture**:

```text
com.example.ielts/
├── data/                  # Tầng Dữ liệu
│   ├── local/             # Room Database, DAO, Entity (Offline Cache)
│   ├── remote/            # Retrofit Service, DTOs (Request/Response)
│   └── repository/        # Triển khai Repository Pattern
├── domain/                # Tầng Nghiệp vụ cốt lõi
│   └── model/             # Data Models (User, Test, Vocabulary)
├── ui/                    # Tầng Giao diện (Package by Feature)
│   ├── auth/              # Login, Register, Target Band
│   ├── dashboard/         # Dashboard & Analytics
│   ├── reading/           # Reading Split-view
│   ├── listening/         # Listening ExoPlayer
│   ├── writing/           # Writing Editor & AI Evaluation
│   ├── speaking/          # Speaking Recorder & Evaluation
│   └── vocabulary/        # Flashcard SRS
└── utils/                 # Tiện ích chung (Constants, Resource wrapper)
```

---

## 🛠️ 5. Công nghệ Sử dụng (Tech Stack)

* **📱 Mobile (Android):** Kotlin, MVVM, Retrofit 2, Room DB, Media3 ExoPlayer, MPAndroidChart, WorkManager.
* **🖥️ Backend & Database:** Node.js (Express/NestJS) / Python (FastAPI), PostgreSQL / MongoDB, JWT.
* **🤖 AI & Cloud:** Google Gemini 3.8 Flash API, Whisper STT, Cloudinary.

---

## 🚀 6. Hướng dẫn Cài đặt & Khởi chạy

1. **Clone mã nguồn:**
   ```bash
   git clone https://github.com/qan333/NT118.R14-Group20.git
   ```
2. **Mở dự án:**
   * Mở **Android Studio** (phiên bản Hedgehog hoặc mới hơn).
   * Chọn `File` -> `Open` -> Trỏ đến thư mục vừa clone.
3. **Đồng bộ Gradle:**
   * Chờ Android Studio tải các dependencies và sync Gradle thành công.
4. **Chạy ứng dụng:**
   * Chọn máy ảo Android (Pixel 6/7, Android 12 trở lên) hoặc kết nối thiết bị thật qua USB Debugging.
   * Bấm nút **Run `app`** (Shift + F10).

---

## 📅 7. Lộ trình Phát triển (Milestones)

* **[Phase 1 - Khởi tạo & Base](https://github.com/qan333/NT118.R14-Group20/milestone/1):** Thiết lập Database, Base Backend, Base Android và Auth Flow.
* **[Phase 2 - Core 4 Kỹ năng](https://github.com/qan333/NT118.R14-Group20/milestone/2):** Xây dựng 4 kỹ năng Reading, Listening, Writing AI, Speaking AI và Flashcard SRS.
* **[Phase 3 - Hoàn thiện và nộp](https://github.com/qan333/NT118.R14-Group20/milestone/3):** Dashboard, Nhắc học, Kiểm thử E2E, Video Demo 15 phút và Slide báo cáo.
