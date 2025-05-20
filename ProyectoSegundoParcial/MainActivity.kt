package com.example.tiendalibros

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.io.ByteArrayOutputStream
import java.time.format.TextStyle

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val preferenceHelper = PreferenceHelper(context)

            // Load initial colors
            val initialCardColor = preferenceHelper.getCardColor()
            val initialAppBackgroundColor = preferenceHelper.getAppBackgroundColor()
            Log.d(TAG, "Initial card_color: ${initialCardColor.toHex()}")
            Log.d(TAG, "Initial app_background_color: ${initialAppBackgroundColor.toHex()}")

            // Use mutableStateOf to ensure UI updates
            val cardColor = remember { mutableStateOf(initialCardColor) }
            val appBackgroundColor = remember { mutableStateOf(initialAppBackgroundColor) }

            MaterialTheme {
                AppNavegacion(
                    cardColor = cardColor.value,
                    appBackgroundColor = appBackgroundColor.value,
                    onCardColorChange = { newColor ->
                        cardColor.value = newColor
                        preferenceHelper.saveCardColor(newColor)
                    },
                    onAppBackgroundColorChange = { newColor ->
                        appBackgroundColor.value = newColor
                        preferenceHelper.saveAppBackgroundColor(newColor)
                    }
                )
            }
        }
    }
}

@Composable
fun AppNavegacion(
    cardColor: Color,
    appBackgroundColor: Color,
    onCardColorChange: (Color) -> Unit,
    onAppBackgroundColorChange: (Color) -> Unit
) {
    val navControlador = rememberNavController()
    NavHost(navController = navControlador, startDestination = "uiprincipal") {
        composable("uiprincipal") {
            UIPrincipal(
                navControlador = navControlador,
                cardColor = cardColor,
                appBackgroundColor = appBackgroundColor
            )
        }
        composable("libroFormUI/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull() ?: 0
            LibroFormUI(
                navControlador = navControlador,
                bookId = bookId,
                appBackgroundColor = appBackgroundColor
            )
        }
        composable("libroFormUI") {
            LibroFormUI(
                navControlador = navControlador,
                bookId = 0,
                appBackgroundColor = appBackgroundColor
            )
        }
        composable("preferencias") {
            PreferenciasUI(
                navControlador = navControlador,
                cardColor = cardColor,
                appBackgroundColor = appBackgroundColor,
                onCardColorChange = onCardColorChange,
                onAppBackgroundColorChange = onAppBackgroundColorChange
            )
        }
        composable("ayuda") {
            AyudaUI(
                navControlador = navControlador,
                appBackgroundColor = appBackgroundColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UIPrincipal(
    navControlador: NavController,
    cardColor: Color,
    appBackgroundColor: Color
) {
    val context = LocalContext.current
    val operaciones = LibroOperaciones(context)
    val libros = remember { mutableStateListOf<Libro>() }
    var showDeleteDialog by remember { mutableStateOf<Libro?>(null) }
    var showDeleteSuccessDialog by remember { mutableStateOf(false) }

    // Function to refresh the list
    val refreshLibros = {
        libros.clear()
        libros.addAll(operaciones.obtenerLibros())
    }

    // Load books initially
    LaunchedEffect(Unit) {
        refreshLibros()
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { libro ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Confirmar Eliminación") },
            text = {
                Text(
                    "¿Estás seguro de que quieres eliminar '${libro.nombre_libro}'?",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        operaciones.eliminarLibro(libro.id_libro)
                        refreshLibros()
                        showDeleteDialog = null
                        showDeleteSuccessDialog = true
                    }
                ) {
                    Text(
                        "Eliminar",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Serif
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text(
                        "Cancelar",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Serif
                        )
                    )
                }
            }
        )
    }

    // Delete Success Dialog
    if (showDeleteSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteSuccessDialog = false },
            title = { Text("Eliminación Exitosa") },
            text = {
                Text(
                    "Libro eliminado exitosamente.",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { showDeleteSuccessDialog = false }) {
                    Text(
                        "Aceptar",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Serif
                        )
                    )
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Libros",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = FontFamily.Serif
                        )
                    )
                },
                modifier = Modifier.padding(horizontal = 10.dp),
                actions = {
                    IconButton(onClick = {
                        navControlador.navigate("libroFormUI")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Book",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        navControlador.navigate("preferencias")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        navControlador.navigate("ayuda")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Help",
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
                            containerColor = cardColor
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
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = libro.nombre_libro,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontFamily = FontFamily.Serif
                                    )
                                )
                                Text(
                                    text = "$${libro.precio}",
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.DarkGray,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                )
                                Text(
                                    text = libro.descripcion,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontSize = 14.sp,
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
                                    navControlador.navigate("libroFormUI/${libro.id_libro}")
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Edit",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = {
                                    showDeleteDialog = libro
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
fun AyudaUI(
    navControlador: NavController,
    appBackgroundColor: Color
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ayuda",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = FontFamily.Serif
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navControlador.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Cómo Usar la Aplicación",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = FontFamily.Serif
                )
            )
            Text(
                text = "Esta aplicación te permite gestionar una lista de libros, incluyendo la creación, edición y eliminación de libros. Puedes personalizar los colores de la interfaz y acceder a esta guía para aprender a usar cada función. A continuación, se describen los botones disponibles:",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontFamily = FontFamily.Serif
                )
            )
            Divider(color = Color.Gray, thickness = 1.dp)
            Text(
                text = "Botones de la Aplicación",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    fontFamily = FontFamily.Serif
                )
            )
            // Add Book Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Agregar Libro: Abre un formulario para crear un nuevo libro, donde puedes ingresar el nombre, precio, autor y una imagen.",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
            // Settings Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Configuración: Accede a las preferencias para personalizar el color de fondo de las tarjetas y de la aplicación.",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
            // Help Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ayuda: Abre esta pantalla para ver instrucciones sobre cómo usar la aplicación y el propósito de cada botón.",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
            // Edit Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Editar Libro: Abre el formulario de un libro existente para modificar su nombre, precio, autor o imagen.",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
            // Delete Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Eliminar Libro: Muestra un diálogo para confirmar la eliminación de un libro de la lista.",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
            // Back Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Volver: Regresa a la pantalla anterior desde los formularios, preferencias o esta pantalla de ayuda.",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
            // Add Image Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Agregar Imagen: Abre la cámara para capturar una imagen del libro en el formulario de creación o edición.",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
            // Save Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Guardar: Guarda un libro nuevo o los cambios en un libro existente después de completar el formulario.",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibroFormUI(navControlador: NavController, bookId: Int, appBackgroundColor: Color, onSave: () -> Unit = {}) {
    val context = LocalContext.current
    val operaciones = LibroOperaciones(context)

    var nombreLibro by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var libroImg by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showEmptyFieldsDialog by remember { mutableStateOf(false) }
    var emptyFieldsMessage by remember { mutableStateOf("") }
    var showSaveSuccessDialog by remember { mutableStateOf(false) }
    var showUpdateSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(bookId) {
        if (bookId != 0) {
            operaciones.obtenerLibroPorId(bookId)?.let { libro ->
                nombreLibro = libro.nombre_libro
                precio = libro.precio.toString()
                descripcion = libro.descripcion
                libroImg = libro.libro_img
                try {
                    imageBitmap = ParseImg.convertBase64ToBitmap(libro.libro_img)
                } catch (e: Exception) {
                    println("Error loading image: ${e.message}")
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && imageUri != null) {
            try {
                context.contentResolver.openInputStream(imageUri!!)?.use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageBitmap = bitmap
                    val thumbnailBitmap = resizeToThumbnail(bitmap, 150, 150)
                    val outputStream = ByteArrayOutputStream()
                    thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    val byteArray = outputStream.toByteArray()
                    libroImg = Base64.encodeToString(byteArray, Base64.DEFAULT)
                }
            } catch (e: Exception) {
                println("Error loading image: ${e.message}")
            }
        }
    }

    val createImageUri: () -> Uri? = {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "book_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    // Empty Fields Alert Dialog
    if (showEmptyFieldsDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyFieldsDialog = false },
            title = { Text("Campos Obligatorios") },
            text = {
                Text(
                    emptyFieldsMessage,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { showEmptyFieldsDialog = false }) {
                    Text(
                        "Aceptar",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Serif
                        )
                    )
                }
            }
        )
    }

    // Save Success Dialog
    if (showSaveSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSaveSuccessDialog = false
                navControlador.popBackStack()
            },
            title = { Text("Guardado Exitoso") },
            text = {
                Text(
                    "Libro guardado exitosamente.",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showSaveSuccessDialog = false
                    navControlador.popBackStack()
                }) {
                    Text(
                        "Aceptar",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Serif
                        )
                    )
                }
            }
        )
    }

    // Update Success Dialog
    if (showUpdateSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showUpdateSuccessDialog = false
                navControlador.popBackStack()
            },
            title = { Text("Actualización Exitosa") },
            text = {
                Text(
                    "Libro actualizado exitosamente.",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showUpdateSuccessDialog = false
                    navControlador.popBackStack()
                }) {
                    Text(
                        "Aceptar",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Serif
                        )
                    )
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (bookId == 0) "Agregar libro" else "Editar libro",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = FontFamily.Serif
                        )
                    )
                },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nombreLibro,
                onValueChange = { nombreLibro = it },
                label = {
                    Text(
                        "Nombre del libro",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Thin,
                            color = Color.Gray,
                            fontFamily = FontFamily.Serif
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = precio,
                onValueChange = { newValue ->
                    // Allow empty input, digits, one decimal point, and exactly two decimal places
                    if (newValue.isEmpty() || newValue.matches(Regex("\\d*\\.?\\d{0,2}"))) {
                        precio = newValue
                    }
                },
                label = {
                    Text(
                        "Precio",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Thin,
                            color = Color.Gray,
                            fontFamily = FontFamily.Serif
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = {
                    Text(
                        "Autor",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Thin,
                            color = Color.Gray,
                            fontFamily = FontFamily.Serif
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            imageBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Captured Image Preview",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(bottom = 8.dp)
                )
            }
            Button(
                onClick = {
                    imageUri = createImageUri()
                    if (imageUri != null) {
                        cameraLauncher.launch(imageUri!!)
                    } else {
                        println("Failed to create image Uri")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD0E3F5),
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Image",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "Agregar Imagen",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Thin,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
            Button(
                onClick = {
                    // Check for empty fields
                    val emptyFields = mutableListOf<String>()
                    if (nombreLibro.isBlank()) emptyFields.add("Nombre del libro")
                    if (precio.isBlank()) emptyFields.add("Precio")
                    if (descripcion.isBlank()) emptyFields.add("Autor")
                    if (libroImg.isBlank()) emptyFields.add("Imagen")

                    if (emptyFields.isNotEmpty()) {
                        emptyFieldsMessage = "Los siguientes campos son obligatorios: ${emptyFields.joinToString(", ")}"
                        showEmptyFieldsDialog = true
                    } else {
                        val libro = Libro(
                            id_libro = if (bookId == 0) 0 else bookId,
                            nombre_libro = nombreLibro,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            descripcion = descripcion,
                            libro_img = libroImg
                        )
                        if (bookId == 0) {
                            operaciones.insertarLibro(libro)
                            showSaveSuccessDialog = true
                        } else {
                            operaciones.actualizarLibro(libro)
                            showUpdateSuccessDialog = true
                        }
                        onSave()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD0E3F5),
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Save",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "Guardar",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Thin,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenciasUI(
    navControlador: NavController,
    cardColor: Color,
    appBackgroundColor: Color,
    onCardColorChange: (Color) -> Unit,
    onAppBackgroundColorChange: (Color) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Preferencias",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = FontFamily.Serif
                        )
                    )
                },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RGBColorPicker(
                title = "Color de Fondo de Tarjetas",
                initialColor = cardColor,
                onColorChange = onCardColorChange
            )
        }
    }
}

@Composable
fun RGBColorPicker(
    title: String,
    initialColor: Color,
    onColorChange: (Color) -> Unit
) {
    var red by remember { mutableStateOf(initialColor.red * 255f) }
    var green by remember { mutableStateOf(initialColor.green * 255f) }
    var blue by remember { mutableStateOf(initialColor.blue * 255f) }

    val color = Color(red / 255f, green / 255f, blue / 255f)

    // Update parent when color changes
    LaunchedEffect(color) {
        onColorChange(color)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = FontFamily.Serif
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        ColorSlider("Rojo", red) { red = it }
        ColorSlider("Verde", green) { green = it }
        ColorSlider("Azul", blue) { blue = it }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(color)
                .border(2.dp, Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "HEX: ${color.toHex()}",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = FontFamily.Serif
            )
        )
    }
}

@Composable
fun ColorSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Text(
            text = "$label: ${value.toInt()}",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                fontFamily = FontFamily.Serif
            )
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..255f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = when (label) {
                    "Rojo" -> Color.Red
                    "Verde" -> Color.Green
                    "Azul" -> Color.Blue
                    else -> Color.Gray
                }
            )
        )
    }
}

// Convertir Color a HEX
fun Color.toHex(): String {
    val r = (red * 255).toInt().coerceIn(0, 255)
    val g = (green * 255).toInt().coerceIn(0, 255)
    val b = (blue * 255).toInt().coerceIn(0, 255)
    return String.format("#%02X%02X%02X", r, g, b)
}

private fun resizeToThumbnail(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val aspectRatio = width.toFloat() / height.toFloat()
    val newWidth: Int
    val newHeight: Int
    if (width > height) {
        newWidth = maxWidth
        newHeight = (maxWidth / aspectRatio).toInt()
    } else {
        newHeight = maxHeight
        newWidth = (maxHeight * aspectRatio).toInt()
    }
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}