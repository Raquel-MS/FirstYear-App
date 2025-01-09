package com.example.firstyearapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class activity_seleccion_bebe : AppCompatActivity() {

    private lateinit var btnBebe1: ImageView
    private lateinit var btnBebe2: ImageView
    private lateinit var btnBebe3: ImageView
    private lateinit var btnBebe4: ImageView
    private lateinit var etBebe1: TextView
    private lateinit var etBebe2: TextView
    private lateinit var etBebe3: TextView
    private lateinit var etBebe4: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccion_bebe)

        //Incializar botones
        btnBebe1 = findViewById(R.id.btnBebe1)
        btnBebe2 = findViewById(R.id.btnBebe2)
        btnBebe3 = findViewById(R.id.btnBebe3)
        btnBebe4 = findViewById(R.id.btnBebe4)

        //Incializar etiquetas
        etBebe1 = findViewById(R.id.etBebe1)
        etBebe2 = findViewById(R.id.etBebe2)
        etBebe3 = findViewById(R.id.etBebe3)
        etBebe4 = findViewById(R.id.etBebe4)

        //cuidadorId del Main o InicioSesion
        val cuidadorId = intent.getStringExtra("cuidadorId") //cuidadorId del Main o InicioSesion

        val db = FirebaseFirestore.getInstance()

        //Función mostrar todos los botones OFF
        configurarBotones()

        db.collection("relacion_cuidador_bebe")
            .whereEqualTo("cuidador_id", cuidadorId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val bebesLista = mutableListOf<String>()
                    val nombresBebes = mutableListOf<String>()
                    val relacionIds = mutableListOf<String>() //Se necesita este valor apra poder borrar el bebé y su relación
                    for (document in documents) {
                        val bebeId = document.getString("bebe_id")
                        val nombreBebe = document.getString("nombreBebe")
                        val relacionId = document.id

                        if (bebeId != null && nombreBebe != null) {
                            bebesLista.add(bebeId)
                            nombresBebes.add(nombreBebe)
                            relacionIds.add(relacionId)
                        }
                    }
                    // Llamar función para actualizar la interfaz con los bebés
                    mostrarBebes(bebesLista, nombresBebes, relacionIds, cuidadorId)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al obtener los bebés", Toast.LENGTH_SHORT).show()
            }

        //Programación botón cerrar sesión
        val btnCerrarSesion: ImageView = findViewById(R.id.btnCerrarSesion)

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
            val intent = Intent(this, activityInicioSesion::class.java)
            startActivity(intent)
        }

        //Programación botón acceso a activity añadir bebé
        val btnAnadir: ImageView = findViewById(R.id.btnAnadir)

        btnAnadir.setOnClickListener {
            val intent = Intent(this, activityAnadirBebe::class.java)
            intent.putExtra("cuidadorId", cuidadorId) // Pasa el cuidadorId
            startActivity(intent)
        }
    }

    private fun configurarBotones() {
        // Mostrar todos los botones con su imagen OFF y deshabilitados
        btnBebe1.setImageResource(R.drawable.bebe1off)
        btnBebe1.isEnabled = false

        btnBebe2.setImageResource(R.drawable.bebe2off)
        btnBebe2.isEnabled = false

        btnBebe3.setImageResource(R.drawable.bebe3off)
        btnBebe3.isEnabled = false

        btnBebe4.setImageResource(R.drawable.bebe4off)
        btnBebe4.isEnabled = false
    }

    //Función para mostrar los bebés que tiene el cuidador
    fun mostrarBebes(bebesLista: List<String>, nombresBebes: List<String>,  relacionIds: List<String>, cuidadorId: String?) {
        val botones = listOf(btnBebe1, btnBebe2, btnBebe3, btnBebe4)
        val textos = listOf(etBebe1, etBebe2, etBebe3, etBebe4)
        val imagenesOn = listOf(R.drawable.bebe1on, R.drawable.bebe2on, R.drawable.bebe3on, R.drawable.bebe4on)

        for (i in bebesLista.indices) {
            // Cambiar la imagen a la versión ON, habilitar el botón y mostrar su nombre
            botones[i].setImageResource(imagenesOn[i])
            botones[i].isEnabled = true
            textos[i].text = nombresBebes[i]

            // Programar el botón cuando haga clic selecciona el bebé
            botones[i].setOnClickListener {
                val intent = Intent(this, activity_selecciona::class.java)
                intent.putExtra("bebeId", bebesLista[i])
                intent.putExtra("relacion_id", relacionIds[i])
                intent.putExtra("cuidadorId", cuidadorId)
                startActivity(intent)
            }
        }
    }


    //Función para cerrar sesión del cuidador actual
    fun cerrarSesion() {
        val cuidadorIniciado = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val editor = cuidadorIniciado.edit()
        editor.clear()  // Borrar datos
        editor.apply()

        // Ir a la pantalla de inicio de sesión
        val intent = Intent(this, activityInicioSesion::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()

        // Consulta apra obtener los bebés después de añadirlos
        val cuidadorId = intent.getStringExtra("cuidadorId")

        if (cuidadorId != null) {
            val db = FirebaseFirestore.getInstance()

            db.collection("relacion_cuidador_bebe")
                .whereEqualTo("cuidador_id", cuidadorId)
                .get()
                .addOnSuccessListener { documents ->

                    if (!documents.isEmpty) {
                        val bebesList = mutableListOf<String>()
                        val nombresBebes = mutableListOf<String>()
                        val relacionIds =  mutableListOf<String>()

                        for (document in documents) {
                            val bebeId = document.getString("bebe_id")
                            val nombreBebe = document.getString("nombreBebe")

                            if (bebeId != null && nombreBebe != null) {
                                bebesList.add(bebeId)
                                nombresBebes.add(nombreBebe)
                                relacionIds.add(document.id)
                            }
                        }
                        mostrarBebes(bebesList, nombresBebes, relacionIds, cuidadorId)
                    } else {
                        configurarBotones() // Función para poner botones con imagen OFF y dehabilitados
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error al obtener los bebés", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error: cuidadorId es nulo", Toast.LENGTH_SHORT).show()
        }
    }
}