package com.example.uitvolunteermap.core.ai.captioning

import com.example.uitvolunteermap.core.ai.captioning.model.UitContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UitPostStyleRulesTest {

    private val rules = UitPostStyleRules()

    @Test
    fun `apply with rich labels produces title and content mentioning team and program`() {
        val ctx = UitContext(
            campaignName = "Mùa Hè Xanh 2026",
            teamName = "Đội Sao Sáng"
        )
        val labels = listOf(
            LabelInput(text = "Person", confidence = 0.92f),
            LabelInput(text = "Book", confidence = 0.81f),
            LabelInput(text = "Smile", confidence = 0.74f)
        )

        val result = rules.apply(labels = labels, ctx = ctx, photoCount = 3)

        assertTrue(
            "Title or content should mention the team",
            result.title.contains("Đội Sao Sáng") || result.content.contains("Đội Sao Sáng")
        )
        assertTrue(
            "Content should mention the program",
            result.content.contains("Mùa Hè Xanh 2026")
        )
        assertTrue(
            "Should include base hashtags",
            result.hashtags.containsAll(listOf("#UIT", "#UITVolunteer", "#TinhNguyenUIT"))
        )
        assertTrue(
            "Should include #MuaHeXanh from program hint",
            result.hashtags.contains("#MuaHeXanh")
        )
        assertEquals(3, result.perPhotoCaptions.size)
        assertFalse(
            "Per-photo captions should never be blank",
            result.perPhotoCaptions.any { it.isBlank() }
        )
    }

    @Test
    fun `apply with low confidence labels falls back to generic copy`() {
        val ctx = UitContext(teamName = "Đội Hỗ Trợ")
        val labels = listOf(
            LabelInput(text = "Person", confidence = 0.3f),
            LabelInput(text = "Wall", confidence = 0.4f)
        )

        val result = rules.apply(labels = labels, ctx = ctx, photoCount = 1)

        assertTrue(
            "Title or content should still mention the team",
            result.title.contains("Đội Hỗ Trợ") || result.content.contains("Đội Hỗ Trợ")
        )
        assertTrue(
            "Should still emit base hashtags even with no usable labels",
            result.hashtags.containsAll(listOf("#UIT", "#UITVolunteer", "#TinhNguyenUIT"))
        )
        assertEquals(1, result.perPhotoCaptions.size)
        assertTrue(result.perPhotoCaptions.first().isNotBlank())
    }

    @Test
    fun `apply caps hashtags at MAX_HASHTAGS`() {
        val ctx = UitContext(
            campaignName = "Tiếp Sức Mùa Thi 2026",
            teamName = "Đội Tình Nguyện"
        )
        val labels = listOf(
            LabelInput(text = "Person", confidence = 0.9f),
            LabelInput(text = "Book", confidence = 0.9f),
            LabelInput(text = "Paper", confidence = 0.9f),
            LabelInput(text = "School", confidence = 0.9f),
            LabelInput(text = "Smile", confidence = 0.9f)
        )

        val result = rules.apply(labels = labels, ctx = ctx, photoCount = 2)

        assertTrue(
            "Total hashtags should not exceed MAX_HASHTAGS",
            result.hashtags.size <= UitPostStyleRules.MAX_HASHTAGS
        )
        assertTrue(
            "Tiep Suc Mua Thi hashtag should be present",
            result.hashtags.contains("#TiepSucMuaThi")
        )
    }

    @Test
    fun `apply with empty labels still produces non-blank caption`() {
        val ctx = UitContext.Default

        val result = rules.apply(labels = emptyList(), ctx = ctx, photoCount = 2)

        assertTrue(result.title.isNotBlank())
        assertTrue(result.content.isNotBlank())
        assertEquals(2, result.perPhotoCaptions.size)
        assertTrue(result.hashtags.isNotEmpty())
    }

    @Test
    fun `apply with different nonces yields different titles`() {
        val ctx = UitContext(
            campaignName = "Mùa Hè Xanh 2026",
            teamName = "Đội A"
        )
        val labels = listOf(
            LabelInput(text = "Person", confidence = 0.9f),
            LabelInput(text = "Book", confidence = 0.85f)
        )

        val titles = (0..7).map {
            rules.apply(labels = labels, ctx = ctx, photoCount = 3, nonce = it).title
        }.toSet()

        assertTrue(
            "Regenerating with different nonces should produce >1 distinct titles",
            titles.size > 1
        )
        assertNotEquals(1, titles.size)
    }
}
