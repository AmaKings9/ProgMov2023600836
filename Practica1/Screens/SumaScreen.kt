package com.example.practica1_3compose.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.practica1_3compose.ui.components.NewMenu
import com.example.practica1_3compose.ui.theme.Pink40
import com.example.practica1_3compose.ui.theme.Purple40

/* Pantalla principal de la aplicacion */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SumaScreen(navController: NavController) {
    Scaffold( modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = { Text(text = "Práctica 1: Suma de números") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atras")
                }
            }
        )

    }) {
        var txtnum1 by remember { mutableStateOf("") }
        var txtnum2 by remember { mutableStateOf("") }
        var txtnum3 by remember { mutableStateOf("") }
        var txtres by remember { mutableStateOf("") }

        fun suma() {
            val num1 = txtnum1.toIntOrNull() ?: 0
            val num2 = txtnum2.toIntOrNull() ?: 0
            val num3 = txtnum3.toIntOrNull() ?: 0
            txtres = (num1+num2+num3).toString()
        }

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Row (Modifier.fillMaxWidth().padding(20.dp)) {
                Text(text = "Número 1:", Modifier.padding(10.dp))
                TextField(value = txtnum1, onValueChange = {txtnum1 = it})
            }
            Row (Modifier.fillMaxWidth().padding(20.dp)){
                Text(text = "Número 2:", Modifier.padding(10.dp))
                TextField(value = txtnum2, onValueChange = {txtnum2 = it})
            }
            Row (Modifier.fillMaxWidth().padding(20.dp)){
                Text(text = "Número 3:", Modifier.padding(10.dp))
                TextField(value = txtnum3, onValueChange = {txtnum3 = it})
            }
            Button(onClick = {suma()}) {
                Text(text = "Sumar")
            }
            Row (Modifier.fillMaxWidth().padding(20.dp)){
                Text(text = "Resultado:", Modifier.padding(10.dp))
                Text(text = txtres, Modifier.padding(10.dp))
            }
        }
    }
}
