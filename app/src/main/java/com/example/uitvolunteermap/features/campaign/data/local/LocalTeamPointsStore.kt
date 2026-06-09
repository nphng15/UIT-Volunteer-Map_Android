package com.example.uitvolunteermap.features.campaign.data.local

import android.content.Context
import com.example.uitvolunteermap.core.common.di.IoDispatcher
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamVisitPoint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * JSON-file persistence for team activity points a leader marked manually.
 * Mirrors the LocalPostStore pattern; survives process restart.
 */
@Singleton
class LocalTeamPointsStore @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    private val mutex = Mutex()
    private val gson = Gson()
    private val file: File
        get() = File(context.filesDir, "campaign_area/team_points.json")

    suspend fun loadForCampaign(campaignId: Int): List<TeamVisitPoint> = withContext(ioDispatcher) {
        mutex.withLock { readUnsafe().filter { it.campaignId == campaignId } }
    }

    suspend fun add(point: TeamVisitPoint) = withContext(ioDispatcher) {
        mutex.withLock {
            val current = readUnsafe().toMutableList()
            current.removeAll { it.id == point.id }
            current.add(0, point)
            writeUnsafe(current)
        }
    }

    suspend fun remove(id: String) = withContext(ioDispatcher) {
        mutex.withLock { writeUnsafe(readUnsafe().filterNot { it.id == id }) }
    }

    private fun readUnsafe(): List<TeamVisitPoint> {
        if (!file.exists()) return emptyList()
        return runCatching {
            val type = object : TypeToken<List<TeamVisitPoint>>() {}.type
            gson.fromJson<List<TeamVisitPoint>>(file.readText(), type).orEmpty()
        }.onFailure { Timber.w(it, "Failed to load local team points") }
            .getOrDefault(emptyList())
    }

    private fun writeUnsafe(points: List<TeamVisitPoint>) {
        runCatching {
            file.parentFile?.mkdirs()
            file.writeText(gson.toJson(points))
        }.onFailure { Timber.w(it, "Failed to persist local team points") }
    }
}
