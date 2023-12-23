package com.rb.anytextwiget.jetpackUI

import android.view.Gravity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.rb.anytextwiget.R
import com.rb.anytextwiget.TextGravityData
import com.rb.anytextwiget.WidgetFontInfo

val HORIZONTAL = "horizontal"
val VERTICAL = "vertical"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GravitySheet(
    gravitiesType: String,
    gravitiesList: MutableList<TextGravityData>,
    currentGravity: TextGravityData,
    gravitySelectedEvent: (gravityData: TextGravityData) -> Unit,
    onDismiss: () -> Unit
) {

    val context = LocalContext.current

    val fontUtils = FontUtils()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            text = "Gravities",
            fontFamily = fontUtils.openSans(FontWeight.Bold),
            fontSize = TextUnit(28f, TextUnitType.Sp),
            modifier = Modifier.padding(20.dp)
        )

        LazyColumn() {
            items(gravitiesList) {
                GravityItem(
                    gravitiesType = gravitiesType,
                    textGravityData = it,
                    currentGravity = currentGravity,
                    gravitySelectedEvent = gravitySelectedEvent,
                    fontUtils = fontUtils
                )
            }

        }

    }
}

@Composable
fun GravityItem(
    gravitiesType: String,
    textGravityData: TextGravityData,
    currentGravity: TextGravityData,
    gravitySelectedEvent: (gravityData: TextGravityData) -> Unit,
    fontUtils: FontUtils
) {
    val context = LocalContext.current

    var gravityIcon = R.drawable.ic_round_vertical_align_bottom_50
    var rotation = 0f
    TextButton(
        onClick = {
                  gravitySelectedEvent(textGravityData)
        },
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        if (gravitiesType == HORIZONTAL) {
            when (textGravityData.gravityName) {
                "Start" -> {
                    gravityIcon =  R.drawable.ic_round_vertical_align_bottom_50
                    rotation = 90f
                }

                "Center" -> {
                    gravityIcon =  R.drawable.ic_round_vertical_align_center_50
                    rotation = 0f
                }

                "End" -> {
                    gravityIcon =  R.drawable.ic_round_vertical_align_bottom_50
                    rotation = -90f
                }
            }
        } else {
            when (textGravityData.gravityName) {
                "Top" -> {
                    gravityIcon =  R.drawable.ic_round_vertical_align_bottom_50
                    rotation = 180f
                }

                "Center" -> {
                    gravityIcon =  R.drawable.ic_round_vertical_align_center_50
                    rotation = 0f
                }

                "Bottom" -> {
                    gravityIcon =  R.drawable.ic_round_vertical_align_bottom_50
                    rotation = 0f
                }
            }
        }

        Icon(
            painter = painterResource(id = gravityIcon),
            contentDescription = "Gravity icon",
            modifier = Modifier.rotate(rotation).padding(10.dp, 10.dp)
        )

        Text(
            text = textGravityData.gravityName,
            fontSize = TextUnit(18f, TextUnitType.Sp),
            fontFamily = fontUtils.openSans(FontWeight.Normal),
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )

        if (textGravityData.gravityName == currentGravity.gravityName) {
            Icon(
                painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                contentDescription = "Selected font icon",
                modifier = Modifier.padding(10.dp, 0.dp)
            )
        }

    }
}

