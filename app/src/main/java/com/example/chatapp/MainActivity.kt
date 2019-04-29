package com.example.chatapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val TAG = "RegistrationScreen"
        val button = register_button_register

        button.setOnClickListener {
            val username = username_textview_register.text.toString()
            val email = email_edittext_register.text.toString()
            val password = password_edittext_register.text.toString()

            Log.d(TAG, "Username: $username")
            Log.d(TAG, "Email : $email")
            Log.d(TAG, "Password: $password")
        }

        login_textview_register.setOnClickListener {
            Log.i(TAG, "Open Login activity.")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
