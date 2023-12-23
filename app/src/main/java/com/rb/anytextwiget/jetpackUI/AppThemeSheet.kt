package com.rb.anytextwiget.jetpackUI

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.rb.anytextwiget.AppUtils
import com.rb.anytextwiget.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppThemeSheet(currentTheme: String, selectedTheme: (theme: String)->Unit, onDismiss: ()->Unit) {
    val fontUtils = FontUtils()

    ModalBottomSheet(onDismissRequest =  onDismiss) {
        Text(
            text = "App Themes",
            fontFamily = fontUtils.openSans(FontWeight.Bold),
            fontSize = TextUnit(28f, TextUnitType.Sp),
            modifier = Modifier.padding(20.dp)
        )

        //Light theme.
        TextButton(modifier = Modifier
            .fillMaxWidth(),
            contentPadding = PaddingValues(15.dp),
            onClick = {
                selectedTheme(AppUtils.LIGHT)
            onDismiss()}
        ) {
            Icon(
                painter = painterResource(id = R.drawable.app_theme_light_35dp),
                contentDescription = "Light App Theme Icon",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(10.dp, 0.dp)
            )
            Text(
                text = "Light Theme",
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontFamily = fontUtils.openSans(FontWeight.Normal),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)

            )

            //Show checked if current theme is light.
            if (currentTheme == AppUtils.LIGHT) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                    contentDescription = "Light theme selected icon",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        }


        //Dark theme.
        TextButton(modifier = Modifier
            .fillMaxWidth(),
            contentPadding = PaddingValues(15.dp),
            onClick = {
                selectedTheme(AppUtils.DARK)
                onDismiss()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.app_theme_dark_35dp),
                contentDescription = "Dark App Theme Icon",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(10.dp, 0.dp)
            )
            Text(
                text = "Dark Theme",
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontFamily = fontUtils.openSans(FontWeight.Normal),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )

            //Show checked if current theme is dark.
            if (currentTheme == AppUtils.DARK) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                    contentDescription = "Dark theme selected icon",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        }


        //System theme.
        TextButton(modifier = Modifier
            .fillMaxWidth().padding(0.dp, 0.dp, 0.dp, 15.dp),
            contentPadding = PaddingValues(15.dp),
            onClick = {
                selectedTheme(AppUtils.FOLLOW_SYSTEM)
                onDismiss()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.app_theme_system_35dp),
                contentDescription = "System App Theme Icon",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(10.dp, 0.dp)
            )
            Text(
                text = "System Auto Theme",
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontFamily = fontUtils.openSans(FontWeight.Normal),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )

            //Show checked if current theme is system.
            if (currentTheme == AppUtils.FOLLOW_SYSTEM) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                    contentDescription = "System Auto theme selected icon",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}