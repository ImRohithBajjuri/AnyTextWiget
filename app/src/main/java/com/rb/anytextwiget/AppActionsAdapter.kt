package com.rb.anytextwiget

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.icu.number.NumberFormatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rb.anytextwiget.databinding.AppActionItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppActionsAdapter(): RecyclerView.Adapter<AppActionsAdapter.ViewHolder>() {
    lateinit var context: Context
    lateinit var dataList: MutableList<String>

    interface AppActionsAdapterInterface{
        fun appActionSelected(appPackage: String)
    }

    companion object {
        var currentAppAction: String? = null
    }

    lateinit var appActionsAdapterInterface: AppActionsAdapterInterface

    constructor(context: Context, dataList: MutableList<String>, appActionsAdapterInterface: AppActionsAdapterInterface) : this() {
        this.context = context
        this.dataList = dataList
        this.appActionsAdapterInterface = appActionsAdapterInterface
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AppActionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            val packageName = dataList[position]
            val appInfo = context.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val appName = context.packageManager.getApplicationLabel(appInfo)
            val appIcon = context.packageManager.getApplicationIcon(appInfo)

            withContext(Dispatchers.Main){
                holder.binding.appActionItemImage.setImageDrawable(appIcon)
                holder.binding.appActionItemName.text = appName

                if (currentAppAction != null) {
                    holder.handleAppActionSelection(position)
                }
            }
        }

        if (position == dataList.size-1) {
            holder.binding.appActionItemDiv.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(val binding: AppActionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            val sharedPreferences=context.getSharedPreferences("apppref", Context.MODE_PRIVATE)

            //Adjust UI with theme
            adjustTheme(sharedPreferences.getString("apptheme",AppUtils.LIGHT)!!)

            itemView.setOnClickListener {
                appActionSelected()

                appActionsAdapterInterface.appActionSelected(dataList.get(adapterPosition))
            }
        }

        fun handleAppActionSelection(position: Int) {
            binding.appActionItemSelectedButton.visibility = View.GONE

            if (dataList.get(position) == currentAppAction) {
                binding.appActionItemSelectedButton.visibility = View.VISIBLE
            }
        }

        fun appActionSelected() {
            val currentPos: Int
            if (currentAppAction != null) {
                currentPos = dataList.indexOf(currentAppAction)
            }
            else {
                currentPos = 0
            }

            //Update the current app action to the newly selected one
            currentAppAction = dataList.get(adapterPosition)

            //Update to show selected button
            notifyItemChanged(currentPos)
            notifyItemChanged(adapterPosition)

        }

        fun darkMode(isNight : Boolean) {
            if (isNight){
                binding.appActionItemName.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.appActionItemDiv.setBackgroundColor(ContextCompat.getColor(context, R.color.darkGrey4))
            }
            else{
                binding.appActionItemName.setTextColor(ContextCompat.getColor(context, R.color.Black))
                binding.appActionItemDiv.setBackgroundColor(ContextCompat.getColor(context, R.color.LightGrey))
            }
        }

        fun adjustTheme(appTheme:String) {
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
    }
}