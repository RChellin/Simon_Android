package com.example.simon.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultEntityDao {

    @Query("SELECT * FROM game_results")
    fun getAllGames(): Flow<List<GameResultEntity>>

    @Insert
    suspend fun insertGame(game: GameResultEntity)
}