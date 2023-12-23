package com.rb.anytextwiget.jetpackUI

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rb.anytextwiget.AppUtils
import com.rb.anytextwiget.AppWidgetData
import com.rb.anytextwiget.ColorData
import com.rb.anytextwiget.R
import com.rb.anytextwiget.WidgetData
import com.rb.anytextwiget.ui.theme.violet
import com.rb.anytextwiget.ui.theme.violetDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportWidgetSheet(
    context: AppCompatActivity,
    widgetData: WidgetData,
    widgetsList: MutableList<WidgetData>,
    onAddClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val fontUtils = FontUtils()
    val appUtils = AppUtils()

    var isAlreadyAvailable = false

    ModalBottomSheet(onDismissRequest = onDismiss) {
        //Header.
        Text(
            text = "Add this widget",
            fontFamily = fontUtils.openSans(FontWeight.Bold),
            fontSize = TextUnit(28f, TextUnitType.Sp),
            modifier = Modifier.padding(20.dp)
        )

        //Widget.
        ImportingWidgetItem(context = context, widgetData = widgetData, clickEvent = { }) {

        }

        //If already available text.
        for (data in widgetsList) {
            if (data.widgetID == widgetData.widgetID) {
                Row(modifier = Modifier.padding(10.dp)) {

                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "Widget already available icon",
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.CenterVertically)
                            .padding(5.dp)
                    )
                    Text(
                        text = "This widget is already available in your saved list",
                        fontSize = TextUnit(13f, TextUnitType.Sp),
                        fontFamily = fontUtils.openSans(
                            FontWeight.Normal
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(5.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
                isAlreadyAvailable = true
                break
            }
        }


        //Add button.
        Button(
            onClick = {
                if (!isAlreadyAvailable) {
                    onAddClick()
                } else {
                    Toast.makeText(
                        context, "Widget is already available in your saved list",
                        Toast.LENGTH_LONG
                    ).show()

                }
            },
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(0.dp, 15.dp),
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.AddCircle,
                contentDescription = "Add widget to list icon",
                modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 0.dp)
            )
            Text(
                text = "Add this widget",
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontFamily = fontUtils.openSans(
                    FontWeight.SemiBold
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImportingWidgetItem(
    context: AppCompatActivity,
    widgetData: WidgetData,
    clickEvent: () -> Unit,
    longClickEvent: () -> Unit
) {
    //Widget colour.
    var widgetColour = violet
    try {
        widgetColour =
            Color(android.graphics.Color.parseColor(widgetData.widgetBackgroundColor!!.colorHexCode))
    } catch (e: Exception) {
        e.printStackTrace()
    }

    //Widget Corners.
    val widgetCorners = if (widgetData.widgetRoundCorners) {
        Dp(30f)
    } else {
        Dp(0f)
    }

    //Widget outline width
    val widgetOutlineWidth = if (widgetData.outlineEnabled) {
        Dp(widgetData.widgetOutlineWidth.toFloat())
    } else {
        Dp(0f)
    }

    //Widget outline colour.
    var widgetOutlineColour = violetDark
    try {
        widgetOutlineColour =
            Color(android.graphics.Color.parseColor(widgetData.widgetOutlineColor!!.colorHexCode))
    } catch (e: Exception) {
        e.printStackTrace()
    }

    //Widget text colour
    var widgetTextColour = androidx.compose.ui.graphics.Color.White
    try {
        widgetTextColour =
            Color(android.graphics.Color.parseColor(widgetData.widgetTextColor!!.colorHexCode))
    } catch (e: Exception) {
        e.printStackTrace()
    }

    //Widget text font family.
    var font = ResourcesCompat.getFont(
        context,
        R.font.open_sans_semibold
    )

    try {
        val info = widgetData.widgetFontInfo!!
        font = if (info.sourceName != "NA") {
            ResourcesCompat.getFont(
                context,
                context.resources.getIdentifier(info.sourceName, "font", context.packageName)
            )!!
        } else {
            val fontText: String = if (info.fontStyle == "normal") {
                info.fontName.lowercase()
                    .replace(" ", "_", true)
            } else {
                info.fontName.lowercase()
                    .replace(" ", "_", true) + "_" + info.fontStyle.lowercase()
            }
            ResourcesCompat.getFont(
                context,
                context.resources.getIdentifier(fontText, "font", context.packageName)
            )!!
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    //Widget text horizontal gravity.
    val horizontalGravity = when (widgetData.widgetTextHorizontalGravity!!.gravityValue) {
        Gravity.CENTER_HORIZONTAL -> Alignment.CenterHorizontally
        Gravity.START -> Alignment.Start
        Gravity.END -> Alignment.End
        else -> {
            Alignment.CenterHorizontally
        }
    }

    //Widget text vertical gravity.
    val verticalGravity = when (widgetData.widgetTextVerticalGravity!!.gravityValue) {
        Gravity.CENTER_VERTICAL -> Alignment.CenterVertically
        Gravity.TOP -> Alignment.Top
        Gravity.BOTTOM -> Alignment.Bottom
        else -> {
            Alignment.CenterVertically
        }
    }

    //Widget text shadow radius.
    val widgetTextShadowRadius = if (widgetData.textShadowEnabled) {
        widgetData.textShadowData!!.shadowRadius
    } else {
        0
    }

    //Widget text shadow  colour.
    var widgetTextShadowColour = androidx.compose.ui.graphics.Color.Black
    try {
        widgetTextShadowColour =
            Color(android.graphics.Color.parseColor(widgetData.textShadowData!!.shadowColor!!.colorHexCode))
    } catch (e: Exception) {
        e.printStackTrace()
    }

    //Widget Card.
    Surface(
        color = widgetColour,
        shape = RoundedCornerShape(widgetCorners),
        border = BorderStroke(widgetOutlineWidth, widgetOutlineColour),
        modifier = Modifier
            .fillMaxWidth()
            .height(Dp(200f))
            .padding(Dp(10f))
            .combinedClickable(
                onLongClick = longClickEvent,
                onClick = clickEvent,
                onClickLabel = "Opens Editor",
                onDoubleClick = null,
                onLongClickLabel = "Opens option sheet"
            ),
    ) {
        //Widget background content.
        when (widgetData.widgetBackGroundType) {
            "image" -> {
                //Widget Image flipper.
                WidgetImageFlipper(
                    context = context,
                    imagesList = widgetData.widgetMultiImageList!!
                )
            }

            "gradient" -> {
                if (widgetData.widgetBackgroundGradient != null) {
                    //Gradient Image.
                    val sourceName =
                        "no_corners_" + widgetData.widgetBackgroundGradient!!.sourceName
                    val gradient =
                        context.resources.getIdentifier(
                            sourceName,
                            "drawable",
                            context.packageName
                        )
                    val bitmap = BitmapFactory.decodeResource(context.resources, gradient)
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Widget gradient background",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

            }
        }

        //Widget text.
        Text(
            text = widgetData.widgetText!!,
            color = widgetTextColour,
            fontSize = TextUnit(widgetData.widgetTextSize.toFloat(), TextUnitType.Sp),
            fontFamily = FontFamily(typeface = font!!),
            style = TextStyle(
                shadow = Shadow(
                    color = widgetTextShadowColour,
                    offset = Offset(
                        widgetData.textShadowData!!.horizontalDir.toFloat(),
                        widgetData.textShadowData!!.verticalDir.toFloat()
                    ),
                    blurRadius = widgetTextShadowRadius.toFloat()
                )
            ),
            modifier = Modifier
                .wrapContentHeight(verticalGravity)
                .wrapContentWidth(horizontalGravity)
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WidgetImageFlipper(context: AppCompatActivity, imagesList: MutableList<String>) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        imagesList.size
    }
    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false,
    ) {
        var bitmap: Bitmap? = null
        try {
            bitmap =
                AppUtils.getBitmapWithContentPath(context = context, path = imagesList[it], 2)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (bitmap != null) {
            Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Widget images")
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        repeat(times = Int.MAX_VALUE, action = {
            delay(5000)
            val toScrollPage = if (pagerState.canScrollForward) {
                pagerState.currentPage + 1
            } else {
                0
            }
            pagerState.animateScrollToPage(toScrollPage)
        })
    })
}


