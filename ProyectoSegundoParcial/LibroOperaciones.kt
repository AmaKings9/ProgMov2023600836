package com.example.tiendalibros

import android.content.ContentValues
import android.content.Context
import android.database.Cursor


class LibroOperaciones(context: Context) {
    private val dbHelper = DBHelper(context)

    fun obtenerLibros(): List<Libro> {
        val db = dbHelper.readableDatabase
        val libros = mutableListOf<Libro>()
        val cursor = db.rawQuery("SELECT * FROM Libros", null)
        if (cursor.moveToFirst()) {
            do {
                libros.add(
                    Libro(
                        id_libro = cursor.getInt(0),
                        nombre_libro = cursor.getString(1),
                        precio = cursor.getDouble(2),
                        descripcion = cursor.getString(3),
                        libro_img = cursor.getString(4)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return libros
    }

    fun insertarLibro(libro: Libro) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre_libro", libro.nombre_libro)
            put("precio", libro.precio)
            put("descricion", libro.descripcion)
            put("libro_img", libro.libro_img)
        }
        db.insert("Libros", null, values)
        db.close()
    }

    fun obtenerLibroPorId(id: Int): Libro? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Libros WHERE id_libro = ?", arrayOf(id.toString()))
        var libro: Libro? = null
        if (cursor.moveToFirst()) {
            libro = Libro(
                id_libro = cursor.getInt(0),
                nombre_libro = cursor.getString(1),
                precio = cursor.getDouble(2),
                descripcion = cursor.getString(3),
                libro_img = cursor.getString(4)
            )
        }
        cursor.close()
        db.close()
        return libro
    }

    fun actualizarLibro(libro: Libro) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre_libro", libro.nombre_libro)
            put("precio", libro.precio)
            put("descricion", libro.descripcion)
            put("libro_img", libro.libro_img)
        }
        db.update("Libros", values, "id_libro = ?", arrayOf(libro.id_libro.toString()))
        db.close()
    }

    fun eliminarLibro(id: Int) {
        val db = dbHelper.writableDatabase
        db.delete("Libros", "id_libro = ?", arrayOf(id.toString()))
        db.close()
    }
}