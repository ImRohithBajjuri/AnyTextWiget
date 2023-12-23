package com.rb.anytextwiget

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.rb.anytextwiget.databinding.ColorItemBinding
import java.lang.IllegalArgumentException

public class ColorItemsAdapter constructor() : RecyclerView.Adapter<ColorItemsAdapter.ViewHolder>() {
    lateinit var context: Context
    lateinit var dataList: List<ColorData>
    interface ColorItemInterface{
        fun itemClicked(colorData: ColorData,callFrom: String)
    }
    lateinit var itemInterface:ColorItemInterface
    lateinit var itemInterfaceFromSheet:ColorItemInterface
    var selectedColorItemPos:Int = 0
    lateinit var callFrom:String
    var isDark : Boolean=false


    constructor(context: Context, dataList: List<ColorData>, itemInterface: ColorItemInterface,itemInterfaceFromSheet:ColorItemInterface,selectedColorItemPos:Int,callFrom:String, isDark: Boolean) : this() {
        this.context = context
        this.dataList = dataList
        this.itemInterface=itemInterface
        this.itemInterfaceFromSheet=itemInterfaceFromSheet
        this.selectedColorItemPos=selectedColorItemPos
        this.callFrom=callFrom
        this.isDark = isDark
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=ColorItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.coloritemname.setText(dataList.get(position).colorName)

        holder.binding.coloritemhexcode.setText(dataList.get(position).colorHexCode)

        holder.binding.coloritemcolorimage.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context,R.color.Black))
        try {
            holder.binding.coloritemcolorimage.imageTintList= ColorStateList.valueOf(Color.parseColor(dataList.get(position).colorHexCode))
        }
        catch (e:IllegalArgumentException){
            dataList.get(position).colorHexCode="#000000"
            e.printStackTrace()
        }

        holder.binding.coloritemselectedbutton.visibility=View.GONE

        holder.binding.coloritemcolorimage.backgroundTintList= ColorStateList.valueOf(ContextCompat.getColor(context,android.R.color.transparent))

        if (!isDark){
            if (dataList.get(position).colorHexCode=="#FFEEEEEE" || dataList.get(position).colorHexCode!!.substring(3, dataList.get(position).colorHexCode!!.length).equals("EEEEEE")){
                holder.binding.coloritemcolorimage.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(context.resources,R.color.Grey,null))
            }
        }
        else{
            if (dataList.get(position).colorHexCode=="#FF212121" || dataList.get(position).colorHexCode!!.substring(3, dataList.get(position).colorHexCode!!.length).equals("212121")){
                holder.binding.coloritemcolorimage.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(context.resources,R.color.Grey,null))
            }

        }

        //Check the selected item position and handle the visibility
        if (selectedColorItemPos==position){
            holder.binding.coloritemselectedbutton.visibility=View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(val binding: ColorItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            val sharedPreferences=context.getSharedPreferences("apppref", MODE_PRIVATE)

            //Adjust UI with theme
            adjustTheme(sharedPreferences.getString("apptheme",AppUtils.LIGHT)!!)

            itemView.setOnClickListener {
                //Get the current selected item pos
                val currentPos=selectedColorItemPos

                //Update the selected item pos
                selectedColorItemPos=adapterPosition

                //Notify the adapter for changes
                notifyItemChanged(currentPos)
                notifyItemChanged(selectedColorItemPos)

                //Notify
                itemInterface.itemClicked(dataList.get(adapterPosition),callFrom)
                itemView.postDelayed(Runnable {
                    itemInterfaceFromSheet.itemClicked(dataList.get(adapterPosition),callFrom)
                },200)
            }
        }

        fun getJSONFromColorData(colorData: ColorData):String{
            val gson=Gson()
            return gson.toJson(colorData)
        }

        fun adjustTheme(appTheme:String){
            if (appTheme == AppUtils.LIGHT){
                darkMode(false)

            }
            if (appTheme == AppUtils.DARK){
                darkMode(true)


            }
            if (appTheme == AppUtils.FOLLOW_SYSTEM){
                when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES ->{
                        darkMode(true)

                    }

                    Configuration.UI_MODE_NIGHT_NO ->{
                        darkMode(false)

                    }
                }
            }
        }

        fun darkMode(isNight : Boolean){
            if (isNight){
                binding.coloritemname.setTextColor(ContextCompat.getColor(context,R.color.white))
            }
            else{
                binding.coloritemname.setTextColor(ContextCompat.getColor(context,R.color.Black))
            }
        }
    }
}