package com.example.androidtest

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtest.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMenuItem1.setOnClickListener {
            Toast.makeText(this, "Item 1 selected", Toast.LENGTH_SHORT).show()
        }
        binding.btnMenuItem2.setOnClickListener {
            Toast.makeText(this, "Item 2 selected", Toast.LENGTH_SHORT).show()
        }
        binding.btnMenuItem3.setOnClickListener {
            Toast.makeText(this, "Item 3 selected", Toast.LENGTH_SHORT).show()
        }
    }
}
