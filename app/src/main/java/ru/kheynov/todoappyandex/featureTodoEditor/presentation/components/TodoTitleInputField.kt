package ru.kheynov.todoappyandex.featureTodoEditor.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.core.ui.AppTheme
import ru.kheynov.todoappyandex.core.ui.LocalSpacing
import ru.kheynov.todoappyandex.core.ui.separator
import ru.kheynov.todoappyandex.core.ui.tertiary

@Composable
fun TodoTitleInputField(
    text: String,
    onChanged: (String) -> Unit,
) {
    val spacing = LocalSpacing.current
    Card(
        elevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spaceMedium)
            .clip(shape = MaterialTheme.shapes.large)
            .background(MaterialTheme.colors.surface)
    ) {
        BasicTextField(
            value = text,
            cursorBrush = SolidColor(MaterialTheme.colors.onSurface),
            textStyle = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onSurface
            ),

            onValueChange = onChanged,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .border(1.dp, color = MaterialTheme.colors.separator)
                        .padding(
                            horizontal = spacing.spaceSmall,
                            vertical = spacing.spaceSmall
                        ),
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.todo_text_prompt),
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.tertiary
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TodoTitleInputFieldPreview() {
    AppTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colors.background)) {
            TodoTitleInputField(text = "Aboba", onChanged = {})
        }
    }
}