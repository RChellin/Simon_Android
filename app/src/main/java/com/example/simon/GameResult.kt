package com.example.simon

data class GameResult(
    val sequence: List<String>,
    val errorIndex: Int,
    val maxCorrectLength: Int
)

