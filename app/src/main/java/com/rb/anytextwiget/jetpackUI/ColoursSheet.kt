package com.rb.anytextwiget.jetpackUI

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.rb.anytextwiget.AppUtils
import com.rb.anytextwiget.AppUtils.Companion.getSavedColors
import com.rb.anytextwiget.AppUtils.Companion.getSavedWidgets
import com.rb.anytextwiget.AppUtils.Companion.removeColor
import com.rb.anytextwiget.AppUtils.Companion.saveTheNewColor
import com.rb.anytextwiget.ColorData
import com.rb.anytextwiget.ColorSelectionSheet
import com.rb.anytextwiget.CreateWidgetActivity
import com.rb.anytextwiget.R
import com.rb.anytextwiget.WidgetData
import com.rb.anytextwiget.WidgetUIData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class ColoursSheet(var context: AppCompatActivity) {
    var coloursList = mutableStateListOf<ColorData>()

    lateinit var colourSheetInterface: ColorSelectionSheet.ColorSheetInterface

    var editColorPosition = 0

    var sharedPreferences = context.getSharedPreferences("colorspref",
        Context.MODE_PRIVATE
    )

    init {
        colourSheetInterface = object : ColorSelectionSheet.ColorSheetInterface {
            override fun saveColor(colorData: ColorData) {
                saveTheNewColor(context, colorData)
                coloursList.add(colorData)
            }

            override fun editColor(newColor: Int) {
                updateAndSaveEditedColor(newColor)
            }

        }
        (context as CreateWidgetActivity).seTColorSheetInterface(colourSheetInterface)


    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ColoursSheetUI(colourSelectedEvent: (colourData: ColorData) -> Unit, onDismiss: () -> Unit) {
        val fontUtils = FontUtils()

        coloursList = SnapshotStateList<ColorData>()

        val sharedPreferences = LocalContext.current.getSharedPreferences(
            "colorspref",
            Context.MODE_PRIVATE
        )

        val context = LocalContext.current

        //Check and get the default colors.
        val defaultColorsJSON = sharedPreferences.getString("defaultcolors", null)
        if (defaultColorsJSON == null) {
            //Add the default colors to shared preferences
            coloursList.addAll(AppUtils.addDefaultColors(LocalContext.current))
        } else {
            val defaultColors = AppUtils.getDefaultColorsFromJson(defaultColorsJSON)
            if (defaultColors.size == 6) {
                //Add the new color to shared preferences
                coloursList.addAll(AppUtils.addDefaultColors(LocalContext.current))
            } else {
                coloursList.addAll(defaultColors)
            }
        }


        //Get the user's saved colors
        val savedColors = sharedPreferences.getString("savedcolors", null)

        if (savedColors != null) {
            coloursList.addAll(getSavedColors(LocalContext.current))
        }

        val scrollState = rememberScrollState()

        ModalBottomSheet(onDismissRequest = onDismiss) {
            Text(
                text = "${LocalContext.current.getString(R.string.color).replace("c", "C")}s",
                fontFamily = fontUtils.openSans(FontWeight.Bold),
                fontSize = TextUnit(28f, TextUnitType.Sp),
                modifier = Modifier
                    .padding(20.dp)
                    .weight(1f, fill = false)
            )

            LazyColumn(modifier = Modifier
                .weight(2f)
                .fillMaxWidth()) {
                items(coloursList) {
                    ColourItem(colourData = it, fontUtils = fontUtils, colourSelectedEvent)
                }
            }

            Button(
                onClick = {
                    val builder = ColorPickerDialog.newBuilder()
                    builder.setColor(android.graphics.Color.parseColor("#ffffff"))
                    builder.setDialogTitle(R.string.colorPickerTitle)
                    builder.setSelectedButtonText(R.string.colorPickerAddButton)
                    builder.setShowColorShades(true)
                    builder.setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                    builder.setShowAlphaSlider(true)
                    builder.setDialogId(21)
                    builder.show(context as AppCompatActivity)
                },
                contentPadding = PaddingValues(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 15.dp)
                    .weight(1f, fill = false)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_add_circle_24),
                    contentDescription = "Add new ${LocalContext.current.getString(R.string.color)} button icon",
                    modifier = Modifier.padding(10.dp, 0.dp)
                )

                Text(
                    text = "Add a new ${LocalContext.current.getString(R.string.color)}",
                    fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ColourItem(colourData: ColorData, fontUtils: FontUtils, colourSelectedEvent: (colourData: ColorData) -> Unit) {
        var colour = MaterialTheme.colorScheme.onBackground
        try {
            colour = Color(android.graphics.Color.parseColor(colourData.colorHexCode))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var showColourOptions by remember {
            mutableStateOf(false)
        }

        val context = LocalContext.current

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(15.dp),
            onClick = {colourSelectedEvent(colourData)}) {
            Image(
                painter = painterResource(id = R.drawable.ic_round_lens_24),
                contentDescription = "${colourData.colorName}",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(10.dp, 0.dp),
                colorFilter = ColorFilter.tint(colour)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            ) {
                Text(
                    text = colourData.colorName!!,
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(FontWeight.Normal),
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = colourData.colorHexCode!!,
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(FontWeight.Normal),
                    textAlign = TextAlign.Start,
                )
            }

            Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                //More options.
                IconButton(onClick = { showColourOptions = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.more_horiz_24dp),
                        contentDescription = "${LocalContext.current.getString(R.string.color)} options"
                    )
                }

                //Colour options.
                DropdownMenu(
                    expanded = showColourOptions,
                    onDismissRequest = { },
                    properties = PopupProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                        excludeFromSystemGesture = true
                    )
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Edit", fontFamily = fontUtils.openSans(
                                    FontWeight.SemiBold
                                )
                            )
                        },
                        leadingIcon = {
                            Icon(
                               imageVector = Icons.Rounded.Create,
                                contentDescription = "Edit ${LocalContext.current.getString(R.string.color)} Option"
                            )
                        },
                        onClick = {
                            showEditColorDialog(colourData)
                            editColorPosition = coloursList.indexOf(colourData)
                            showColourOptions = false
                        })

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Delete", fontFamily = fontUtils.openSans(
                                    FontWeight.SemiBold
                                )
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Remove ${LocalContext.current.getString(R.string.color)} Option"
                            )
                        },
                        onClick = {
                            removeColor(context, colourData)
                            showColourOptions = false
                        })
                }
            }

        }

    }

    fun showEditColorDialog(colorData: ColorData){
        val builder = ColorPickerDialog.newBuilder()
        builder.setColor(android.graphics.Color.parseColor("#ffffff"))
        try {
            builder.setColor(android.graphics.Color.parseColor(colorData.colorHexCode))
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
        builder.show(context)

    }

    fun updateAndSaveEditedColor(newColor:Int){
        var colorHexCode= String.format("#%08X",newColor).uppercase(Locale.getDefault())
        val colorID = coloursList[editColorPosition].ID

        //Check if the edited color is a valid color
        try {
            android.graphics.Color.parseColor(colorHexCode)
        }
        catch (e: IllegalArgumentException){
            colorHexCode="#000000"
            e.printStackTrace()
        }

        //Update the UI
        coloursList.get(editColorPosition).colorHexCode=colorHexCode

        CoroutineScope(Dispatchers.IO).launch{
            //Save the edited color
            val currentSavedColors=ArrayList<ColorData>()

            //Save the color to shared preferences
            //Get the current saved colors and add them to a list
            val savedColorsJSON= sharedPreferences.getString("savedcolors", null)
            if (savedColorsJSON!=null){
                currentSavedColors.addAll(getSavedColors(context))
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
    }

    suspend fun updateSavedWidgets(colorHexCode:String,colorID:String){

        //Edit the color in the saved widgets
        //Get the saved widgets
        val savedWidgetsList=ArrayList<WidgetData>()
        val widgetPreferences=context.getSharedPreferences("widgetspref", Context.MODE_PRIVATE)
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


    fun getSavedUIWidgets(json: String):MutableList<WidgetUIData>{
        val gson=Gson()
        val type=object: TypeToken<MutableList<WidgetUIData>>(){}.type
        return gson.fromJson(json, type)
    }

    fun saveEditedUIWidget(editedUIList:List<WidgetUIData>){
        val sharedPreferences=context.getSharedPreferences("widgetspref", Context.MODE_PRIVATE)

        val uiList=ArrayList<WidgetUIData>()

        //Add all UI widgets and save to shared preferences
        uiList.addAll(editedUIList)
        val gson=Gson()
        val savingJSON= gson.toJson(uiList)
        sharedPreferences.edit().putString("saveduiwidgets", savingJSON).apply()
    }


    suspend fun updateUIWidgets(newColor: String,colorID:String){
        //Get the saved UI widgets
        val sharedPreferences =context.getSharedPreferences("widgetspref", Context.MODE_PRIVATE)
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
        AppUtils.updateUIWidgets(context)
    }


}


