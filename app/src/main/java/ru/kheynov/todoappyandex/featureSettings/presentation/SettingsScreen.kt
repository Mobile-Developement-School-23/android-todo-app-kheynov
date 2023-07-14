package ru.kheynov.todoappyandex.featureSettings.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.core.ui.AppTheme
import ru.kheynov.todoappyandex.core.ui.LocalSpacing
import ru.kheynov.todoappyandex.core.ui.UiTheme
import ru.kheynov.todoappyandex.core.ui.tertiary
import ru.kheynov.todoappyandex.featureSettings.presentation.components.DropDownSelector
import ru.kheynov.todoappyandex.featureSettings.presentation.stateHolders.SettingsState
import ru.kheynov.todoappyandex.featureSettings.presentation.stateHolders.SettingsUiEvent

private const val COLUMN_WIDTH_FRACTION = 0.7f

@Suppress("LongMethod")
@Composable
fun SettingsScreen(
    state: State<SettingsState>,
    onEvent: (SettingsUiEvent) -> Unit,
) {
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )
    Surface(
        modifier = Modifier.background(MaterialTheme.colors.background),
    ) {
        val spacing = LocalSpacing.current
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        onEvent(SettingsUiEvent.NavigateBack)
                    }
                    .padding(spacing.spaceMedium)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(id = R.string.back_button),
                    style = MaterialTheme.typography.body1,
                )
            }
            Divider(modifier = Modifier.padding(bottom = spacing.spaceMedium))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.app_theme),
                    style = MaterialTheme.typography.body1
                )
                DropDownSelector(
                    items = UiTheme.values(),
                    selectedItem = state.value.theme,
                    onItemSelected = { onEvent(SettingsUiEvent.ChangeTheme(it)) },
                )
            }
            Divider(modifier = Modifier.padding(vertical = spacing.spaceMedium))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.fillMaxWidth(COLUMN_WIDTH_FRACTION)) {
                    Text(
                        text = stringResource(id = R.string.notifications),
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        text = stringResource(id = R.string.notifications_setting_description),
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.tertiary
                    )
                }
                Switch(
                    checked = state.value.isNotificationsEnabled,
                    onCheckedChange = { checked ->
                        if (!hasNotificationPermission) {
                            onEvent(SettingsUiEvent.ToggleNotifications(false))
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && checked) {
                                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        } else onEvent(SettingsUiEvent.ToggleNotifications(checked))
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    AppTheme {
        Surface(modifier = Modifier.height(200.dp)) {
            val state = remember {
                mutableStateOf(
                    SettingsState(
                        theme = UiTheme.SYSTEM,
                        isNotificationsEnabled = true
                    )
                )
            }
            SettingsScreen(
                state = state,
                onEvent = {
                    when (it) {
                        is SettingsUiEvent.ChangeTheme ->
                            state.value =
                                state.value.copy(theme = it.theme)

                        SettingsUiEvent.NavigateBack -> {}
                        is SettingsUiEvent.ToggleNotifications ->
                            state.value =
                                state.value.copy(isNotificationsEnabled = it.state)
                    }
                }
            )
        }
    }
}
