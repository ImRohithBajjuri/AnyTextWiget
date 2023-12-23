package com.rb.anytextwiget

import java.io.Serializable

class WidgetFontInfo() : Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = -1697467181928067648L
    }

    lateinit var fontName:String
    lateinit var fontStyle:String
    var sourceName: String = "NA"

    constructor(fontName: String, fontStyle: String, sourceName: String): this() {
        this.fontName = fontName
        this.fontStyle = fontStyle
        this.sourceName = sourceName
    }

}