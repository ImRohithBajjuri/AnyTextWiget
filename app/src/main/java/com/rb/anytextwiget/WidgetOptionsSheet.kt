package com.rb.anytextwiget

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rb.anytextwiget.databinding.FragmentWidgetOptionsSheetBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class WidgetOptionsSheet() : BottomSheetDialogFragment() {

    lateinit var contexT: Context
    lateinit var widgetData: WidgetData

    interface WidgetOptionsInterface {
        fun widgetDeleted()

        fun widgetSavedToDevice(savedUri: Uri)

        fun widgetSavedAsImage(savedUri: Uri)

        fun widgetSaveCancelled()

        fun widgetCloned()
    }

    lateinit var optionsInterface: WidgetOptionsInterface

    lateinit var binding: FragmentWidgetOptionsSheetBinding

    var isDark: Boolean = false


    constructor(widgetData: WidgetData, optionsInterface: WidgetOptionsInterface) : this() {
        this.widgetData = widgetData
        this.optionsInterface = optionsInterface
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        contexT = requireActivity()
        val sharedPreferences = contexT.getSharedPreferences("apppref", MODE_PRIVATE)
        val roundCorners = sharedPreferences.getBoolean("roundcorners", true)
        val appTheme = sharedPreferences.getString("apptheme", AppUtils.LIGHT)

        if (appTheme == AppUtils.LIGHT) {
            adjustSheetStyle(false, roundCorners)
        }
        if (appTheme == AppUtils.DARK) {
            adjustSheetStyle(true, roundCorners)
        }
        if (appTheme == AppUtils.FOLLOW_SYSTEM) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> adjustSheetStyle(true, roundCorners)

                Configuration.UI_MODE_NIGHT_NO -> adjustSheetStyle(false, roundCorners)
            }
        }

        return super.onCreateDialog(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        contexT = requireActivity()
        binding = FragmentWidgetOptionsSheetBinding.inflate(inflater, container, false)

        val sharedPreferences = contexT.getSharedPreferences("apppref", MODE_PRIVATE)
        //Adjust app UI with theme
        adjustTheme(sharedPreferences.getString("apptheme", AppUtils.LIGHT)!!)

        setAds()

        //Adjust visibility of place widget option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = contexT.getSystemService(AppWidgetManager::class.java)
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                binding.placeWidgetOption.visibility = View.VISIBLE
            }
        }

        //Show widget save as image option only if the widget has a color background
        if (widgetData.widgetBackGroundType == "color") {
            binding.widgetSaveAsImage.visibility = View.VISIBLE
        }

        binding.widgetdeleteoption.setOnClickListener {
            deleteWidget(widgetData.widgetID!!)

            optionsInterface.widgetDeleted()
            dismiss()

        }


        binding.widgetsavetodeviceoption.setOnClickListener {
            if (AppUtils.hasStoragePermission(requireActivity())) {
                try {
                    val snackbar = AppUtils.showSnackbar(
                        contexT,
                        "Saving your widget, please wait...",
                        binding.widgetOptionsSheetParent,
                        isDark
                    )
                    snackbar.duration = Snackbar.LENGTH_INDEFINITE
                    snackbar.show()
                    CoroutineScope(Dispatchers.IO).launch {
                        val saveInterface: AppUtils.WidgetSaveInterface
                        withContext(Dispatchers.Main) {
                            saveInterface = object : AppUtils.WidgetSaveInterface {
                                override fun widgetSaved(savedPath: String) {
                                    snackbar.dismiss()
                                    optionsInterface.widgetSavedToDevice(Uri.parse(savedPath))
                                    dismiss()
                                }

                                override fun widgetSaveFailed() {
                                    snackbar.dismiss()
                                    optionsInterface.widgetSaveCancelled()
                                    dismiss()
                                }

                            }
                        }
                        AppUtils.saveWidgetToDevice(contexT, widgetData, saveInterface)
                    }
                } catch (e: IOException) {
                    optionsInterface.widgetSaveCancelled()
                    dismiss()
                    e.printStackTrace()
                }
            }
            else {
               val builder = AppUtils.buildStoragePermission(requireActivity())
                builder.setTitle("Storage permission required")
                builder.setMessage("storage permission is required for saving widgets to your device")
                builder.show()
            }
        }

        binding.widgetSaveAsImage.setOnClickListener {
            if (AppUtils.hasStoragePermission(requireActivity())){
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        val bitmap = AppUtils.makeWidgetImage(contexT, widgetData)
                        val name = AppUtils.uniqueContentNameGenerator("Widget")
                        val savedPath = AppUtils.saveImageBitmap(contexT, name, bitmap)
                        optionsInterface.widgetSavedAsImage(Uri.parse(savedPath))

                        withContext(Dispatchers.Main) {
                            dismiss()
                        }
                    }
                } catch (e: IOException) {
                    dismiss()
                    e.printStackTrace()
                }
            }
            else {
                val builder = AppUtils.buildStoragePermission(requireActivity())
                builder.setTitle("Storage permission required")
                builder.setMessage("storage permission is required for saving widgets to your device")
                builder.show()
            }
        }

        binding.sharewidgetoption.setOnClickListener {
            if (AppUtils.hasStoragePermission(requireActivity())) {
                try {
                    val snackbar = AppUtils.showSnackbar(
                        contexT,
                        "Preparing to share your widget, please wait...",
                        binding.widgetOptionsSheetParent,
                        isDark
                    )
                    snackbar.duration = Snackbar.LENGTH_INDEFINITE
                    snackbar.show()

                    CoroutineScope(Dispatchers.IO).launch {
                        val saveInterface: AppUtils.WidgetSaveInterface
                        withContext(Dispatchers.Main) {
                            saveInterface = object : AppUtils.WidgetSaveInterface {
                                override fun widgetSaved(savedPath: String) {
                                    snackbar.dismiss()
                                    val intent = Intent(Intent.ACTION_SEND)
                                    intent.type = "*/*"
                                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(savedPath))
                                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    startActivity(Intent.createChooser(intent, "Share via"))
                                    dismiss()
                                }

                                override fun widgetSaveFailed() {
                                    snackbar.dismiss()
                                    optionsInterface.widgetSaveCancelled()
                                    dismiss()
                                }

                            }
                        }
                        AppUtils.saveWidgetToDevice(contexT, widgetData, saveInterface)
                    }
                } catch (e: IOException) {
                    optionsInterface.widgetSaveCancelled()
                    dismiss()
                    e.printStackTrace()
                }
            }
            else {
                val builder = AppUtils.buildStoragePermission(requireActivity())
                builder.setTitle("Storage permission required")
                builder.setMessage("storage permission is required for saving widgets to your device")
                builder.show()
            }
        }

        binding.placeWidgetOption.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                placeWidgetOnHomeScreen(widgetData)
            }
        }

        binding.cloneWidgetOption.setOnClickListener {
            cloneWidget()
        }


        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 90) {
            if (resultCode == RESULT_OK) {
                try {
                    val snackbar = AppUtils.showSnackbar(
                        contexT,
                        "Saving your widget, please wait...",
                        binding.widgetOptionsSheetParent,
                        isDark
                    )
                    snackbar.duration = Snackbar.LENGTH_INDEFINITE
                    snackbar.show()

                    CoroutineScope(Dispatchers.IO).launch {
                        val saveInterface: AppUtils.WidgetSaveInterface
                        withContext(Dispatchers.Main) {
                            saveInterface = object : AppUtils.WidgetSaveInterface {
                                override fun widgetSaved(savedPath: String) {
                                    snackbar.dismiss()
                                    optionsInterface.widgetSavedToDevice(Uri.parse(savedPath))
                                    dismiss()
                                }

                                override fun widgetSaveFailed() {
                                    snackbar.dismiss()
                                    optionsInterface.widgetSaveCancelled()
                                    dismiss()
                                }

                            }
                        }
                        AppUtils.saveWidgetToDevice(contexT, widgetData, saveInterface)
                    }
                } catch (e: IOException) {
                    optionsInterface.widgetSaveCancelled()
                    dismiss()
                    e.printStackTrace()
                }
            }
        }
    }

    fun deleteWidget(widgetID: String) {
        val widgetsList = ArrayList<WidgetData>()

        val sharedPreferences = contexT.getSharedPreferences("widgetspref", MODE_PRIVATE)

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

    fun askStoragePermission(): AlertDialog.Builder {
        val builder = AlertDialog.Builder(contexT)
        builder.setTitle("Storage permission required")
        builder.setMessage("storage permission is required for saving widgets to your device")
        return builder
    }

    fun adjustSheetStyle(isNight: Boolean, roundCorners: Boolean) {
        if (isNight) {
            if (roundCorners) {
                setStyle(STYLE_NORMAL, R.style.bottomSheetDialogStyleDark)
            } else {
                setStyle(STYLE_NORMAL, R.style.noCornersBottomSheetDialogStyleDark)
            }
        } else {
            if (roundCorners) {
                setStyle(STYLE_NORMAL, R.style.bottomSheetDialogStyle)
            } else {
                setStyle(STYLE_NORMAL, R.style.noCornersBottomSheetDialogStyle)
            }
        }
    }

    fun darkMode(isNight: Boolean) {
        isDark = isNight
        if (isNight) {
            binding.widgetOptionsSheetHeader.setTextColor(
                ContextCompat.getColor(
                    contexT,
                    R.color.white
                )
            )
            binding.sharewidgetoption.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.widgetsavetodeviceoption.setTextColor(
                ContextCompat.getColor(
                    contexT,
                    R.color.white
                )
            )
            binding.widgetdeleteoption.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.placeWidgetOption.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.cloneWidgetOption.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.widgetSaveAsImage.setTextColor(ContextCompat.getColor(contexT, R.color.white))



            binding.sharewidgetoption.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_share_dark_mode,
                0,
                0,
                0
            )
            binding.widgetsavetodeviceoption.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_save_alt_dark,
                0,
                0,
                0
            )
            binding.widgetdeleteoption.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_delete_dark,
                0,
                0,
                0
            )
            binding.placeWidgetOption.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_add_to_home_screen_dark,
                0,
                0,
                0
            )
            binding.cloneWidgetOption.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_content_copy_dark,
                0,
                0,
                0
            )
            binding.widgetSaveAsImage.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_outline_image_dark_30,
                0,
                0,
                0
            )


        } else {
            binding.widgetOptionsSheetHeader.setTextColor(
                ContextCompat.getColor(
                    contexT,
                    R.color.Black
                )
            )
            binding.sharewidgetoption.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.widgetsavetodeviceoption.setTextColor(
                ContextCompat.getColor(
                    contexT,
                    R.color.Black
                )
            )
            binding.widgetdeleteoption.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.placeWidgetOption.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.cloneWidgetOption.setTextColor(ContextCompat.getColor(contexT, R.color.Black))


            binding.sharewidgetoption.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_share_30,
                0,
                0,
                0
            )
            binding.widgetsavetodeviceoption.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_save_alt_for_widget_options,
                0,
                0,
                0
            )
            binding.widgetdeleteoption.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_delete_24,
                0,
                0,
                0
            )
            binding.placeWidgetOption.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_add_to_home_screen_24,
                0,
                0,
                0
            )
            binding.cloneWidgetOption.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_content_copy,
                0,
                0,
                0
            )

        }
    }

    fun adjustTheme(appTheme: String) {
        if (appTheme == AppUtils.LIGHT) {
            darkMode(false)
        }
        if (appTheme == AppUtils.DARK) {
            darkMode(true)
        }
        if (appTheme == AppUtils.FOLLOW_SYSTEM) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> darkMode(true)

                Configuration.UI_MODE_NIGHT_NO -> darkMode(false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun placeWidgetOnHomeScreen(widgetData: WidgetData) {
        val appWidgetManager = contexT.getSystemService(AppWidgetManager::class.java)
        val intent = Intent(contexT, PlaceWidgetRequestReceiver::class.java)

        val bundle = Bundle()
        bundle.putString("widgetid", widgetData.widgetID)
        intent.putExtra("prewbundle", bundle)

        var flags = PendingIntent.FLAG_UPDATE_CURRENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        }


        val pendingIntent = PendingIntent.getBroadcast(contexT, 506, intent, flags)
        val componentName = ComponentName(contexT, YourWidget::class.java)

        val views = buildWidgetPreview(widgetData)

        val uiBundle = Bundle()
        uiBundle.putParcelable(AppWidgetManager.EXTRA_APPWIDGET_PREVIEW, views)


        appWidgetManager.requestPinAppWidget(componentName, uiBundle, pendingIntent)

    }

    fun buildWidgetPreview(data: WidgetData): RemoteViews {
        val views = RemoteViews(contexT.packageName, R.layout.your_widget)

        var id = 0
        try {
            val info = data.widgetFontInfo!!
            id = if (info.sourceName != "NA") {
                contexT.resources.getIdentifier(info.sourceName, "font", contexT.packageName)
            } else {
                val fontText: String = if (info.fontStyle == "normal") {
                    info.fontName.lowercase()
                        .replace(" ", "_", true)
                } else {
                    info.fontName.lowercase()
                        .replace(" ", "_", true) + "_" + info.fontStyle.lowercase()
                }


                contexT.resources.getIdentifier(fontText, "font", contexT.packageName)
            }

        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }

        try {
            val bitmap = textBitmap(
                id, data.widgetTextSize, Color.parseColor(
                    data.widgetTextColor!!.colorHexCode
                ), data.widgetText.toString(), data
            )
            views.setImageViewBitmap(R.id.widgetuiimage, bitmap)
            views.setViewPadding(
                R.id.widgetuiimage,
                AppUtils.dptopx(contexT, data.textPadding),
                AppUtils.dptopx(contexT, data.textPadding),
                AppUtils.dptopx(contexT, data.textPadding),
                AppUtils.dptopx(contexT, data.textPadding)
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
            val bitmap = AppUtils.getBitmapWithContentPath(contexT,imageList.get(0), 5)
            views.setImageViewBitmap(R.id.widgetuibackground, bitmap)
        }
        if (data.widgetBackGroundType == "gradient") {
            if (data.widgetBackgroundGradient != null) {
                try {
                    val gradient = if (widgetData.widgetRoundCorners) {
                        contexT.resources.getIdentifier(
                            data.widgetBackgroundGradient!!.sourceName,
                            "drawable",
                            contexT.packageName
                        )
                    } else {
                        val sourceName = "no_corners_" + data.widgetBackgroundGradient!!.sourceName

                        contexT.resources.getIdentifier(sourceName, "drawable", contexT.packageName)
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
        if (widgetData.outlineEnabled) {
            //Set the outline
            val width = "${widgetData.widgetOutlineWidth}dp"
            val drawableName = if (widgetData.widgetRoundCorners) {
                "outline_background_$width"
            } else {
                "no_corners_outline_background_$width"
            }

            val calculatePadding = 5 + widgetData.widgetOutlineWidth
            val reqPadding = AppUtils.dptopx(contexT, calculatePadding)
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
                    contexT.resources.getIdentifier(drawableName, "drawable", contexT.packageName)

                views.setImageViewResource(R.id.widgetUIOutline, drawable)

            } catch (e: Resources.NotFoundException) {
                e.printStackTrace()
            }

            if (widgetData.widgetOutlineColor != null) {
                //Set the color
                try {
                    views.setInt(
                        R.id.widgetUIOutline, "setColorFilter", Color.parseColor(
                            widgetData.widgetOutlineColor!!.colorHexCode
                        )
                    )


                    val color = Color.parseColor(widgetData.widgetOutlineColor!!.colorHexCode)
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

    fun textBitmap(font: Int, size: Int, color: Int, text: String, data: WidgetData): Bitmap {
        val textView = TextView(contexT)
        textView.setTypeface(ResourcesCompat.getFont(contexT, R.font.open_sans_semibold))
        try {
            textView.setTypeface(ResourcesCompat.getFont(contexT, font))

        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
        textView.setText(text)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
        textView.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    contexT,
                    R.color.Black
                )
            )
        )

        try {
            textView.setTextColor(ColorStateList.valueOf(color))

            if (data.textShadowEnabled) {
                if (data.textShadowData != null) {
                    textView.setShadowLayer(
                        AppUtils.dptopx(contexT, data.textShadowData!!.shadowRadius).toFloat(),
                        AppUtils.dptopx(contexT, data.textShadowData!!.horizontalDir).toFloat(),
                        AppUtils.dptopx(contexT, data.textShadowData!!.verticalDir).toFloat(),
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

    fun cloneWidget() {
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


            saveNewWidget(clonedWidgetData)

            withContext(Dispatchers.Main) {
                optionsInterface.widgetCloned()
                dismiss()
            }
        }
    }

    fun saveNewWidget(widgetData: WidgetData) {
        val widgetsList = ArrayList<WidgetData>()

        val sharedPreferences = contexT.getSharedPreferences("widgetspref", MODE_PRIVATE)

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

    fun setAds() {
        if (activity == null) {
            return
        }
        val themePreferences = requireActivity().getSharedPreferences("apppref",
            Context.MODE_PRIVATE
        )

        if (!themePreferences.getBoolean("disableads", false)) {
            MobileAds.initialize(requireActivity()) {
                val adRequest = AdRequest.Builder().build()
                binding.bannerad8.loadAd(adRequest)
            }



            binding.bannerad8.visibility = View.VISIBLE
        } else {
            binding.bannerad8.visibility = View.GONE
        }
    }

}