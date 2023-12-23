package com.rb.anytextwiget

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rb.anytextwiget.databinding.GradientItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GradientAdapter() : RecyclerView.Adapter<GradientAdapter.ViewHolder>() {
    lateinit var context: Context
    lateinit var dataList: MutableList<GradientData>
    lateinit var listener: GradientsSheet.GradientsListener
    lateinit var listenerFromSheet: GradientsSheet.GradientsListener
    var currentPosition: Int = 0

    constructor(context: Context, dataList: MutableList<GradientData>, currentPosition: Int, listener: GradientsSheet.GradientsListener, listenerFromSheet: GradientsSheet.GradientsListener) : this() {
        this.context = context
        this.dataList = dataList
        this.listener = listener
        this.currentPosition = currentPosition
        this.listenerFromSheet = listenerFromSheet
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GradientItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.gradientItemName.text = dataList[position].name
        holder.binding.gradientItemChildOne.text = dataList[position].colorOne
        holder.binding.gradientItemChildTwo.text = dataList[position].colorTwo

        //Set the child hex images
        try {
            val drawableOne = ContextCompat.getDrawable(context, R.drawable.ic_baseline_lens_16)
            DrawableCompat.wrap(drawableOne!!)
            DrawableCompat.setTint(drawableOne, Color.parseColor(dataList[position].colorOne))

            val drawableTwo = ContextCompat.getDrawable(context, R.drawable.ic_baseline_lens_16)
            DrawableCompat.wrap(drawableTwo!!)
            DrawableCompat.setTint(drawableTwo, Color.parseColor(dataList[position].colorTwo))

            holder.binding.gradientItemChildOne.setCompoundDrawablesWithIntrinsicBounds(drawableOne, null, null, null)
            holder.binding.gradientItemChildTwo.setCompoundDrawablesWithIntrinsicBounds(drawableTwo, null, null, null)


        }
        catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        //Get the gradient
        CoroutineScope(Dispatchers.IO).launch {
            val sourceName = "no_corners_" + dataList[position].sourceName

            val gradient = context.resources.getIdentifier(sourceName, "drawable", "com.rb.anytextwiget")
            withContext(Dispatchers.Main) {
                Glide.with(context).load(ContextCompat.getDrawable(context, gradient)).circleCrop().into(holder.binding.gradientItemImg)
            }
        }

        //Set the selected icon
        holder.binding.gradientItemSelected.visibility = View.GONE
        if (position == currentPosition) {
            holder.binding.gradientItemSelected.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: GradientItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: GradientItemBinding = itemView

        init {
            val sharedPreferences=context.getSharedPreferences("apppref", Context.MODE_PRIVATE)

            //Adjust UI with theme
            adjustTheme(sharedPreferences.getString("apptheme",AppUtils.LIGHT)!!)


            binding.root.setOnClickListener {

              /*  //Get the current selected item pos
                val currentPos = currentPosition

                //Update the selected item pos
                currentPosition = adapterPosition

                //Notify the adapter for changes
                notifyItemChanged(currentPos)
                notifyItemChanged(currentPosition)
*/
                //Notify
                listener.gradientSelected(dataList[adapterPosition])

                listenerFromSheet.gradientSelected(dataList[adapterPosition])
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
                binding.gradientItemName.setTextColor(ContextCompat.getColor(context,R.color.white))
            }
            else{
                binding.gradientItemName.setTextColor(ContextCompat.getColor(context,R.color.Black))
            }
        }
    }


}