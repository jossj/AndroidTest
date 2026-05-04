package com.example.androidtest.model

data class Student(
    val id: Long? = null,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val classRoom: ClassRoom? = null
)

data class ClassRoom(
    val id: Long? = null,
    val name: String = "",
    val yearLevel: String = ""
)
