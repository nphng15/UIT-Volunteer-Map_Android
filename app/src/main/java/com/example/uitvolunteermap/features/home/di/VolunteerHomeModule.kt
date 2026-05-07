package com.example.uitvolunteermap.features.home.di

import com.example.uitvolunteermap.features.home.data.repository.MockVolunteerHomeRepository
import com.example.uitvolunteermap.features.home.domain.repository.VolunteerHomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VolunteerHomeModule {

    @Binds
    @Singleton
    abstract fun bindVolunteerHomeRepository(
        repository: MockVolunteerHomeRepository
    ): VolunteerHomeRepository
}
