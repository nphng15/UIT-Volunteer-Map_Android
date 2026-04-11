package com.example.uitvolunteermap.app.di

import com.example.uitvolunteermap.features.profile.data.repository.FakeProfileRepositoryImpl
import com.example.uitvolunteermap.features.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileDataModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(repository: FakeProfileRepositoryImpl): ProfileRepository
}
