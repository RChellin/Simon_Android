package com.example.simon

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.lifecycle.SavedStateHandle

enum class GameState {
    IDLE,               // schermata appena aperta
    COMPUTER_TURN,      // il computer mostra la sequenza
    PAUSED,             // pausa durante il turno del computer
    PLAYER_TURN,        // il giocatore sta inserendo la sequenza
    GAME_OVER           // il giocatore ha sbagliato
}

class GameViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    // =========================
    // DATI DI GIOCO
    // =========================

    private val availableColors = listOf("R", "G", "B", "M", "Y", "C")

    private val _sequence = mutableStateListOf<String>().apply {
        addAll(savedStateHandle["sequence"] ?: emptyList())
    }
    val playerInput = mutableStateListOf<String>().apply {
        addAll(savedStateHandle["player_input"] ?: emptyList())
    }
    var gameState by mutableStateOf(
        GameState.valueOf(savedStateHandle["game_state"] ?: GameState.IDLE.name)
    )

    var errorMessage by mutableStateOf<String?>(
        savedStateHandle["error_message"]
    )
    var activeColor by mutableStateOf<String?>(null)


    // indice corrente inserito dal giocatore
    private var currentPlayerIndex: Int =
        savedStateHandle["cur_player_index"] ?: 0

    // usato per capire se la prima sequenza è stata completata
    private var firstRoundCompleted: Boolean =
        savedStateHandle["first_round_completed"] ?: false

    // job per gestione animazione computer
    private var computerJob: Job? = null

    private var maxCorrectLength: Int =
        savedStateHandle["max_correct_length"] ?: 0

    var errorIndex: Int = savedStateHandle["error_index"] ?: -1

    // callback esterna già esistente
    var onGameFinished: (GameResult) -> Unit = {}

    private val soundPool: SoundPool

    private val soundIds = mutableMapOf<String, Int>()

    init {
        val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

        soundPool = SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build()

        soundIds["R"] = soundPool.load(application, R.raw.pa, 1)
        soundIds["G"] = soundPool.load(application, R.raw.pe, 1)
        soundIds["B"] = soundPool.load(application, R.raw.po, 1)
        soundIds["M"] = soundPool.load(application, R.raw.paa, 1)
        soundIds["Y"] = soundPool.load(application, R.raw.pi, 1)
        soundIds["C"] = soundPool.load(application, R.raw.pu, 1)

        if (gameState == GameState.COMPUTER_TURN) {
            showSequence()
        }
    }

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
            saveGameState()
            playerInput.clear()
            savePlayerInput()

            for (color in _sequence) {

                while (gameState == GameState.PAUSED) {
                    delay(100)
                }

                if (gameState != GameState.COMPUTER_TURN) return@launch

                activeColor = color
                playSound(color)
                delay(600)

                activeColor = null
                delay(250)
            }

            activeColor = null
            currentPlayerIndex = 0
            saveCurrentPlayerIndex()
            gameState = GameState.PLAYER_TURN
            saveGameState()
        }
    }

    // =========================
    // INPUT GIOCATORE
    // =========================

    fun onColorPressed(color: String) {

        if (gameState != GameState.PLAYER_TURN) return

        playerInput.add(color)
        savePlayerInput()

        // feedback visivo
        activeColor = color
        playSound(color)

        viewModelScope.launch {
            delay(600)
            activeColor = null
        }
        // controllo correttezza input
        if (color != _sequence[currentPlayerIndex]) {
            errorIndex = currentPlayerIndex
            saveErrorIndex()
            errorMessage = "Sequenza errata!"
            saveErrorMessage()
            gameState = GameState.GAME_OVER
            saveGameState()

            return
        }

        currentPlayerIndex++
        saveCurrentPlayerIndex()

        // sequenza completata correttamente
        if (currentPlayerIndex == _sequence.size) {

            maxCorrectLength = _sequence.size
            saveMaxCorrectLength()
            firstRoundCompleted = true
            saveFirstRoundCompleted()

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
            saveGameState()
            return
        }

        if (gameState == GameState.PAUSED) {
            gameState = GameState.COMPUTER_TURN
            saveGameState()
            showSequence()
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

            onGameFinished(
                GameResult(
                    sequence = emptyList(),
                    errorIndex = -1,
                    maxCorrectLength = -1
                )
            )

            return
        }

        gameState = GameState.GAME_OVER
        saveGameState()

        errorIndex = currentPlayerIndex

        if (playerInput.isEmpty()) {
            errorIndex = 0
        }

        saveErrorIndex()

        val result = GameResult(
            sequence = _sequence.toList(),
            errorIndex = errorIndex,
            maxCorrectLength = maxCorrectLength
        )

        onGameFinished(result)
    }

    // =========================
    // GESTIONE BACK
    // =========================

    fun onBackPressed() {

        if (gameState == GameState.GAME_OVER) {

            val result = GameResult(
                sequence = _sequence.toList(),
                errorIndex = errorIndex,
                maxCorrectLength = maxCorrectLength
            )
            onGameFinished(result)

            return
        }

        if (gameState == GameState.IDLE) {

            onGameFinished(
                GameResult(
                    sequence = emptyList(),
                    errorIndex = -1,
                    maxCorrectLength = -1
                )
            )

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
        saveSequence()
    }

    private fun resetGame() {

        computerJob?.cancel()

        _sequence.clear()
        saveSequence()

        playerInput.clear()
        savePlayerInput()

        currentPlayerIndex = 0
        saveCurrentPlayerIndex()

        maxCorrectLength = 0
        saveMaxCorrectLength()

        activeColor = null

        errorMessage = null
        saveErrorMessage()

        errorIndex = -1
        saveErrorIndex()

        firstRoundCompleted = false
        saveFirstRoundCompleted()

        gameState = GameState.IDLE
        saveGameState()
    }

    // =========================
    // HELPERS UI
    // =========================
    private fun saveSequence() {
        savedStateHandle["sequence"] = ArrayList(_sequence)
    }

    private fun savePlayerInput() {
        savedStateHandle["player_input"] = ArrayList(playerInput)
    }

    private fun saveGameState() {
        savedStateHandle["game_state"] = gameState.name
    }

    private fun saveCurrentPlayerIndex() {
        savedStateHandle["cur_player_index"] = currentPlayerIndex
    }

    private fun saveFirstRoundCompleted() {
        savedStateHandle["first_round_completed"] = firstRoundCompleted
    }

    private fun saveMaxCorrectLength() {
        savedStateHandle["max_correct_length"] = maxCorrectLength
    }

    private fun saveErrorMessage() {
        savedStateHandle["error_message"] = errorMessage
    }
    private fun saveErrorIndex() {
        savedStateHandle["error_index"] = errorIndex
    }
    fun isStartEnabled(): Boolean {
        return gameState == GameState.IDLE
    }

    fun isPauseEnabled(): Boolean {
        return gameState == GameState.COMPUTER_TURN || gameState == GameState.PAUSED
    }

    fun isEndGameEnabled(): Boolean {
        return gameState != GameState.IDLE && gameState != GameState.GAME_OVER
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

    private fun playSound(color: String) {

        val soundId = soundIds[color] ?: return

        soundPool.play(
            soundId,
            1f,
            1f,
            1,
            0,
            1f
        )
    }
}
