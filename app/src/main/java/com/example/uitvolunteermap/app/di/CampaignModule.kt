package com.example.uitvolunteermap.features.campaign.di

import com.example.uitvolunteermap.features.campaign.data.repository.MockCampaignRepository
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CampaignModule {

    @Binds
    @Singleton
    abstract fun bindCampaignRepository(
        repository: MockCampaignRepository
    ): CampaignRepository
}
