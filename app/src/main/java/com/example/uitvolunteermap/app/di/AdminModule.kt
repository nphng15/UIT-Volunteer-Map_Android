package com.example.uitvolunteermap.app.di

import com.example.uitvolunteermap.features.admin.account.data.repository.RemoteAccountRepository
import com.example.uitvolunteermap.features.admin.account.domain.repository.AccountRepository
import com.example.uitvolunteermap.features.admin.campaign.data.repository.RemoteAdminCampaignRepository
import com.example.uitvolunteermap.features.admin.campaign.domain.repository.AdminCampaignRepository
import com.example.uitvolunteermap.features.admin.dashboard.data.repository.RemoteAdminDashboardRepository
import com.example.uitvolunteermap.features.admin.dashboard.domain.repository.AdminDashboardRepository
import com.example.uitvolunteermap.features.admin.post.data.repository.RemoteAdminPostRepository
import com.example.uitvolunteermap.features.admin.post.domain.repository.AdminPostRepository
import com.example.uitvolunteermap.features.admin.team.data.repository.RemoteAdminTeamRepository
import com.example.uitvolunteermap.features.admin.team.domain.repository.AdminTeamRepository
import com.example.uitvolunteermap.features.profile.data.repository.RemoteUserProfileRepository
import com.example.uitvolunteermap.features.profile.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdminModule {

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        repository: RemoteAccountRepository
    ): AccountRepository

    @Binds
    @Singleton
    abstract fun bindAdminCampaignRepository(
        repository: RemoteAdminCampaignRepository
    ): AdminCampaignRepository

    @Binds
    @Singleton
    abstract fun bindAdminTeamRepository(
        repository: RemoteAdminTeamRepository
    ): AdminTeamRepository

    @Binds
    @Singleton
    abstract fun bindAdminPostRepository(
        repository: RemoteAdminPostRepository
    ): AdminPostRepository

    @Binds
    @Singleton
    abstract fun bindAdminDashboardRepository(
        repository: RemoteAdminDashboardRepository
    ): AdminDashboardRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        repository: RemoteUserProfileRepository
    ): UserProfileRepository
}
