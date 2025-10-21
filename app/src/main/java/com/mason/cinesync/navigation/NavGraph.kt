package com.mason.cinesync.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mason.cinesync.token.TokenManager
import com.mason.cinesync.ui.screen.LoginScreen
import com.mason.cinesync.ui.screen.PopularMoviesScreen
import com.mason.cinesync.ui.screen.RegisterScreen

// 定義路由
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MOVIES = "movies"
}

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    // 在函數體內檢查 Token 狀態，確保 TokenManager 已初始化
    val startDestination = if (TokenManager.hasValidToken()) {
        Routes.MOVIES
    } else {
        Routes.LOGIN
    }

    // 監聽登出事件
    LaunchedEffect(Unit) {
        TokenManager.logoutFlow.collect {
            // 當收到登出事件時，導航到登入畫面並清除返回堆疊
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 登入畫面
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.MOVIES) {
                        // 清除登入和註冊畫面，防止返回
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // 註冊畫面
        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.MOVIES) {
                        // 清除登入和註冊畫面，防止返回
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // 電影列表畫面
        composable(Routes.MOVIES) {
            PopularMoviesScreen()
        }
    }
}

