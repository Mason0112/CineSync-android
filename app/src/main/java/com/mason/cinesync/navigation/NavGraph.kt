package com.mason.cinesync.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mason.cinesync.token.TokenManager
import com.mason.cinesync.ui.screen.LoginScreen
import com.mason.cinesync.ui.screen.MovieMessageBoardScreen
import com.mason.cinesync.ui.screen.PopularMoviesScreen
import com.mason.cinesync.ui.screen.RegisterScreen

// 定義路由
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MOVIES = "movies"
    const val MOVIE_MESSAGE_BOARD = "movie_message_board/{movieId}"

    fun movieMessageBoard(movieId: Int) = "movie_message_board/$movieId"
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
                },
                onMovieClick = { movieId ->
                    navController.navigate(Routes.movieMessageBoard(movieId))
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

        // 電影留言板畫面
        composable(
            route = Routes.MOVIE_MESSAGE_BOARD,
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
            MovieMessageBoardScreen(
                movieId = movieId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }
    }
}

