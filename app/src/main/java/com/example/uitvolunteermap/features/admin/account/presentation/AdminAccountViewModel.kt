package com.example.uitvolunteermap.features.admin.account.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.admin.account.domain.entity.Account
import com.example.uitvolunteermap.features.admin.account.domain.usecase.CreateAccountUseCase
import com.example.uitvolunteermap.features.admin.account.domain.usecase.DeleteAccountUseCase
import com.example.uitvolunteermap.features.admin.account.domain.usecase.GetAccountsUseCase
import com.example.uitvolunteermap.features.admin.account.domain.usecase.UpdateAccountUseCase
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
class AdminAccountViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AdminAccountUiState(
            canManageAccounts = sessionManager.userRole.value == UserRole.ADMIN
        )
    )
    val uiState: StateFlow<AdminAccountUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AdminAccountUiEffect>()
    val uiEffect: SharedFlow<AdminAccountUiEffect> = _uiEffect.asSharedFlow()

    init {
        onEvent(AdminAccountUiEvent.Refresh)
    }

    fun onEvent(event: AdminAccountUiEvent) {
        when (event) {
            AdminAccountUiEvent.Refresh -> loadAccounts(isPullRefresh = false)
            AdminAccountUiEvent.PullToRefresh -> loadAccounts(isPullRefresh = true)

            is AdminAccountUiEvent.SearchQueryChanged ->
                _uiState.update { it.copy(searchQuery = event.query) }
            is AdminAccountUiEvent.RoleFilterChanged ->
                _uiState.update { it.copy(roleFilter = event.role) }

            // ─── Create ──────────────────────────────────────────────────────
            AdminAccountUiEvent.CreateClicked ->
                _uiState.update { it.copy(createForm = CreateAccountForm()) }
            AdminAccountUiEvent.CreateDismissed ->
                _uiState.update { it.copy(createForm = null) }
            is AdminAccountUiEvent.CreateFullnameChanged ->
                updateCreateForm { it.copy(fullname = event.value, errorMessage = null) }
            is AdminAccountUiEvent.CreateMssvChanged ->
                updateCreateForm { it.copy(mssv = event.value, errorMessage = null) }
            is AdminAccountUiEvent.CreateClassChanged ->
                updateCreateForm { it.copy(className = event.value, errorMessage = null) }
            is AdminAccountUiEvent.CreateEmailChanged ->
                updateCreateForm { it.copy(email = event.value, errorMessage = null) }
            is AdminAccountUiEvent.CreatePhoneChanged ->
                updateCreateForm { it.copy(phoneNumber = event.value, errorMessage = null) }
            is AdminAccountUiEvent.CreateUsernameChanged ->
                updateCreateForm { it.copy(username = event.value, errorMessage = null) }
            is AdminAccountUiEvent.CreatePasswordChanged ->
                updateCreateForm { it.copy(password = event.value, errorMessage = null) }
            is AdminAccountUiEvent.CreateRoleChanged ->
                updateCreateForm { it.copy(role = event.role) }
            AdminAccountUiEvent.CreateSubmitted -> submitCreate()

            // ─── Edit ────────────────────────────────────────────────────────
            is AdminAccountUiEvent.EditClicked -> openEdit(event.accId)
            AdminAccountUiEvent.EditDismissed ->
                _uiState.update { it.copy(editForm = null) }
            is AdminAccountUiEvent.EditPasswordChanged ->
                updateEditForm { it.copy(password = event.value, errorMessage = null) }
            is AdminAccountUiEvent.EditRoleChanged ->
                updateEditForm { it.copy(role = event.role, errorMessage = null) }
            AdminAccountUiEvent.EditSubmitted -> submitEdit()

            // ─── Delete ──────────────────────────────────────────────────────
            is AdminAccountUiEvent.DeleteClicked -> {
                if (!_uiState.value.isDeleting) {
                    _uiState.update { it.copy(pendingDeleteId = event.accId) }
                }
            }
            AdminAccountUiEvent.DeleteConfirmed -> submitDelete()
            AdminAccountUiEvent.DeleteCancelled ->
                _uiState.update { it.copy(pendingDeleteId = null) }
        }
    }

    // ─── Load ──────────────────────────────────────────────────────────────────

    private fun loadAccounts(isPullRefresh: Boolean) {
        viewModelScope.launch {
            if (isPullRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            when (val result = getAccountsUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            accounts = result.data.map(::toListItem),
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.error.userMessage
                        )
                    }
                }
            }
        }
    }

    // ─── Create ──────────────────────────────────────────────────────────────────

    private fun submitCreate() {
        val form = _uiState.value.createForm ?: return
        if (form.isSubmitting) return
        if (!_uiState.value.canManageAccounts) return

        if (form.role !in listOf(AccountRole.VOLUNTEER, AccountRole.LEADER)) {
            updateCreateForm { it.copy(errorMessage = "Chỉ được tạo tài khoản Volunteer hoặc Leader.") }
            return
        }

        updateCreateForm { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            val result = createAccountUseCase(
                fullname = form.fullname,
                mssv = form.mssv,
                className = form.className,
                email = form.email,
                phoneNumber = form.phoneNumber,
                username = form.username,
                password = form.password,
                role = form.role.apiValue
            )
            when (result) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(createForm = null) }
                    _uiEffect.emit(AdminAccountUiEffect.ShowMessage("Tạo tài khoản thành công."))
                    loadAccounts(isPullRefresh = false)
                }

                is AppResult.Error ->
                    updateCreateForm {
                        it.copy(isSubmitting = false, errorMessage = result.error.userMessage)
                    }
            }
        }
    }

    // ─── Edit ──────────────────────────────────────────────────────────────────

    private fun openEdit(accId: Int) {
        val account = _uiState.value.accounts.firstOrNull { it.accId == accId } ?: return
        _uiState.update {
            it.copy(
                editForm = EditAccountForm(
                    accId = account.accId,
                    username = account.username,
                    role = account.role ?: AccountRole.VOLUNTEER
                )
            )
        }
    }

    private fun submitEdit() {
        val form = _uiState.value.editForm ?: return
        if (form.isSubmitting) return
        if (!_uiState.value.canManageAccounts) return

        updateEditForm { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            // Chỉ gửi password nếu admin nhập; role luôn gửi giá trị đang chọn.
            val result = updateAccountUseCase(
                accountId = form.accId,
                password = form.password.takeIf { it.isNotBlank() },
                roleName = form.role.apiValue
            )
            when (result) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(editForm = null) }
                    _uiEffect.emit(AdminAccountUiEffect.ShowMessage("Cập nhật tài khoản thành công."))
                    loadAccounts(isPullRefresh = false)
                }

                is AppResult.Error ->
                    updateEditForm {
                        it.copy(isSubmitting = false, errorMessage = result.error.userMessage)
                    }
            }
        }
    }

    // ─── Delete ──────────────────────────────────────────────────────────────────

    private fun submitDelete() {
        if (!_uiState.value.canManageAccounts) return
        if (_uiState.value.isDeleting) return
        val accId = _uiState.value.pendingDeleteId ?: return

        _uiState.update { it.copy(pendingDeleteId = null, isDeleting = true) }

        viewModelScope.launch {
            when (val result = deleteAccountUseCase(accId)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isDeleting = false) }
                    _uiEffect.emit(AdminAccountUiEffect.ShowMessage("Xóa tài khoản thành công."))
                    loadAccounts(isPullRefresh = false)
                }

                is AppResult.Error -> {
                    _uiState.update { it.copy(isDeleting = false) }
                    _uiEffect.emit(AdminAccountUiEffect.ShowMessage(result.error.userMessage))
                }
            }
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────────

    private fun toListItem(account: Account): AccountListItemUiModel {
        val role = AccountRole.fromApi(account.roleName)
        return AccountListItemUiModel(
            accId = account.accId,
            username = account.username,
            roleName = account.roleName,
            role = role,
            joinedAt = account.createdAt?.take(10) ?: "—"
        )
    }

    private inline fun updateCreateForm(transform: (CreateAccountForm) -> CreateAccountForm) {
        _uiState.update { state ->
            state.createForm?.let { state.copy(createForm = transform(it)) } ?: state
        }
    }

    private inline fun updateEditForm(transform: (EditAccountForm) -> EditAccountForm) {
        _uiState.update { state ->
            state.editForm?.let { state.copy(editForm = transform(it)) } ?: state
        }
    }
}
