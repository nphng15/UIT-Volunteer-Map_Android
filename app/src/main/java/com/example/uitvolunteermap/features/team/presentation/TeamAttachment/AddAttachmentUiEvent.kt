package com.example.uitvolunteermap.features.team.presentation.addattachment

import android.net.Uri

sealed class AddAttachmentUiEvent {
    data class OnPhotosPicked(val uris: List<Uri>) : AddAttachmentUiEvent()
    data class OnDeletePhoto(val uri: Uri) : AddAttachmentUiEvent()
    object OnSave : AddAttachmentUiEvent()
    object OnDismiss : AddAttachmentUiEvent()
}