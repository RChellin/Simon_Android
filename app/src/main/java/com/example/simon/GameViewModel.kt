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
    val sequence: List<String> = _sequence

    private val _playerInput = mutableStateListOf<String>()
    val playerInput: List<String> = _playerInput

    var gameState by mutableStateOf(GameState.IDLE)
        private set

    var activeColor by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

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
            _playerInput.clear()

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

        _playerInput.add(color)

        // feedback visivo
        activeColor = color

        viewModelScope.launch {
            delay(200)
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

        when (gameState) {

            GameState.COMPUTER_TURN -> {
                gameState = GameState.PAUSED
            }

            GameState.PAUSED -> {
                gameState = GameState.COMPUTER_TURN
            }

            else -> {}
        }
    }

    // =========================
    // FINE PARTITA
    // =========================

    fun endGame() {

        computerJob?.cancel()

        // se siamo ancora alla prima sequenza
        // la partita non viene salvata
        if (!firstRoundCompleted && _sequence.size == 1) {

            resetGame()

            onGameFinished?.invoke(emptyList())

            return
        }

        // comportamento equivalente a errore
        gameState = GameState.GAME_OVER

        val partialSequence = buildList {

            addAll(_playerInput)

            // se non ha ancora premuto nulla
            if (_playerInput.isEmpty() && _sequence.isNotEmpty()) {
                add("ERRORE")
            }
        }

        onGameFinished?.invoke(partialSequence)
    }

    // =========================
    // GESTIONE BACK
    // =========================

    fun onBackPressed() {

        when (gameState) {

            GameState.GAME_OVER -> {

                onGameFinished?.invoke(_playerInput.toList())
            }

            GameState.IDLE -> {

                onGameFinished?.invoke(emptyList())
            }

            else -> {

                // se la partita non è terminata
                // comportamento equivalente a "Fine partita"
                endGame()
            }
        }
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
        _playerInput.clear()

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

        return when (gameState) {

            GameState.COMPUTER_TURN,
            GameState.PAUSED -> ""

            else -> {
                if (_playerInput.isEmpty()) "-"
                else _playerInput.joinToString(", ")
            }
        }
    }
}
