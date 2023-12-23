package com.rb.anytextwiget

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetRemoteService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        val dataList=intent!!.getStringArrayListExtra("imageList")
        val sampleSize=intent.getIntExtra("sampleSize",1)
        return WidgetRemoteFactory(applicationContext,dataList!!,sampleSize)
    }
}