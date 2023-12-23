package com.rb.anytextwiget

import java.io.Serializable

class TextShadowData: Serializable {
    var shadowRadius: Int = 5
    var horizontalDir: Int = 0
    var verticalDir: Int = 0
    var shadowColor: ColorData? = null
}