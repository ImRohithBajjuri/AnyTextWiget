package com.rb.anytextwiget

import java.io.Serializable

class UIFontData: Serializable {
    var name:String?=null
    var id:Int=0

    constructor(name: String?, id: Int) {
        this.name = name
        this.id = id
    }
}