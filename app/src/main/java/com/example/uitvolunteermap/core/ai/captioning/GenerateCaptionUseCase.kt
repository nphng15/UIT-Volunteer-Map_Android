package com.example.uitvolunteermap.core.ai.captioning

import android.net.Uri
import com.example.uitvolunteermap.core.ai.captioning.model.CaptionSuggestion
import com.example.uitvolunteermap.core.ai.captioning.model.UitContext
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import javax.inject.Inject
import timber.log.Timber

class GenerateCaptionUseCase @Inject constructor(
    private val labeling: ImageLabelingDataSource,
    private val rules: UitPostStyleRules
) {

    suspend operator fun invoke(
        uris: List<Uri>,
        ctx: UitContext,
        nonce: Int = 0
    ): AppResult<CaptionSuggestion> {
        if (uris.isEmpty()) {
            return AppResult.Error(AppError.Validation("Chưa có ảnh để gợi ý nội dung."))
        }

        val labels = uris.flatMap { uri ->
            runCatching { labeling.label(uri) }
                .onFailure { Timber.w(it, "Skipping labels for %s", uri) }
                .getOrDefault(emptyList())
        }

        val suggestion = rules.apply(
            labels = labels,
            ctx = ctx,
            photoCount = uris.size,
            nonce = nonce
        )
        return AppResult.Success(suggestion)
    }
}
