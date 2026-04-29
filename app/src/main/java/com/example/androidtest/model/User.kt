package com.example.androidtest.model

data class User(
    val id: Long? = null,
    val username: String,
    val email: String,
    val role: String = "USER"
)
