package ru.kheynov.todoappyandex.featureTodoEditor.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.core.ui.AppTheme
import ru.kheynov.todoappyandex.core.ui.LocalSpacing


@Composable
fun EditorTopBar(
    onCloseClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .clickable(onClick = onCloseClick)
                .padding(spacing.spaceMedium),
            imageVector = Icons.Filled.Close,
            tint = MaterialTheme.colors.onBackground,
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .clickable(onClick = onSaveClick)
                .padding(spacing.spaceMedium),
            style = MaterialTheme.typography.button,
            text = stringResource(id = R.string.save).uppercase(),
            color = MaterialTheme.colors.secondary,
        )
    }
}

@Preview
@Composable
fun EditorTopBarPreview() {
    AppTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colors.background)) {
            EditorTopBar(
                onCloseClick = {},
                onSaveClick = {}
            )
        }
    }
}