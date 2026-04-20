package com.example.uitvolunteermap.app.di

import com.example.uitvolunteermap.features.post.data.repository.InMemoryPostRepository
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PostModule {

    @Binds
    @Singleton
    abstract fun bindPostRepository(
        repository: InMemoryPostRepository
    ): PostRepository
}
