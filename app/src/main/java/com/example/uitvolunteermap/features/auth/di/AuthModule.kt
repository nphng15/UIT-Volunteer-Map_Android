package com.example.uitvolunteermap.features.auth.di

import com.example.uitvolunteermap.features.auth.data.repository.AuthRepositoryImpl
import com.example.uitvolunteermap.features.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}