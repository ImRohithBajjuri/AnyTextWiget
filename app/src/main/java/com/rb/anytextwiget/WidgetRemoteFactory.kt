package com.rb.anytextwiget

import android.content.Context
import android.graphics.*
import android.media.Image
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.drawToBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WidgetRemoteFactory(): RemoteViewsService.RemoteViewsFactory {

    lateinit var context:Context
    lateinit var dataList: MutableList<String>
    var sampleSize:Int=3

    constructor(context: Context, dataList: MutableList<String>, sampleSize: Int) : this() {
        this.context = context
        this.dataList = dataList
        this.sampleSize=sampleSize
    }


    override fun onCreate() {
    }

    override fun onDataSetChanged() {

    }

    override fun onDestroy() {
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val views=RemoteViews(context.packageName, R.layout.multi_image_item)

        try {
            val bitmap=AppUtils.getBitmapWithContentPath(context, dataList.get(position), sampleSize)

            views.setImageViewBitmap(R.id.multiImage, bitmap)

        }
        catch (e: Exception){


            e.printStackTrace()
        }

        return views
    }

    override fun getLoadingView(): RemoteViews {
        val views=RemoteViews(context.packageName, R.layout.multi_image_item)
        return views
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    fun getRoundBitmap(bitmap: Bitmap): Bitmap{
        //Must be always done in this order!
        val output=Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val canvas=Canvas(output)

        paint.isAntiAlias=true

        canvas.drawARGB(0, 0, 0, 0)
        paint.color = 0xff424242.toInt()
        paint.style = Paint.Style.FILL_AND_STROKE

        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF= RectF(rect)

        canvas.drawRoundRect(rectF, AppUtils.dptopx(context, 15).toFloat(), AppUtils.dptopx(context, 15).toFloat(), paint)


        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        canvas.drawBitmap(bitmap, rect, rectF, paint)

        return output
    }






}