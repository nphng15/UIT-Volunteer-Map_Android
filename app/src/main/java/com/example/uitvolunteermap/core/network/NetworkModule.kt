package com.example.uitvolunteermap.core.network

import com.example.uitvolunteermap.BuildConfig
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.auth.data.remote.AuthApiService
import com.example.uitvolunteermap.features.admin.account.data.datasource.AccountApiService
import com.example.uitvolunteermap.features.admin.campaign.data.datasource.AdminCampaignApiService
import com.example.uitvolunteermap.features.admin.dashboard.data.datasource.AdminDashboardApiService
import com.example.uitvolunteermap.features.admin.post.data.datasource.AdminPostApiService
import com.example.uitvolunteermap.features.admin.team.data.datasource.AdminTeamApiService
import com.example.uitvolunteermap.features.attendance.data.datasource.AttendanceApiService
import com.example.uitvolunteermap.features.campaign.data.datasource.CampaignApiService
import com.example.uitvolunteermap.features.campaign.data.datasource.TeamApiService
import com.example.uitvolunteermap.features.checkin.data.remote.CheckinApiService
import com.example.uitvolunteermap.features.post.data.remote.PostApiService
import com.example.uitvolunteermap.features.profile.data.datasource.UserProfileApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(sessionManager: SessionManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .connectTimeout(BuildConfig.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(BuildConfig.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                sessionManager.bearerToken?.let { requestBuilder.header("Authorization", it) }
                chain.proceed(requestBuilder.build())
            }
            .addInterceptor { chain ->
                val response = chain.proceed(chain.request())
                val path = chain.request().url.encodedPath
                if (response.code == 401 && !path.contains("auth/login")) {
                    sessionManager.onSessionExpired()
                }
                response
            }
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideCampaignApiService(retrofit: Retrofit): CampaignApiService =
        retrofit.create(CampaignApiService::class.java)

    @Provides
    @Singleton
    fun provideTeamApiService(retrofit: Retrofit): TeamApiService =
        retrofit.create(TeamApiService::class.java)

    @Provides
    @Singleton
    fun providePostApiService(retrofit: Retrofit): PostApiService =
        retrofit.create(PostApiService::class.java)

    @Provides
    @Singleton
    fun provideCheckinApiService(retrofit: Retrofit): CheckinApiService =
        retrofit.create(CheckinApiService::class.java)

    @Provides
    @Singleton
    fun provideAttendanceApiService(retrofit: Retrofit): AttendanceApiService =
        retrofit.create(AttendanceApiService::class.java)

    // ===== Admin API services =====
    @Provides
    @Singleton
    fun provideAccountApiService(retrofit: Retrofit): AccountApiService =
        retrofit.create(AccountApiService::class.java)

    @Provides
    @Singleton
    fun provideAdminCampaignApiService(retrofit: Retrofit): AdminCampaignApiService =
        retrofit.create(AdminCampaignApiService::class.java)

    @Provides
    @Singleton
    fun provideAdminTeamApiService(retrofit: Retrofit): AdminTeamApiService =
        retrofit.create(AdminTeamApiService::class.java)

    @Provides
    @Singleton
    fun provideAdminPostApiService(retrofit: Retrofit): AdminPostApiService =
        retrofit.create(AdminPostApiService::class.java)

    @Provides
    @Singleton
    fun provideAdminDashboardApiService(retrofit: Retrofit): AdminDashboardApiService =
        retrofit.create(AdminDashboardApiService::class.java)

    @Provides
    @Singleton
    fun provideUserProfileApiService(retrofit: Retrofit): UserProfileApiService =
        retrofit.create(UserProfileApiService::class.java)
}
