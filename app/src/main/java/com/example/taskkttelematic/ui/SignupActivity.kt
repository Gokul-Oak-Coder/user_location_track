package com.example.taskkttelematic.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.taskkttelematic.databinding.ActivitySignupBinding
import com.example.taskkttelematic.viewModel.UserViewModel

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupBtn.setOnClickListener {
            val email = binding.emailTextSignup.text.toString()
            val password = binding.passTextSignup.text.toString()
            val firstName = binding.firstNameText.text.toString()
            val lastName = binding.lastNameText.text.toString()

            viewModel.signup(email, password, firstName, lastName) { success, message ->
                runOnUiThread {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        // Redirect to login or main activity
                        finish()
                    }
                }
            }
        }

        binding.loginText.setOnClickListener{
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}