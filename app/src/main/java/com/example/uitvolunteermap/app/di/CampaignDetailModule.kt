package com.example.uitvolunteermap.app.di

import com.example.uitvolunteermap.features.campaign.data.repository.DefaultCampaignAreaRepository
import com.example.uitvolunteermap.features.campaign.data.repository.RemoteAddPostRepository
import com.example.uitvolunteermap.features.campaign.data.repository.RemoteCampaignDetailRepository
import com.example.uitvolunteermap.features.campaign.data.repository.RemoteTeamFormationDetailRepository
import com.example.uitvolunteermap.features.campaign.domain.repository.AddPostRepository
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignAreaRepository
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
        repository: RemoteAddPostRepository
    ): AddPostRepository

    @Binds
    @Singleton
    abstract fun bindCampaignDetailRepository(
        repository: RemoteCampaignDetailRepository
    ): CampaignDetailRepository

    @Binds
    @Singleton
    abstract fun bindTeamFormationDetailRepository(
        repository: RemoteTeamFormationDetailRepository
    ): TeamFormationDetailRepository

    @Binds
    @Singleton
    abstract fun bindCampaignAreaRepository(
        repository: DefaultCampaignAreaRepository
    ): CampaignAreaRepository
}
