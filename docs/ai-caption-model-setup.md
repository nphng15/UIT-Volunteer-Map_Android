# AI Captioning — Cài model on-device (tùy chọn)

Tính năng **Gợi ý AI** ở màn "Thêm bài viết" có 2 chế độ:

| Chế độ | Cần model? | Mô tả |
|--------|-----------|-------|
| **Mẫu nhanh** (mặc định) | ❌ Không | ML Kit phân tích ảnh + template tiếng Việt. Tức thì, chạy ngay sau khi `git pull` + build, **không cần làm gì thêm**. |
| **Dùng AI** | ✅ Có | Một mô hình ngôn ngữ chạy on-device (`.task`) viết lại caption tự nhiên hơn. Phải đặt file model vào máy như hướng dẫn dưới. |

> Nếu không cài model, nút **Dùng AI** sẽ tự nhắc và quay về **Mẫu nhanh** — app vẫn chạy bình thường.

---

## 1. Tải model

Dùng **Qwen2.5-0.5B-Instruct** (Apache-2.0, **không cần đăng nhập HuggingFace**, ~546 MB):

```
https://huggingface.co/litert-community/Qwen2.5-0.5B-Instruct/resolve/main/Qwen2.5-0.5B-Instruct_multi-prefill-seq_q8_ekv1280.task
```

Tải bằng trình duyệt, hoặc:

```bash
# macOS / Linux / Git-Bash
curl -L -o qwen.task "https://huggingface.co/litert-community/Qwen2.5-0.5B-Instruct/resolve/main/Qwen2.5-0.5B-Instruct_multi-prefill-seq_q8_ekv1280.task"
```

Muốn tiếng Việt mượt hơn (đổi lại nặng ~1.6 GB, chậm hơn): thay bằng **Qwen2.5-1.5B-Instruct**:

```
https://huggingface.co/litert-community/Qwen2.5-1.5B-Instruct/resolve/main/Qwen2.5-1.5B-Instruct_multi-prefill-seq_q8_ekv1280.task
```

> App cũng nhận các model `.task` khác của LiteRT Community (SmolLM, Phi-4-mini…) — chỉ cần đổi tên file theo bước 2.

---

## 2. Đẩy model vào máy / emulator

App tự dò file ở thư mục `llm/` với một trong các tên (ưu tiên từ trên xuống):
`qwen.task` → `gemma.task` → `smollm.task` → `phi.task` → `model.task`.

Đặt file ở **một trong hai** vị trí (đều là vùng riêng của app, **không cần quyền runtime**):

### Cách A — Bộ nhớ ngoài app-private (khuyến nghị, không cần root)

> Cần cài app trước để thư mục tồn tại; nếu chưa có thì lệnh `mkdir` bên dưới sẽ tạo.

```bash
adb shell mkdir -p /sdcard/Android/data/com.example.uitvolunteermap/files/llm
adb push qwen.task /sdcard/Android/data/com.example.uitvolunteermap/files/llm/qwen.task
```

### Cách B — Bộ nhớ trong (bản debug, qua `run-as`)

```bash
adb push qwen.task /data/local/tmp/qwen.task
adb shell run-as com.example.uitvolunteermap mkdir -p files/llm
adb shell run-as com.example.uitvolunteermap cp /data/local/tmp/qwen.task files/llm/qwen.task
```

---

## 3. Kiểm tra

1. Mở app → "Thêm bài viết" → chọn ảnh → bấm chip **Dùng AI**.
2. Lần đầu sẽ chờ vài giây (nạp model), các lần sau nhanh hơn.
3. Xem log để biết model đã nạp chưa:

```bash
adb logcat -s OnDeviceLlmEngine:I
# Thành công: "LLM engine loaded from /storage/emulated/0/Android/data/.../files/llm/qwen.task"
```

Xác nhận file đã nằm đúng chỗ:

```bash
adb shell ls -la /sdcard/Android/data/com.example.uitvolunteermap/files/llm/
```

---

## Ghi chú kỹ thuật

- **Runtime**: MediaPipe LLM Inference (`com.google.mediapipe:tasks-genai`), backend **CPU**.
- **Nạp lazy**: model chỉ nạp vào RAM khi người dùng thật sự dùng chế độ AI (tránh tốn RAM/OOM). Model 0.5B cần ~0.6–1 GB RAM khi chạy → emulator nên để **RAM ≥ 4 GB**.
- **Không bundle trong APK**: file model **không** được commit vào repo (quá lớn). Mỗi máy tự đặt như trên.
- **Gỡ AI**: chỉ cần xoá file `.task` → app tự quay về chế độ Mẫu nhanh.
- Code liên quan: `core/ai/captioning/OnDeviceLlmEngine.kt` (hằng số `MODEL_SUBDIR`, `MODEL_FILE_NAMES`).
