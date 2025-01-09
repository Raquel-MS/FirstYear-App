package com.example.firstyearapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class activityLM : AppCompatActivity() {


    private lateinit var btnPIzq: ImageView
    private lateinit var btnPDer: ImageView
    private lateinit var comentLM: EditText
    private var etSelec: EditText? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lm)

        // Declaración Pechos
        btnPIzq = findViewById(R.id.btnIzq)
        btnPDer = findViewById(R.id.btnDer)

        btnPIzq.setOnClickListener {
            // Selección botón Pecho izquierdo
            btnPIzq.isSelected = true
            btnPIzq.setBackgroundResource(R.drawable.imagen_borde)

            // Quitar selección del botón Pecho derecho
            btnPDer.isSelected = false
            btnPDer.setBackgroundResource(0)
        }

        btnPDer.setOnClickListener {
            // Selección botón Pecho derecho
            btnPDer.isSelected = true
            btnPDer.setBackgroundResource(R.drawable.imagen_borde)

            // Quitar selección del Pecho izquierdo
            btnPIzq.isSelected = false
            btnPIzq.setBackgroundResource(0)
        }

        // Variables para botones y EditText
        val btnEmpLM: Button = findViewById(R.id.btnEmpLM)
        val btnTermLM: Button = findViewById(R.id.btnTermLM)
        val empLM: EditText = findViewById(R.id.empLMFH)
        val termLM: EditText = findViewById(R.id.termLMFH)

        // Formato para la fecha y hora
        val formatoFechaH = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        // Evento al hacer clic en el botón Empezar
        btnEmpLM.setOnClickListener {
            val actualFH = formatoFechaH.format(Calendar.getInstance().time)
            empLM.setText(actualFH)
        }

        empLM.setOnClickListener {
            etSelec = empLM
            mostrarFechaHora()
        }

        // Evento al hacer clic en el botón Terminar
        btnTermLM.setOnClickListener {
            val actualFH = formatoFechaH.format(Calendar.getInstance().time)
            termLM.setText(actualFH)
        }

        termLM.setOnClickListener {
            etSelec = termLM
            mostrarFechaHora()
        }

        // Recibir el bebeId y cuidadorId de la otra activity
        val bebeId = intent.getStringExtra("bebeId")
        val cuidadorId = intent.getStringExtra("cuidadorId")
        val relacionId = intent.getStringExtra("relacion_id")

        //Programación botón atrás
        val botonAtras6: ImageView = findViewById(R.id.btnAtras6)

        botonAtras6.setOnClickListener {
            val intent = Intent(this, activity_selecciona::class.java)
            intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
            intent.putExtra("cuidadorId", cuidadorId) //mandar el cuidadro con el que habíamos entrado en esta activity.
            intent.putExtra("relacion_id", relacionId)
            startActivity(intent)
        }

        //Programación botón guardar
        val btnGLM: Button = findViewById(R.id.btnGuardarLM)
        comentLM = findViewById(R.id.campoLM)

        btnGLM.setOnClickListener {
            val tipoPecho = when {
                btnPIzq.isSelected -> "Izquierdo" // Pecho izquierdo
                btnPDer.isSelected -> "Derecho" // Pecho derecho
                else -> ""
            }
            val fechaInicio = empLM.text.toString()
            val fechaTermina = termLM.text.toString()
            val comentario = comentLM.text.toString()  //este dato es opcional
            val lmId = UUID.randomUUID().toString()     // Crea un ID para cada registro

            //Verificar que han completado todos los campos
            if (tipoPecho.isEmpty() || fechaInicio.isEmpty() || fechaTermina.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //Llamada a función para guardar
                guardarDatosLM(
                    context = this,
                    bebeId = bebeId,
                    lmId = lmId,
                    tipoPecho = tipoPecho,
                    fechaInicio = fechaInicio,
                    fechaTermina = fechaTermina,
                    comentario = comentario,
                    cuidadorId = cuidadorId,
                    relacionId = relacionId
                )
            }
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

    private fun guardarDatosLM(context: Context, bebeId: String?, lmId: String, tipoPecho: String, fechaInicio: String, fechaTermina: String, comentario: String, cuidadorId: String?, relacionId: String?){

    val datosLM = hashMapOf(
        "bebeId" to bebeId,
        "lmId" to lmId,
        "tipoPecho" to tipoPecho,
        "fechaInicio" to fechaInicio,
        "fechaTermina" to fechaTermina,
        "comentario" to comentario
    )

    db.collection("alimentacion_LM")
    .add(datosLM)
    .addOnSuccessListener {
        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()

        // Regresar a la actividad anterior
        val intent = Intent(context, activity_selecciona::class.java)
        intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
        intent.putExtra("cuidadorId", cuidadorId) //mandar el cuidadro con el que habíamos entrado en esta activity.
        intent.putExtra("relacion_id", relacionId)
        context.startActivity(intent)
    }
    .addOnFailureListener {e ->
        Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
}