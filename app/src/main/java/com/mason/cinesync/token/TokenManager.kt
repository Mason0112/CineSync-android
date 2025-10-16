package com.mason.cinesync.token

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object TokenManager {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        // 建立或取得 Master Key
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // 使用加密的 SharedPreferences
        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "auth_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private const val TOKEN_KEY = "jwt_token"

    fun saveToken(token: String?) {
        sharedPreferences.edit {
            putString(TOKEN_KEY, token)
        }
    }

    fun getToken(): String? = sharedPreferences.getString(TOKEN_KEY, null)

    fun deleteToken() {
        sharedPreferences.edit {
            remove(TOKEN_KEY)
        }
    }
}
