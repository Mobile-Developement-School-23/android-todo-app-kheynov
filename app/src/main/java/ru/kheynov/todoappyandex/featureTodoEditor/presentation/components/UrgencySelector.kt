package ru.kheynov.todoappyandex.featureTodoEditor.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.core.domain.entities.TodoUrgency
import ru.kheynov.todoappyandex.core.ui.AppTheme
import ru.kheynov.todoappyandex.core.ui.LocalSpacing
import ru.kheynov.todoappyandex.core.ui.tertiary

@Composable
fun UrgencySelector(
    urgency: TodoUrgency,
    onClick: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = spacing.spaceMedium)
            .padding(vertical = spacing.spaceMedium),
    ) {
        Text(
            text = stringResource(id = R.string.urgency),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground,
        )
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        Text(
            when (urgency) {
                TodoUrgency.LOW -> stringResource(R.string.low_urgency)
                TodoUrgency.STANDARD -> stringResource(R.string.standard_urgency)
                TodoUrgency.HIGH -> stringResource(R.string.high_urgency)
            },
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.tertiary,
        )
    }
}

@Preview
@Composable
fun UrgencySelectorPreview() {
    AppTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colors.background)) {
            UrgencySelector(
                urgency = TodoUrgency.LOW,
                onClick = {}
            )
        }
    }
}