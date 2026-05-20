package com.example.uitvolunteermap.features.post.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.post.domain.entity.PostUiModel
import com.example.uitvolunteermap.features.post.domain.usecase.GetPostsUseCase
import com.example.uitvolunteermap.features.post.presentation.campaignposts.CampaignPostCardUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        onEvent(FeedUiEvent.RefreshRequested)
    }

    fun onEvent(event: FeedUiEvent) {
        when (event) {
            FeedUiEvent.RefreshRequested -> loadPosts()
            is FeedUiEvent.PostCardClicked -> {
                _uiState.update { current ->
                    current.copy(
                        expandedPostId = if (current.expandedPostId == event.postId) {
                            null
                        } else {
                            event.postId
                        }
                    )
                }
            }
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getPostsUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            posts = result.data.map(PostUiModel::toCardUiModel),
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
}

private fun PostUiModel.toCardUiModel(): CampaignPostCardUiModel = CampaignPostCardUiModel(
    id = id,
    teamId = teamId,
    teamName = teamName,
    title = title,
    excerpt = excerpt,
    content = content,
    authorName = authorName,
    publishedAt = publishedAt,
    updatedAt = updatedAt,
    thumbnailUrl = thumbnailUrl,
    accentColors = emptyList(),
    attachmentLabels = attachmentLabels
)
