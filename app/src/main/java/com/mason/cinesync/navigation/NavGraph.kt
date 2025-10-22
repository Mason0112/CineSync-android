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
    // 電影畫面作為主畫面，不需要登入即可查看
    val startDestination = Routes.MOVIES

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 電影列表畫面（主畫面）
        composable(Routes.MOVIES) {
            PopularMoviesScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        // 登入畫面
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = {
                    // 登入成功後返回電影畫面
                    navController.popBackStack(Routes.MOVIES, inclusive = false)
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
                    // 註冊成功後返回電影畫面
                    navController.popBackStack(Routes.MOVIES, inclusive = false)
                }
            )
        }
    }
}

