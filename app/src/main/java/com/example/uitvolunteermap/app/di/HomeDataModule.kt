package com.example.uitvolunteermap.app.di

import com.example.uitvolunteermap.features.home.data.repository.FakeHomeRepository
import com.example.uitvolunteermap.features.home.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeDataModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(repository: FakeHomeRepository): HomeRepository
}