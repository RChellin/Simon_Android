package com.example.simon.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.simon.GameResult

@Entity(tableName = "game_results")
data class GameResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val sequence: String,
    val errorIndex: Int,
    val maxCorrectLength: Int
)
fun GameResult.toEntity(): GameResultEntity {
    return GameResultEntity(
        id = id,
        sequence = sequence.joinToString(","),
        errorIndex = errorIndex,
        maxCorrectLength = maxCorrectLength
    )
}

fun GameResultEntity.toGameResult(): GameResult {
    return GameResult(
        id = id,
        sequence = sequence.split(","),
        errorIndex = errorIndex,
        maxCorrectLength = maxCorrectLength
    )
}

