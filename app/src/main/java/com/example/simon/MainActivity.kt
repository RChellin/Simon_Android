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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
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
                //tra uno Screen e l'altro passo una Lista di Liste di Stringhe
                //ogni lista di Stringhe interna è una sequenza
                val curListOfList = rememberSaveable { mutableStateListOf<List<String>>() }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        //queste due righe servono per limitare il tempo per le transazioni (pottoni e cambio Screen)
                        enterTransition = { fadeIn(animationSpec = tween(100)) },
                        exitTransition = { fadeOut(animationSpec = tween(100)) },

                        navController = navController, startDestination = "screen1",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("screen1") {
                            Screen1(
                                onGameFinished = { passedList ->
                                    curListOfList.add(passedList)
                                    navController.navigate("screen2")
                                }
                            )
                        }
                        composable("screen2") {
                            Screen2(curListOfList)
                        }
                    }
                }
            }
        }
    }
}
