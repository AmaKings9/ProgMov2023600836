package com.example.tiendalibros

import android.content.Context
import android.database.Cursor

class LibroOperaciones(context: Context) {
    private val dbHelper = DBHelper(context)

    fun obtenerLibros(): List<Libro> {
        val libros = mutableListOf<Libro>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM Libros", null)

        if (cursor.moveToFirst()) {
            do {
                val libro = Libro(
                    id_libro = cursor.getInt(0),
                    nombre_libro = cursor.getString(1),
                    precio = cursor.getDouble(2),
                    descripcion = cursor.getString(3),
                    libro_img = cursor.getString(4)
                )
                libros.add(libro)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return libros
    }
}