package com.example.railway.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.railway.R
import com.example.railway.data.FirebaseRepository

class AuthActivity : AppCompatActivity() {

    private lateinit var nameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var passEdit: EditText
    private lateinit var loginBtn: Button
    private lateinit var registerBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        nameEdit = findViewById(R.id.inputName)
        emailEdit = findViewById(R.id.inputEmail)
        passEdit = findViewById(R.id.inputPassword)
        loginBtn = findViewById(R.id.btnLogin)
        registerBtn = findViewById(R.id.btnRegister)

        loginBtn.setOnClickListener {
            val email = emailEdit.text.toString().trim()
            val pass = passEdit.text.toString()
            if (email.isEmpty() || pass.isEmpty()) {
                showToast("Please fill all fields")
                return@setOnClickListener
            }
            FirebaseRepository.login(email, pass) { success, error ->
                if (success) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    showToast("Login Failed: $error")
                }
            }
        }

        registerBtn.setOnClickListener {
            val name = nameEdit.text.toString().trim()
            val email = emailEdit.text.toString().trim()
            val pass = passEdit.text.toString()
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                showToast("Please fill all fields")
                return@setOnClickListener
            }
            FirebaseRepository.register(name, email, pass) { success, error ->
                if (success) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    showToast("Registration Failed: $error")
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
