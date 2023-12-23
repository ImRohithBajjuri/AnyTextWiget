package com.rb.anytextwiget

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rb.anytextwiget.databinding.TextGravityItemBinding


class TextGravitySelectionAdapter(): RecyclerView.Adapter<TextGravitySelectionAdapter.ViewHolder>() {
    lateinit var context: Context
    lateinit var dataList: MutableList<TextGravityData>
    var currentGravity: Int = 0
    lateinit var type: String

    interface TextGravitySelectionInterface{
        fun gravitySelected(gravityData: TextGravityData, type: String)
    }

    lateinit var textGravitySelectionInterface: TextGravitySelectionInterface
    lateinit var textGravitySelectionInterface2: TextGravitySelectionInterface

    constructor(context: Context, dataList: MutableList<TextGravityData>, currentGravity: Int, type: String, textGravitySelectionInterface: TextGravitySelectionInterface, textGravitySelectionInterface2: TextGravitySelectionInterface) : this() {
        this.context = context
        this.dataList = dataList
        this.currentGravity = currentGravity
        this.type = type
        this.textGravitySelectionInterface = textGravitySelectionInterface
        this.textGravitySelectionInterface2 = textGravitySelectionInterface2

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TextGravityItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textGravityItemText.text = dataList[position].gravityName

        holder.binding.textGravitySelected.visibility = View.GONE
        if (dataList.get(position).gravityValue == currentGravity){
            holder.binding.textGravitySelected.visibility = View.VISIBLE
        }
    }

    inner class ViewHolder(val binding: TextGravityItemBinding) : RecyclerView.ViewHolder(binding.root){

        init {

            val sharedPreferences=context.getSharedPreferences("apppref", Context.MODE_PRIVATE)

            //Adjust UI with theme
            adjustTheme(sharedPreferences.getString("apptheme",AppUtils.LIGHT)!!)

            binding.root.setOnClickListener {
                textGravitySelectionInterface.gravitySelected(dataList.get(adapterPosition), type)
                textGravitySelectionInterface2.gravitySelected(dataList.get(adapterPosition), type)
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
                binding.textGravityItemText.setTextColor(ContextCompat.getColor(context,R.color.white))
            }
            else{
                binding.textGravityItemText.setTextColor(ContextCompat.getColor(context,R.color.Black))
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}