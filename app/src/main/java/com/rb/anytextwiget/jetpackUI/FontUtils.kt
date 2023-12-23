package com.rb.anytextwiget.jetpackUI
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.rb.anytextwiget.R

class FontUtils {
    var provider: GoogleFont.Provider
    init {
         provider = GoogleFont.Provider(
            providerAuthority = "com.google.android.gms.fonts",
            providerPackage = "com.google.android.gms",
            certificates = R.array.com_google_android_gms_fonts_certs
        )
    }

    fun openSans(weight: FontWeight): FontFamily {
        return FontFamily(
            Font(googleFont = GoogleFont("Open Sans"), fontProvider = provider, weight = weight),
        )
    }

    fun poppins(weight: FontWeight): FontFamily {
        return FontFamily(
            Font(googleFont = GoogleFont("Poppins"), fontProvider = provider, weight = weight),
        )
    }

    fun loadFont(familyName: String, weight: FontWeight): FontFamily {
        return FontFamily(
            Font(googleFont = GoogleFont("Open Sans"), fontProvider = provider, weight = weight),
        )
    }
}