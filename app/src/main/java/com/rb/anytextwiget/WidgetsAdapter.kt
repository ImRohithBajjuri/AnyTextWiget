package com.rb.anytextwiget

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.*
import android.app.ActivityOptions
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TypefaceSpan
import android.util.TypedValue
import android.view.*
import android.view.animation.Animation
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.MotionEventCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rb.anytextwiget.databinding.WidgetLayoutBinding
import kotlinx.coroutines.*


class WidgetsAdapter constructor() : RecyclerView.Adapter<WidgetsAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var dataList: List<WidgetData>
    lateinit var usingFrom: String
    var widgetUIID: Int = 0
    var optionsInterface: WidgetOptionsSheet.WidgetOptionsInterface? = null

    lateinit var ff: PendingIntent

    interface SortInterface {
        fun startSort(holder: ViewHolder)
    }

    var sortInterface: SortInterface? = null

    companion object {
        var isSorting: Boolean = false
    }

    constructor(
        context: Context,
        dataList: List<WidgetData>,
        widgetUIID: Int,
        usingFrom: String,
        optionsInterface: WidgetOptionsSheet.WidgetOptionsInterface?, sortInterface: SortInterface?


    ) : this() {
        this.context = context
        this.dataList = dataList
        this.widgetUIID = widgetUIID
        this.usingFrom = usingFrom
        this.optionsInterface = optionsInterface
        this.sortInterface = sortInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = WidgetLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.widgetText.setText(dataList[position].widgetText)

        if (dataList.get(position).widgetTextColor?.colorHexCode != null) {
            try {
                holder.binding.widgetText.setTextColor(
                    ColorStateList.valueOf(
                        Color.parseColor(
                            dataList.get(
                                position
                            ).widgetTextColor?.colorHexCode
                        )
                    )
                )
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }

        holder.binding.widgetText.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            dataList.get(position).widgetTextSize.toFloat()
        )

        holder.binding.widgetText.setPadding(
            AppUtils.dptopx(
                context,
                dataList.get(position).textPadding
            )
        )


        try {
            val info = dataList[position].widgetFontInfo!!
            if (info.sourceName != "NA") {
                val id =
                    context.resources.getIdentifier(info.sourceName, "font", context.packageName)
                holder.binding.widgetText.typeface = ResourcesCompat.getFont(context, id)
            } else {
                val fontText: String = if (info.fontStyle == "normal") {
                    info.fontName.lowercase()
                        .replace(" ", "_", true)
                } else {
                    info.fontName.lowercase()
                        .replace(" ", "_", true) + "_" + info.fontStyle.lowercase()
                }


                val id = context.resources.getIdentifier(fontText, "font", context.packageName)
                holder.binding.widgetText.typeface = ResourcesCompat.getFont(context, id)
            }

        } catch (e: Resources.NotFoundException) {
            holder.binding.widgetText.setTypeface(
                ResourcesCompat.getFont(
                    context,
                    R.font.open_sans_semibold
                )
            )

            e.printStackTrace()
        }


        if (dataList[position].widgetBackGroundType == "color") {


            try {

                holder.binding.widgetcard.setCardBackgroundColor(
                    ColorStateList.valueOf(
                        Color.parseColor(
                            dataList.get(
                                position
                            ).widgetBackgroundColor!!.colorHexCode
                        )
                    )
                )

                val colorData = dataList.get(position).widgetBackgroundColor


                if (holder.isDark) {
                    if (colorData!!.colorHexCode == "#000000" || colorData.colorHexCode!!.substring(
                            3,
                            colorData.colorHexCode!!.length
                        ).equals("000000")
                    ) {
                        holder.binding.widgetcard.cardElevation =
                            AppUtils.dptopx(context, 3).toFloat()
                    }
                } else {
                    if (colorData!!.colorHexCode == "#EEEEEE" || colorData.colorHexCode!!.substring(
                            3,
                            colorData.colorHexCode!!.length
                        ).equals("EEEEEE")
                    ) {
                        holder.binding.widgetcard.cardElevation =
                            AppUtils.dptopx(context, 3).toFloat()
                    }
                }
            } catch (e: IllegalArgumentException) {
                holder.binding.widgetcard.setCardBackgroundColor(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.white,
                        null
                    )
                )
                e.printStackTrace()
            }
        }


        holder.binding.widgetcard.radius = AppUtils.dptopx(context, 30).toFloat()
        if (dataList.get(position).widgetRoundCorners != null) {
            if (dataList.get(position).widgetRoundCorners) {
                holder.binding.widgetcard.radius = AppUtils.dptopx(context, 30).toFloat()
            } else {
                holder.binding.widgetcard.radius = 0f
            }
        }


        holder.binding.widgetBackgroundFlipper.visibility = View.GONE
        if (dataList[position].widgetBackGroundType == "image") {
            holder.binding.widgetBackgroundFlipper.visibility = View.VISIBLE
            holder.multiImageList.clear()
            holder.multiImageList.addAll(dataList.get(position).widgetMultiImageList!!)
            holder.multiImageAdapter.notifyDataSetChanged()
        }


        holder.binding.widgetGradientBgr.visibility = View.GONE
        if (dataList[position].widgetBackGroundType == "gradient") {
            if (dataList[position].widgetBackgroundGradient != null) {
                holder.binding.widgetGradientBgr.visibility = View.VISIBLE
                try {
                    val sourceName =
                        "no_corners_" + dataList[position].widgetBackgroundGradient!!.sourceName
                    val gradient =
                        context.resources.getIdentifier(sourceName, "drawable", context.packageName)
                    val drawable = ContextCompat.getDrawable(context, gradient)

                    holder.binding.widgetGradientBgr.setImageDrawable(drawable)
                } catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                }
            }
        }


        val verticalGravity: Int
        val horizontalGravity: Int
        if (dataList.get(position).widgetTextVerticalGravity != null) {
            verticalGravity = dataList.get(position).widgetTextVerticalGravity!!.gravityValue
        } else {
            verticalGravity = Gravity.CENTER_VERTICAL
        }

        if (dataList.get(position).widgetTextHorizontalGravity != null) {
            horizontalGravity = dataList.get(position).widgetTextHorizontalGravity!!.gravityValue
        } else {
            horizontalGravity = Gravity.CENTER_HORIZONTAL
        }

        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = verticalGravity or horizontalGravity

        holder.binding.widgetText.gravity = verticalGravity or horizontalGravity

        /*       try {
                   holder.itemView.widgetText.layoutParams = layoutParams
               }
               catch (e: ClassCastException){
                   e.printStackTrace()
               }*/

        //Set the outline and it's color
        holder.binding.widgetcard.strokeWidth = 0
        if (dataList.get(position).outlineEnabled) {
            holder.binding.widgetcard.strokeWidth =
                AppUtils.dptopx(context, dataList.get(position).widgetOutlineWidth)

            try {
                holder.binding.widgetcard.setStrokeColor(
                    ColorStateList.valueOf(
                        Color.parseColor(
                            dataList[position].widgetOutlineColor!!.colorHexCode
                        )
                    )
                )
            } catch (e: IllegalArgumentException) {
                holder.binding.widgetcard.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#000000")))
                e.printStackTrace()
            }

        }


        //Set the text shadow
        try {
            holder.binding.widgetText.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
            if (dataList[position].textShadowEnabled) {
                val data = dataList[position].textShadowData
                if (data != null) {
                    holder.binding.widgetText.setShadowLayer(
                        AppUtils.dptopx(context, data.shadowRadius).toFloat(),
                        AppUtils.dptopx(context, data.horizontalDir).toFloat(),
                        AppUtils.dptopx(context, data.verticalDir).toFloat(),
                        Color.parseColor(data.shadowColor!!.colorHexCode)
                    )
                }
            }

        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }


        CoroutineScope(Dispatchers.Main).launch {
            if (isSorting) {
                holder.binding.sortHolder.visibility = View.VISIBLE
            } else {
                holder.binding.sortHolder.visibility = View.GONE
            }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(val binding: WidgetLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var multiImageList: MutableList<String>
        lateinit var multiImageAdapter: MultiImageAdapter
        var isDark: Boolean = false


        init {
            val sharedPreferences = context.getSharedPreferences("apppref", Context.MODE_PRIVATE)

            //Adjust UI with theme
            adjustTheme(sharedPreferences.getString("apptheme", AppUtils.LIGHT)!!)


            multiImageList = ArrayList()
            multiImageAdapter = MultiImageAdapter(
                context,
                multiImageList,
                null,
                MultiImageAdapter.fromWidgetAdapter,
                null
            )
            binding.widgetBackgroundFlipper.adapter = multiImageAdapter




            binding.root.setOnClickListener {
                if (!isSorting) {
                    val animation = AnimUtils.pressAnim(object : Animation.AnimationListener {
                        override fun onAnimationStart(p0: Animation?) {

                        }

                        override fun onAnimationEnd(p0: Animation?) {
                            if (usingFrom == "selection") {
                                CoroutineScope(Dispatchers.Main).launch {
                                    //Check if the image size is too large and then load a reduced version
                                    try {
                                        handleSelectionClick(3)
                                    } catch (e: OutOfMemoryError) {

                                        handleSelectionClick(5)
                                        e.printStackTrace()
                                    } catch (e: IllegalArgumentException) {

                                        handleSelectionClick(5)
                                        e.printStackTrace()
                                    } catch (e: Resources.NotFoundException) {

                                        e.printStackTrace()
                                    }
                                }
                            }
                            if (usingFrom == "main") {
                                handleMainClick()
                            }
                        }

                        override fun onAnimationRepeat(p0: Animation?) {
                        }

                    })
                    it.startAnimation(animation)
                }
            }

            binding.root.setOnLongClickListener {
                if (!isSorting) {
                    val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                100,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    } else {
                        vibrator.vibrate(100)

                    }

                    if (usingFrom.equals("main")) {
                        val widgetOptionsSheet = WidgetOptionsSheet(
                            dataList.get(adapterPosition),
                            optionsInterface!!
                        )
                        widgetOptionsSheet.show(
                            (context as AppCompatActivity).supportFragmentManager,
                            "WOSUseCaseOne"
                        )
                    }
                }

                true
            }

            binding.sortHolder.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        sortInterface!!.startSort(this@ViewHolder)
                    }
                    return true
                }

            })

        }

        suspend fun handleSelectionClick(sampleSize: Int) {
            val data = dataList.get(adapterPosition)

            //Get the widget view and update it with proper data
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

                val multiImageIntent = Intent(context, WidgetRemoteService::class.java)
                multiImageIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetUIID)
                multiImageIntent.data = Uri.parse(multiImageIntent.toUri(Intent.URI_INTENT_SCHEME))
                multiImageIntent.putStringArrayListExtra("imageList", imageList)
                multiImageIntent.putExtra("sampleSize", sampleSize)

                views.setRemoteAdapter(R.id.widgetUIBackgroundFlipper, multiImageIntent)
                views.setViewVisibility(R.id.widgetuibackground, View.GONE)
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
                            val sourceName =
                                "no_corners_" + data.widgetBackgroundGradient!!.sourceName
                            context.resources.getIdentifier(
                                sourceName,
                                "drawable",
                                context.packageName
                            )
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
                views.setInt(
                    R.id.widgetUIImageLayout,
                    "setVerticalGravity",
                    Gravity.CENTER_VERTICAL
                )

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


            var pendingIntent: PendingIntent


            //Set the action with the new action data. And in case if there is no action data (old widgets),
            // then use the old way of passing pending intent

            //Initialize the flags
            var flags = PendingIntent.FLAG_UPDATE_CURRENT

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flags = PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            }



            if (data.widgetClickAction != null) {
                if (data.widgetClickAction!!.actionType == AppUtils.ACTIONS_SIMPLE) {
                    val intent3 = Intent(context, YourWidget::class.java)
                    intent3.action = "widgetClick"
                    intent3.putExtra("widgetUIID", widgetUIID)
                    intent3.putExtra("actionName", data.widgetClickAction!!.actionName)
                    pendingIntent = PendingIntent.getBroadcast(context, widgetUIID, intent3, flags)
                } else {
                    val intent =
                        context.packageManager.getLaunchIntentForPackage(data.widgetClickAction!!.appPackageName)

                    if (intent != null) {
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        pendingIntent =
                            PendingIntent.getActivity(context, widgetUIID, intent, flags)
                    } else {
                        Toast.makeText(
                            context,
                            "${data.widgetClickAction!!.actionName} is not available for click",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent2 = Intent(context, MainActivity::class.java)
                        intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        pendingIntent =
                            PendingIntent.getActivity(context, widgetUIID, intent2, flags)
                    }
                }
            } else {
                if (data.widgetBackGroundType.equals("image")) {
                    if (data.widgetMultiImageList == null) {
                        val intent2 = Intent(context, MainActivity::class.java)
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        pendingIntent = PendingIntent.getActivity(
                            context,
                            widgetUIID,
                            intent2,
                            flags
                        )
                    } else {
                        if (data.widgetMultiImageList!!.size > 1) {
                            val intent3 = Intent(context, YourWidget::class.java)

                            intent3.action = "widgetClick"
                            intent3.putExtra("widgetUIID", widgetUIID)
                            intent3.putExtra("actionName", AppUtils.ACTION_NEXTIMAGE)
                            pendingIntent =
                                PendingIntent.getBroadcast(context, widgetUIID, intent3, flags)
                        } else {
                            val intent2 = Intent(context, MainActivity::class.java)
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            pendingIntent = PendingIntent.getActivity(
                                context,
                                widgetUIID,
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
                        widgetUIID,
                        intent2,
                        flags
                    )
                }
            }


            //Set the outline and it's color
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
                    val drawable = context.resources.getIdentifier(
                        drawableName,
                        "drawable",
                        context.packageName
                    )

                    views.setImageViewResource(R.id.widgetUIOutline, drawable)

                } catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                }

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
            } else {
                views.setViewVisibility(R.id.widgetUIOutline, View.GONE)
                views.setViewPadding(R.id.widgetuibackground, 0, 0, 0, 0)
                views.setViewPadding(R.id.widgetUIBackgroundFlipper, 0, 0, 0, 0)

            }

            views.setOnClickPendingIntent(R.id.widgetuiparent, pendingIntent)


            //Save the new UI widget
            val widgetUIData = WidgetUIData()
            widgetUIData.widgetData = data
            widgetUIData.widgetUIID = widgetUIID
            saveNewUIWidget(widgetUIData)


            val widgetManager = AppWidgetManager.getInstance(context)
            widgetManager.updateAppWidget(widgetUIID, views)

            //Set the result to RESULT_OK and finish the activity
            val intent = Intent()
            (context as AppCompatActivity).setResult(RESULT_OK, intent)
            (context as AppCompatActivity).finish()
        }

        fun handleMainClick() {
            val intent = Intent(context, CreateWidgetActivity::class.java)
            intent.putExtra("type", "edit")
            intent.putExtra("currentdata", dataList.get(adapterPosition))
            val bundle = ActivityOptions.makeSceneTransitionAnimation(
                context as AppCompatActivity,
                binding.widgetcard,
                "mainToPreview"
            )
            (context as AppCompatActivity).startActivityForResult(intent, 36, bundle.toBundle())
        }

        fun getJSONFromWidgetData(widgetData: WidgetData): String {
            val gson = Gson()
            return gson.toJson(widgetData)
        }

        fun textBitmap(font: Int, size: Int, color: Int, text: String, data: WidgetData?): Bitmap {
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


                if (data!!.textShadowEnabled) {
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
            textView.setPadding(AppUtils.dptopx(context, 15))



            textView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )



            textView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            textView.layout(0, 0, textView.measuredWidth, textView.measuredHeight)
            val bitmap =
                Bitmap.createBitmap(textView.width, textView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            textView.draw(canvas)
            return bitmap
        }

        fun saveNewUIWidget(widgetUIData: WidgetUIData) {
            val sharedPreferences = context.getSharedPreferences("widgetspref", MODE_PRIVATE)

            val uiList = ArrayList<WidgetUIData>()

            //Get the current saved UI widgets list
            val savedUIWidgetsJSON = sharedPreferences.getString("saveduiwidgets", null)
            if (savedUIWidgetsJSON != null) {
                val savedUIWidgets = getSavedUIWidgets(savedUIWidgetsJSON)
                uiList.addAll(savedUIWidgets)
            }

            //Add the new UI widget and save to shared preferences
            uiList.add(widgetUIData)
            val gson = Gson()
            val savingJSON = gson.toJson(uiList)
            sharedPreferences.edit().putString("saveduiwidgets", savingJSON).apply()
        }

        fun getSavedUIWidgets(json: String): MutableList<WidgetUIData> {
            val gson = Gson()
            val type = object : TypeToken<MutableList<WidgetUIData>>() {}.type
            return gson.fromJson(json, type)
        }

        @Throws(Exception::class)
        fun getCroppedBitmap(bitmap: Bitmap): Bitmap? {
            val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color

            canvas.drawRoundRect(
                rectF, AppUtils.dptopx(context, 15).toFloat(), AppUtils.dptopx(
                    context,
                    15
                ).toFloat(), paint
            )

            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)


            canvas.drawBitmap(bitmap, rect, rectF, paint)
            return output

        }

        fun getRoundBitmap(bitmap: Bitmap): Bitmap {
            //Must be always done in this order!
            val output = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val canvas = Canvas(output)

            paint.isAntiAlias = true

            canvas.drawARGB(0, 0, 0, 0)
            paint.color = -0xbdbdbe

            val rect = Rect(-bitmap.width, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)

            canvas.drawRoundRect(
                rectF, AppUtils.dptopx(context, 20).toFloat(), AppUtils.dptopx(
                    context,
                    20
                ).toFloat(), paint
            )
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

            canvas.drawBitmap(bitmap, rect, rectF, paint)

            return output
        }

        fun getRoundBitmap2(bitmap: Bitmap): Bitmap {
            val roundBitmap = RoundedBitmapDrawableFactory.create(context.resources, bitmap)
            roundBitmap.cornerRadius = 20f
            roundBitmap.setAntiAlias(true)
            roundBitmap.isFilterBitmap = true
            return roundBitmap.bitmap!!
        }

        fun adjustTheme(appTheme: String) {
            if (appTheme == AppUtils.LIGHT) {
                isDark = false
            }
            if (appTheme == AppUtils.DARK) {
                isDark = true
            }
            if (appTheme == AppUtils.FOLLOW_SYSTEM) {
                when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        isDark = true
                    }

                    Configuration.UI_MODE_NIGHT_NO -> {
                        isDark = false
                    }
                }
            }
        }

    }

    fun askPermission() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Permission required!")
        builder.setMessage("Storage permission is required for viewing widgets with image background")
        builder.setPositiveButton("GIVE", DialogInterface.OnClickListener { dialogInterface, i ->
ActivityCompat.requestPermissions(
                context as AppCompatActivity, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 99
            )


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    context as AppCompatActivity, arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.ACCESS_MEDIA_LOCATION
                    ), 99
                )
            }
            else {
                ActivityCompat.requestPermissions(
                    context as AppCompatActivity, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 99
                )
            }
        })

        builder.show()
    }

}