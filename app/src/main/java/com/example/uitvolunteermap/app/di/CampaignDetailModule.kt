package com.example.uitvolunteermap.features.campaign.di

import com.example.uitvolunteermap.features.campaign.data.repository.MockAddPostRepository
import com.example.uitvolunteermap.features.campaign.data.repository.MockCampaignDetailRepository
import com.example.uitvolunteermap.features.campaign.data.repository.MockTeamFormationDetailRepository
import com.example.uitvolunteermap.features.campaign.domain.repository.AddPostRepository
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignDetailRepository
import com.example.uitvolunteermap.features.campaign.domain.repository.TeamFormationDetailRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CampaignDetailModule {

    @Binds
    @Singleton
    abstract fun bindAddPostRepository(
        repository: MockAddPostRepository
    ): AddPostRepository

    @Binds
    @Singleton
    abstract fun bindCampaignDetailRepository(
        repository: MockCampaignDetailRepository
    ): CampaignDetailRepository

    @Binds
    @Singleton
    abstract fun bindTeamFormationDetailRepository(
        repository: MockTeamFormationDetailRepository
    ): TeamFormationDetailRepository
}
