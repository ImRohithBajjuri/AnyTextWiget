package com.rb.anytextwiget

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.anytextwiget.databinding.FragmentGradientsSheetBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GradientsSheet() : BottomSheetDialogFragment() {

    lateinit var binding: FragmentGradientsSheetBinding

    lateinit var dataList: MutableList<GradientData>
    lateinit var currentGradientData: GradientData

    interface GradientsListener{
        fun gradientSelected(gradientData: GradientData)
    }

    lateinit var listener: GradientsListener

    lateinit var listerFromSheet: GradientsListener

    var isDark: Boolean=false


    constructor(listener: GradientsListener, currentGradientData: GradientData) : this() {
        this.listener = listener
        this.currentGradientData = currentGradientData
    }


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
        // Inflate the layout for this fragment
        binding = FragmentGradientsSheetBinding.inflate(inflater, container, false)

        val themePreferences=requireActivity().getSharedPreferences("apppref", Context.MODE_PRIVATE)

        //Adjust UI with theme
        adjustTheme(themePreferences.getString("apptheme",AppUtils.LIGHT)!!)

        setAds()

        dataList = ArrayList()

        listerFromSheet = object : GradientsListener {
            override fun gradientSelected(gradientData: GradientData) {
                dismiss()
            }

        }

        //Get the gradients and set em!
        CoroutineScope(Dispatchers.IO).launch {
            dataList = AppUtils.getGradients(requireActivity())

            val currentPosition = getCurrentGradientPosition()

            withContext(Dispatchers.Main) {
                val layoutManager = LinearLayoutManager(requireActivity())
                val adapter = GradientAdapter(requireActivity(), dataList, currentPosition, listener,  listerFromSheet)
                binding.gradientSelectionRecy.layoutManager = layoutManager
                binding.gradientSelectionRecy.adapter = adapter

            }

        }


        return binding.root
    }

    fun getCurrentGradientPosition():Int{
        for (data in dataList){
            if (data.sourceName == currentGradientData.sourceName){
                return dataList.indexOf(data)
            }
        }
        return 0
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
        isDark=isNight
        if (isNight){
            binding.gradientSelectionSheetHeader.setTextColor(ContextCompat.getColor(requireActivity(),R.color.white))
        }
        else{
            binding.gradientSelectionSheetHeader.setTextColor(ContextCompat.getColor(requireActivity(),R.color.Black))

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
                binding.bannerad6.loadAd(adRequest)
            }



            binding.bannerad6.visibility = View.VISIBLE
        } else {
            binding.bannerad6.visibility = View.GONE
        }
    }

}