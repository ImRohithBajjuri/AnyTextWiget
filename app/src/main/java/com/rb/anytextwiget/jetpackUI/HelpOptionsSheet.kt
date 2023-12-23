package com.rb.anytextwiget.jetpackUI

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.rb.anytextwiget.HelpInfo
import com.rb.anytextwiget.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpOptionsSheet(context: AppCompatActivity, onDismiss: () -> Unit) {
    val fontUtils = FontUtils()
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            text = "Help",
            fontFamily = fontUtils.openSans(FontWeight.Bold),
            fontSize = TextUnit(28f, TextUnitType.Sp),
            modifier = Modifier.padding(20.dp)
        )

        TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
            val intent = Intent(context, HelpInfo::class.java)
            intent.putExtra("from", "helpCreate")
            context.startActivity(intent)
            if (Build.VERSION.SDK_INT >= 34) {
                (context as AppCompatActivity).overrideActivityTransition(
                    Activity.OVERRIDE_TRANSITION_OPEN,
                    R.anim.activity_open,
                    R.anim.activity_pusher
                )
            } else {
                (context).overridePendingTransition(
                    R.anim.activity_open,
                    R.anim.activity_pusher
                )
            }
        }, contentPadding = PaddingValues(15.dp)) {
            Text(
                text = "How to create a widget",
                fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                fontSize = TextUnit(18f, TextUnitType.Sp),
                modifier = Modifier.weight(1f)
            )
        }

        TextButton(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(15.dp), onClick = {
            val intent = Intent(context, HelpInfo::class.java)
            intent.putExtra("from", "helpPlace")
            context.startActivity(intent)
            if (Build.VERSION.SDK_INT >= 34) {
                (context as AppCompatActivity).overrideActivityTransition(
                    Activity.OVERRIDE_TRANSITION_OPEN,
                    R.anim.activity_open,
                    R.anim.activity_pusher
                )
            } else {
                (context).overridePendingTransition(
                    R.anim.activity_open,
                    R.anim.activity_pusher
                )
            }
        }) {
            Text(
                text = "How to place widgets on home screen",
                fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                fontSize = TextUnit(18f, TextUnitType.Sp),
                modifier = Modifier.weight(1f)
            )
        }

        TextButton(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(15.dp), onClick = {
            val intent = Intent(context, HelpInfo::class.java)
            intent.putExtra("from", "helpSave")
            context.startActivity(intent)
            if (Build.VERSION.SDK_INT >= 34) {
                (context as AppCompatActivity).overrideActivityTransition(
                    Activity.OVERRIDE_TRANSITION_OPEN,
                    R.anim.activity_open,
                    R.anim.activity_pusher
                )
            } else {
                (context).overridePendingTransition(
                    R.anim.activity_open,
                    R.anim.activity_pusher
                )
            }
        }) {
            Text(
                text = "How to save a widget as file",
                fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                fontSize = TextUnit(18f, TextUnitType.Sp),
                modifier = Modifier.weight(1f)
            )
        }



        TextButton(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(15.dp), onClick = {
            val intent = Intent(context, HelpInfo::class.java)
            intent.putExtra("from", "helpImport")
            context.startActivity(intent)
            if (Build.VERSION.SDK_INT >= 34) {
                (context as AppCompatActivity).overrideActivityTransition(
                    Activity.OVERRIDE_TRANSITION_OPEN,
                    R.anim.activity_open,
                    R.anim.activity_pusher
                )
            } else {
                (context).overridePendingTransition(
                    R.anim.activity_open,
                    R.anim.activity_pusher
                )
            }
        }) {
            Text(
                text = "How to import a widget",
                fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                fontSize = TextUnit(18f, TextUnitType.Sp),
                modifier = Modifier.weight(1f)
            )
        }

        TextButton(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(15.dp), onClick = {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setDataAndType(
                Uri.fromParts(
                    "mailto",
                    "rebootingbrains@gmail.com",
                    null
                ), "plain/text"
            )
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("rebootingbrains@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
            intent.putExtra(Intent.EXTRA_TEXT, "Body")
            context.startActivity(Intent.createChooser(intent, "Please select a mail app,"))
        }) {
            Text(
                text = "Need more help? Email us",
                fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                fontSize = TextUnit(18f, TextUnitType.Sp),
                modifier = Modifier.weight(1f)

                )
        }


    }
}
