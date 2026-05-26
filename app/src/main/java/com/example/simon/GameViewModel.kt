package com.example.simon

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameState {
    IDLE,               // schermata appena aperta
    COMPUTER_TURN,      // il computer mostra la sequenza
    PAUSED,             // pausa durante il turno del computer
    PLAYER_TURN,        // il giocatore sta inserendo la sequenza
    GAME_OVER           // il giocatore ha sbagliato
}

class GameViewModel : ViewModel() {

    // =========================
    // DATI DI GIOCO
    // =========================

    private val availableColors = listOf("R", "G", "B", "M", "Y", "C")

    private val _sequence = mutableStateListOf<String>()
    val playerInput = mutableStateListOf<String>()
    var gameState by mutableStateOf(GameState.IDLE)

    var activeColor by mutableStateOf<String?>(null)

    var errorMessage by mutableStateOf<String?>(null)

    // indice corrente inserito dal giocatore
    private var currentPlayerIndex = 0

    // usato per capire se la prima sequenza è stata completata
    private var firstRoundCompleted = false

    // job per gestione animazione computer
    private var computerJob: Job? = null

    // callback esterna già esistente
    var onGameFinished: ((List<String>) -> Unit)? = null

    // =========================
    // AVVIO PARTITA
    // =========================

    fun startGame() {
        if (gameState != GameState.IDLE) return

        resetGame()

        addNewColor()
        showSequence()
    }

    // =========================
    // TURNO COMPUTER
    // =========================

    private fun showSequence() {

        computerJob?.cancel()

        computerJob = viewModelScope.launch {

            gameState = GameState.COMPUTER_TURN
            playerInput.clear()

            for (color in _sequence) {

                while (gameState == GameState.PAUSED) {
                    delay(100)
                }

                if (gameState != GameState.COMPUTER_TURN) return@launch

                activeColor = color
                delay(600)

                activeColor = null
                delay(250)
            }

            activeColor = null
            currentPlayerIndex = 0
            gameState = GameState.PLAYER_TURN
        }
    }

    // =========================
    // INPUT GIOCATORE
    // =========================

    fun onColorPressed(color: String) {

        if (gameState != GameState.PLAYER_TURN) return

        playerInput.add(color)

        // feedback visivo
        activeColor = color

        viewModelScope.launch {
            delay(600)
            activeColor = null
        }

        // TODO:
        // riproduzione suono del colore

        // controllo correttezza input
        if (color != _sequence[currentPlayerIndex]) {

            errorMessage = "Sequenza errata!"
            gameState = GameState.GAME_OVER

            return
        }

        currentPlayerIndex++

        // sequenza completata correttamente
        if (currentPlayerIndex == _sequence.size) {

            firstRoundCompleted = true

            viewModelScope.launch {

                delay(800)

                addNewColor()

                showSequence()
            }
        }
    }

    // =========================
    // PAUSA / RIPRENDI
    // =========================

    fun togglePause() {

        if (gameState == GameState.COMPUTER_TURN) {
            gameState = GameState.PAUSED
            return
        }

        if (gameState == GameState.PAUSED) {
            gameState = GameState.COMPUTER_TURN
        }
    }

    // =========================
    // FINE PARTITA
    // =========================

    fun endGame() {

        computerJob?.cancel()
        activeColor = null

        // se è in corso la presentazione della prima sequenza la partita non viene salvata
        if (_sequence.size == 1 && gameState == GameState.COMPUTER_TURN) {
            resetGame()

            if (onGameFinished != null) {
                onGameFinished!!(emptyList())
            }

            return
        }

        gameState = GameState.GAME_OVER
        errorMessage = "Partita terminata"

        val partialSequence = mutableListOf<String>()

        partialSequence.addAll(playerInput)

        // Se il giocatore non ha ancora premuto nulla, considero errore sul primo rettangolo
        if (playerInput.isEmpty() && _sequence.isNotEmpty()) {
            partialSequence.add("ERRORE")
        }

        if (onGameFinished != null) {
            onGameFinished!!(playerInput)
        }
    }

    // =========================
    // GESTIONE BACK
    // =========================

    fun onBackPressed() {

        if (gameState == GameState.GAME_OVER) {
            if (onGameFinished != null) {
                onGameFinished!!(playerInput.toList())
            }

            return
        }

        if (gameState == GameState.IDLE) {
            if (onGameFinished != null) {
                onGameFinished!!(emptyList())
            }

            return
        }

        endGame()
    }

    // =========================
    // UTILS
    // =========================

    private fun addNewColor() {
        _sequence.add(
            availableColors.random(Random(System.currentTimeMillis()))
        )
    }

    private fun resetGame() {

        computerJob?.cancel()

        _sequence.clear()
        playerInput.clear()

        currentPlayerIndex = 0

        activeColor = null
        errorMessage = null

        firstRoundCompleted = false

        gameState = GameState.IDLE
    }

    // =========================
    // HELPERS UI
    // =========================

    fun isStartEnabled(): Boolean {
        return gameState == GameState.IDLE
    }

    fun isPauseEnabled(): Boolean {
        return gameState == GameState.COMPUTER_TURN ||
                gameState == GameState.PAUSED
    }

    fun isEndGameEnabled(): Boolean {
        return gameState != GameState.IDLE &&
                gameState != GameState.GAME_OVER
    }

    fun isColorButtonsEnabled(): Boolean {
        return gameState == GameState.PLAYER_TURN
    }

    fun pauseButtonText(): String {
        return if (gameState == GameState.PAUSED) {
            "Riprendi"
        } else {
            "Pausa"
        }
    }

    fun textAreaContent(): String {

        if (gameState == GameState.COMPUTER_TURN || gameState == GameState.PAUSED) {
            return ""
        }

        if (playerInput.isEmpty()) {
            return "-"
        }

        return playerInput.joinToString(", ")
    }
}
