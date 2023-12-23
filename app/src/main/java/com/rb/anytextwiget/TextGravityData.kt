package com.rb.anytextwiget

import java.io.Serializable

class TextGravityData() : Serializable {
    lateinit var gravityName: String
    var gravityValue: Int = 0

    constructor(gravityName: String, gravityValue: Int): this() {
        this.gravityName = gravityName
        this.gravityValue = gravityValue
    }
}