package com.rb.anytextwiget

import android.animation.Animator
import android.animation.LayoutTransition
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Context.VIBRATOR_SERVICE
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.rb.anytextwiget.databinding.FragmentColorSelectionSheetBinding
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class ColorSelectionSheet constructor() : BottomSheetDialogFragment(),ColorPickerDialogListener,ColorItemsAdapter.ColorItemInterface {

    companion object {
        val BACKGROUND_COLOR = "backgroundcolor"
        val TEXT_COLOR = "textcolor"
        val OUTLINE_COLOR ="outlinecolor"
        val TXT_SHADOW_COLOR = "textshadowcolor"
    }

    lateinit var dataList: MutableList<ColorData>
    lateinit var contexT:Context
    lateinit var adapter: ColorItemsAdapter
    lateinit var itemInterface:ColorItemsAdapter.ColorItemInterface
    lateinit var sharedPreferences:SharedPreferences
    lateinit var currentTextColorData: ColorData
    lateinit var callFrom:String
    var editColorPosition=0
    lateinit var itemTouchHelper: ItemTouchHelper
    var isDark: Boolean=false

    interface ColorSheetInterface{
        fun saveColor(colorData: ColorData)

        fun editColor(newColor:Int)
    }

    lateinit var colorSheetInterface: ColorSheetInterface

    lateinit var materialList: MutableList<ColorData>

    lateinit var binding: FragmentColorSelectionSheetBinding

    constructor(
        itemInterface: ColorItemsAdapter.ColorItemInterface,
        currentTextColorData: ColorData,
        callFrom: String
    ) : this() {
        this.itemInterface = itemInterface
        this.currentTextColorData=currentTextColorData
        this.callFrom=callFrom
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        contexT= requireActivity()
        val sharedPreferences=contexT.getSharedPreferences("apppref", MODE_PRIVATE)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        contexT= requireActivity()
        binding=FragmentColorSelectionSheetBinding.inflate(inflater, container, false)

        val themePreferences=contexT.getSharedPreferences("apppref", MODE_PRIVATE)

        //Adjust UI with theme
        adjustTheme(themePreferences.getString("apptheme",AppUtils.LIGHT)!!)

        setAds()

        sharedPreferences=contexT.getSharedPreferences("colorspref", MODE_PRIVATE)

        dataList = ArrayList<ColorData>()

        //Check and get the default colors.
        val defaultColorsJSON=sharedPreferences.getString("defaultcolors", null)
        if (defaultColorsJSON==null){
            //Add the default colors to shared preferences
            dataList.addAll(AppUtils.addDefaultColors(contexT))
        }
        else{
            val defaultColors=AppUtils.getDefaultColorsFromJson(defaultColorsJSON)
            if (defaultColors.size == 6){
                //Add the new color to shared preferences
                dataList.addAll(AppUtils.addDefaultColors(contexT))
            }
            else{
                dataList.addAll(defaultColors)
            }
        }


        //Get the user's saved colors
        val savedColors= sharedPreferences.getString("savedcolors", null)

        if (savedColors!=null){
            dataList.addAll(getSavedColors(savedColors))
        }



        val layoutManager=LinearLayoutManager(contexT)
        binding.colorselectionrecy.layoutManager=layoutManager


        val currentColorPosition = getCurrentColorPosition()

        adapter= ColorItemsAdapter(
            contexT,
            dataList,
            itemInterface,
            this,
            currentColorPosition,
            callFrom,
            isDark
        )
        binding.colorselectionrecy.adapter=adapter

        (binding.colorselectionrecy.itemAnimator as SimpleItemAnimator).supportsChangeAnimations=false



        val layoutManager2 = LinearLayoutManager(contexT)
        binding.materialYouColorSelectionRecy.layoutManager = layoutManager2

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.colorSelectionOptionsCard.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Main).launch {

                materialList = AppUtils.getMaterialYouColors(contexT)

                val currentPosition = getCurrentMaterialYouPosotion()

                val adapter2 = ColorItemsAdapter(
                    contexT,
                    materialList,
                    itemInterface,
                    this@ColorSelectionSheet,
                    currentPosition,
                    callFrom,
                    isDark
                )
                binding.materialYouColorSelectionRecy.adapter = adapter2
            }
        }
        else {
            binding.colorSelectionOptionsCard.visibility = View.GONE
        }



        colorSheetInterface=object :ColorSheetInterface{
            override fun saveColor(colorData: ColorData) {
                saveTheNewColor(colorData)
            }

            override fun editColor(newColor: Int) {
                updateAndSaveEditedColor(newColor)
            }

        }
        (contexT as CreateWidgetActivity).seTColorSheetInterface(colorSheetInterface)



        val vibrator=contexT.getSystemService(VIBRATOR_SERVICE) as Vibrator


        val layoutTransition=LayoutTransition()
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.colorselectionlayout.layoutTransition=layoutTransition


        val itemTouchHelperCallback=object :ItemTouchHelper.SimpleCallback(0, (ItemTouchHelper.START or ItemTouchHelper.END)){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //Give haptic feedback
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            60,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                }
                else{
                    vibrator.vibrate(60)
                }

                val colorData=dataList.get(viewHolder.adapterPosition)
                val position=viewHolder.adapterPosition

                editColorPosition=viewHolder.adapterPosition

                //Get position in the saved colors
                var positionInSavedColors=0
                val savedColors= sharedPreferences.getString("savedcolors", null)

                CoroutineScope(Dispatchers.IO).launch {
                    if (savedColors!=null){
                        val savedList=getSavedColors(savedColors)
                        for (data in savedList){
                            if (data.ID==colorData.ID){
                                positionInSavedColors=savedList.indexOf(data)
                            }
                        }
                    }
                }

                if (direction==ItemTouchHelper.START){
                    //Remove the color from the UI
                    dataList.removeAt(viewHolder.adapterPosition)
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)

                    //Remove the color from the saved colors list
                    removeColor(colorData)


                    val snackbar= AppUtils.showSnackbar(
                        contexT,
                        getString(R.string.colorDeletedText),
                        binding.colorselectionparent,
                        isDark
                    )
                    snackbar.setAction("undo", object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            dataList.add(position, colorData)
                            adapter.notifyItemInserted(position)

                            //Add back the removed color
                            undoRemoveColor(colorData, positionInSavedColors)
                        }
                    })
                    snackbar.show()
                }
                else{
                    viewHolder.itemView.animate().translationX(1.0f).setDuration(400).setListener(object : Animator.AnimatorListener{
                        override fun onAnimationStart(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {
                            itemTouchHelper.attachToRecyclerView(null)
                            itemTouchHelper.attachToRecyclerView(binding.colorselectionrecy)
                        }

                        override fun onAnimationCancel(animation: Animator) {
                        }

                        override fun onAnimationRepeat(animation: Animator) {
                        }

                    }).start()
                    showEditColorDialog(colorData)
                }
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                var currentSelectedPosition =0
                if (dataList.get(viewHolder.adapterPosition).ID==currentTextColorData.ID){
                    currentSelectedPosition=viewHolder.adapterPosition
                }

                when(viewHolder.adapterPosition){
                    0, 1, 2, 3, 4, 5, currentSelectedPosition -> {
                        return 0
                    }
                    else-> {
                        return super.getSwipeDirs(recyclerView, viewHolder)
                    }
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val itemView=viewHolder.itemView
                var editX=dX
                val editLimitX=itemView.right/4

                //Add the swipe dx value to the limit value to give stretch starter
                val stretchedLimitX=editLimitX+dX/4
                if (editX>stretchedLimitX){
                    //Set the limit with the dx value to give stretchy feeling
                    editX=editLimitX.toFloat()+dX/4
                }


                if (dX<0){
                    leftSwipeOption(
                        c,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        itemView.height.toFloat(),
                        dX
                    )

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)


                }
                else if (dX>0){
                    rightSwipeOption(
                        c,
                        itemView.top.toFloat(),
                        itemView.left.toFloat(),
                        itemView.bottom.toFloat(),
                        itemView.height.toFloat(),
                        editX
                    )

                    super.onChildDraw(c, recyclerView, viewHolder, editX, dY, actionState, isCurrentlyActive)

                }

                if (dX==0f){
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }

            }

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return 0.3f
            }

        }


        itemTouchHelper=ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.colorselectionrecy)


        binding.colorselectionaddcolorbutton.setOnClickListener(View.OnClickListener {
            val animation = AnimUtils.pressAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    val builder = ColorPickerDialog.newBuilder()
                    builder.setColor(Color.parseColor("#ffffff"))
                    builder.setDialogTitle(R.string.colorPickerTitle)
                    builder.setSelectedButtonText(R.string.colorPickerAddButton)
                    builder.setShowColorShades(true)
                    builder.setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                    builder.setShowAlphaSlider(true)
                    builder.setDialogId(21)
                    builder.show((contexT as CreateWidgetActivity))

                }

                override fun onAnimationRepeat(p0: Animation?) {

                }

            })
            it.startAnimation(animation)
        })

        binding.materialYouOption.setOnClickListener {
            binding.materialYouColorSelectionRecy.visibility = View.VISIBLE
            binding.colorselectionrecy.visibility = View.GONE
            binding.colorselectionaddcolorbutton.visibility = View.GONE



            //Adjust the UI
            binding.appColorsOption.setTextColor(ContextCompat.getColor(requireActivity(), R.color.Grey))
            binding.appColorsOption.background =
                ColorDrawable(ContextCompat.getColor(requireActivity(), android.R.color.transparent))

            //Animate the selector
            binding.bgrColorOptionAnim.visibility = View.VISIBLE
            val listener = object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {

                }

                override fun onAnimationEnd(p0: Animator) {
                    binding.materialYouOption.background = ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.selection_background
                    )
                    binding.materialYouOption.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.colorPrimary
                        )
                    )

                    binding.bgrColorOptionAnim.visibility = View.GONE
                }

                override fun onAnimationCancel(p0: Animator) {
                }

                override fun onAnimationRepeat(p0: Animator) {
                }

            }
            animateBackgroundSelector( binding.materialYouOption.x, listener)

        }

        binding.appColorsOption.setOnClickListener {
            binding.materialYouColorSelectionRecy.visibility = View.GONE
            binding.colorselectionrecy.visibility = View.VISIBLE
            binding.colorselectionaddcolorbutton.visibility = View.VISIBLE




            //Adjust the UI
            binding.materialYouOption.setTextColor(ContextCompat.getColor(requireActivity(), R.color.Grey))
            binding.materialYouOption.background =
                ColorDrawable(ContextCompat.getColor(requireActivity(), android.R.color.transparent))

            //Animate the selector
            binding.bgrColorOptionAnim.visibility = View.VISIBLE
            val listener = object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {

                }

                override fun onAnimationEnd(p0: Animator) {
                    binding.appColorsOption.background = ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.selection_background
                    )
                    binding.appColorsOption.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.colorPrimary
                        )
                    )

                    binding.bgrColorOptionAnim.visibility = View.GONE
                }

                override fun onAnimationCancel(p0: Animator) {
                }

                override fun onAnimationRepeat(p0: Animator) {
                }

            }
            animateBackgroundSelector( binding.appColorsOption.x, listener)
        }


        return binding.root
    }

    fun getSavedColors(savedColorsJSON: String):MutableList<ColorData>{
        val gson=Gson()
        val type=object : TypeToken<MutableList<ColorData>>(){}.type

        return gson.fromJson(savedColorsJSON, type)
    }

    fun getCurrentColorPosition():Int{
        for (data in dataList){
            if (data.ID==currentTextColorData.ID){
                return dataList.indexOf(data)
            }
        }
        return 0
    }

    fun getCurrentMaterialYouPosotion():Int{
        for (data in materialList){
            if (data.ID==currentTextColorData.ID){
                return materialList.indexOf(data)
            }
        }
        return 0
    }

    override fun onColorSelected(dialogId: Int, color: Int) {

    }

    override fun onDialogDismissed(dialogId: Int) {
    }

    override fun itemClicked(colorData: ColorData, callFrom: String) {
        dismiss()
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


        val currentSavedColors=ArrayList<ColorData>()

        //Save the color to shared preferences
        //Get the current saved colors and add them to a list
        val savedColorsJSON= sharedPreferences.getString("savedcolors", null)
        if (savedColorsJSON!=null){
            currentSavedColors.addAll(getSavedColors(savedColorsJSON))
        }

        //Add the new color
        currentSavedColors.add(data)

        //Save back the updated list
        val gson=Gson()
        val json=gson.toJson(currentSavedColors)
        sharedPreferences.edit().putString("savedcolors", json).apply()

        //Update the current list
        dataList.add(data)
        adapter.notifyDataSetChanged()

        AppUtils.showSnackbar(contexT, getString(R.string.newColorAddedText), binding.root, isDark).show()

    }

    fun leftSwipeOption(c: Canvas, top: Float, right: Float, bottom: Float, height: Float, dX: Float){
        val width=height/3

        val paint=Paint()
        val rectF=RectF(right + dX, top, right, bottom)
        paint.color=ContextCompat.getColor(contexT, R.color.red2)

        c.drawRect(rectF, paint)

        val drawable=ContextCompat.getDrawable(contexT, R.drawable.ic_round_delete_for_colors_sheet)
        val icon=Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas=Canvas(icon)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        val iconRectF=RectF((right - 2 * width), top + width, right - width, bottom - width)

        c.drawBitmap(icon,null,iconRectF,paint)
    }

    fun rightSwipeOption(c: Canvas, top: Float, left: Float, bottom: Float, height: Float, dX: Float){
        val width=height/3

        val paint=Paint()
        val rectF=RectF(left + dX, top, left, bottom)
        paint.color=ContextCompat.getColor(contexT, R.color.Grey2)

        c.drawRect(rectF, paint)
        val drawable=ContextCompat.getDrawable(contexT, R.drawable.ic_round_create_for_color_sheet)
        val icon=Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas=Canvas(icon)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        val iconRectF=RectF(left + width, top + width, left + 2 * width, bottom - width)
        c.drawBitmap(icon, null, iconRectF, paint)
    }

    fun removeColor(colorData: ColorData) {
        val currentSavedColors=ArrayList<ColorData>()

        //Get the current saved colors and add them to a list
        val savedColorsJSON= sharedPreferences.getString("savedcolors", null)
        if (savedColorsJSON!=null){
            currentSavedColors.addAll(getSavedColors(savedColorsJSON))
        }

        if (!currentSavedColors.isEmpty()){
            val iterator=currentSavedColors.iterator()
            while (iterator.hasNext()){
                val data=iterator.next()
                if (data.ID==colorData.ID){
                    iterator.remove()

                    //Save back the updated list
                    val gson=Gson()
                    val json=gson.toJson(currentSavedColors)
                    sharedPreferences.edit().putString("savedcolors", json).apply()

                    break
                }
            }
        }
    }

    fun undoRemoveColor(colorData: ColorData, position: Int){
        val currentSavedColors=ArrayList<ColorData>()

        //Save the color back to shared preferences
        //Get the current saved colors and add them to a list
        val savedColorsJSON= sharedPreferences.getString("savedcolors", null)
        if (savedColorsJSON!=null){
            currentSavedColors.addAll(getSavedColors(savedColorsJSON))
        }

        //Add the new color
        currentSavedColors.add(position, colorData)

        //Save back the updated list
        val gson=Gson()
        val json=gson.toJson(currentSavedColors)
        sharedPreferences.edit().putString("savedcolors", json).apply()

    }

    fun showEditColorDialog(colorData: ColorData){
        val builder = ColorPickerDialog.newBuilder()
        builder.setColor(Color.parseColor("#ffffff"))
        try {
            builder.setColor(Color.parseColor(colorData.colorHexCode))
        }
        catch (e:IllegalArgumentException){
            e.printStackTrace()
        }
        builder.setDialogTitle(R.string.editColor)
        builder.setSelectedButtonText(R.string.colorPickerEditButton)
        builder.setShowColorShades(true)
        builder.setDialogType(ColorPickerDialog.TYPE_CUSTOM)
        builder.setShowAlphaSlider(true)
        builder.setDialogId(22)
        builder.show((contexT as CreateWidgetActivity))
    }

    fun updateAndSaveEditedColor(newColor:Int){
        var colorHexCode=String.format("#%08X",newColor).toUpperCase(Locale.getDefault())
        val colorID=dataList.get(editColorPosition).ID

        //Check if the edited color is a valid color
        try {
            Color.parseColor(colorHexCode)
        }
        catch (e: IllegalArgumentException){
            colorHexCode="#000000"
            e.printStackTrace()
        }

        //Update the UI
        dataList.get(editColorPosition).colorHexCode=colorHexCode
        adapter.notifyItemChanged(editColorPosition)

        CoroutineScope(Dispatchers.IO).launch{
            //Save the edited color
            val currentSavedColors=ArrayList<ColorData>()

            //Save the color to shared preferences
            //Get the current saved colors and add them to a list
            val savedColorsJSON= sharedPreferences.getString("savedcolors", null)
            if (savedColorsJSON!=null){
                currentSavedColors.addAll(getSavedColors(savedColorsJSON))
            }

            //Edit the color
            for (data in currentSavedColors){
                if (data.ID==colorID){
                    data.colorHexCode=colorHexCode

                    //Save back the updated list
                    val gson=Gson()
                    val json=gson.toJson(currentSavedColors)
                    sharedPreferences.edit().putString("savedcolors", json).apply()
                    break
                }
            }

            //Update the color in saved widgets
            updateSavedWidgets(colorHexCode,colorID.toString())

            //Update the UI widgets
            updateUIWidgets(colorHexCode, colorID.toString())
        }

        AppUtils.showSnackbar(contexT, getString(R.string.colorEditedText), binding.root, isDark).show()

    }

    fun getSavedWidgets(json: String):MutableList<WidgetData>{
        val gson= Gson()
        val type=object : TypeToken<MutableList<WidgetData>>(){}.type
        return gson.fromJson(json, type)
    }

    suspend fun updateUIWidgets(newColor: String,colorID:String){
        //Get the saved UI widgets
        val sharedPreferences =contexT.getSharedPreferences("widgetspref", MODE_PRIVATE)
        val uiList=ArrayList<WidgetUIData>()
        val savedUIWidgetsJSON=sharedPreferences.getString("saveduiwidgets", null)
        if (savedUIWidgetsJSON!=null){
            val savedUIWidgets=getSavedUIWidgets(savedUIWidgetsJSON)
            uiList.addAll(savedUIWidgets)
        }

        for (data in uiList){
            if (data.widgetData!!.widgetTextColor?.ID ==colorID){
                data.widgetData!!.widgetTextColor!!.colorHexCode=newColor
                saveEditedUIWidget(uiList)
            }
            if (data.widgetData!!.widgetBackGroundType.equals("color")){
                if (data.widgetData!!.widgetBackgroundColor!!.ID==colorID){
                    data.widgetData!!.widgetBackgroundColor?.colorHexCode =newColor
                    saveEditedUIWidget(uiList)
                }
            }
        }

        //Update the widgets on home screen
        AppUtils.updateUIWidgets(contexT)
    }

    fun getSavedUIWidgets(json: String):MutableList<WidgetUIData>{
        val gson=Gson()
        val type=object: TypeToken<MutableList<WidgetUIData>>(){}.type
        return gson.fromJson(json, type)
    }

    fun saveEditedUIWidget(editedUIList:List<WidgetUIData>){
        val sharedPreferences=contexT.getSharedPreferences("widgetspref", MODE_PRIVATE)

        val uiList=ArrayList<WidgetUIData>()

        //Add all UI widgets and save to shared preferences
        uiList.addAll(editedUIList)
        val gson=Gson()
        val savingJSON= gson.toJson(uiList)
        sharedPreferences.edit().putString("saveduiwidgets", savingJSON).apply()
    }

    suspend fun updateSavedWidgets(colorHexCode:String,colorID:String){

        //Edit the color in the saved widgets
        //Get the saved widgets
        val savedWidgetsList=ArrayList<WidgetData>()
        val widgetPreferences=contexT.getSharedPreferences("widgetspref", MODE_PRIVATE)
        val savedWidgetsJSON=widgetPreferences.getString("savedwidgets", null)

        if (savedWidgetsJSON!=null){
            val savedWidgets=getSavedWidgets(savedWidgetsJSON)
            savedWidgetsList.addAll(savedWidgets)
        }

        for (data in savedWidgetsList){
            if (data.widgetTextColor?.ID ==colorID){
                data.widgetTextColor?.colorHexCode =colorHexCode
            }

            if (data.widgetBackGroundType.equals("color")){
                if (data.widgetBackgroundColor?.ID ==colorID){
                    data.widgetBackgroundColor?.colorHexCode =colorHexCode
                }
            }
        }

        //Save back to shared preferences
        val gson=Gson()
        val json=gson.toJson(savedWidgetsList)
        widgetPreferences.edit().putString("savedwidgets", json).apply()

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
            binding.colorSelectionSheetHeader.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            binding.colorselectionaddcolorbutton.setCardBackgroundColor(ContextCompat.getColor(contexT,R.color.green4Dark))
        }
        else{
            binding.colorSelectionSheetHeader.setTextColor(ContextCompat.getColor(contexT,R.color.Black))
            binding.colorselectionaddcolorbutton.setCardBackgroundColor(ContextCompat.getColor(contexT,R.color.green4))
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

    fun animateBackgroundSelector(requiredPosX: Float, listener: Animator.AnimatorListener) {
        //Set the proper width so that it will blend well with the background
        binding.bgrColorOptionAnim.width = binding.materialYouOption.width

        //Get the current position x
        val currentPosX =  binding.bgrColorOptionAnim.x


        //Start the animation
        val anim = ValueAnimator.ofFloat(currentPosX, requiredPosX)
        anim.addUpdateListener {
            val value = it.animatedValue
            binding.bgrColorOptionAnim.x = value as Float

        }
        anim.addListener(listener)
        anim.duration = 400
        anim.interpolator = DecelerateInterpolator()
        anim.start()
    }

    fun setAds() {
        if (activity == null) {
            return
        }
       val themePreferences = requireActivity().getSharedPreferences("apppref", MODE_PRIVATE)

        if (!themePreferences.getBoolean("disableads", false)) {
            MobileAds.initialize(requireActivity()) {
                val adRequest = AdRequest.Builder().build()
                binding.bannerad3.loadAd(adRequest)
            }



            binding.bannerad3.visibility = View.VISIBLE
        } else {
            binding.bannerad3.visibility = View.GONE
        }
    }
}