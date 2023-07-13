package ru.kheynov.todoappyandex.featureTodoEditor.presentation.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.core.ui.AppTheme
import ru.kheynov.todoappyandex.core.ui.LocalSpacing
import ru.kheynov.todoappyandex.core.ui.tertiary

@Composable
fun TodoTitleInputField(
    text: String,
    onChanged: (String) -> Unit,
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spaceMedium)
            .clip(shape = MaterialTheme.shapes.medium),
    ) {
        BasicTextField(
            value = text,
            onValueChange = onChanged,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .padding(16.dp) // margin left and right
                        .fillMaxWidth()

                        .padding(horizontal = 16.dp, vertical = 12.dp), // inner padding
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.todo_text_prompt),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colors.tertiary
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Preview
@Composable
fun TodoTitleInputFieldPreview() {
    AppTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colors.background)) {
            TodoTitleInputField(text = "", onChanged = {})
        }
    }
}