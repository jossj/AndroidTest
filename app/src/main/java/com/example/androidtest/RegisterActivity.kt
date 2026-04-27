package com.example.androidtest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtest.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text?.toString()?.trim() ?: ""
            val email = binding.etEmail.text?.toString()?.trim() ?: ""
            val password = binding.etPassword.text?.toString() ?: ""
            val confirm = binding.etConfirmPassword.text?.toString() ?: ""

            if (!validateInputs(name, email, password, confirm)) return@setOnClickListener

            startActivity(Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            finish()
        }

        binding.tvLoginLink.setOnClickListener { finish() }
    }

    private fun validateInputs(name: String, email: String, password: String, confirm: String): Boolean {
        binding.tilFullName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
        binding.tvRegisterError.visibility = View.GONE

        if (name.isEmpty()) {
            binding.tilFullName.error = getString(R.string.error_name_required)
            return false
        }

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

        if (confirm.isEmpty()) {
            binding.tilConfirmPassword.error = getString(R.string.error_confirm_password_required)
            return false
        }

        if (password != confirm) {
            binding.tilConfirmPassword.error = getString(R.string.error_passwords_mismatch)
            return false
        }

        return true
    }
}
