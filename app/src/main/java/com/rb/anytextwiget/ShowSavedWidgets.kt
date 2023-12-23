package com.rb.anytextwiget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rb.anytextwiget.databinding.ActivityShowSavedWidgetsBinding

class ShowSavedWidgets : AppCompatActivity() {
    lateinit var dataList: MutableList<WidgetData>
    lateinit var adapter: WidgetsAdapter
    var widgetID:Int=0
    lateinit var themePreferences: SharedPreferences

    lateinit var binding: ActivityShowSavedWidgetsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowSavedWidgetsBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        themePreferences=getSharedPreferences("apppref", MODE_PRIVATE)
        //Adjust UI with app theme
        val appTheme=themePreferences.getString("apptheme",AppUtils.LIGHT)
        adjustTheme(appTheme!!)


        widgetID= intent.extras!!.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)

        dataList=ArrayList<WidgetData>()

        //Get the saved widgets
        val sharedPreferences=getSharedPreferences("widgetspref", MODE_PRIVATE)
        val savedWidgetsJSON=sharedPreferences.getString("savedwidgets",null)

        if (savedWidgetsJSON!=null){
            val savedWidgetsList=getSavedWidgets(savedWidgetsJSON)
            dataList.addAll(savedWidgetsList)
        }

        if (dataList.isEmpty()){
            binding.nowidgetsplaceholderinshowsavedwidgets.visibility=View.VISIBLE
        }
        else{
            binding. nowidgetsplaceholderinshowsavedwidgets.visibility=View.GONE

        }



        val layoutManager=LinearLayoutManager(this)
        binding.selectwidgetrecy.layoutManager=layoutManager
        adapter= WidgetsAdapter(this,dataList,widgetID,"selection",null, null)
        binding.selectwidgetrecy.adapter=adapter

        //Update the first displayed widget manually to set it's typeface.
        binding.selectwidgetrecy.postDelayed(Runnable { adapter.notifyItemChanged(0) },100)

    }

    fun getSavedWidgets(json:String):MutableList<WidgetData>{
        val gson=Gson()
        val type=object : TypeToken<MutableList<WidgetData>>(){}.type
        return gson.fromJson(json,type)
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


    fun darkMode(isNight:Boolean){
        if (isNight){
            window.statusBarColor=ContextCompat.getColor(this,R.color.colorPrimaryDark)
            binding. showSavedWidgetsToolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark))

            binding. showSavedWidgetsParent.setBackgroundColor(ContextCompat.getColor(this,R.color.Black))
        }
        else{
            window.statusBarColor=ContextCompat.getColor(this,R.color.colorPrimary)
            binding. showSavedWidgetsToolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))

            binding.showSavedWidgetsParent.setBackgroundColor(ContextCompat.getColor(this,R.color.LightGrey3))
        }
    }
}