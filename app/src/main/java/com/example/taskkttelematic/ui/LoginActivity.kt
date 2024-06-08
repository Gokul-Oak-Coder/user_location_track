package com.example.taskkttelematic.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.taskkttelematic.MainActivity
import com.example.taskkttelematic.databinding.ActivityLoginBinding
import com.example.taskkttelematic.viewModel.UserViewModel


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            val email = binding.emailText.text.toString()
            val password = binding.passText.text.toString()

            viewModel.login(email, password) { success, message, fullName ->
                runOnUiThread {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        // Show user's full name in MainActivity or redirect
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("USER_FULL_NAME", fullName)
                        intent.putExtra("USER_EMAIL",email)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        binding.signupText.setOnClickListener{
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}