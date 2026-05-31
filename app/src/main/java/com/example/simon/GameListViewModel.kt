package com.example.simon

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simon.database.AppDatabase
import com.example.simon.database.toEntity
import com.example.simon.database.toGameResult
import kotlinx.coroutines.launch

//viewModel che gestisce la lista delle partite salvate nel database
class GameListViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).gameResultDao()
    val gameResults = mutableStateListOf<GameResult>()

    //carica le partite salvate appena viene creato il viewModel
    init {
        loadGames()
    }

    //legge tutti i Game dal database e aggiorna la lista mostrata dalla UI
    private fun loadGames() {
        viewModelScope.launch {
            val entities = dao.getAllGames()
            gameResults.clear()
            gameResults.addAll(entities.map { it.toGameResult() })
        }
    }

    //salva un nuovo Game nel database e ricarica la lista
    fun addGameResult(result: GameResult) {
        if (result.sequence.isEmpty()) {
            return
        }

        viewModelScope.launch {
            dao.insertGame(result.toEntity())
            loadGames()
        }
    }
    //prende dal database il Game con l'id richiesto
    suspend fun getGameById(id: Int): GameResult? {
        return dao.getGameById(id)?.toGameResult()
    }
}