package com.rb.anytextwiget

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.icu.text.TimeZoneNames
import android.icu.util.TimeZone
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.anytextwiget.databinding.FragmentTimeZonesSheetBinding


class TimeZonesSheet : BottomSheetDialogFragment() {


    lateinit var binding: FragmentTimeZonesSheetBinding
    lateinit var timeZones: MutableList<String>
    lateinit var filteredList: MutableList<String>
    lateinit var adapter: TimeZonesAdapter

    interface TimeZonesSheetListener {
        fun onTimeZoneSelected(timeZone: String)
    }

    lateinit var listener: TimeZonesSheetListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val sharedPreferences=requireActivity().getSharedPreferences("apppref", Context.MODE_PRIVATE)
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimeZonesSheetBinding.inflate(inflater, container, false)

        timeZones = ArrayList()
        filteredList = ArrayList()
        timeZones.addAll(TimeZone.getAvailableIDs())

        adapter = TimeZonesAdapter(requireActivity(), timeZones)
        adapter.setSelectionListener(listener)
        val lm = LinearLayoutManager(requireActivity())
        binding.timeZonesSheetRecy.layoutManager = lm
        binding.timeZonesSheetRecy.adapter = adapter

        binding.timeZonesSheetSearch.addTextChangedListener {
            searchFilter(it.toString())
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
        if (isNight){


        }
        else {

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

    fun searchFilter(text: String) {
        //Check if the searched font has a match with font name in the list and add it to a new list
        filteredList=ArrayList<String>()

        if (!TextUtils.isEmpty(text.trim())){
            for (data in timeZones){
                if (data.trim().lowercase().contains(text.trim().lowercase())) {
                    filteredList.add(data)
                    adapter.filterList(filteredList)
                }
            }
            if (filteredList.isEmpty()){
                adapter.filterList(filteredList)
            }
        }
        else {
            adapter.filterList(timeZones)
        }
    }

    fun setSelectionListener(listener: TimeZonesSheetListener) {
        this.listener = listener
    }

}