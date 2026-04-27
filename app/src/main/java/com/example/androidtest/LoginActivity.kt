package com.example.androidtest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtest.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim() ?: ""
            val password = binding.etPassword.text?.toString() ?: ""

            if (!validateInputs(email, password)) return@setOnClickListener

            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }

        binding.tvForgotPassword.setOnClickListener {
            // Placeholder for forgot password flow
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tvLoginError.visibility = View.GONE

        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_email_required)
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_email_invalid)
            return false
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_password_required)
            return false
        }

        if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.error_password_short)
            return false
        }

        return true
    }
}
