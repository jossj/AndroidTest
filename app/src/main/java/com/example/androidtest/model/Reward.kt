package com.example.androidtest.model

data class Reward(
    val id: Long? = null,
    val title: String = "",
    val description: String? = null,
    val points: Int? = null,
    val type: String? = null,
    val student: Student? = null
)
