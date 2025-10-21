package com.mason.cinesync.ui.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mason.cinesync.token.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState

/**
 * 範例 1: 在 ViewModel 中訂閱 logoutFlow
 *
 * 這是推薦的做法，讓 ViewModel 處理業務邏輯，
 * UI 只需要觀察 ViewModel 的狀態
 */
class ExampleViewModel : ViewModel() {

    private val _shouldNavigateToLogin = MutableStateFlow(false)
    val shouldNavigateToLogin: StateFlow<Boolean> = _shouldNavigateToLogin.asStateFlow()

    init {
        // 在 ViewModel 初始化時開始監聽登出事件
        viewModelScope.launch {
            TokenManager.logoutFlow.collect {
                // 收到登出事件，更新狀態通知 UI
                _shouldNavigateToLogin.value = true
            }
        }
    }

    fun onNavigatedToLogin() {
        // UI 完成導航後重置狀態
        _shouldNavigateToLogin.value = false
    }
}

/**
 * 範例 2: 在 Composable Screen 中使用
 */
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel,
    onNavigateToLogin: () -> Unit
) {
    // 觀察 ViewModel 的狀態
    val shouldNavigateToLogin = viewModel.shouldNavigateToLogin.collectAsState().value

    // 當需要導航到登入畫面時執行
    LaunchedEffect(shouldNavigateToLogin) {
        if (shouldNavigateToLogin) {
            onNavigateToLogin()
            viewModel.onNavigatedToLogin() // 重置狀態
        }
    }

    // 你的 UI 內容...
}

/**
 * 範例 3: 直接在 Composable 中訂閱（不推薦，但可行）
 *
 * 這種方式會讓 UI 直接處理業務邏輯，
 * 建議還是透過 ViewModel 來處理
 */
@Composable
fun ExampleScreenDirect(
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        TokenManager.logoutFlow.collect {
            // 收到登出事件，直接導航
            onNavigateToLogin()
        }
    }

    // 你的 UI 內容...
}

/**
 * 使用說明：
 *
 * 1. 在你的主要 ViewModel（如 PopularMoviesViewModel 或 AuthViewModel）中
 *    加入類似的 logoutFlow 訂閱邏輯
 *
 * 2. 在你的 Navigation 設定中，根據 shouldNavigateToLogin 的狀態
 *    導航到登入畫面
 *
 * 3. AuthInterceptor 會在收到 401 錯誤時自動呼叫 TokenManager.notifyLogout()
 *    你的 UI 就會自動收到通知並導航到登入畫面
 */

