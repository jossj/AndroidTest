package com.example.androidtest.model

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val points: Int,
    val totalPoints: Int
)

data class RewardRow(
    val name: String,
    val typePoints: List<Int?>,
    val total: Int
)
