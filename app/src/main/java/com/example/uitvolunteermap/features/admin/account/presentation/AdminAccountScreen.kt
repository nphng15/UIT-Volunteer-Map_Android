package com.example.uitvolunteermap.features.admin.account.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.AdminBottomBar
import com.example.uitvolunteermap.core.ui.AdminBottomBarTab
import com.example.uitvolunteermap.core.ui.AdminTopBar
import com.example.uitvolunteermap.features.admin.account.presentation.components.AccountEmptyState
import com.example.uitvolunteermap.features.admin.account.presentation.components.AccountErrorState
import com.example.uitvolunteermap.features.admin.account.presentation.components.AccountListItem
import com.example.uitvolunteermap.features.admin.account.presentation.components.AccountSearchBar
import com.example.uitvolunteermap.features.admin.account.presentation.components.AccountTokens
import com.example.uitvolunteermap.features.admin.account.presentation.components.ConfirmDeleteAccountDialog
import com.example.uitvolunteermap.features.admin.account.presentation.components.CreateAccountCard
import com.example.uitvolunteermap.features.admin.account.presentation.components.EditAccountCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAccountScreen(
    state: AdminAccountUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AdminAccountUiEvent) -> Unit,
    onTabSelected: (AdminBottomBarTab) -> Unit,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullToRefreshState()
    val createSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            onEvent(AdminAccountUiEvent.PullToRefresh)
        }
    }
    LaunchedEffect(state.isRefreshing) {
        if (!state.isRefreshing) {
            pullRefreshState.endRefresh()
        }
    }

    if (state.pendingDeleteId != null) {
        ConfirmDeleteAccountDialog(
            username = state.pendingDeleteUsername,
            onConfirm = { onEvent(AdminAccountUiEvent.DeleteConfirmed) },
            onDismiss = { onEvent(AdminAccountUiEvent.DeleteCancelled) }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent,
        bottomBar = {
            AdminBottomBar(
                currentTab = AdminBottomBarTab.Accounts,
                onTabSelected = onTabSelected
            )
        },
        topBar = {
            AdminTopBar(onLogout = onLogout)
        },
        floatingActionButton = {
            if (state.canManageAccounts) {
                FloatingActionButton(
                    onClick = { onEvent(AdminAccountUiEvent.CreateClicked) },
                    containerColor = AccountTokens.Accent,
                    contentColor = AccountTokens.Surface,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm tài khoản"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AccountTokens.BackgroundTop,
                            AccountTokens.BackgroundBottom
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            when {
                state.isLoading && state.accounts.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null && state.accounts.isEmpty() -> {
                    AccountErrorState(
                        message = state.errorMessage,
                        onRetry = { onEvent(AdminAccountUiEvent.Refresh) }
                    )
                }

                else -> {
                    val filtered = state.filteredAccounts
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(pullRefreshState.nestedScrollConnection)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                AccountSearchBar(
                                    query = state.searchQuery,
                                    onQueryChange = {
                                        onEvent(AdminAccountUiEvent.SearchQueryChanged(it))
                                    },
                                    roleFilter = state.roleFilter,
                                    onRoleFilterChange = {
                                        onEvent(AdminAccountUiEvent.RoleFilterChanged(it))
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                            item {
                                Text(
                                    text = "Danh sách (${filtered.size})",
                                    color = AccountTokens.MutedText,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }

                            if (filtered.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp)
                                    ) {
                                        AccountEmptyState(
                                            message = "Thử thay đổi từ khóa tìm kiếm hoặc bộ lọc.",
                                            onRefresh = { onEvent(AdminAccountUiEvent.Refresh) }
                                        )
                                    }
                                }
                            } else {
                                items(filtered, key = { it.accId }) { account ->
                                    AccountListItem(
                                        account = account,
                                        showActions = state.canManageAccounts,
                                        onEditClick = {
                                            onEvent(AdminAccountUiEvent.EditClicked(account.accId))
                                        },
                                        onDeleteClick = {
                                            onEvent(AdminAccountUiEvent.DeleteClicked(account.accId))
                                        }
                                    )
                                }
                            }
                            item { Spacer(modifier = Modifier.height(8.dp)) }
                        }

                        PullToRefreshContainer(
                            modifier = Modifier.align(Alignment.TopCenter),
                            state = pullRefreshState
                        )
                    }
                }
            }
        }
    }

    // ─── Create sheet ────────────────────────────────────────────────────────
    state.createForm?.let { form ->
        ModalBottomSheet(
            onDismissRequest = { onEvent(AdminAccountUiEvent.CreateDismissed) },
            sheetState = createSheetState,
            dragHandle = null,
            containerColor = Color.Transparent,
            scrimColor = AccountTokens.PrimaryText.copy(alpha = 0.4f)
        ) {
            CreateAccountCard(
                form = form,
                onClose = { onEvent(AdminAccountUiEvent.CreateDismissed) },
                onFullnameChange = { onEvent(AdminAccountUiEvent.CreateFullnameChanged(it)) },
                onMssvChange = { onEvent(AdminAccountUiEvent.CreateMssvChanged(it)) },
                onClassChange = { onEvent(AdminAccountUiEvent.CreateClassChanged(it)) },
                onEmailChange = { onEvent(AdminAccountUiEvent.CreateEmailChanged(it)) },
                onPhoneChange = { onEvent(AdminAccountUiEvent.CreatePhoneChanged(it)) },
                onUsernameChange = { onEvent(AdminAccountUiEvent.CreateUsernameChanged(it)) },
                onPasswordChange = { onEvent(AdminAccountUiEvent.CreatePasswordChanged(it)) },
                onRoleChange = { onEvent(AdminAccountUiEvent.CreateRoleChanged(it)) },
                onSubmit = { onEvent(AdminAccountUiEvent.CreateSubmitted) }
            )
        }
    }

    // ─── Edit sheet ──────────────────────────────────────────────────────────
    state.editForm?.let { form ->
        ModalBottomSheet(
            onDismissRequest = { onEvent(AdminAccountUiEvent.EditDismissed) },
            sheetState = editSheetState,
            dragHandle = null,
            containerColor = Color.Transparent,
            scrimColor = AccountTokens.PrimaryText.copy(alpha = 0.4f)
        ) {
            EditAccountCard(
                form = form,
                onClose = { onEvent(AdminAccountUiEvent.EditDismissed) },
                onPasswordChange = { onEvent(AdminAccountUiEvent.EditPasswordChanged(it)) },
                onRoleChange = { onEvent(AdminAccountUiEvent.EditRoleChanged(it)) },
                onSubmit = { onEvent(AdminAccountUiEvent.EditSubmitted) }
            )
        }
    }
}
