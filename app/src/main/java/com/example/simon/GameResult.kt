package com.example.simon

data class GameResult(
    val id: Int = 0,
    val sequence: List<String>,
    val errorIndex: Int,
    val maxCorrectLength: Int
)

