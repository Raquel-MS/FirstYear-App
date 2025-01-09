package com.example.firstyearapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class activity_sueno : AppCompatActivity() {

    private var etSelec: EditText? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sueno)

        // Variables para botones y EditText
        val btnEmpSueno: Button = findViewById(R.id.btnEmpSueno)
        val btnTermSueno: Button = findViewById(R.id.btnTermSueno)
        val fHEmpSueno: EditText = findViewById(R.id.empSuenoFH)
        val fHTermSueno: EditText = findViewById(R.id.termSuenoFH)
        val campoSueno: EditText = findViewById(R.id.campoSueno)

        // Formato para la fecha y hora
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        // Evento al hacer clic en el botón Empezar
        btnEmpSueno.setOnClickListener {
            val actualFH = formatoFecha.format(Calendar.getInstance().time)
            fHEmpSueno.setText(actualFH)
        }

        fHEmpSueno.setOnClickListener {
            etSelec = fHEmpSueno
            mostrarFechaHora()
        }

        // Evento al hacer clic en el botón Terminar
        btnTermSueno.setOnClickListener {
            val actualFH = formatoFecha.format(Calendar.getInstance().time)
            fHTermSueno.setText(actualFH)
        }

        fHTermSueno.setOnClickListener {
            etSelec = fHTermSueno
            mostrarFechaHora()
        }

        // Recibir el bebeId y cuidadorId de la otra activity
        val bebeId = intent.getStringExtra("bebeId")
        val cuidadorId = intent.getStringExtra("cuidadorId")
        val relacionId = intent.getStringExtra("relacion_id")

        //Programación botón atrás
        val botonAtras8: ImageView = findViewById(R.id.btnAtras8)

        botonAtras8.setOnClickListener {
            val intent = Intent(this, activity_selecciona::class.java)
            intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
            intent.putExtra("cuidadorId", cuidadorId) //mandar el cuidadro con el que habíamos entrado en esta activity.
            intent.putExtra("relacion_id", relacionId)
            startActivity(intent)
        }

        //Programación botón guardar
        val btnGuardarSueno: Button = findViewById(R.id.btnGSueno)

        btnGuardarSueno.setOnClickListener {
            val fechaInicio = fHEmpSueno.text.toString()

            //Verificar que han seleccionado la fecha y hora
            if (fechaInicio.isEmpty()) {
                Toast.makeText(this, "Por favor, selecciona la fecha y hora de inicio.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fechaTermina = fHTermSueno.text.toString()

            //Verificar que han seleccionado la fecha y hora
            if (fechaTermina.isEmpty()) {
                Toast.makeText(this, "Por favor, selecciona la fecha y hora de finalización.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //este dato es opcional
            val comentario = campoSueno.text.toString()

            //Llamada función guardar datos
            guardarDatosSueno(bebeId, fechaInicio, fechaTermina, comentario, cuidadorId, relacionId)
        }
    }

    // Función para seleccionar fecha
    private fun mostrarFechaHora() {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        val seleccionarFecha = DatePickerDialog(
            this,
            { _, anio, mes, dia ->
                val fechaSeleccionada = "$dia/${mes + 1}/$anio"
                etSelec?.setText(fechaSeleccionada)
                mostrarHora() //llamada a la función selector de hora
            },
            year,
            month,
            day
        )
        seleccionarFecha.show()
    }

    // Función para mostrar el selector de hora
    private fun mostrarHora() {
        val calendario = Calendar.getInstance()
        val hour = calendario.get(Calendar.HOUR_OF_DAY)
        val minute = calendario.get(Calendar.MINUTE)

        val seleccionaHora = TimePickerDialog(
            this,
            { _, hora, minuto ->
                val horaSeleccionada = String.format("%02d:%02d", hora, minuto)
                etSelec?.append(" $horaSeleccionada")
            },
            hour,
            minute,
            true
        )
        seleccionaHora.show()
    }

    // Función guardar datos sueño
    private fun guardarDatosSueno(bebeId: String?, fechaInicio: String, fechaTermina: String, comentario: String, cuidadorId: String?, relacionId: String?) {
        val suenoId = UUID.randomUUID().toString() // Crea un ID para cada registro

        val datosSueno = hashMapOf(
            "bebeId" to bebeId,
            "suenoId" to suenoId,
            "fechaInicio" to fechaInicio,
            "fechaTermina" to fechaTermina,
            "comentario" to comentario
        )

        db.collection("sueno")
            .add(datosSueno)
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