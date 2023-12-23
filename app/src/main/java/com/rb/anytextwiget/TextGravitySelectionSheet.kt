package com.rb.anytextwiget

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.anytextwiget.databinding.FragmentTextGravitySelectionSheetBinding



class TextGravitySelectionSheet() : BottomSheetDialogFragment(), TextGravitySelectionAdapter.TextGravitySelectionInterface {

    lateinit var contexT: Context
    lateinit var dataList: MutableList<TextGravityData>
    lateinit var adapter: TextGravitySelectionAdapter
    lateinit var type: String
    var currentGravity: Int = 0
    lateinit var textGravitySelectionInterface2: TextGravitySelectionAdapter.TextGravitySelectionInterface
    lateinit var binding: FragmentTextGravitySelectionSheetBinding

    companion object{
        val HORIZONTAL = "horizontal"
        val VERTICAL = "vertical"
    }


    constructor(type: String, currentGravity: Int, textGravitySelectionInterface2: TextGravitySelectionAdapter.TextGravitySelectionInterface) : this() {
        this.type = type
        this.currentGravity = currentGravity
        this.textGravitySelectionInterface2 = textGravitySelectionInterface2
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        contexT= requireActivity()
        val sharedPreferences=contexT.getSharedPreferences("apppref", Context.MODE_PRIVATE)
        val roundCorners=sharedPreferences.getBoolean("roundcorners",true)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        contexT = requireActivity()

        // Inflate the layout for this fragment
        binding =  FragmentTextGravitySelectionSheetBinding.inflate(inflater, container, false)

        val themePreferences=contexT.getSharedPreferences("apppref", Context.MODE_PRIVATE)

        //Adjust UI with theme
        adjustTheme(themePreferences.getString("apptheme", AppUtils.LIGHT)!!)

        setAds()

        if (type == VERTICAL){
            binding.textGravitySelectionHeader.text = "Select vertical gravity"
        }
        else{
            binding.textGravitySelectionHeader.text = "Select horizontal gravity"
        }


        dataList= ArrayList()

        //Add the required gravities
        loadGravities(type)

        adapter = TextGravitySelectionAdapter(contexT, dataList, currentGravity, type, this, textGravitySelectionInterface2)
        val layoutManager = LinearLayoutManager(contexT)
        binding.textGravitySelectionRecy.layoutManager = layoutManager
        binding.textGravitySelectionRecy.adapter = adapter

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

    fun loadGravities(type: String){
        if (type == VERTICAL){
            dataList.add(TextGravityData("Top", Gravity.TOP))
            dataList.add(TextGravityData("Center", Gravity.CENTER_VERTICAL))
            dataList.add(TextGravityData("Bottom", Gravity.BOTTOM))
        }
        else{
            dataList.add(TextGravityData("Start", Gravity.START))
            dataList.add(TextGravityData("Center", Gravity.CENTER_HORIZONTAL))
            dataList.add(TextGravityData("End", Gravity.END))
        }
    }

    fun darkMode(isNight:Boolean){
        if (isNight){
            binding.textGravitySelectionHeader.setTextColor(ContextCompat.getColor(contexT, R.color.white))
        }
        else{
            binding.textGravitySelectionHeader.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
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

     fun setAds() {
     if (activity == null) {
         return
     }
     val themePreferences = requireActivity().getSharedPreferences("apppref",
         Context.MODE_PRIVATE
     )

     if (!themePreferences.getBoolean("disableads", false)) {
         MobileAds.initialize(requireActivity()) {
             val adRequest = AdRequest.Builder().build()
             binding.bannerad5.loadAd(adRequest)
         }



         binding.bannerad5.visibility = View.VISIBLE
     } else {
         binding.bannerad5.visibility = View.GONE
     }
 }


    override fun gravitySelected(gravityData: TextGravityData, type: String) {
        dismiss()
    }

}