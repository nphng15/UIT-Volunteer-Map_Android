package com.example.uitvolunteermap.core.ai.captioning.model

enum class CaptionMode {
    /**
     * Fast path: ML Kit Image Labeling + Text Recognition (OCR) -> UIT template
     * engine. Always available, deterministic, instant, no model download.
     */
    TEMPLATE_FAST,

    /**
     * Quality path: an on-device LLM (`qwen.task` via MediaPipe LlmInference)
     * rewrites the template seed into more natural prose. Requires the model
     * file on disk; falls back to TEMPLATE_FAST when missing.
     *
     * (Enum name kept as VL_GEMMA for compatibility; current model is text-only
     * Qwen, not the gated Gemma 3n VL.)
     */
    VL_GEMMA
}
