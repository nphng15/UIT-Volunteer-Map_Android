package com.example.uitvolunteermap.testing

import com.example.uitvolunteermap.app.di.AuthDataModule
import com.example.uitvolunteermap.app.di.PostModule
import com.example.uitvolunteermap.features.auth.domain.repository.AuthRepository
import com.example.uitvolunteermap.features.campaign.di.CampaignDetailModule
import com.example.uitvolunteermap.features.campaign.domain.repository.AddPostRepository
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignDetailRepository
import com.example.uitvolunteermap.features.campaign.domain.repository.TeamFormationDetailRepository
import com.example.uitvolunteermap.features.home.di.VolunteerHomeModule
import com.example.uitvolunteermap.features.home.domain.repository.VolunteerHomeRepository
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AuthDataModule::class],
)
abstract class VolunteerFlowTestAuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: TestAuthRepository): AuthRepository
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [VolunteerHomeModule::class],
)
abstract class VolunteerFlowTestHomeModule {

    @Binds
    @Singleton
    abstract fun bindVolunteerHomeRepository(
        repository: TestVolunteerHomeRepository,
    ): VolunteerHomeRepository
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CampaignDetailModule::class],
)
abstract class VolunteerFlowTestCampaignModule {

    @Binds
    @Singleton
    abstract fun bindCampaignDetailRepository(
        repository: TestCampaignDetailRepository,
    ): CampaignDetailRepository

    @Binds
    @Singleton
    abstract fun bindTeamFormationDetailRepository(
        repository: TestTeamFormationDetailRepository,
    ): TeamFormationDetailRepository

    @Binds
    @Singleton
    abstract fun bindAddPostRepository(
        repository: TestAddPostRepository,
    ): AddPostRepository
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PostModule::class],
)
abstract class VolunteerFlowTestPostModule {

    @Binds
    @Singleton
    abstract fun bindPostRepository(repository: TestPostRepository): PostRepository
}
