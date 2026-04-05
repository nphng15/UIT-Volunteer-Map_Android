package com.example.uitvolunteermap.features.auth.domain.usecase

import com.example.uitvolunteermap.features.auth.domain.repository.AuthRepository
import javax.inject.Inject
import android.util.Log

class CheckInitialAuthStatusUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AuthStatus {

        Log.d("CheckInitialAuthStatusUseCase", "UseCase: Bắt đầu kiểm tra Splash Boot Flow")

        // 1. Check Health trước
        if (!repository.isServerHealthy()) {
            Log.d("CheckInitialAuthStatusUseCase", "Kết quả: SERVER_ERROR (Server sập hoặc giả lập false)")
            return AuthStatus.SERVER_ERROR
        }

        // 2. Check Verify sau
        return if (repository.isTokenValid()) {
            Log.d("CheckInitialAuthStatusUseCase", "Kết quả: AUTHENTICATED (Vào thẳng Home)")
            AuthStatus.AUTHENTICATED // Auth entry
        } else {
            Log.d("CheckInitialAuthStatusUseCase", "Kết quả: GUEST (Vào màn hình Login)")
            AuthStatus.GUEST         // Guest entry
        }
    }
}

enum class AuthStatus { AUTHENTICATED, GUEST, SERVER_ERROR }