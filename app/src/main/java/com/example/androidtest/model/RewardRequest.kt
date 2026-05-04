package com.example.androidtest.model

data class RewardRequest(
    val title: String,
    val description: String? = null,
    val points: Int,
    val type: String,
    val student: StudentIdRef
)

data class StudentIdRef(val id: Long)
