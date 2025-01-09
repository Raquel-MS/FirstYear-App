package com.example.firstyearapp
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class activity_selecciona : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selecciona)

        //IDs de la activitySeleccionBebe
        val bebeId = intent.getStringExtra("bebeId")
        val relacionId = intent.getStringExtra("relacion_id")
        val cuidadorId = intent.getStringExtra("cuidadorId")
        db = FirebaseFirestore.getInstance()
        val etNombreBebe: TextView = findViewById(R.id.etNombreBebe)

        // recuperar nombre del bebé de la BBDD y poner el nombre del bebé en la etiqueta
        if (bebeId != null) {
            db.collection("bebes").document(bebeId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nombreBebe = document.getString("nombreBebe")
                        etNombreBebe.setText(nombreBebe)
                    } else {
                        Toast.makeText(this, "Bebé no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al obtener el nombre: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        //Programación botón atrás
        val botonAtras5: ImageView = findViewById(R.id.btnAtras5)

        botonAtras5.setOnClickListener {
            val intent = Intent(this, activity_seleccion_bebe::class.java)
            intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
            intent.putExtra("cuidadorId", cuidadorId) //mandar el cuidadro con el que habíamos entrado en esta activity.
            intent.putExtra("relacion_id", relacionId)
            startActivity(intent)
        }

        // Programación botón borrar bebé
        val btnBorrarBebe: ImageView = findViewById(R.id.btnBorrarBebe)

        btnBorrarBebe.setOnClickListener {
            if (bebeId != null && relacionId != null) {
                AlertDialog.Builder(this)
                    .setTitle("Eliminación definitiva")
                    .setMessage("¿Seguro que quieres borrar a este bebé?")
                    .setPositiveButton("Sí") { _, _ ->

                        //Eliminar bebe y relación de la BBDD
                        db.collection("bebes").document(bebeId).delete()
                        db.collection("relacion_cuidador_bebe").document(relacionId).delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Bebé eliminado correctamente", Toast.LENGTH_SHORT).show()

                                //Volver a la pantalla seleccionar bebé
                                val intent = Intent(this, activity_seleccion_bebe::class.java)
                                intent.putExtra("cuidadorId", cuidadorId) //mandar el cuidadro con el que habíamos entrado en esta activity.
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("No", null)
                    .show()
            } else {
                Toast.makeText(this, "ID de bebé no válido", Toast.LENGTH_SHORT).show()
            }
        }

        //Programación botón acceso a Lactancia Materna
        val botonPecho: ImageView = findViewById(R.id.btnPecho)

        botonPecho.setOnClickListener {
            val intent = Intent(this, activityLM::class.java)
            intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
            intent.putExtra("cuidadorId", cuidadorId) // Pasa el cuidadorId para el botón atrás
            intent.putExtra("relacion_id", relacionId)
            startActivity(intent)
        }

        //Programación botón acceso a Cambio pañal
        val botonPanal: ImageView = findViewById(R.id.btnPanal)

        botonPanal.setOnClickListener {
            val intent = Intent(this, activity_panal::class.java)
            intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
            intent.putExtra("cuidadorId", cuidadorId) // Pasa el cuidadorId para el botón atrás
            intent.putExtra("relacion_id", relacionId)
            startActivity(intent)
        }

        //Programación botón acceso a sueño
        val botonSueno: ImageView = findViewById(R.id.btnSueno)

        botonSueno.setOnClickListener {
            val intent = Intent(this, activity_sueno::class.java)
            intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
            intent.putExtra("cuidadorId", cuidadorId) // Pasa el cuidadorId para el botón atrás
            intent.putExtra("relacion_id", relacionId)
            startActivity(intent)
        }

        //Programación botón acceso a biberón
        val botonBiberon: ImageView = findViewById(R.id.btnBiberon)

        botonBiberon.setOnClickListener {
            val intent = Intent(this, activity_bibe::class.java)
            intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
            intent.putExtra("cuidadorId", cuidadorId) // Pasa el cuidadorId para el botón atrás
            intent.putExtra("relacion_id", relacionId)
            startActivity(intent)
        }

        //Programación botón acceso a hitos
        val botonHitos: ImageView = findViewById(R.id.btnHitos)

        botonHitos.setOnClickListener {
            val intent = Intent(this, activity_des::class.java)
            intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
            intent.putExtra("cuidadorId", cuidadorId) // Pasa el cuidadorId para el botón atrás
            intent.putExtra("relacion_id", relacionId)
            startActivity(intent)
        }

        //Programación botón acceso a los registros
        val botonRegistros: ImageView = findViewById(R.id.btnRegistros)

        botonRegistros.setOnClickListener {
            val intent = Intent(this, ActivityRegistros::class.java)
            intent.putExtra("bebeId", bebeId)  // Mandar el bebeId a esta activity
            intent.putExtra("cuidadorId", cuidadorId) // Pasa el cuidadorId para el botón atrás
            intent.putExtra("relacion_id", relacionId)
            startActivity(intent)
        }
    }
}