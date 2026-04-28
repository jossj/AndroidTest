package com.example.androidtest

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtest.adapter.UserAdapter
import com.example.androidtest.databinding.ActivityMenuBinding
import com.example.androidtest.network.ApiClient
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val adapter = UserAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter

        loadUsers()
    }

    private fun loadUsers() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
        binding.rvUsers.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = ApiClient.service.getUsers()
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    adapter.submitList(users)
                    binding.rvUsers.visibility = View.VISIBLE
                } else {
                    showError(getString(R.string.error_server, response.code()))
                }
            } catch (e: Exception) {
                showError(getString(R.string.error_network))
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
    }
}
