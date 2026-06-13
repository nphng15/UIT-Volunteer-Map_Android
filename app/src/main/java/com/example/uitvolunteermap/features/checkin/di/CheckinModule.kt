package com.example.uitvolunteermap.features.checkin.di

import com.example.uitvolunteermap.features.checkin.data.repository.RemoteCheckinRepository
import com.example.uitvolunteermap.features.checkin.domain.repository.CheckinRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CheckinModule {

    @Binds
    abstract fun bindCheckinRepository(
        impl: RemoteCheckinRepository
    ): CheckinRepository
}
