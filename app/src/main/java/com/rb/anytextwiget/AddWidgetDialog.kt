package com.rb.anytextwiget

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rb.anytextwiget.databinding.FragmentAddWidgetDialogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class AddWidgetDialog() : BottomSheetDialogFragment() {
    lateinit var contexT:Context
    lateinit var appWidgetData: AppWidgetData
    lateinit var dataList:MutableList<WidgetData>
    var isAlreadyAvailable:Boolean=false
    lateinit var adapter: MultiImageAdapter
    lateinit var binding: FragmentAddWidgetDialogBinding
    var isDark : Boolean=false


    interface AddWidgetDialogInterface{
        fun newWidgetAdded()
    }

    lateinit var addWidgetDialogInterface: AddWidgetDialogInterface

    constructor(appWidgetData: AppWidgetData,dataList:MutableList<WidgetData>,addWidgetDialogInterface: AddWidgetDialogInterface) : this() {
        this.appWidgetData = appWidgetData
        this.dataList=dataList
        this.addWidgetDialogInterface=addWidgetDialogInterface
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        contexT= requireActivity()
        val sharedPreferences=contexT.getSharedPreferences("apppref", Context.MODE_PRIVATE)
        val roundCorners=sharedPreferences.getBoolean("roundcorners",true)
        val appTheme=sharedPreferences.getString("apptheme",AppUtils.LIGHT)

        if (appTheme == AppUtils.LIGHT){
            adjustSheetStyle(false,roundCorners)
        }
        if (appTheme == AppUtils.DARK){
            adjustSheetStyle(true,roundCorners)
        }
        if (appTheme == AppUtils.FOLLOW_SYSTEM){
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> adjustSheetStyle(true,roundCorners)

                Configuration.UI_MODE_NIGHT_NO ->  adjustSheetStyle(false,roundCorners)
            }
        }

        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contexT=requireActivity()
        // Inflate the layout for this fragment
        binding= FragmentAddWidgetDialogBinding.inflate(inflater, container, false)

        val widgetData=appWidgetData.widgetData

        val sharedPreferences=contexT.getSharedPreferences("apppref", Context.MODE_PRIVATE)

        //Adjust UI with theme
        adjustTheme(sharedPreferences.getString("apptheme",AppUtils.LIGHT)!!)


        //Check if the widget is already available
        binding.widgetdialogalreadyavailabletext.visibility=View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            for (data in dataList){
                if (data.widgetID== appWidgetData.widgetData?.widgetID){
                    binding.widgetdialogalreadyavailabletext.visibility=View.VISIBLE
                    isAlreadyAvailable=true
                    break
                }
            }
        }


        //Set the widget attributes
        binding.widgetdialogcardtext.text = appWidgetData.widgetData!!.widgetText



        try {
            binding.widgetdialogcardtext.setTextColor(ColorStateList.valueOf(Color.parseColor(widgetData!!.widgetTextColor!!.colorHexCode)))
        }
        catch (e:IllegalArgumentException){
            binding.widgetdialogcardtext.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(contexT,R.color.white)))

            e.printStackTrace()
        }

        binding.widgetdialogcardtext.setTextSize(TypedValue.COMPLEX_UNIT_SP,widgetData!!.widgetTextSize.toFloat())

        binding.widgetdialogcardtext.setPadding(AppUtils.dptopx(contexT, widgetData.textPadding))

        try {
            if (widgetData.widgetFontInfo!!.sourceName != "NA") {
                val id = contexT.resources.getIdentifier(widgetData.widgetFontInfo!!.sourceName, "font", contexT.packageName)
                val typeFace = ResourcesCompat.getFont(contexT, id)
                binding.widgetdialogcardtext.typeface = typeFace
            } else {
                val fontText: String = if (widgetData.widgetFontInfo!!.fontStyle == "normal") {
                    widgetData.widgetFontInfo!!.fontName.lowercase()
                        .replace(" ", "_", true)
                } else {
                    widgetData.widgetFontInfo!!.fontName.lowercase()
                        .replace(" ", "_", true) + "_" + widgetData.widgetFontInfo!!.fontStyle.lowercase()
                }


                val id = contexT.resources.getIdentifier(fontText, "font", contexT.packageName)
                val typeFace2 = ResourcesCompat.getFont(contexT, id)
                binding.widgetdialogcardtext.typeface = typeFace2
            }

        }
        catch (e:Resources.NotFoundException){
            binding.widgetdialogcardtext.typeface = ResourcesCompat.getFont(contexT,R.font.open_sans_semibold)
            e.printStackTrace()
        }

        binding.widgetdialogcardpreview.elevation=0f
        binding.widgetdialogcardpreview.setCardBackgroundColor(ContextCompat.getColor(contexT,R.color.Black))

        try {
            binding.widgetdialogcardpreview.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(widgetData.widgetBackgroundColor!!.colorHexCode)))

            val colorData=widgetData.widgetBackgroundColor


            if (isDark){
                if (colorData!!.colorHexCode=="#212121" || colorData.colorHexCode!!.substring(3, colorData.colorHexCode!!.length).equals("212121")){
                    binding.widgetdialogcardpreview.elevation=AppUtils.dptopx(contexT,8).toFloat()
                }
            }
            else{
                if (colorData!!.colorHexCode=="#FFFFFF" || colorData.colorHexCode!!.substring(3, colorData.colorHexCode!!.length).equals("FFFFFF")){
                    binding.widgetdialogcardpreview.elevation=AppUtils.dptopx(contexT,8).toFloat()
                }
            }

        }
        catch (e:IllegalArgumentException){
            e.printStackTrace()
        }

        binding.widgetdialogcardpreview.radius=AppUtils.dptopx(contexT, 30).toFloat()
        if (widgetData.widgetRoundCorners!=null){
            if (widgetData.widgetRoundCorners){
                binding.widgetdialogcardpreview.radius=AppUtils.dptopx(contexT, 30).toFloat()
            }
            else{
                binding.widgetdialogcardpreview.radius=0f
            }
        }

        binding.widgetDialogBackgroundFlipper.visibility = View.GONE
        if (widgetData.widgetBackGroundType == "image") {
            binding.widgetDialogBackgroundFlipper.visibility = View.VISIBLE
            adapter=MultiImageAdapter(contexT,widgetData.widgetMultiImageList!!,null,MultiImageAdapter.fromAddWidgetDialog,appWidgetData.ifBackgroundImageBytesList!!)
            binding.widgetDialogBackgroundFlipper.adapter=adapter
        }

        binding.widgetDialogGradientBgr.visibility = View.GONE
        if (widgetData.widgetBackGroundType == "gradient") {
            if (widgetData.widgetBackgroundGradient != null) {
                binding.widgetDialogGradientBgr.visibility = View.VISIBLE
                try {
                    val sourceName = "no_corners_" + widgetData.widgetBackgroundGradient!!.sourceName

                    val gradient = resources.getIdentifier(sourceName, "drawable", contexT.packageName)
                    val drawable = ContextCompat.getDrawable(contexT, gradient)

                    binding.widgetDialogGradientBgr.setImageDrawable(drawable)
                }
                catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                }

            }
        }

        val verticalGravity: Int
        val horizontalGravity: Int
        if (widgetData.widgetTextVerticalGravity != null){
            verticalGravity =widgetData.widgetTextVerticalGravity!!.gravityValue
        }
        else{
            verticalGravity = Gravity.CENTER_VERTICAL
        }

        if (widgetData.widgetTextHorizontalGravity != null){
            horizontalGravity = widgetData.widgetTextHorizontalGravity!!.gravityValue
        }
        else{
            horizontalGravity = Gravity.CENTER_HORIZONTAL
        }
        binding.widgetdialogcardtext.gravity = verticalGravity or horizontalGravity


        //Set the outline
        if (widgetData.outlineEnabled) {
            binding.widgetdialogcardpreview.strokeWidth = AppUtils.dptopx(contexT, widgetData.widgetOutlineWidth)
            if (widgetData.widgetOutlineColor != null) {
                try {
                    binding.widgetdialogcardpreview.setStrokeColor(ColorStateList.valueOf(Color.parseColor(widgetData.widgetOutlineColor!!.colorHexCode)))
                }
                catch (e: IllegalArgumentException) {
                    binding.widgetdialogcardpreview.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#000000")))
                }
            }
        }

        //Set the text shadow
        if (widgetData.textShadowEnabled) {
            if (widgetData.textShadowData != null) {
                try {
                    binding.widgetdialogcardtext.setShadowLayer(
                        AppUtils.dptopx(contexT, widgetData.textShadowData!!.shadowRadius).toFloat(),
                        AppUtils.dptopx(contexT, widgetData.textShadowData!!.horizontalDir).toFloat(),
                        AppUtils.dptopx(contexT, widgetData.textShadowData!!.verticalDir).toFloat(), Color.parseColor(widgetData.textShadowData!!.shadowColor!!.colorHexCode))
                }
                catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }

            }
        }



        binding.widgetdialogaddbutton.setOnClickListener {
            val animation=AnimUtils.pressAnim(object :Animation.AnimationListener{
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (!isAlreadyAvailable){
                        val snackbar=AppUtils.showSnackbar(contexT,"Saving the widget, please wait...",binding.addWidgetDialogParent,isDark)
                        snackbar.duration=Snackbar.LENGTH_INDEFINITE
                        snackbar.show()
                        CoroutineScope(Dispatchers.Default).launch{
                            addWidgetToSavedWidgets(appWidgetData,snackbar)
                        }

                    }
                    else{
                        Toast.makeText(contexT,"Widget is already available in your saved list",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(animation)
        }

        binding.widgetdialogcardpreview.setOnClickListener {
            handleClick(widgetData)
        }
        return binding.root
    }


    suspend fun addWidgetToSavedWidgets(appWidgetData: AppWidgetData, snackbar: Snackbar){
        //Save the image(s) to device if the widget has background image(s) and the image(s) is(are) not available on the device
        if (appWidgetData.widgetData!!.widgetBackGroundType == "image"){
            if (appWidgetData.widgetData!!.widgetMultiImageList==null){
                if (!AppUtils.contentExists(appWidgetData.widgetData!!.widgetBackgroundImageUri,contexT)){
                    val savedUri=AppUtils.saveImageBytes(contexT,AppUtils.uniqueContentNameGenerator("Image"),appWidgetData.ifBackgroundImageBytes!!)
                    appWidgetData.widgetData!!.widgetBackgroundImageUri=savedUri
                }
            }
            else{
                for (imageUri in appWidgetData.widgetData!!.widgetMultiImageList!!){
                    val index=appWidgetData.widgetData!!.widgetMultiImageList!!.indexOf(imageUri)
                    if (!AppUtils.contentExists(imageUri,contexT)){
                        val savedUri=AppUtils.saveImageBytes(contexT,AppUtils.uniqueContentNameGenerator("Image"),appWidgetData.ifBackgroundImageBytesList!!.get(index))
                        appWidgetData.widgetData!!.widgetMultiImageList!!.set(index,savedUri)
                    }
                }
            }
        }

        saveWidget(appWidgetData)

        //Add the color to saved colors if it isn't available
        saveNewColor(appWidgetData)

        withContext(Dispatchers.Main) {
            snackbar.dismiss()
            addWidgetDialogInterface.newWidgetAdded()
            dismiss()
        }
    }

    suspend fun saveNewColor(appWidgetData: AppWidgetData){
        var isTextColorAlreadyAvailable=false
        var isBackgroundColorAlreadyAvailable=false

        val savedColors=AppUtils.getSavedColors(contexT)
        val defaultColors=AppUtils.getDefaultColors(contexT)
        val dataList=ArrayList<ColorData>()
        dataList.addAll(defaultColors)
        dataList.addAll(savedColors)

        for (data in dataList){
            if (data.ID==appWidgetData.widgetData!!.widgetTextColor!!.ID){
                isTextColorAlreadyAvailable=true
            }

            if (appWidgetData.widgetData!!.widgetBackGroundType == "color"){
                if (data.ID==appWidgetData.widgetData!!.widgetBackgroundColor!!.ID){
                    isBackgroundColorAlreadyAvailable=true
                }
            }
        }

        if (!isTextColorAlreadyAvailable){
            AppUtils.saveNewColor(contexT,appWidgetData.widgetData!!.widgetTextColor!!)
        }

        if (!isBackgroundColorAlreadyAvailable){
            AppUtils.saveNewColor(contexT,appWidgetData.widgetData!!.widgetBackgroundColor!!)
        }
    }

    suspend fun saveWidget(appWidgetData: AppWidgetData){
        val widgetsList=ArrayList<WidgetData>()

        val sharedPreferences=contexT.getSharedPreferences("widgetspref", AppCompatActivity.MODE_PRIVATE)

        //Get the saved widgets
        val savedWidgetsJSON=sharedPreferences.getString("savedwidgets", null)

        if (savedWidgetsJSON!=null){
            val savedWidgetsList=getSavedWidgetsFromJSON(savedWidgetsJSON)
            widgetsList.addAll(savedWidgetsList)
        }

        //Add outline color to old widgets
        if (appWidgetData.widgetData!!.widgetOutlineColor  == null) {
            appWidgetData.widgetData!!.widgetOutlineColor = AppUtils.getDefaultColors(contexT)[0]
        }

        //Add the new widget data and save it to the pref
        widgetsList.add(appWidgetData.widgetData!!)

        val json=getJSONFromWidgetDataList(widgetsList)

        sharedPreferences.edit().putString("savedwidgets", json).apply()
    }

    fun getSavedWidgetsFromJSON(json: String):MutableList<WidgetData>{
        val gson= Gson()
        val type=object : TypeToken<MutableList<WidgetData>>(){}.type
        return gson.fromJson(json, type)
    }

    fun getJSONFromWidgetDataList(widgetsList: MutableList<WidgetData>):String{
        val gson= Gson()
        return gson.toJson(widgetsList)
    }

    fun adjustSheetStyle(isNight:Boolean,roundCorners:Boolean){
        if (isNight){
            if (roundCorners){
                setStyle(STYLE_NORMAL,R.style.bottomSheetDialogStyleDark)
            }
            else{
                setStyle(STYLE_NORMAL,R.style.noCornersBottomSheetDialogStyleDark)
            }
        }
        else{
            if (roundCorners){
                setStyle(STYLE_NORMAL,R.style.bottomSheetDialogStyle)
            }
            else{
                setStyle(STYLE_NORMAL,R.style.noCornersBottomSheetDialogStyle)
            }
        }
    }

    fun darkMode(isNight:Boolean){
        isDark=isNight
        if (isNight){
            binding.addWidgetDialogHeader.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            binding.widgetdialogaddbutton.setCardBackgroundColor(ContextCompat.getColor(contexT,R.color.green4Dark))
        }
        else{
            binding.addWidgetDialogHeader.setTextColor(ContextCompat.getColor(contexT,R.color.Black))
            binding.widgetdialogaddbutton.setCardBackgroundColor(ContextCompat.getColor(contexT,R.color.green4))
        }
    }

    fun adjustTheme(appTheme:String){
        if (appTheme == AppUtils.LIGHT){
            darkMode(false)
        }
        if (appTheme == AppUtils.DARK){
            darkMode(true)
        }
        if (appTheme == AppUtils.FOLLOW_SYSTEM){
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> darkMode(true)

                Configuration.UI_MODE_NIGHT_NO -> darkMode(false)
            }
        }
    }

    fun handleClick(widgetData: WidgetData){
        if (widgetData.widgetClickAction!!.actionType == AppUtils.ACTIONS_SIMPLE) {
            when (widgetData.widgetClickAction!!.actionName) {
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

                AppUtils.ACTION_OPEN_LINK -> {openLink(widgetData)}

                AppUtils.ACTION_NEXTIMAGE -> {
                    toggleNextImage()
                }

            }
        }
        else {
            if (widgetData.widgetClickAction!!.appPackageName == "com.rb.anytextwiget") {
                Toast.makeText(contexT, "Opens this app", Toast.LENGTH_SHORT).show()
            }
            else {
                openApp(widgetData)
            }
        }
    }

    fun openApp(widgetData: WidgetData){
        val intent = contexT.packageManager.getLaunchIntentForPackage(widgetData.widgetClickAction!!.appPackageName)
        if (intent != null){
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        else {
            Toast.makeText(contexT, "${widgetData.widgetClickAction!!.actionName} is not available for click", Toast.LENGTH_LONG).show()
        }
    }


    fun toggleWifi(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            panelIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(panelIntent)
        }
        else{
            val wifiManager = contexT.applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
            if (wifiManager.isWifiEnabled){
                wifiManager.setWifiEnabled(false)
            }
            else {
                wifiManager.setWifiEnabled(true)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun toggleBlueTooth(){
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
    fun toggleDnd(){
        val notificationManager = contexT.applicationContext.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

        // Check if the notification policy access has been granted for the app.
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            Toast.makeText(contexT, "Do Not Disturb access has not been granted", Toast.LENGTH_LONG).show()
        }
        else {
            if (notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_NONE){
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
            }
            else {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun toggleFlashlight(){
        val cameraManager = contexT.applicationContext.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager

        cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {

            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                cameraManager.unregisterTorchCallback(this)
                if (!enabled){
                    cameraManager.setTorchMode(cameraManager.cameraIdList[0], true)
                }
                else {
                    cameraManager.setTorchMode(cameraManager.cameraIdList[0], false)
                }

                super.onTorchModeChanged(cameraId, enabled)
            }
        }, Handler())
    }

    fun toggleNextImage(){
        binding.widgetDialogBackgroundFlipper.showNext()
    }

    fun openLink(widgetData: WidgetData) {
        if (widgetData.widgetClickAction!!.actionExtra != null && widgetData.widgetClickAction!!.actionExtra.trim().isNotEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(widgetData.widgetClickAction!!.actionExtra)
                startActivity(intent)
            }
            catch (e: Exception) {
                Toast.makeText(requireActivity(), "Unable to open this link. Please enter a proper one", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }

        }

    }


}