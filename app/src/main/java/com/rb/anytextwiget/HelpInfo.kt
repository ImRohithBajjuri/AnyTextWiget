package com.rb.anytextwiget

import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.rb.anytextwiget.databinding.ActivityHelpInfoBinding

class HelpInfo : AppCompatActivity() {
    lateinit var themePreferences: SharedPreferences
    lateinit var from: String

    lateinit var binding: ActivityHelpInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpInfoBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        from = intent.getStringExtra("from")!!

        themePreferences=getSharedPreferences("apppref", MODE_PRIVATE)
        //Adjust UI with app theme
        val appTheme=themePreferences.getString("apptheme",AppUtils.LIGHT)
        adjustTheme(appTheme!!)

        //Adjust round corners
        val roundCorners=themePreferences.getBoolean("roundcorners",true)
        adjustRoundCorners(roundCorners)


        when (from){
            "helpCreate" -> {
                binding.helpInfoToolbar.setTitle("Create widgets")
                binding.helpInfoCreatingWidgetLayout.visibility = View.VISIBLE
            }
            "helpPlace" -> {
                binding.helpInfoToolbar.setTitle("Place widgets on home screen")
                binding.helpInfoPlacingWidgetLayout.visibility = View.VISIBLE
            }

            "helpSave" -> {
                binding. helpInfoToolbar.setTitle("Save widgets as files")
                binding.helpInfoSavingWidgetLayout.visibility = View.VISIBLE
            }

            "helpImport" -> {
                binding.helpInfoToolbar.setTitle("Importing widgets")
                binding.helpInfoImportingWidgetLayout.visibility = View.VISIBLE

            }
        }


        binding.helpInfoToolbar.setNavigationOnClickListener {
            finish()
        }

        val helpCreateWidgetTextString1 = SpannableStringBuilder(getString(R.string.helpCreateText1))
        helpCreateWidgetTextString1.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.Grey)),8,helpCreateWidgetTextString1.length,SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.helpInfoCreatingWidgetText.text=helpCreateWidgetTextString1

        val helpCreateWidgetTextString2 = SpannableStringBuilder(getString(R.string.helpCreateText2))
        helpCreateWidgetTextString2.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.Grey)),8,helpCreateWidgetTextString2.length,SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.helpInfoCreatingWidgetText2.text=helpCreateWidgetTextString2

        val helpCreateWidgetTextString3 = SpannableStringBuilder(getString(R.string.helpCreateText3))
        helpCreateWidgetTextString3.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.Grey)),8,helpCreateWidgetTextString3.length,SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
        binding. helpInfoCreatingWidgetText3.text=helpCreateWidgetTextString3




        val helpPlaceWidgetTextString1 = SpannableStringBuilder(getString(R.string.helpPlaceText1))
        helpPlaceWidgetTextString1.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.Grey)),8,helpPlaceWidgetTextString1.length,SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.helpInfoPlacingWidgetText.text=helpPlaceWidgetTextString1


        val helpPlaceWidgetTextString2 = SpannableStringBuilder(getString(R.string.helpPlaceText2))
        helpPlaceWidgetTextString2.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.Grey)),8,helpPlaceWidgetTextString2.length,SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
        binding. helpInfoPlacingWidgetText2.text=helpPlaceWidgetTextString2


        val helpPlaceWidgetTextString3 = SpannableStringBuilder(getString(R.string.helpPlaceText3))
        helpPlaceWidgetTextString3.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.Grey)),8,helpPlaceWidgetTextString3.length,SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
        binding. helpInfoPlacingWidgetText3.text=helpPlaceWidgetTextString3


        val helpImportingWidgetTextString1 = SpannableStringBuilder(getString(R.string.helpImportText1))
        helpImportingWidgetTextString1.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.Grey)),8,helpImportingWidgetTextString1.length,SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.helpInfoImportingWidgetText.text=helpImportingWidgetTextString1


        val helpImportingWidgetTextString2 = SpannableStringBuilder(getString(R.string.helpImportText2))
        helpImportingWidgetTextString2.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.Grey)),8,helpImportingWidgetTextString2.length,SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.helpInfoImportingWidgetText2.text=helpImportingWidgetTextString2
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_stable, R.anim.activity_close)
    }

    fun darkMode(isNight:Boolean){
        if (isNight){
            window.statusBarColor=ContextCompat.getColor(this,R.color.colorPrimaryDark)
            binding.  helpInfoToolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark))
            binding. helpInfoParent.setBackgroundColor(ContextCompat.getColor(this,R.color.Black))
            binding. helpInfoCreatingWidgetText.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding. helpInfoCreatingWidgetText2.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding.   helpInfoCreatingWidgetText3.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding. helpInfoCreatingWidgetImageCard.strokeWidth=AppUtils.dptopx(this,2)
            binding.  helpInfoCreatingWidgetImageCard2.strokeWidth=AppUtils.dptopx(this,2)
            binding. helpInfoCreatingWidgetImageCard3.strokeWidth=AppUtils.dptopx(this,2)
            binding.  helpInfoCreatingWidgetImageCard4.strokeWidth=AppUtils.dptopx(this,2)


            binding.   helpInfoPlacingWidgetText.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding.  helpInfoPlacingWidgetText2.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding.  helpInfoPlacingWidgetText3.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding. helpInfoPlacingWidgetImageCard.strokeWidth=AppUtils.dptopx(this,2)
            binding.   helpInfoPlacingWidgetImageCard2.strokeWidth=AppUtils.dptopx(this,2)
            binding.  helpInfoPlacingWidgetImageCard3.strokeWidth=AppUtils.dptopx(this,2)
            binding. helpInfoPlacingWidgetImageCard4.strokeWidth=AppUtils.dptopx(this,2)
            binding. helpInfoPlacingWidgetImageCard5.strokeWidth=AppUtils.dptopx(this,2)


            binding.  helpInfoSavingWidgetImageCard.strokeWidth=AppUtils.dptopx(this,2)
            binding.  helpInfoSavingWidgetImageCard2.strokeWidth=AppUtils.dptopx(this,2)


            binding.helpInfoImportingWidgetText.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding.helpInfoImportingWidgetText2.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding. helpInfoImportingWidgetImageCard.strokeWidth=AppUtils.dptopx(this,2)
            binding. helpInfoImportingWidgetImageCard2.strokeWidth=AppUtils.dptopx(this,2)
            binding. helpInfoImportingWidgetImageCard3.strokeWidth=AppUtils.dptopx(this,2)
            binding. helpInfoImportingWidgetImageCard4.strokeWidth=AppUtils.dptopx(this,2)

        }
        else{
            window.statusBarColor=ContextCompat.getColor(this,R.color.colorPrimary)
            binding.  helpInfoToolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
            binding.  helpInfoParent.setBackgroundColor(ContextCompat.getColor(this,R.color.LightGrey3))
            binding.  helpInfoCreatingWidgetText.setTextColor(ContextCompat.getColor(this,R.color.Black))
            binding.   helpInfoCreatingWidgetText2.setTextColor(ContextCompat.getColor(this,R.color.Black))
            binding.   helpInfoCreatingWidgetText3.setTextColor(ContextCompat.getColor(this,R.color.Black))
            binding.   helpInfoCreatingWidgetImageCard.strokeWidth=AppUtils.dptopx(this,0)
            binding.  helpInfoCreatingWidgetImageCard2.strokeWidth=AppUtils.dptopx(this,0)
            binding.  helpInfoCreatingWidgetImageCard3.strokeWidth=AppUtils.dptopx(this,0)
            binding.helpInfoCreatingWidgetImageCard4.strokeWidth=AppUtils.dptopx(this,0)


            binding.  helpInfoPlacingWidgetText.setTextColor(ContextCompat.getColor(this,R.color.Black))
            binding.   helpInfoPlacingWidgetText2.setTextColor(ContextCompat.getColor(this,R.color.Black))
            binding.  helpInfoPlacingWidgetText3.setTextColor(ContextCompat.getColor(this,R.color.Black))
            binding.  helpInfoPlacingWidgetImageCard.strokeWidth=AppUtils.dptopx(this,0)
            binding.  helpInfoPlacingWidgetImageCard2.strokeWidth=AppUtils.dptopx(this,0)
            binding.  helpInfoPlacingWidgetImageCard3.strokeWidth=AppUtils.dptopx(this,0)
            binding. helpInfoPlacingWidgetImageCard4.strokeWidth=AppUtils.dptopx(this,0)
            binding. helpInfoPlacingWidgetImageCard5.strokeWidth=AppUtils.dptopx(this,0)


            binding.   helpInfoSavingWidgetImageCard.strokeWidth=AppUtils.dptopx(this,0)
            binding.   helpInfoSavingWidgetImageCard2.strokeWidth=AppUtils.dptopx(this,0)


            binding.   helpInfoImportingWidgetText.setTextColor(ContextCompat.getColor(this,R.color.Black))
            binding.   helpInfoImportingWidgetText2.setTextColor(ContextCompat.getColor(this,R.color.Black))
            binding.   helpInfoImportingWidgetImageCard.strokeWidth=AppUtils.dptopx(this,0)
            binding.  helpInfoImportingWidgetImageCard2.strokeWidth=AppUtils.dptopx(this,0)
            binding.   helpInfoImportingWidgetImageCard3.strokeWidth=AppUtils.dptopx(this,0)
            binding.   helpInfoImportingWidgetImageCard4.strokeWidth=AppUtils.dptopx(this,0)
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

    fun adjustRoundCorners(roundCorners: Boolean){
        if (!roundCorners){
            binding.   helpInfoCreatingWidgetImageCard.radius=0f
            binding.    helpInfoCreatingWidgetImageCard2.radius=0f
            binding.    helpInfoCreatingWidgetImageCard3.radius=0f
            binding.    helpInfoCreatingWidgetImageCard4.radius=0f

            binding.  helpInfoPlacingWidgetImageCard.radius=0f
            binding.  helpInfoPlacingWidgetImageCard2.radius=0f
            binding.   helpInfoPlacingWidgetImageCard3.radius=0f
            binding.   helpInfoPlacingWidgetImageCard4.radius=0f
            binding.   helpInfoPlacingWidgetImageCard5.radius=0f

            binding.   helpInfoImportingWidgetImageCard.radius=0f
            binding.    helpInfoImportingWidgetImageCard2.radius=0f
            binding.    helpInfoImportingWidgetImageCard3.radius=0f
            binding.    helpInfoImportingWidgetImageCard4.radius=0f


            binding.  helpInfoSavingWidgetImageCard.radius=0f
            binding.   helpInfoSavingWidgetImageCard2.radius=0f
        }
    }

}