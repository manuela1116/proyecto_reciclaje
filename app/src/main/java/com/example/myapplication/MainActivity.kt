    package com.example.myapplication
    
    import android.content.Intent
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.text.Editable
    import android.text.TextWatcher
    import android.view.View
    import android.widget.ArrayAdapter
    import android.widget.Button
    import android.widget.EditText
    import android.widget.Spinner
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AlertDialog
    import com.android.volley.Request
    import com.android.volley.Response
    import com.android.volley.toolbox.StringRequest
    import com.android.volley.toolbox.Volley
    import java.lang.StringBuilder
    import java.security.MessageDigest
    import java.security.NoSuchAlgorithmException
    
    class MainActivity : AppCompatActivity() {

        var editTextNombre:EditText?=null
        var editTextApellido:EditText?=null
        var editTextTelefono:EditText?=null
        var editTextCorreo:EditText?=null
        var editTextContrasena:EditText?=null
        var editTextDireccion:EditText?=null
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            editTextCorreo = findViewById(R.id.editTextText2)
            editTextContrasena = findViewById(R.id.editTextText3)
    
            val btnLink = findViewById<Button>(R.id.buttonConnectPHP)
    
            btnLink.setOnClickListener {
                val correoUsuario = editTextCorreo?.text.toString()
                solicitarRecuperacionContrasena(correoUsuario)
            }
    
        }
    
        fun solicitarRecuperacionContrasena(correoUsuario: String) {
            val url = "http://192.168.56.1/proyectoReciclaje/recordarcontraseña.php"
            val queue = Volley.newRequestQueue(this)
            val request = object : StringRequest (Method.POST, url,
                Response.Listener<String> { response ->
                    mostrarContrasenaGenerada(response)
                }, Response.ErrorListener { error ->
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                }) {
                override fun getParams(): MutableMap<String, String>? {
                    val params = HashMap<String, String>()
                    params["email"] = correoUsuario
                    return params
                }
            }
            queue.add(request)
        }
    
        fun mostrarContrasenaGenerada(contrasenaGenerada: String) {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Contraseña generada")
            alertDialogBuilder.setMessage("La contraseña generada es: $contrasenaGenerada")
            alertDialogBuilder.setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    
        class PasswordEncryptor {
            fun encryptPassword(password: String): String {
                try {
                    val md = MessageDigest.getInstance("SHA-256")
                    val digest = md.digest(password.toByteArray())
                    return bytesToHex(digest)
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                }
                return ""
            }
            private fun bytesToHex(bytes: ByteArray): String {
                val hexString = StringBuilder()
                for (byte in bytes) {
                    val hex = Integer.toHexString(0xff and byte.toInt())
                    if (hex.length == 1) {
                        hexString.append('0')
                    }
                    hexString.append(hex)
                }
                return hexString.toString()
            }
        }
    
        fun buttonValidarUsuario(view: View) {
    
            val url = "http://192.168.56.1/proyectoReciclaje/validar_usuario.php"
            val queue = Volley.newRequestQueue(this)
            val encryptor = PasswordEncryptor()
            val request = object : StringRequest(Method.POST, url,
                Response.Listener<String> { resultado ->
    
                    if (resultado.trim() == "success") {
                        Toast.makeText(this, "Inicio de sesion exitoso", Toast.LENGTH_LONG).show()
                        limpiarCamposIngreso()
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
                    parametros.put("contrasena",encryptor.encryptPassword(editTextContrasena?.text.toString()))
    
                    return parametros
                }
            }
            queue.add(request)
        }
    
        fun limpiarCamposIngreso() {
            editTextCorreo?.setText("")
            editTextContrasena?.setText("")
        }
    
        fun mostrarFormularioRegistro(view: View){
            setContentView(R.layout.registro_usuario)
            editTextCorreo=findViewById(R.id.editTextCorreo)
            editTextNombre=findViewById(R.id.editTextNombre)
            editTextApellido=findViewById(R.id.editTextApellido)
            editTextTelefono=findViewById(R.id.editTextTelefono)
            editTextContrasena=findViewById(R.id.editTextContrasena)
            editTextDireccion=findViewById(R.id.editTextDireccion)
    
            val spinnerEstrato=findViewById<Spinner>(R.id.spinnerEstrato)
            val estratoOptions = arrayOf("Estrato","Estrato 1", "Estrato 2", "Estrato 3", "Estrato 4", "Estrato 5", "Estrato 6")
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estratoOptions)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerEstrato.adapter = adapter
    
            spinnerEstrato.setSelection(0)
    
            val TextViewMensaje = findViewById<TextView>(R.id.textViewMensaje)
    
            editTextCorreo?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val correo = editTextCorreo?.text.toString()
                    if (!correo.isEmpty()) {
                        verificarCorreoElectronico(correo)
                    }
                }
    
                override fun afterTextChanged(s: Editable?) {}
            })
            editTextContrasena?.addTextChangedListener(object  : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val contrasena = editTextContrasena?.text.toString()
                    if (!contrasena.isEmpty()) {
                        verificarContrasena(contrasena)
                    }
                }
    
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    
        fun verificarContrasena(contrasena: String) {
            val url = "http://192.168.56.1/proyectoReciclaje/verificar_contraseña.php"
            val request = object : StringRequest(Method.POST, url,
                Response.Listener<String> { response ->
                    if (response.trim() == "true") {
                        mostrarMensajeError("La contraseña cumple con los requisitos", Toast.LENGTH_LONG)
                        habilitarCamposRegistro()
                    } else {
                        mostrarMensajeError("La contraseña debe tener al menos 8 caracteres, una mayúscula y un número", Toast.LENGTH_LONG)
                        deshabilitarCamposRegistro()
                    }
                }, Response.ErrorListener { error ->
                    mostrarMensajeError("Error al conectar con el servidor: ${error.message}", Toast.LENGTH_LONG)
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["contrasena"] = contrasena
                    return params
                }
            }
            Volley.newRequestQueue(this).add(request)
        }
    
        fun verificarCorreoElectronico(correo: String) {
            val url = "http://192.168.56.1/proyectoReciclaje/verificar_correo.php"
    
            val request = object : StringRequest(Method.POST, url,
                Response.Listener<String> { response ->
                    if (response.trim() == "El usuario ya se encuentra registrado") {
                        mostrarMensajeError("El correo electronico ya esta registrado", Toast.LENGTH_LONG)
                        deshabilitarCamposRegistro()
                    } else {
                        mostrarMensajeError("El correo electrónico está disponible para el registro", Toast.LENGTH_LONG)
                        habilitarCamposRegistro()
                    }
                }, Response.ErrorListener { error ->
                    mostrarMensajeError("Error al conectar con el servidor: ${error.message}", Toast.LENGTH_LONG)
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["correo_usuario"] = correo
                    return params
                }
            }
            Volley.newRequestQueue(this).add(request)
        }
    
        fun mostrarMensajeError(mensaje: String, duracion: Int) {
            Toast.makeText(this, mensaje, duracion).show()
        }
    
        fun deshabilitarCamposRegistro() {
            editTextNombre?.isEnabled = false
            editTextApellido?.isEnabled = false
            editTextTelefono?.isEnabled = false
            editTextDireccion?.isEnabled = false
        }
    
        fun habilitarCamposRegistro() {
            editTextNombre?.isEnabled = true
            editTextApellido?.isEnabled = true
            editTextTelefono?.isEnabled = true
            editTextContrasena?.isEnabled = true
            editTextDireccion?.isEnabled = true
    
        }
    
        fun buttonRegistrar(view:View) {
            val url="http://192.168.56.1/proyectoReciclaje/registrar_usuario.php"
            val queue=Volley.newRequestQueue(this)
            val encryptor = PasswordEncryptor()
            var resultadoPost = object : StringRequest(Request.Method.POST,url,
                Response.Listener<String> { response ->
                    Toast.makeText(this,"Usuario registrado exitosamente",Toast.LENGTH_LONG).show()
                    val tipoUsuarioSeleccionado = obtenerTipoUsuarioSeleccionado()
                    redirigirSegunTipoUsuario(tipoUsuarioSeleccionado)
                    limpiarCampos()
                },Response.ErrorListener { error ->
                    Toast.makeText(this,"Error $error ",Toast.LENGTH_LONG).show()
                }){
                override fun getParams(): MutableMap<String, String> {
                    val parametros=HashMap<String,String>()
                    parametros.put("correo_usuario",editTextCorreo?.text.toString())
                    parametros.put("nombre_usuario",editTextNombre?.text.toString())
                    parametros.put("apellido_usuario",editTextApellido?.text.toString())
                    parametros.put("numerotelefonico_usuario",editTextTelefono?.text.toString())
                    parametros.put("contrasena",encryptor.encryptPassword(editTextContrasena?.text.toString()))
                    parametros.put("direccion_usuario",editTextDireccion?.text.toString())
                    parametros.put("estrato_usuario", (findViewById<Spinner>(R.id.spinnerEstrato)).selectedItemPosition.toString())
    
                    return parametros
                }
            }
            queue.add(resultadoPost)
        }
    
        fun limpiarCampos() {
            editTextCorreo?.setText("")
            editTextNombre?.setText("")
            editTextApellido?.setText("")
            editTextTelefono?.setText("")
            editTextContrasena?.setText("")
            editTextDireccion?.setText("")
            val spinnerEstrato = findViewById<Spinner>(R.id.spinnerEstrato)
            spinnerEstrato.setSelection(0)
        }

        fun obtenerTipoUsuarioSeleccionado(): String {
            val spinnerTipoUsuario = findViewById<Spinner>(R.id.spinnerTipoUsuario)
            return spinnerTipoUsuario.selectedItem.toString()
        }

        fun redirigirSegunTipoUsuario(tipo_usuario: String) {
            val intent = when (tipo_usuario) {
                "Cliente" -> Intent(this, ModuloClienteActivity::class.java)
                "Trabajador" -> Intent(this, ModuloEmpresaActivity::class.java)
                else -> Intent(this, MainActivity::class.java)
            }
            startActivity(intent)
        }
    
    }