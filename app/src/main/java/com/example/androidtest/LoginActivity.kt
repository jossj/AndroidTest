package com.example.androidtest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.androidtest.databinding.ActivityLoginBinding
import com.example.androidtest.network.ApiClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text?.toString()?.trim() ?: ""
            val password = binding.etPassword.text?.toString() ?: ""
            if (!validateInputs(username, password)) return@setOnClickListener
            login(username, password)
        }

        binding.tvForgotPassword.setOnClickListener { /* placeholder */ }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login(username: String, password: String) {
        setLoading(true)
        ApiClient.setCredentials(username, password)

        lifecycleScope.launch {
            try {
                val response = ApiClient.service.secure()
                when {
                    response.isSuccessful -> {
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    }
                    response.code() == 401 -> {
                        ApiClient.setCredentials("", "")
                        showError(getString(R.string.error_invalid_credentials))
                    }
                    else -> {
                        ApiClient.setCredentials("", "")
                        showError(getString(R.string.error_server, response.code()))
                    }
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
        binding.btnLogin.isEnabled = !loading
        binding.btnLogin.text =
            if (loading) getString(R.string.logging_in) else getString(R.string.login_button)
        binding.tvLoginError.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.tvLoginError.text = message
        binding.tvLoginError.visibility = View.VISIBLE
    }

    private fun validateInputs(username: String, password: String): Boolean {
        binding.tilUsername.error = null
        binding.tilPassword.error = null
        binding.tvLoginError.visibility = View.GONE

        if (username.isEmpty()) {
            binding.tilUsername.error = getString(R.string.error_username_required)
            return false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_password_required)
            return false
        }
        return true
    }
}
