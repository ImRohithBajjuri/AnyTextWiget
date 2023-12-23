package com.rb.anytextwiget

import java.io.Serializable

class GradientData() : Serializable {

    companion object{
        @JvmStatic
        val serialVersionUID = 5819243400423382543L
    }

    constructor(name: String, sourceName: String, colorOne: String, colorTwo: String): this() {
        this.name = name
        this.sourceName = sourceName
        this.colorOne = colorOne
        this.colorTwo = colorTwo
    }

    var name : String = "Gradient"
    var sourceName : String = "no_corners_gradient_pink_purple"
    var colorOne : String = "Color one hex"
    var colorTwo : String = "Color two hex"
}