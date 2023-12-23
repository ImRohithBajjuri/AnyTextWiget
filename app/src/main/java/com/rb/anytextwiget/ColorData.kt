package com.rb.anytextwiget

import java.io.Serializable

class ColorData : Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 7652694978376793764L
    }

    var colorName:String?=null
    var colorHexCode:String?=null
    var ID:String?=null
}