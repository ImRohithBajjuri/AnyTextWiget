package com.rb.anytextwiget

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.anytextwiget.databinding.FragmentNewReleasesSheetBinding


class NewReleasesSheet : BottomSheetDialogFragment() {
    lateinit var contexT:Context
    lateinit var binding : FragmentNewReleasesSheetBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        contexT= requireActivity()
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
        // Inflate the layout for this fragment
        contexT=requireActivity()
        binding= FragmentNewReleasesSheetBinding.inflate(inflater, container, false)

        val themePreferences=contexT.getSharedPreferences("apppref", Context.MODE_PRIVATE)

        //Adjust UI with theme
        adjustTheme(themePreferences.getString("apptheme",AppUtils.LIGHT)!!)





        binding.whatsNewTextTwo.text = "Create a looping video of your widgets as an mp4 file. Just click on the 3 dot menu in main page and select 'Loop widgets into video'"

        binding.whatsNewTextThree.text = "Now devices running android version 12 and above can use their material you ${getString(R.string.color)}s to set them as widget text, background ${getString(R.string.color)} etc."

        binding.whatsNewTextFourSubOne.text = "10 new amazing and beautiful gradients that include new pastels. Try them all personalize your widgets even more"


        binding.whatsNewTextFiveSubOne.text = "Lastly this update brings minor bug fixes all over the app"

        binding.whatsNewHeader.text="What's new (V4.1)"



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
            binding.whatsNewHeader.setTextColor(ContextCompat.getColor(contexT,R.color.white))

            binding.whatsNewTitleOne.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.whatsNewTitleTwo.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.whatsNewTitleThree.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.whatsNewTitleFour.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.whatsNewTitleFive.setTextColor(ContextCompat.getColor(contexT, R.color.white))



            binding.whatsNewCardOne.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.darkGrey5))
            binding.whatsNewCardTwo.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.darkGrey5))
            binding.whatsNewCardThree.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.darkGrey5))
            binding.whatsNewCardFour.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.darkGrey5))
            binding.whatsNewCardFive.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.darkGrey5))


            binding.whatsNewDivFiveSubOne.setBackgroundColor(ContextCompat.getColor(contexT, R.color.darkGrey3))
            binding.whatsNewDivFiveSubTwo.setBackgroundColor(ContextCompat.getColor(contexT, R.color.darkGrey3))
            binding.whatsNewDivFour.setBackgroundColor(ContextCompat.getColor(contexT, R.color.darkGrey3))
            binding.whatsNewDivTwo.setBackgroundColor(ContextCompat.getColor(contexT, R.color.darkGrey3))


            binding.whatsNewTextFourSubOne.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.whatsNewTextFourSubTwo.setTextColor(ContextCompat.getColor(contexT, R.color.white))


            binding.whatsNewTextFiveSubOne.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.whatsNewTextFiveSubTwo.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.whatsNewTextFiveSubThree.setTextColor(ContextCompat.getColor(contexT, R.color.white))


            binding.whatsNewTextOne.setTextColor(ContextCompat.getColor(contexT,R.color.white))

            binding.whatsNewTextTwo.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            binding.whatsNewTextTwoSubOne.setTextColor(ContextCompat.getColor(contexT, R.color.white))


            binding.whatsNewTextThree.setTextColor(ContextCompat.getColor(contexT,R.color.white))


        }
        else{
            binding.whatsNewHeader.setTextColor(ContextCompat.getColor(contexT,R.color.Black))

            binding.whatsNewTitleOne.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.whatsNewTitleTwo.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.whatsNewTitleThree.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.whatsNewTitleFour.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.whatsNewTitleFive.setTextColor(ContextCompat.getColor(contexT, R.color.Black))


            binding.whatsNewCardOne.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.white))
            binding.whatsNewCardTwo.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.white))
            binding.whatsNewCardThree.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.white))
            binding.whatsNewCardFour.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.white))
            binding.whatsNewCardFive.setCardBackgroundColor(ContextCompat.getColor(contexT, R.color.white))



            binding.whatsNewDivFiveSubOne.setBackgroundColor(ContextCompat.getColor(contexT, R.color.LightGrey))
            binding.whatsNewDivFiveSubTwo.setBackgroundColor(ContextCompat.getColor(contexT, R.color.LightGrey))

            binding.whatsNewDivFour.setBackgroundColor(ContextCompat.getColor(contexT, R.color.LightGrey))
            binding.whatsNewDivTwo.setBackgroundColor(ContextCompat.getColor(contexT, R.color.LightGrey))


            binding.whatsNewTextFourSubOne.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.whatsNewTextFourSubTwo.setTextColor(ContextCompat.getColor(contexT, R.color.Black))

            binding.whatsNewTextFiveSubOne.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.whatsNewTextFiveSubTwo.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.whatsNewTextFiveSubThree.setTextColor(ContextCompat.getColor(contexT, R.color.Black))


            binding.whatsNewTextOne.setTextColor(ContextCompat.getColor(contexT,R.color.Black))

            binding.whatsNewTextTwo.setTextColor(ContextCompat.getColor(contexT,R.color.Black))
            binding.whatsNewTextTwoSubOne.setTextColor(ContextCompat.getColor(contexT, R.color.Black))

            binding.whatsNewTextThree.setTextColor(ContextCompat.getColor(contexT,R.color.Black))

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