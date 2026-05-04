package com.example.androidtest.network

import com.example.androidtest.model.Reward
import com.example.androidtest.model.RewardRequest
import com.example.androidtest.model.Student
import com.example.androidtest.model.User
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/public/hello")
    suspend fun hello(): Response<String>

    @GET("api/secure")
    suspend fun secure(): Response<String>

    @GET("api/users")
    suspend fun getUsers(): Response<List<User>>

    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: Long): Response<User>

    @POST("api/users")
    suspend fun createUser(@Body user: User): Response<User>

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: User): Response<User>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>

    @GET("api/students")
    suspend fun getStudents(): Response<List<Student>>

    @GET("api/rewards")
    suspend fun getRewards(): Response<List<Reward>>

    @POST("api/rewards")
    suspend fun createReward(@Body reward: RewardRequest): Response<Reward>
}
