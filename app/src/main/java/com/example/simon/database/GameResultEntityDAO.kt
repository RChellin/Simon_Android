package com.example.simon.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultEntityDao {

    @Query("SELECT * FROM game_results ORDER BY id DESC")
    suspend fun getAllGames(): List<GameResultEntity>
    @Insert
    suspend fun insertGame(game: GameResultEntity)

    @Query("SELECT * FROM game_results WHERE id = :id")
    suspend fun getGameById(id: Int): GameResultEntity?
}