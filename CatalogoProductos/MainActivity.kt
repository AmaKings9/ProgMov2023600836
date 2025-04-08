package com.example.tiendalibros

import android.database.Cursor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.time.format.TextStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavegacion()
        }
    }
}

@Composable
fun AppNavegacion(){

    val navControlador = rememberNavController()

    // definir el host
    NavHost(navController = navControlador, startDestination = "uiprincipal") {
        composable("uiprincipal") {
            UIPrincipal(navControlador)
        }

        composable("agregarLibrosUI") {
            agregarLibrosUI(navControlador)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UIPrincipal(navControlador: NavController) {
    val context = LocalContext.current
    val operaciones = LibroOperaciones(context)
    val libros by remember { mutableStateOf(operaciones.obtenerLibros()) }

    val auxSQLite = DBHelper(LocalContext.current)
    val base = auxSQLite.writableDatabase
    val cursor = base.rawQuery("SELECT * FROM Libros", null)
    val lista = mutableListOf<String>()
    while (cursor.moveToNext()) {
        lista.add(cursor.getString(4))
    }
    cursor.close()
    base.close()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Libros") },
                actions = {
                    IconButton(onClick = {
                        println("Plus button clicked")
                         navControlador.navigate("agregarLibrosUI")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Book",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(libros) { libro ->
                    Card(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5DC)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Image(
                                bitmap = ParseImg.convertBase64ToBitmap(libro.libro_img).asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(end = 16.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)

                            ) {
                                Text(
                                    text = libro.nombre_libro,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontFamily = FontFamily.Serif
                                    )
                                )
                                Text(
                                    text = "$${libro.precio}",
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.DarkGray,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                )
                                Text(
                                    text = libro.descripcion,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Gray,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .padding(start = 16.dp)
                            ) {
                                IconButton(onClick = {
                                    println("Edit clicked for ${libro.nombre_libro}")
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Edit",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = {
                                    println("Delete clicked for ${libro.nombre_libro}")
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun agregarLibrosUI(navControlador: NavController) {
    val context = LocalContext.current
    val operaciones = LibroOperaciones(context)

    var nombreLibro by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var libroImg by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Agregar Libro") },
                navigationIcon = {
                    IconButton(onClick = { navControlador.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombreLibro,
                onValueChange = { nombreLibro = it },
                label = { Text("Nombre del Libro") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it.filter { char -> char.isDigit() || char == '.' } },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    libroImg = ParseImg.convertDrawableToBase64(context, R.drawable.cadaver_exquisito)
                    println("Camera button clicked, using placeholder image")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Image",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Agregar Imagen")
            }

            Button(
                onClick = {
                    if (nombreLibro.isNotBlank() && precio.isNotBlank() && descripcion.isNotBlank() && libroImg.isNotBlank()) {
                        val nuevoLibro = Libro(
                            nombre_libro = nombreLibro,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            descripcion = descripcion,
                            libro_img = libroImg
                        )
                        navControlador.popBackStack()
                    } else {
                        println("Please fill all fields")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Save",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Guardar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun previsualizacion() {
   AppNavegacion()
}