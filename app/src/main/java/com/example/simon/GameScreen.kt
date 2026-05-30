package com.example.simon

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.BackHandler

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameScreen(
    onGameFinished: (GameResult) -> Unit,
    viewModel: GameViewModel = viewModel()
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
    viewModel.onGameFinished = onGameFinished

    BackHandler {
        viewModel.onBackPressed()
    }

    //GRID di BOTTONI (colori)
    val grid = @Composable {
        BoxWithConstraints {
            val buttonHeight = maxHeight / 3
            val buttonWidth = maxWidth / 2

            Column {
                for (row in 0 until 3) {    //3 righe
                    Row {
                        for (col in 0 until 2) {    //2 colonne
                            val index = row * 2 + col   //calcolo indice

                            Button(
                                onClick = {
                                    viewModel.onColorPressed(colorsL[index])
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor =
                                        if (viewModel.activeColor == colorsL[index])
                                            colorsC[index]
                                        else
                                            colorsC[index].copy(alpha = 0.35f),

                                    disabledContainerColor =
                                        if (viewModel.activeColor == colorsL[index])
                                            colorsC[index]
                                        else
                                            colorsC[index].copy(alpha = 0.35f),

                                    contentColor = Color.Black,
                                    disabledContentColor = Color.Black
                                ),
                                shape = RectangleShape,
                                modifier = Modifier
                                    .width(buttonWidth)
                                    .height(buttonHeight)
                                    .padding(smallSpacing),
                                enabled = viewModel.isColorButtonsEnabled(),
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

    //TEXTBOX che viene popolata dalla sequenza
    val textBox = @Composable {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(spacing)
        ) {
            Text(
                text = viewModel.textAreaContent(), //se la sequenza è vuola uso "-" come placeholder
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
    val buttonStartGame = @Composable {

        Button(
            onClick = {
                viewModel.startGame()
            },
            enabled = viewModel.isStartEnabled()
        ) {
            Text("Avvia partita")
        }
    }

    //BUTTON per salvare la sequenza e passare a Screen2
    val bottonEndGame = @Composable {
        Button(
            onClick = {
                viewModel.endGame()
            },
            enabled = viewModel.isEndGameEnabled()
        ) {
            Text(stringResource(R.string.fine_partita))
        }
    }

    //BUTTON per mettere il gioco in pausa e riprenderlo
    val buttonPauseResume = @Composable {
            Button(
                onClick = {
                    viewModel.togglePause()
                },
                enabled = viewModel.isPauseEnabled()
            ) {
                Text(viewModel.pauseButtonText())
            }

    }
    @Composable
    fun ErrorMessage(errorMessage: String?) {

        errorMessage?.let {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = it,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    //necessario per scroll automatico
    val scrollState = rememberScrollState()
    LaunchedEffect(viewModel.playerInput.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    if (isPortrait) {   //PORTRAIT MODE
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing)
        ) {
            val screenHeight = maxHeight
            //dedico 1/4 dello spazio al textBox ed ai due bottoni, i restanti 3/4 alla grid
            val bottomSectionHeight = screenHeight / 4
            val gridSectionHeight = screenHeight * 3 / 4

            Column(modifier = Modifier.fillMaxSize()) {

                //GRID di bottoni (per aggiungere elementi alla sequenza)
                Box(
                    modifier = Modifier
                        .height(gridSectionHeight)
                        .fillMaxWidth()
                ) {
                    grid()
                }

                Spacer(modifier = Modifier.height(spacing))

                //area dedicata a textBox e bottoni in basso
                Column(
                    modifier = Modifier
                        .height(bottomSectionHeight)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    //TEXT BOX con scroll se cresce troppo
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                    ) {
                        textBox()
                    }
                    ErrorMessage(viewModel.errorMessage)
                    Spacer(modifier = Modifier.height(spacing))

                    //BUTTONS cancella e fine partita
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        buttonStartGame()
                        Spacer(modifier = Modifier.width(spacing))
                        bottonEndGame()
                        Spacer(modifier = Modifier.width(spacing))
                        buttonPauseResume()
                    }
                }
            }
        }
    } else {    //LANDSCAPE MODE
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing)
        ) {

            //GRID di bottoni (per aggiungere elementi alla sequenza)
            Box(modifier = Modifier.weight(1.5f)) {
                grid()
            }

            Spacer(modifier = Modifier.width(spacing))

            //area dedicata a textBox e bottoni in basso
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //TEXT BOX con scroll se cresce troppo
                BoxWithConstraints {

                    val maxTextHeight = maxHeight * (2f / 3f)

                    Box(
                        modifier = Modifier
                            .heightIn(max = maxTextHeight)
                            .verticalScroll(scrollState)
                    ) {
                        textBox()
                    }
                }
                ErrorMessage(viewModel.errorMessage)

                Spacer(modifier = Modifier.height(spacing))

                //BUTTONS cancella e fine partita
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    buttonStartGame()
                    Spacer(modifier = Modifier.width(spacing))
                    bottonEndGame()
                }
                Spacer(modifier = Modifier.height(spacing))

                //BUTTON pausa e ricomincia
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    buttonPauseResume()
                }
            }
        }
    }
}
