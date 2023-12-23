package com.rb.anytextwiget

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.anytextwiget.databinding.FragmentHelpSheetBinding


class HelpSheet : BottomSheetDialogFragment() {
    lateinit var contexT: Context
    lateinit var binding: FragmentHelpSheetBinding

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
        binding = FragmentHelpSheetBinding.inflate(inflater, container, false)

        val themePreferences=contexT.getSharedPreferences("apppref", Context.MODE_PRIVATE)

        //Adjust UI with theme
        adjustTheme(themePreferences.getString("apptheme",AppUtils.LIGHT)!!)

        binding.helpCreatingWidgetsText.setOnClickListener {
            val intent = Intent(contexT, HelpInfo::class.java)
            intent.putExtra("from","helpCreate")
            startActivity(intent)
            (contexT as AppCompatActivity).overridePendingTransition(R.anim.activity_open, R.anim.activity_stable)
        }

        binding.helpPlacingWidgetsText.setOnClickListener {
            val intent = Intent(contexT, HelpInfo::class.java)
            intent.putExtra("from","helpPlace")
            startActivity(intent)
            (contexT as AppCompatActivity).overridePendingTransition(R.anim.activity_open, R.anim.activity_stable)
        }

        binding.helpSavingWidgetsText.setOnClickListener {
            val intent = Intent(contexT, HelpInfo::class.java)
            intent.putExtra("from","helpSave")
            startActivity(intent)
            (contexT as AppCompatActivity).overridePendingTransition(R.anim.activity_open, R.anim.activity_stable)
        }

        binding.helpImportingWidgetsText.setOnClickListener {
            val intent = Intent(contexT, HelpInfo::class.java)
            intent.putExtra("from","helpImport")
            startActivity(intent)
            (contexT as AppCompatActivity).overridePendingTransition(R.anim.activity_open, R.anim.activity_stable)
        }

        binding.helpEmailUsText.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.type = "plain/text"
            intent.data = Uri.fromParts("mailto", "rebootingbrains@gmail.com", null)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("rebootingbrains@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
            intent.putExtra(Intent.EXTRA_TEXT, "Body")
            startActivity(Intent.createChooser(intent, "Please select a mail app,"))
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
            binding.helpSheetHeader.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.helpCreatingWidgetsText.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.helpPlacingWidgetsText.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.helpSavingWidgetsText.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.helpImportingWidgetsText.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.helpEmailUsText.setTextColor(ContextCompat.getColor(contexT, R.color.white))

        }
        else{
            binding.helpSheetHeader.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.helpCreatingWidgetsText.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.helpPlacingWidgetsText.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.helpSavingWidgetsText.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.helpImportingWidgetsText.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.helpEmailUsText.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
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