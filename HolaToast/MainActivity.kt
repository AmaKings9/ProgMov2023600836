package com.example.holatoast

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.holatoast.ui.theme.HolaToastTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UIPrincipal()
        }
    }
}

@Composable
fun UIPrincipal() {
    Column (
        modifier = Modifier
            .fillMaxSize().padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )   {
        val contexto = LocalContext.current
        var nombre by rememberSaveable { mutableStateOf("") }

        Text("Nombre:")

        OutlinedTextField(
            value = nombre,
            onValueChange = {nombre = it},
            label = { Text("Introduce tu nombre")},
            modifier = Modifier.padding(20.dp)
        )

        Button(
            onClick = {Toast.makeText(contexto, "Hola Mundo $nombre!!", Toast.LENGTH_SHORT).show()},
        ) {
            Text("Saludar!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Previsualizacion() {
    UIPrincipal()
}