package com.eaglecleaners.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)
        emailField = findViewById(R.id.email_field)
        passwordField = findViewById(R.id.password_field)
        loginButton = findViewById(R.id.btn_login)
        loginButton.setOnClickListener {
            login(emailField.text.toString(), passwordField.text.toString())
        }
        auth = Firebase.auth
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    getSharedPreferences("loginStatus", Context.MODE_PRIVATE).edit().putBoolean("isLoggedIn", true)
                            ?.apply()
                    startActivity(Intent(this, ManageRequestsActivity::class.java))
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    companion object {
        private const val TAG = "AdminLogin"
    }
}