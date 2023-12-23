package com.rb.anytextwiget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.rb.anytextwiget.databinding.ActivitySettingsBinding
import com.rb.anytextwiget.jetpackUI.MainPage
import com.rb.anytextwiget.jetpackUI.SettingsPage
import com.rb.anytextwiget.ui.theme.AnyTextWigetTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


class SettingsActivity : AppCompatActivity(), AppThemeSelectionSheet.ThemeSelectionInterface {
    lateinit var sharedPreferences: SharedPreferences

    var materialYou: Boolean = false

    lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("apppref", MODE_PRIVATE)
        if (sharedPreferences.getString("apptheme", AppUtils.LIGHT)!! == AppUtils.LIGHT) {
            setTheme(R.style.AppTheme)
        }
        if (sharedPreferences.getString("apptheme", AppUtils.LIGHT)!! == AppUtils.DARK) {
            setTheme(R.style.AppThemeDark)
        }
        if (sharedPreferences.getString("apptheme", AppUtils.LIGHT)!! == AppUtils.FOLLOW_SYSTEM) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    setTheme(R.style.AppThemeDark)
                }

                Configuration.UI_MODE_NIGHT_NO -> {
                    setTheme(R.style.AppTheme)
                }
            }
        }

        binding = ActivitySettingsBinding.inflate(LayoutInflater.from(this))

        val isJetpackUI = true
        if (isJetpackUI) {
            val settingsPage = SettingsPage(context = this)
            setContent {
                AnyTextWigetTheme {
                    settingsPage.SettingsUI()
                }
            }
            return
        }
        else {
            setContentView(binding.root)
        }

        //Control ads
        setAds()

        binding.settingstoolbar.setNavigationOnClickListener {
            finish()
        }

        val roundCorners = sharedPreferences.getBoolean("roundcorners", true)

        val disableAds = sharedPreferences.getBoolean("disableads", false)

        materialYou = sharedPreferences.getBoolean("materialyou", false)

        binding.approundcornersswitch.isChecked = roundCorners

        setRoundCorners(roundCorners)

        binding.disableAdsSwitch.isChecked = disableAds


        val appTheme = sharedPreferences.getString("apptheme", AppUtils.LIGHT)

        if (appTheme == AppUtils.LIGHT) {
            binding.appThemeSelectedPref.text = "Light"
            darkMode(false)
        }
        if (appTheme == AppUtils.DARK) {
            binding.appThemeSelectedPref.text = "Dark"
            darkMode(true)
        }
        if (appTheme == AppUtils.FOLLOW_SYSTEM) {
            binding.appThemeSelectedPref.text = "Follow system"
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> darkMode(true)

                Configuration.UI_MODE_NIGHT_NO -> darkMode(false)
            }
        }

        setSupportWatchAd()


        val appurl = Uri.parse("https://play.google.com/store/apps/details?id=com.rb.anytextwiget")

        binding.aboutRate.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, appurl)
            startActivity(intent)
        }

        binding.aboutfeedback.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Give us your feedback")
            builder.setMessage("Please give us your feedback on your app experience to our email. This will help us improve the app.")
            builder.setNegativeButton(
                "Nope"
            ) { dialog, which ->
                //Do nothing.
            }
            builder.setPositiveButton(
                "Of course"
            ) { dialog, which ->
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.type = "plain/text"
                intent.data = Uri.fromParts("mailto", "rebootingbrains@gmail.com", null)
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("rebootingbrains@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
                intent.putExtra(Intent.EXTRA_TEXT, "Body")
                startActivity(Intent.createChooser(intent, "Please select a mail app,"))

            }
            builder.show()
        }

        binding.aboutshare.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/html"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Hey, checkout the Any text widget app on Play Store $appurl"
            )

            startActivity(Intent.createChooser(intent, "Share with :"))
        }

        /*
                aboutoss.setOnClickListener {
                    val intent = Intent(this, OssLicensesMenuActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(
                        R.anim.activity_open,
                        R.anim.activity_close
                    )
                }
        */

        binding.abouttos.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("https://docs.google.com/document/d/e/2PACX-1vTCeAgG3GkISG96d1Cbp9X9djc5URQpBeYGyjVBEmTJanrfqlDvkR-HivMjElFIxM93JtfAE2sxOpoZ/pub")
            startActivity(intent)
            overridePendingTransition(R.anim.activity_open, R.anim.activity_stable)
        }

        binding.aboutprivacy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("https://docs.google.com/document/d/e/2PACX-1vSv_t5seI2rfLiy1ZeRYQxW-yv2MfywC_T0Lsq8GjVa4g0Y7lsuQ9wo-jJle0JMkijtphIhaWdVY1L6/pub")
            startActivity(intent)
            overridePendingTransition(R.anim.activity_open, R.anim.activity_stable)
        }

        binding.approundcornersswitch.setOnCheckedChangeListener { compoundButton, b ->
            sharedPreferences.edit().putBoolean("roundcorners", b).apply()

            //Update the UI
            setRoundCorners(b)
        }

        binding.appThemePrefLayout.setOnClickListener {
            val appThemeSelectionSheet = AppThemeSelectionSheet(
                sharedPreferences.getString("apptheme", AppUtils.LIGHT)!!,
                this
            )
            appThemeSelectionSheet.show(supportFragmentManager, "useCaseOne")
        }

        binding.disableAdsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            sharedPreferences.edit().putBoolean("disableads", isChecked).apply()

            //Control ads
            setAds()

            if (isChecked) {
                askWatchAd()
            }
        }

        binding.aboutSupport.setOnClickListener {
            val supportOptionsSheet = SupportAppOptionsSheet()
            supportOptionsSheet.show(supportFragmentManager, "useCaseOne")
        }

        binding.aboutWhatsNew.setOnClickListener {
            val newReleasesSheet = NewReleasesSheet()
            newReleasesSheet.show(supportFragmentManager, "useCaseTwo")
        }

        binding.aboutTg.setOnClickListener {
            val intent = Intent()
            intent.setAction(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://t.me/anytextwidget"))
            startActivity(intent)
        }

        binding.settingWatchAdButton.setOnClickListener {
            val anim = AnimUtils.pressAnim(null)
            it.startAnimation(anim)

            val adRequest = AdRequest.Builder().build()

            InterstitialAd.load(
                this,
                getString(R.string.interstitialAdOneID),
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(p0: InterstitialAd) {
                        p0.show(this@SettingsActivity)
                        p0.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                super.onAdFailedToShowFullScreenContent(p0)
                            }

                            override fun onAdShowedFullScreenContent() {
                            }

                            override fun onAdDismissedFullScreenContent() {
                                binding.settingWatchAdButton.visibility = View.GONE
                                sharedPreferences.edit().putBoolean("supportAdViewed2", true)
                                    .apply()


                                Toast.makeText(
                                    this@SettingsActivity,
                                    "Thank you for supporting the app!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                super.onAdDismissedFullScreenContent()
                            }

                            override fun onAdImpression() {
                                super.onAdImpression()
                            }
                        }
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                    }
                })
        }

       binding.aboutCrafty.setOnClickListener {
           val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.rb.crafty"))
           startActivity(intent)
       }

    }

    override fun finish() {
        setResult(RESULT_OK)
        super.finish()
        overridePendingTransition(R.anim.activity_stable, R.anim.activity_close)
    }

    override fun themeSlected(selectedTheme: String) {
        binding.appThemeSelectedPref.text = selectedTheme

        if (selectedTheme == AppUtils.LIGHT) {
            binding.appThemeSelectedPref.text = "Light"
            darkMode(false)
        }
        if (selectedTheme == AppUtils.DARK) {
            binding.appThemeSelectedPref.text = "Dark"
            darkMode(true)
        }
        if (selectedTheme == AppUtils.FOLLOW_SYSTEM) {
            binding.appThemeSelectedPref.text = "Follow system"
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> darkMode(true)

                Configuration.UI_MODE_NIGHT_NO -> darkMode(false)
            }
        }
    }

    fun setRoundCorners(roundCorners: Boolean) {
        if (roundCorners) {
            binding.aboutcard.radius = AppUtils.dptopx(this, 30).toFloat()
            binding.appUIOptionscard.radius = AppUtils.dptopx(this, 30).toFloat()

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(
                AppUtils.dptopx(this, 10), AppUtils.dptopx(this, 5), AppUtils.dptopx(
                    this,
                    10
                ), AppUtils.dptopx(this, 10)
            )

            binding.aboutcard.layoutParams = layoutParams
            binding.appUIOptionscard.layoutParams = layoutParams

        } else {
            binding.aboutcard.radius = 0f
            binding.appUIOptionscard.radius = 0f

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, AppUtils.dptopx(this, 5), 0, AppUtils.dptopx(this, 10))

            binding.aboutcard.layoutParams = layoutParams
            binding.appUIOptionscard.layoutParams = layoutParams
        }
    }

    fun darkMode(isNight: Boolean) {
        if (isNight) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
            binding.settingstoolbar.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryDark
                )
            )

            binding.settingsParent.setBackgroundColor(ContextCompat.getColor(this, R.color.Black))
            binding.appUIOptionscard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkGrey
                )
            )
            binding.aboutcard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey))
            binding.appThemePrefImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_dark_mode_24
                )
            )
            binding.appRoundCornersSwitchImage.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    R.color.purpleLight
                )
            )

            binding.disableAdsSwitchImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purpleLight))

            binding.aboutSupport.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_favorite_dark_30,
                0,
                0,
                0
            )

            binding.aboutRate.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_star_rate_dark,
                0,
                0,
                0
            )
            binding.aboutfeedback.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_feedback_dark_mode,
                0,
                0,
                0
            )
            binding.aboutshare.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_share_dark_mode,
                0,
                0,
                0
            )
            binding.aboutoss.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_article_dark_mode,
                0,
                0,
                0
            )
            binding.abouttos.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_assignment_dark_mode,
                0,
                0,
                0
            )
            binding.aboutprivacy.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_privacy_tip_dark_mode,
                0,
                0,
                0
            )
            binding.aboutWhatsNew.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_whatshot_dark,
                0,
                0,
                0
            )
            binding.aboutTg.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_telegram_icon_dark,
                0,
                0,
                0
            )

            binding.approundcornersswitch.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.appThemePrefTitle.setTextColor(ContextCompat.getColor(this, R.color.white))

            binding.disableAdsSwitch.setTextColor(ContextCompat.getColor(this, R.color.white))

            binding.aboutSupport.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.aboutRate.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.aboutfeedback.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.aboutshare.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.aboutoss.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.abouttos.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.aboutprivacy.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.aboutWhatsNew.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.aboutTg.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.aboutCrafty.setTextColor(ContextCompat.getColor(this, R.color.white))




            binding.settingsDiv1.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkGrey3))
            binding.settingsDiv2.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkGrey3))
            binding.settingsDiv3.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkGrey3))
            binding.settingsDiv4.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkGrey3))
            binding.settingsDiv5.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkGrey3))
            binding.settingsDiv6.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkGrey3))
            binding.settingsDiv7.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkGrey3))
            binding.settingsDiv8.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkGrey3))
            binding.settingsDiv9.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkGrey3))
            binding.settingsDiv10.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkGrey3))
            binding.settingsDiv12.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkGrey3))

        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
            binding.settingstoolbar.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )

            binding.settingsParent.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.LightGrey3
                )
            )
            binding.appUIOptionscard.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding.aboutcard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.appThemePrefImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_light_mode_24
                )
            )
            binding.appRoundCornersSwitchImage.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )

            binding.disableAdsSwitchImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))

            binding.aboutSupport.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_favorite_30,
                0,
                0,
                0
            )

            binding.aboutRate.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_star_rate_24,
                0,
                0,
                0
            )
            binding.aboutfeedback.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_feedback_24,
                0,
                0,
                0
            )
            binding.aboutshare.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_share_30,
                0,
                0,
                0
            )
            binding.aboutoss.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_article_24,
                0,
                0,
                0
            )
            binding.abouttos.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_assignment_24,
                0,
                0,
                0
            )
            binding.aboutprivacy.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_privacy_tip_24,
                0,
                0,
                0
            )
            binding.aboutWhatsNew.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_round_whatshot_30,
                0,
                0,
                0
            )
            binding.aboutTg.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_telegram_icon,
                0,
                0,
                0
            )

            binding.approundcornersswitch.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.appThemePrefTitle.setTextColor(ContextCompat.getColor(this, R.color.Black))

            binding.disableAdsSwitch.setTextColor(ContextCompat.getColor(this, R.color.Black))


            binding.aboutSupport.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.aboutRate.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.aboutfeedback.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.aboutshare.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.aboutoss.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.abouttos.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.aboutprivacy.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.aboutWhatsNew.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.aboutTg.setTextColor(ContextCompat.getColor(this, R.color.Black))
            binding.aboutCrafty.setTextColor(ContextCompat.getColor(this, R.color.Black))



            binding.settingsDiv1.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey3))
            binding.settingsDiv2.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey3))
            binding.settingsDiv3.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey3))
            binding.settingsDiv4.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey3))
            binding.settingsDiv5.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey3))
            binding.settingsDiv6.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey3))
            binding.settingsDiv7.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey3))
            binding.settingsDiv8.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey3))
            binding.settingsDiv9.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey3))
            binding.settingsDiv10.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey3))
            binding.settingsDiv12.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.LightGrey3))
        }
    }

    fun setAds() {
        if (!sharedPreferences.getBoolean("disableads", false)) {
            MobileAds.initialize(this) {
                val adRequest = AdRequest.Builder().build()
                binding.bannerAdTwo.loadAd(adRequest)
            }

            binding.bannerAdTwo.visibility = View.VISIBLE
        } else {
            binding.bannerAdTwo.visibility = View.GONE
        }
    }

    fun setSupportWatchAd() {
        val isDone = sharedPreferences.getBoolean("supportAdViewed2", false)
        if (!isDone) {
            binding.settingWatchAdButton.visibility = View.VISIBLE
        } else {
            binding.settingWatchAdButton.visibility = View.GONE
        }
    }

    fun askWatchAd()  {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Support the app")
        builder.setMessage("Ads help supporting the app, would you like to watch an ad before turning off?")
        builder.setNegativeButton(
            "Nope"
        ) { dialog, which ->
            //Do nothing.
        }
        builder.setPositiveButton(
            "Sure"
        ) { dialog, which ->
            val adRequest = AdRequest.Builder().build()

            InterstitialAd.load(
                this,
                getString(R.string.interstitialAdOneID),
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(p0: InterstitialAd) {
                        p0.show(this@SettingsActivity)
                        p0.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                super.onAdFailedToShowFullScreenContent(p0)
                            }

                            override fun onAdShowedFullScreenContent() {
                            }

                            override fun onAdDismissedFullScreenContent() {
                                binding.settingWatchAdButton.visibility = View.GONE
                                sharedPreferences.edit().putBoolean("supportAdViewed2", true)
                                    .apply()


                                Toast.makeText(
                                    this@SettingsActivity,
                                    "Thank you for supporting the app!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                super.onAdDismissedFullScreenContent()
                            }

                            override fun onAdImpression() {
                                super.onAdImpression()
                            }
                        }
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                    }
                })
        }
        builder.show()
    }


}