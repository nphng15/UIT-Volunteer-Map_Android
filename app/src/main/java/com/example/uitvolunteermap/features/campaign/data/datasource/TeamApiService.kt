package com.example.uitvolunteermap.features.campaign.data.datasource

// TODO: Uncomment when Retrofit is wired up in DI and the backend is reachable.

/*
import com.example.uitvolunteermap.features.campaign.data.model.CampaignApiResponse
import com.example.uitvolunteermap.features.campaign.data.model.TeamDto
import com.example.uitvolunteermap.features.campaign.data.model.TeamListItemDto
import retrofit2.http.GET
import retrofit2.http.Path

interface TeamApiService {

    // GET /teams — Public, không cần token.
    // Trả về danh sách rút gọn (không có member details).
    @GET("teams")
    suspend fun getTeams(): CampaignApiResponse<List<TeamListItemDto>>

    // GET /teams/:id — Public, không cần token.
    // Trả về chi tiết một team kèm leaders (có avatarUrl).
    @GET("teams/{id}")
    suspend fun getTeam(
        @Path("id") teamId: Int
    ): CampaignApiResponse<TeamDto>
}
*/
