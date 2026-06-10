/*
 * [Parent Feature/Milestone] Kilo Android App
 * [Subtask] Secure settings for API key management
 * [Upstream] UI Settings -> [Downstream] kilo serve environment
 * [Law Check] 45 lines | Passed Do It Check
 */

package com.kilocli.android

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object KiloSettings {
    private const val PREFS_NAME = "kilo_secure_prefs"
    
    private fun prefs(context: Context): SharedPreferences {
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            PREFS_NAME,
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionKeyScheme.AES256_GCM
        )
    }

    fun saveApiKey(context: Context, key: String) {
        prefs(context).edit().putString("api_key", key).apply()
    }

    fun getApiKey(context: Context): String? {
        return prefs(context).getString("api_key", null)
    }

    fun saveServerPort(context: Context, port: Int) {
        prefs(context).edit().putInt("server_port", port).apply()
    }

    fun getServerPort(context: Context): Int {
        return prefs(context).getInt("server_port", 0)
    }
}