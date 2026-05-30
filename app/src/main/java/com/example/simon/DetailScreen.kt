package com.example.simon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.collections.joinToString

@Composable
fun DetailScreen(
    gameId: Int,
    gameListViewModel: GameListViewModel
) {
    val gameResultState = remember { mutableStateOf<GameResult?>(null) }

    LaunchedEffect(gameId) {
        gameResultState.value = gameListViewModel.getGameById(gameId)
    }

    val gameResult = gameResultState.value

    if (gameResult == null) {
        Text("Caricamento...")
        return
    }

    val spacing = 12.dp
    val smallSpacing = 6.dp
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing)
    ) {

        Column(modifier = Modifier.padding(bottom = spacing)) {

            //TITLE della pagina
            Text(
                text = "Dettaglio Partita",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(smallSpacing))

            //LINE per stile
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.15f)
                    .height(2.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(percent = 50)
                    )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(spacing),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(
                        horizontal = spacing * 0.75f,
                        vertical = smallSpacing
                    )
            ) {
                Text(
                    text = gameResult.maxCorrectLength.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.width(spacing))

            Text(
                text = gameResult.sequence.joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}