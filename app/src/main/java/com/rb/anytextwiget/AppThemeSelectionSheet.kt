package com.rb.anytextwiget

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.anytextwiget.databinding.FragmentAppThemeSelectionSheetBinding


class AppThemeSelectionSheet() : BottomSheetDialogFragment() {
    lateinit var contexT : Context
    lateinit var currentTheme: String

    interface ThemeSelectionInterface{
     fun  themeSlected(selectedTheme : String)
    }

    lateinit var themeSelectionInterface: ThemeSelectionInterface

    lateinit var binding : FragmentAppThemeSelectionSheetBinding

    constructor(currentTheme: String,themeSelectionInterface: ThemeSelectionInterface ) : this() {
        this.currentTheme = currentTheme
        this.themeSelectionInterface=themeSelectionInterface
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        contexT=requireActivity()
        val sharedPreferences=contexT.getSharedPreferences("apppref", Context.MODE_PRIVATE)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contexT=requireActivity()
        // Inflate the layout for this fragment
        binding= FragmentAppThemeSelectionSheetBinding.inflate(inflater, container, false)
        val sharedPreferences = contexT.getSharedPreferences("apppref", AppCompatActivity.MODE_PRIVATE)


        //Show 'follow system' only if device is android 10 or above
        binding.systemTheme.visibility=View.GONE
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            binding.systemTheme.visibility=View.VISIBLE
        }

        if (currentTheme==AppUtils.LIGHT){
            binding.lightTheme.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_round_check_circle_24,0)
            binding.darkTheme.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
            binding.systemTheme.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        }
        if (currentTheme==AppUtils.DARK){
            binding.lightTheme.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
            binding.darkTheme.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_round_check_circle_24,0)
            binding.systemTheme.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)

        }
        if (currentTheme==AppUtils.FOLLOW_SYSTEM){
            binding.lightTheme.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
            binding.darkTheme.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
            binding.systemTheme.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_round_check_circle_24,0)
        }

        //Adjust UI with theme
        adjustTheme(sharedPreferences.getString("apptheme",AppUtils.LIGHT)!!)

        binding.lightTheme.setOnClickListener {
            sharedPreferences.edit().putString("apptheme",AppUtils.LIGHT).apply()
            themeSelectionInterface.themeSlected(AppUtils.LIGHT)
            dismiss()
        }

        binding.darkTheme.setOnClickListener {
            sharedPreferences.edit().putString("apptheme",AppUtils.DARK).apply()
            themeSelectionInterface.themeSlected(AppUtils.DARK)
            dismiss()
        }

        binding.systemTheme.setOnClickListener {
            sharedPreferences.edit().putString("apptheme",AppUtils.FOLLOW_SYSTEM).apply()
            themeSelectionInterface.themeSlected(AppUtils.FOLLOW_SYSTEM)
            dismiss()
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
            binding.appThemeSelectionHeader.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            binding.lightTheme.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            binding.darkTheme.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            binding.systemTheme.setTextColor(ContextCompat.getColor(contexT,R.color.white))

        }
        else{
            binding.appThemeSelectionHeader.setTextColor(ContextCompat.getColor(contexT,R.color.Black))
            binding.lightTheme.setTextColor(ContextCompat.getColor(contexT,R.color.Black))
            binding.darkTheme.setTextColor(ContextCompat.getColor(contexT,R.color.Black))
            binding.systemTheme.setTextColor(ContextCompat.getColor(contexT,R.color.Black))
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

}