# PHASE 2: PHÁT TRIỂN 4 KỸ NĂNG & CORE FEATURES
> **Milestone trên GitHub:** `Phase 2 - Core 4 Kỹ năng`

---

## 📌 Parent Issue #2: `[Phase 2][Reading] Module Luyện thi Reading & Chấm điểm`
* **Mô tả:** Màn hình đọc passage, câu hỏi split view, highlight và API chấm điểm.

### [Sub-issue 2A] Giao diện Reading Split-View, Timer & Highlight
* **Tiêu đề GitHub:** `[Phase 2][Reading][Sub-issue A] Giao diện Reading Split-View, Timer & Highlight`
* **Assignee:** Thành viên 2 (Mobile)
* **Parent Issue:** #2
```markdown
## Overview
Xây dựng giao diện luyện đề Reading chia đôi màn hình (Passage và Questions) có đồng hồ bấm giờ đếm ngược và hỗ trợ highlight văn bản.

## Reference
* **Parent issue**: #2
* **Figma UI**: Module Reading Interface

## Target Files (Quy định các file làm việc)
* `app/src/main/java/com/example/ielts/ui/reading/ReadingPracticeActivity.kt`
* `app/src/main/java/com/example/ielts/ui/reading/ReadingViewModel.kt`
* `app/src/main/res/layout/activity_reading_practice.xml`
* `app/src/main/res/layout/item_reading_question.xml`

## Scope
* Layout chia đôi: Nửa trên hiển thị bài đọc (hỗ trợ cuộn độc lập), nửa dưới hiển thị câu hỏi.
* Đếm ngược thời gian làm bài (60 phút) và tự động thu bài khi hết giờ.
* Cho phép bôi đen đoạn văn để highlight màu vàng giúp tìm thông tin dễ dàng.

## Acceptance Criteria
- [ ] Giao diện chia đôi màn hình cuộn mượt mà, không bị giật lag
- [ ] Đồng hồ đếm ngược hoạt động chính xác khi ẩn/hiện app
- [ ] Highlight lưu tạm được trong suốt phiên làm bài
```

### [Sub-issue 2B] Backend API Đề thi Reading & Tự động chấm điểm
* **Tiêu đề GitHub:** `[Phase 2][Reading][Sub-issue B] Backend API Đề thi Reading & Tự động chấm điểm`
* **Assignee:** Thành viên 1 (Backend)
* **Parent Issue:** #2
```markdown
## Overview
Xây dựng API trả về nội dung đề thi Reading (Passage + Câu hỏi trắc nghiệm/điền từ) và API nộp bài tự động tính số câu đúng cùng Band điểm.

## Reference
* **Parent issue**: #2
* **Thang điểm**: Bảng quy đổi điểm IELTS Reading (40 câu sang thang 9.0)

## Target Files (Quy định các file làm việc)
* `backend/src/controllers/reading.controller.ts`
* `backend/src/routes/reading.routes.ts`
* `backend/src/services/reading_grader.service.ts`

## Scope
* Seed data 2-3 đề mẫu chuẩn format Cambridge IELTS.
* API `GET /api/v1/tests/reading/:id`.
* API `POST /api/v1/submissions/reading` đối chiếu đáp án, tính số câu đúng và quy đổi ra Band điểm tương ứng.

## Acceptance Criteria
- [ ] Trả về đầy đủ bài đọc, loại câu hỏi và các lựa chọn
- [ ] Chấm điểm chính xác theo thang điểm IELTS Academic
- [ ] Trả về giải thích chi tiết cho từng câu hỏi
```

---

## 📌 Parent Issue #3: `[Phase 2][Listening] Module Luyện thi Listening với ExoPlayer`
* **Mô tả:** Phát audio nghe với ExoPlayer, chỉnh tốc độ phát và câu hỏi.

### [Sub-issue 3A] Tích hợp ExoPlayer, Chỉnh tốc độ phát & Transcript đồng bộ
* **Tiêu đề GitHub:** `[Phase 2][Listening][Sub-issue A] Tích hợp ExoPlayer, Chỉnh tốc độ phát & Transcript đồng bộ`
* **Assignee:** Thành viên 2 (Mobile)
* **Parent Issue:** #3
```markdown
## Overview
Tích hợp thư viện Media3 ExoPlayer để phát file âm thanh Listening, hỗ trợ chỉnh tốc độ phát lại và hiển thị transcript có thể bấm để tua.

## Reference
* **Parent issue**: #3
* **Thư viện**: `androidx.media3:media3-exoplayer`

## Target Files (Quy định các file làm việc)
* `app/src/main/java/com/example/ielts/ui/listening/ListeningPracticeActivity.kt`
* `app/src/main/java/com/example/ielts/ui/listening/ListeningViewModel.kt`
* `app/src/main/res/layout/activity_listening_practice.xml`

## Scope
* Trình phát audio: Play, Pause, Seekbar, nút tua nhanh/lùi 5 giây.
* Bộ chọn tốc độ phát: 0.75x, 1.0x, 1.25x.
* Hiển thị danh sách câu hỏi đồng bộ với audio.

## Acceptance Criteria
- [ ] Audio phát mượt mà từ URL cloud, không bị giật khi tua
- [ ] Tốc độ thay đổi tức thì và không làm méo tiếng quá mức
- [ ] Tự động tạm dừng phát khi có cuộc gọi đến hoặc thoát màn hình
```

---

## 📌 Parent Issue #4: `[Phase 2][Writing] Module Luyện thi Writing & AI Evaluation`
* **Mô tả:** Trình soạn thảo văn bản Task 1/2 và pipeline AI Gemini chấm điểm 4 tiêu chí.

### [Sub-issue 4A] Trình soạn thảo Writing Task 1 & 2 với Realtime Word Count
* **Tiêu đề GitHub:** `[Phase 2][Writing][Sub-issue A] Trình soạn thảo Writing Task 1 & 2 với Realtime Word Count`
* **Assignee:** Thành viên 3 (Mobile)
* **Parent Issue:** #4
```markdown
## Overview
Xây dựng màn hình làm bài Writing hiển thị đề bài, ô nhập văn bản có đếm số từ theo thời gian thực và đồng hồ bấm giờ.

## Reference
* **Parent issue**: #4
* **Quy chuẩn IELTS**: Task 1 tối thiểu 150 từ, Task 2 tối thiểu 250 từ

## Target Files (Quy định các file làm việc)
* `app/src/main/java/com/example/ielts/ui/writing/WritingEditorActivity.kt`
* `app/src/main/java/com/example/ielts/ui/writing/WritingViewModel.kt`
* `app/src/main/res/layout/activity_writing_editor.xml`
* `app/src/main/res/layout/activity_writing_evaluation.xml`

## Scope
* Hiển thị đề bài Task 1 (kèm ảnh biểu đồ) hoặc Task 2.
* Ô nhập văn bản lớn, đếm từ realtime. Đổi màu cảnh báo nếu chưa đủ số từ tối thiểu.
* Tính năng lưu nháp (Draft) bài viết tự động vào local Room DB để tránh mất dữ liệu.

## Acceptance Criteria
- [ ] Đếm số từ chính xác theo thời gian thực (bỏ qua khoảng trắng thừa)
- [ ] Dữ liệu bài viết không bị mất khi xoay màn hình hoặc thoát app đột ngột
- [ ] Nút "Nộp bài chấm điểm" gửi dữ liệu lên server thành công
```

### [Sub-issue 4B] Pipeline AI (LLM) chấm điểm Writing chuẩn 4 tiêu chí IELTS
* **Tiêu đề GitHub:** `[Phase 2][Writing][Sub-issue B] Pipeline AI (LLM) chấm điểm Writing chuẩn 4 tiêu chí IELTS`
* **Assignee:** Thành viên 1 (Backend)
* **Parent Issue:** #4
```markdown
## Overview
Tích hợp Gemini 3.8 Flash API để đánh giá bài viết Writing theo 4 tiêu chí chuẩn IELTS và trả về dữ liệu cấu trúc JSON gồm điểm số, nhận xét và sửa lỗi.

## Reference
* **Parent issue**: #4
* **Tài liệu**: Google Gemini API JSON Schema

## Target Files (Quy định các file làm việc)
* `backend/src/services/ai_writing.service.ts`
* `backend/src/controllers/ai_evaluation.controller.ts`
* `backend/src/prompts/ielts_writing_prompt.ts`

## Scope
* Viết prompt chuẩn IELTS cho 4 tiêu chí: Task Achievement, Coherence & Cohesion, Lexical Resource, Grammatical Range & Accuracy.
* Ép kiểu phản hồi từ LLM về đúng định dạng JSON xác định trước.
* Trích xuất danh sách các câu sai ngữ pháp và đưa ra câu gợi ý sửa lại (Rewrite).

## Acceptance Criteria
- [ ] Thời gian phản hồi chấm bài dưới 10 giây
- [ ] Kết quả trả về đúng định dạng JSON 100%, không bị lỗi parse
- [ ] Điểm số ước tính và feedback có tính xây dựng cao
```

---

## 📌 Parent Issue #5: `[Phase 2][Speaking] Module Luyện thi Speaking & AI Ghi âm`
* **Mô tả:** Giao diện thi nói bấm giờ, ghi âm và STT AI chấm Speaking.

### [Sub-issue 5A] Giao diện Cue Card, Bộ bấm giờ & Ghi âm giọng nói (Audio Recorder)
* **Tiêu đề GitHub:** `[Phase 2][Speaking][Sub-issue A] Giao diện Cue Card, Bộ bấm giờ & Ghi âm giọng nói (Audio Recorder)`
* **Assignee:** Thành viên 2 (Mobile)
* **Parent Issue:** #5
```markdown
## Overview
Xây dựng màn hình thi Speaking hiển thị Cue Card, đồng hồ chuẩn bị/nói và bộ ghi âm giọng nói người dùng có hiển thị trực quan.

## Reference
* **Parent issue**: #5
* **Framework**: Android `MediaRecorder` hoặc `AudioRecord`

## Target Files (Quy định các file làm việc)
* `app/src/main/java/com/example/ielts/ui/speaking/SpeakingPracticeActivity.kt`
* `app/src/main/java/com/example/ielts/ui/speaking/SpeakingViewModel.kt`
* `app/src/main/java/com/example/ielts/utils/AudioRecorderHelper.kt`
* `app/src/main/res/layout/activity_speaking_practice.xml`

## Scope
* Yêu cầu cấp quyền `RECORD_AUDIO` đúng chuẩn Android runtime permission.
* Đồng hồ đếm ngược 1 phút chuẩn bị -> Tự động chuyển sang 2 phút ghi âm bài nói.
* Ghi âm file âm thanh định dạng `.m4a` / `.aac` và cho phép nghe lại trước khi nộp.

## Acceptance Criteria
- [ ] Ghi âm rõ tiếng, không bị rè hoặc mất tiếng
- [ ] Xử lý mượt mà khi người dùng từ chối cấp quyền micro
- [ ] Upload file âm thanh thành công lên server/cloud storage
```

### [Sub-issue 5B] Pipeline STT & AI Đánh giá bài Speaking
* **Tiêu đề GitHub:** `[Phase 2][Speaking][Sub-issue B] Pipeline STT & AI Đánh giá bài Speaking`
* **Assignee:** Thành viên 1 (Backend)
* **Parent Issue:** #5
```markdown
## Overview
Xây dựng backend service nhận file ghi âm từ mobile, chuyển đổi thành văn bản (STT) và phân tích đưa ra band điểm Speaking ước tính.

## Reference
* **Parent issue**: #5
* **Engine**: Whisper API / Gemini Multimodal Audio

## Target Files (Quy định các file làm việc)
* `backend/src/services/stt_service.ts`
* `backend/src/services/ai_speaking.service.ts`
* `backend/src/controllers/speaking.controller.ts`

## Scope
* Nhận file audio upload qua multipart/form-data.
* Đưa file qua STT để lấy transcript bài nói của thí sinh.
* Chấm điểm và đưa ra feedback về độ trôi chảy, từ vựng và ngữ pháp.

## Acceptance Criteria
- [ ] Xử lý được các file ghi âm có độ dài từ 1 - 3 phút
- [ ] Trả về transcript bài nói với độ chính xác cao
- [ ] Phản hồi kết quả đánh giá chi tiết cho client
```

---

## 📌 Parent Issue #6: `[Phase 2][Vocabulary] Hệ thống Flashcards & Thuật toán Lặp lại ngắt quãng (SRS)`
* **Mô tả:** Thẻ lật từ vựng thông minh và lưu từ vựng offline.

### [Sub-issue 6A] Màn hình Flashcard lật thẻ 2 mặt & Thuật toán SRS
* **Tiêu đề GitHub:** `[Phase 2][Vocabulary][Sub-issue A] Màn hình Flashcard lật thẻ 2 mặt & Thuật toán SRS`
* **Assignee:** Thành viên 3 (Mobile)
* **Parent Issue:** #6
```markdown
## Overview
Xây dựng màn hình học từ vựng dạng thẻ Flashcard có hiệu ứng lật 2 mặt (Từ vựng / Nghĩa + Ví dụ) và phân loại độ nhớ (Dễ, Vừa, Khó).

## Reference
* **Parent issue**: #6
* **Animation**: Flip card animation bằng XML Animator

## Target Files (Quy định các file làm việc)
* `app/src/main/java/com/example/ielts/data/local/entity/VocabularyEntity.kt`
* `app/src/main/java/com/example/ielts/ui/vocabulary/FlashcardActivity.kt`
* `app/src/main/java/com/example/ielts/ui/vocabulary/VocabularyViewModel.kt`
* `app/src/main/res/layout/activity_flashcard.xml`

## Scope
* Thẻ từ vựng hiển thị: Từ, Phiên âm IPA, Nghĩa tiếng Việt, Câu ví dụ mẫu.
* 3 nút đánh giá sau khi lật thẻ: Khó (ôn lại ngay), Vừa (3 ngày), Dễ (7 ngày).
* Lưu trạng thái học vào Room DB để hỗ trợ học offline.

## Acceptance Criteria
- [ ] Hiệu ứng lật thẻ 3D mượt mà, không bị giật lag
- [ ] Tính toán chính xác ngày cần ôn tập tiếp theo của từ
- [ ] Hoạt động bình thường cả khi không có kết nối Internet
```
