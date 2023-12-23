package com.rb.anytextwiget

import java.io.Serializable

class FontItemData(): Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = -5904871368741676949L
    }

    var normalInfo: WidgetFontInfo? = null
    var lightInfo: WidgetFontInfo? =null
    var italicInfo: WidgetFontInfo?= null
    var mediumInfo: WidgetFontInfo?= null
    var semiboldInfo: WidgetFontInfo?= null
    var boldInfo: WidgetFontInfo?= null
    var extraBoldInfo: WidgetFontInfo? = null


    //Old constructor
    constructor(normalInfo: WidgetFontInfo?, italicInfo: WidgetFontInfo?, mediumInfo: WidgetFontInfo?, semiboldInfo: WidgetFontInfo?,  boldInfo: WidgetFontInfo?): this(){
        this.normalInfo = normalInfo
        this.italicInfo = italicInfo
        this.mediumInfo = mediumInfo
        this.semiboldInfo = semiboldInfo
        this.boldInfo = boldInfo

    }

    //New constructor
    constructor(normalInfo: WidgetFontInfo?, lightInfo: WidgetFontInfo?, italicInfo: WidgetFontInfo?, mediumInfo: WidgetFontInfo?, semiboldInfo: WidgetFontInfo?,  boldInfo: WidgetFontInfo?, extraBoldInfo: WidgetFontInfo?): this() {
        this.normalInfo = normalInfo
        this.lightInfo = lightInfo
        this.italicInfo = italicInfo
        this.mediumInfo = mediumInfo
        this.semiboldInfo = semiboldInfo
        this.boldInfo = boldInfo
        this.extraBoldInfo = extraBoldInfo

    }


}