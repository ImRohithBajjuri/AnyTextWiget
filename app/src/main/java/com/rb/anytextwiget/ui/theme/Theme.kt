package com.rb.anytextwiget.ui.theme

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.rb.anytextwiget.AppUtils
import com.rb.anytextwiget.jetpackUI.disableAds

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)


var darkTheme = mutableStateOf(false)

var roundedCorners = mutableIntStateOf(30)

var cardHorzPadding = mutableIntStateOf(10)


fun updateThemePref(selectedTheme: String, systemPref: Boolean) {
    when (selectedTheme) {
        AppUtils.LIGHT -> darkTheme.value = false
        AppUtils.DARK -> darkTheme.value = true
        AppUtils.FOLLOW_SYSTEM -> darkTheme.value = systemPref
        else -> {
            darkTheme.value = false
        }
    }
}

fun updateCornerRadiiWithPadding(cornersEnabled: Boolean) {
    if (cornersEnabled) {
        cardHorzPadding.intValue = 10
        roundedCorners.intValue = 30
    } else {
        cardHorzPadding.intValue = 0
        roundedCorners.intValue = 0
    }
}

@Composable
fun AnyTextWigetTheme(
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    //Get the current theme.
    updateThemePref(
        selectedTheme = LocalContext.current.getSharedPreferences(
            "apppref",
            MODE_PRIVATE
        ).getString("apptheme", AppUtils.LIGHT)!!,
        isSystemInDarkTheme()
    )

    //Get the current corners.
    updateCornerRadiiWithPadding(cornersEnabled = LocalContext.current.getSharedPreferences(
        "apppref",
        MODE_PRIVATE
    ).getBoolean("roundcorners", true))

    //Get current disable ads.
    disableAds.value = LocalContext.current.getSharedPreferences(
        "apppref",
        MODE_PRIVATE
    ).getBoolean("disableads", true)

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme.value) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                context
            )
        }

        darkTheme.value -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !darkTheme.value
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = Shapes(
            extraSmall = ShapeDefaults.ExtraSmall.copy(CornerSize(roundedCorners.intValue.dp)),
            small = ShapeDefaults.Small.copy(CornerSize(roundedCorners.intValue.dp)),
            medium = ShapeDefaults.Medium.copy(CornerSize(roundedCorners.intValue.dp)),
            large = ShapeDefaults.Large.copy(CornerSize(roundedCorners.intValue.dp)),
            extraLarge = ShapeDefaults.ExtraLarge.copy(CornerSize(roundedCorners.intValue.dp))
        ))
}