package ru.kheynov.todoappyandex.featureTodoEditor.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.core.domain.entities.TodoUrgency
import ru.kheynov.todoappyandex.core.ui.AppTheme
import ru.kheynov.todoappyandex.core.ui.LocalSpacing

@Composable
fun BottomSheetContent(
    selected: TodoUrgency,
    onUrgencySelected: (TodoUrgency) -> Unit,
) {
    val spacing = LocalSpacing.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Text(text = stringResource(id = R.string.choose_urgency))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.spaceMedium),
                horizontalArrangement = Arrangement.Center,
            ) {
                TodoUrgency.values().forEach { urgency ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = spacing.spaceMedium)
                            .clip(MaterialTheme.shapes.medium)
                            .selectable(
                                selected = urgency == selected,
                                onClick = { onUrgencySelected(urgency) }
                            )
                            .background(
                                if (urgency == selected) MaterialTheme.colors.secondary
                                else MaterialTheme.colors.background
                            )
                            .padding(spacing.spaceSmall)
                    ) {
                        Text(
                            text = when (urgency) {
                                TodoUrgency.LOW -> stringResource(R.string.low_urgency)
                                TodoUrgency.STANDARD -> stringResource(R.string.standard_urgency)
                                TodoUrgency.HIGH -> stringResource(R.string.high_urgency)
                            },
                            style = MaterialTheme.typography.button,
                            color = if (urgency == selected) MaterialTheme.colors.onSecondary
                            else MaterialTheme.colors.onBackground
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(spacing.spaceLarge))
        }
    }
}

@Preview(widthDp = 500)
@Composable
fun BottomSheetContentPreview() {
    AppTheme {
        BottomSheetContent(selected = TodoUrgency.STANDARD, onUrgencySelected = {})
    }
}