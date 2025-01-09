package com.example.firstyearapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.DatePickerDialog
import android.content.Intent
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class activityAnadirBebe : AppCompatActivity() {

    private lateinit var campoFecha: EditText
    private lateinit var btnFem: ImageView
    private lateinit var btnMasc: ImageView
    private var genero: String? = null
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anadir_bebe)

        db = FirebaseFirestore.getInstance()
        val campoNombreBebe: EditText = findViewById(R.id.campoNombreBebe)
        val selectorRelacion: Spinner = findViewById(R.id.selectorRol)
        val cuidadorId = intent.getStringExtra("cuidadorId")

        val opc = ArrayAdapter.createFromResource(
            this,
            R.array.opciones_rol,
            android.R.layout.simple_spinner_item
        )

        // Especificar cómo se ven
        opc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectorRelacion.adapter = opc

        // Géneros
        btnFem = findViewById(R.id.btnFem)
        btnMasc = findViewById(R.id.btnMasc)

        btnFem.setOnClickListener {
            // Selección botón femenino
            genero = "Femenino"
            btnFem.isSelected = true
            btnFem.setBackgroundResource(R.drawable.imagen_borde)

            // Quitar selección del botón masculino y su fondo
            btnMasc.isSelected = false
            btnMasc.setBackgroundResource(0)
        }

        btnMasc.setOnClickListener {
            // Selección botón masculino
            genero = "Masculino"
            btnMasc.isSelected = true
            btnMasc.setBackgroundResource(R.drawable.imagen_borde)

            // Quitar selección del botón femenino y su fondo
            btnFem.isSelected = false
            btnFem.setBackgroundResource(0)
        }

        //Programación selector fecha nacimiento
        campoFecha = findViewById(R.id.campoFNac)

        campoFecha.setOnClickListener {
            mostrarFN()
        }
        //Programación botón Guardar
        val botonGBebe: Button = findViewById(R.id.btnGBebe)

        botonGBebe.setOnClickListener {
            val nombreBebe = campoNombreBebe.text.toString()
            val fechaNacimiento = campoFecha.text.toString()
            val rol = selectorRelacion.selectedItem.toString()

            if (nombreBebe.isNotEmpty() && fechaNacimiento.isNotEmpty() && genero != null) {

                val cuidadorId = intent.getStringExtra("cuidadorId") //cuidadorId del Main o InicioSesion

                if (cuidadorId != null) {
                    guardarBebeFirestore(nombreBebe, genero!!, fechaNacimiento, rol, cuidadorId)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Error: No se ha identificado el cuidador", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        //Programación botón atrás
        val botonAtras4: ImageView = findViewById(R.id.btnAtras4)

        botonAtras4.setOnClickListener {
            val intent = Intent(this, activity_seleccion_bebe::class.java)
            intent.putExtra("cuidadorId", cuidadorId) //mandar el cuidadro con el que habíamos entrado en esta activity.
            startActivity(intent)
        }
    }

    //Función para mostrar la fecha de nacimiento
    private fun mostrarFN() {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        // Selección de la fecha
        val seleccionarFecha = DatePickerDialog(
            this,
            { _, anio, mes, dia ->
                // Ajustar el formato de la fecha
                val fechaSeleccionada = "$dia/${mes + 1}/$anio"
                campoFecha.setText(fechaSeleccionada)
            },
            year,
            month,
            day
        )
        seleccionarFecha.show()
    }

    //Función para guardar los datos
    private fun guardarBebeFirestore(nombreBebe: String, genero: String, fechaNacimiento: String, rol: String, cuidadorId: String) {
        val bebeId = UUID.randomUUID().toString() // Crea un ID para cada bebé

        // Guardar los datos del bebé
        val bebeDatos = hashMapOf(
            "bebe_id" to bebeId,
            "nombreBebe" to nombreBebe,
            "genero" to genero,
            "fecha_nacimiento" to fechaNacimiento
        )

        db.collection("bebes").document(bebeId)
            .set(bebeDatos)
            .addOnSuccessListener {
                Toast.makeText(this, "Bebé guardado", Toast.LENGTH_SHORT).show()

                // Guarda relación cuidador-bebé
                guardarRelacionCuidadorBebe(bebeId, cuidadorId, rol, nombreBebe)

                val intent = Intent(this, activity_seleccion_bebe::class.java)
                intent.putExtra("cuidadorId", cuidadorId)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar el bebé", Toast.LENGTH_SHORT).show()
            }
    }
    private fun guardarRelacionCuidadorBebe(bebeId: String, cuidadorId: String, rol: String, nombreBebe: String) {
        val relacionId = UUID.randomUUID().toString() // Crea un ID para la relación cuidador-bebé

        val relacionDatos = hashMapOf(
            "relacion_id" to relacionId,
            "bebe_id" to bebeId,
            "cuidador_id" to cuidadorId,
            "rol" to rol,
            "nombreBebe" to nombreBebe
        )

        db.collection("relacion_cuidador_bebe").document(relacionId)
            .set(relacionDatos)
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar la relación", Toast.LENGTH_SHORT).show()
            }
    }
}

