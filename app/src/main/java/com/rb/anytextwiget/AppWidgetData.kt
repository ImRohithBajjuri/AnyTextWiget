package com.rb.anytextwiget

import java.io.Serializable

class AppWidgetData : Serializable{
    companion object{
        @JvmStatic
        val serialVersionUID = 7567551317599289605L
    }
    var widgetData:WidgetData?=null
    var ifBackgroundImageBytes:ByteArray?=null
    var ifBackgroundImageBytesList:MutableList<ByteArray>?=null
}