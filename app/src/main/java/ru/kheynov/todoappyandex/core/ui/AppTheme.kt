package ru.kheynov.todoappyandex.core.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


private val DarkColorPalette = darkColors(
    primary = colorPrimaryDark,
    primaryVariant = colorPrimaryDark,
    secondary = colorAccentDark,
    background = colorBackgroundPrimaryDark,
    surface = colorBackgroundSecondaryDark,
)

private val LightColorPalette = lightColors(
    primary = colorPrimaryLight,
    primaryVariant = colorPrimaryLight,
    secondary = colorAccentLight,
    background = colorBackgroundPrimaryLight,
    surface = colorBackgroundSecondaryLight,
)

val Colors.red: Color get() = if (isLight) redLight else redDark
val Colors.green: Color get() = if (isLight) greenLight else greenDark
val Colors.blue: Color get() = if (isLight) blueLight else blueDark
val Colors.lightBlue: Color get() = if (isLight) lightBlueLight else lightBlueDark
val Colors.separator: Color get() = if (isLight) separatorLight else separatorDark
val Colors.gray: Color get() = if (isLight) grayLightPalette else grayDark
val Colors.grayLight: Color get() = if (isLight) grayLightLight else grayLightDark
val Colors.overlay: Color get() = if (isLight) overlayLight else overlayDark
val Colors.disabled: Color get() = if (isLight) colorDisableLight else colorDisableDark
val Colors.tertiary: Color get() = if (isLight) colorTertiaryLight else colorTertiaryDark
val Colors.elevated: Color get() = if (isLight) colorBackgroundElevatedLight else colorBackgroundElevatedDark

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable ()
    -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    CompositionLocalProvider(LocalSpacing provides Dimensions()) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content,
        )
    }
}

//create preview for light and dark theme
@Preview("Light Theme", widthDp = 360, heightDp = 800)
@Preview("Dark Theme", widthDp = 360, heightDp = 800, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun AppThemePreview() {
    AppTheme {
        Surface {
            Column {
                BoxPreview(
                    background = MaterialTheme.colors.primary,
                    textColor = MaterialTheme.colors.onPrimary,
                    textStyle = MaterialTheme.typography.h1,
                    text = "Primary (LargeTitle)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.primaryVariant,
                    textColor = MaterialTheme.colors.onPrimary,
                    textStyle = MaterialTheme.typography.body1,
                    text = "PrimaryVariant (Body)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.secondary,
                    textColor = MaterialTheme.colors.onSecondary,
                    textStyle = MaterialTheme.typography.h2,
                    text = "Secondary (Title)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.background,
                    textColor = MaterialTheme.colors.onBackground,
                    textStyle = MaterialTheme.typography.body1,
                    text = "Background (Body)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.surface,
                    textColor = MaterialTheme.colors.onSurface,
                    textStyle = MaterialTheme.typography.button,
                    text = "Surface (button)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.red,
                    textColor = MaterialTheme.colors.onError,
                    textStyle = MaterialTheme.typography.subtitle1,
                    text = "Red (subtitle1)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.green,
                    textColor = MaterialTheme.colors.onError,
                    textStyle = MaterialTheme.typography.subtitle2,
                    text = "Green (subtitle2)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.blue,
                    textColor = MaterialTheme.colors.onError,
                    textStyle = MaterialTheme.typography.body1,
                    text = "Blue (body1)",
                )

                BoxPreview(
                    background = MaterialTheme.colors.lightBlue,
                    textColor = MaterialTheme.colors.onError,
                    textStyle = MaterialTheme.typography.body2,
                    text = "LightBlue (body2)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.separator,
                    textColor = MaterialTheme.colors.onError,
                    textStyle = MaterialTheme.typography.caption,
                    text = "Separator (caption)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.gray,
                    textColor = MaterialTheme.colors.onError,
                    textStyle = MaterialTheme.typography.body1,
                    text = "Gray (body)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.grayLight,
                    textColor = MaterialTheme.colors.onError,
                    textStyle = MaterialTheme.typography.body1,
                    text = "GrayLight (body)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.overlay,
                    textColor = MaterialTheme.colors.onError,
                    textStyle = MaterialTheme.typography.body1,
                    text = "Overlay (body)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.disabled,
                    textColor = MaterialTheme.colors.onError,
                    textStyle = MaterialTheme.typography.body1,
                    text = "Disabled (body)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.tertiary,
                    textColor = MaterialTheme.colors.onError,
                    textStyle = MaterialTheme.typography.body1,
                    text = "Tertiary (body)",
                )
                BoxPreview(
                    background = MaterialTheme.colors.elevated,
                    textColor = MaterialTheme.colors.primary,
                    textStyle = MaterialTheme.typography.body1,
                    text = "Elevated (body)",
                )
            }
        }
    }
}

@Composable
fun BoxPreview(
    background: Color,
    textColor: Color,
    textStyle: TextStyle,
    text: String,
) {
    Box(
        modifier = Modifier
            .background(background)
            .fillMaxWidth()
            .border(0.5.dp, Color.Black)
            .height(50.dp)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = textColor,
            style = textStyle,
        )
    }
}
