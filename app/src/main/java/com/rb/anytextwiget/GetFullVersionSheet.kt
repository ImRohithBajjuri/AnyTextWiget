package com.rb.anytextwiget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.anytextwiget.databinding.FragmentGetFullVersionSheetBinding

class GetFullVersionSheet : BottomSheetDialogFragment() {
    lateinit var contexT:Context

    lateinit var binding: FragmentGetFullVersionSheetBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        contexT=requireActivity()!!
        val sharedPreferences=contexT.getSharedPreferences("apppref", Context.MODE_PRIVATE)
        val roundCorners=sharedPreferences.getBoolean("roundcorners", true)
        if (roundCorners){
            setStyle(STYLE_NORMAL, R.style.bottomSheetDialogStyle)
        }
        else{
            setStyle(STYLE_NORMAL, R.style.noCornersBottomSheetDialogStyle)
        }

        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        contexT=requireActivity()
        binding= FragmentGetFullVersionSheetBinding.inflate(inflater, container, false)

        val removeAdsTextString=SpannableStringBuilder("Remove ads : ")
        removeAdsTextString.append(getString(R.string.removeAdsDes))
        removeAdsTextString.setSpan(ForegroundColorSpan(ContextCompat.getColor(contexT,R.color.Grey)),12,removeAdsTextString.length,SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.removeadstext.text=removeAdsTextString

        val getMoreFeaturesTextString=SpannableStringBuilder("Get more features : ")
        getMoreFeaturesTextString.append(getString(R.string.getMoreFeaturesDes))
        getMoreFeaturesTextString.setSpan(ForegroundColorSpan(ContextCompat.getColor(contexT,R.color.Grey)),19,getMoreFeaturesTextString.length,SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.morefeaturestext.text=getMoreFeaturesTextString

        val supportMeTextString=SpannableStringBuilder("You can support me : ")
        supportMeTextString.append(getString(R.string.supportMeDes))
        supportMeTextString.setSpan(ForegroundColorSpan(ContextCompat.getColor(contexT,R.color.Grey)),20,supportMeTextString.length,SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.supportmetext.text=supportMeTextString


        return binding.root
    }

}