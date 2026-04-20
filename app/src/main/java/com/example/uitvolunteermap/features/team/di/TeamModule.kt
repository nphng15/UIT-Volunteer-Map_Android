package com.example.uitvolunteermap.features.team.di

import com.example.uitvolunteermap.features.team.data.repository.FakeTeamRepository
import com.example.uitvolunteermap.features.team.domain.repository.TeamRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TeamModule {

    @Binds
    @Singleton
    abstract fun bindTeamRepository(
        fakeTeamRepository: FakeTeamRepository
    ): TeamRepository
}