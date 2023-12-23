package com.rb.anytextwiget

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rb.anytextwiget.databinding.TimeZoneItemBinding

class TimeZonesAdapter(): RecyclerView.Adapter<TimeZonesAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var timeZonesList: MutableList<String>
    lateinit var listener: TimeZonesSheet.TimeZonesSheetListener

    constructor(context: Context, timeZonesList: MutableList<String>) : this() {
        this.context = context
        this.timeZonesList = timeZonesList
    }


    inner class ViewHolder(val binding: TimeZoneItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onTimeZoneSelected(timeZonesList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TimeZoneItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return timeZonesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.timeZoneItem.text = timeZonesList[position]
    }

    fun filterList(filteredList:MutableList<String>){
        timeZonesList=filteredList
        notifyDataSetChanged()
    }

    fun setSelectionListener(listener: TimeZonesSheet.TimeZonesSheetListener) {
        this.listener = listener
    }


}