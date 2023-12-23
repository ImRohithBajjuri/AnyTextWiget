package com.rb.anytextwiget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.anytextwiget.databinding.AddNameLayoutBinding
import com.rb.anytextwiget.databinding.FragmentActionSelectionSheetBinding
import kotlinx.coroutines.*
import java.util.*

class ActionSelectionSheet() : BottomSheetDialogFragment(), AppActionsAdapter.AppActionsAdapterInterface {
    lateinit var contexT: Context
    lateinit var binding: FragmentActionSelectionSheetBinding
    lateinit var currentActionData: ActionData
    var isMultiImage: Boolean = false
    lateinit var adapter: AppActionsAdapter
    lateinit var dataList: MutableList<String>

    interface ActionSheetInterface{
        fun actionSelected(actionData: ActionData)
    }

    lateinit var actionSheetInterface: ActionSheetInterface

    var isDark: Boolean = false

    constructor(dataList: MutableList<String>,actionSheetInterface: ActionSheetInterface, currentActionData: ActionData, isMultiImage: Boolean) : this() {
        this.dataList = dataList
        this.actionSheetInterface = actionSheetInterface
        this.currentActionData = currentActionData
        this.isMultiImage = isMultiImage
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        contexT = requireActivity()
        val sharedPreferences=contexT.getSharedPreferences("apppref", Context.MODE_PRIVATE)
        val roundCorners=sharedPreferences.getBoolean("roundcorners", true)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        contexT=requireActivity()

        // Inflate the layout for this fragment
        binding =  FragmentActionSelectionSheetBinding.inflate(inflater, container, false)

        val sharedPreferences = contexT.getSharedPreferences("apppref", AppCompatActivity.MODE_PRIVATE)
        val roundCorners = sharedPreferences.getBoolean("roundcorners", true)


        //Adjust UI with theme
        adjustTheme(sharedPreferences.getString("apptheme",AppUtils.LIGHT)!!)


        //Set the round corners
        setRoundCorners(roundCorners)


        //Adjust the wifi action des according to the android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            this.binding.wifiActionDes.text = "Opens Wi-Fi panel"
        }
        else {
            this.binding.wifiActionDes.text = "Toggles Wi-Fi on/off"
        }

        //Adjust visibility of flashlight according to android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            this.binding.flashlightActionLayout.visibility = View.VISIBLE
            this.binding.doNotDisturbActionLayout.visibility = View.VISIBLE
        }
        else {
            this.binding.flashlightActionLayout.visibility = View.GONE
            this.binding.doNotDisturbActionLayout.visibility = View.GONE
        }

        //Adjust visibility of next image action according to isMultiImage var
        if (isMultiImage){
            this.binding.nextImageActionLayout.visibility = View.VISIBLE
            this.binding.simpleActionDiv5.visibility = View.VISIBLE
        }
        else {
            this.binding.nextImageActionLayout.visibility = View.GONE
            this.binding.simpleActionDiv5.visibility = View.GONE
        }


        val layoutManager = LinearLayoutManager(contexT)

        this.binding.appActionsRecy.layoutManager = layoutManager

        //Set the current app action data only if action type is app
        if (currentActionData.actionType == AppUtils.ACTIONS_APP) {
            AppActionsAdapter.currentAppAction = currentActionData.appPackageName
        }


        adapter = AppActionsAdapter(contexT, dataList, this@ActionSelectionSheet)


        this.binding.appActionsRecy.adapter = adapter


        CoroutineScope(Dispatchers.Main).launch {


        }

        //Set the current simple action data and update the UI only if the action type is simple
        if (currentActionData.actionType == AppUtils.ACTIONS_SIMPLE) {
            handleSimpleActionSelection(currentActionData.actionName)
        }


        this.binding.wifiActionLayout.setOnClickListener {
            handleSimpleActionSelection(AppUtils.ACTION_WIFI)

            val actionData = ActionData()
            actionData.actionType = AppUtils.ACTIONS_SIMPLE
            actionData.actionName = AppUtils.ACTION_WIFI

            actionSheetInterface.actionSelected(actionData)


            //Dismiss after a delay
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                dismiss()
            }
        }

        this.binding.doNotDisturbActionLayout.setOnClickListener {
            handleSimpleActionSelection(AppUtils.ACTION_DONOTDISTURB)

            val actionData = ActionData()
            actionData.actionType = AppUtils.ACTIONS_SIMPLE
            actionData.actionName = AppUtils.ACTION_DONOTDISTURB

            actionSheetInterface.actionSelected(actionData)

            //Dismiss after a delay
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                dismiss()
            }
        }

        this.binding.flashlightActionLayout.setOnClickListener {
            handleSimpleActionSelection(AppUtils.ACTION_FLASHLIGHT)

            val actionData = ActionData()
            actionData.actionType = AppUtils.ACTIONS_SIMPLE
            actionData.actionName = AppUtils.ACTION_FLASHLIGHT

            actionSheetInterface.actionSelected(actionData)

            //Dismiss after a delay
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                dismiss()
            }
        }

        this.binding.bluetoothActionLayout.setOnClickListener {
            handleSimpleActionSelection(AppUtils.ACTION_BLUETOOTH)

            val actionData = ActionData()
            actionData.actionType = AppUtils.ACTIONS_SIMPLE
            actionData.actionName = AppUtils.ACTION_BLUETOOTH

            actionSheetInterface.actionSelected(actionData)

            //Dismiss after a delay
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                dismiss()
            }
        }

        this.binding.nextImageActionLayout.setOnClickListener {
            handleSimpleActionSelection(AppUtils.ACTION_NEXTIMAGE)

            val actionData = ActionData()
            actionData.actionType = AppUtils.ACTIONS_SIMPLE
            actionData.actionName = AppUtils.ACTION_NEXTIMAGE

            actionSheetInterface.actionSelected(actionData)

            //Dismiss after a delay
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                dismiss()
            }
        }

        this.binding.openLinkActionLayout.setOnClickListener {
            if (currentActionData.actionExtra.trim().isEmpty()) {
                setLinkDialog("https://", isDark)
            }
            else {
                setLinkDialog(currentActionData.actionExtra, isDark)
            }
        }

        this.binding.nothingActionLayout.setOnClickListener {
            handleSimpleActionSelection(AppUtils.ACTION_NOTHING)

            val actionData = ActionData()
            actionData.actionType = AppUtils.ACTIONS_SIMPLE
            actionData.actionName = AppUtils.ACTION_NOTHING

            actionSheetInterface.actionSelected(actionData)

            //Dismiss after a delay
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                dismiss()
            }
        }

        return binding.root
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
        isDark = isNight
        if (isNight){
            this.binding.actionSheetHeader.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            this.binding.simpleActionDiv1.setBackgroundColor(ContextCompat.getColor(contexT,R.color.darkGrey4))
            this.binding.simpleActionDiv3.setBackgroundColor(ContextCompat.getColor(contexT,R.color.darkGrey4))
            this.binding.simpleActionDiv4.setBackgroundColor(ContextCompat.getColor(contexT,R.color.darkGrey4))
            this.binding.simpleActionDiv5.setBackgroundColor(ContextCompat.getColor(contexT,R.color.darkGrey4))
            this.binding.simpleActionDiv6.setBackgroundColor(ContextCompat.getColor(contexT,R.color.darkGrey4))
            this.binding.simpleActionDiv7.setBackgroundColor(ContextCompat.getColor(contexT,R.color.darkGrey4))



            this.binding.simpleActionsCard.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.darkGrey5))
            this.binding.appActionsCard.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.darkGrey5))


            this.binding.wifiActionText.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            this.binding.bluetoothActionText.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            this.binding.doNotDisturbActionText.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            this.binding.flashlightActionText.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            this.binding.nextImageActionText.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            this.binding.nothingActionText.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            this.binding.openLinkActionText.setTextColor(ContextCompat.getColor(contexT,R.color.white))



            this.binding.wifiActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.purpleLight))
            this.binding.bluetoothActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.purpleLight))
            this.binding.doNotDisturbActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.purpleLight))
            this.binding.flashlightActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.purpleLight))
            this.binding.nextImageActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.purpleLight))
            this.binding.openLinkActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.purpleLight))
            this.binding.nothingActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.purpleLight))


        }
        else{
            this.binding.actionSheetHeader.setTextColor(ContextCompat.getColor(contexT,R.color.Black))
            this.binding.simpleActionDiv1.setBackgroundColor(ContextCompat.getColor(contexT,R.color.LightGrey3))
            this.binding.simpleActionDiv3.setBackgroundColor(ContextCompat.getColor(contexT,R.color.LightGrey3))
            this.binding.simpleActionDiv4.setBackgroundColor(ContextCompat.getColor(contexT,R.color.LightGrey3))
            this.binding.simpleActionDiv5.setBackgroundColor(ContextCompat.getColor(contexT,R.color.LightGrey3))
            this.binding.simpleActionDiv6.setBackgroundColor(ContextCompat.getColor(contexT,R.color.LightGrey3))
            this.binding.simpleActionDiv7.setBackgroundColor(ContextCompat.getColor(contexT,R.color.LightGrey3))


            this.binding.simpleActionsCard.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.white))
            this.binding.appActionsCard.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.white))


            this.binding.wifiActionText.setTextColor(ContextCompat.getColor(contexT,R.color.darkGrey))
            this.binding.bluetoothActionText.setTextColor(ContextCompat.getColor(contexT,R.color.darkGrey))
            this.binding.doNotDisturbActionText.setTextColor(ContextCompat.getColor(contexT,R.color.darkGrey))
            this.binding.flashlightActionText.setTextColor(ContextCompat.getColor(contexT,R.color.darkGrey))
            this.binding.nextImageActionText.setTextColor(ContextCompat.getColor(contexT,R.color.darkGrey))
            this.binding.nothingActionText.setTextColor(ContextCompat.getColor(contexT,R.color.darkGrey))
            this.binding.openLinkActionText.setTextColor(ContextCompat.getColor(contexT,R.color.darkGrey))



            this.binding.wifiActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
            this.binding.bluetoothActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
            this.binding.doNotDisturbActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
            this.binding.flashlightActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
            this.binding.nextImageActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
            this.binding.nothingActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
            this.binding.openLinkActionImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
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

    fun handleSimpleActionSelection(action: String){
        when(action){
            AppUtils.ACTION_WIFI -> {
                this.binding.wifiActionSelectedButton.visibility = View.VISIBLE
                this.binding.doNotDisturbActionSelectedButton.visibility = View.GONE
                this.binding.flashlightActionSelectedButton.visibility = View.GONE
                this.binding.bluetoothActionSelectedButton.visibility = View.GONE
                this.binding.nextImageActionSelectedButton.visibility = View.GONE
                this.binding.openLinkActionSelectedButton.visibility = View.GONE
                this.binding.nothingActionSelectedButton.visibility = View.GONE
            }

            AppUtils.ACTION_DONOTDISTURB -> {
                this.binding.wifiActionSelectedButton.visibility = View.GONE
                this.binding.doNotDisturbActionSelectedButton.visibility = View.VISIBLE
                this.binding.flashlightActionSelectedButton.visibility = View.GONE
                this.binding.bluetoothActionSelectedButton.visibility = View.GONE
                this.binding.nextImageActionSelectedButton.visibility = View.GONE
                this.binding.openLinkActionSelectedButton.visibility = View.GONE
                this.binding.nothingActionSelectedButton.visibility = View.GONE
            }

            AppUtils.ACTION_FLASHLIGHT -> {
                this.binding.wifiActionSelectedButton.visibility = View.GONE
                this.binding.doNotDisturbActionSelectedButton.visibility = View.GONE
                this.binding.flashlightActionSelectedButton.visibility = View.VISIBLE
                this.binding.bluetoothActionSelectedButton.visibility = View.GONE
                this.binding.nextImageActionSelectedButton.visibility = View.GONE
                this.binding.openLinkActionSelectedButton.visibility = View.GONE
                this.binding.nothingActionSelectedButton.visibility = View.GONE
            }

            AppUtils.ACTION_BLUETOOTH -> {
                this.binding.wifiActionSelectedButton.visibility = View.GONE
                this.binding.doNotDisturbActionSelectedButton.visibility = View.GONE
                this.binding.flashlightActionSelectedButton.visibility = View.GONE
                this.binding.bluetoothActionSelectedButton.visibility = View.VISIBLE
                this.binding.nextImageActionSelectedButton.visibility = View.GONE
                this.binding.openLinkActionSelectedButton.visibility = View.GONE
                this.binding.nothingActionSelectedButton.visibility = View.GONE
            }

            AppUtils.ACTION_NEXTIMAGE -> {
                this.binding.wifiActionSelectedButton.visibility = View.GONE
                this.binding.doNotDisturbActionSelectedButton.visibility = View.GONE
                this.binding.flashlightActionSelectedButton.visibility = View.GONE
                this.binding.bluetoothActionSelectedButton.visibility = View.GONE
                this.binding.nextImageActionSelectedButton.visibility = View.VISIBLE
                this.binding.openLinkActionSelectedButton.visibility = View.GONE
                this.binding.nothingActionSelectedButton.visibility = View.GONE
            }

            AppUtils.ACTION_OPEN_LINK -> {
                this.binding.wifiActionSelectedButton.visibility = View.GONE
                this.binding.doNotDisturbActionSelectedButton.visibility = View.GONE
                this.binding.flashlightActionSelectedButton.visibility = View.GONE
                this.binding.bluetoothActionSelectedButton.visibility = View.GONE
                this.binding.nextImageActionSelectedButton.visibility = View.GONE
                this.binding.openLinkActionSelectedButton.visibility = View.VISIBLE
                this.binding.nothingActionSelectedButton.visibility = View.GONE

                this.binding.openLinkActionDes.text = "Opens '${currentActionData.actionExtra}'"
            }

            AppUtils.ACTION_NOTHING -> {
                this.binding.wifiActionSelectedButton.visibility = View.GONE
                this.binding.doNotDisturbActionSelectedButton.visibility = View.GONE
                this.binding.flashlightActionSelectedButton.visibility = View.GONE
                this.binding.bluetoothActionSelectedButton.visibility = View.GONE
                this.binding.nextImageActionSelectedButton.visibility = View.GONE
                this.binding.openLinkActionSelectedButton.visibility = View.GONE
                this.binding.nothingActionSelectedButton.visibility = View.VISIBLE
            }
        }

        //Update the UI for app actions adapter
        if (AppActionsAdapter.currentAppAction != null){
            val index = dataList.indexOf(AppActionsAdapter.currentAppAction)
            AppActionsAdapter.currentAppAction = null

            adapter.notifyItemChanged(index)
        }
    }

    fun setRoundCorners(roundCorners: Boolean) {
        if (roundCorners) {
            this.binding.simpleActionsCard.radius = AppUtils.dptopx(contexT, 30).toFloat()
            this.binding.appActionsCard.radius = AppUtils.dptopx(contexT, 30).toFloat()

            val layoutParams= LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(
                AppUtils.dptopx(contexT, 10), AppUtils.dptopx(contexT, 5), AppUtils.dptopx(
                    contexT,
                    10
                ), AppUtils.dptopx(contexT, 0)
            )


            this.binding.simpleActionsCard.layoutParams=layoutParams
            this.binding.appActionsCard.layoutParams=layoutParams

        } else {
            this.binding.simpleActionsCard.radius = 0f
            this.binding.appActionsCard.radius = 0f

            val layoutParams= LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(
                AppUtils.dptopx(contexT, 0), AppUtils.dptopx(contexT, 5), AppUtils.dptopx(
                    contexT,
                    0
                ), AppUtils.dptopx(contexT, 0)
            )
            this.binding.simpleActionsCard.layoutParams=layoutParams
            this.binding.appActionsCard.layoutParams=layoutParams
        }
    }

    fun setLinkDialog(currentLink:String, isNight: Boolean) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Set a link")

        val view = AddNameLayoutBinding.inflate(LayoutInflater.from(requireActivity()))
        val editText = view.nameinput
        editText.setText(currentLink)
        editText.hint = "Enter a url/web link"
        editText.inputType = InputType.TYPE_TEXT_VARIATION_URI
        editText.setHintTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
        editText.background = ColorDrawable(Color.TRANSPARENT)



        //Set dark mode for edit text
        if (isNight) {
            editText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
        }
        else {
            editText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.Black))
        }
        builder.setView(view.root)


        builder.setPositiveButton("set", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                handleSimpleActionSelection(AppUtils.ACTION_OPEN_LINK)

                val actionData = ActionData()
                actionData.actionType = AppUtils.ACTIONS_SIMPLE
                actionData.actionName = AppUtils.ACTION_OPEN_LINK
                actionData.actionExtra = editText.text.toString()

                actionSheetInterface.actionSelected(actionData)


                //Dismiss after a delay
                CoroutineScope(Dispatchers.Main).launch {
                    delay(200)
                    dismiss()
                }
            }

        })

        builder.setNegativeButton(
            "cancel"
        ) { p0, p1 -> //Do nothing}
        }
        builder.show()
    }

    override fun appActionSelected(appPackage: String) {
        //Update the UI
        this.binding.wifiActionSelectedButton.visibility = View.GONE
        this.binding.doNotDisturbActionSelectedButton.visibility = View.GONE
        this.binding.flashlightActionSelectedButton.visibility = View.GONE
        this.binding.bluetoothActionSelectedButton.visibility = View.GONE
        this.binding.nextImageActionSelectedButton.visibility = View.GONE
        this.binding.nothingActionSelectedButton.visibility = View.GONE



        val appInfo =contexT.packageManager.getApplicationInfo(appPackage, PackageManager.GET_META_DATA)
        val appName = contexT.packageManager.getApplicationLabel(appInfo)

        //Pass the selected app action data to activity
        val actionData = ActionData()
        actionData.actionName = appName.toString()
        actionData.actionType = AppUtils.ACTIONS_APP
        actionData.appPackageName = appPackage

        actionSheetInterface.actionSelected(actionData)

        //Dismiss after a delay
        CoroutineScope(Dispatchers.Main).launch {
            delay(200)
            dismiss()
        }
    }

    /*fun setAds() {
        if (activity == null) {
            return
        }
        val themePreferences = requireActivity().getSharedPreferences("apppref",
            Context.MODE_PRIVATE
        )

        if (!themePreferences.getBoolean("disableads", false)) {
            MobileAds.initialize(requireActivity()) {
                val adRequest = AdRequest.Builder().build()
                binding.bannerad7.loadAd(adRequest)
            }



            binding.bannerad7.visibility = View.VISIBLE
        } else {
            binding.bannerad7.visibility = View.GONE
        }
    }*/


    override fun onDismiss(dialog: DialogInterface) {
        AppActionsAdapter.currentAppAction = null
        super.onDismiss(dialog)
    }
}