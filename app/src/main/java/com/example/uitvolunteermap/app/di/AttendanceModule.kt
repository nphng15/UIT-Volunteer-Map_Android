package com.example.uitvolunteermap.app.di

import com.example.uitvolunteermap.features.attendance.data.repository.RemoteAttendanceRepository
import com.example.uitvolunteermap.features.attendance.domain.repository.AttendanceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AttendanceModule {

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(
        repository: RemoteAttendanceRepository
    ): AttendanceRepository
}
