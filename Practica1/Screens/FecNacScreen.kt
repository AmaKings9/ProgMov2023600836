package com.example.practica1_3compose.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.practica1_3compose.ui.components.NewMenu
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/* Pantalla principal de la aplicacion */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FecNacScreen(navController: NavController) {
    Scaffold( modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = { Text(text = "Práctica 1: Fecha Nacimiento") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atras")
                }
            }
        )
    }) {
        var txt_fecha_nac by remember { mutableStateOf("") }
        var txt_res by remember { mutableStateOf("") }
        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        // Función para mostrar el selector de fecha
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                txt_fecha_nac = "$day/${month + 1}/$year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )


        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Row (Modifier.fillMaxWidth().padding(20.dp)) {
                Text(text = "Fecha de nacimiento:", Modifier.padding(10.dp))
                Button(onClick = { datePickerDialog.show() }) {
                    Text(text = if (txt_fecha_nac.isEmpty()) "Seleccionar fecha" else "Fecha: $txt_fecha_nac")
                }
            }

            Button(onClick = {
                txt_res = calcularTiempoTranscurrido(txt_fecha_nac)
            }) {
                Text(text = "Calcular")
            }
            Row (Modifier.fillMaxWidth().padding(20.dp)){
                Text(text = "Han transcurrido:", Modifier.padding(10.dp))
                Text(text = txt_res, Modifier.padding(10.dp))
            }
        }
    }
}

fun calcularTiempoTranscurrido(fecha_nac: String): String {

    if (fecha_nac.isEmpty()){
        return "Seleccione una fecha válida"
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaNacObj = dateFormat.parse(fecha_nac) ?: return "Fecha inválida"

    val actual = Calendar.getInstance().time
    val diferencia = actual.time - fechaNacObj.time
    if (diferencia < 0){
        return "Fecha inválida"
    }

    val segundos = diferencia / 1000
    val minutos = segundos / 60
    val horas = minutos / 60
    val dias = horas / 24
    val semanas = dias / 7
    val meses = dias / 30  // Aproximado, no considera años bisiestos.


    return "$meses meses, $semanas semanas, $dias días, $horas hrs, $minutos min, $segundos seg."
}
