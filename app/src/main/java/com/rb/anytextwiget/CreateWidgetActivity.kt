package com.rb.anytextwiget

import android.Manifest
import android.animation.Animator
import android.animation.LayoutTransition
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.database.CursorIndexOutOfBoundsException
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.hardware.camera2.CameraManager
import android.icu.text.TimeZoneNames
import android.icu.util.TimeZone
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.*
import android.view.View.GONE
import android.view.animation.*
import android.widget.*
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setMargins
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.rb.anytextwiget.databinding.ActivityCreateWidgetBinding
import com.rb.anytextwiget.databinding.AddNameLayoutBinding
import com.rb.anytextwiget.databinding.SetTextSizeLayoutBinding
import com.rb.anytextwiget.jetpackUI.CreationPage
import com.rb.anytextwiget.ui.theme.AnyTextWigetTheme
import kotlinx.coroutines.*
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min


class CreateWidgetActivity : AppCompatActivity(), ColorPickerDialogListener,
    MultiImageAdapter.MultiImageInterface,
    TextGravitySelectionAdapter.TextGravitySelectionInterface,
    ActionSelectionSheet.ActionSheetInterface, GradientsSheet.GradientsListener {
    lateinit var itemInterface: ColorItemsAdapter.ColorItemInterface
    lateinit var currentWidget: WidgetData
    lateinit var defaultColors: MutableList<ColorData>
    lateinit var colorSheetInterface: ColorSelectionSheet.ColorSheetInterface
    lateinit var fontItemsInterface: FontsAdapter.fontItemInterface
    lateinit var type: String
    var colorsEdited: Boolean = false
    lateinit var multiImageList: MutableList<String>
    lateinit var multiImageAdapter: MultiImageAdapter
    lateinit var themePreferences: SharedPreferences
    var isDark: Boolean = false
    var isInitialNextImageActionSet = false
    lateinit var appsList: MutableList<String>
    lateinit var gradientsList: MutableList<GradientData>

    lateinit var binding: ActivityCreateWidgetBinding

    lateinit var imageLauncher: ActivityResultLauncher<Intent>

    lateinit var creationPage: CreationPage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themePreferences = getSharedPreferences("apppref", MODE_PRIVATE)
        if (themePreferences.getString("apptheme", AppUtils.LIGHT)!! == AppUtils.LIGHT) {
            setTheme(R.style.AppTheme)
        }
        if (themePreferences.getString("apptheme", AppUtils.LIGHT)!! == AppUtils.DARK) {
            setTheme(R.style.AppThemeDark)
        }
        if (themePreferences.getString("apptheme", AppUtils.LIGHT)!! == AppUtils.FOLLOW_SYSTEM) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    setTheme(R.style.AppThemeDark)
                }

                Configuration.UI_MODE_NIGHT_NO -> {
                    setTheme(R.style.AppTheme)
                }
            }
        }

        binding = ActivityCreateWidgetBinding.inflate(LayoutInflater.from(this))
        type = intent.getStringExtra("type")!!

        gradientsList = ArrayList()
        multiImageList = ArrayList()

        val sharedPreferences = getSharedPreferences("colorspref", MODE_PRIVATE)

        //Get the default colors json;
        val defaultColorsJSON = sharedPreferences.getString("defaultcolors", null)



        defaultColors = if (defaultColorsJSON == null) {
            AppUtils.addDefaultColors(applicationContext)
        } else {
            AppUtils.getDefaultColorsFromJson(defaultColorsJSON)
        }

        val isJetpackUI = true
        if (isJetpackUI) {
            if (type == "create") {

                currentWidget = WidgetData()
                currentWidget.widgetTextColor = defaultColors.get(0)
                currentWidget.widgetBackGroundType = "color"
                currentWidget.widgetBackgroundColor = defaultColors.get(1)
                currentWidget.widgetText = "Add some text"
                currentWidget.widgetID = UUID.randomUUID().toString()
                currentWidget.widgetTextSize = 21
                currentWidget.widgetTextFontID = R.font.open_sans_semibold
                currentWidget.widgetRoundCorners = true
                currentWidget.widgetMultiImageList = multiImageList
                currentWidget.outlineEnabled = false
                currentWidget.widgetOutlineColor = defaultColors.get(0)
                currentWidget.widgetOutlineWidth = 3
                currentWidget.textShadowEnabled = false

                //Get gradients
                CoroutineScope(Dispatchers.IO).launch {
                    gradientsList = AppUtils.getGradients(this@CreateWidgetActivity)
                    currentWidget.widgetBackgroundGradient = gradientsList[0]
                }


                //Set default font data with open sans semibold
                val widgetFontData = WidgetFontInfo()
                widgetFontData.fontName = "Open sans semibold"
                widgetFontData.fontStyle = AppUtils.semibold
                widgetFontData.sourceName = "open_sans_semibold"
                currentWidget.widgetFontInfo = widgetFontData

                //Set default text gravities
                currentWidget.widgetTextVerticalGravity =
                    TextGravityData("Center", Gravity.CENTER_VERTICAL)
                currentWidget.widgetTextHorizontalGravity =
                    TextGravityData("Center", Gravity.CENTER_HORIZONTAL)

                //Set the default action data
                val actionData = ActionData()
                actionData.actionName = "Any Text Widget"
                actionData.actionType = AppUtils.ACTIONS_APP
                actionData.appPackageName = "com.rb.anytextwiget"
                currentWidget.widgetClickAction = actionData

                //Set the default text shadow and switch
                val textShadowData = TextShadowData()
                textShadowData.shadowColor = defaultColors[0]
                currentWidget.textShadowData = textShadowData


            }

            if (type == "edit") {
                currentWidget = intent.getSerializableExtra("currentdata") as WidgetData
            }

            imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == AppCompatActivity.RESULT_OK) {
                    runBlocking {
                        CoroutineScope(Dispatchers.IO).launch {
                            val inputStream = contentResolver.openInputStream(it!!.data!!.data!!)
                            val bit = BitmapFactory.decodeStream(inputStream)

                            AppUtils.saveImageToCache(this@CreateWidgetActivity, bit, object : AppUtils.WidgetSaveInterface {
                                override fun widgetSaved(savedPath: String) {
                                    creationPage.currentData.widgetMultiImageList!!.add(savedPath)
                                    creationPage.imagesList.add(savedPath)


                                    //Set action to show image and update the initial action val to true
                                    if (!isInitialNextImageActionSet) {
                                        val actionData = ActionData()
                                        actionData.actionType = AppUtils.ACTIONS_SIMPLE
                                        actionData.actionName = AppUtils.ACTION_NEXTIMAGE

                                        creationPage.currentData.widgetClickAction = actionData
                                        isInitialNextImageActionSet = true
                                    }

                                    Toast.makeText(this@CreateWidgetActivity, "Image added, swipe to view", Toast.LENGTH_LONG).show()
                                }

                                override fun widgetSaveFailed() {
                                }

                            })

                        }
                    }
                }
            }


            creationPage = CreationPage(this, currentData = currentWidget, imageLauncher)
            setContent {
                AnyTextWigetTheme() {
                   creationPage.CreationPageUI()
                }
            }
            return
        }

        setContentView(binding.root)

        //Adjust UI with theme
        adjustTheme(themePreferences.getString("apptheme", AppUtils.LIGHT)!!)



        multiImageAdapter = MultiImageAdapter(
            this,
            multiImageList,
            this,
            MultiImageAdapter.fromCreateWidgetActivity,
            null
        )
        binding.widgetBackgroundImageFlipper.adapter = multiImageAdapter
        binding.createWidgetPreviewMultiImageFlipper.adapter = multiImageAdapter


        //If the type is create then the default color be black;
        if (type == "create") {
            binding.createwidgettoolbar.setTitle("Create a new widget")

            currentWidget = WidgetData()
            currentWidget.widgetTextColor = defaultColors.get(0)
            currentWidget.widgetBackGroundType = "color"
            currentWidget.widgetBackgroundColor = defaultColors.get(1)
            currentWidget.widgetText = "Add some text"
            currentWidget.widgetID = UUID.randomUUID().toString()
            currentWidget.widgetTextSize = 21
            currentWidget.widgetTextFontID = R.font.open_sans_semibold
            currentWidget.widgetRoundCorners = true
            currentWidget.widgetMultiImageList = multiImageList
            currentWidget.outlineEnabled = false
            currentWidget.widgetOutlineColor = defaultColors.get(0)
            currentWidget.widgetOutlineWidth = 3
            currentWidget.textShadowEnabled = false

            //Get gradients
            CoroutineScope(Dispatchers.IO).launch {
                gradientsList = AppUtils.getGradients(this@CreateWidgetActivity)
                currentWidget.widgetBackgroundGradient = gradientsList[0]
            }


            //Set default font data with open sans semibold
            val widgetFontData = WidgetFontInfo()
            widgetFontData.fontName = "Open sans semibold"
            widgetFontData.fontStyle = AppUtils.semibold
            widgetFontData.sourceName = "open_sans_semibold"
            currentWidget.widgetFontInfo = widgetFontData

            //Set default text gravities
            currentWidget.widgetTextVerticalGravity =
                TextGravityData("Center", Gravity.CENTER_VERTICAL)
            currentWidget.widgetTextHorizontalGravity =
                TextGravityData("Center", Gravity.CENTER_HORIZONTAL)

            //Set the default action data
            val actionData = ActionData()
            actionData.actionName = "Any Text Widget"
            actionData.actionType = AppUtils.ACTIONS_APP
            actionData.appPackageName = "com.rb.anytextwiget"
            currentWidget.widgetClickAction = actionData


            //Set the current color as Black for default;
            setCurrentTextColor(currentWidget.widgetTextColor!!)

            //Set the current font as Open Sans semi-bold for default
            setCurrentTextStyle(widgetFontData)

            //Set the background type to color as default
            handleWidgetBackgroundSelection("solid")

            //Set the current widget background as White for default
            setCurrentWidgetBackgroundColor(currentWidget.widgetBackgroundColor!!)

            //Set the current round corners as true dor default
            setCurrentRoundCorners(true)

            //Set the current widget text gravities
            setVerticalGravity(currentWidget.widgetTextVerticalGravity!!)
            setHorizontalGravity(currentWidget.widgetTextHorizontalGravity!!)

            //Set the current widget click action
            setClickAction(actionData)


            //Set the default widget outline switch
            setOutline(currentWidget.outlineEnabled)
            binding.widgetOutlineSwitch.isChecked = false


            //Set the default outline color
            setOutlineColor(currentWidget.widgetOutlineColor!!)


            //Set the default outline width
            setOutlineWidth(currentWidget.widgetOutlineWidth)
            binding.widgetOutlineWidthSeekBar.progress = currentWidget.widgetOutlineWidth


            //Set the default text shadow and switch
            val textShadowData = TextShadowData()
            textShadowData.shadowColor = defaultColors[0]
            currentWidget.textShadowData = textShadowData

            setTextShadow(currentWidget.textShadowEnabled)
            binding.textShadowSwitch.isChecked = false


            //Set the default text shadow color
            setTextShadowRadius(currentWidget.textShadowData!!.shadowRadius)
            binding.textShadowRadiusSeekBar.progress = currentWidget.textShadowData!!.shadowRadius


            //Set the default text shadow with modified horizontal dir
            setTextShadowHDir((currentWidget.textShadowData!!.horizontalDir))

            //Change the modified value to it's real value by adding 50
            binding.textShadowHDirSeekBar.progress = currentWidget.textShadowData!!.horizontalDir + 10


            //Set the default text shadow with modified vertical dir
            setTextShadowVDir((currentWidget.textShadowData!!.verticalDir))

            //Change the modified value to it's real value by adding 50
            binding.textShadowVDirSeekBar.progress = currentWidget.textShadowData!!.verticalDir + 10

            //Set the default text shadow color
            setTextShadowColor(currentWidget.textShadowData!!.shadowColor!!)

        }

        if (type == "edit") {
            //Get the gradients
            CoroutineScope(Dispatchers.IO).launch {
                gradientsList = AppUtils.getGradients(this@CreateWidgetActivity)
            }

            binding.createwidgettoolbar.setTitle("Edit your widget")


            currentWidget = intent.getSerializableExtra("currentdata") as WidgetData

            //Check if round corners is null;
            if (currentWidget.widgetRoundCorners == null) {
                currentWidget.widgetRoundCorners = true
            }



            setCurrentTextColor(currentWidget.widgetTextColor!!)

            setCurrentTextStyle(currentWidget.widgetFontInfo!!)



            setCurrentText(currentWidget.widgetText!!)


            setCurrentTextSize(currentWidget.widgetTextSize)

            setCurrentTextPadding(currentWidget.textPadding)

            if (currentWidget.widgetBackGroundType == "color") {
                handleWidgetBackgroundSelection("solid")
                setCurrentWidgetBackgroundColor(currentWidget.widgetBackgroundColor!!)
            }

            if (currentWidget.widgetBackGroundType == "image") {
                handleWidgetBackgroundSelection("image")
                //Check is image uri is not null and then set the image background
                if (currentWidget.widgetMultiImageList == null) {
                    if (currentWidget.widgetBackgroundImageUri != null) {
                        multiImageList.add(currentWidget.widgetBackgroundImageUri!!)
                    }
                } else {
                    multiImageList.addAll(currentWidget.widgetMultiImageList!!)
                }
                addImageBackground()
            }

            if (currentWidget.widgetBackGroundType == "gradient") {
                handleWidgetBackgroundSelection("gradient")
            }


            if (currentWidget.widgetTextVerticalGravity == null) {
                val verticalData = TextGravityData("Center", Gravity.CENTER_VERTICAL)
                currentWidget.widgetTextVerticalGravity = verticalData
            }

            if (currentWidget.widgetTextHorizontalGravity == null) {
                val horizontalData = TextGravityData("Center", Gravity.CENTER_HORIZONTAL)
                currentWidget.widgetTextHorizontalGravity = horizontalData
            }

            setVerticalGravity(currentWidget.widgetTextVerticalGravity!!)
            setHorizontalGravity(currentWidget.widgetTextHorizontalGravity!!)

            if (currentWidget.widgetClickAction == null) {
                //Set the default action data
                val actionData = ActionData()
                actionData.actionName = "Any Text Widget"
                actionData.actionType = AppUtils.ACTIONS_APP
                actionData.appPackageName = "com.rb.anytextwiget"
                currentWidget.widgetClickAction = actionData
            }

            setClickAction(currentWidget.widgetClickAction!!)

            setCurrentRoundCorners(currentWidget.widgetRoundCorners)
            binding.widgetroundcornersswitch.isChecked = currentWidget.widgetRoundCorners


            //Set the outline switch
            setOutline(currentWidget.outlineEnabled)
            binding.widgetOutlineSwitch.isChecked = currentWidget.outlineEnabled

            //Set the outline color
            if (currentWidget.widgetOutlineColor != null) {
                setOutlineColor(currentWidget.widgetOutlineColor!!)
            }

            //Set the outline width
            setOutlineWidth(currentWidget.widgetOutlineWidth)


            //Set the text shadow switch
            setTextShadow(currentWidget.textShadowEnabled)
            binding.textShadowSwitch.isChecked = currentWidget.textShadowEnabled

            if (currentWidget.textShadowData == null) {
                val textShadowData = TextShadowData()
                textShadowData.shadowColor = defaultColors[0]
                currentWidget.textShadowData = textShadowData
            }


            //Set the text shadow radius
            setTextShadowRadius(currentWidget.textShadowData!!.shadowRadius)
            binding.textShadowRadiusSeekBar.progress = currentWidget.textShadowData!!.shadowRadius


            //Set the text shadow horizontal direction
            setTextShadowHDir(currentWidget.textShadowData!!.horizontalDir)
            binding.textShadowHDirSeekBar.progress = currentWidget.textShadowData!!.horizontalDir + 10


            //Set the text shadow vertical direction
            setTextShadowVDir(currentWidget.textShadowData!!.verticalDir)
            binding.textShadowVDirSeekBar.progress = currentWidget.textShadowData!!.horizontalDir + 10

            //Set text shadow color
            setTextShadowColor(currentWidget.textShadowData!!.shadowColor!!)

        }

        //Handle action image tint
        handleActionImageTint(currentWidget.widgetClickAction!!)

        //Set the corners
        setRoundCorners()


        binding.widgetClickActionName.isSelected = true
        binding. widgetClickActionName.maxLines = 1
        binding.widgetClickActionName.setHorizontallyScrolling(true)


        appsList = ArrayList()
        CoroutineScope(Dispatchers.IO).launch {
            //Get the apps list
            appsList = AppUtils.getInstalledApps(this@CreateWidgetActivity)

            //Sort the apps list in alphabetical order
            sortApps()
        }

        //Check and add if the current text and background colors are new
        CoroutineScope(Dispatchers.IO).launch {
            checkAndAddColors()
        }
/*

        //Instantiate Multi text adapter
        val layoutManager = LinearLayoutManager(this)
        multiTextAdapter = MultiTextAdapter(this, currentWidget.widgetMultiTextList!!, this)
        createWidgetMultiTextRecy.layoutManager = layoutManager
        createWidgetMultiTextRecy.adapter = multiTextAdapter
*/


        val layoutTransition = LayoutTransition()
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.createWidgetInputLayout.layoutTransition = layoutTransition
        binding.createWidgetContentParent.layoutTransition = layoutTransition


        itemInterface = object : ColorItemsAdapter.ColorItemInterface {
            override fun itemClicked(colorData: ColorData, callFrom: String) {
                if (callFrom == ColorSelectionSheet.TEXT_COLOR) {
                    setCurrentTextColor(colorData)
                }
                if (callFrom == ColorSelectionSheet.BACKGROUND_COLOR) {
                    setCurrentWidgetBackgroundColor(colorData)
                }
                if (callFrom == ColorSelectionSheet.OUTLINE_COLOR) {
                    setOutlineColor(colorData)
                }
                if (callFrom == ColorSelectionSheet.TXT_SHADOW_COLOR) {
                    setTextShadowColor(colorData)
                }


            }
        }

        fontItemsInterface = object : FontsAdapter.fontItemInterface {
            override fun itemClicked(widgetFontInfo: WidgetFontInfo) {
                setCurrentTextStyle(widgetFontInfo)
            }
        }


        //Set checked listener to listen to changes
        binding.widgetOutlineSwitch.setOnCheckedChangeListener { p0, p1 ->
            //Update the Data and UI
            setOutline(p1)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Update the data
                currentWidget.widgetText = p0.toString()

                //Update the UI
                binding. previewText.text = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }
        binding.createWidgetInputText.addTextChangedListener(textWatcher)


        binding.createwidgettoolbar.setNavigationOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setNegativeButton(
                "NO",
                DialogInterface.OnClickListener { dialogInterface, i -> })
            builder.setPositiveButton("YES", DialogInterface.OnClickListener { dialogInterface, i ->
                if (!colorsEdited) {
                    setResult(RESULT_CANCELED)
                } else {
                    if (type == "create") {
                        setResult(RESULT_OK)
                    } else {
                        val intent = Intent()
                        intent.putExtra("widgetdata", currentWidget)
                        setResult(RESULT_OK, intent)
                    }
                }
                if (type.equals("create")) {
                    finish()
                }
                if (type.equals("edit")) {
                    supportFinishAfterTransition()
                }
            })

            if (type.equals("create")) {
                builder.setTitle("Unfinished widget!")
                builder.setMessage("This widget will not be saved, do you still want to continue?")
            }
            if (type.equals("edit")) {
                builder.setTitle("Unfinished edits to widget!")
                builder.setMessage("Edits to this widget will not be saved, do you still want to continue?")
            }

            builder.show()

        }



        binding.widgettextcolorcard.setOnClickListener {
            val animation = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    val colorSelectionSheet = ColorSelectionSheet(
                        itemInterface,
                        currentWidget.widgetTextColor!!,
                        ColorSelectionSheet.TEXT_COLOR
                    )
                    colorSelectionSheet.show(supportFragmentManager, "colorSheetUseCase1")
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
            it.startAnimation(animation)
        }

        binding.savewidgetbutton.setOnClickListener {
            val animation = AnimUtils.blinkAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    if (type.equals("create")) {
                        //Check if the user has added an image if selected image background and then change the background type accordingly
                        if (currentWidget.widgetBackGroundType.equals("image")) {
                            if (currentWidget.widgetMultiImageList!!.isEmpty()) {
                                noImageAddedDialog(type)
                            } else {
                                saveNewWidget(currentWidget)
                            }
                        } else {
                            saveNewWidget(currentWidget)
                        }
                    }

                    if (type.equals("edit")) {
                        //Check if the user has added an image if selected image background and then change the background type accordingly
                        if (currentWidget.widgetBackGroundType.equals("image")) {
                            if (currentWidget.widgetMultiImageList!!.isEmpty()) {
                                noImageAddedDialog(type)
                            } else {
                                saveEditedWidget(currentWidget)
                            }
                        } else {
                            saveEditedWidget(currentWidget)
                        }
                    }
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }

            })
            it.startAnimation(animation)
        }

        binding.widgettextsizeseekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                setCurrentTextSize(p0!!.progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        binding.widgettextpaddingseekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                setCurrentTextPadding(p0!!.progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })


        binding. widgettextstylecard.setOnClickListener {
            val animation = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    val newFontsSelectionSheet = FontSelectionSheet(
                        currentWidget.widgetFontInfo!!.sourceName,
                        fontItemsInterface
                    )
                    CoroutineScope(Dispatchers.Main).launch {
                        newFontsSelectionSheet.show(
                            supportFragmentManager,
                            "newFontsSheetUseCaseOn"
                        )
                    }
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }

            })
            it.startAnimation(animation)

        }

        binding.widgetbackgroundcolorcard.setOnClickListener {
            val animation = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    val colorSelectionSheet = ColorSelectionSheet(
                        itemInterface,
                        currentWidget.widgetBackgroundColor!!,
                        ColorSelectionSheet.BACKGROUND_COLOR
                    )
                    colorSelectionSheet.show(supportFragmentManager, "colorSheetUseCase2")
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
            it.startAnimation(animation)
        }

        binding.widgetbackgroundcolorselection.setOnClickListener {
            handleWidgetBackgroundSelection("solid")
        }

        binding.widgetbackgroundimageselection.setOnClickListener {
            handleWidgetBackgroundSelection("image")
        }

        binding. gradientSelection.setOnClickListener {
            handleWidgetBackgroundSelection("gradient")
        }

        binding.widgetbackgroundaddimagebutton.setOnClickListener {
            val animation = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
//                    if (ActivityCompat.checkSelfPermission(
//                            this@CreateWidgetActivity,
//                            Manifest.permission.READ_EXTERNAL_STORAGE
//                        ) == PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(
//                            this@CreateWidgetActivity,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE
//                        ) == PackageManager.PERMISSION_GRANTED
//                    ) {
//
//                    } else {
//
//                    }
                    if (AppUtils.hasStoragePermission(this@CreateWidgetActivity)) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(intent, 46)
                    }
                    else {
                        askPermission()
                    }

                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
            it.startAnimation(animation)
        }

        binding.widgetroundcornersswitch.setOnCheckedChangeListener { compoundButton, b ->
            setCurrentRoundCorners(b)
        }

        binding.widgettextsize.setOnClickListener {
            showSetTextSizeDialog(currentWidget.widgetTextSize)
        }

        binding.createwidgetcardpreview.setOnClickListener {
            //Handle click according to the action
            handleClick()


        }

        binding.widgetbackgroundimagecard.setOnClickListener {
            binding.  createWidgetPreviewMultiImageFlipper.showNext()
            binding.  widgetBackgroundImageFlipper.showNext()
            indicateCurrentImage(binding. createWidgetPreviewMultiImageFlipper.displayedChild)
            binding.  widgetMultiImageHelperText.visibility = View.GONE
        }

        binding.widgetVerticalTextGravityLayout.setOnClickListener {
            val textGravitySelectionSheet = TextGravitySelectionSheet(
                TextGravitySelectionSheet.VERTICAL,
                currentWidget.widgetTextVerticalGravity!!.gravityValue,
                this
            )
            textGravitySelectionSheet.show(supportFragmentManager, "useCaseOne")
        }

        binding.widgetHorizontalTextGravityLayout.setOnClickListener {
            val textGravitySelectionSheet = TextGravitySelectionSheet(
                TextGravitySelectionSheet.HORIZONTAL,
                currentWidget.widgetTextHorizontalGravity!!.gravityValue,
                this
            )
            textGravitySelectionSheet.show(supportFragmentManager, "useCaseTwo")
        }

        binding.widgetClickActionCard.setOnClickListener {
            val animation = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (appsList != null) {
                        if (currentWidget.widgetBackGroundType == "image") {
                            if (currentWidget.widgetMultiImageList!!.size > 1) {
                                val actionSheet = ActionSelectionSheet(
                                    appsList,
                                    this@CreateWidgetActivity,
                                    currentWidget.widgetClickAction!!,
                                    true
                                )
                                actionSheet.show(supportFragmentManager, "useCaseOne")
                            } else {
                                val actionSheet = ActionSelectionSheet(
                                    appsList,
                                    this@CreateWidgetActivity,
                                    currentWidget.widgetClickAction!!,
                                    false
                                )
                                actionSheet.show(supportFragmentManager, "useCaseOne")
                            }
                        } else {
                            val actionSheet = ActionSelectionSheet(
                                appsList,
                                this@CreateWidgetActivity,
                                currentWidget.widgetClickAction!!,
                                false
                            )
                            actionSheet.show(supportFragmentManager, "useCaseOne")
                        }
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(animation)
        }

        binding.widgetOutlineColorInfo.setOnClickListener {
            val colorSelectionSheet = ColorSelectionSheet(
                itemInterface,
                currentWidget.widgetOutlineColor!!,
                ColorSelectionSheet.OUTLINE_COLOR
            )
            colorSelectionSheet.show(supportFragmentManager, "colorSheetUseCase3")
        }

        binding.widgetOutlineWidthSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                setOutlineWidth(p0!!.progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        binding.widgetGradientBackgroundCard.setOnClickListener {
            it.startAnimation(AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    val gradientsSheet = GradientsSheet(
                        this@CreateWidgetActivity,
                        currentWidget.widgetBackgroundGradient!!
                    )
                    gradientsSheet.show(supportFragmentManager, "useCaseOne")

                }

                override fun onAnimationRepeat(p0: Animation?) {
                }

            }))


        }

        binding.textShadowSwitch.setOnCheckedChangeListener { compoundButton, b ->
            //Update the text shadow data and ui
            setTextShadow(b)
        }

        binding.textShadowRadiusSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                setTextShadowRadius(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        binding.textShadowHDirSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                //Modify the value with -50 to make accommodation for negative value
                val modifiedValue = p1 - 10

                setTextShadowHDir(modifiedValue)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        binding.textShadowVDirSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                //Modify the value with -50 to make accommodation for negative value
                val modifiedValue = p1 - 10
                setTextShadowVDir(modifiedValue)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        binding.textShadowColorInfo.setOnClickListener {
            val colorSelectionSheet = ColorSelectionSheet(
                itemInterface,
                currentWidget.textShadowData!!.shadowColor!!,
                ColorSelectionSheet.TXT_SHADOW_COLOR
            )
            colorSelectionSheet.show(supportFragmentManager, "colorSheetUseCase4")
        }

        var expanded = true
        binding.previewHeightButton.setOnClickListener {

            val listener = ValueAnimator.AnimatorUpdateListener {
                val rotationValue = it.animatedValue as Float
                binding. previewHeightButton.rotation = rotationValue
            }

            if (expanded) {
                binding.  createwidgetcardpreview.layoutParams.height = AppUtils.dptopx(this, 100)
                binding.  createwidgetcardpreview.requestLayout()


                val valueAnimator = ValueAnimator.ofFloat(binding. previewHeightButton.rotation, 180f)
                valueAnimator.duration = 500
                valueAnimator.addUpdateListener(listener)
                valueAnimator.start()
                expanded = false
            } else {
                binding. createwidgetcardpreview.layoutParams.height = AppUtils.dptopx(this, 200)
                binding. createwidgetcardpreview.requestLayout()


                val valueAnimator = ValueAnimator.ofFloat(binding. previewHeightButton.rotation, 0f)
                valueAnimator.duration = 500
                valueAnimator.addUpdateListener(listener)
                valueAnimator.start()

                expanded = true

            }

        }

        binding.createWidgetTextTypeInput.setOnClickListener {
            handleTextTypeSelection("input")
        }

        binding.textTypeTimer.setOnClickListener {
            handleTextTypeSelection("timer")
        }

        binding.textTypeClock.setOnClickListener {
            handleTextTypeSelection("clock")
        }

        binding.textTimerDateLay.setOnClickListener {
            val datePicker = DatePickerDialog(this, { view, year, month, dayOfMonth ->
                setTextTimerDate(dayOfMonth, month, year)
            }, currentWidget.textTypeTimerYear, currentWidget.textTypeTimerMonth, currentWidget.textTimerDay)
            datePicker.show()
        }

        binding.textTimerTimeLay.setOnClickListener {
            val timePicker = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    setTextTimerTime(hourOfDay, minute)
                }
            },
                12, 12, false)

            timePicker.show()
        }

        binding.textClockCard.setOnClickListener {
            it.startAnimation(AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val timeZoneSheet = TimeZonesSheet()
                    timeZoneSheet.show(supportFragmentManager, "UseCaseOne")
                    timeZoneSheet.setSelectionListener(object : TimeZonesSheet.TimeZonesSheetListener {
                        override fun onTimeZoneSelected(timeZone: String) {
                            setClockTimeZone(timeZone)
                            timeZoneSheet.dismiss()
                        }
                    })
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            }))
        }
    }


    fun setCurrentTextSize(size: Int) {
        binding.widgettextsize.text = size.toString()

        //Update the UI
        binding.widgettextsizeseekbar.progress = size
        binding.previewText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())

        //Update the size in data model
        currentWidget.widgetTextSize = size
    }

    fun setCurrentText(text: String) {
        currentWidget.widgetText = text

        binding.previewText.text = text
        binding.createWidgetInputText.setText(text, TextView.BufferType.EDITABLE)
    }

    fun setCurrentTextColor(colorData: ColorData) {
        //Update the text color in the data model
        currentWidget.widgetTextColor = colorData

        //Update the UI
        try {
            binding.widgettextcolorimage.imageTintList =
                ColorStateList.valueOf(Color.parseColor(colorData.colorHexCode))

            binding.previewText.setTextColor(ColorStateList.valueOf(Color.parseColor(colorData.colorHexCode)))

        } catch (e: IllegalArgumentException) {
            binding.widgettextcolorimage.imageTintList = ColorStateList.valueOf(Color.parseColor("#000000"))

            binding.previewText.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))

            e.printStackTrace()
        }
        binding.widgettextcolorname.setText(colorData.colorName)
        binding.widgettextcolorimage.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent))

        if (isDark) {
            if (colorData.colorHexCode == "#212121" || colorData.colorHexCode!!.substring(
                    3,
                    colorData.colorHexCode!!.length
                ).equals("212121")
            ) {
                binding.widgettextcolorimage.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.Grey))
            }
        } else {
            if (colorData.colorHexCode == "#FFFFFF" || colorData.colorHexCode!!.substring(
                    3,
                    colorData.colorHexCode!!.length
                ).equals("FFFFFF")
            ) {
                binding.widgettextcolorimage.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.Grey))
            }
        }
    }

    fun getCurrentColor(json: String): ColorData {
        val gson = Gson()
        return gson.fromJson(json, ColorData::class.java)
    }

    fun getJSONFromColorData(colorData: ColorData): String {
        val gson = Gson()
        return gson.toJson(colorData)
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

    fun saveNewWidget(widgetData: WidgetData) {
        val widgetsList = ArrayList<WidgetData>()

        val sharedPreferences = getSharedPreferences("widgetspref", MODE_PRIVATE)

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

        //Set the result to RESULT_OK to update the list in MainActivity
        setResult(RESULT_OK)
        supportFinishAfterTransition()
    }

    fun saveEditedWidget(widgetData: WidgetData) {
        val widgetsList = ArrayList<WidgetData>()

        val sharedPreferences = getSharedPreferences("widgetspref", MODE_PRIVATE)

        //Get the saved widgets
        val savedWidgetsJSON = sharedPreferences.getString("savedwidgets", null)

        if (savedWidgetsJSON != null) {
            val savedWidgetsList = getSavedWidgetsFromJSON(savedWidgetsJSON)
            widgetsList.addAll(savedWidgetsList)
        }

        //Edit the widget data and save it to the pref
        for (data in widgetsList) {
            if (data.widgetID.equals(widgetData.widgetID)) {
                widgetsList.set(widgetsList.indexOf(data), widgetData)
            }
        }


        val json = getJSONFromWidgetDataList(widgetsList)

        sharedPreferences.edit().putString("savedwidgets", json).apply()

        //Set the result to RESULT_OK and pass the widget ID to update the list in MainActivity
        val intent = Intent()
        intent.putExtra("widgetdata", widgetData)
        setResult(RESULT_OK, intent)
        supportFinishAfterTransition()
    }

    fun showAddNameDialog(newColor: Int) {
        val view =
         AddNameLayoutBinding.inflate(LayoutInflater.from(this), null, false)
        if (isDark) {
            view.nameinput.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Set a name")
        builder.setPositiveButton("Confirm", DialogInterface.OnClickListener { dialogInterface, i ->
            val colorData = ColorData()
            colorData.colorName = view.nameinput.text.toString()
            colorData.colorHexCode =
                String.format("#%08X", newColor).toUpperCase(Locale.getDefault())
            colorData.ID = UUID.randomUUID().toString()

            colorSheetInterface.saveColor(colorData)

        })
        builder.setView(view.root)
        builder.show()
    }

    fun seTColorSheetInterface(colorSheetInterface: ColorSelectionSheet.ColorSheetInterface) {
        this.colorSheetInterface = colorSheetInterface
    }

    fun setCurrentTextStyle(widgetFontInfo: WidgetFontInfo) {
        //Update the widget font id in the data model
        currentWidget.widgetFontInfo = widgetFontInfo


        //Update the UI
        binding.widgettextstylename.setText(widgetFontInfo.fontName)


        binding.widgettextfonticon.setTypeface(
            ResourcesCompat.getFont(
                applicationContext,
                R.font.open_sans_semibold
            )
        )

        try {

            if (widgetFontInfo.sourceName != "NA") {
                val id = resources.getIdentifier(widgetFontInfo.sourceName, "font", packageName)
                val typeFace = ResourcesCompat.getFont(this, id)
                binding. widgettextfonticon.setTypeface(typeFace)
                binding.previewText.setTypeface(typeFace)
            } else {
                val fontText: String = if (widgetFontInfo.fontStyle == "normal") {
                    widgetFontInfo.fontName.lowercase()
                        .replace(" ", "_", true)
                } else {
                    widgetFontInfo.fontName.lowercase()
                        .replace(" ", "_", true) + "_" + widgetFontInfo.fontStyle.lowercase()
                }


                val id = resources.getIdentifier(fontText, "font", packageName)
                val typeFace2 = ResourcesCompat.getFont(this, id)
                binding.widgettextfonticon.setTypeface(typeFace2)
                binding.previewText.setTypeface(typeFace2)

            }

        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
    }

    fun setCurrentTextPadding(padding: Int) {
        binding.widgettextpadding.text = padding.toString()

        //Update the UI
        binding.widgettextpaddingseekbar.progress = padding

        val pms = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        pms.gravity = currentWidget.widgetTextVerticalGravity!!.gravityValue or currentWidget.widgetTextHorizontalGravity!!.gravityValue
        pms.setMargins(AppUtils.dptopx(this, padding))
        binding.previewText.layoutParams = pms
        binding.previewText.invalidate()
        binding.previewText.requestLayout()

        //Update the size in data model
        currentWidget.textPadding = padding
    }


    fun setCurrentWidgetBackgroundColor(colorData: ColorData) {
        //Update the text color in the data model
        currentWidget.widgetBackgroundColor = colorData

        //Update the UI
        try {
            binding.widgetbackgroundcolorimage.imageTintList = ColorStateList.valueOf(
                Color.parseColor(
                    colorData.colorHexCode
                )
            )

            binding.createwidgetcardpreview.setCardBackgroundColor(
                ColorStateList.valueOf(
                    Color.parseColor(
                        colorData.colorHexCode
                    )
                )
            )

        } catch (e: IllegalArgumentException) {
            binding.widgetbackgroundcolorimage.imageTintList =
                ColorStateList.valueOf(Color.parseColor("FFFFFF"))

            binding.createwidgetcardpreview.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("FFFFFF")))

        }
        binding.widgetbackgroundcolorname.setText(colorData.colorName)

        //Show the ring if the color selected is white
        binding.widgetbackgroundcolorimage.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent))

        //Show the outline around preview card if the selected color is color primary
        binding.createwidgetcardpreview.cardElevation = 0f

        if (isDark) {
            if (colorData.colorHexCode == "#212121" || colorData.colorHexCode!!.substring(
                    3,
                    colorData.colorHexCode!!.length
                ).equals("212121")
            ) {
                binding.widgetbackgroundcolorimage.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.Grey))
            }

            if (colorData.colorHexCode == "#6200EE" || colorData.colorHexCode!!.substring(
                    3,
                    colorData.colorHexCode!!.length
                ).equals("6200EE")
            ) {
                binding.createwidgetcardpreview.cardElevation = AppUtils.dptopx(this, 8).toFloat()
            }
        } else {
            if (colorData.colorHexCode == "#FFFFFF" || colorData.colorHexCode!!.substring(
                    3,
                    colorData.colorHexCode!!.length
                ).equals("FFFFFF")
            ) {
                binding.widgetbackgroundcolorimage.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.Grey))
            }

            if (colorData.colorHexCode == "#3700B3" || colorData.colorHexCode!!.substring(
                    3,
                    colorData.colorHexCode!!.length
                ).equals("3700B3")
            ) {
                binding.createwidgetcardpreview.cardElevation = AppUtils.dptopx(this, 8).toFloat()
            }
        }


    }

    fun handleWidgetBackgroundSelection(selection: String) {
        if (selection == "solid") {
            //Update the UI

            //Animate the selector
            binding.bgrSelectionAnim.visibility = View.VISIBLE
            val listener = object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {
                }

                override fun onAnimationEnd(p0: Animator) {
                    binding.widgetbackgroundcolorselection.background = ContextCompat.getDrawable(
                        this@CreateWidgetActivity,
                        R.drawable.selection_background
                    )
                    binding.widgetbackgroundcolorselection.setTextColor(
                        ContextCompat.getColor(
                            this@CreateWidgetActivity,
                            R.color.colorPrimary
                        )
                    )

                    binding.bgrSelectionAnim.visibility = View.GONE
                }

                override fun onAnimationCancel(p0: Animator) {
                }

                override fun onAnimationRepeat(p0: Animator) {
                }

            }

            animateBackgroundSelector(binding. widgetbackgroundcolorselection.x, listener)





            binding.widgetbackgroundimageselection.setTextColor(ContextCompat.getColor(this, R.color.Grey))
            binding.widgetbackgroundimageselection.background =
                ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))

            binding.gradientSelection.setTextColor(ContextCompat.getColor(this, R.color.Grey))
            binding.gradientSelection.background =
                ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))



            binding.widgetbackgroundcolorcard.visibility = View.VISIBLE
            binding.widgetbackgroundimageselectionlayout.visibility = View.GONE
            binding.createWidgetPreviewMultiImageFlipper.visibility = GONE
            binding.createWidgetPreviewImageIndicator.visibility = View.GONE
            binding.widgetGradientBackgroundCard.visibility = View.GONE
            binding. previewGradientDisplay.visibility = View.GONE



            currentWidget.widgetBackGroundType = "color"

            //Set the current color to preview
            setCurrentWidgetBackgroundColor(currentWidget.widgetBackgroundColor!!)
        }
        if (selection == "image") {
            //Update the UI

            //Animate the selector
            binding.bgrSelectionAnim.visibility = View.VISIBLE
            val listener = object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {

                }

                override fun onAnimationEnd(p0: Animator) {

                    binding.widgetbackgroundimageselection.background = ContextCompat.getDrawable(
                        this@CreateWidgetActivity,
                        R.drawable.selection_background
                    )
                    binding. widgetbackgroundimageselection.setTextColor(
                        ContextCompat.getColor(
                            this@CreateWidgetActivity,
                            R.color.colorPrimary
                        )
                    )

                    binding. bgrSelectionAnim.visibility = View.GONE

                }

                override fun onAnimationCancel(p0: Animator) {
                }

                override fun onAnimationRepeat(p0: Animator) {
                }

            }
            animateBackgroundSelector(binding.widgetbackgroundimageselection.x, listener)


            binding.widgetbackgroundcolorselection.setTextColor(ContextCompat.getColor(this, R.color.Grey))
            binding.widgetbackgroundcolorselection.background =
                ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))

            binding.gradientSelection.setTextColor(ContextCompat.getColor(this, R.color.Grey))
            binding.gradientSelection.background =
                ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))

            binding.widgetbackgroundcolorcard.visibility = View.GONE
            binding.widgetbackgroundimageselectionlayout.visibility = View.VISIBLE
            binding.createWidgetPreviewMultiImageFlipper.visibility = View.GONE
            binding.widgetbackgroundimagecard.visibility = View.GONE
            binding. createWidgetPreviewImageIndicator.visibility = View.GONE
            binding. widgetGradientBackgroundCard.visibility = View.GONE
            binding. previewGradientDisplay.visibility = View.GONE


            //Update the data
            currentWidget.widgetBackGroundType = "image"

            //Set the current background image to preview
            if (!multiImageList.isEmpty()) {
                addImageBackground()
            }
        }
        if (selection == "gradient") {
            //update the UI

            //Animate the selector
            binding.bgrSelectionAnim.visibility = View.VISIBLE
            val listener = object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {

                }

                override fun onAnimationEnd(p0: Animator) {
                    binding.gradientSelection.background = ContextCompat.getDrawable(
                        this@CreateWidgetActivity,
                        R.drawable.selection_background
                    )
                    binding.gradientSelection.setTextColor(
                        ContextCompat.getColor(
                            this@CreateWidgetActivity,
                            R.color.colorPrimary
                        )
                    )

                    binding.bgrSelectionAnim.visibility = View.GONE
                }

                override fun onAnimationCancel(p0: Animator) {
                }

                override fun onAnimationRepeat(p0: Animator) {
                }

            }
            animateBackgroundSelector(binding.gradientSelection.x, listener)


            binding.widgetbackgroundcolorselection.setTextColor(ContextCompat.getColor(this, R.color.Grey))
            binding. widgetbackgroundcolorselection.background =
                ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))

            binding. widgetbackgroundimageselection.setTextColor(ContextCompat.getColor(this, R.color.Grey))
            binding.widgetbackgroundimageselection.background =
                ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))



            binding. widgetbackgroundcolorcard.visibility = View.GONE
            binding. widgetbackgroundimageselectionlayout.visibility = View.GONE
            binding.createWidgetPreviewMultiImageFlipper.visibility = View.GONE
            binding.widgetbackgroundimagecard.visibility = View.GONE
            binding. createWidgetPreviewImageIndicator.visibility = View.GONE
            binding.  widgetGradientBackgroundCard.visibility = View.VISIBLE
            binding. previewGradientDisplay.visibility = View.VISIBLE


            currentWidget.widgetBackGroundType = "gradient"

            if (currentWidget.widgetBackgroundGradient == null) {
                setCurrentBackgroundGradient(gradientsList[0])
            } else {
                setCurrentBackgroundGradient(currentWidget.widgetBackgroundGradient!!)
            }

        }
    }

    fun handleTextTypeSelection(selection: String) {
        when (selection) {
            "input" -> {
                //Update the UI

                //Animate the selector
                binding.txtBgrSelectionAnim.visibility = View.VISIBLE
                val listener = object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {
                    }

                    override fun onAnimationEnd(p0: Animator) {
                        binding.createWidgetTextTypeInput.background = ContextCompat.getDrawable(
                            this@CreateWidgetActivity,
                            R.drawable.selection_background
                        )
                        binding.createWidgetTextTypeInput.setTextColor(
                            ContextCompat.getColor(
                                this@CreateWidgetActivity,
                                R.color.colorPrimary
                            )
                        )

                        binding.txtBgrSelectionAnim.visibility = View.GONE
                    }

                    override fun onAnimationCancel(p0: Animator) {
                    }

                    override fun onAnimationRepeat(p0: Animator) {
                    }

                }

                animateTextTypeSelector(binding. createWidgetTextTypeInput.x, listener)


                binding.textTypeTimer.setTextColor(ContextCompat.getColor(this, R.color.Grey))
                binding.textTypeTimer.background =
                    ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))

                binding.textTypeClock.setTextColor(ContextCompat.getColor(this, R.color.Grey))
                binding.textTypeClock.background =
                    ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))


                binding.textTimerCard.visibility = View.GONE
                binding.widgettextinputcard.visibility = View.VISIBLE
                binding.textClockCard.visibility = View.GONE

                binding.previewText.visibility = View.VISIBLE
                binding.previewChrono.visibility = View.GONE
                binding.previewAnalogClock.visibility = View.GONE


                currentWidget.textType = "input"

                setCurrentText(currentWidget.widgetText!!)
            }

            "timer" -> {
                //Update the UI

                //Animate the selector
                binding.txtBgrSelectionAnim.visibility = View.VISIBLE
                val listener = object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {
                    }

                    override fun onAnimationEnd(p0: Animator) {
                        binding.textTypeTimer.background = ContextCompat.getDrawable(
                            this@CreateWidgetActivity,
                            R.drawable.selection_background
                        )
                        binding.textTypeTimer.setTextColor(
                            ContextCompat.getColor(
                                this@CreateWidgetActivity,
                                R.color.colorPrimary
                            )
                        )

                        binding.txtBgrSelectionAnim.visibility = View.GONE
                    }

                    override fun onAnimationCancel(p0: Animator) {
                    }

                    override fun onAnimationRepeat(p0: Animator) {
                    }

                }

                animateTextTypeSelector(binding. textTypeTimer.x, listener)





                binding.createWidgetTextTypeInput.setTextColor(ContextCompat.getColor(this, R.color.Grey))
                binding.createWidgetTextTypeInput.background =
                    ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))

                binding.textTypeClock.setTextColor(ContextCompat.getColor(this, R.color.Grey))
                binding.textTypeClock.background =
                    ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))


                binding.textTimerCard.visibility = View.VISIBLE
                binding.widgettextinputcard.visibility = View.GONE
                binding.textClockCard.visibility = View.GONE


                binding.previewText.visibility = View.GONE
                binding.previewChrono.visibility = View.VISIBLE
                binding.previewAnalogClock.visibility = View.GONE


                currentWidget.textType = "timer"

            }

            "clock" -> {
                //Update the UI

                //Animate the selector
                binding.txtBgrSelectionAnim.visibility = View.VISIBLE
                val listener = object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {
                    }

                    override fun onAnimationEnd(p0: Animator) {
                        binding.textTypeClock.background = ContextCompat.getDrawable(
                            this@CreateWidgetActivity,
                            R.drawable.selection_background
                        )
                        binding.textTypeClock.setTextColor(
                            ContextCompat.getColor(
                                this@CreateWidgetActivity,
                                R.color.colorPrimary
                            )
                        )

                        binding.txtBgrSelectionAnim.visibility = View.GONE
                    }

                    override fun onAnimationCancel(p0: Animator) {
                    }

                    override fun onAnimationRepeat(p0: Animator) {
                    }

                }

                animateTextTypeSelector(binding. textTypeClock.x, listener)


                binding.createWidgetTextTypeInput.setTextColor(ContextCompat.getColor(this, R.color.Grey))
                binding.createWidgetTextTypeInput.background =
                    ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))

                binding.textTypeTimer.setTextColor(ContextCompat.getColor(this, R.color.Grey))
                binding.textTypeTimer.background =
                    ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))

                binding.textTimerCard.visibility = View.GONE
                binding.widgettextinputcard.visibility = View.GONE
                binding.textClockCard.visibility = View.VISIBLE

                binding.previewText.visibility = View.GONE
                binding.previewChrono.visibility = View.GONE
                binding.previewAnalogClock.visibility = View.VISIBLE


                currentWidget.textType = "clock"

            }
        }
    }


    fun addImageBackground() {
        binding. widgetbackgroundimagecard.visibility = View.VISIBLE
        binding. createWidgetPreviewMultiImageFlipper.visibility = View.VISIBLE

        multiImageAdapter.notifyDataSetChanged()

        //Update the indicators
        if (multiImageList.size > 1) {
            binding.createWidgetPreviewImageIndicator.visibility = View.VISIBLE

            handleImageIndicators()
            indicateCurrentImage(binding.createWidgetPreviewMultiImageFlipper.displayedChild)

            val helperPreference = getSharedPreferences("widgetHelperPref", MODE_PRIVATE)
            val showMultiImageHelperText = helperPreference.getBoolean("showHelperText", true)
            if (showMultiImageHelperText) {
                helperPreference.edit().putBoolean("showHelperText", false).apply()
                binding.widgetMultiImageHelperText.visibility = View.VISIBLE
            }
        }


        currentWidget.widgetBackgroundImageUri = multiImageList.get(0)

        currentWidget.widgetMultiImageList = multiImageList



        if (multiImageList.size == 5) {
            binding.widgetbackgroundaddimagebutton.visibility = View.GONE
        } else {
            binding. widgetbackgroundaddimagebutton.visibility = View.VISIBLE
        }


    }

    fun removeImageItem() {
        binding.widgetbackgroundaddimagebutton.visibility = View.VISIBLE
        if (multiImageList.isEmpty()) {
            binding.createWidgetPreviewMultiImageFlipper.visibility = View.GONE
            binding.widgetbackgroundimagecard.visibility = View.GONE
            binding.createWidgetPreviewImageIndicator.visibility = View.GONE
        }


        //Update the indicators
        if (multiImageList.size > 1) {
            handleImageIndicators()
            indicateCurrentImage(0)
        } else {
            binding.createWidgetPreviewImageIndicator.visibility = View.GONE
        }

        //Change the click action from next image to default app action if the size is 1 or less
        if (currentWidget.widgetMultiImageList!!.size < 2) {
            if (currentWidget.widgetClickAction!!.actionType == AppUtils.ACTIONS_SIMPLE) {
                if (currentWidget.widgetClickAction!!.actionName == AppUtils.ACTION_NEXTIMAGE) {
                    //Set the default action data
                    val actionData = ActionData()
                    actionData.actionName = "Any Text Widget"
                    actionData.actionType = AppUtils.ACTIONS_APP
                    actionData.appPackageName = "com.rb.anytextwiget"
                    setClickAction(actionData)
                }
            }
        }

        currentWidget.widgetMultiImageList = multiImageList
        currentWidget.widgetBackgroundImageUri = null
    }

    fun getContentPath(rawData: Uri?): String {
        var path = "image path"
        try {
            val projection = arrayOf<String>(MediaStore.MediaColumns._ID)
            val cursor: Cursor =
                getContentResolver().query(rawData!!, projection, null, null, null)!!
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex(projection[0])
            val id: String = cursor.getString(index)
            cursor.close()
            path = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id)
                .toString()
        } catch (e: CursorIndexOutOfBoundsException) {
            e.printStackTrace()
        }
        return path
    }

    fun getBitmapWithContentPath(path: Uri?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val inputStream: InputStream = getContentResolver().openInputStream(path!!)!!
            val bitmapOptions: BitmapFactory.Options = BitmapFactory.Options()
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888
            val nonRotatedBitmap: Bitmap = BitmapFactory.decodeStream(
                inputStream,
                null,
                bitmapOptions
            )!!
            bitmap = checkOrientationAndGetBitmap(inputStream, nonRotatedBitmap)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun checkOrientationAndGetBitmap(inputStream: InputStream, bitmap: Bitmap): Bitmap? {
        var ei: ExifInterface? = null
        var rotatedBitmap: Bitmap? = null
        try {
            ei = ExifInterface(inputStream)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(bitmap, 270f)
                ExifInterface.ORIENTATION_NORMAL -> {
                    rotatedBitmap = bitmap
                    return rotatedBitmap
                }
                else -> {
                    rotatedBitmap = bitmap
                    return rotatedBitmap
                }
            }
            bitmap.recycle()
            return rotatedBitmap
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return rotatedBitmap
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun askPermission() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission required!")
        builder.setMessage("Storage permission is required for adding image")
        builder.setPositiveButton("GIVE", DialogInterface.OnClickListener { dialogInterface, i ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                   this, arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.ACCESS_MEDIA_LOCATION
                    ), 46
                )
            }
            else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 46
                )
            }

        })

        builder.show()
    }

    fun setCurrentRoundCorners(roundCorners: Boolean) {
        //Update the value in the data model
        currentWidget.widgetRoundCorners = roundCorners

        //Update the UI
        if (roundCorners) {
            binding.createwidgetcardpreview.radius = AppUtils.dptopx(this, 30).toFloat()
        } else {
            binding.createwidgetcardpreview.radius = 0f
        }
    }

    fun setRoundCorners() {
        val sharedPreferences = getSharedPreferences("apppref", MODE_PRIVATE)
        val roundCorners = sharedPreferences.getBoolean("roundcorners", true)

        if (roundCorners) {
            //Set the radius
            binding.widgettextinputcard.radius = AppUtils.dptopx(this, 20).toFloat()
            binding.widgettextcolorcard.radius = AppUtils.dptopx(this, 30).toFloat()
            binding.widgettextsizecard.radius = AppUtils.dptopx(this, 30).toFloat()
            binding.widgettextstylecard.radius = AppUtils.dptopx(this, 30).toFloat()
            binding.widgetbackgroundcolorcard.radius = AppUtils.dptopx(this, 30).toFloat()
            binding.widgetbackgroundimagecard.radius = AppUtils.dptopx(this, 30).toFloat()
            binding.widgetroundcornerscard.radius = AppUtils.dptopx(this, 30).toFloat()
            binding.widgetClickActionCard.radius = AppUtils.dptopx(this, 30).toFloat()
            binding. widgetTextGravityCard.radius = AppUtils.dptopx(this, 30).toFloat()
            binding.widgetOutlineCard.radius = AppUtils.dptopx(this, 30).toFloat()
            binding.widgetGradientBackgroundCard.radius = AppUtils.dptopx(this, 30).toFloat()
            binding. widgetBackgroundSelectionCard.radius = AppUtils.dptopx(this, 15).toFloat()


            //Set the margins
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(
                AppUtils.dptopx(this, 10), AppUtils.dptopx(this, 5), AppUtils.dptopx(
                    this,
                    10
                ), AppUtils.dptopx(this, 10)
            )

            val layoutParams2 = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams2.setMargins(
                AppUtils.dptopx(this, 10), AppUtils.dptopx(this, 5), AppUtils.dptopx(
                    this,
                    10
                ), AppUtils.dptopx(this, 10)
            )

            binding. widgettextinputcard.layoutParams = layoutParams
            binding. widgettextcolorcard.layoutParams = layoutParams
            binding. widgettextsizecard.layoutParams = layoutParams
            binding.widgettextstylecard.layoutParams = layoutParams
            binding.widgetbackgroundcolorcard.layoutParams = layoutParams2
            binding. widgetroundcornerscard.layoutParams = layoutParams
            binding.widgetTextGravityCard.layoutParams = layoutParams
            binding. widgetClickActionCard.layoutParams = layoutParams
            binding. widgetOutlineCard.layoutParams = layoutParams
            binding. widgetGradientBackgroundCard.layoutParams = layoutParams2
            binding.widgetBackgroundSelectionCard.layoutParams = layoutParams


        } else {
            //Set the radius
            binding.widgettextinputcard.radius = 0f
            binding.widgettextcolorcard.radius = 0f
            binding. widgettextsizecard.radius = 0f
            binding. widgettextstylecard.radius = 0f
            binding. widgetbackgroundcolorcard.radius = 0f
            binding. widgetbackgroundimagecard.radius = 0f
            binding. widgetroundcornerscard.radius = 0f
            binding. widgetTextGravityCard.radius = 0f
            binding. widgetClickActionCard.radius = 0f
            binding. widgetOutlineCard.radius = 0f
            binding.widgetGradientBackgroundCard.radius = 0f
            binding.widgetBackgroundSelectionCard.radius = 0f


            //Set the margins
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, AppUtils.dptopx(this, 5), 0, AppUtils.dptopx(this, 10))

            val layoutParams2 = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams2.setMargins(0, AppUtils.dptopx(this, 5), 0, AppUtils.dptopx(this, 10))

            binding.widgettextinputcard.layoutParams = layoutParams
            binding.widgettextcolorcard.layoutParams = layoutParams
            binding. widgettextsizecard.layoutParams = layoutParams
            binding. widgettextstylecard.layoutParams = layoutParams
            binding.widgetbackgroundcolorcard.layoutParams = layoutParams2
            binding. widgetroundcornerscard.layoutParams = layoutParams
            binding.widgetTextGravityCard.layoutParams = layoutParams
            binding.widgetClickActionCard.layoutParams = layoutParams
            binding. widgetOutlineCard.layoutParams = layoutParams
            binding. widgetBackgroundSelectionCard.layoutParams = layoutParams
            binding.  widgetGradientBackgroundCard.layoutParams = layoutParams2

        }
    }

    fun setVerticalGravity(gravityData: TextGravityData) {
        //Update the UI
        binding.widgetTextVerticalGravityName.text = gravityData.gravityName

        if (gravityData.gravityName == "Top") {
            binding. widgetTextVerticalGravityImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_vertical_align_bottom_50
                )
            )
            binding.widgetTextVerticalGravityImage.rotation = 180f
        }
        if (gravityData.gravityName == "Center") {
            binding.widgetTextVerticalGravityImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_vertical_align_center_50
                )
            )
            binding.widgetTextVerticalGravityImage.rotation = 0f
        }
        if (gravityData.gravityName == "Bottom") {
            binding. widgetTextVerticalGravityImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_vertical_align_bottom_50
                )
            )
            binding.widgetTextVerticalGravityImage.rotation = 0f
        }


        //Update the data
        currentWidget.widgetTextVerticalGravity = gravityData

        /*val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.setMargins(
            AppUtils.dptopx(this, 15),
            AppUtils.dptopx(this, 15),
            AppUtils.dptopx(this, 15),
            AppUtils.dptopx(this, 15)
        )
        layoutParams.gravity =
            (gravityData.gravityValue or currentWidget.widgetTextHorizontalGravity!!.gravityValue)
        previewText.layoutParams = layoutParams
*/
        binding. previewText.gravity =
            (gravityData.gravityValue or currentWidget.widgetTextHorizontalGravity!!.gravityValue)

    }

    fun setHorizontalGravity(gravityData: TextGravityData) {
        //Update the UI
        binding.widgetTextHorizontalGravityName.text = gravityData.gravityName

        if (gravityData.gravityName == "Start") {
            binding. widgetTextHorizontalGravityImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_vertical_align_bottom_50
                )
            )
            binding.widgetTextHorizontalGravityImage.rotation = 90f
        }
        if (gravityData.gravityName == "Center") {
            binding. widgetTextHorizontalGravityImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_vertical_align_center_50
                )
            )
            binding.widgetTextHorizontalGravityImage.rotation = 90f
        }
        if (gravityData.gravityName == "End") {
            binding. widgetTextHorizontalGravityImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_vertical_align_bottom_50
                )
            )
            binding. widgetTextHorizontalGravityImage.rotation = -90f
        }


        //Update the data
        currentWidget.widgetTextHorizontalGravity = gravityData

        /*    val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams.setMargins(
                AppUtils.dptopx(this, 15),
                AppUtils.dptopx(this, 15),
                AppUtils.dptopx(this, 15),
                AppUtils.dptopx(this, 15)
            )
            layoutParams.gravity =
                (currentWidget.widgetTextVerticalGravity!!.gravityValue or gravityData.gravityValue)
            previewText.layoutParams = layoutParams*/

        binding.previewText.gravity =
            (currentWidget.widgetTextVerticalGravity!!.gravityValue or gravityData.gravityValue)
    }

    fun setClickAction(actionData: ActionData) {
        //Update the UI
        if (actionData.actionType == AppUtils.ACTIONS_SIMPLE) {
            when (actionData.actionName) {
                AppUtils.ACTION_WIFI -> {
                    binding.widgetClickActionImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_round_wifi_50
                        )
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        binding.widgetClickActionName.text = "Opens Wi-Fi panel"
                    } else {
                        binding.widgetClickActionName.text = "Toggles Wi-Fi on/off"
                    }
                }

                AppUtils.ACTION_DONOTDISTURB -> {
                    binding. widgetClickActionImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_round_do_not_disturb_on_50
                        )
                    )
                    binding.widgetClickActionName.text = "Toggles do not disturb on/off"

                    //Ask for Dnd access
                    val notificationManager =
                        applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!notificationManager.isNotificationPolicyAccessGranted) {
                            askDndAccess()
                        }
                    }
                }

                AppUtils.ACTION_FLASHLIGHT -> {
                    binding. widgetClickActionImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_round_flashlight_on_50
                        )
                    )
                    binding.widgetClickActionName.text = "Toggles flashlight on/off"
                }

                AppUtils.ACTION_BLUETOOTH -> {
                    binding.widgetClickActionImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_round_bluetooth_50
                        )
                    )
                    binding. widgetClickActionName.text = "Toggles bluetooth on/off"
                }

                AppUtils.ACTION_NEXTIMAGE -> {
                    binding.  widgetClickActionImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_round_skip_next_50
                        )
                    )
                    binding.widgetClickActionName.text = "Shows the next image"
                }

                AppUtils.ACTION_OPEN_LINK -> {
                    binding. widgetClickActionImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_round_open_in_browser_35
                        )
                    )
                    binding. widgetClickActionName.text = "Opens '${actionData.actionExtra}'"
                }

                AppUtils.ACTION_NOTHING -> {
                    binding. widgetClickActionImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_baseline_do_not_disturb_alt_50
                        )
                    )
                    binding.widgetClickActionName.text = "Does nothing when clicked"
                }
            }
        } else {
            if (packageManager.getLaunchIntentForPackage(actionData.appPackageName) != null) {
                val appInfo = packageManager.getApplicationInfo(
                    actionData.appPackageName,
                    PackageManager.GET_META_DATA
                )
                val appName = packageManager.getApplicationLabel(appInfo)
                val appIcon = packageManager.getApplicationIcon(actionData.appPackageName)

                binding.widgetClickActionImage.setImageDrawable(appIcon)
                binding.widgetClickActionName.text = "Opens $appName"
            } else {
                Toast.makeText(
                    this,
                    "${actionData.actionName} is not available for click",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        //Handle action image tint
        handleActionImageTint(actionData)

        //Update the data
        currentWidget.widgetClickAction = actionData
    }

    fun noImageAddedDialog(type: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No image added!")
        builder.setMessage("Do you want to continue without image? This will set the background to solid")
        builder.setNegativeButton("no") { dialog, which -> //Do nothing
        }
        builder.setPositiveButton("yes", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if (type.equals("create")) {
                    //Change the background type to color and save
                    handleWidgetBackgroundSelection("solid")
                    saveNewWidget(currentWidget)
                }
                if (type.equals("edit")) {
                    //Change the background type to color and save
                    handleWidgetBackgroundSelection("solid")
                    saveEditedWidget(currentWidget)
                }
            }
        })
        builder.show()
    }

    fun showSetTextSizeDialog(currentSize: Int) {
        val view = SetTextSizeLayoutBinding.inflate( LayoutInflater.from(applicationContext),
            null,
            false
        )
        view.sizeinput.setText(currentSize.toString())

        view.sizeinput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.toString().isEmpty()) {
                    val size = s.toString().trim().toInt()
                    if (size > 100) {
                        view.sizeinput.setText(100.toString())
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Set text size")
        builder.setPositiveButton("Confirm", DialogInterface.OnClickListener { dialogInterface, i ->
            var textSize = 0
            if (!view.sizeinput.text.toString().trim().isEmpty()) {
                textSize = view.sizeinput.text.toString().trim().toInt()
            } else {
                textSize = 0
            }
            setCurrentTextSize(textSize)
        })
        builder.setView(view.root)
        builder.show()
    }

    fun handleImageIndicators() {
        if (multiImageList.size == 2) {
            binding.indicatorTwo.visibility = View.VISIBLE
            binding.indicatorThree.visibility = View.GONE
            binding.indicatorFour.visibility = View.GONE
            binding.indicatorFive.visibility = View.GONE
        }
        if (multiImageList.size == 3) {
            binding.indicatorTwo.visibility = View.VISIBLE
            binding. indicatorThree.visibility = View.VISIBLE
            binding. indicatorFour.visibility = View.GONE
            binding.indicatorFive.visibility = View.GONE
        }
        if (multiImageList.size == 4) {
            binding. indicatorTwo.visibility = View.VISIBLE
            binding.indicatorThree.visibility = View.VISIBLE
            binding.indicatorFour.visibility = View.VISIBLE
            binding. indicatorFive.visibility = View.GONE
        }
        if (multiImageList.size == 5) {
            binding.indicatorTwo.visibility = View.VISIBLE
            binding. indicatorThree.visibility = View.VISIBLE
            binding.  indicatorFour.visibility = View.VISIBLE
            binding.  indicatorFive.visibility = View.VISIBLE
        }
    }

    fun indicateCurrentImage(position: Int) {
        if (position == 0) {
            binding.indicatorOne.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            binding. indicatorTwo.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding.  indicatorThree.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding. indicatorFour.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding. indicatorFive.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))


        }

        if (position == 1) {
            binding.indicatorOne.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding.indicatorTwo.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            binding.indicatorThree.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding.indicatorFour.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding.indicatorFive.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
        }

        if (position == 2) {
            binding. indicatorOne.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding.indicatorTwo.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding. indicatorThree.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            binding. indicatorFour.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding. indicatorFive.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
        }

        if (position == 3) {
            binding.indicatorOne.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding.indicatorTwo.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding.indicatorThree.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding.indicatorFour.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            binding.indicatorFive.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
        }
        if (position == 4) {
            binding.indicatorOne.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding.indicatorTwo.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding. indicatorThree.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding. indicatorFour.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey2))
            binding. indicatorFive.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
        }
    }

    fun setCurrentBackgroundGradient(gradientData: GradientData) {
        //Update the UI
        CoroutineScope(Dispatchers.IO).launch {

            try {
                val sourceName = "no_corners_" + gradientData.sourceName

                val gradient = resources.getIdentifier(sourceName, "drawable", packageName)
                val drawable = ContextCompat.getDrawable(this@CreateWidgetActivity, gradient)

                withContext(Dispatchers.Main) {
                    Glide.with(this@CreateWidgetActivity).load(drawable).circleCrop()
                        .into(binding.gradientBackgroundImage)
                    binding. previewGradientDisplay.setImageDrawable(drawable)
                }
            } catch (e: Resources.NotFoundException) {
                e.printStackTrace()
            }

        }

        binding.gradientBackgroundName.text = gradientData.name

        //Update the data
        currentWidget.widgetBackgroundGradient = gradientData

    }

    fun darkMode(isNight: Boolean) {
        if (isNight) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
            binding.createwidgettoolbar.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryDark
                )
            )
            binding. createwidgetappbarlayout.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryDark
                )
            )

            binding.createwidgetparent.setBackgroundColor(ContextCompat.getColor(this, R.color.Black))
            binding.widgettextinputcard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkGrey
                )
            )
            binding.widgettextcolorcard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkGrey
                )
            )
            binding.createWidgetInputText.setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )
            )
            binding.widgettextsizecard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkGrey
                )
            )
            binding.widgettextstylecard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkGrey
                )
            )
            binding. widgetbackgroundcolorcard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkGrey
                )
            )
            binding.widgetroundcornerscard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkGrey
                )
            )
            binding. widgetTextGravityCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkGrey
                )
            )
            binding.widgetClickActionCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkGrey
                )
            )

            binding.widgetOutlineCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkGrey
                )
            )

            binding. widgetGradientBackgroundCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkGrey
                )
            )

            binding.widgetBackgroundSelectionCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkGrey
                )
            )

            binding.textShadowCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkGrey
                )
            )

            binding. widgetbackgroundaddimagebutton.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.green4Dark
                )
            )

            binding.widgetSelectColorText.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.widgetSetTextSizeText.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.widgetSelectFontText.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.widgetBackgroundColorText.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding. widgetSetVerticalGravityText.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.widgetSetHorizontalGravityText.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.widgetClickActionText.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding. widgetroundcornersswitch.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding. widgetOutlineSwitch.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding. widgetOutlineWidthText.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.widgetSelectOutlineColorText.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding. widgetOutlineWidthIcon.setTextColor(ContextCompat.getColor(this, R.color.purpleLight))
            binding. gradientBackgroundText.setTextColor(ContextCompat.getColor(this, R.color.white))

            binding. textShadowSwitch.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.textShadowRadiusText.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding. textShadowHDirText.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding. textShadowVDirText.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.textShadowSelectColorText.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding. textShadowIcon.setTextColor(ContextCompat.getColor(this, R.color.purpleLight))
            binding. textShadowRadiusIcon.setTextColor(ContextCompat.getColor(this, R.color.purpleLight))
            binding. textShadowHDirIcon.setTextColor(ContextCompat.getColor(this, R.color.purpleLight))
            binding.  textShadowVDirIcon.setTextColor(ContextCompat.getColor(this, R.color.purpleLight))







            binding. widgetRoundCornersImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purpleLight))
            binding. widgetSetTextSizeImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purpleLight))
            binding. widgettextfonticon.setTextColor(ContextCompat.getColor(this, R.color.purpleLight))
            binding. widgetTextVerticalGravityImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purpleLight))
            binding. widgetTextHorizontalGravityImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purpleLight))
            binding. widgetClickActionImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purpleLight))

            binding.widgetOutlineIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purpleLight))
            binding.widgetOutlineWidthIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purpleLight))



            binding.widgetTextGravityDiv.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey3))

            binding. widgetOutlineDiv.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey3))
            binding. widgetOutlineDiv2.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey3))

            binding. textShadowDiv.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey3))
            binding. textShadowDiv2.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey3))
            binding. textShadowDiv3.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey3))
            binding.   textShadowDiv4.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey3))


            binding.createWidgetContentParent.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryDark
                )
            )
            binding.createWidgetInputLayoutParent.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.Black
                )
            )
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
            binding.createwidgettoolbar.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )
            binding. createwidgetappbarlayout.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )


            binding. createwidgetparent.setBackgroundColor(ContextCompat.getColor(this, R.color.LightGrey3))
            binding.widgettextinputcard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.  createWidgetInputText.setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.Black
                    )
                )
            )
            binding. gradientBackgroundText.setTextColor(ContextCompat.getColor(this, R.color.Black))


            binding.  widgettextcolorcard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding. widgettextsizecard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding. widgettextstylecard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.  widgetbackgroundcolorcard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding. widgetroundcornerscard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding.  widgetTextGravityCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding. widgetClickActionCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )

            binding.widgetOutlineCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )

            binding.widgetGradientBackgroundCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )

            binding. widgetBackgroundSelectionCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding. widgetbackgroundaddimagebutton.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.green4
                )
            )
            binding.  widgetSelectColorText.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding. widgetSetTextSizeText.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.widgetSelectFontText.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.widgetBackgroundColorText.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.widgetSetVerticalGravityText.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.widgetSetHorizontalGravityText.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.widgetClickActionText.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.widgetroundcornersswitch.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding. widgetOutlineSwitch.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding. widgetOutlineWidthText.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.  widgetSelectOutlineColorText.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding. widgetOutlineWidthIcon.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))


            binding. widgetRoundCornersImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
            binding.  widgetSetTextSizeImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
            binding.  widgettextfonticon.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            binding. widgetTextVerticalGravityImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
            binding.widgetTextHorizontalGravityImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
            binding. widgetClickActionImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))

            binding.widgetOutlineIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
            binding. widgetOutlineWidthIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))


            binding.widgetTextGravityDiv.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.LightGrey3
                )
            )



            binding.  widgetOutlineDiv.setBackgroundColor(ContextCompat.getColor(this, R.color.LightGrey3))
            binding.widgetOutlineDiv2.setBackgroundColor(ContextCompat.getColor(this, R.color.LightGrey3))


            binding.createWidgetContentParent.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )
            binding.createWidgetInputLayoutParent.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.LightGrey3
                )
            )
        }
    }

    fun adjustTheme(appTheme: String) {
        if (appTheme == AppUtils.LIGHT) {
            isDark = false
            darkMode(false)
        }
        if (appTheme == AppUtils.DARK) {
            isDark = true
            darkMode(true)
        }
        if (appTheme == AppUtils.FOLLOW_SYSTEM) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    darkMode(true)
                    isDark = true
                }

                Configuration.UI_MODE_NIGHT_NO -> {
                    darkMode(false)
                    isDark = false
                }
            }
        }
    }

    fun toggleWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            panelIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(panelIntent)
        } else {
            val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            if (wifiManager.isWifiEnabled) {
                wifiManager.setWifiEnabled(false)
            } else {
                wifiManager.setWifiEnabled(true)
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
    fun toggleDnd() {
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Check if the notification policy access has been granted for the app.
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            askDndAccess()
        } else {
            if (notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_NONE) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
            } else {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun toggleFlashlight() {
        val cameraManager = applicationContext.getSystemService(CAMERA_SERVICE) as CameraManager

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

    fun toggleNextImage() {
        binding.createWidgetPreviewMultiImageFlipper.showNext()
        binding. widgetBackgroundImageFlipper.showNext()
        indicateCurrentImage( binding.createWidgetPreviewMultiImageFlipper.displayedChild)
        binding.widgetMultiImageHelperText.visibility = View.GONE

    }

    fun openLink() {
        if (currentWidget.widgetClickAction!!.actionExtra.trim().isNotEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(currentWidget.widgetClickAction!!.actionExtra)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Unable to open this link. Please enter a proper one",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }

        }

    }

    fun openApp() {
        val intent =
            packageManager.getLaunchIntentForPackage(currentWidget.widgetClickAction!!.appPackageName)
        if (intent != null) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                "${currentWidget.widgetClickAction!!.actionName} is not available for click",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun handleClick() {
        if (currentWidget.widgetClickAction!!.actionType == AppUtils.ACTIONS_SIMPLE) {
            when (currentWidget.widgetClickAction!!.actionName) {
                AppUtils.ACTION_WIFI -> {
                    toggleWifi()
                }

                AppUtils.ACTION_BLUETOOTH -> {
                    toggleBlueTooth()
                }

                AppUtils.ACTION_DONOTDISTURB -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        toggleDnd()
                    }
                }

                AppUtils.ACTION_FLASHLIGHT -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        toggleFlashlight()
                    }
                }

                AppUtils.ACTION_NEXTIMAGE -> {
                    toggleNextImage()
                }

                AppUtils.ACTION_OPEN_LINK -> {
                    openLink()
                }

            }
        } else {
            if (currentWidget.widgetClickAction!!.appPackageName == "com.rb.anytextwiget") {
                Toast.makeText(this, "Opens this app", Toast.LENGTH_SHORT).show()
            } else {
                openApp()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun askDndAccess() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission required...")
        builder.setMessage("App required Do Not Disturb access in order for this toggle action to work")
        builder.setPositiveButton("Give", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivity(intent)
            }

        })
        builder.show()
    }

    fun handleActionImageTint(actionData: ActionData) {
        if (actionData.actionType == AppUtils.ACTIONS_SIMPLE) {
            if (isDark) {
                binding.widgetClickActionImage.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purpleLight))
            } else {
                binding. widgetClickActionImage.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
            }
        } else {
            binding.widgetClickActionImage.imageTintList = null
        }
    }

    fun sortApps() {
        Collections.sort(appsList, Comparator<String> { o1, o2 ->
            val appInfo1 = packageManager.getApplicationInfo(o1, PackageManager.GET_META_DATA)
            val appName1 = packageManager.getApplicationLabel(appInfo1).toString()

            val appInfo2 = packageManager.getApplicationInfo(o2, PackageManager.GET_META_DATA)
            val appName2 = packageManager.getApplicationLabel(appInfo2).toString()
            appName1.compareTo(appName2)
        })
    }

    fun setOutline(isEnabled: Boolean) {
        if (isEnabled) {
            //Update the UI
            binding.  createwidgetcardpreview.strokeWidth =
                AppUtils.dptopx(this, currentWidget.widgetOutlineWidth)
            binding.createwidgetcardpreview.requestLayout()



            binding. widgetOutlineColorInfo.visibility = View.VISIBLE
            binding. widgetOutlineColorImage.visibility = View.VISIBLE
            binding.  widgetOutlineDiv2.visibility = View.VISIBLE
            binding.  widgetOutlineWidthText.visibility = View.VISIBLE
            binding.  widgetOutlineWidthSeekBar.visibility = View.VISIBLE
            binding.   widgetOutlineDiv.visibility = View.VISIBLE


            //Update the data
            currentWidget.outlineEnabled = isEnabled
            binding.  createwidgetcardpreview.requestLayout()

        } else {
            //Update the UI
            binding.  createwidgetcardpreview.strokeWidth = 0
            binding.  createwidgetcardpreview.requestLayout()

            binding.    widgetOutlineColorInfo.visibility = View.GONE
            binding.    widgetOutlineColorImage.visibility = View.GONE
            binding.     widgetOutlineDiv2.visibility = View.GONE
            binding.   widgetOutlineWidthIcon.visibility = View.GONE
            binding.     widgetOutlineWidthText.visibility = View.GONE
            binding.     widgetOutlineWidthSeekBar.visibility = View.GONE
            binding.   widgetOutlineDiv.visibility = View.GONE

            //Update the data
            currentWidget.outlineEnabled = isEnabled
        }
    }

    fun setOutlineColor(colorData: ColorData) {
        //Update the UI
        try {
            binding.  createwidgetcardpreview.setStrokeColor(ColorStateList.valueOf(Color.parseColor(colorData.colorHexCode)))

            binding.  widgetOutlineColorImage.imageTintList =
                ColorStateList.valueOf(Color.parseColor(colorData.colorHexCode))
        } catch (e: IllegalArgumentException) {
            binding.    createwidgetcardpreview.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#000000")))
            binding.    widgetOutlineColorImage.imageTintList =
                ColorStateList.valueOf(Color.parseColor("#000000"))


            e.printStackTrace()
        }

        binding.  widgetOutlineColorName.text = colorData.colorName

        //Show ring if the color is same as the card's
        binding.    widgetOutlineColorImage.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent))
        if (isDark) {
            if (colorData.colorHexCode == "#212121" || colorData.colorHexCode!!.substring(
                    3,
                    colorData.colorHexCode!!.length
                ).equals("212121")
            ) {
                binding. widgetOutlineColorImage.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.Grey))
            }
        } else {
            if (colorData.colorHexCode == "#FFFFFF" || colorData.colorHexCode!!.substring(
                    3,
                    colorData.colorHexCode!!.length
                ).equals("FFFFFF")
            ) {
                binding. widgetOutlineColorImage.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.Grey))
            }
        }


        //Update the data
        currentWidget.widgetOutlineColor = colorData
    }

    fun setOutlineWidth(width: Int) {
        //Update the data
        currentWidget.widgetOutlineWidth = width

        //Update the UI
        binding.  widgetOutlineWidthIcon.text = "$width"
        binding.  widgetOutlineWidthSeekBar.progress = width

        if (currentWidget.outlineEnabled) {
            binding.   createwidgetcardpreview.strokeWidth = AppUtils.dptopx(this, width)
            binding.    createwidgetcardpreview.requestLayout()
        }
    }

    fun animateBackgroundSelector(requiredPosX: Float, listener: Animator.AnimatorListener) {
        //Set the proper width so that it will blend well with the background
        binding.    bgrSelectionAnim.width = binding. gradientSelection.width

        //Get the current position x
        val currentPosX = binding. bgrSelectionAnim.x


        //Start the animation
        val anim = ValueAnimator.ofFloat(currentPosX, requiredPosX)
        anim.addUpdateListener {
            val value = it.animatedValue
            binding. bgrSelectionAnim.x = value as Float

        }
        anim.addListener(listener)
        anim.duration = 400
        anim.interpolator = DecelerateInterpolator()
        anim.start()


    }

    fun animateTextTypeSelector(requiredPosX: Float, listener: Animator.AnimatorListener) {
        //Set the proper width so that it will blend well with the background
        binding.txtBgrSelectionAnim.width = binding.createWidgetTextTypeInput.width

        //Get the current position x
        val currentPosX = binding. txtBgrSelectionAnim.x


        //Start the animation
        val anim = ValueAnimator.ofFloat(currentPosX, requiredPosX)
        anim.addUpdateListener {
            val value = it.animatedValue
            binding. txtBgrSelectionAnim.x = value as Float

        }
        anim.addListener(listener)
        anim.duration = 400
        anim.interpolator = DecelerateInterpolator()
        anim.start()
    }


    fun setTextShadow(isEnabled: Boolean) {
        if (isEnabled) {
            //Update the UI
            binding. textShadowHDirIcon.visibility = View.VISIBLE
            binding. textShadowHDirText.visibility = View.VISIBLE
            binding. textShadowHDirSeekBar.visibility = View.VISIBLE

            binding. textShadowVDirIcon.visibility = View.VISIBLE
            binding. textShadowVDirText.visibility = View.VISIBLE
            binding. textShadowVDirSeekBar.visibility = View.VISIBLE

            binding. textShadowRadiusIcon.visibility = View.VISIBLE
            binding. textShadowRadiusText.visibility = View.VISIBLE
            binding. textShadowRadiusSeekBar.visibility = View.VISIBLE

            binding.  textShadowColorImg.visibility = View.VISIBLE
            binding.  textShadowColorInfo.visibility = View.VISIBLE

            binding. textShadowDiv.visibility = View.VISIBLE
            binding.  textShadowDiv2.visibility = View.VISIBLE
            binding.  textShadowDiv3.visibility = View.VISIBLE
            binding.  textShadowDiv4.visibility = View.VISIBLE


            binding.   previewText.setShadowLayer(
                AppUtils.dptopx(this, currentWidget.textShadowData!!.shadowRadius).toFloat(),
                AppUtils.dptopx(this, currentWidget.textShadowData!!.horizontalDir).toFloat(),
                AppUtils.dptopx(this, currentWidget.textShadowData!!.verticalDir).toFloat(),
                Color.parseColor(currentWidget.textShadowData!!.shadowColor!!.colorHexCode)
            )
            binding. previewText.requestLayout()


            //Update the data
            currentWidget.textShadowEnabled = true
        } else {
            //Update the UI
            binding. textShadowHDirIcon.visibility = View.GONE
            binding. textShadowHDirText.visibility = View.GONE
            binding. textShadowHDirSeekBar.visibility = View.GONE

            binding. textShadowVDirIcon.visibility = View.GONE
            binding. textShadowVDirText.visibility = View.GONE
            binding.  textShadowVDirSeekBar.visibility = View.GONE


            binding. textShadowRadiusIcon.visibility = View.GONE
            binding.  textShadowRadiusText.visibility = View.GONE
            binding.   textShadowRadiusSeekBar.visibility = View.GONE


            binding. textShadowColorImg.visibility = View.GONE
            binding.  textShadowColorInfo.visibility = View.GONE

            binding.  textShadowDiv.visibility = View.GONE
            binding.  textShadowDiv2.visibility = View.GONE
            binding.  textShadowDiv3.visibility = View.GONE
            binding.  textShadowDiv4.visibility = View.GONE


            binding.  previewText.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
            binding.  previewText.requestLayout()

            //Update the data
            currentWidget.textShadowEnabled = false
        }
    }

    fun setTextShadowRadius(radius: Int) {
        //Update the UI
        if (currentWidget.textShadowEnabled) {
            binding.  textShadowRadiusIcon.text = "$radius"

            binding. previewText.setShadowLayer(
                AppUtils.dptopx(this, radius).toFloat(),
                AppUtils.dptopx(this, currentWidget.textShadowData!!.horizontalDir).toFloat(),
                AppUtils.dptopx(this, currentWidget.textShadowData!!.verticalDir).toFloat(),
                Color.parseColor(currentWidget.textShadowData!!.shadowColor!!.colorHexCode)
            )

            binding.  previewText.requestLayout()
        }


        //Update the data
        currentWidget.textShadowData!!.shadowRadius = radius
    }

    fun setTextShadowHDir(value: Int) {
        //Update the UI
        if (currentWidget.textShadowEnabled) {
            binding.  textShadowHDirIcon.text = "$value"
            binding.  previewText.setShadowLayer(
                AppUtils.dptopx(this, currentWidget.textShadowData!!.shadowRadius).toFloat(),
                AppUtils.dptopx(this, value).toFloat(),
                AppUtils.dptopx(this, currentWidget.textShadowData!!.verticalDir).toFloat(),
                Color.parseColor(currentWidget.textShadowData!!.shadowColor!!.colorHexCode)
            )

            binding. previewText.requestLayout()
        }


        //Update the data
        currentWidget.textShadowData!!.horizontalDir = value
    }

    fun setTextShadowVDir(value: Int) {
        //Update the UI
        if (currentWidget.textShadowEnabled) {
            binding. textShadowVDirIcon.text = "$value"
            binding.   previewText.setShadowLayer(
                AppUtils.dptopx(this, currentWidget.textShadowData!!.shadowRadius).toFloat(),
                AppUtils.dptopx(this, currentWidget.textShadowData!!.horizontalDir).toFloat(),
                AppUtils.dptopx(this, value).toFloat(),
                Color.parseColor(currentWidget.textShadowData!!.shadowColor!!.colorHexCode)
            )

            binding. previewText.requestLayout()
        }


        //Update the data
        currentWidget.textShadowData!!.verticalDir = value
    }

    fun setTextShadowColor(colorData: ColorData) {
        //Update the UI
        if (currentWidget.textShadowEnabled) {
            try {
                binding. textShadowColorImg.imageTintList =
                    ColorStateList.valueOf(Color.parseColor(colorData.colorHexCode))

                binding. previewText.setShadowLayer(
                    AppUtils.dptopx(this, currentWidget.textShadowData!!.shadowRadius).toFloat(),
                    AppUtils.dptopx(this, currentWidget.textShadowData!!.horizontalDir).toFloat(),
                    AppUtils.dptopx(this, currentWidget.textShadowData!!.verticalDir).toFloat(),
                    Color.parseColor(colorData.colorHexCode)
                )
            } catch (e: IllegalArgumentException) {
                binding.  textShadowColorImg.imageTintList =
                    ColorStateList.valueOf(Color.parseColor("#000000"))

                binding.  previewText.setShadowLayer(
                    AppUtils.dptopx(this, currentWidget.textShadowData!!.shadowRadius).toFloat(),
                    AppUtils.dptopx(this, currentWidget.textShadowData!!.horizontalDir).toFloat(),
                    AppUtils.dptopx(this, currentWidget.textShadowData!!.verticalDir).toFloat(),
                    Color.parseColor("#000000")
                )
                e.printStackTrace()
            }
        }

        binding.  textShadowColorName.text = colorData.colorName

        //Update the data
        currentWidget.textShadowData!!.shadowColor = colorData

    }

    fun setTextTimerDate(day: Int, month: Int, year: Int) {
        //Update the UI.
        val dateFormat = SimpleDateFormat.getDateInstance()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.HOUR_OF_DAY, currentWidget.textTypeTimerHour)
        calendar.set(Calendar.MINUTE, currentWidget.textTypeTimerMinute)


        val dateText = "${dateFormat.format(calendar.time)}"
        binding.textTimerDate.text = "Timer set for $dateText"

        binding.textTimerInfoTxt.text = "Timer will end at $dateText ${currentWidget.textTypeTimerHour}:${currentWidget.textTypeTimerMinute}"

        binding.previewChrono.base = System.currentTimeMillis()

        //Update data.
        currentWidget.textTimerDay = day
        currentWidget.textTypeTimerMonth = month
        currentWidget.textTypeTimerYear = year
    }

    fun setTextTimerTime(hour: Int, minute: Int) {
        //Update the UI.
        binding.textTimerTime.text = "Timer set for $hour:${minute}"

        val dateFormat = SimpleDateFormat.getDateInstance()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, currentWidget.textTimerDay)
        calendar.set(Calendar.MONTH, currentWidget.textTypeTimerMonth)
        calendar.set(Calendar.YEAR, currentWidget.textTypeTimerYear)


        val dateText = "${dateFormat.format(calendar.time)}"
        binding.textTimerInfoTxt.text = "Timer will end at $dateText ${currentWidget.textTypeTimerHour}:${currentWidget.textTypeTimerMinute}"

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        binding.previewChrono.base = System.currentTimeMillis()

        //Update data.
        currentWidget.textTypeTimerHour = hour
        currentWidget.textTypeTimerMinute = minute
    }

    fun setClockTimeZone(timeZone: String) {
        //Update the UI.
        binding.textClockTZ.text = timeZone

        //Update the data.
        currentWidget.clockTimeZone = timeZone

        //Update the preview.
        binding.previewAnalogClock.timeZone = timeZone
        binding.previewAnalogClock.requestLayout()


    }


    suspend fun checkAndAddColors() {
        val sharedPreferences = getSharedPreferences("colorspref", MODE_PRIVATE)

        val colorsList: MutableList<ColorData> = ArrayList<ColorData>()

        colorsList.addAll(getDefaultColors(sharedPreferences))

        //Get the user's saved colors
        val savedColors = sharedPreferences.getString("savedcolors", null)

        var savedColorsList: MutableList<ColorData> = ArrayList<ColorData>()

        if (savedColors != null) {
            savedColorsList = getSavedColors(savedColors)
            colorsList.addAll(savedColorsList)
        }

        val colorIDList = getColorIDList(colorsList)

        addNewColor(colorIDList, savedColorsList)
    }

    suspend fun getSavedColors(savedColorsJSON: String): MutableList<ColorData> {
        val gson = Gson()
        val type = object : TypeToken<MutableList<ColorData>>() {}.type

        return gson.fromJson(savedColorsJSON, type)
    }

    suspend fun getColorIDList(colorsList: MutableList<ColorData>): MutableList<String?> {
        val idList: MutableList<String?> = ArrayList()
        for (color in colorsList) {
            idList.add(color.ID)
        }
        return idList
    }

    suspend fun addNewColor(colorIDList: MutableList<String?>, colorsList: MutableList<ColorData>) {
        val sharedPreferences = getSharedPreferences("colorspref", MODE_PRIVATE)

        val gson = Gson()

        //Check and save the new colors from text color data and background color data
        if (currentWidget.widgetTextColor!!.ID == currentWidget.widgetBackgroundColor!!.ID) {
            if (!colorIDList.contains(currentWidget.widgetTextColor!!.ID!!)) {
                colorsList.add(currentWidget.widgetTextColor!!)

                val json = gson.toJson(colorsList)
                sharedPreferences.edit().putString("savedcolors", json).apply()
            }
        } else {
            if (!colorIDList.contains(currentWidget.widgetTextColor!!.ID!!)) {
                colorsList.add(currentWidget.widgetTextColor!!)

                val json = gson.toJson(colorsList)
                sharedPreferences.edit().putString("savedcolors", json).apply()
            }

            if (!colorIDList.contains(currentWidget.widgetBackgroundColor!!.ID!!)) {
                colorsList.add(currentWidget.widgetBackgroundColor!!)

                val json = gson.toJson(colorsList)
                sharedPreferences.edit().putString("savedcolors", json).apply()
            }

        }
    }

    suspend fun getDefaultColors(sharedPreferences: SharedPreferences): MutableList<ColorData> {

        val dataList: MutableList<ColorData> = ArrayList<ColorData>()
        //Check and get the default colors.
        val defaultColorsJSON = sharedPreferences.getString("defaultcolors", null)
        if (defaultColorsJSON == null) {
            //Add the default colors to shared preferences
            dataList.addAll(AppUtils.addDefaultColors(this))
        } else {
            val defaultColors = AppUtils.getDefaultColorsFromJson(defaultColorsJSON)
            if (defaultColors.size == 6) {
                //Add the new color to shared preferences
                dataList.addAll(AppUtils.addDefaultColors(this))
            } else {
                dataList.addAll(defaultColors)
            }
        }

        return dataList
    }

    fun saveTheNewColor(data: ColorData){
        //Check if the added new color is a valid color
        try {
            Color.parseColor(data.colorHexCode)
        }
        catch (e: IllegalArgumentException){
            data.colorHexCode="#000000"
            e.printStackTrace()
        }

        val sharedPreferences = getSharedPreferences("colorspref", MODE_PRIVATE)


        val currentSavedColors=ArrayList<ColorData>()

        //Save the color to shared preferences
        //Get the current saved colors and add them to a list
        val savedColorsJSON= sharedPreferences.getString("savedcolors", null)
        if (savedColorsJSON!=null){
            CoroutineScope(Dispatchers.IO).launch {
                currentSavedColors.addAll(getSavedColors(savedColorsJSON))
            }
        }

        //Add the new color
        currentSavedColors.add(data)

        //Save back the updated list
        val gson=Gson()
        val json=gson.toJson(currentSavedColors)
        sharedPreferences.edit().putString("savedcolors", json).apply()

        AppUtils.showSnackbar(this, getString(R.string.newColorAddedText), binding.root, isDark).show()

    }



    fun textBitmap(font: Int, size: Int, color: Int, text: String): Bitmap {
        val textView = TextView(this)
        textView.setTypeface(ResourcesCompat.getFont(this, R.font.open_sans_semibold))
        try {
            textView.setTypeface(ResourcesCompat.getFont(this, font))

        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
        textView.setText(text)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
        textView.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    R.color.Black
                )
            )
        )

        try {
            textView.setTextColor(ColorStateList.valueOf(color))
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 46) {
            if (resultCode == RESULT_OK) {
                runBlocking {
                    CoroutineScope(Dispatchers.IO).launch {
                        val inputStream = contentResolver.openInputStream(data!!.data!!)
                        val bit = BitmapFactory.decodeStream(inputStream)

                      AppUtils.saveImageToCache(this@CreateWidgetActivity, bit, object : AppUtils.WidgetSaveInterface {
                          override fun widgetSaved(savedPath: String) {
                              multiImageList.add(savedPath)
                              addImageBackground()

                              //Show the added image and update the indicators
                              if (multiImageList.size > 1) {
                                  binding.  widgetBackgroundImageFlipper.displayedChild = multiImageList.size - 1
                                  binding.   createWidgetPreviewMultiImageFlipper.displayedChild = multiImageList.size - 1

                                  indicateCurrentImage(binding. createWidgetPreviewMultiImageFlipper.displayedChild)

                                  //Set action to show image and update the initial action val to true
                                  if (!isInitialNextImageActionSet) {
                                      val actionData = ActionData()
                                      actionData.actionType = AppUtils.ACTIONS_SIMPLE
                                      actionData.actionName = AppUtils.ACTION_NEXTIMAGE
                                      setClickAction(actionData)
                                      isInitialNextImageActionSet = true
                                  }
                              }
                          }

                          override fun widgetSaveFailed() {
                          }

                      })

                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 67) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 46)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        if (dialogId == 21) {
            showAddNameDialog(color)
        }
        if (dialogId == 22){
            colorSheetInterface.editColor(color)
            colorsEdited = true
        }

    }

    override fun onDialogDismissed(dialogId: Int) {

    }

    override fun imageRemoved(position: Int) {
        removeImageItem()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.keyCode == KeyEvent.KEYCODE_BACK) {
            val builder = AlertDialog.Builder(this)
            builder.setNegativeButton(
                "NO",
                DialogInterface.OnClickListener { dialogInterface, i -> })
            builder.setPositiveButton("YES", DialogInterface.OnClickListener { dialogInterface, i ->
                if (!colorsEdited) {
                    setResult(RESULT_CANCELED)
                } else {
                    if (type == "create") {
                        setResult(RESULT_OK)
                    } else {
                        val intent = Intent()
                        intent.putExtra("widgetdata", currentWidget)
                        setResult(RESULT_OK, intent)
                    }
                }
                if (type.equals("create")) {
                    finish()
                }
                if (type.equals("edit")) {
                    supportFinishAfterTransition()
                }
            })

            if (type.equals("create")) {
                builder.setTitle("Unfinished widget!")
                builder.setMessage("This widget will not be saved, do you still want to continue?")
            }
            if (type.equals("edit")) {
                builder.setTitle("Unfinished edits to widget!")
                builder.setMessage("Edits to this widget will not be saved, do you still want to continue?")
            }

            builder.show()
        }
        return false
    }

    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, R.anim.activity_puller, R.anim.activity_close)
        }
        else {
           overridePendingTransition(R.anim.activity_puller, R.anim.activity_close)
        }
    }

    override fun gravitySelected(gravityData: TextGravityData, type: String) {
        if (type == TextGravitySelectionSheet.VERTICAL) {
            setVerticalGravity(gravityData)
        } else {
            setHorizontalGravity(gravityData)
        }

    }

    override fun actionSelected(actionData: ActionData) {
        setClickAction(actionData)
    }

    override fun gradientSelected(gradientData: GradientData) {
        setCurrentBackgroundGradient(gradientData)
    }

}