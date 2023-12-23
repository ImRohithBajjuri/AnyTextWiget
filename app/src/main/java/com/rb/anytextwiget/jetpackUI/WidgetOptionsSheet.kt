package com.rb.anytextwiget.jetpackUI

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rb.anytextwiget.AppUtils
import com.rb.anytextwiget.HelpInfo
import com.rb.anytextwiget.MainActivity
import com.rb.anytextwiget.PlaceWidgetRequestReceiver
import com.rb.anytextwiget.R
import com.rb.anytextwiget.WidgetData
import com.rb.anytextwiget.YourWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

val SHARE_CLICK_EVENT = "share"
val SAVE_FILE_CLICK_EVENT = "saveWidget"
val SAVE_IMAGE_CLICK_EVENT = "saveAsImage"
val CLONE_CLICK_EVENT = "cloneWidget"
val DELETE_CLICK_EVENT = "deleteWidget"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetOptionsSheet(
    context: AppCompatActivity,
    widgetData: WidgetData,
    onDismiss: () -> Unit,
    optionSheetClickEvent: (clickEvent: String) -> Unit
) {
    val fontUtils = FontUtils()
    val appUtils = AppUtils()

    var showPermissionDialog by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            text = "Widget Options",
            fontFamily = fontUtils.openSans(FontWeight.Bold),
            fontSize = TextUnit(28f, TextUnitType.Sp),
            modifier = Modifier.padding(20.dp)
        )

        //Adjust visibility of place widget option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                    val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                    val intent = Intent(context, PlaceWidgetRequestReceiver::class.java)

                    val bundle = Bundle()
                    bundle.putString("widgetid", widgetData.widgetID)
                    intent.putExtra("prewbundle", bundle)

                    var flags = PendingIntent.FLAG_UPDATE_CURRENT

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        flags = PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    }


                    val pendingIntent = PendingIntent.getBroadcast(context, 506, intent, flags)
                    val componentName = ComponentName(context, YourWidget::class.java)

                    val views = buildWidgetPreview(context, widgetData)

                    val uiBundle = Bundle()
                    uiBundle.putParcelable(AppWidgetManager.EXTRA_APPWIDGET_PREVIEW, views)


                    appWidgetManager.requestPinAppWidget(componentName, uiBundle, pendingIntent)
                }, contentPadding = PaddingValues(15.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_add_to_home_screen_24),
                        contentDescription = "Place widget on home screen icon",
                        modifier = Modifier.padding(10.dp)
                    )
                    Text(
                        text = "Place widget on home screen",
                        fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                        fontSize = TextUnit(18f, TextUnitType.Sp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }


        TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
            if (AppUtils.hasStoragePermission(context)) {
                optionSheetClickEvent(SHARE_CLICK_EVENT)
            }
            else {
                showPermissionDialog = true
            }

        }, contentPadding = PaddingValues(15.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_share_30),
                contentDescription = "Share widget option icon",
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = "Share widget",
                fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                fontSize = TextUnit(18f, TextUnitType.Sp),
                modifier = Modifier.weight(1f)
            )
        }


        TextButton(modifier = Modifier.fillMaxWidth(), onClick = {

            //Check storage permission.
            if (AppUtils.hasStoragePermission(context)) {
                optionSheetClickEvent(SAVE_FILE_CLICK_EVENT)
            } else {
                showPermissionDialog = true
            }
        }, contentPadding = PaddingValues(15.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_round_save_alt_for_widget_options),
                contentDescription = "Save widget option icon",
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = "Save widget as file",
                fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                fontSize = TextUnit(18f, TextUnitType.Sp),
                modifier = Modifier.weight(1f)
            )
        }

        //Show widget save as image option only if the widget has a color background
        if (widgetData.widgetBackGroundType != "image") {
            TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                if (AppUtils.hasStoragePermission(context = context)) {
                    optionSheetClickEvent(SAVE_IMAGE_CLICK_EVENT)
                } else {
                    showPermissionDialog = true
                }
            }, contentPadding = PaddingValues(15.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outline_image_30),
                    contentDescription = "Save widget as image icon",
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = "Save widget as image",
                    fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    modifier = Modifier.weight(1f)
                )
            }
        }


        TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
            optionSheetClickEvent(CLONE_CLICK_EVENT)
        }, contentPadding = PaddingValues(15.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_round_content_copy),
                contentDescription = "Clone widget option icon",
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = "Clone widget",
                fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                fontSize = TextUnit(18f, TextUnitType.Sp),
                modifier = Modifier.weight(1f)
            )
        }

        TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
            optionSheetClickEvent(DELETE_CLICK_EVENT)
            /*deleteWidget(context, widgetData.widgetID!!, uiList = uiList)
            scope.launch {
                snackbarHostState.showSnackbar(message = "Widget deleted!")
            }*/
        }, contentPadding = PaddingValues(15.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_round_delete_24),
                contentDescription = "Delete widget option icon",
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = "Delete widget",
                fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                fontSize = TextUnit(18f, TextUnitType.Sp),
                modifier = Modifier.weight(1f)
            )
        }

        //Permission Dialog.
        if (showPermissionDialog) {
            appUtils.BuildAlertDialog(
                title = "Permission required!",
                description = "Storage permission is required for saving widgets to your device",
                confirmEvent = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(
                            context, arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.ACCESS_MEDIA_LOCATION
                            ), 103
                        )
                    } else {
                        ActivityCompat.requestPermissions(
                            context, arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ), 103
                        )
                    }

                    showPermissionDialog = false
                },
                dismissEvent = { showPermissionDialog = false }) {

            }
        }

        //Show ads.
        if (!disableAds.value) {
            AndroidView(factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = context.getString(R.string.bannerAdFiveId)
                    val adRequest = AdRequest.Builder().build()
                    loadAd(adRequest)
                }
            })
        }
    }
}

private fun buildWidgetPreview(context: AppCompatActivity, data: WidgetData): RemoteViews {
    val views = RemoteViews(context.packageName, R.layout.your_widget)

    var id = 0
    try {
        val info = data.widgetFontInfo!!
        id = if (info.sourceName != "NA") {
            context.resources.getIdentifier(info.sourceName, "font", context.packageName)
        } else {
            val fontText: String = if (info.fontStyle == "normal") {
                info.fontName.lowercase()
                    .replace(" ", "_", true)
            } else {
                info.fontName.lowercase()
                    .replace(" ", "_", true) + "_" + info.fontStyle.lowercase()
            }


            context.resources.getIdentifier(fontText, "font", context.packageName)
        }

    } catch (e: Resources.NotFoundException) {
        e.printStackTrace()
    }

    try {
        val bitmap = textBitmap(
            context,
            id, data.widgetTextSize, Color.parseColor(
                data.widgetTextColor!!.colorHexCode
            ), data.widgetText.toString(), data
        )
        views.setImageViewBitmap(R.id.widgetuiimage, bitmap)
        views.setViewPadding(
            R.id.widgetuiimage,
            AppUtils.dptopx(context, data.textPadding),
            AppUtils.dptopx(context, data.textPadding),
            AppUtils.dptopx(context, data.textPadding),
            AppUtils.dptopx(context, data.textPadding)
        )
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    }


    //Set the round corners
    if (data.widgetRoundCorners != null) {
        if (data.widgetRoundCorners) {
            views.setImageViewResource(
                R.id.widgetuibackground,
                R.drawable.widget_round_background
            )
        } else {
            views.setImageViewResource(R.id.widgetuibackground, R.drawable.no_corners_shape)
        }
    }


    views.setViewVisibility(R.id.widgetuibackground, View.VISIBLE)


    if (data.widgetBackGroundType == "color") {

        try {
            views.setInt(
                R.id.widgetuibackground,
                "setColorFilter",
                Color.parseColor(data.widgetBackgroundColor!!.colorHexCode)
            )
            val color = Color.parseColor(data.widgetBackgroundColor!!.colorHexCode)
            val alpha = Color.alpha(color)
            views.setInt(R.id.widgetuibackground, "setAlpha", alpha)
        } catch (e: IllegalArgumentException) {
            views.setInt(
                R.id.widgetuibackground,
                "setColorFilter",
                Color.parseColor("#ffffff")
            )

            e.printStackTrace()
        }
    }
    if (data.widgetBackGroundType == "image") {
        val imageList: MutableList<String>
        imageList = ArrayList()
        imageList.addAll(data.widgetMultiImageList!!)
        val bitmap = AppUtils.getBitmapWithContentPath(context, imageList.get(0), 5)
        views.setImageViewBitmap(R.id.widgetuibackground, bitmap)
    }
    if (data.widgetBackGroundType == "gradient") {
        if (data.widgetBackgroundGradient != null) {
            try {
                val gradient = if (data.widgetRoundCorners) {
                    context.resources.getIdentifier(
                        data.widgetBackgroundGradient!!.sourceName,
                        "drawable",
                        context.packageName
                    )
                } else {
                    val sourceName = "no_corners_" + data.widgetBackgroundGradient!!.sourceName

                    context.resources.getIdentifier(sourceName, "drawable", context.packageName)
                }

                views.setImageViewResource(R.id.widgetuibackground, gradient)
            } catch (e: Resources.NotFoundException) {
                e.printStackTrace()
            }
        }
    }

    //Set Gravities
    if (data.widgetTextVerticalGravity != null) {
        views.setInt(
            R.id.widgetUIImageLayout,
            "setVerticalGravity",
            data.widgetTextVerticalGravity!!.gravityValue
        )
    } else {
        views.setInt(R.id.widgetUIImageLayout, "setVerticalGravity", Gravity.CENTER_VERTICAL)
    }

    if (data.widgetTextHorizontalGravity != null) {
        views.setInt(
            R.id.widgetUIImageLayout,
            "setHorizontalGravity",
            data.widgetTextHorizontalGravity!!.gravityValue
        )
    } else {
        views.setInt(
            R.id.widgetUIImageLayout,
            "setHorizontalGravity",
            Gravity.CENTER_HORIZONTAL
        )
    }

    //Set the outline, width and it's color
    if (data.outlineEnabled) {
        //Set the outline
        val width = "${data.widgetOutlineWidth}dp"
        val drawableName = if (data.widgetRoundCorners) {
            "outline_background_$width"
        } else {
            "no_corners_outline_background_$width"
        }

        val calculatePadding = 5 + data.widgetOutlineWidth
        val reqPadding = AppUtils.dptopx(context, calculatePadding)
        views.setViewPadding(
            R.id.widgetuibackground,
            reqPadding,
            reqPadding,
            reqPadding,
            reqPadding
        )
        views.setViewPadding(
            R.id.widgetUIBackgroundFlipper,
            reqPadding,
            reqPadding,
            reqPadding,
            reqPadding
        )

        views.setViewVisibility(R.id.widgetUIOutline, View.VISIBLE)

        try {
            val drawable =
                context.resources.getIdentifier(drawableName, "drawable", context.packageName)

            views.setImageViewResource(R.id.widgetUIOutline, drawable)

        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }

        if (data.widgetOutlineColor != null) {
            //Set the color
            try {
                views.setInt(
                    R.id.widgetUIOutline, "setColorFilter", Color.parseColor(
                        data.widgetOutlineColor!!.colorHexCode
                    )
                )


                val color = Color.parseColor(data.widgetOutlineColor!!.colorHexCode)
                val alpha = Color.alpha(color)
                views.setInt(R.id.widgetUIOutline, "setAlpha", alpha)
            } catch (e: IllegalArgumentException) {
                views.setInt(
                    R.id.widgetUIOutline,
                    "setColorFilter",
                    Color.parseColor("#FFFFFF")
                )
            }
        }


    } else {
        views.setViewVisibility(R.id.widgetUIOutline, View.GONE)

        views.setViewPadding(R.id.widgetuibackground, 0, 0, 0, 0)
        views.setViewPadding(R.id.widgetUIBackgroundFlipper, 0, 0, 0, 0)

    }
    return views

}

private fun textBitmap(
    context: AppCompatActivity,
    font: Int,
    size: Int,
    color: Int,
    text: String,
    data: WidgetData
): Bitmap {
    val textView = TextView(context)
    textView.setTypeface(ResourcesCompat.getFont(context, R.font.open_sans_semibold))
    try {
        textView.setTypeface(ResourcesCompat.getFont(context, font))

    } catch (e: Resources.NotFoundException) {
        e.printStackTrace()
    }
    textView.setText(text)
    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
    textView.setTextColor(
        ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                R.color.Black
            )
        )
    )

    try {
        textView.setTextColor(ColorStateList.valueOf(color))

        if (data.textShadowEnabled) {
            if (data.textShadowData != null) {
                textView.setShadowLayer(
                    AppUtils.dptopx(context, data.textShadowData!!.shadowRadius).toFloat(),
                    AppUtils.dptopx(context, data.textShadowData!!.horizontalDir).toFloat(),
                    AppUtils.dptopx(context, data.textShadowData!!.verticalDir).toFloat(),
                    Color.parseColor(data.textShadowData!!.shadowColor!!.colorHexCode)
                )
            }
        }
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    }
    textView.gravity = Gravity.CENTER
    textView.includeFontPadding = false



    textView.layoutParams = FrameLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )



    textView.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    textView.layout(0, 0, textView.measuredWidth, textView.measuredHeight)
    val bitmap = Bitmap.createBitmap(textView.width, textView.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    textView.draw(canvas)
    return bitmap
}

fun cloneWidget(
    context: AppCompatActivity,
    widgetData: WidgetData,
    uiList: SnapshotStateList<WidgetData>
) {
    CoroutineScope(Dispatchers.IO).launch {

        val clonedWidgetData = WidgetData()
        clonedWidgetData.widgetText = widgetData.widgetText
        clonedWidgetData.widgetTextColor = widgetData.widgetTextColor
        clonedWidgetData.widgetBackGroundType = widgetData.widgetBackGroundType
        clonedWidgetData.widgetBackgroundColor = widgetData.widgetBackgroundColor
        clonedWidgetData.widgetBackgroundImageUri = widgetData.widgetBackgroundImageUri
        clonedWidgetData.widgetTextSize = widgetData.widgetTextSize
        clonedWidgetData.widgetTextFontID = widgetData.widgetTextFontID
        clonedWidgetData.widgetRoundCorners = widgetData.widgetRoundCorners
        clonedWidgetData.widgetFontInfo = widgetData.widgetFontInfo
        clonedWidgetData.widgetMultiImageList = widgetData.widgetMultiImageList
        clonedWidgetData.widgetTextVerticalGravity = widgetData.widgetTextVerticalGravity
        clonedWidgetData.widgetTextHorizontalGravity = widgetData.widgetTextHorizontalGravity
        clonedWidgetData.widgetClickAction = widgetData.widgetClickAction
        clonedWidgetData.widgetID = UUID.randomUUID().toString()
        clonedWidgetData.outlineEnabled = widgetData.outlineEnabled
        clonedWidgetData.widgetOutlineColor = widgetData.widgetOutlineColor
        clonedWidgetData.widgetOutlineWidth = widgetData.widgetOutlineWidth
        clonedWidgetData.widgetBackgroundGradient = widgetData.widgetBackgroundGradient
        clonedWidgetData.textShadowEnabled = widgetData.textShadowEnabled
        clonedWidgetData.textShadowData = widgetData.textShadowData


        //Save to preferences.
        saveNewWidget(context, clonedWidgetData)

        //Add to UI list.
        uiList.add(clonedWidgetData)
    }
}

fun saveNewWidget(context: AppCompatActivity, widgetData: WidgetData) {
    val widgetsList = ArrayList<WidgetData>()

    val sharedPreferences = context.getSharedPreferences("widgetspref", Context.MODE_PRIVATE)

    //Get the saved widgets
    val savedWidgetsJSON = sharedPreferences.getString("savedwidgets", null)

    if (savedWidgetsJSON != null) {
        val savedWidgetsList = getSavedWidgetsFromJSON(savedWidgetsJSON)
        widgetsList.addAll(savedWidgetsList)
    }

    //Add the new widget data and save it to the pref
    widgetsList.add(widgetData)

    val json = getJSONFromWidgetDataList(widgetsList)

    sharedPreferences.edit().putString("savedwidgets", json).apply()


}

fun getSavedWidgetsFromJSON(json: String): MutableList<WidgetData> {
    val gson = Gson()
    val type = object : TypeToken<MutableList<WidgetData>>() {}.type
    return gson.fromJson(json, type)
}

fun getJSONFromWidgetDataList(widgetsList: MutableList<WidgetData>): String {
    val gson = Gson()
    return gson.toJson(widgetsList)
}

fun deleteWidget(
    context: AppCompatActivity,
    widgetID: String,
    uiList: SnapshotStateList<WidgetData>
) {
    val widgetsList = ArrayList<WidgetData>()

    val sharedPreferences = context.getSharedPreferences("widgetspref", Context.MODE_PRIVATE)

    //Get the saved widgets
    val savedWidgetsJSON = sharedPreferences.getString("savedwidgets", null)

    if (savedWidgetsJSON != null) {
        val savedWidgetsList = getSavedWidgetsFromJSON(savedWidgetsJSON)
        widgetsList.addAll(savedWidgetsList)
    }

    //Delete the widget data and save it to the pref
    val iterator = widgetsList.iterator()
    while (iterator.hasNext()) {
        val data = iterator.next()
        if (data.widgetID == widgetID) {
            iterator.remove()
        }
    }


    val json = getJSONFromWidgetDataList(widgetsList)

    sharedPreferences.edit().putString("savedwidgets", json).apply()

    //Delete from UI.
    //Delete the widget from UI.
    val uiIterator = uiList.iterator()
    while (uiIterator.hasNext()) {
        val data = uiIterator.next()
        if (data.widgetID == widgetID) {
            uiIterator.remove()
        }
    }
}



