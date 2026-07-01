package com.example.cryptotester

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.cryptotester.ui.navigation.AppNavGraph
import com.example.cryptotester.ui.theme.CryptoTesterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoTesterTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}