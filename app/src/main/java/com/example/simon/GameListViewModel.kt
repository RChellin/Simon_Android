package com.example.simon

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simon.database.AppDatabase
import com.example.simon.database.toEntity
import com.example.simon.database.toGameResult
import kotlinx.coroutines.launch


class GameListViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).gameResultDao()
    val gameResults = mutableStateListOf<GameResult>()

    init {
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            val entities = dao.getAllGames()

            gameResults.clear()

            gameResults.addAll(
                entities.map { it.toGameResult() }
            )
        }
    }

    fun addGameResult(result: GameResult) {
        if (result.sequence.isEmpty()) {
            return
        }

        viewModelScope.launch {
            dao.insertGame(result.toEntity())
            loadGames()
        }
    }
}