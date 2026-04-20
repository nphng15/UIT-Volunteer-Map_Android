package com.example.uitvolunteermap.app.di

import com.example.uitvolunteermap.features.auth.data.repository.FakeAuthRepository
import com.example.uitvolunteermap.features.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: FakeAuthRepository): AuthRepository
}
