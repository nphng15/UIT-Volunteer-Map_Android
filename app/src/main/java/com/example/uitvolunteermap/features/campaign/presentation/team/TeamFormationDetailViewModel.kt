package com.example.uitvolunteermap.features.campaign.presentation.team

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetTeamFormationDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TeamFormationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTeamFormationDetailUseCase: GetTeamFormationDetailUseCase
) : ViewModel() {

    private val teamId: Int = checkNotNull(
        savedStateHandle[AppDestination.TeamFormationDetail.teamIdArg]
    )

    private val _uiState = MutableStateFlow(TeamFormationDetailUiState())
    val uiState: StateFlow<TeamFormationDetailUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<TeamFormationDetailUiEffect>()
    val uiEffect: SharedFlow<TeamFormationDetailUiEffect> = _uiEffect.asSharedFlow()

    init {
        onEvent(TeamFormationDetailUiEvent.RefreshRequested)
    }

    fun onEvent(event: TeamFormationDetailUiEvent) {
        when (event) {
            TeamFormationDetailUiEvent.RefreshRequested -> loadTeamDetail()
            TeamFormationDetailUiEvent.BackClicked -> emitEffect(TeamFormationDetailUiEffect.NavigateBack)
            TeamFormationDetailUiEvent.HeroEditClicked -> showMessage("Chuc nang sua anh se duoc noi voi API sau.")
            TeamFormationDetailUiEvent.AddActivityClicked -> {
                emitEffect(TeamFormationDetailUiEffect.NavigateToAddPostPopup(teamId))
            }
            is TeamFormationDetailUiEvent.LeaderClicked -> showMessage("Thong tin chi huy ${event.leaderId} se duoc bo sung sau.")
            is TeamFormationDetailUiEvent.ActivityClicked -> showMessage("Chi tiet hoat dong ${event.activityId} se duoc noi sau.")
        }
    }

    private fun loadTeamDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getTeamFormationDetailUseCase(teamId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        TeamFormationDetailUiState(
                            appName = result.data.appName,
                            appSubtitle = result.data.appSubtitle,
                            title = result.data.title,
                            description = result.data.description,
                            heroCards = result.data.heroCards.map { card ->
                                TeamHeroCardUiModel(
                                    label = card.label,
                                    isPrimary = card.isPrimary
                                )
                            },
                            leaders = result.data.leaders.map { leader ->
                                TeamLeaderUiModel(
                                    id = leader.id,
                                    initials = leader.initials,
                                    role = leader.role,
                                    name = leader.name
                                )
                            },
                            activities = result.data.activities.map { activity ->
                                TeamActivityUiModel(
                                    id = activity.id,
                                    label = activity.label,
                                    isAddButton = activity.isAddButton
                                )
                            },
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.error.userMessage
                        )
                    }
                }
            }
        }
    }

    private fun showMessage(message: String) {
        emitEffect(TeamFormationDetailUiEffect.ShowMessage(message))
    }

    private fun emitEffect(effect: TeamFormationDetailUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }
}
