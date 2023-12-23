package com.rb.anytextwiget

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rb.anytextwiget.databinding.FontItemBinding
import kotlinx.coroutines.*

class FontsAdapter() : RecyclerView.Adapter<FontsAdapter.ViewHolder>() {
    lateinit var context:Context
    lateinit var dataList: MutableList<FontItemData>
    var selectedFontItemPos:Int=0
    var selectedFont: String= "source name"
    interface fontItemInterface{
        fun itemClicked(widgetFontInfo: WidgetFontInfo)
    }
    lateinit var itemInterface: fontItemInterface
    lateinit var fontsItemInterfaceForSheet: fontItemInterface


    constructor(context: Context, dataList: MutableList<FontItemData>, selectedFont: String, itemInterface: fontItemInterface, fontsItemInterfaceForSheet: fontItemInterface):this() {
        this.context = context
        this.dataList = dataList
        this.selectedFont = selectedFont
        this.itemInterface = itemInterface
        this.fontsItemInterfaceForSheet = fontsItemInterfaceForSheet
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=FontItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.fontItemHeader.text = dataList[position].normalInfo!!.fontName

        holder.binding.fontitemname.text = dataList[position].normalInfo!!.fontName
        holder.binding.fontitemnormalname.text = dataList[position].normalInfo!!.fontName

        //Use coroutine to avoid slow launch and lag of fonts sheet
        CoroutineScope(Dispatchers.Default).launch {

            try {
                val id = context.resources.getIdentifier(dataList[position].normalInfo!!.sourceName, "font", context.packageName)

                val typeFace = ResourcesCompat.getFont(context, id)

                withContext(Dispatchers.Main){
                    holder.binding.fontitemname.typeface = typeFace
                    holder.binding.fontitemnormalicon.typeface = typeFace
                }

            }
            catch (e: Resources.NotFoundException) {
                Log.e("FOTE", "${dataList.get(position).normalInfo!!.fontName} ")
                e.printStackTrace()
            }

        }


        holder.handleFontItemVisibility(position)


        holder.handleFontItemSelection(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(val binding: FontItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            val sharedPreferences=context.getSharedPreferences("apppref", Context.MODE_PRIVATE)

            setRoundedCorners(sharedPreferences.getBoolean("roundcorners",true))

            //Adjust UI with theme
            adjustTheme(sharedPreferences.getString("apptheme",AppUtils.LIGHT)!!)


            binding.normalfontitemlayout.setOnClickListener {

                fontItemSelected(dataList[adapterPosition].normalInfo!!)
            }

            binding.lightFontItemLayout.setOnClickListener {
                if (dataList.get(adapterPosition).lightInfo!=null){
                    fontItemSelected(dataList[adapterPosition].lightInfo!!)

                }
            }

            binding.italicfontitemlayout.setOnClickListener {
                if (dataList.get(adapterPosition).italicInfo!=null){
                    fontItemSelected(dataList[adapterPosition].italicInfo!!)

                }
            }

            binding.semiboldfontitemlayout.setOnClickListener {
                if (dataList.get(adapterPosition).semiboldInfo!=null){
                    fontItemSelected(dataList[adapterPosition].semiboldInfo!!)
                }
            }

            binding.mediumfontitemlayout.setOnClickListener {
                if (dataList.get(adapterPosition).mediumInfo!=null){
                    fontItemSelected(dataList[adapterPosition].mediumInfo!!)

                }
            }

            binding.boldfontitemlayout.setOnClickListener {
                if (dataList.get(adapterPosition).boldInfo!=null){
                    fontItemSelected(dataList[adapterPosition].boldInfo!!)
                }
            }

            binding.extraboldFontItemLayout.setOnClickListener {
                if (dataList.get(adapterPosition).extraBoldInfo!=null){
                    fontItemSelected(dataList[adapterPosition].extraBoldInfo!!)
                }
            }
        }

        fun fontItemSelected(widgetFontInfo: WidgetFontInfo){
            //Get the current selected item pos
            val currentPos=selectedFontItemPos

            //Update the selected item pos
            selectedFontItemPos=adapterPosition

            //Update the selected font ID
            selectedFont= widgetFontInfo.sourceName

            //Notify adapter for changes
            notifyItemChanged(currentPos)
            notifyItemChanged(selectedFontItemPos)

            //Notify
            itemInterface.itemClicked(widgetFontInfo)
            itemView.postDelayed({ fontsItemInterfaceForSheet.itemClicked(widgetFontInfo) }, 200)
        }

        fun handleFontItemSelection(position: Int){
            //Set the current selected font
            //Set the initial visibilities of selected icons to gone
            binding.normalfontitemselectedbutton.visibility=View.GONE
            binding.italicfontitemselectedbutton.visibility=View.GONE
            binding.mediumfontitemselectedbutton.visibility=View.GONE
            binding.semiboldfontitemselectedbutton.visibility=View.GONE
            binding.boldfontitemselectedbutton.visibility=View.GONE

            if (dataList.get(position).normalInfo!!.sourceName == selectedFont){
                binding.normalfontitemselectedbutton.visibility=View.VISIBLE
                binding.italicfontitemselectedbutton.visibility=View.GONE
                binding.mediumfontitemselectedbutton.visibility=View.GONE
                binding.semiboldfontitemselectedbutton.visibility=View.GONE
                binding.boldfontitemselectedbutton.visibility=View.GONE
                selectedFontItemPos=position
            }

            if (dataList.get(position).lightInfo != null){
                if (dataList.get(position).lightInfo!!.sourceName == selectedFont){
                    binding.normalfontitemselectedbutton.visibility=View.GONE
                    binding.lightFontItemSelectedButton.visibility=View.VISIBLE
                    binding.italicfontitemselectedbutton.visibility=View.GONE
                    binding.mediumfontitemselectedbutton.visibility=View.GONE
                    binding.semiboldfontitemselectedbutton.visibility=View.GONE
                    binding.boldfontitemselectedbutton.visibility=View.GONE
                    binding.extraboldFontItemSelectedButton.visibility=View.GONE

                    selectedFontItemPos=position

                }
            }

            if (dataList.get(position).italicInfo != null){
                if (dataList.get(position).italicInfo!!.sourceName == selectedFont){
                    binding.normalfontitemselectedbutton.visibility=View.GONE
                    binding.lightFontItemSelectedButton.visibility=View.GONE
                    binding.italicfontitemselectedbutton.visibility=View.VISIBLE
                    binding.mediumfontitemselectedbutton.visibility=View.GONE
                    binding.semiboldfontitemselectedbutton.visibility=View.GONE
                    binding.boldfontitemselectedbutton.visibility=View.GONE
                    binding.extraboldFontItemSelectedButton.visibility=View.GONE

                    selectedFontItemPos=position

                }
            }

            if (dataList.get(position).mediumInfo!=null){
                if (dataList.get(position).mediumInfo!!.sourceName == selectedFont){
                    binding.normalfontitemselectedbutton.visibility=View.GONE
                    binding.lightFontItemSelectedButton.visibility=View.GONE
                    binding.italicfontitemselectedbutton.visibility=View.GONE
                    binding.mediumfontitemselectedbutton.visibility=View.VISIBLE
                    binding.semiboldfontitemselectedbutton.visibility=View.GONE
                    binding.boldfontitemselectedbutton.visibility=View.GONE
                    binding.extraboldFontItemSelectedButton.visibility=View.GONE

                    selectedFontItemPos=position

                }
            }

            if (dataList.get(position).semiboldInfo!=null){
                if (dataList.get(position).semiboldInfo!!.sourceName == selectedFont){
                    binding.normalfontitemselectedbutton.visibility=View.GONE
                    binding.lightFontItemSelectedButton.visibility=View.GONE
                    binding.italicfontitemselectedbutton.visibility=View.GONE
                    binding.mediumfontitemselectedbutton.visibility=View.GONE
                    binding.semiboldfontitemselectedbutton.visibility=View.VISIBLE
                    binding.boldfontitemselectedbutton.visibility=View.GONE
                    binding.extraboldFontItemSelectedButton.visibility=View.GONE
                    selectedFontItemPos=position

                }
            }

            if (dataList.get(position).boldInfo!=null){
                if (dataList.get(position).boldInfo!!.sourceName == selectedFont){
                    binding.normalfontitemselectedbutton.visibility=View.GONE
                    binding.lightFontItemSelectedButton.visibility=View.GONE
                    binding.italicfontitemselectedbutton.visibility=View.GONE
                    binding.mediumfontitemselectedbutton.visibility=View.GONE
                    binding.semiboldfontitemselectedbutton.visibility=View.GONE
                    binding.boldfontitemselectedbutton.visibility=View.VISIBLE
                    binding.extraboldFontItemSelectedButton.visibility=View.GONE

                    selectedFontItemPos=position
                }
            }

            if (dataList.get(position).extraBoldInfo != null){
                if (dataList.get(position).boldInfo!!.sourceName == selectedFont){
                    binding.normalfontitemselectedbutton.visibility=View.GONE
                    binding.lightFontItemSelectedButton.visibility=View.GONE
                    binding.italicfontitemselectedbutton.visibility=View.GONE
                    binding.mediumfontitemselectedbutton.visibility=View.GONE
                    binding.semiboldfontitemselectedbutton.visibility=View.GONE
                    binding.boldfontitemselectedbutton.visibility=View.GONE
                    binding.extraboldFontItemSelectedButton.visibility=View.VISIBLE

                    selectedFontItemPos=position
                }
            }
        }

        fun handleFontItemVisibility(position: Int){
            //Set initial visibility of items to GONE
            binding.lightFontItemLayout.visibility=View.GONE
            binding.italicfontitemlayout.visibility=View.GONE
            binding.mediumfontitemlayout.visibility=View.GONE
            binding.semiboldfontitemlayout.visibility=View.GONE
            binding.boldfontitemlayout.visibility=View.GONE
            binding.extraboldFontItemLayout.visibility=View.GONE


            binding.fontitemdiv1.visibility=View.GONE
            binding.fontitemdiv2.visibility=View.GONE
            binding.fontitemdiv3.visibility=View.GONE
            binding.fontitemdiv4.visibility=View.GONE
            binding.fontitemdiv5.visibility=View.GONE
            binding.fontitemdiv6.visibility=View.GONE



            //Set the visibility accordingly
            if (dataList.get(position).lightInfo!=null){

                binding.lightFontItemName.text = dataList[position].lightInfo?.fontName
                binding.lightFontItemNormalName.text = dataList[position].lightInfo?.fontName


                CoroutineScope(Dispatchers.IO).launch {

                    try {
                        val id = context.resources.getIdentifier(dataList[position].lightInfo?.sourceName, "font", context.packageName)

                        val typeFace = ResourcesCompat.getFont(context, id)

                        withContext(Dispatchers.Main){
                            binding.lightFontItemName.typeface = typeFace
                            binding.fontItemLightIcon.typeface = typeFace
                        }

                    }
                    catch (e: Resources.NotFoundException) {
                        e.printStackTrace()
                    }


                }


                binding.lightFontItemLayout.visibility=View.VISIBLE
                binding.fontitemdiv1.visibility=View.VISIBLE

            }
            if (dataList.get(position).italicInfo!=null){

                binding.italicfontitemname.text = dataList[position].italicInfo?.fontName
                binding.italicfontitemnormalname.text = dataList[position].italicInfo?.fontName


                CoroutineScope(Dispatchers.IO).launch {

                    try {
                        val id = context.resources.getIdentifier(dataList[position].italicInfo?.sourceName, "font", context.packageName)

                        val typeFace = ResourcesCompat.getFont(context, id)

                        withContext(Dispatchers.Main){
                            binding.italicfontitemname.typeface = typeFace
                            binding.fontitemitalicicon.typeface = typeFace
                        }

                    }
                    catch (e: Resources.NotFoundException) {
                        Log.e("FOTE", "${dataList[position].italicInfo?.fontName} ")
                        e.printStackTrace()
                    }


                }


                binding.italicfontitemlayout.visibility=View.VISIBLE
                binding.fontitemdiv2.visibility=View.VISIBLE

            }
            if (dataList.get(position).mediumInfo!=null){
                binding.mediumfontitemname.text = dataList.get(position).mediumInfo?.fontName
                binding.mediumfontitemnormalname.text = dataList.get(position).mediumInfo?.fontName


                CoroutineScope(Dispatchers.IO).launch {

                    try {
                        val id = context.resources.getIdentifier(dataList[position].mediumInfo?.sourceName, "font", context.packageName)

                        val typeFace = ResourcesCompat.getFont(context, id)

                        withContext(Dispatchers.Main){
                            binding.mediumfontitemname.typeface = typeFace
                            binding.fontitemmediumicon.typeface = typeFace
                        }

                    }
                    catch (e: Resources.NotFoundException) {
                        Log.e("FOTE", "${dataList[position].mediumInfo?.fontName} ")
                        e.printStackTrace()
                    }


                }


                binding.mediumfontitemlayout.visibility=View.VISIBLE
                binding.fontitemdiv3.visibility=View.VISIBLE
            }
            if (dataList.get(position).semiboldInfo!=null){
                binding.semiboldfontitemname.text = dataList.get(position).semiboldInfo?.fontName
                binding.semiboldfontitemnormalname.text = dataList.get(position).semiboldInfo?.fontName


                CoroutineScope(Dispatchers.IO).launch {

                    try {
                        val id = context.resources.getIdentifier(dataList[position].semiboldInfo?.sourceName, "font", context.packageName)

                        val typeFace = ResourcesCompat.getFont(context, id)

                        withContext(Dispatchers.Main){
                            binding.semiboldfontitemname.typeface = typeFace
                            binding.fontitemsemiboldicon.typeface = typeFace
                        }

                    }
                    catch (e: Resources.NotFoundException) {
                        Log.e("FOTE", "${dataList[position].semiboldInfo?.fontName} ")
                        e.printStackTrace()
                    }


                }



                binding.semiboldfontitemlayout.visibility=View.VISIBLE
                binding.fontitemdiv4.visibility=View.VISIBLE

            }
            if (dataList.get(position).boldInfo!=null){
                binding.boldfontitemname.text = dataList[position].boldInfo?.fontName
                binding.boldfontitemnormalname.text = dataList[position].boldInfo?.fontName


                CoroutineScope(Dispatchers.IO).launch {

                    try {
                        val id = context.resources.getIdentifier(dataList[position].boldInfo?.sourceName, "font", context.packageName)

                        val typeFace = ResourcesCompat.getFont(context, id)

                        withContext(Dispatchers.Main){
                            binding.boldfontitemname.typeface = typeFace
                            binding.fontitemboldicon.typeface = typeFace
                        }

                    }
                    catch (e: Resources.NotFoundException) {
                        Log.e("FOTE", "${dataList[position].boldInfo?.fontName} ")
                        e.printStackTrace()
                    }


                }

                binding.boldfontitemlayout.visibility=View.VISIBLE
                binding.fontitemdiv5.visibility=View.VISIBLE

            }
            if (dataList.get(position).extraBoldInfo !=null){
                binding.extraboldFontItemName.text = dataList[position].extraBoldInfo?.fontName
                binding.extraboldFontItemNormalName.text = dataList[position].extraBoldInfo?.fontName


                CoroutineScope(Dispatchers.IO).launch {

                    try {
                        val id = context.resources.getIdentifier(dataList[position].extraBoldInfo?.sourceName, "font", context.packageName)

                        val typeFace = ResourcesCompat.getFont(context, id)

                        withContext(Dispatchers.Main){
                            binding.extraboldFontItemName.typeface = typeFace
                            binding.fontItemExtraboldIcon.typeface = typeFace
                        }

                    }
                    catch (e: Resources.NotFoundException) {
                        e.printStackTrace()
                    }


                }

                binding.extraboldFontItemLayout.visibility=View.VISIBLE
                binding.fontitemdiv6.visibility=View.VISIBLE

            }

        }

        fun setRoundedCorners(roundCorners : Boolean){
            if (!roundCorners){
                val layoutParamsCompat = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParamsCompat.marginStart=AppUtils.dptopx(context,0)
                layoutParamsCompat.marginEnd=AppUtils.dptopx(context,0)
                layoutParamsCompat.topMargin=AppUtils.dptopx(context,5)
                binding.fontItemCard.layoutParams=layoutParamsCompat

                binding.fontItemCard.radius=0f
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
                when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES ->{
                        darkMode(true)
                    }

                    Configuration.UI_MODE_NIGHT_NO ->{
                        darkMode(false)
                    }
                }
            }
        }

        fun darkMode(isNight : Boolean){
            if (isNight){
                binding.fontItemCard.setCardBackgroundColor(ContextCompat.getColor(context,R.color.darkGrey5))

                binding.fontitemname.setTextColor(ContextCompat.getColor(context,R.color.white))
                binding.fontitemnormalicon.setTextColor(ContextCompat.getColor(context,R.color.purpleLight))

                binding.lightFontItemName.setTextColor(ContextCompat.getColor(context,R.color.white))
                binding.fontItemLightIcon.setTextColor(ContextCompat.getColor(context,R.color.purpleLight))

                binding.italicfontitemname.setTextColor(ContextCompat.getColor(context,R.color.white))
                binding.fontitemitalicicon.setTextColor(ContextCompat.getColor(context,R.color.purpleLight))

                binding.mediumfontitemname.setTextColor(ContextCompat.getColor(context,R.color.white))
                binding.fontitemmediumicon.setTextColor(ContextCompat.getColor(context,R.color.purpleLight))

                binding.semiboldfontitemname.setTextColor(ContextCompat.getColor(context,R.color.white))
                binding.fontitemsemiboldicon.setTextColor(ContextCompat.getColor(context,R.color.purpleLight))

                binding.boldfontitemname.setTextColor(ContextCompat.getColor(context,R.color.white))
                binding.fontitemboldicon.setTextColor(ContextCompat.getColor(context,R.color.purpleLight))

                binding.extraboldFontItemName.setTextColor(ContextCompat.getColor(context,R.color.white))
                binding.fontItemExtraboldIcon.setTextColor(ContextCompat.getColor(context,R.color.purpleLight))



                binding.fontitemdiv1.setBackgroundColor(ContextCompat.getColor(context,R.color.darkGrey4))
                binding.fontitemdiv2.setBackgroundColor(ContextCompat.getColor(context,R.color.darkGrey4))
                binding.fontitemdiv3.setBackgroundColor(ContextCompat.getColor(context,R.color.darkGrey4))
                binding.fontitemdiv4.setBackgroundColor(ContextCompat.getColor(context,R.color.darkGrey4))
                binding.fontitemdiv5.setBackgroundColor(ContextCompat.getColor(context,R.color.darkGrey4))
                binding.fontitemdiv6.setBackgroundColor(ContextCompat.getColor(context,R.color.darkGrey4))


            }
            else{
                binding.fontItemCard.setCardBackgroundColor(ContextCompat.getColor(context,R.color.white))

                binding.fontitemname.setTextColor(ContextCompat.getColor(context,R.color.Black))
                binding.fontitemnormalicon.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))

                binding.lightFontItemName.setTextColor(ContextCompat.getColor(context,R.color.Black))
                binding.fontItemLightIcon.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))


                binding.italicfontitemname.setTextColor(ContextCompat.getColor(context,R.color.Black))
                binding.fontitemitalicicon.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))

                binding.mediumfontitemname.setTextColor(ContextCompat.getColor(context,R.color.Black))
                binding.fontitemmediumicon.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))

                binding.semiboldfontitemname.setTextColor(ContextCompat.getColor(context,R.color.Black))
                binding.fontitemsemiboldicon.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))

                binding.boldfontitemname.setTextColor(ContextCompat.getColor(context,R.color.Black))
                binding.fontitemboldicon.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))

                binding.extraboldFontItemName.setTextColor(ContextCompat.getColor(context,R.color.Black))
                binding.fontItemExtraboldIcon.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))



                binding.fontitemdiv1.setBackgroundColor(ContextCompat.getColor(context,R.color.LightGrey3))
                binding.fontitemdiv2.setBackgroundColor(ContextCompat.getColor(context,R.color.LightGrey3))
                binding.fontitemdiv3.setBackgroundColor(ContextCompat.getColor(context,R.color.LightGrey3))
                binding.fontitemdiv4.setBackgroundColor(ContextCompat.getColor(context,R.color.LightGrey3))
                binding.fontitemdiv5.setBackgroundColor(ContextCompat.getColor(context,R.color.LightGrey3))
                binding.fontitemdiv6.setBackgroundColor(ContextCompat.getColor(context,R.color.LightGrey3))

            }
        }
    }

    fun filterList(filteredList:MutableList<FontItemData>){
        dataList=filteredList
        notifyDataSetChanged()
    }


}