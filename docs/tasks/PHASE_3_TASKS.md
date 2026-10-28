# PHASE 3: DASHBOARD, TỐI ƯU & HỒ SƠ NỘP ĐỒ ÁN
> **Milestone trên GitHub:** `Phase 3 - Hoàn thiện và nộp`

---

## 📌 Parent Issue #7: `[Phase 3][Dashboard & Reminder] Tiến độ học tập & Thông báo`
* **Mô tả:** Màn hình Dashboard, vẽ biểu đồ MPAndroidChart, tính Streak và gửi thông báo WorkManager.

### [Sub-issue 7A] Màn hình Dashboard, Biểu đồ Band điểm & Streak học tập
* **Tiêu đề GitHub:** `[Phase 3][Dashboard][Sub-issue A] Màn hình Dashboard, Biểu đồ Band điểm & Streak học tập`
* **Assignee:** Thành viên 3 (Mobile)
* **Parent Issue:** #7
```markdown
## Overview
Xây dựng màn hình chính (Dashboard) tổng hợp thông tin học tập: Band ước tính, Target Band, số ngày học liên tiếp (Streak) và biểu đồ tiến bộ.

## Reference
* **Parent issue**: #7
* **Thư viện**: `MPAndroidChart`

## Target Files (Quy định các file làm việc)
* `app/src/main/java/com/example/ielts/ui/dashboard/DashboardFragment.kt`
* `app/src/main/java/com/example/ielts/ui/dashboard/DashboardViewModel.kt`
* `app/src/main/res/layout/fragment_dashboard.xml`

## Scope
* Hiển thị widget Streak với biểu tượng ngọn lửa sinh động.
* Vẽ biểu đồ đường thể hiện điểm số các bài test qua thời gian.
* Thống kê tổng số từ vựng đã thuộc và số bài test đã hoàn thành.

## Acceptance Criteria
- [ ] Biểu đồ hiển thị đẹp mắt, tương thích chế độ Dark/Light mode
- [ ] Streak tự động tăng khi hoàn thành ít nhất 1 bài học trong ngày
- [ ] Tốc độ load dữ liệu Dashboard dưới 1 giây
```

### [Sub-issue 7B] Tính năng Nhắc nhở học tập hàng ngày qua WorkManager
* **Tiêu đề GitHub:** `[Phase 3][Reminder][Sub-issue B] Tính năng Nhắc nhở học tập hàng ngày qua WorkManager`
* **Assignee:** Thành viên 3 (Mobile)
* **Parent Issue:** #7
```markdown
## Overview
Tích hợp Android WorkManager để lập lịch gửi thông báo đẩy cục bộ (Local Push Notification) nhắc nhở người dùng vào học hàng ngày.

## Reference
* **Parent issue**: #7
* **Thư viện**: `androidx.work:work-runtime-ktx`

## Target Files (Quy định các file làm việc)
* `app/src/main/java/com/example/ielts/ui/reminder/StudyReminderWorker.kt`
* `app/src/main/java/com/example/ielts/ui/reminder/ReminderReceiver.kt`
* `app/src/main/AndroidManifest.xml` (thêm quyền POST_NOTIFICATIONS)

## Scope
* Cho phép người dùng bật/tắt nhắc nhở và chọn giờ học trong ngày (TimePicker).
* Lập lịch PeriodicWorkRequest chạy ngầm kể cả khi app bị tắt.
* Bấm vào thông báo sẽ mở thẳng vào app.

## Acceptance Criteria
- [ ] Bắn thông báo đúng giờ đã cài đặt trên Android 13+ (đã xin quyền `POST_NOTIFICATIONS`)
- [ ] Không gây hao pin thiết bị
- [ ] Giữ nguyên lịch nhắc sau khi thiết bị khởi động lại (Reboot)
```

---

## 📌 Parent Issue #8: `[Phase 3][Nộp] Kiểm thử, Tài liệu & Video Demo`
* **Mô tả:** Hồ sơ nghiệm thu đồ án: Video demo, slide báo cáo, kiểm thử E2E.

### [Sub-issue 8A] Video Demo ứng dụng tối đa 15 phút & Slide thuyết trình
* **Tiêu đề GitHub:** `[Phase 3][Nộp][Sub-issue A] Video Demo tối đa 15 phút & Slide thuyết trình`
* **Assignee:** Cả nhóm 3 người
* **Parent Issue:** #8
```markdown
## Overview
Quay video demo tối đa 15 phút thể hiện toàn bộ Use Case của ứng dụng (đặc biệt là 4 kỹ năng và chấm điểm AI) và hoàn thiện Slide thuyết trình.

## Reference
* **Parent issue**: #8
* **Rubric môn học**: Yêu cầu video demo <= 15 phút, slide trình bày kiến trúc và đóng góp của từng thành viên

## Target Files (Quy định các file làm việc)
* `README.md` (chèn link Video YouTube/Drive & link Slide)
* `docs/presentation/SLIDE_REPORT.pdf`
* `docs/video_script.md`

## Scope
* Kịch bản quay video: Đăng nhập -> Kiểm tra đầu vào -> Luyện 4 kỹ năng (Reading, Listening, Writing AI, Speaking AI) -> Flashcard -> Dashboard.
* Gắn tag phiên bản release `v1.0` cho bản nộp trên GitHub.
* Đính kèm link video (YouTube/Drive) vào file README.md.

## Acceptance Criteria
- [ ] Video có độ dài <= 15 phút, có link trong README
- [ ] Demo mượt mà không xảy ra crash ứng dụng trong video
- [ ] Đầy đủ link video và slide trong tài liệu nộp bài
```

### [Sub-issue 8B] Kiểm thử End-to-End toàn diện & Tối ưu hiệu năng
* **Tiêu đề GitHub:** `[Phase 3][Nộp][Sub-issue B] Kiểm thử End-to-End toàn diện & Tối ưu hiệu năng`
* **Assignee:** Cả nhóm 3 người
* **Parent Issue:** #8
```markdown
## Overview
Thực hiện kiểm thử toàn bộ luồng sử dụng của ứng dụng từ lúc người dùng mới đăng ký đến khi hoàn thành các bài thi và xem kết quả.

## Reference
* **Parent issue**: #8
* **Danh sách Use Cases**: 10+ Use Cases chính của đồ án

## Target Files (Quy định các file làm việc)
* `app/src/androidTest/...`
* `docs/testing/TEST_CASES_REPORT.md`

## Scope
* Test kiểm tra tràn bộ nhớ (Memory Leak) khi phát audio hoặc ghi âm lâu.
* Test khi mất kết nối mạng (Offline handling).
* Sửa toàn bộ các lỗi UI bị lệch layout trên các dòng máy Android khác nhau.

## Acceptance Criteria
- [ ] Không có lỗi crash nghiêm trọng (Fatal Exception) trong suốt quá trình test
- [ ] Xử lý thông báo mất mạng thân thiện với người dùng
- [ ] Ứng dụng chạy mượt mà ở mức 60fps trên thiết bị thật
```
