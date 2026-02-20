package com.studio.amen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.amen.presentation.theme.AmenTheme
import com.example.amen.presentation.ui.navigation.AmenNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmenTheme {
                val navController = rememberNavController()
                AmenNavGraph(navController = navController)
            }
        }
    }
}