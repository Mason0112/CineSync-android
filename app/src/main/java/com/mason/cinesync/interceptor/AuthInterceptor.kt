package com.mason.cinesync.interceptor

import com.mason.cinesync.token.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 獲取 token (直接使用 TokenManager 單例)
        val token = TokenManager.getToken()

        // 如果有 token,就加入 Authorization header
        val requestWithAuth = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        // 執行請求
        val response = chain.proceed(requestWithAuth)

        // 處理 401 Unauthorized（token 過期或無效）
        if (response.code == 401 && token != null) {
            response.close()

            // 清除無效的 token
            TokenManager.deleteToken()

            // 可以在這裡觸發事件通知 UI 跳轉到登入畫面
            // 或嘗試刷新 token（如果有 refresh token 機制）

            throw IOException("Unauthorized: Token expired or invalid")
        }

        return response
    }
}