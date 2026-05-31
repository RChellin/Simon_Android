package com.example.simon.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simon.GameListViewModel
import com.example.simon.GameResult
import com.example.simon.R
import kotlin.collections.joinToString

//titolo schermata dettaglio
@Composable
fun DetailTitle(
    titleFontSize: TextUnit,
    spacing: Dp,
    smallSpacing: Dp
) {
    Column(
        modifier = Modifier.padding(bottom = spacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.dettaglio),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = titleFontSize,
                letterSpacing = 2.sp
            )
        )

        Spacer(modifier = Modifier.height(smallSpacing))
        //linea decorativa sotto al titolo
        Box(
            modifier = Modifier
                .fillMaxWidth(0.25f)
                .height(2.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(percent = 50)
                )
        )
    }
}
//sezione punteggio
@Composable
fun ScoreSection(
    score: Int,
    scoreBoxSize: Dp,
    scoreFontSize: TextUnit,
    spacing: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.punteggio),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
            )
        )

        Spacer(modifier = Modifier.height(spacing))
        //quadrato che evidenzia il punteggio della partita
        Box(
            modifier = Modifier
                .size(scoreBoxSize)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = score.toString(),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = scoreFontSize
                )
            )
        }
    }
}
//sezione sequenza
@Composable
fun SequenceSection(
    sequence: List<String>,
    sequenceFontSize: TextUnit,
    spacing: Dp,
    smallSpacing: Dp,
    shape: RoundedCornerShape,
    maxHeight: Dp
) {
    //permette di scorrere la sequenza quando è troppo lunga
    val sequenceScrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.sequenza)+":",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(smallSpacing))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxHeight)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .verticalScroll(sequenceScrollState)
                .padding(spacing * 1.5f)
        ) {
            Text(
                text = sequence.joinToString(", "),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = sequenceFontSize
                )
            )
        }
    }
}
//*****schermata dettaglio partita*****
@Composable
fun DetailScreen(
    gameId: Int,
    gameListViewModel: GameListViewModel
) {
    val gameResultState = remember { mutableStateOf<GameResult?>(null) }
    //carica dal database la partita corrispondente all'id ricevuto
    LaunchedEffect(gameId) {
        gameResultState.value = gameListViewModel.getGameById(gameId)
    }

    val gameResult = gameResultState.value

    if (gameResult == null) {
        Text(text = stringResource(R.string.caricamento))
        return
    }
    //controllo orientamento
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    val spacing = 12.dp
    val smallSpacing = 6.dp
    val shape = RoundedCornerShape(16.dp)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing)
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        // dimensioni che cambiano in relazione allo spazio disponibile
        val scoreBoxSize = if (isPortrait) screenWidth * 0.26f else screenHeight * 0.28f
        val titleFontSize = if (isPortrait) 26.sp else 24.sp
        val scoreFontSize = if (isPortrait) 42.sp else 38.sp
        val sequenceFontSize = if (isPortrait) 22.sp else 20.sp

        if (isPortrait) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DetailTitle(
                    titleFontSize = titleFontSize,
                    spacing = spacing,
                    smallSpacing = smallSpacing
                )

                Spacer(modifier = Modifier.height(screenHeight * 0.10f))

                ScoreSection(
                    score = gameResult.maxCorrectLength,
                    scoreBoxSize = scoreBoxSize,
                    scoreFontSize = scoreFontSize,
                    spacing = spacing
                )

                Spacer(modifier = Modifier.height(screenHeight * 0.12f))

                SequenceSection(
                    sequence = gameResult.sequence,
                    sequenceFontSize = sequenceFontSize,
                    spacing = spacing,
                    smallSpacing = smallSpacing,
                    shape = shape,
                    maxHeight = screenHeight * 0.30f
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    DetailTitle(
                        titleFontSize = titleFontSize,
                        spacing = spacing,
                        smallSpacing = smallSpacing
                    )

                    Spacer(modifier = Modifier.height(spacing * 2))

                    ScoreSection(
                        score = gameResult.maxCorrectLength,
                        scoreBoxSize = scoreBoxSize,
                        scoreFontSize = scoreFontSize,
                        spacing = spacing
                    )
                }

                Spacer(modifier = Modifier.width(spacing * 2))

                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    SequenceSection(
                        sequence = gameResult.sequence,
                        sequenceFontSize = sequenceFontSize,
                        spacing = spacing,
                        smallSpacing = smallSpacing,
                        shape = shape,
                        maxHeight = screenHeight *  0.60f
                    )
                }
            }
        }
    }
}