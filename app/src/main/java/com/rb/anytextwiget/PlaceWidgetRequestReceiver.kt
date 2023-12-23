package com.rb.anytextwiget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaceWidgetRequestReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val widgetUIID = intent!!.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)

        val bundle = intent.getBundleExtra("prewbundle")
        val widgetID = bundle!!.getString("widgetid")



        val dataList= ArrayList<WidgetData>()


        //Get the saved widgets
        val sharedPreferences=context!!.getSharedPreferences("widgetspref", AppCompatActivity.MODE_PRIVATE)
        val savedWidgetsJSON=sharedPreferences.getString("savedwidgets", null)

        if (savedWidgetsJSON!=null){
            val savedWidgetsList=getSavedWidgets(savedWidgetsJSON)
            dataList.addAll(savedWidgetsList)
        }

        for (data in dataList){
            if (data.widgetID == widgetID){
                //Save the new UI widget
                val widgetUIData = WidgetUIData()
                widgetUIData.widgetData = data
                widgetUIData.widgetUIID = widgetUIID
                saveNewUIWidget(context, widgetUIData)
                break
            }
        }

        //Update the widget on home screen to refresh and show the newly added widget
        AppUtils.updateSingleUIWidget(context, widgetUIID)

        Toast.makeText(context, "Widget placed on your home screen", Toast.LENGTH_SHORT).show()
    }

    fun saveNewUIWidget(context: Context,widgetUIData: WidgetUIData){
        val sharedPreferences=context.getSharedPreferences("widgetspref", Activity.MODE_PRIVATE)

        val uiList=ArrayList<WidgetUIData>()

        //Get the current saved UI widgets list
        val savedUIWidgetsJSON=sharedPreferences.getString("saveduiwidgets", null)
        if (savedUIWidgetsJSON!=null){
            val savedUIWidgets=getSavedUIWidgets(savedUIWidgetsJSON)
            uiList.addAll(savedUIWidgets)
        }

        //Add the new UI widget and save to shared preferences
        uiList.add(widgetUIData)
        val gson= Gson()
        val savingJSON= gson.toJson(uiList)
        sharedPreferences.edit().putString("saveduiwidgets", savingJSON).apply()
    }

    fun getSavedUIWidgets(json: String):MutableList<WidgetUIData>{
        val gson=Gson()
        val type=object: TypeToken<MutableList<WidgetUIData>>(){}.type
        return gson.fromJson(json, type)
    }

    fun getSavedWidgets(json: String):MutableList<WidgetData>{
        val gson= Gson()
        val type=object : TypeToken<MutableList<WidgetData>>(){}.type
        return gson.fromJson(json, type)
    }


}