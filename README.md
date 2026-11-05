# BandUp — Gemini IELTS Writing AI Evaluation

**Module:** Phase 2 / Writing / Sub-issue B — Pipeline AI chấm điểm IELTS Writing theo 4 tiêu chí

**Backend:** Node.js, TypeScript, Express, Zod

**Trạng thái:** MVP hoàn thành, 21/21 bài kiểm thử tự động PASS.

## 1. Giới thiệu

Gemini Writing AI là module chấm điểm IELTS Writing trong ứng dụng BandUp, sử dụng Google Gemini API để phân tích bài viết dựa trên bốn tiêu chí IELTS Writing.

Hệ thống cung cấp:

- Điểm ước tính cho từng tiêu chí IELTS Writing.
- Điểm tổng (Estimated Band).
- Nhận xét chi tiết bằng tiếng Việt.
- Phát hiện và sửa lỗi ngữ pháp.
- Kế hoạch cải thiện bài viết.
- Dẫn chứng từ bài viết để giải thích đánh giá.

Hỗ trợ IELTS Academic Task 1, General Training Task 1 và Task 2.

**Lưu ý:** Điểm số AI chỉ phục vụ luyện tập, không thay thế đánh giá chính thức từ giám khảo IELTS.

## 2. Kiến trúc hệ thống

```text
Android / Postman
       |
       v
POST /ai/evaluate/writing
       |
       v
JWT Authentication
       |
       v
Input Validation (Zod)
       |
       v
IELTS Prompt + JSON Schema
       |
       v
Gemini API
       |
       +-- Primary Model
       +-- Retry / Fallback
       |
       v
Output Validation
       |
       +-- Validate Evidence
       +-- Validate Grammar Corrections
       +-- Calculate Estimated Band
       |
       v
Structured JSON Response
```

## 3. Cấu hình

Trong `backend/.env`:

```dotenv
GEMINI_API_KEY=YOUR_REAL_GEMINI_API_KEY
GEMINI_MODEL=gemini-3.5-flash-lite
GEMINI_FALLBACK_MODEL=gemini-3.1-flash-lite
GEMINI_TIMEOUT_MS=30000
```

API key chỉ được lưu tại Backend, không đưa vào ứng dụng Android hoặc GitHub.

Khởi động:

```powershell
cd E:\cc\NT118.R14-Group20\backend
npm install
npm run dev
```

## 4. API Endpoint

```http
POST http://localhost:3000/ai/evaluate/writing
```

**Authorization:** Bearer JWT

**Content-Type:** application/json

Ví dụ Request:

```json
{
  "taskType": "TASK_2",
  "question": "Some people believe that working from home is better than working in an office. To what extent do you agree or disagree?",
  "essay": "Working from home has become more common in recent years. I agree that it can improve employees' lives because they spend less time commuting. However, some workers feels lonely when they do not meet colleagues regularly. Companies should therefore allow employees to work from home while also providing opportunities for face-to-face meetings."
}
```

Ngoài `TASK_2`, hệ thống còn hỗ trợ `ACADEMIC_TASK_1` và `GENERAL_TASK_1`. Academic Task 1 cần truyền thêm `sourceMaterial` mô tả dữ liệu biểu đồ bằng văn bản.

## 5. Tiêu chí chấm điểm

| Tiêu chí | Nội dung |
|---|---|
| Task Response / Task Achievement | Đáp ứng yêu cầu đề bài, phát triển luận điểm |
| Coherence and Cohesion | Bố cục, tính logic và liên kết ý |
| Lexical Resource | Vốn từ vựng và mức độ chính xác |
| Grammatical Range and Accuracy | Cấu trúc câu và độ chính xác ngữ pháp |

Mỗi tiêu chí được chấm từ 0 đến 9, theo bước 0,5 band.

Backend tính điểm trung bình bốn tiêu chí và làm tròn về mức 0,5 gần nhất.

## 6. Kiểm tra dữ liệu từ Gemini

Gemini trả về Structured JSON, được Backend kiểm tra lại bằng Zod.

**Evidence Validation**

- Kiểm tra đoạn trích có xuất hiện trong bài gốc không.
- Chuẩn hóa dấu ngoặc hoặc khoảng trắng khi phù hợp.
- Evidence không xác minh được sẽ bị bỏ và ghi cảnh báo.

**Grammar Validation**

- Chỉ chấp nhận câu gốc xuất hiện trong bài viết.
- Loại các correction trùng lặp.
- Không trả correction nếu câu sửa không thay đổi.
- Giới hạn tối đa 5 câu được sửa.

## 7. Retry và Fallback

Hệ thống hỗ trợ tối đa hai lần thử model chính và một lần thử model dự phòng khi gặp lỗi tạm thời.

Timeout cấu hình là **30 giây**, với giới hạn tối đa 10 giây cho từng lượt gọi Gemini.

Khi Gemini trả `429 Rate Limited`, Backend trả lỗi tương ứng thay vì liên tục thử lại, giúp tránh tiêu thụ quota không cần thiết.

## 8. Kiểm thử

```powershell
npm run typecheck
npm run lint
npm run test:writing
```

Kết quả đã ghi nhận:

- TypeScript Typecheck: PASS
- ESLint: PASS
- Automated Tests: 21/21 PASS
- Real Gemini API: Đã chấm thành công hai bài Task 2 với tổng cộng 5 lỗi ngữ pháp được cố ý đưa vào và phát hiện đủ.

Các bài kiểm thử tự động sử dụng mock Gemini, không tiêu thụ quota API thực tế.

## 9. Giới hạn hiện tại

Module chưa hỗ trợ lưu lịch sử bài viết vào PostgreSQL, tải ảnh biểu đồ trực tiếp, hoặc quản lý quota theo từng người dùng.

Độ chính xác của điểm IELTS và tốc độ phản hồi cần tiếp tục được đánh giá trên bộ dữ liệu lớn hơn, có điểm tham chiếu từ giám khảo.

## 10. Tài liệu tham khảo

- [Gemini API](https://ai.google.dev/gemini-api/docs)
- [Google AI Studio](https://aistudio.google.com/)
- [IELTS Writing Resources](https://ielts.org/take-a-test/preparation-resources/writing-test-resources)
- [GitHub Pull Request #49](https://github.com/huynhhoangtuvan11092006/NT118.R14-Group20/pull/49)