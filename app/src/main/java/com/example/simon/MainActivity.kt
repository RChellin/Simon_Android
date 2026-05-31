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
                //viewmodel condiviso tra lista e dettaglio delle partite
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
                                    //salva la partita conclusa e torna alla lista
                                    gameListViewModel.addGameResult(result)

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
                                //passa al dettaglio usando l'id della partita salvata
                                onAskDetail = { result ->
                                    navController.navigate("DetailScreen/${result.id}")
                                },
                                //avvia una nuova partita
                                onPlay = {
                                    navController.navigate("GameScreen")
                                }
                            )
                        }
                        composable("DetailScreen/{gameId}") { backStackEntry ->
                            //recupera l'id della partita dalla route
                            val gameId = backStackEntry.arguments?.getString("gameId")?.toIntOrNull()
                            if (gameId != null) {
                                DetailScreen(
                                    gameId = gameId,
                                    gameListViewModel = gameListViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}