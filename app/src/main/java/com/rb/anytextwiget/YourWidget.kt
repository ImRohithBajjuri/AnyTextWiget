package com.rb.anytextwiget

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.*
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.lang.Exception


class YourWidget : AppWidgetProvider() {
    lateinit var fontsList: MutableList<FontItemData>
    lateinit var dataList: MutableList<WidgetData>


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        //Get the saved UI widgets
        val sharedPreferences = context.getSharedPreferences("widgetspref", MODE_PRIVATE)
        val uiList = ArrayList<WidgetUIData>()
        val savedUIWidgetsJSON = sharedPreferences.getString("saveduiwidgets", null)
        if (savedUIWidgetsJSON != null) {
            val savedUIWidgets = getSavedUIWidgets(savedUIWidgetsJSON)
            uiList.addAll(savedUIWidgets)
        }


        //There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            for (widgetUIData in uiList) {
                if (widgetUIData.widgetUIID == appWidgetId) {
                    CoroutineScope(Dispatchers.Main).launch {
                        //If image bitmap is too big, change the sample size and update the widget
                        try {
                            val views = updateWidget(context, widgetUIData, 3)
                            delay(3000)
                            appWidgetManager.updateAppWidget(appWidgetId, views)

                        } catch (e: IllegalArgumentException) {
                            val views = updateWidget(context, widgetUIData, 5)
                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        } catch (e: OutOfMemoryError) {
                            val views = updateWidget(context, widgetUIData, 5)
                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        } catch (e: Resources.NotFoundException) {
                        }
                    }

                }
            }
        }


    }


    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)

        //Remove the saved UI data
        //Get the saved UI widgets
        val sharedPreferences = context!!.getSharedPreferences("widgetspref", MODE_PRIVATE)
        val uiList = ArrayList<WidgetUIData>()
        val savedUIWidgetsJSON = sharedPreferences.getString("saveduiwidgets", null)
        if (savedUIWidgetsJSON != null) {
            val savedUIWidgets = getSavedUIWidgets(savedUIWidgetsJSON)
            uiList.addAll(savedUIWidgets)
        }

        for (id in appWidgetIds!!.asList()) {
            removeSavedUIWidget(uiList, id)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == "widgetClick") {
            val actionName = intent.getStringExtra("actionName")
            val actionExtra = intent.getStringExtra("actionExtra")
            val widgetUIID = intent.getIntExtra("widgetUIID", 0)

            if (actionName != null) {
                handleSimpleActionClick(context!!, actionName, widgetUIID, actionExtra!!)
            } else {
                toggleNextImage(context!!, widgetUIID)
            }
        }

        super.onReceive(context, intent)
    }

    fun getSavedUIWidgets(json: String): MutableList<WidgetUIData> {
        val gson = Gson()
        val type = object : TypeToken<MutableList<WidgetUIData>>() {}.type
        return gson.fromJson(json, type)
    }

    fun removeSavedUIWidget(uiList: MutableList<WidgetUIData>, id: Int) {
        val iterator = uiList.iterator()
        while (iterator.hasNext()) {
            val data = iterator.next()
            if (data.widgetUIID == id) {
                iterator.remove()
            }
        }
    }

    fun textBitmap(context: Context, font: Int, size: Int, color: Int, text: String, data: WidgetData): Bitmap {
        val textView = TextView(context)
        textView.typeface = ResourcesCompat.getFont(context, R.font.open_sans_semibold)

        try {
            textView.typeface = ResourcesCompat.getFont(context, font)

        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
        textView.text = text
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
                    textView.setShadowLayer(AppUtils.dptopx(context, data.textShadowData!!.shadowRadius).toFloat(),
                        AppUtils.dptopx(context, data.textShadowData!!.horizontalDir).toFloat(),
                        AppUtils.dptopx(context, data.textShadowData!!.verticalDir).toFloat(), Color.parseColor(data.textShadowData!!.shadowColor!!.colorHexCode))
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


        textView.setPadding(AppUtils.dptopx(context, 15))



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


    suspend fun updateWidget(
        context: Context,
        widgetUIData: WidgetUIData,
        sampleSize: Int
    ): RemoteViews {

        val views = RemoteViews(context.packageName, R.layout.your_widget)



        var id = 0
        try {
            val info = widgetUIData.widgetData!!.widgetFontInfo!!
            if (info.sourceName != "NA") {
                id = context.resources.getIdentifier(info.sourceName, "font", context.packageName)
            }
            else {
                val fontText: String = if (info.fontStyle == "normal") {
                    info.fontName.lowercase()
                        .replace(" ", "_", true)
                } else {
                    info.fontName.lowercase()
                        .replace(" ", "_", true) + "_" + info.fontStyle.lowercase()
                }


                id = context.resources.getIdentifier(fontText, "font", context.packageName)
            }

        }
        catch (e: Resources.NotFoundException){
            e.printStackTrace()
        }
        try {
            val bitmap = textBitmap(
                context,
                id,
                widgetUIData.widgetData!!.widgetTextSize,
                Color.parseColor(widgetUIData.widgetData!!.widgetTextColor!!.colorHexCode),
                widgetUIData.widgetData!!.widgetText.toString(), widgetUIData.widgetData!!
            )
            views.setImageViewBitmap(R.id.widgetuiimage, bitmap)
            views.setViewPadding(
                R.id.widgetuiimage,
                AppUtils.dptopx(context, widgetUIData.widgetData!!.textPadding),
                AppUtils.dptopx(context, widgetUIData.widgetData!!.textPadding),
                AppUtils.dptopx(context, widgetUIData.widgetData!!.textPadding),
                AppUtils.dptopx(context, widgetUIData.widgetData!!.textPadding)
            )
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        //Set the round corners
        if (widgetUIData.widgetData!!.widgetRoundCorners != null) {
            if (widgetUIData.widgetData!!.widgetRoundCorners) {
                views.setImageViewResource(
                    R.id.widgetuibackground,
                    R.drawable.widget_round_background
                )


            } else {
                views.setImageViewResource(R.id.widgetuibackground, R.drawable.no_corners_shape)
            }
        }


        if (widgetUIData.widgetData!!.widgetBackGroundType == "color") {


            try {
                views.setInt(
                    R.id.widgetuibackground, "setColorFilter", Color.parseColor(
                        widgetUIData.widgetData!!.widgetBackgroundColor!!.colorHexCode
                    )
                )


                val color =
                    Color.parseColor(widgetUIData.widgetData!!.widgetBackgroundColor!!.colorHexCode)
                val alpha = Color.alpha(color)
                views.setInt(R.id.widgetuibackground, "setAlpha", alpha)
            } catch (e: IllegalArgumentException) {
                views.setInt(R.id.widgetuibackground, "setColorFilter", Color.parseColor("#FFFFFF"))
            }

        }

        views.setViewVisibility(R.id.widgetuibackground, View.VISIBLE)

        if (widgetUIData.widgetData!!.widgetBackGroundType == "image") {
            views.setViewVisibility(R.id.widgetuibackground, View.GONE)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val imageList: MutableList<String>
                imageList = ArrayList()
                imageList.addAll(widgetUIData.widgetData!!.widgetMultiImageList!!)

                val multiImageIntent = Intent(context, WidgetRemoteService::class.java)
                multiImageIntent.putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    widgetUIData.widgetUIID
                )
                multiImageIntent.data = Uri.parse(multiImageIntent.toUri(Intent.URI_INTENT_SCHEME))
                multiImageIntent.putStringArrayListExtra("imageList", imageList)
                multiImageIntent.putExtra("sampleSize", sampleSize)
                views.setRemoteAdapter(R.id.widgetUIBackgroundFlipper, multiImageIntent)
            }
        }


        if (widgetUIData.widgetData!!.widgetBackGroundType == "gradient") {
            if (widgetUIData.widgetData!!.widgetBackgroundGradient != null) {
                try {
                    val gradient = if (widgetUIData.widgetData!!.widgetRoundCorners) {
                        context.resources.getIdentifier(widgetUIData.widgetData!!.widgetBackgroundGradient!!.sourceName, "drawable", context.packageName)
                    } else {
                        val sourceName = "no_corners_" + widgetUIData.widgetData!!.widgetBackgroundGradient!!.sourceName

                        context.resources.getIdentifier(widgetUIData.widgetData!!.widgetBackgroundGradient!!.sourceName, "drawable", context.packageName)
                    }

                    views.setImageViewResource(R.id.widgetuibackground, gradient)

                }
                catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            views.setViewOutlinePreferredRadius(
                R.id.widgetuiparent,
                15f,
                TypedValue.COMPLEX_UNIT_DIP
            )
        }



        var pendingIntent: PendingIntent


        //Set the action with the new action data. And in case if there is no action data (old widgets),
        // then use the old way of passing pending intent

        //Initialize the flags
        var flags = PendingIntent.FLAG_UPDATE_CURRENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        }



        if (widgetUIData.widgetData!!.widgetClickAction != null) {
            if (widgetUIData.widgetData!!.widgetClickAction!!.actionType == AppUtils.ACTIONS_SIMPLE) {
                val intent3 = Intent(context, YourWidget::class.java)
                intent3.action = "widgetClick"
                intent3.putExtra("widgetUIID", widgetUIData.widgetUIID)
                intent3.putExtra("actionName", widgetUIData.widgetData!!.widgetClickAction!!.actionName)

                if (widgetUIData!!.widgetData!!.widgetClickAction!!.actionExtra != null) {
                    intent3.putExtra("actionExtra", widgetUIData.widgetData!!.widgetClickAction!!.actionExtra)
                }
                pendingIntent =
                    PendingIntent.getBroadcast(context, widgetUIData.widgetUIID, intent3, flags)
            } else {
                val intent =
                    context.packageManager.getLaunchIntentForPackage(widgetUIData.widgetData!!.widgetClickAction!!.appPackageName)

                if (intent != null) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    pendingIntent =
                        PendingIntent.getActivity(context, widgetUIData.widgetUIID, intent, flags)
                } else {
                    Toast.makeText(
                        context,
                        "${widgetUIData.widgetData!!.widgetClickAction!!.actionName} is not available for click",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent2 = Intent(context, MainActivity::class.java)
                    intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    pendingIntent =
                        PendingIntent.getActivity(context, widgetUIData.widgetUIID, intent2, flags)
                }
            }
        } else {
            if (widgetUIData.widgetData!!.widgetBackGroundType.equals("image")) {
                if (widgetUIData.widgetData!!.widgetMultiImageList == null) {
                    val intent2 = Intent(context, MainActivity::class.java)
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    pendingIntent = PendingIntent.getActivity(
                        context,
                        widgetUIData.widgetUIID,
                        intent2,
                        flags
                    )
                } else {
                    if (widgetUIData.widgetData!!.widgetMultiImageList!!.size > 1) {
                        val intent3 = Intent(context, YourWidget::class.java)
                        intent3.action = "widgetClick"
                        intent3.putExtra("widgetUIID", widgetUIData.widgetUIID)
                        intent3.putExtra("actionName", AppUtils.ACTION_NEXTIMAGE)
                        pendingIntent = PendingIntent.getBroadcast(
                            context,
                            widgetUIData.widgetUIID,
                            intent3,
                            flags
                        )
                    } else {
                        val intent2 = Intent(context, MainActivity::class.java)
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        pendingIntent = PendingIntent.getActivity(
                            context,
                            widgetUIData.widgetUIID,
                            intent2,
                            flags
                        )
                    }
                }
            } else {
                val intent2 = Intent(context, MainActivity::class.java)
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                pendingIntent = PendingIntent.getActivity(
                    context,
                    widgetUIData.widgetUIID,
                    intent2,
                    flags
                )
            }
        }

        if (widgetUIData.widgetData!!.widgetTextVerticalGravity != null) {
            views.setInt(
                R.id.widgetUIImageLayout,
                "setVerticalGravity",
                widgetUIData.widgetData!!.widgetTextVerticalGravity!!.gravityValue
            )
        } else {
            views.setInt(R.id.widgetUIImageLayout, "setVerticalGravity", Gravity.CENTER_VERTICAL)
        }

        if (widgetUIData.widgetData!!.widgetTextHorizontalGravity != null) {
            views.setInt(
                R.id.widgetUIImageLayout,
                "setHorizontalGravity",
                widgetUIData.widgetData!!.widgetTextHorizontalGravity!!.gravityValue
            )
        } else {
            views.setInt(
                R.id.widgetUIImageLayout,
                "setHorizontalGravity",
                Gravity.CENTER_HORIZONTAL
            )
        }


        //Set the outline, width and it's color
        if (widgetUIData.widgetData!!.outlineEnabled) {
            views.setViewVisibility(R.id.widgetUIOutline, View.VISIBLE)

            //Set the outline
            val width = "${widgetUIData.widgetData!!.widgetOutlineWidth}dp"
            val drawableName = if (widgetUIData.widgetData!!.widgetRoundCorners) {
                "outline_background_$width"
            } else {
                "no_corners_outline_background_$width"
            }


            val calculatePadding = 5 + widgetUIData.widgetData!!.widgetOutlineWidth
            val reqPadding = AppUtils.dptopx(context, calculatePadding)
            views.setViewPadding(R.id.widgetuibackground, reqPadding, reqPadding, reqPadding, reqPadding)
            views.setViewPadding(R.id.widgetUIBackgroundFlipper, reqPadding, reqPadding, reqPadding, reqPadding)


            try {
                val drawable = context.resources.getIdentifier(drawableName, "drawable", context.packageName)

                views.setImageViewResource(R.id.widgetUIOutline, drawable)

            }
            catch (e: Resources.NotFoundException) {
                e.printStackTrace()
            }

            if (widgetUIData.widgetData!!.widgetOutlineColor != null) {

                //Set the color
                try {
                    views.setInt(
                        R.id.widgetUIOutline, "setColorFilter", Color.parseColor(
                            widgetUIData.widgetData!!.widgetOutlineColor!!.colorHexCode
                        )
                    )

                    val color =
                        Color.parseColor(widgetUIData.widgetData!!.widgetOutlineColor!!.colorHexCode)
                    val alpha = Color.alpha(color)
                    views.setInt(R.id.widgetUIOutline, "setAlpha", alpha)
                } catch (e: IllegalArgumentException) {
                    views.setInt(
                        R.id.widgetUIOutline,
                        "setColorFilter",
                        Color.parseColor("#000000")
                    )
                }
            } else {
                views.setInt(R.id.widgetUIOutline, "setColorFilter", Color.parseColor("#000000"))

            }


        } else {
            views.setViewVisibility(R.id.widgetUIOutline, View.GONE)
            views.setViewPadding(R.id.widgetuibackground, 0, 0, 0, 0)
            views.setViewPadding(R.id.widgetUIBackgroundFlipper, 0, 0, 0, 0)

        }


        views.setOnClickPendingIntent(R.id.widgetuiparent, pendingIntent)

        

        return views
    }


    suspend fun updateUIWidget(context: Context, widgetData: WidgetData) {
        //Get the saved UI widgets
        val sharedPreferences = context.getSharedPreferences("widgetspref", MODE_PRIVATE)
        val uiList = ArrayList<WidgetUIData>()
        val savedUIWidgetsJSON = sharedPreferences.getString("saveduiwidgets", null)
        if (savedUIWidgetsJSON != null) {
            val savedUIWidgets = getSavedUIWidgets(savedUIWidgetsJSON)
            uiList.addAll(savedUIWidgets)
        }

        //Loop through the uiList and save and update all the ui widgets containing the updated widget
        for (uiData in uiList) {
            if (uiData.widgetData != null) {
                if (uiData.widgetData!!.widgetID == widgetData.widgetID) {
                    uiData.widgetData = widgetData

                    saveEditedUIWidget(context, uiList)

                    AppUtils.updateSingleUIWidget(context = context, uiData.widgetUIID)
                }
            }
        }
    }

    fun getSavedWidgets(json: String): MutableList<WidgetData> {
        val gson = Gson()
        val type = object : TypeToken<MutableList<WidgetData>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveEditedUIWidget(context: Context, editedUIList: List<WidgetUIData>) {
        val sharedPreferences = context.getSharedPreferences("widgetspref", MODE_PRIVATE)

        val uiList = ArrayList<WidgetUIData>()

        //Add all UI widgets and save to shared preferences
        uiList.addAll(editedUIList)
        val gson = Gson()
        val savingJSON = gson.toJson(uiList)
        sharedPreferences.edit().putString("saveduiwidgets", savingJSON).apply()
    }

    suspend fun saveWidgets(context: Context) {
        val sharedPreferences = context.getSharedPreferences("widgetspref", MODE_PRIVATE)

        val gson = Gson()

        val json = gson.toJson(dataList)

        sharedPreferences.edit().putString("savedwidgets", json).apply()

    }

    fun toggleWifi(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            panelIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(panelIntent)
        } else {
            val wifiManager =
                context.applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
            if (wifiManager.isWifiEnabled) {
                wifiManager.isWifiEnabled = false
            } else {
                wifiManager.isWifiEnabled = true
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun toggleBlueTooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled) {
                bluetoothAdapter.disable()
            } else {
                bluetoothAdapter.enable()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun toggleDnd(context: Context) {
        val notificationManager =
            context.applicationContext.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

        // Check if the notification policy access has been granted for the app.
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            Toast.makeText(context, "Do Not Disturb access has not been granted", Toast.LENGTH_LONG)
                .show()
        } else {
            if (notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_NONE) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
            } else {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun toggleFlashlight(context: Context) {
        val cameraManager =
            context.applicationContext.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager

        cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {

            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                cameraManager.unregisterTorchCallback(this)
                if (!enabled) {
                    cameraManager.setTorchMode(cameraManager.cameraIdList[0], true)
                } else {
                    cameraManager.setTorchMode(cameraManager.cameraIdList[0], false)
                }

                super.onTorchModeChanged(cameraId, enabled)
            }
        }, Handler())
    }

    fun toggleNextImage(context: Context, widgetUIID: Int) {
        val views = RemoteViews(context.packageName, R.layout.your_widget)
        views.showNext(R.id.widgetUIBackgroundFlipper)
        AppWidgetManager.getInstance(context).updateAppWidget(widgetUIID, views)
    }

    fun openLink(context: Context, actionExtra: String) {
        if (actionExtra.trim().isNotEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.data = Uri.parse(actionExtra)
                context.startActivity(intent)
            }
            catch (e: Exception) {
                Toast.makeText(context, "Unable to open this link. Please enter a proper one", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    fun handleSimpleActionClick(context: Context, actionName: String, widgetUIID: Int, actionExtra: String) {
        when (actionName) {
            AppUtils.ACTION_WIFI -> {
                toggleWifi(context)
            }

            AppUtils.ACTION_BLUETOOTH -> {
                toggleBlueTooth()
            }

            AppUtils.ACTION_DONOTDISTURB -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    toggleDnd(context)
                }
            }

            AppUtils.ACTION_FLASHLIGHT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    toggleFlashlight(context)
                }
            }

            AppUtils.ACTION_NEXTIMAGE -> {
                toggleNextImage(context, widgetUIID)
            }

            AppUtils.ACTION_OPEN_LINK -> {
                openLink(context, actionExtra)
            }

        }
    }

}

