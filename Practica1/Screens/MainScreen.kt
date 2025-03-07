package com.example.practica1_3compose.screens

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.practica1_3compose.ui.components.NewMenu
import com.example.practica1_3compose.ui.theme.Pink40
import com.example.practica1_3compose.ui.theme.Purple40

/* Pantalla principal de la aplicacion */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val activity = (LocalContext.current as? Activity)
    Scaffold( modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = { Text(text = "Práctica 1") },
            actions = {
                var isMenuOpened by remember {
                    mutableStateOf(false)
                }
                IconButton(onClick = { isMenuOpened = true}) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Opciones de menú")

                    DropdownMenu(expanded = isMenuOpened, onDismissRequest = { isMenuOpened = false }) {
                        DropdownMenuItem(text = {
                            Text(text = "suma")
                        }, onClick = {
                            isMenuOpened = false
                            navController.navigate("sumaScreen")
                        })
                        DropdownMenuItem(text = {
                            Text(text = "nombre")
                        }, onClick = {
                            isMenuOpened = false
                            navController.navigate("nombreScreen")
                        })
                        DropdownMenuItem(text = {
                            Text(text = "fecha nacimiento")
                        }, onClick = {
                            isMenuOpened = false
                            navController.navigate("fechaNacScreen")
                        })
                        DropdownMenuItem(text = {
                            Text(text = "salir")
                        }, onClick = {
                            isMenuOpened = false
                            activity?.finish()
                        })
                    }
                }
            })
    }) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Práctica 1", color = Purple40)
            Text(text = "Reyes Cruz Amairani", color = Pink40)
        }
    }
}
