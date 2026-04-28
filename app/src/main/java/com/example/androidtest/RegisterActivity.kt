package com.example.androidtest

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.androidtest.databinding.ActivityRegisterBinding
import com.example.androidtest.model.User
import com.example.androidtest.network.ApiClient
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.tvLoginLink.setOnClickListener { finish() }

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text?.toString()?.trim() ?: ""
            val email = binding.etEmail.text?.toString()?.trim() ?: ""
            val password = binding.etPassword.text?.toString() ?: ""
            val confirm = binding.etConfirmPassword.text?.toString() ?: ""
            if (!validateInputs(name, email, password, confirm)) return@setOnClickListener
            register(name, email)
        }
    }

    private fun register(username: String, email: String) {
        setLoading(true)
        // POST /api/users is a protected endpoint; admin credentials are used to create the record.
        ApiClient.setCredentials("admin", "admin123")

        lifecycleScope.launch {
            try {
                val response = ApiClient.service.createUser(
                    User(username = username, email = email, role = "USER")
                )
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@RegisterActivity,
                        getString(R.string.register_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    ApiClient.setCredentials("", "")
                    showError(getString(R.string.error_register_failed, response.code()))
                }
            } catch (e: Exception) {
                ApiClient.setCredentials("", "")
                showError(getString(R.string.error_network))
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnRegister.isEnabled = !loading
        binding.btnRegister.text =
            if (loading) getString(R.string.registering) else getString(R.string.register_button)
        binding.tvRegisterError.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.tvRegisterError.text = message
        binding.tvRegisterError.visibility = View.VISIBLE
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
