package com.example.simon

import android.content.res.Configuration
import android.provider.Settings.Global.getString
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Screen1(
    onGameFinished: (List<String>) -> Unit
) {
    val spacing = 12.dp
    val smallSpacing = 6.dp

    val colorsC = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Magenta,
        Color.Yellow,
        Color.Cyan
    )

    val colorsL = listOf("R", "G", "B", "M", "Y", "C")

    var sequence = rememberSaveable { mutableStateListOf<String>() }

    val grid = @Composable {
        BoxWithConstraints {
            val buttonHeight = maxHeight/3
            val buttonWidth = maxWidth/2

            Column {
                for (row in 0 until 3) {
                    Row {
                        for (col in 0 until 2) {
                            val index = row * 2 + col

                            Button(
                                onClick = { sequence.add(colorsL[index]) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorsC[index],
                                    contentColor = Color.Black
                                ),
                                shape = RectangleShape,
                                modifier = Modifier
                                    .width(buttonWidth)
                                    .height(buttonHeight)
                                    .padding(smallSpacing)
                            ) {
                                Text(
                                    colorsL[index],
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val textBox = @Composable {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(spacing)
        ) {
            Text(
                text = if (sequence.isEmpty()) "—" else sequence.joinToString(" • "),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    val bottonDelete = @Composable {
        OutlinedButton(
            onClick = { sequence.clear() }
        ) {
            Text(stringResource(R.string.cancella))
        }
    }

    val bottonEndGame = @Composable {
        Button(
            onClick = {
                onGameFinished(sequence.toList())
                sequence.clear()
            }
        ) {
            Text(stringResource(R.string.fine_partita))
        }
    }

    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isPortrait) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing)
        ) {
            val screenHeight = maxHeight

            val bottomSectionHeight = screenHeight/4
            val gridSectionHeight = screenHeight*3/4

            Column(modifier = Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier
                        .height(gridSectionHeight)
                        .fillMaxWidth()
                ) {
                    grid()
                }

                Spacer(modifier = Modifier.height(spacing))

                Column(
                    modifier = Modifier
                        .height(bottomSectionHeight)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    // TEXT BOX con scroll se cresce troppo
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        textBox()
                    }

                    Spacer(modifier = Modifier.height(spacing))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        bottonDelete()
                        Spacer(modifier = Modifier.width(spacing))
                        bottonEndGame()
                    }
                }
            }
        }
    } else {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing)
        ) {

            Box(modifier = Modifier.weight(1.5f)) {
                grid()
            }

            Spacer(modifier = Modifier.width(spacing))

            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {

                textBox()

                Spacer(modifier = Modifier.height(spacing))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    bottonDelete()
                    Spacer(modifier = Modifier.width(spacing))
                    bottonEndGame()
                }
            }
        }
    }
}