package com.example.tiendalibros

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(private val context: Context) : SQLiteOpenHelper(context, "Libros.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create the table
        val createTableQuery = """
            CREATE TABLE Libros (
                id_libro INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_libro TEXT NOT NULL,
                precio DOUBLE NOT NULL,
                descricion TEXT NOT NULL,
                libro_img TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        // Insert data using ContentValues
        insertInitialData(db)
    }


    private fun insertInitialData(db: SQLiteDatabase) {
        // Use the context passed to the constructor
        val base64ImageCadaver = ParseImg.convertDrawableToBase64(context, R.drawable.cadaver_exquisito)
        val base64ImageCreatividad = ParseImg.convertDrawableToBase64(context, R.drawable.creativida)
        val base64ImageDorian = ParseImg.convertDrawableToBase64(context, R.drawable.doria_gray)
        val base64ImageHarry = ParseImg.convertDrawableToBase64(context, R.drawable.harry_potter)
        val base64ImageHunger = ParseImg.convertDrawableToBase64(context, R.drawable.hunger_games)
        val base64ImageInnovadores = ParseImg.convertDrawableToBase64(context, R.drawable.innovadores)
        val base64ImageKaramazov = ParseImg.convertDrawableToBase64(context, R.drawable.karamazov)
        val base64ImageQuijote = ParseImg.convertDrawableToBase64(context, R.drawable.quijote)
        val base64ImageSentido = ParseImg.convertDrawableToBase64(context, R.drawable.sentido)
        val base64ImageIt = ParseImg.convertDrawableToBase64(context, R.drawable.it)

        // List of products to insert
        val products = listOf(
            arrayOf("Cadáver Exquisito", 180.00, "Agustina Bazterrica", base64ImageCadaver),
            arrayOf("Creatividad,S.A.", 480.00, "Ed Catmull", base64ImageCreatividad),
            arrayOf("El retrato de Dorian Gray", 430.00, "Oscar Wilde", base64ImageDorian),
            arrayOf("Harry Potter y la piedra filosofal", 500.00, "JK Rowling", base64ImageHarry),
            arrayOf("Los Juegos del Hambre", 300.00, "Suzanne Collins", base64ImageHunger),
            arrayOf("Los innovadores", 470.00, "Walter Isaacson", base64ImageInnovadores),
            arrayOf("Los Hermanos Karamazov", 330.00, "Fiodor Dovstoyevsky", base64ImageKaramazov),
            arrayOf("Don Quijote de la Mancha", 780.00, "Miguel de Cervantes", base64ImageQuijote),
            arrayOf("El hombre en busca de sentido", 280.00, "Viktor Frankl", base64ImageSentido),
            arrayOf("It", 450.00, "Stephen King", base64ImageIt)
        )

        // Insert each product
        products.forEach { product ->
            val values = ContentValues().apply {
                put("nombre_libro", product[0] as String)
                put("precio", product[1] as Double)
                put("descricion", product[2] as String)
                put("libro_img", product[3] as String)
            }
            db.insert("Libros", null, values)
        }


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Libros")
        onCreate(db!!)
    }
}