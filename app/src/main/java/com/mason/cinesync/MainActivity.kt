package com.mason.cinesync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mason.cinesync.ui.screen.PopularMoviesScreen
import com.mason.cinesync.ui.theme.CineSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CineSyncTheme {
                PopularMoviesScreen()
            }
        }
    }
}
