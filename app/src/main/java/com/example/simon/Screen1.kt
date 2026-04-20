package com.example.simon

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Screen1(
    onGameFinished: (List<String>) -> Unit
) {
    val colorsC = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Magenta,
        Color.Yellow,
        Color.Cyan
    )

    val colorsL = listOf("R", "G", "B", "M", "Y", "C")

    var sequence by remember { mutableStateOf(listOf<String>()) }


    val grid = @Composable {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    for (col in 0 until 2) {
                        val index = row * 2 + col
                        Button(
                            onClick = {
                                sequence = sequence + colorsL[index]
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorsC[index],
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp),
                            shape = RectangleShape
                        ) {
                            Text(colorsL[index])
                        }
                    }
                }
            }
        }
    }

    val textBox = @Composable {
        Text(
            text = sequence.joinToString(", "),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .border(1.dp, Color.Gray)
                .padding(8.dp)
        )
    }

    val bottonDelete = @Composable {
        Button(onClick = {
            // Cancella
            sequence = emptyList()
        }) {
            Text("Cancella")
        }
    }

    val bottonEndGame = @Composable {
        Button(onClick = {
            // Fine partita
            val finalSequence = sequence
            sequence = emptyList()
            onGameFinished(finalSequence)
        }) {
            Text("Fine partita")
        }
    }

    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    if (isPortrait) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                grid()
            }

            Spacer(modifier = Modifier.height(16.dp))
            textBox()
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                bottonDelete()
                bottonEndGame()
            }
        }
    } else {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(modifier = Modifier.weight(1.5f)) {
                grid()
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                textBox()
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    bottonDelete()
                    bottonEndGame()
                }
            }
        }
    }
}