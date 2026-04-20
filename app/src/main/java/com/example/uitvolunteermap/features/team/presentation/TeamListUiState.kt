import com.example.uitvolunteermap.features.team.domain.model.Team

data class TeamListUiState(
    val isLoading: Boolean = false,
    val teams: List<Team> = emptyList(),
    val errorMessage: String? = null
)