package com.example.firstyearapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

class activityInicioSesion : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        db = FirebaseFirestore.getInstance()

        //Elementos
        val campoCorreo: EditText = findViewById(R.id.campoISCorreo)
        val campoContrasena: EditText = findViewById(R.id.campoISCont)
        val botonAcceder: Button = findViewById(R.id.btnAcceder)
        val botonNoCuenta: Button = findViewById(R.id.btnNoCuenta)

        //botón acceder
        botonAcceder.setOnClickListener {
            val correo = campoCorreo.text.toString().trim()
            val contrasena = campoContrasena.text.toString().trim()
            val intent = Intent(this, activity_seleccion_bebe::class.java)

            //Verificar que hay datos en los campos
            if (correo.isNotEmpty() && contrasena.isNotEmpty()) {
                iniciarSesion(correo, contrasena)
            } else {
                // Los campos no pueden estar vacíos
                Toast.makeText(this, "Por favor, escribe tu correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción para el botón "No tengo cuenta"
        botonNoCuenta.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun iniciarSesion(correo: String, contrasena: String) {
        db.collection("cuidadores")
            .whereEqualTo("correo", correo)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val cuidador = documents.first()
                    val contrasenaGuardada = cuidador.getString("contrasena")

                    // Cifrar la contraseña antes de compararla
                    val contrasenaCifrada = hashPassword(contrasena)

                    if (contrasenaGuardada == contrasenaCifrada) {
                        // Contraseña correcta
                        Toast.makeText(this, "¡Ya estás dentro!", Toast.LENGTH_SHORT).show()

                        // Guardar el correo para mantener la sesión iniciada
                        val cuidadorIniciado = getSharedPreferences("AppPreferences", MODE_PRIVATE)
                        val editor = cuidadorIniciado.edit()
                        editor.putString("correo", correo)
                        editor.apply()

                        // Ir a la pantalla selección bebé
                        val intent = Intent(this, activity_seleccion_bebe::class.java)
                        intent.putExtra("cuidadorId", correo)
                        startActivity(intent)
                        finish()
                    } else {
                        // Contraseña incorrecta
                        Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // No se ha registrado ese correo
                    Toast.makeText(this, "No existe ninguna cuenta con ese correo", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error al buscar al usuario: ", exception)
                Toast.makeText(this, "Error al iniciar sesión: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Función para cifrar la contraseña usando SHA-256
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
