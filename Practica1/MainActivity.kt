package com.example.practica1_3compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.practica1_3compose.screens.FecNacScreen
import com.example.practica1_3compose.screens.MainScreen
import com.example.practica1_3compose.screens.NombreScreen
import com.example.practica1_3compose.screens.SumaScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost (navController = navController, startDestination = "mainScreen", builder = {
                composable("mainScreen") {
                    MainScreen(navController)
                }
                composable("sumaScreen") {
                    SumaScreen(navController)
                }
                composable("nombreScreen") {
                    NombreScreen(navController)
                }
                composable("fechaNacScreen") {
                    FecNacScreen(navController)
                }
            })
        }
    }
}
