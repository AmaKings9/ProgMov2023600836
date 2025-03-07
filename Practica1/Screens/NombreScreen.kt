package com.example.practica1_3compose.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun NombreScreen(navController: NavController) {
    Scaffold( modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = { Text(text = "Práctica 1: Nombre completo") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atras")
                }
            }
        )
    }) {
        var txt_nombre by remember { mutableStateOf("") }
        var txt_ap_pat by remember { mutableStateOf("") }
        var txt_ap_mat by remember { mutableStateOf("") }
        var txt_nombre_final by remember { mutableStateOf("") }

        fun juntar_nombre() {
            val ap_pat = txt_ap_pat
            val ap_mat = txt_ap_mat
            val nom = txt_nombre
            txt_nombre_final = "$ap_pat $ap_mat $nom"
        }

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Row (Modifier.fillMaxWidth().padding(20.dp), Arrangement.SpaceAround) {
                Text(text = "Apellido Paterno:", Modifier.padding(10.dp).weight(1f))
                TextField(value = txt_ap_pat, onValueChange = {txt_ap_pat = it}, Modifier.weight(2f))
            }
            Row (Modifier.fillMaxWidth().padding(20.dp), Arrangement.SpaceAround){
                Text(text = "Apellido Materno:", Modifier.padding(10.dp).weight(1f))
                TextField(value = txt_ap_mat, onValueChange = {txt_ap_mat = it}, Modifier.weight(2f))
            }
            Row (Modifier.fillMaxWidth().padding(20.dp), Arrangement.SpaceAround){
                Text(text = "Nombre:", Modifier.padding(10.dp).weight(1f))
                TextField(value = txt_nombre, onValueChange = {txt_nombre = it}, Modifier.weight(2f))
            }
            Button(onClick = {juntar_nombre()}) {
                Text(text = "Aceptar")
            }
            Row (Modifier.fillMaxWidth().padding(20.dp)){
                Text(text = "Su nombre completo es: $txt_nombre_final", Modifier.padding(10.dp))
            }
        }
    }
}
