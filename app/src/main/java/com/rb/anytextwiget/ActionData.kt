package com.rb.anytextwiget

import java.io.Serializable

class ActionData: Serializable {
    companion object{
        @JvmStatic
        val serialVersionUID = 7652694978376793968L
    }
    lateinit var actionName: String
    lateinit var appPackageName: String
    lateinit var actionType: String
    var actionExtra: String = ""
}