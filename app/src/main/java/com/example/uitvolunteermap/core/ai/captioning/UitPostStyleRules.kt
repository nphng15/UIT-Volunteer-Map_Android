package com.example.uitvolunteermap.core.ai.captioning

import com.example.uitvolunteermap.core.ai.captioning.model.CaptionSuggestion
import com.example.uitvolunteermap.core.ai.captioning.model.UitContext
import javax.inject.Inject
import javax.inject.Singleton

data class LabelInput(
    val text: String,
    val confidence: Float
)

@Singleton
class UitPostStyleRules @Inject constructor() {

    fun apply(
        labels: List<LabelInput>,
        ctx: UitContext,
        photoCount: Int,
        nonce: Int = 0,
        ocrLines: List<String> = emptyList()
    ): CaptionSuggestion {
        val rankedLabels = labels
            .filter { it.confidence >= MIN_CONFIDENCE }
            .sortedByDescending { it.confidence }
            .distinctBy { it.text.lowercase() }

        val subjects = rankedLabels
            .mapNotNull { UitPostTemplates.LabelToSubject[it.text.lowercase()] }
            .take(MAX_SUBJECTS)
            .ifEmpty { listOf(UitPostTemplates.FallbackSubject) }

        val seed = computeSeed(rankedLabels, ctx, photoCount, nonce)
        // OCR may reveal the real event/program name printed on a banner.
        val bannerHeadline = pickBannerHeadline(ocrLines)
        val program = bannerHeadline ?: ctx.resolvedProgram
        val team = ctx.resolvedTeam

        val title = pick(UitPostTemplates.TitleTemplates, seed, 0)
            .replace("{program}", program)
            .replace("{team}", team)
            .trim()

        val opening = pick(UitPostTemplates.OpeningSentences, seed, 1)
            .replace("{program}", program)
            .replace("{team}", team)
        val connector = pick(UitPostTemplates.SubjectsConnectors, seed, 2)
            .replace("{subjects}", buildSubjectsPhrase(subjects))
        val middle = pick(UitPostTemplates.MiddleEmphases, seed, 3)
        val closing = pick(UitPostTemplates.ClosingSentences, seed, 4)
            .replace("{program}", program)
            .replace("{team}", team)

        val bannerSentence = bannerHeadline
            ?.let { "Tấm băng-rôn “$it” nổi bật giữa khu vực hoạt động." }

        val content = listOfNotNull(opening, "$connector.", bannerSentence, middle, closing)
            .joinToString(separator = " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        val hashtags = buildHashtags(subjects, ctx, ocrLines)
        val perPhotoCaptions = buildPerPhotoCaptions(subjects, photoCount, team)

        return CaptionSuggestion(
            title = title,
            content = content,
            hashtags = hashtags,
            perPhotoCaptions = perPhotoCaptions,
            rawLabels = rankedLabels.map { it.text }
        )
    }

    /**
     * Pick the OCR line that most looks like an event banner headline: a few
     * words, mostly letters, not a date or a stray fragment.
     */
    private fun pickBannerHeadline(ocrLines: List<String>): String? {
        return ocrLines
            .map { it.trim().trim('-', '–', '•', '*', '.', ',') }
            .filter { line ->
                val words = line.split(Regex("\\s+"))
                val letters = line.count { it.isLetter() }
                words.size in 2..7 &&
                    letters >= line.length / 2 &&
                    !line.any { it.isDigit() && line.count { c -> c.isDigit() } > 4 }
            }
            .maxByOrNull { line -> line.count { it.isUpperCase() } }
            ?.let { headline ->
                // Title-case lightly so an ALL-CAPS banner reads naturally.
                if (headline == headline.uppercase()) {
                    headline.split(" ").joinToString(" ") { w ->
                        w.lowercase().replaceFirstChar { c -> c.uppercase() }
                    }
                } else headline
            }
    }

    private fun <T> pick(list: List<T>, seed: Int, axis: Int): T {
        val offset = (seed xor (axis * PRIME)).rem(list.size).let { if (it < 0) it + list.size else it }
        return list[offset]
    }

    private fun buildSubjectsPhrase(subjects: List<UitPostTemplates.SubjectEntry>): String {
        val phrases = subjects.map { "${it.text} ${it.emoji}".trim() }
        return when (phrases.size) {
            0 -> "những khoảnh khắc đáng nhớ 📸"
            1 -> phrases.first()
            2 -> "${phrases[0]} cùng ${phrases[1]}"
            else -> {
                val head = phrases.dropLast(1).joinToString(", ")
                val tail = phrases.last()
                "$head và $tail"
            }
        }
    }

    private fun buildHashtags(
        subjects: List<UitPostTemplates.SubjectEntry>,
        ctx: UitContext,
        ocrLines: List<String> = emptyList()
    ): List<String> {
        val result = LinkedHashSet<String>()
        result.addAll(UitPostTemplates.BaseHashtags)

        subjects.flatMap { it.extraHashtags }.forEach { result.add(it) }

        // Match program hashtags against both the known context AND any text the
        // OCR found on banners in the photos.
        val programText = buildString {
            append(ctx.campaignName.orEmpty()).append(' ')
            append(ctx.programName.orEmpty()).append(' ')
            append(ocrLines.joinToString(" "))
        }
        UitPostTemplates.ProgramHashtagHints.forEach { (regex, tag) ->
            if (regex.containsMatchIn(programText)) {
                result.add(tag)
            }
        }

        ctx.teamName
            ?.takeIf { it.isNotBlank() }
            ?.let { team ->
                val sanitized = team
                    .replace(Regex("[^\\p{L}\\p{Nd}]+"), "")
                    .let(::removeVietnameseDiacritics)
                if (sanitized.isNotBlank()) {
                    result.add("#${sanitized}")
                }
            }

        return result.take(MAX_HASHTAGS).toList()
    }

    private fun buildPerPhotoCaptions(
        subjects: List<UitPostTemplates.SubjectEntry>,
        photoCount: Int,
        team: String
    ): List<String> {
        if (photoCount <= 0) return emptyList()
        if (subjects.isEmpty()) {
            return List(photoCount) { idx -> "Khoảnh khắc ${idx + 1} cùng $team 📸" }
        }
        return List(photoCount) { idx ->
            val subject = subjects[idx % subjects.size]
            "${subject.text.replaceFirstChar { it.uppercase() }} ${subject.emoji}".trim()
        }
    }

    private fun computeSeed(
        labels: List<LabelInput>,
        ctx: UitContext,
        photoCount: Int,
        nonce: Int
    ): Int {
        var seed = photoCount.coerceAtLeast(1)
        seed = seed * 31 + labels.size
        seed = seed * 31 + (ctx.campaignName?.length ?: 0)
        seed = seed * 31 + (ctx.teamName?.length ?: 0)
        seed = seed * 31 + nonce
        labels.forEach { seed = seed * 31 + it.text.lowercase().hashCode() }
        return Math.floorMod(seed, Int.MAX_VALUE)
    }

    private fun removeVietnameseDiacritics(input: String): String {
        if (input.isEmpty()) return input
        val map = mapOf(
            'à' to 'a', 'á' to 'a', 'ạ' to 'a', 'ả' to 'a', 'ã' to 'a',
            'â' to 'a', 'ầ' to 'a', 'ấ' to 'a', 'ậ' to 'a', 'ẩ' to 'a', 'ẫ' to 'a',
            'ă' to 'a', 'ằ' to 'a', 'ắ' to 'a', 'ặ' to 'a', 'ẳ' to 'a', 'ẵ' to 'a',
            'è' to 'e', 'é' to 'e', 'ẹ' to 'e', 'ẻ' to 'e', 'ẽ' to 'e',
            'ê' to 'e', 'ề' to 'e', 'ế' to 'e', 'ệ' to 'e', 'ể' to 'e', 'ễ' to 'e',
            'ì' to 'i', 'í' to 'i', 'ị' to 'i', 'ỉ' to 'i', 'ĩ' to 'i',
            'ò' to 'o', 'ó' to 'o', 'ọ' to 'o', 'ỏ' to 'o', 'õ' to 'o',
            'ô' to 'o', 'ồ' to 'o', 'ố' to 'o', 'ộ' to 'o', 'ổ' to 'o', 'ỗ' to 'o',
            'ơ' to 'o', 'ờ' to 'o', 'ớ' to 'o', 'ợ' to 'o', 'ở' to 'o', 'ỡ' to 'o',
            'ù' to 'u', 'ú' to 'u', 'ụ' to 'u', 'ủ' to 'u', 'ũ' to 'u',
            'ư' to 'u', 'ừ' to 'u', 'ứ' to 'u', 'ự' to 'u', 'ử' to 'u', 'ữ' to 'u',
            'ỳ' to 'y', 'ý' to 'y', 'ỵ' to 'y', 'ỷ' to 'y', 'ỹ' to 'y',
            'đ' to 'd'
        )
        val builder = StringBuilder(input.length)
        input.forEach { ch ->
            val lower = ch.lowercaseChar()
            val mapped = map[lower]
            val replacement = if (mapped != null) {
                if (ch.isUpperCase()) mapped.uppercaseChar() else mapped
            } else ch
            builder.append(replacement)
        }
        return builder.toString()
    }

    companion object {
        const val MIN_CONFIDENCE = 0.6f
        const val MAX_SUBJECTS = 3
        const val MAX_HASHTAGS = 6
        private const val PRIME = 0x9E3779B1.toInt()
    }
}
