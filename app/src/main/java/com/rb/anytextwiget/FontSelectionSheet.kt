package com.rb.anytextwiget

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rb.anytextwiget.databinding.FragmentFontSelectionSheetBinding

import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*
import kotlin.collections.ArrayList

class FontSelectionSheet() : BottomSheetDialogFragment(),FontsAdapter.fontItemInterface {
    lateinit var contexT: Context

    lateinit var dataList: MutableList<FontItemData>

    lateinit var filteredList: MutableList<FontItemData>

    lateinit var adapter:FontsAdapter

    lateinit var searchAdapter: FontsAdapter

    var currentFont:String= "source name"

    lateinit var itemInterface:FontsAdapter.fontItemInterface

    lateinit var binding: FragmentFontSelectionSheetBinding


    constructor(currentFont: String, itemInterface: FontsAdapter.fontItemInterface) : this() {
        this.currentFont = currentFont
        this.itemInterface=itemInterface
    }


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


        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it2 ->
                val behaviour = BottomSheetBehavior.from(it2)
                val layoutParams = it2.layoutParams
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                it2.layoutParams = layoutParams
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        contexT = requireActivity()

        // Inflate the layout for this fragment
        binding = FragmentFontSelectionSheetBinding.inflate(inflater, container, false)

        val themePreferences=contexT.getSharedPreferences("apppref", Context.MODE_PRIVATE)

        //Adjust UI with theme
        adjustTheme(themePreferences.getString("apptheme",AppUtils.LIGHT)!!)



        dataList=ArrayList<FontItemData>()
        filteredList=ArrayList<FontItemData>()


        var list: MutableList<FontItemData> =   ArrayList()
        CoroutineScope(Dispatchers.Default).launch {
             list = AppUtils.getNewFontsList(contexT)
            dataList.addAll(list)
        }

        val layoutManager= LinearLayoutManager(contexT)
        binding.fontselectionrecy.layoutManager=layoutManager

        val layoutManager2=LinearLayoutManager(contexT)
        binding.fontselectionsearchrecy.layoutManager=layoutManager2

        adapter= FontsAdapter(contexT, dataList, currentFont, itemInterface, this)
        searchAdapter= FontsAdapter(contexT,filteredList, currentFont,itemInterface,this)


        CoroutineScope(Dispatchers.Main).launch {
            binding.fontselectionrecy.adapter = adapter
            binding.fontselectionsearchrecy.adapter = searchAdapter

        }


        //Update the first displayed font item manually to set it's typeface.
        binding.fontselectionrecy.postDelayed(Runnable { adapter.notifyItemChanged(0) },100)


        (binding.fontselectionrecy.itemAnimator as SimpleItemAnimator).supportsChangeAnimations=false
        (binding.fontselectionsearchrecy.itemAnimator as SimpleItemAnimator).supportsChangeAnimations=false


        val searchViewClearIcon=(binding.fontSelectionSearchView.findViewById(androidx.appcompat.R.id.search_close_btn)) as ImageView
        searchViewClearIcon.imageTintList= ColorStateList.valueOf(
            ContextCompat.getColor(
                contexT,
                R.color.Grey
            )
        )

        val searchEditText = (binding.fontSelectionSearchView.findViewById(androidx.appcompat.R.id.search_src_text)) as EditText
        searchEditText.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(contexT, R.color.Grey)))


        binding.fontSelectionSearchIcon.setOnClickListener {
            binding.fontSelectionSearchView.isIconified=true
            handleSearchVisibility(true)
            binding.fontSelectionSearchView.isIconified=false

        }

        binding.fontSelectionCloseSearch.setOnClickListener {
            handleSearchVisibility(false)
            binding.fontSelectionSearchView.setQuery("", false)
        }


        binding.fontSelectionSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               CoroutineScope(Dispatchers.Main).launch {
                   searchFilter(newText.toString())
               }
                return true
            }

        })

        return binding.root
    }

    override fun itemClicked(widgetFontInfo: WidgetFontInfo) {
        dismiss()
    }

    fun handleSearchVisibility(visible: Boolean){
        if (visible){
            binding.fontSelectionHeaderLayout.visibility=View.INVISIBLE
            binding.fontSelectionSearchLayout.visibility=View.VISIBLE
        }
        else{
            binding. fontSelectionHeaderLayout.visibility=View.VISIBLE
            binding.fontSelectionSearchLayout.visibility=View.GONE
            binding.fontSelectionSearchNA.visibility=View.GONE
            binding. fontselectionsearchrecy.visibility=View.GONE
        }
    }

    fun searchFilter(text: String) {
        //Check if the searched font has a match with font name in the list and add it to a new list
        filteredList=ArrayList<FontItemData>()

        //Set the initial fontNA text and search recy visibility to Gone
        binding. fontSelectionSearchNA.visibility=View.GONE
        binding. fontselectionsearchrecy.visibility=View.GONE
        binding. fontselectionrecy.visibility=View.VISIBLE

        if (!TextUtils.isEmpty(text.trim())){
            for (data in dataList){
                if (data.normalInfo!!.fontName.trim().lowercase().startsWith(text.trim().lowercase())) {
                    filteredList.add(data)
                    searchAdapter.filterList(filteredList)
                    binding.   fontselectionsearchrecy.visibility=View.VISIBLE
                    binding.  fontselectionrecy.visibility=View.GONE
                }
            }
            if (filteredList.isEmpty()){
                binding.  fontSelectionSearchNA.visibility=View.VISIBLE
                binding.  fontselectionrecy.visibility=View.GONE
                searchAdapter.filterList(filteredList)
            }
        }
    }

    fun adjustSheetStyle(isNight:Boolean,roundCorners:Boolean){
        if (isNight){
            if (roundCorners){
                setStyle(STYLE_NORMAL,R.style.bottomSheetDialogStyleForFontsSheetDark)
            }
            else{
                setStyle(STYLE_NORMAL,R.style.noCornersBottomSheetDialogStyleForFontsSheetDark)
            }
        }
        else{
            if (roundCorners){
                setStyle(STYLE_NORMAL,R.style.bottomSheetDialogStyleForFontsSheet)
            }
            else{
                setStyle(STYLE_NORMAL,R.style.noCornersBottomSheetDialogStyleForFontsSheet)
            }
        }
    }

    fun darkMode(isNight:Boolean){
        val editText= binding.fontSelectionSearchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        if (isNight){
            binding.fontselectionsheetheader.setTextColor(ContextCompat.getColor(contexT,R.color.white))
            editText.setTextColor(ContextCompat.getColor(contexT,R.color.white))
        }
        else{
            binding.fontselectionsheetheader.setTextColor(ContextCompat.getColor(contexT,R.color.Black))
            editText.setTextColor(ContextCompat.getColor(contexT,R.color.Black))
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

   /* fun setAds() {
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
    }*/

}