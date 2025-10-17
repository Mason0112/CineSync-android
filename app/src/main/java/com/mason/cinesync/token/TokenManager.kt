package com.mason.cinesync.token

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object TokenManager {
    private lateinit var sharedPreferences: SharedPreferences

    private const val TOKEN_KEY = "jwt_token"
    private const val REFRESH_TOKEN_KEY = "refresh_token"
    private const val TOKEN_EXPIRY_KEY = "token_expiry"

    /**
     * 檢查是否已初始化
     */
    private fun checkInitialized() {
        check(::sharedPreferences.isInitialized) {
            "TokenManager has not been initialized. Call TokenManager.init(context) first."
        }
    }

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

    /**
     * 儲存訪問 token
     */
    fun saveToken(token: String?) {
        checkInitialized()
        sharedPreferences.edit {
            putString(TOKEN_KEY, token)
        }
    }

    /**
     * 獲取訪問 token
     */
    fun getToken(): String? {
        checkInitialized()
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    /**
     * 儲存刷新 token
     */
    fun saveRefreshToken(refreshToken: String?) {
        checkInitialized()
        sharedPreferences.edit {
            putString(REFRESH_TOKEN_KEY, refreshToken)
        }
    }

    /**
     * 獲取刷新 token
     */
    fun getRefreshToken(): String? {
        checkInitialized()
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
    }

    /**
     * 儲存 token 過期時間（毫秒）
     */
    fun saveTokenExpiry(expiryTime: Long) {
        checkInitialized()
        sharedPreferences.edit {
            putLong(TOKEN_EXPIRY_KEY, expiryTime)
        }
    }

    /**
     * 獲取 token 過期時間
     */
    fun getTokenExpiry(): Long {
        checkInitialized()
        return sharedPreferences.getLong(TOKEN_EXPIRY_KEY, 0L)
    }

    /**
     * 檢查 token 是否已過期
     */
    fun isTokenExpired(): Boolean {
        checkInitialized()
        val expiryTime = getTokenExpiry()
        return expiryTime > 0 && System.currentTimeMillis() >= expiryTime
    }

    /**
     * 檢查是否有有效的 token
     */
    fun hasValidToken(): Boolean {
        checkInitialized()
        return getToken() != null && !isTokenExpired()
    }

    /**
     * 刪除訪問 token
     */
    fun deleteToken() {
        checkInitialized()
        sharedPreferences.edit {
            remove(TOKEN_KEY)
            remove(TOKEN_EXPIRY_KEY)
        }
    }

    /**
     * 清除所有認證資訊（登出時使用）
     */
    fun clearAll() {
        checkInitialized()
        sharedPreferences.edit {
            remove(TOKEN_KEY)
            remove(REFRESH_TOKEN_KEY)
            remove(TOKEN_EXPIRY_KEY)
        }
    }

    /**
     * 儲存完整的認證資訊
     */
    fun saveAuthData(token: String, refreshToken: String? = null, expiryInSeconds: Long? = null) {
        checkInitialized()
        sharedPreferences.edit {
            putString(TOKEN_KEY, token)
            refreshToken?.let { putString(REFRESH_TOKEN_KEY, it) }
            expiryInSeconds?.let {
                // 將過期時間轉換為絕對時間戳
                val expiryTime = System.currentTimeMillis() + (it * 1000)
                putLong(TOKEN_EXPIRY_KEY, expiryTime)
            }
        }
    }
}
