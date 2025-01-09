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

class activity_panal : AppCompatActivity() {


    private lateinit var btnSPis: ImageView
    private lateinit var btnSCaca: ImageView
    private lateinit var panalS: EditText
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panal)

        // Declaración botones
        btnSPis = findViewById(R.id.btnPis)
        btnSCaca = findViewById(R.id.btnCaca)
        panalS = findViewById(R.id.panalFH)
        val campoPanal: EditText = findViewById(R.id.campoPanal)

        btnSPis.setOnClickListener {
            // Condicional si está seleccionada o no
            btnSPis.isSelected = !btnSPis.isSelected
            if (btnSPis.isSelected) {
                btnSPis.setBackgroundResource(R.drawable.imagen_borde)
            } else {
                btnSPis.setBackgroundResource(0)
            }
        }
        btnSCaca.setOnClickListener {
            // Condicional si está seleccionada o no
            btnSCaca.isSelected = !btnSCaca.isSelected
            if (btnSCaca.isSelected) {
                btnSCaca.setBackgroundResource(R.drawable.imagen_borde)
            } else {
                btnSCaca.setBackgroundResource(0)
            }
        }

        // Formato para la fecha y hora
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        val btnFH: Button = findViewById(R.id.btnEmpPanal)
        // Evento al hacer clic en el botón Fecha y hora
        btnFH.setOnClickListener {
            val actualFH = formatoFecha.format(Calendar.getInstance().time)
            panalS.setText(actualFH)
        }
        panalS.setOnClickListener {
           mostrarFechaHora()
        }

        // Recibir el bebeId y cuidadorId de la otra activity
        val bebeId = intent.getStringExtra("bebeId")
        val cuidadorId = intent.getStringExtra("cuidadorId")
        val relacionId = intent.getStringExtra("relacion_id")

        //Programación botón atrás
        val botonAtras7: ImageView = findViewById(R.id.btnAtras7)

        botonAtras7.setOnClickListener {
            val intent = Intent(this, activity_selecciona::class.java)
            intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
            intent.putExtra("cuidadorId", cuidadorId) //mandar el cuidadro con el que habíamos entrado en esta activity.
            intent.putExtra("relacion_id", relacionId)
            startActivity(intent)
        }

        //Programación botón guardar
        val btnGPanal: Button = findViewById(R.id.btnGuardarPanal)

        btnGPanal.setOnClickListener {
            //Función para guardar pañal
            guardarDatosPanal(bebeId, cuidadorId, campoPanal, relacionId)
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
                panalS.setText(fechaSeleccionada)
                mostrarHora(fechaSeleccionada) //llamada a la función selector de hora
            },
            year,
            month,
            day
        )
        seleccionarFecha.show()
    }

    // Función para mostrar el selector de hora
    private fun mostrarHora(fechaSeleccionada: String) {
        val calendario = Calendar.getInstance()
        val hour = calendario.get(Calendar.HOUR_OF_DAY)
        val minute = calendario.get(Calendar.MINUTE)

        val seleccionaHora = TimePickerDialog(
            this,
            { _, hora, minuto ->
                val horaSeleccionada = String.format("%02d:%02d", hora, minuto)
                panalS.setText("$fechaSeleccionada $horaSeleccionada")
            },
            hour,
            minute,
            true
        )
        seleccionaHora.show()
    }

    //Función para guardar los datos
    private fun guardarDatosPanal(bebeId: String?, cuidadorId: String?, campoPanal: EditText, relacionId: String?) {
    val tipoPanal = when {
        btnSPis.isSelected && btnSCaca.isSelected -> "pis y caca" // pis y caca
        btnSPis.isSelected -> "pis" // pis
        btnSCaca.isSelected -> "caca" // caca
        else -> ""
    }

    //Verificar que han seleccionado el tipo de pañal que han cambiado
    if (tipoPanal.isEmpty()) {
        Toast.makeText(this, "Por favor, selecciona el tipo de pañal cambiado.", Toast.LENGTH_SHORT).show()
        return
    }

    val fechaInicio = panalS.text.toString()

    //Verificar que han seleccionado la fecha y hora
    if (fechaInicio.isEmpty()) {
        Toast.makeText(this, "Por favor, selecciona la fecha y hora de inicio.", Toast.LENGTH_SHORT).show()
        return
    }

    //este dato es opcional
    val comentario = campoPanal.text.toString()
    val panalId = UUID.randomUUID().toString() // Crea un ID para cada registro

    val datosPanal = hashMapOf(
        "bebeId" to bebeId,
        "panalId" to panalId,
        "tipopanal" to tipoPanal,
        "fechaInicio" to fechaInicio,
        "comentario" to comentario
    )

    db.collection("cambio_panal")
    .add(datosPanal)
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
        Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
    }
  }
}