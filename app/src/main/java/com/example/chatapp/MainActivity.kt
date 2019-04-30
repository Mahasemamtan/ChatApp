package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val TAG = "RegistrationScreen"

        register_button_register.setOnClickListener {
            val username = username_textview_register.text.toString()
            val email = email_edittext_register.text.toString()
            val password = password_edittext_register.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please, enter valid email and/or password :)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "Username: $username")
            Log.d(TAG, "Email : $email")
            Log.d(TAG, "Password: $password")

            // Firebase authentication to create user with Email and Password
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // else if successful
                    Toast.makeText(this, "User ${email} created!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Successfully created user with UID: ${it.result?.user?.uid}")
                }
        }

        login_textview_register.setOnClickListener {
            Log.i(TAG, "Open Login activity.")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
