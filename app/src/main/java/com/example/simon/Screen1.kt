package com.example.simon

import android.R.attr.maxHeight
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
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

    //GRID di BOTTONI (colori)
    val grid = @Composable {
        BoxWithConstraints {
            val buttonHeight = maxHeight/3
            val buttonWidth = maxWidth/2

            Column {
                for (row in 0 until 3) {    //3 righe
                    Row {
                        for (col in 0 until 2) {    //2 colonne
                            val index = row * 2 + col   //calcolo indice

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
                text = if (sequence.isEmpty()) "-" else sequence.joinToString(", "), //se la sequenza è vuola uso "-" come placeholder
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    //BUTTON per cancellare tutta la sequenza corrente
    val bottonDelete = @Composable {
        OutlinedButton(
            onClick = { sequence.clear() }
        ) {
            Text(stringResource(R.string.cancella))
        }
    }

    //BUTTON per salvare la sequenza e passare a Screen2
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

    //necessario per scroll automatico
    val scrollState = rememberScrollState()
    LaunchedEffect(sequence.size) {
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
            val bottomSectionHeight = screenHeight/4
            val gridSectionHeight = screenHeight*3/4

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

                    Spacer(modifier = Modifier.height(spacing))

                    //BUTTONS cancella e fine partita
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
                verticalArrangement = Arrangement.Center
            ) {

                Column(
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

                    Spacer(modifier = Modifier.height(spacing))

                    //BUTTONS cancella e fine partita
                    Row(
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
}