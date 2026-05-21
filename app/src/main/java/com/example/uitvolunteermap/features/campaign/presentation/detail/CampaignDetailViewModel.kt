package com.example.uitvolunteermap.features.campaign.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetCampaignDetailUseCase
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
class CampaignDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCampaignDetailUseCase: GetCampaignDetailUseCase
) : ViewModel() {

    private val campaignId: Int = checkNotNull(
        savedStateHandle[AppDestination.CampaignDetail.campaignIdArg]
    )

    private val _uiState = MutableStateFlow(CampaignDetailUiState())
    val uiState: StateFlow<CampaignDetailUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CampaignDetailUiEffect>()
    val uiEffect: SharedFlow<CampaignDetailUiEffect> = _uiEffect.asSharedFlow()

    init {
        onEvent(CampaignDetailUiEvent.RefreshRequested)
    }

    fun onEvent(event: CampaignDetailUiEvent) {
        when (event) {
            CampaignDetailUiEvent.RefreshRequested -> loadCampaignDetail()
            CampaignDetailUiEvent.BackClicked -> emitEffect(CampaignDetailUiEffect.NavigateBack)
            CampaignDetailUiEvent.ReadMoreClicked -> showMessage("Phần nội dung mở rộng sẽ được nối với API sau.")
            CampaignDetailUiEvent.ViewAllPostsClicked -> {
                emitEffect(CampaignDetailUiEffect.NavigateToCampaignPosts(campaignId))
            }
            CampaignDetailUiEvent.OpenGoogleMapsClicked -> showMessage("Liên kết Google Maps đang dùng dữ liệu mock ở giai đoạn này.")
            is CampaignDetailUiEvent.TeamClicked -> {
                emitEffect(CampaignDetailUiEffect.NavigateToTeamDetail(event.teamId))
            }
            is CampaignDetailUiEvent.PostClicked -> showMessage("Chi tiết bài viết ${event.postId} sẽ được nối sau.")
        }
    }

    private fun loadCampaignDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getCampaignDetailUseCase(campaignId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        CampaignDetailUiState(
                            appName = result.data.appName,
                            title = result.data.title,
                            schedule = result.data.schedule,
                            heroHeadline = result.data.heroHeadline,
                            heroSupportingText = result.data.heroSupportingText,
                            stats = result.data.stats.map { stat ->
                                CampaignDetailStatUiModel(
                                    value = stat.value,
                                    label = stat.label
                                )
                            },
                            description = result.data.description,
                            teamSectionTitle = result.data.teamSectionTitle,
                            teams = result.data.teams.map { team ->
                                CampaignDetailTeamUiModel(
                                    id = team.id,
                                    name = team.name,
                                    shortName = team.shortName,
                                    accentColors = team.accentColors,
                                    previewImageResId = team.previewImageResId
                                )
                            },
                            posts = result.data.posts.map { post ->
                                CampaignDetailPostUiModel(
                                    id = post.id,
                                    teamName = post.teamName,
                                    title = post.title,
                                    publishedAt = post.publishedAt,
                                    summary = post.summary,
                                    accentColors = post.accentColors,
                                    isLightBadge = post.isLightBadge
                                )
                            },
                            mapOverview = CampaignMapOverviewUiModel(
                                selectedArea = result.data.mapOverview.selectedArea,
                                headerTitle = result.data.mapOverview.headerTitle,
                                footerTitle = result.data.mapOverview.footerTitle,
                                footerDescription = result.data.mapOverview.footerDescription,
                                ctaLabel = result.data.mapOverview.ctaLabel,
                                locations = result.data.mapOverview.locations.map { location ->
                                    CampaignMapLocationUiModel(
                                        id = location.id,
                                        label = location.label,
                                        supportingText = location.supportingText,
                                        xFraction = location.xFraction,
                                        yFraction = location.yFraction,
                                        isHighlighted = location.isHighlighted
                                    )
                                }
                            ),
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
        emitEffect(CampaignDetailUiEffect.ShowMessage(message))
    }

    private fun emitEffect(effect: CampaignDetailUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }
}
