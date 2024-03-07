package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    var editTextNombre:EditText?=null
    var editTextApellido:EditText?=null
    var editTextTelefono:EditText?=null
    var editTextCorreo:EditText?=null
    var editTextContrasena:EditText?=null
    var editTextDireccion:EditText?=null
    var editTextEstrato:EditText?=null

    fun validarCorreo() {

        val url = "http://192.168.56.1/proyectoReciclaje/verificar_correo.php"
        val queue=Volley.newRequestQueue(this)
        var resultadoPost = object : StringRequest(Request.Method.POST,url,
            Response.Listener<String> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val correoExistente = jsonResponse.getBoolean("correo_existente")
                    if (correoExistente) {
                        Toast.makeText(this, "El correo electronico ya esta registrado", Toast.LENGTH_LONG).show()
                        deshabilitarCamposRegistro()
                    } else {
                        habilitarCamposRegistro()
                    }
                } catch (e: Exception){
                    Toast.makeText(this, "Error al procesar la respuesta del servidor", Toast.LENGTH_LONG).show()
                }
            },Response.ErrorListener { error ->
                Toast.makeText(this,"Error $error ",Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val parametros=HashMap<String,String>()
                parametros.put("correo_usuario",editTextCorreo?.text.toString())

                return parametros
            }
        }
        queue.add(resultadoPost)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editTextCorreo = findViewById(R.id.editTextText2)
        editTextCorreo?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarCorreo()
            }
        })
        editTextContrasena = findViewById(R.id.editTextText3)

    }

    fun buttonValidarUsuario(view: View) {

        val url = "http://192.168.56.1/proyectoReciclaje/validar_usuario.php"
        val queue = Volley.newRequestQueue(this)
        val request = object : StringRequest(Method.POST, url,
            Response.Listener<String> { resultado ->

                if (resultado.trim() == "success") {
                    Toast.makeText(this, "Inicio de sesion exitoso", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, ModuloClienteActivity::class.java)
                    startActivity(intent)
                }else {
                    Toast.makeText(this, "Usuario no registrado", Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(this,"Error $error ", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val parametros=HashMap<String,String>()
                parametros.put("correo_usuario",editTextCorreo?.text.toString())
                parametros.put("contrasena",editTextContrasena?.text.toString())

                return parametros
            }
        }
        queue.add(request)
    }

    fun mostrarFormularioRegistro(view:View){
        setContentView(R.layout.registro_usuario)
        editTextCorreo=findViewById(R.id.editTextCorreo)
        editTextNombre=findViewById(R.id.editTextNombre)
        editTextApellido=findViewById(R.id.editTextApellido)
        editTextTelefono=findViewById(R.id.editTextTelefono)
        editTextContrasena=findViewById(R.id.editTextContrasena)
        editTextDireccion=findViewById(R.id.editTextDireccion)
        editTextEstrato=findViewById(R.id.editTextEstrato)
    }

    fun deshabilitarCamposRegistro() {
        editTextNombre?.isEnabled = false
        editTextApellido?.isEnabled = false
        editTextTelefono?.isEnabled = false
        editTextContrasena?.isEnabled = false
        editTextDireccion?.isEnabled = false
        editTextEstrato?.isEnabled = false

        println("Campos deshabilitados")
    }

    fun habilitarCamposRegistro() {
        editTextNombre?.isEnabled = true
        editTextApellido?.isEnabled = true
        editTextTelefono?.isEnabled = true
        editTextContrasena?.isEnabled = true
        editTextDireccion?.isEnabled = true
        editTextEstrato?.isEnabled = true

        println("Campos habilitados")
    }

    fun buttonRegistrarUsuario(view:View) {
        val url="http://192.168.56.1/proyectoReciclaje/registrar_usuario.php"
        val queue=Volley.newRequestQueue(this)
        var resultadoPost = object : StringRequest(Request.Method.POST,url,
            Response.Listener<String> { response ->
                Toast.makeText(this,"Usuario registrado exitosamente",Toast.LENGTH_LONG).show()
            },Response.ErrorListener { error ->
                Toast.makeText(this,"Error $error ",Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val parametros=HashMap<String,String>()
                parametros.put("correo_usuario",editTextCorreo?.text.toString())
                parametros.put("nombre_usuario",editTextNombre?.text.toString())
                parametros.put("apellido_usuario",editTextApellido?.text.toString())
                parametros.put("numerotelefonico_usuario",editTextTelefono?.text.toString())
                parametros.put("contrasena",editTextContrasena?.text.toString())
                parametros.put("direccion_usuario",editTextDireccion?.text.toString())
                parametros.put("estrato_usuario",editTextEstrato?.text.toString())

                return parametros
            }
        }
        queue.add(resultadoPost)
    }

    fun buttonVolverLogin(view: View) {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}