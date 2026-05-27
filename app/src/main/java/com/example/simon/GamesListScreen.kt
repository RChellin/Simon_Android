package com.example.simon

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp


@Composable
fun GameListScreen(
    gameResults: List<GameResult>,
    onAskDetail: (GameResult) -> Unit,
    onPlay: () -> Unit
) {
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
                text = stringResource(R.string.recap_partite),
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

        BoxWithConstraints(
            modifier = Modifier.weight(1f)
        ) {

            val maxTextHeight = maxHeight * (5f / 6f)

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = maxTextHeight),
                    verticalArrangement = Arrangement.spacedBy(smallSpacing)
                ) {

                    items(gameResults.reversed()) { game ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(spacing)
                                .clickable(
                                    onClick = { onAskDetail(game) }
                                ),
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
                                    text = game.maxCorrectLength.toString(),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(spacing))

                            val correct = game.sequence
                                .take(game.errorIndex)
                                .joinToString(", ")

                            val wrong = game.sequence
                                .drop(game.errorIndex)
                                .joinToString(", ")

                            Text(
                                text = buildAnnotatedString {
                                    append(correct)

                                    if (correct.isNotEmpty() && wrong.isNotEmpty()) {
                                        append(", ")
                                    }

                                    withStyle(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(wrong)
                                    }
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing))

                ExtendedFloatingActionButton(
                    onClick = { onPlay() },
                    icon = {
                        Icon(
                            Icons.Filled.Gamepad,
                            contentDescription = "Gioca"
                        )
                    },
                    text = {
                        Text(text = stringResource(R.string.gioca))
                    },
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}