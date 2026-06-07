package com.example.uitvolunteermap.core.ai.captioning.model

enum class CaptionMode {
    /**
     * Fast path: ML Kit Image Labeling -> UIT template engine.
     * Always available, deterministic, no model download required.
     */
    TEMPLATE_FAST,

    /**
     * Quality path: Gemma 3n E2B (vision-language) via LiteRT-LM sees the
     * picked images directly and writes the caption. Requires the
     * `gemma3n.litertlm` model file to be present on disk. Falls back to
     * TEMPLATE_FAST if the model is missing.
     */
    VL_GEMMA
}
