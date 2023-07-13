package ru.kheynov.todoappyandex.featureTodoEditor.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.core.ui.AppTheme
import ru.kheynov.todoappyandex.core.ui.LocalSpacing
import ru.kheynov.todoappyandex.core.ui.disabled
import ru.kheynov.todoappyandex.core.ui.red

@Composable
fun DeleteButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .clickable(enabled = enabled, onClick = onClick)
            .padding(spacing.spaceMedium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val tint =
            if (enabled) MaterialTheme.colors.red
            else MaterialTheme.colors.disabled

        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = null,
            tint = tint,
        )
        Spacer(modifier = Modifier.width(spacing.spaceSmall))
        Text(
            text = stringResource(id = R.string.delete),
            color = tint,
            style = MaterialTheme.typography.body1,
        )
    }
}

@Preview
@Composable
fun DeleteButtonPreview() {
    AppTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colors.background)) {
            DeleteButton(enabled = true, onClick = {})
        }
    }
}