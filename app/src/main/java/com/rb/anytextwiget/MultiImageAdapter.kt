package com.rb.anytextwiget

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.rb.anytextwiget.databinding.MultiImageItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOError
import java.io.IOException


class MultiImageAdapter() : BaseAdapter() {
    lateinit var context:Context
    lateinit var dataList:MutableList<String>
    lateinit var from:String
     var byteDataList:MutableList<ByteArray>?=null

    companion object{
        val fromWidgetAdapter="widgetAdapter"
        val fromCreateWidgetActivity="createWidgetActivity"
        val fromSetWidget="setWidget"
        val fromAddWidgetDialog="addWidgetDialog"
    }

    interface MultiImageInterface{
        fun imageRemoved(position: Int)
    }

    var multiImageInterface:MultiImageAdapter.MultiImageInterface?=null

    constructor(context: Context, dataList: MutableList<String>,multiImageInterface: MultiImageInterface?,from:String,byteDataList:MutableList<ByteArray>?) : this() {
        this.context = context
        this.dataList = dataList
        this.multiImageInterface=multiImageInterface
        this.from=from
        this.byteDataList=byteDataList
    }


    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var rootView: MultiImageItemBinding = MultiImageItemBinding.inflate(LayoutInflater.from(parent!!.context), parent, false)
        if (!AppUtils.hasStoragePermission(context)) {
            return LayoutInflater.from(context).inflate(R.layout.multi_image_item,parent,false)
        }

        if (position!=dataList.size){
        /*    if (rootView==null){

            }*/


            rootView=
                MultiImageItemBinding.inflate(LayoutInflater.from(parent!!.context),parent,false)

            rootView.multiImageRemoveButton.visibility=View.GONE
            if (from == fromCreateWidgetActivity){
                rootView.multiImageRemoveButton.visibility=View.VISIBLE
            }

            if (from == fromAddWidgetDialog){

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        var bitmap: Bitmap?
                        if (AppUtils.contentExists(dataList.get(position),context)){
                            bitmap=AppUtils.getBitmapWithContentPath(context,
                                dataList[position],2)
                        }
                        else{
                            bitmap= BitmapFactory.decodeByteArray(byteDataList!!.get(position),0,byteDataList!!.get(position).size)
                        }
                        withContext(Dispatchers.Main){
                            rootView!!.multiImage.setImageBitmap(bitmap)
                        }
                    }
                    catch (e:IOException){
                        e.printStackTrace()
                    }
                    catch (e:OutOfMemoryError){
                        e.printStackTrace()
                    }
                    catch (e:IndexOutOfBoundsException){
                        e.printStackTrace()
                    }
                }
            }
            else{
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val bitmap=AppUtils.getBitmapWithContentPath(context,dataList.get(position),2)
                        withContext(Dispatchers.Main){
                            rootView.multiImage.setImageBitmap(bitmap)
                        }
                    }
                    catch (e:IOException){

                        e.printStackTrace()
                    }
                }
            }

            rootView.multiImageRemoveButton.setOnClickListener {
                val anim=AnimUtils.blinkAnim(object : Animation.AnimationListener{
                    override fun onAnimationStart(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        dataList.removeAt(position)
                        notifyDataSetChanged()
                        if (from== fromCreateWidgetActivity){
                            if (multiImageInterface!=null){
                                multiImageInterface!!.imageRemoved(position)
                            }
                        }

                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })

                it.startAnimation(anim)
            }

            return rootView.root
        }
        return rootView.root
    }

}