package com.mason.cinesync.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mason.cinesync.model.dto.MovieApiResponse
import com.mason.cinesync.token.TokenManager
import com.mason.cinesync.ui.component.CineSyncTopBar
import com.mason.cinesync.viewmodel.AuthViewModel
import com.mason.cinesync.viewmodel.AuthViewModelFactory
import com.mason.cinesync.viewmodel.PopularMoviesUiState
import com.mason.cinesync.viewmodel.PopularMoviesViewModel
import com.mason.cinesync.viewmodel.PopularMoviesViewModelFactory
import com.mason.cinesync.viewmodel.UsersViewModel
import com.mason.cinesync.viewmodel.UsersViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopularMoviesScreen(
    viewModel: PopularMoviesViewModel = viewModel(factory = PopularMoviesViewModelFactory()),
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory()),
    usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory()),
    onNavigateToLogin: () -> Unit = {},
    onMovieClick: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val loginUserState by usersViewModel.userUiState.collectAsState()
    var isLoggedIn by remember { mutableStateOf(TokenManager.hasValidToken()) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            usersViewModel.loadLoggedInUser()
        }
    }

    LaunchedEffect(Unit) {
        TokenManager.logoutFlow.collect {
            isLoggedIn = false
        }
    }

    val userName = when (val state = loginUserState) {
        is com.mason.cinesync.viewmodel.UsersUiState.Success -> state.loginResponse.userName
        else -> null
    }

    val listState = rememberLazyGridState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val state = uiState
            if (state !is PopularMoviesUiState.Success) return@derivedStateOf false

            // 目前的列表資訊
            val layoutInfo = listState.layoutInfo
            // 目前列表的總數目
            val totalItemsNumber = layoutInfo.totalItemsCount
            // 最後一個可見項目的索引
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            // 檢查是否接近底部且沒有正在載入且還有更多頁面
            lastVisibleItemIndex > (totalItemsNumber - 4) &&

                    !state.isLoadingMore &&
                    state.currentPage < state.totalPages
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadNextPage()
        }
    }

    Scaffold(
        topBar = {
            CineSyncTopBar(
                title = "CineSync",
                isLoggedIn = isLoggedIn,
                userName = userName,
                onLoginClick = {
                    onNavigateToLogin()
                },
                onLogoutClick = {
                    authViewModel.logout()
                    isLoggedIn = false
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is PopularMoviesUiState.Loading -> {
                    // 初次載入
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is PopularMoviesUiState.Error -> {
                    if (state.movies.isEmpty()) {
                        // 初次載入失敗
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Error: ${state.message}")
                                // 可以加入重試按鈕
                            }
                        }
                    } else {
                        // 載入更多失敗，但保留已有的電影列表
                        MovieGrid(
                            movies = state.movies,
                            listState = listState,
                            isLoadingMore = false,
                            errorMessage = state.message,
                            onMovieClick = onMovieClick
                        )
                    }
                }

                is PopularMoviesUiState.Success -> {
                    MovieGrid(
                        movies = state.movies,
                        listState = listState,
                        isLoadingMore = state.isLoadingMore,
                        errorMessage = null,
                        onMovieClick = onMovieClick
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieGrid(
    movies: List<MovieApiResponse>,
    listState: androidx.compose.foundation.lazy.grid.LazyGridState,
    isLoadingMore: Boolean,
    errorMessage: String?,
    onMovieClick: (Int) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieItem(
                movie = movie,
                onClick = { onMovieClick(movie.id.toInt()) }
            )
        }

        // 載入更多的指示器
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // 載入更多時發生錯誤
        if (errorMessage != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "載入失敗: $errorMessage",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun MovieItem(
    movie: MovieApiResponse,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(2f / 3f)
        )
    }
}