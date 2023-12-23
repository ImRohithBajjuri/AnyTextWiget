package com.rb.anytextwiget

import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation

class AnimUtils {
    companion object{
        @JvmStatic
        fun pressAnim(listener: Animation.AnimationListener?): Animation? {
            val press: Animation = ScaleAnimation(1f, 0.8f, 1f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            press.duration = 250
            press.repeatCount = 1
            press.repeatMode = Animation.REVERSE
            press.interpolator = AccelerateDecelerateInterpolator()
            if (listener != null) {
                press.setAnimationListener(listener)
            }
            return press
        }

        @JvmStatic
        fun blinkAnim(listener: Animation.AnimationListener?): Animation? {
            val blink: Animation = AlphaAnimation(1f, 0f)
            blink.duration = 150
            blink.repeatCount = 1
            blink.repeatMode = Animation.REVERSE
            if (listener != null) {
                blink.setAnimationListener(listener)
            }
            return blink
        }
    }

}