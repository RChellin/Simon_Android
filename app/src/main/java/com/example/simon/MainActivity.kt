package com.example.simon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simon.ui.theme.SimonTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge display on API level < 35
        enableEdgeToEdge()

        setContent {
            SimonTheme {
                val navController = rememberNavController()
                //tra uno Screen e l'altro passo una Lista di GameResult
                //ogni GameResult interna è una sequenza e un indice d'errore
                val gameResults = rememberSaveable { mutableStateListOf<GameResult>() }
                val selectedGame = rememberSaveable { mutableStateOf<GameResult?>(null) }
                val gameListViewModel: GameListViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        //queste due righe servono per limitare il tempo per le transazioni (pottoni e cambio Screen)
                        enterTransition = { fadeIn(animationSpec = tween(100)) },
                        exitTransition = { fadeOut(animationSpec = tween(100)) },

                        navController = navController, startDestination = "GameListScreen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("GameScreen") {
                            GameScreen(
                                onGameFinished = { result ->

                                    if (result.sequence.isNotEmpty()) {
                                        gameResults.add(result)
                                        gameListViewModel.addGameResult(result)
                                    }

                                    navController.navigate("GameListScreen") {
                                        popUpTo("GameScreen") {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                        composable("GameListScreen") {
                            GameListScreen(
                                gameResults = gameListViewModel.gameResults,
                                onAskDetail = { result ->
                                    selectedGame.value = result
                                    navController.navigate("DetailScreen")
                                },
                                onPlay = {
                                    navController.navigate("GameScreen")
                                }
                            )
                        }
                        composable("DetailScreen") {
                            if (selectedGame.value != null) {
                                DetailScreen(selectedGame.value!!)
                            }
                        }
                    }
                }
            }
        }
    }
}
