package com.example.firstyearapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale


class ActivityRegistros : AppCompatActivity() {

    private val db = Firebase.firestore
    private var botonSeleccionado: ImageView? = null //Variable apra ver qué botón está selccionado
    private var documentoSeleccionadoId: String? = null // Variable para almacenar el dato seleccionado
    private var coleccionSeleccionada: String? = null //Variable apra almacenar la colección seleccionada
    private var idCampo: String? = null //variable para almacenar id
    private lateinit var tableLayout: TableLayout
    private lateinit var btnRegPecho: ImageView
    private lateinit var btnRegPanal: ImageView
    private lateinit var btnRegSueno: ImageView
    private lateinit var btnRegBibe: ImageView
    private lateinit var btnRegDes: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registros)

        // Botones de las categorías
        btnRegPecho = findViewById(R.id.btnRPecho)
        btnRegPanal = findViewById(R.id.btnRPanal)
        btnRegSueno = findViewById(R.id.btnRSueno)
        btnRegBibe = findViewById(R.id.btnRBibe)
        btnRegDes = findViewById(R.id.btnRDes)

        tableLayout = findViewById(R.id.layoutListadoRegistros)
        // Recibir el bebeId y cuidadorId de la otra activity
        val bebeId = intent.getStringExtra("bebeId")?: return //no puede ser nulo
        val cuidadorId = intent.getStringExtra("cuidadorId")
        val relacionId = intent.getStringExtra("relacion_id")

        //Programación botón atrás
        val botonAtras11: ImageView = findViewById(R.id.btnAtras11)

        botonAtras11.setOnClickListener {
            val intent = Intent(this, activity_selecciona::class.java)
            intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
            intent.putExtra("cuidadorId", cuidadorId) //mandar el cuidadro con el que habíamos entrado en esta activity.
            intent.putExtra("relacion_id", relacionId)
            startActivity(intent)
        }

        //Botones y función para mostrar los datos en la tabla
        findViewById<ImageView>(R.id.btnRPecho).setOnClickListener {
            mostrarDatosPecho(bebeId)
            coleccionSeleccionada = "alimentacion_LM"
            idCampo = "lmId"

            // Selección botón LM
            btnRegPecho.isSelected = true
            btnRegPecho.setBackgroundResource(R.drawable.imagen_borde)
            // Quitar selección del botón Panal
            btnRegPanal.isSelected = false
            btnRegPanal.setBackgroundResource(0)
            // Quitar selección del botón Sueno
            btnRegSueno.isSelected = false
            btnRegSueno.setBackgroundResource(0)
            // Quitar selección del botón Bibe
            btnRegBibe.isSelected = false
            btnRegBibe.setBackgroundResource(0)
            // Quitar selección del botón Desarrollo
            btnRegDes.isSelected = false
            btnRegDes.setBackgroundResource(0)
        }

        findViewById<ImageView>(R.id.btnRPanal).setOnClickListener {
            mostrarDatosPanal(bebeId)
            coleccionSeleccionada = "cambio_panal"
            idCampo = "panalId"

            // Selección botón pañal
            btnRegPanal.isSelected = true
            btnRegPanal.setBackgroundResource(R.drawable.imagen_borde)
            // Quitar selección del botón LM
            btnRegPecho.isSelected = false
            btnRegPecho.setBackgroundResource(0)
            // Quitar selección del botón Sueno
            btnRegSueno.isSelected = false
            btnRegSueno.setBackgroundResource(0)
            // Quitar selección del botón Bibe
            btnRegBibe.isSelected = false
            btnRegBibe.setBackgroundResource(0)
            // Quitar selección del botón Desarrollo
            btnRegDes.isSelected = false
            btnRegDes.setBackgroundResource(0)
        }

        findViewById<ImageView>(R.id.btnRSueno).setOnClickListener {
            mostrarDatosSueno(bebeId)
            coleccionSeleccionada = "sueno"
            idCampo = "suenoId"

            // Selección botón Sueno
            btnRegSueno.isSelected = true
            btnRegSueno.setBackgroundResource(R.drawable.imagen_borde)
            // Quitar selección del botón LM
            btnRegPecho.isSelected = false
            btnRegPecho.setBackgroundResource(0)
            // Quitar selección del botón pañal
            btnRegPanal.isSelected = false
            btnRegPanal.setBackgroundResource(0)
            // Quitar selección del botón Bibe
            btnRegBibe.isSelected = false
            btnRegBibe.setBackgroundResource(0)
            // Quitar selección del botón Desarrollo
            btnRegDes.isSelected = false
            btnRegDes.setBackgroundResource(0)
        }

        findViewById<ImageView>(R.id.btnRBibe).setOnClickListener {
            mostrarDatosBibe(bebeId)
            coleccionSeleccionada = "alimentacion_biberon"
            idCampo = "bibeId"

            // Selección botón bibe
            btnRegBibe.isSelected = true
            btnRegBibe.setBackgroundResource(R.drawable.imagen_borde)
            // Quitar selección del botón LM
            btnRegPecho.isSelected = false
            btnRegPecho.setBackgroundResource(0)
            // Quitar selección del botón panal
            btnRegPanal.isSelected = false
            btnRegPanal.setBackgroundResource(0)
            // Quitar selección del botón Sueno
            btnRegSueno.isSelected = false
            btnRegSueno.setBackgroundResource(0)
            // Quitar selección del botón Desarrollo
            btnRegDes.isSelected = false
            btnRegDes.setBackgroundResource(0)
        }

        findViewById<ImageView>(R.id.btnRDes).setOnClickListener {
            mostrarDatosDesarrollo(bebeId)
            coleccionSeleccionada = "hitos_desarrollo"
            idCampo = "desId"

            // Selección botón Desarrollo
            btnRegDes.isSelected = true
            btnRegDes.setBackgroundResource(R.drawable.imagen_borde)
            // Quitar selección del botón LM
            btnRegPecho.isSelected = false
            btnRegPecho.setBackgroundResource(0)
            // Quitar selección del botón panal
            btnRegPanal.isSelected = false
            btnRegPanal.setBackgroundResource(0)
            // Quitar selección del botón Bibe
            btnRegSueno.isSelected = false
            btnRegSueno.setBackgroundResource(0)
            // Quitar selección del botón Sueno
            btnRegBibe.isSelected = false
            btnRegBibe.setBackgroundResource(0)
        }
        //Botón para eliminar los datos
        findViewById<Button>(R.id.btnEliminar).setOnClickListener {
            if (documentoSeleccionadoId != null && coleccionSeleccionada != null && idCampo != null) {
                db.collection(coleccionSeleccionada!!).document(documentoSeleccionadoId!!).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show()
                        actualizarVista()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Por favor, selecciona un registro para eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //Función para calcular la duracción de la toma
    private fun calcularDuracion(fechaInicio: String, fechaTermina: String): Long {
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return try {
            val inicio = formato.parse(fechaInicio)
            val fin = formato.parse(fechaTermina)
            if (inicio != null && fin != null) (fin.time - inicio.time) / 60000 else 0L
        } catch (e: Exception) {
            Log.e("ParseError", "Error con las fechas", e)
            0L
        }
    }

    // Función para agregar encabezado a la tabla
    private fun agregarEncabezado(vararg encabezados: String) {
        val encabezadoRow = TableRow(this)
        encabezados.forEach { encabezado ->
            val textView = TextView(this).apply {
                text = encabezado
                gravity = Gravity.CENTER
                setTypeface(null, android.graphics.Typeface.BOLD) // Encabezado en negrita
            }
            encabezadoRow.addView(textView)
        }
        tableLayout.addView(encabezadoRow)
    }


    //Función para mostrar lod datos del pecho
    private fun mostrarDatosPecho(bebeId: String) {
        limpiarVistaRegistros() //Borrar los datos anteriores
        agregarEncabezado("Tipo", "Fecha y hora", "Duración", "Coment.") // Encabezado
        db.collection("alimentacion_LM")
            .whereEqualTo("bebeId", bebeId)
            .get()
            .addOnSuccessListener { documentos ->
                for (documento in documentos) {
                    val tipoPecho = documento.getString("tipoPecho") ?: ""
                    val fechaInicio = documento.getString("fechaInicio") ?: ""
                    val fechaTermina = documento.getString("fechaTermina") ?: ""
                    val comentario = documento.getString("comentario") ?: ""
                    val duracion = calcularDuracion(fechaInicio, fechaTermina)
                    val documentoId = documento.id
                    agregarRegistroEnVista(tipoPecho, fechaInicio, "$duracion mins", comentario, documentoId)
                }
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreError", "Error al obtener datos de alimentación LM", e)
            }
    }

    //Función para mostrar lod datos del pañal
    private fun mostrarDatosPanal(bebeId: String) {
        limpiarVistaRegistros() //Borrar los datos anteriores
        agregarEncabezado("Tipo", "Fecha y hora", "Coment.") // Encabezado
        db.collection("cambio_panal")
            .whereEqualTo("bebeId", bebeId)
            .get()
            .addOnSuccessListener { documentos ->
                for (documento in documentos) {
                    val tipopanal = documento.getString("tipopanal") ?: ""
                    val fechaInicio = documento.getString("fechaInicio") ?: ""
                    val comentario = documento.getString("comentario") ?: ""
                    val documentoId = documento.id
                    agregarRegistroEnVista(tipopanal, fechaInicio, comentario, null,  documentoId)
                }
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreError", "Error al obtener los cambios de pañal", e)
            }
    }

    //Función para mostrar lod datos del sueño
    private fun mostrarDatosSueno(bebeId: String) {
        limpiarVistaRegistros() //Borrar los datos anteriores
        agregarEncabezado("Fecha y hora", "Duración", "Coment.") // Encabezado
        db.collection("sueno")
            .whereEqualTo("bebeId", bebeId)
            .get()
            .addOnSuccessListener { documentos ->
                for (documento in documentos) {
                    val fechaInicio = documento.getString("fechaInicio") ?: ""
                    val fechaTermina = documento.getString("fechaTermina") ?: ""
                    val comentario = documento.getString("comentario") ?: ""
                    val duracion = calcularDuracion(fechaInicio, fechaTermina)
                    val documentoId = documento.id
                    agregarRegistroEnVista(fechaInicio, "$duracion mins", comentario, null, documentoId)
                }
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreError", "Error al obtener datos de sueño", e)
            }
    }

    //Función para mostrar lod datos del biberón
    private fun mostrarDatosBibe(bebeId: String) {
        limpiarVistaRegistros() //Borrar los datos anteriores
        agregarEncabezado("Tipo", "Fecha y hora", "Cant.", "Coment.") // Encabezado
        db.collection("alimentacion_biberon")
            .whereEqualTo("bebeId", bebeId)
            .get()
            .addOnSuccessListener { documentos ->
                for (documento in documentos) {
                    val tipoAlimento = documento.getString("tipoAlimento") ?: ""
                    val fechaInicio = documento.getString("fechaInicio") ?: ""
                    val cantidad = documento.getString("cantidad") ?: ""
                    val comentario = documento.getString("comentario") ?: ""
                    val documentoId = documento.id
                    agregarRegistroEnVista(tipoAlimento, fechaInicio, cantidad, comentario, documentoId)
                }
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreError", "Error al obtener datos de alimentación biberón", e)
            }
    }

    //Función para mostrar lod datos del desarrollo
    private fun mostrarDatosDesarrollo(bebeId: String) {
        limpiarVistaRegistros() //Borrar los datos anteriores
        agregarEncabezado("Hito", "Fecha ", "Coment.") // Encabezado
        db.collection("hitos_desarrollo")
            .whereEqualTo("bebeId", bebeId)
            .get()
            .addOnSuccessListener { documentos ->
                for (documento in documentos) {
                    val hitoCumpl = documento.getString("hitoCumpl") ?: ""
                    val fechaInicio = documento.getString("fechaInicio") ?: ""
                    val comentario = documento.getString("comentario") ?: ""
                    val documentoId = documento.id
                    agregarRegistroEnVista(hitoCumpl, fechaInicio, comentario, null,  documentoId)
                }
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreError", "Error al obtener los hitos de desarrollo", e)
            }
    }



    //Función apra actualizar la vista después de borrar datos.
    private fun actualizarVista() {
        when (coleccionSeleccionada) {
            "alimentacion_LM" -> mostrarDatosPecho(intent.getStringExtra("bebeId") ?: "")
            "cambio_panal" -> mostrarDatosPanal(intent.getStringExtra("bebeId") ?: "")
            "sueno" -> mostrarDatosSueno(intent.getStringExtra("bebeId") ?: "")
            "alimentacion_biberon" -> mostrarDatosBibe(intent.getStringExtra("bebeId") ?: "")
            "hitos_desarrollo" -> mostrarDatosDesarrollo(intent.getStringExtra("bebeId") ?: "")
        }
    }

    //Función para borrar los datos anteriores
    private fun limpiarVistaRegistros() {
        tableLayout.removeAllViews()
    }

    //Función para añadir los datos en la tabla
    private fun agregarRegistroEnVista(campo1: String, campo2: String, campo3: String, campo4: String? = null, documentoId: String) {
        val tableRow = TableRow(this)

        val text1 = TextView(this).apply {
            text = campo1
            gravity = Gravity.CENTER
        }
        tableRow.addView(text1)

        val text2 = TextView(this).apply {
            text = campo2
            gravity = Gravity.CENTER
        }
        tableRow.addView(text2)

        // Agrega el 3º y 4º campo solo si tienen un dato
        if (campo3 != null) {
            val text3 = TextView(this).apply {
                text = campo3
                gravity = Gravity.CENTER
            }
            tableRow.addView(text3)
        }

        if (campo4 != null) {
            val text4 = TextView(this).apply {
                text = campo4
                gravity = Gravity.CENTER
            }
            tableRow.addView(text4)
        }

        // Asignar ID de documento al hacer clic en la fila
        tableRow.setOnClickListener {
            documentoSeleccionadoId = documentoId
            // Marcar visualmente la fila seleccionada (opcional)
            tableRow.setBackgroundColor(resources.getColor(R.color.fondo_claro))
        }

        tableLayout.addView(tableRow)
    }
}