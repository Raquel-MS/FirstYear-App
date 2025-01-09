package com.example.firstyearapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Guardar el correo para que no haya que volver a iniciar sesión al abrir la app
        val cuidadorIniciado = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val correoGuardado = cuidadorIniciado.getString("correo", null)

        if (correoGuardado != null) {
            // Si hay un correo guardado, abrir la pantalla de selección de bebé
            val intent = Intent(this, activity_seleccion_bebe::class.java)
            intent.putExtra("cuidadorId", correoGuardado)
            startActivity(intent)
            finish()
            return
        }

        val campoNombre: EditText = findViewById(R.id.campoNombre)
        val campoCorreo: EditText = findViewById(R.id.campoCorreo)
        val campoContrasena: EditText = findViewById(R.id.campoCont)
        val campoTelefono: EditText = findViewById(R.id.campoTelf)
        val botonCrear: Button = findViewById(R.id.btnCrear)
        val botonCuenta: Button = findViewById(R.id.btnCuenta)

        botonCrear.setOnClickListener {
            val nombre = campoNombre.text.toString()
            val correo = campoCorreo.text.toString() //este dato se usa como identificador único
            val contrasena = campoContrasena.text.toString()
            val telefono = campoTelefono.text.toString()

            //Verificar que hay datos en los campos
            if (nombre.isNotEmpty() && correo.isNotEmpty() && contrasena.isNotEmpty()) {
                val contrasenaCifrada = hashPassword(contrasena)

                val cuidador = Cuidador(
                    nombre = nombre,
                    correo = correo,
                    contrasena = contrasenaCifrada,
                    telefono = if (telefono.isEmpty()) null else telefono
                )

                validarYGuardar(cuidador, correo)
            }
            else {
                Log.w("Firestore", "Completa todos los campos obligatorios")
            }
        }

        botonCuenta.setOnClickListener {
            val intent = Intent(this, activityInicioSesion::class.java)
            startActivity(intent)
        }
    }

    //Función para verificar que no se ha utilizado ya ese correo, ya que es Identificador único
    private fun validarYGuardar(cuidador: Cuidador, correo: String) {
        val db = FirebaseFirestore.getInstance()

        // Verificar si el correo ya está registrado
        db.collection("cuidadores").document(correo).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Si el correo ya está registrado, mostrar un mensaje
                    Toast.makeText(this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show()
                    Log.w("Firestore", "Correo ya registrado: $correo")
                } else {
                    // Si el correo no está registrado, guardar el nuevo cuidador
                    guardarCuidador(cuidador, correo)
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al verificar el cuidador", e)
                Toast.makeText(this, "Error al verificar el correo", Toast.LENGTH_SHORT).show()
            }
    }

    // Función para guardar el cuidador
    fun guardarCuidador(cuidador: Cuidador, correo: String) {

        val db = FirebaseFirestore.getInstance()

        // Crear un nuevo documento con un ID automático incremental para el cuidador
        db.collection("cuidadores").document(correo)
            .set(cuidador)
            .addOnSuccessListener {
                Log.d("Firestore", "Cuidador guardado con correo como ID: $correo")

                //Ir a la pantalla selección bebé y enviar el correo como ID
                val intent = Intent(this, activity_seleccion_bebe::class.java)
                intent.putExtra("cuidadorId", correo)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al guardar el cuidador", e)
            }
    }

    // Función para cifrar contraseñas usando SHA-256
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) } // Convertir a formato hexadecimal
    }
}

