package com.example.firstyearapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.DatePickerDialog
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class activity_des : AppCompatActivity() {

    private lateinit var fSDes: EditText
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_des)
        fSDes = findViewById(R.id.fHito)
        fSDes.setOnClickListener {
            mostrarFecha()
        }
        val hitoCumplido: EditText = findViewById(R.id.campoHitoCumplido)
        val campoDes: EditText = findViewById(R.id.campoDes)

        // Recibir el bebeId y cuidadorId de la otra activity
        val bebeId = intent.getStringExtra("bebeId")
        val cuidadorId = intent.getStringExtra("cuidadorId")
        val relacionId = intent.getStringExtra("relacion_id")

        //Programación botón atrás
        val botonAtras10: ImageView = findViewById(R.id.btnAtras10)

        botonAtras10.setOnClickListener {
            val intent = Intent(this, activity_selecciona::class.java)
            intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
            intent.putExtra("cuidadorId", cuidadorId) //mandar el cuidadro con el que habíamos entrado en esta activity.
            intent.putExtra("relacion_id", relacionId)
            startActivity(intent)
        }

        //Programación botón guardar
        val btnGuardarDes: Button = findViewById(R.id.btnGuardarDes)

        btnGuardarDes.setOnClickListener {
            //Llamada a función para guardar datos desarrollo
            guardarDatosHito(bebeId, cuidadorId, hitoCumplido, campoDes, relacionId)
        }
    }

    // Función para seleccionar fecha
    private fun mostrarFecha() {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        val seleccionarFecha = DatePickerDialog(
            this,
            { _, anio, mes, dia ->
                val fechaSeleccionada = "$dia/${mes + 1}/$anio"
                fSDes.setText(fechaSeleccionada)
            },
            year,
            month,
            day
        )
        seleccionarFecha.show()

    }

    // Función para guardar datos desarrollo
    private fun guardarDatosHito(bebeId: String?, cuidadorId: String?, hitoCumplido: EditText, campoDes: EditText, relacionId: String?) {

        val hitoCumpl = hitoCumplido.text.toString()

        //Verificar que han ingresado el hito cumplido
        if (hitoCumpl.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa el hito cumplido.", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaInicio = fSDes.text.toString()

        //Verificar que han seleccionado la fecha
        if (fechaInicio.isEmpty()) {
            Toast.makeText(this, "Por favor, selecciona la fecha.", Toast.LENGTH_SHORT).show()
            return
        }

        //este dato es opcional
        val comentario = campoDes.text.toString()
        val desId = UUID.randomUUID().toString() // Crea un ID para cada registro

        val datosDes = hashMapOf(
            "bebeId" to bebeId,
            "desId" to desId,
            "hitoCumpl" to hitoCumpl,
            "fechaInicio" to fechaInicio,
            "comentario" to comentario
        )

        db.collection("hitos_desarrollo")
            .add(datosDes)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()

                // Regresar a la actividad anterior
                val intent = Intent(this, activity_selecciona::class.java)
                intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
                intent.putExtra("cuidadorId", cuidadorId) //mandar el cuidadro con el que habíamos entrado en esta activity.
                intent.putExtra("relacion_id", relacionId)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
