package com.rb.anytextwiget

import android.graphics.Bitmap
import android.widget.TextView
import java.io.Serializable
 class WidgetData : Serializable {



    companion object{
        @JvmStatic
        val serialVersionUID = 5819243400423382274L
    }


    var widgetText : String? = null
    var widgetBackGroundType : String = "colorBackground"
    var widgetBackgroundColor : ColorData? = null
    var widgetBackgroundImageUri : String? = null
    var widgetTextColor : ColorData? = null
    var widgetTextSize:Int = 21
    var widgetTextFontID:Int = R.font.open_sans_semibold
    var widgetID : String? = null
    var widgetRoundCorners : Boolean = true
    var widgetFontInfo:WidgetFontInfo? = null
    var widgetMultiImageList:MutableList<String>? = null
    var widgetTextVerticalGravity: TextGravityData? = null
    var widgetTextHorizontalGravity: TextGravityData? = null
    var widgetClickAction: ActionData? = null
    var outlineEnabled: Boolean = false
    var widgetOutlineColor: ColorData? = null
    var widgetOutlineWidth: Int = 3
    var widgetBackgroundGradient: GradientData? = null
    var textShadowEnabled: Boolean = false
    var textShadowData: TextShadowData? = null
    var textPadding: Int = 0
    var textType: String = "input"
    var textTimerDay: Int = 0
    var textTypeTimerMonth: Int = 0
    var textTypeTimerYear: Int = 0
    var textTypeTimerHour: Int = 0
    var textTypeTimerMinute: Int = 0
    var clockTimeZone: String = ""
}