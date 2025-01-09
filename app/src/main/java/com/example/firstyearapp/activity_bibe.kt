package com.example.firstyearapp

import android.annotation.SuppressLint
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

class activity_bibe : AppCompatActivity() {


    private lateinit var btnSLM: ImageView
    private lateinit var btnSForm: ImageView
    private var etSelec: EditText? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bibe)

        // Declaración alimentos
        btnSLM = findViewById(R.id.btnLecheM)
        btnSForm = findViewById(R.id.btnForm)

        btnSLM.setOnClickListener {
            // Selección botón Lactancia materna
            btnSLM.isSelected = true
            btnSLM.setBackgroundResource(R.drawable.imagen_borde)
            // Quitar selección del botón Fórmula
            btnSForm.isSelected = false
            btnSForm.setBackgroundResource(0)
        }

        btnSForm.setOnClickListener {
            // Selección botón Fórmula
            btnSForm.isSelected = true
            btnSForm.setBackgroundResource(R.drawable.imagen_borde)
            // Quitar selección de la Lactancia materna
            btnSLM.isSelected = false
            btnSLM.setBackgroundResource(0)
        }

        // Variables para botones y EditText
        val btnEmpLM: Button = findViewById(R.id.btnEmpBibe)
        val btnTermLM: Button = findViewById(R.id.btnTermBibe)
        val empBibeFH: EditText = findViewById(R.id.empBibeFH)
        val termBibeFH: EditText = findViewById(R.id.termBibeFH)
        val campoCant: EditText = findViewById(R.id.campoCant)
        val campoBibe: EditText = findViewById(R.id.campoBibe)

        // Formato para la fecha y hora
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        // Evento al hacer clic en el botón Empezar
        btnEmpLM.setOnClickListener {
            val actualFH = formatoFecha.format(Calendar.getInstance().time)
            empBibeFH.setText(actualFH)
        }

        empBibeFH.setOnClickListener {
            etSelec = empBibeFH
            mostrarFechaHora()
        }

        // Evento al hacer clic en el botón Terminar
        btnTermLM.setOnClickListener {
            val actualFH = formatoFecha.format(Calendar.getInstance().time)
            termBibeFH.setText(actualFH)
        }

        termBibeFH.setOnClickListener {
            etSelec = termBibeFH
            mostrarFechaHora()
        }

        // Recibir el bebeId y cuidadorId de la otra activity
        val bebeId = intent.getStringExtra("bebeId")
        val cuidadorId = intent.getStringExtra("cuidadorId")
        val relacionId = intent.getStringExtra("relacion_id")

        //Programación botón atrás
        val botonAtras9: ImageView = findViewById(R.id.btnAtras9)

        botonAtras9.setOnClickListener {
            val intent = Intent(this, activity_selecciona::class.java)
            intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
            intent.putExtra("cuidadorId", cuidadorId) //mandar el cuidadro con el que habíamos entrado en esta activity.
            intent.putExtra("relacion_id", relacionId)
            startActivity(intent)
        }

        //Programación botón guardar
        val btnGBibe: Button = findViewById(R.id.btnGuardarBibe)

        btnGBibe.setOnClickListener {
            //Lamada función guardar datos del biberón
            guardarDatosBiberon(bebeId, cuidadorId, empBibeFH, termBibeFH, campoCant, campoBibe, relacionId)

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

    //Función para guardar los datos del biberón
    private fun guardarDatosBiberon(bebeId: String?, cuidadorId: String?, empBibeFH: EditText, termBibeFH: EditText, campoCant: EditText, campoBibe: EditText, relacionId: String?){

    // Verificar si bebeId y cuidadorId son nulos
    if (bebeId.isNullOrEmpty() || cuidadorId.isNullOrEmpty()) {
       Toast.makeText(this, "Error: No se encontró el ID del bebé o del cuidador.", Toast.LENGTH_SHORT).show()
       return
    }

    val tipoAlimento = when {
    btnSLM.isSelected -> "LM" // Lactancia materna
    btnSForm.isSelected -> "form" // Fórmula
    else -> ""
    }

    //Verificar que han seleccionado el tipo de leche
    if (tipoAlimento.isEmpty())
    {
        Toast.makeText(this, "Por favor, selecciona el tipo de leche.", Toast.LENGTH_SHORT).show()
        return
    }

    val fechaInicio = empBibeFH.text.toString()

    // Verificar que han seleccionado la fecha y hora
    if (fechaInicio.isEmpty())
    {
        Toast.makeText(this, "Por favor, selecciona la fecha y hora de inicio.", Toast.LENGTH_SHORT)
            .show()
        return
    }

    val fechaTermina = termBibeFH.text.toString()

    //Verificar que han seleccionado la fecha y hora
    if (fechaTermina.isEmpty())
    {
        Toast.makeText(
            this,
            "Por favor, selecciona la fecha y hora de finalización.",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    val cantidad = campoCant.text.toString()
    //Verificar que han ingresado la cantidad de leche
    if (cantidad.isEmpty())
    {
        Toast.makeText(this, "Por favor, ingresa la cantidad de leche.", Toast.LENGTH_SHORT).show()
        return
    }

    //este dato es opcional
    val comentario = campoBibe.text.toString()
    val bibeId = UUID.randomUUID().toString() // Crea un ID para cada registro

    val datosBibe = hashMapOf(
        "bebeId" to bebeId,
        "bibeId" to bibeId,
        "tipoAlimento" to tipoAlimento,
        "fechaInicio" to fechaInicio,
        "fechaTermina" to fechaTermina,
        "cantidad" to cantidad,
        "comentario" to comentario
    )

    db.collection("alimentacion_biberon")
    .add(datosBibe)
    .addOnSuccessListener{
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