package com.example.uitvolunteermap.features.campaign.di

import com.example.uitvolunteermap.features.campaign.data.repository.MockCampaignDetailRepository
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignDetailRepository
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
    abstract fun bindCampaignDetailRepository(
        repository: MockCampaignDetailRepository
    ): CampaignDetailRepository
}
