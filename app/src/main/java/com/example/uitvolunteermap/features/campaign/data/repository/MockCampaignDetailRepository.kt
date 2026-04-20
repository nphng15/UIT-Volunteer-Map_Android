package com.example.uitvolunteermap.features.campaign.data.repository

import com.example.uitvolunteermap.R
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
                AppError.NotFound(message = "Không tìm thấy chiến dịch phù hợp.")
            )
        }

        val campaignTitle = if (campaignId == 1) "Mùa Hè Xanh 2026" else "Xuân Tình Nguyện 2026"
        val schedule = if (campaignId == 1) {
            "01/06/2026 - 30/08/2026 · 9 địa điểm hoạt động"
        } else {
            "05/01/2026 - 25/01/2026 · 6 địa điểm chăm lo xuân"
        }

        val description = if (campaignId == 1) {
            "Mùa Hè Xanh 2026 triển khai các hoạt động an sinh, giáo dục, môi trường và hỗ trợ cộng đồng tại Linh Trung, Củ Chi và các xã vùng ven. Chiến dịch kết nối nhiều đội hình chuyên trách để vừa bám địa bàn, vừa cập nhật bài viết và tiến độ theo từng cụm.\n\nNội dung được triển khai xuyên suốt mùa hè, gắn chặt giữa đội hình, địa điểm hoạt động và nhật ký hiện trường."
        } else {
            "Xuân Tình Nguyện 2026 tập trung vào các hoạt động chăm lo Tết, thăm hỏi mái ấm, tổ chức sân chơi thiếu nhi và kết nối nguồn lực tại các địa bàn khó khăn. Các đội hình được phân theo nhóm an sinh, truyền thông và hậu cần để cập nhật nhanh tình hình từng điểm.\n\nMục tiêu là giữ dòng thông tin xuyên suốt giữa đội hình, bài viết hiện trường và bản đồ hoạt động theo từng ngày cao điểm."
        }

        return AppResult.Success(
            CampaignDetail(
                id = campaignId,
                appName = "UIT · Tình nguyện",
                title = campaignTitle,
                schedule = schedule,
                heroHeadline = if (campaignId == 1) {
                    "Một mùa hè của đội hình, địa điểm và bài viết hiện trường"
                } else {
                    "Một mùa xuân của sẻ chia, mái ấm và nhật ký tình nguyện"
                },
                heroSupportingText = if (campaignId == 1) {
                    "Theo dõi 12 đội hình cùng hoạt động an sinh, giáo dục và môi trường trong suốt chiến dịch."
                } else {
                    "Tổng hợp các điểm chăm lo xuân, đội hình thực địa và lượng bài viết cập nhật mỗi ngày."
                },
                stats = listOf(
                    CampaignDetailStat(value = if (campaignId == 1) "12" else "08", label = "Đội hình"),
                    CampaignDetailStat(value = if (campaignId == 1) "148" else "96", label = "Tình nguyện viên"),
                    CampaignDetailStat(value = if (campaignId == 1) "34" else "21", label = "Bài viết"),
                    CampaignDetailStat(value = if (campaignId == 1) "09" else "06", label = "Địa điểm")
                ),
                description = description,
                teamSectionTitle = "Đội hình",
                teams = listOf(
                    CampaignDetailTeam(
                        id = 101,
                        name = if (campaignId == 1) "Đội nấu cơm" else "Đội an sinh",
                        shortName = if (campaignId == 1) "NC" else "AS",
                        accentColors = listOf(0xFF20303A, 0xFF6D839A),
                        previewImageResId = if (campaignId == 1) R.drawable.muahexanh1 else R.drawable.xuantinhnguyen
                    ),
                    CampaignDetailTeam(
                        id = 102,
                        name = if (campaignId == 1) "Đội giáo dục" else "Đội hậu cần",
                        shortName = if (campaignId == 1) "GD" else "HC",
                        accentColors = listOf(0xFFF7F1D8, 0xFFFFF4CC),
                        previewImageResId = if (campaignId == 1) R.drawable.muahexanh2 else R.drawable.xuantinhnguyen2
                    ),
                    CampaignDetailTeam(
                        id = 103,
                        name = if (campaignId == 1) "Đội truyền thông" else "Đội truyền thông",
                        shortName = if (campaignId == 1) "TT" else "TT",
                        accentColors = listOf(0xFFF1F3F7, 0xFFFFFFFF),
                        previewImageResId = if (campaignId == 1) R.drawable.muahexanh1 else R.drawable.xuantinhnguyen
                    )
                ),
                posts = listOf(
                    CampaignDetailPost(
                        id = 201,
                        teamName = if (campaignId == 1) "Đội nấu cơm" else "Đội an sinh",
                        title = if (campaignId == 1) {
                            "Ngày đầu ở bếp Linh Trung"
                        } else {
                            "Chuyến thăm mái ấm đầu xuân"
                        },
                        publishedAt = if (campaignId == 1) "18.04 · 3 giờ trước" else "12.01 · hôm qua",
                        summary = if (campaignId == 1) {
                            "4:30 sáng, bếp bật đèn. 28 người chia ra hai bên, một bên vo gạo, một bên rửa rau và chuẩn bị bữa sáng cho địa bàn."
                        } else {
                            "Đội an sinh ghé mái ấm từ sớm, chuẩn bị quà Tết và ghi lại những khoảnh khắc đầu tiên của chiến dịch."
                        },
                        accentColors = listOf(0xFF20303A, 0xFF1C3977),
                        isLightBadge = true
                    ),
                    CampaignDetailPost(
                        id = 202,
                        teamName = if (campaignId == 1) "Đội giáo dục" else "Đội hậu cần",
                        title = if (campaignId == 1) {
                            "Lớp toán mùa hè, buổi thứ hai"
                        } else {
                            "Kho quà xuân đã sẵn sàng cho ngày cao điểm"
                        },
                        publishedAt = if (campaignId == 1) "17.04 · hôm qua" else "10.01 · 2 ngày trước",
                        summary = if (campaignId == 1) {
                            "Hôm nay có 12 em, có em lớp 4 và em lớp 9. Bài rất vui, cả đội cùng rà lại giáo án cho buổi sau."
                        } else {
                            "Đội hậu cần kiểm tra lần cuối danh sách nhu yếu phẩm, banner, quà tặng và tuyến đường di chuyển sáng mai."
                        },
                        accentColors = listOf(0xFFFFF4CC, 0xFFF7F1D8),
                        isLightBadge = false
                    )
                ),
                mapOverview = CampaignMapOverview(
                    selectedArea = if (campaignId == 1) "Khu Đông" else "Thủ Đức",
                    headerTitle = "Bản đồ địa điểm",
                    footerTitle = if (campaignId == 1) "Khu Đông - 3 điểm" else "Thủ Đức - 3 điểm",
                    footerDescription = "Chạm vào marker để mở Maps và xem bài viết liên quan",
                    ctaLabel = "Mở Google Maps",
                    locations = listOf(
                        CampaignMapLocation(
                            id = 1,
                            label = "Linh Trung",
                            supportingText = "Đội giáo dục phụ trách · 12 bài viết mới",
                            xFraction = 0.22f,
                            yFraction = 0.42f,
                            isHighlighted = false
                        ),
                        CampaignMapLocation(
                            id = 2,
                            label = "KTX Khu B",
                            supportingText = "3 điểm hỗ trợ · Mới nhất",
                            xFraction = 0.50f,
                            yFraction = 0.44f,
                            isHighlighted = true
                        ),
                        CampaignMapLocation(
                            id = 3,
                            label = "Sóng Xanh",
                            supportingText = "Cụm hậu cần · 2 bài viết mới",
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
