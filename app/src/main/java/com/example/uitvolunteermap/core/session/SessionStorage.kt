package com.example.uitvolunteermap.core.session

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

data class SessionData(
    val token: String,
    val accountId: Int,
    val username: String,
    val role: UserRole
)

@Singleton
class SessionStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private companion object {
        const val PREF_NAME = "session_prefs"
        const val KEY_TOKEN = "token"
        const val KEY_ACCOUNT_ID = "account_id"
        const val KEY_USERNAME = "username"
        const val KEY_ROLE = "role"
    }

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        try {
            EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Timber.w(e, "EncryptedSharedPreferences corrupted, clearing and recreating")
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
            EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    fun save(token: String, accountId: Int, username: String, role: UserRole) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_ACCOUNT_ID, accountId)
            .putString(KEY_USERNAME, username)
            .putString(KEY_ROLE, role.name)
            .apply()
    }

    fun load(): SessionData? {
        val token = prefs.getString(KEY_TOKEN, null) ?: return null
        val accountId = prefs.getInt(KEY_ACCOUNT_ID, -1)
        if (accountId == -1) return null
        val username = prefs.getString(KEY_USERNAME, null) ?: return null
        val roleName = prefs.getString(KEY_ROLE, null) ?: return null
        val role = runCatching { UserRole.valueOf(roleName) }.getOrNull() ?: return null
        return SessionData(token = token, accountId = accountId, username = username, role = role)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
