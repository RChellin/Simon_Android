package com.example.simon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    curList: MutableList<String>
) {
    val spacing = 12.dp
    val smallSpacing = 6.dp
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {

        //NUMBER di elementi per la singola sequnza in esame
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
                text = curList.size.toString(),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.width(spacing))
        
        //SEQUENCE degli elementi
        Text(
            //se non ho inserito elementi scrivo "successione vuota"
            text = if (curList.isEmpty()) stringResource(R.string.successione_vuota) else curList.joinToString(
                ", "
            ),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}