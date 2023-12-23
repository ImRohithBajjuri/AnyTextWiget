package com.rb.anytextwiget.jetpackUI

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.rb.anytextwiget.AppUtils
import com.rb.anytextwiget.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsNewSheet(context: AppCompatActivity, onDismiss: () -> Unit) {
    val fontUtils = FontUtils()
    val appUtils = AppUtils()
    val appVersion = BuildConfig.VERSION_NAME

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            text = "What's New $appVersion",
            fontFamily = fontUtils.openSans(FontWeight.Bold),
            fontSize = TextUnit(28f, TextUnitType.Sp),
            modifier = Modifier.padding(20.dp)
        )

        Card(
            elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
            modifier = Modifier.padding(10.dp, 0.dp),
        ) {
            Text(text = "Add new release info here", fontSize = TextUnit(18f, TextUnitType.Sp), fontFamily = fontUtils.openSans(
                FontWeight.SemiBold), modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}