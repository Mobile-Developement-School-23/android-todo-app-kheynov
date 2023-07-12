package ru.kheynov.todoappyandex.featureSettings.presentation.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.kheynov.todoappyandex.core.ui.AppTheme
import ru.kheynov.todoappyandex.core.ui.LocalSpacing
import ru.kheynov.todoappyandex.core.ui.UiTheme

@Composable
fun DropDownSelector(
    items: Array<UiTheme>,
    selectedItem: UiTheme,
    onItemSelected: (UiTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val spacing = LocalSpacing.current
    Row(
        modifier = modifier
            .clickable {
                expanded = true
            }
            .padding(
                vertical = spacing.spaceSmall,
                horizontal = spacing.spaceMedium
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = selectedItem.title.asString(),
            style = MaterialTheme.typography.body1,
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = null,
        )
        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            items.forEach {
                DropdownMenuItem(onClick = {
                    onItemSelected(it)
                    expanded = false
                }) {
                    Text(
                        text = it.title.asString(),
                        style = MaterialTheme.typography.body1,
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DropDownSelectorPreview() {
    AppTheme {
        Surface {
            DropDownSelector(
                items = UiTheme.values(),
                selectedItem = UiTheme.LIGHT,
                onItemSelected = {},
            )
        }
    }
}