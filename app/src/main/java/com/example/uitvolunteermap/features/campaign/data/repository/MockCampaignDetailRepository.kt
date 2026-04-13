package com.example.uitvolunteermap.features.campaign.data.repository

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignDetail
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignDetailPost
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignDetailStat
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignDetailTeam
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignMapLocation
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignMapOverview
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignDetailRepository
import javax.inject.Inject

class MockCampaignDetailRepository @Inject constructor() : CampaignDetailRepository {

    override suspend fun getCampaignDetail(campaignId: Int): AppResult<CampaignDetail> {
        // TODO: Replace with real implementation once backend is reachable.
        //
        // Real flow sẽ là 2 lần gọi API:
        //   1. val campaign = campaignApiService.getCampaign(token, campaignId).data!!
        //      → GET /campaigns/:id — trả về { campaignId, campaignName, description, startDate, endDate }
        //   2. val teams = teamApiService.getTeams().data ?: emptyList()
        //      → GET /teams — public, trả về danh sách team rút gọn có leaders
        //
        // Sau đó map 2 response thành CampaignDetail (posts và mapOverview sẽ cần endpoint riêng):
        //   campaign.toCampaignDetailDomain(teams = teams.map { it.toCampaignDetailTeam() })
        //
        // Hiện tại backend chưa có endpoint tổng hợp "campaign detail" nên mock bên dưới
        // trả về data đầy đủ để UI có thể phát triển độc lập.
        if (campaignId != 1 && campaignId != 2) {
            return AppResult.Error(
                AppError.NotFound(message = "Khong tim thay chien dich phu hop.")
            )
        }

        val campaignTitle = if (campaignId == 1) "Mua He Xanh 2026" else "Tiep suc mua thi"
        val schedule = if (campaignId == 1) {
            "10/06/2026 - 28/07/2026 - 5 cum hoat dong"
        } else {
            "05/06/2026 - 25/06/2026 - 9 diem tiep suc"
        }

        val description = if (campaignId == 1) {
            "Mua He Xanh 2026 tap trung vao cac hoat dong giao duc, ho tro cong dong va phat trien ky nang tinh nguyen. Chien dich ket hop doi hinh sinh vien, doi truyen thong va cac diem den cong tac xa hoi de tao tac dong rong hon.\n\nNoi dung duoc trien khai theo tung cum dia phuong, ket noi nhanh voi bai viet, dia diem va doi phu trach."
        } else {
            "Tiep suc mua thi 2026 uu tien ho tro thi sinh, huong dan phu huynh, dieu phoi hau can va cap nhat thong tin nhanh tai tung diem thi. Doi hinh tinh nguyen vien duoc bo tri theo cum truong va lich truc cao diem.\n\nMuc tieu la giu thong tin thong suot giua doi hinh, bai viet cap nhat va cac khu vuc can bo sung nhan su."
        }

        return AppResult.Success(
            CampaignDetail(
                id = campaignId,
                appName = "UIT Volunteer Map",
                title = campaignTitle,
                schedule = schedule,
                heroHeadline = if (campaignId == 1) {
                    "Dong bo doi hinh, bai viet va dia diem trong mot nhin"
                } else {
                    "Cap nhat nhanh diem tiep suc, lich truc va bai viet hien truong"
                },
                heroSupportingText = if (campaignId == 1) {
                    "Theo doi cac doi hinh giao duc, hau can va truyen thong trong suot mua he."
                } else {
                    "Tong hop cac diem tiep nuoc, doi hinh truc va luong bai viet theo cum thi."
                },
                stats = listOf(
                    CampaignDetailStat(value = if (campaignId == 1) "5" else "6", label = "Doi"),
                    CampaignDetailStat(value = if (campaignId == 1) "48" else "22", label = "Bai viet"),
                    CampaignDetailStat(value = if (campaignId == 1) "5" else "9", label = "Dia diem"),
                    CampaignDetailStat(value = if (campaignId == 1) "320" else "180", label = "TNV")
                ),
                description = description,
                teamSectionTitle = "Doi hinh",
                teams = if (campaignId == 1) listOf(
                    CampaignDetailTeam(
                        id = 101,
                        name = "Doi nau com",
                        shortName = "NC",
                        accentColors = listOf(0xFF20303A, 0xFF6D839A)
                    ),
                    CampaignDetailTeam(
                        id = 102,
                        name = "Doi giao duc",
                        shortName = "GD",
                        accentColors = listOf(0xFF1C3977, 0xFF4A6FA5)
                    ),
                    CampaignDetailTeam(
                        id = 103,
                        name = "Doi truyen thong",
                        shortName = "TT",
                        accentColors = listOf(0xFF3D2B1F, 0xFF7A5C45)
                    ),
                    CampaignDetailTeam(
                        id = 104,
                        name = "Doi hau can",
                        shortName = "HC",
                        accentColors = listOf(0xFF1A3A2A, 0xFF3D7A5C)
                    ),
                    CampaignDetailTeam(
                        id = 105,
                        name = "Doi y te",
                        shortName = "YT",
                        accentColors = listOf(0xFF3A1A1A, 0xFF7A3D3D)
                    )
                ) else listOf(
                    CampaignDetailTeam(
                        id = 201,
                        name = "Doi tiep suc cong truong",
                        shortName = "TS",
                        accentColors = listOf(0xFF20303A, 0xFF6D839A)
                    ),
                    CampaignDetailTeam(
                        id = 202,
                        name = "Doi hau can",
                        shortName = "HC",
                        accentColors = listOf(0xFF1C3977, 0xFF4A6FA5)
                    ),
                    CampaignDetailTeam(
                        id = 203,
                        name = "Doi dieu phoi",
                        shortName = "DP",
                        accentColors = listOf(0xFF3D2B1F, 0xFF7A5C45)
                    ),
                    CampaignDetailTeam(
                        id = 204,
                        name = "Doi truyen thong",
                        shortName = "TT",
                        accentColors = listOf(0xFF1A3A2A, 0xFF3D7A5C)
                    ),
                    CampaignDetailTeam(
                        id = 205,
                        name = "Doi y te",
                        shortName = "YT",
                        accentColors = listOf(0xFF3A1A1A, 0xFF7A3D3D)
                    ),
                    CampaignDetailTeam(
                        id = 206,
                        name = "Doi an ninh",
                        shortName = "AN",
                        accentColors = listOf(0xFF2A1A3A, 0xFF5C3D7A)
                    )
                ),
                posts = listOf(
                    CampaignDetailPost(
                        id = 201,
                        teamName = if (campaignId == 1) "Doi Mua He Xanh" else "Doi tiep suc cong truong",
                        title = if (campaignId == 1) {
                            "Doi Mua He Xanh hoan thanh 3 diem sinh hoat he trong ngay dau ra quan"
                        } else {
                            "Doi tiep suc khoi dong diem huong dan va tiep nuoc tu sang som"
                        },
                        publishedAt = if (campaignId == 1) "Hom nay 08:30" else "Hom nay 07:10",
                        summary = if (campaignId == 1) {
                            "Cap nhat nhanh tu doi hinh giao duc ve buoi sinh hoat dau tien tai cum Thu Duc, kem lich hoat dong va anh tong hop."
                        } else {
                            "Thong tin moi nhat tu diem tiep suc ve so luong tinh nguyen vien truc, luong thi sinh va hinh anh hien truong."
                        },
                        accentColors = listOf(0xFF20303A, 0xFF1C3977),
                        isLightBadge = true
                    ),
                    CampaignDetailPost(
                        id = 202,
                        teamName = if (campaignId == 1) "Doi Tiep suc" else "Doi hau can",
                        title = if (campaignId == 1) {
                            "Nhom hau can mo them diem tiep nuoc va cap nhat lich truc cuoi tuan"
                        } else {
                            "Nhom hau can mo them diem tiep nuoc va bo sung lich truc cuoi tuan"
                        },
                        publishedAt = "29/03/2026 18:10",
                        summary = "Bai dang tong hop tu doi hau can voi thong tin diem tiep nuoc moi, so luong tinh nguyen vien truc va huong dan lien he nhanh.",
                        accentColors = listOf(0xFFFFF4CC, 0xFFF7F1D8),
                        isLightBadge = false
                    )
                ),
                mapOverview = CampaignMapOverview(
                    selectedArea = "Thu Duc",
                    headerTitle = "Khu vuc hoat dong",
                    footerTitle = "3 diem dang hoat dong gan nhau",
                    footerDescription = "Cham marker de mo Maps va xem bai viet",
                    ctaLabel = "Mo Google Maps",
                    locations = listOf(
                        CampaignMapLocation(
                            id = 1,
                            label = "Linh Trung",
                            supportingText = "Doi giao duc phu trach - 12 bai viet moi",
                            xFraction = 0.22f,
                            yFraction = 0.42f,
                            isHighlighted = false
                        ),
                        CampaignMapLocation(
                            id = 2,
                            label = "KTX Khu B",
                            supportingText = "3 diem ho tro - Moi nhat",
                            xFraction = 0.50f,
                            yFraction = 0.44f,
                            isHighlighted = true
                        ),
                        CampaignMapLocation(
                            id = 3,
                            label = "Song Xanh",
                            supportingText = "Cum hau can - 2 bai viet moi",
                            xFraction = 0.74f,
                            yFraction = 0.68f,
                            isHighlighted = false
                        )
                    )
                )
            )
        )
    }
}
