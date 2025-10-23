package com.mason.cinesync.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.mason.cinesync.model.dto.CommentDto
import com.mason.cinesync.model.dto.CommentRequest
import com.mason.cinesync.token.TokenManager
import com.mason.cinesync.viewmodel.CreateCommentUiState
import com.mason.cinesync.viewmodel.MovieMessageBoardUiState
import com.mason.cinesync.viewmodel.MovieMessageBoardViewModel
import com.mason.cinesync.viewmodel.MovieMessageBoardViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieMessageBoardScreen(
    movieId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    viewModel: MovieMessageBoardViewModel = viewModel(
        factory = MovieMessageBoardViewModelFactory(movieId)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val commentUiState by viewModel.commentUiState.collectAsState()
    val isLoggedIn = TokenManager.hasValidToken()

    var commentText by remember { mutableStateOf("") }

    // Reset comment form when successfully created
    LaunchedEffect(commentUiState) {
        if (commentUiState is CreateCommentUiState.Success) {
            commentText = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Message Board") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is MovieMessageBoardUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MovieMessageBoardUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            is MovieMessageBoardUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Movie header
                    MovieHeader(
                        title = state.movieDetail.title,
                        backdropPath = state.movieDetail.backdropPath,
                        overview = state.movieDetail.overview
                    )

                    // Comment input (only if logged in)
                    if (isLoggedIn) {
                        CommentInputSection(
                            commentText = commentText,
                            onCommentTextChange = { commentText = it },
                            onSubmit = {
                                if (commentText.isNotBlank()) {
                                    viewModel.createComment(
                                        CommentRequest(
                                            movieId = movieId.toString(),
                                            content = commentText
                                        )
                                    )
                                }
                            },
                            commentUiState = commentUiState
                        )
                    } else {
                        LoginPromptSection(
                            onLoginClick = onNavigateToLogin
                        )
                    }

                    // Comments list
                    CommentsList(
                        comments = state.comments,
                        hasMore = state.hasMoreComments,
                        onLoadMore = { viewModel.loadNextPage() }
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieHeader(
    title: String,
    backdropPath: String?,
    overview: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500$backdropPath",
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = overview,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
private fun LoginPromptSection(
    onLoginClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Please log in to leave a comment",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onLoginClick
            ) {
                Text("Log In")
            }
        }
    }
}

@Composable
private fun CommentInputSection(
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    commentUiState: CreateCommentUiState
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = onCommentTextChange,
                label = { Text("Write a comment") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
                enabled = commentUiState !is CreateCommentUiState.Loading
            )
            Spacer(modifier = Modifier.height(8.dp))

            when (commentUiState) {
                is CreateCommentUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                is CreateCommentUiState.Success -> {
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.align(Alignment.End),
                        enabled = commentText.isNotBlank()
                    ) {
                        Text("Post Comment")
                    }
                }

                is CreateCommentUiState.Error -> {
                    Column {
                        Text(
                            text = "Error: ${commentUiState.message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Button(
                            onClick = onSubmit,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Post Comment")
                        }
                    }
                }

                is CreateCommentUiState.Idle -> {
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.align(Alignment.End),
                        enabled = commentText.isNotBlank()
                    ) {
                        Text("Post Comment")
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentsList(
    comments: List<CommentDto>,
    hasMore: Boolean,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - 2) && hasMore
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(comments, key = { it.id }) { comment ->
            CommentItem(comment = comment)
        }

        if (hasMore) {
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
    }
}

@Composable
private fun CommentItem(comment: CommentDto) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = comment.userName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.createdAt ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}