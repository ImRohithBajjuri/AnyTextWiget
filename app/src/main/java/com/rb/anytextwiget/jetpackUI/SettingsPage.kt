package com.rb.anytextwiget.jetpackUI

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import com.rb.anytextwiget.R
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.rb.anytextwiget.AppUtils
import com.rb.anytextwiget.SettingsActivity
import com.rb.anytextwiget.ui.theme.updateCornerRadiiWithPadding

import com.rb.anytextwiget.ui.theme.updateThemePref
import kotlinx.coroutines.launch

class SettingsPage(var context: SettingsActivity) {

    var fontUtils = FontUtils()
    val appPrefs  = context.getSharedPreferences("apppref",
        AppCompatActivity.MODE_PRIVATE
    )
    val appURL = Uri.parse("https://play.google.com/store/apps/details?id=com.rb.anytextwiget")

    val appUtils = AppUtils()

    var supportSheet = SupportSheet(context)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsUI() {
        val snackbarHostState = remember {
            SnackbarHostState()
        }

        val scrollBehaviour =
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())


        var roundedCorners by remember {
            mutableStateOf(appPrefs.getBoolean("roundcorners", true))
        }



        var showAppThemeSheet by remember {
            mutableStateOf(false)
        }

        var currentTheme by remember {
            mutableStateOf(appPrefs.getString("apptheme", "light")!!)
        }

        var appThemeIcon by remember {
            mutableStateOf(R.drawable.ic_round_light_mode_24)
        }

        var showFeedbackDialog by remember {
            mutableStateOf(false)
        }

        var showSupportSheet by remember {
            mutableStateOf(false)
        }

        var showWhatsNewSheet by remember {
            mutableStateOf(false)
        }


        //Set App theme icon.
        when (currentTheme) {
            AppUtils.LIGHT -> appThemeIcon = R.drawable.ic_round_light_mode_24
            AppUtils.DARK -> appThemeIcon = R.drawable.ic_round_dark_mode_24
            AppUtils.FOLLOW_SYSTEM -> appThemeIcon = R.drawable.app_theme_system_50dp
        }

        Scaffold(topBar = {
            //Header.
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontFamily = fontUtils.openSans(FontWeight.Black),
                        fontSize = TextUnit(28f, TextUnitType.Sp),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = { context.finish() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_chevron_left_24),
                            contentDescription = "Close Settings"
                        )
                    }
                },
                scrollBehavior = scrollBehaviour
            )
        }, snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }, containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .nestedScroll(scrollBehaviour.nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(state = rememberScrollState())
                    .padding(0.dp, it.calculateTopPadding(), 0.dp, 0.dp)
            ) {
                Text(
                    text = "UI Options",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(
                        FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(10.dp)
                )

                Card(
                    elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    modifier = Modifier.padding(10.dp, 0.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    //Rounded corners setting.
                    TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                        roundedCorners = !roundedCorners

                        appPrefs.edit().putBoolean("roundcorners", roundedCorners).apply()

                        updateCornerRadiiWithPadding(roundedCorners)

                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_rounded_corner_24),
                            contentDescription = "Enable rounded corners Icon",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(10.dp)
                        )
                        Text(
                            text = "Enable rounded corners in the app",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(FontWeight.Normal),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .weight(1f, fill = true)
                                .align(Alignment.CenterVertically)
                                .padding(0.dp, 10.dp, 10.dp, 10.dp),
                        )
                        Switch(checked = roundedCorners, modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp), onCheckedChange = {isChecked ->
                                roundedCorners = isChecked
                            appPrefs.edit().putBoolean("roundcorners", isChecked).apply()
                        })
                    }


                    //Disable ads setting.
                    TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                        disableAds.value = !disableAds.value
                        appPrefs.edit().putBoolean("disableads", disableAds.value).apply()



                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_do_disturb_alt_50),
                            contentDescription = "Disable Ads Icon",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(10.dp)
                        )
                        Text(
                            text = "Disable ads",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(FontWeight.Normal),
                            modifier = Modifier
                                .weight(1f, fill = true)
                                .align(Alignment.CenterVertically)
                                .padding(0.dp, 10.dp, 10.dp, 10.dp),
                            textAlign = TextAlign.Start
                        )
                        Switch(checked = disableAds.value, modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp), onCheckedChange = {isChecked ->
                            disableAds.value = isChecked
                            appPrefs.edit().putBoolean("disableads", isChecked).apply()
                        })
                    }

                    //App theme setting.
                    TextButton(modifier = Modifier
                        .fillMaxWidth(),
                        onClick = {showAppThemeSheet = true}
                        ) {
                        Icon(
                            painter = painterResource(id = appThemeIcon),
                            contentDescription = "App Theme Icon",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(10.dp)
                        )
                        Column(modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                            .padding(0.dp, 10.dp, 10.dp, 10.dp)) {
                            Text(
                                text = "App theme",
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = currentTheme,
                                fontSize = TextUnit(16f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                        }

                    }

                }

                //Support app button.
                FilledTonalButton(
                    onClick = {
                        val adRequest = AdRequest.Builder().build()

                        InterstitialAd.load(
                            context,
                            context.getString(R.string.interstitialAdOneID),
                            adRequest,
                            object : InterstitialAdLoadCallback() {
                                override fun onAdLoaded(p0: InterstitialAd) {
                                    p0.show(context)
                                    p0.fullScreenContentCallback = object : FullScreenContentCallback() {
                                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                            super.onAdFailedToShowFullScreenContent(p0)
                                        }

                                        override fun onAdShowedFullScreenContent() {
                                        }

                                        override fun onAdDismissedFullScreenContent() {
                                            Toast.makeText(
                                                context,
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
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 20.dp)
                ) {
                    Text(
                        text = "Support app by watching an ad",
                        fontSize = TextUnit(16f, TextUnitType.Sp),
                        fontFamily = fontUtils.openSans(
                            FontWeight.Normal
                        )
                    )
                }

                //About section.
                Text(
                    text = "About",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(
                        FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(10.dp, 20.dp, 0.dp, 10.dp)
                )
                Card(
                    elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    modifier = Modifier.padding(10.dp, 0.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    //Try Crafty.
                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.rb.crafty"))
                        context.startActivity(intent)
                    }, modifier = Modifier.fillMaxWidth(),) {
                        Icon(
                            painter = painterResource(id = R.drawable.crafty_icon_mono),
                            contentDescription = "Crafty Logo",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Text(
                            text = "Try Crafty!",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(
                                FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(0.dp, 10.dp)
                                .align(Alignment.CenterVertically)
                                .weight(1f)

                        )
                    }

                    //Support the app.
                    TextButton(onClick = {showSupportSheet = true}, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_favorite_30),
                            contentDescription = "Support app icon",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Text(
                            text = "Support the app",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(
                                FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(0.dp, 10.dp)
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                        )
                    }

                    //Rate the app.
                    TextButton(onClick = { val intent = Intent(Intent.ACTION_VIEW, appURL)
                        context.startActivity(intent)}, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_star_rate_24),
                            contentDescription = "Rate the app icon",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Text(
                            text = "Rate the app",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(
                                FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(0.dp, 10.dp)
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                        )
                    }

                    //Whats new.
                    TextButton(onClick = {showWhatsNewSheet = true}, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_whatshot_30),
                            contentDescription = "Whats new icon",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Text(
                            text = "What's new",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(
                                FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(0.dp, 10.dp)
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                        )
                    }

                    //Join telegram
                    TextButton(onClick = {
                        val intent = Intent()
                        intent.setAction(Intent.ACTION_VIEW)
                        intent.setData(Uri.parse("https://t.me/anytextwidget"))
                        context.startActivity(intent)
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_telegram_icon),
                            contentDescription = "Join telegram icon",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Text(
                            text = "Join telegram channel",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(
                                FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(0.dp, 10.dp)
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                        )
                    }

                    //Feedback.
                    TextButton(onClick = {showFeedbackDialog = true}, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_feedback_24),
                            contentDescription = "Feedback icon",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Text(
                            text = "Feedback",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(
                                FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(0.dp, 10.dp)
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                        )
                    }


                    //Share the app.
                    TextButton(onClick = {
                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND
                        intent.type = "text/html"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            "Hey, checkout the Any text widget app on Play Store $appURL"
                        )

                        context.startActivity(Intent.createChooser(intent, "Share with :"))
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_share_30),
                            contentDescription = "Share app icon",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Text(
                            text = "Share",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(
                                FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(0.dp, 10.dp)
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                        )
                    }

                    //Open source libraries
                    TextButton(onClick = {
                        context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_article_24),
                            contentDescription = "Open source libraries icon",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Text(
                            text = "Open source libraries",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(
                                FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(0.dp, 10.dp)
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                        )
                    }

                    //Terms of service
                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data =
                            Uri.parse("https://docs.google.com/document/d/e/2PACX-1vTCeAgG3GkISG96d1Cbp9X9djc5URQpBeYGyjVBEmTJanrfqlDvkR-HivMjElFIxM93JtfAE2sxOpoZ/pub")
                        context.startActivity(intent)
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_assignment_24),
                            contentDescription = "Terms of service icon",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Text(
                            text = "Terms of service",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(
                                FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(0.dp, 10.dp)
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                        )
                    }

                    //Privacy policy
                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data =
                            Uri.parse("https://docs.google.com/document/d/e/2PACX-1vSv_t5seI2rfLiy1ZeRYQxW-yv2MfywC_T0Lsq8GjVa4g0Y7lsuQ9wo-jJle0JMkijtphIhaWdVY1L6/pub")
                        context.startActivity(intent)
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_privacy_tip_24),
                            contentDescription = "Privacy policy icon",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Text(
                            text = "Privacy policy",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(
                                FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(0.dp, 10.dp)
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                        )
                    }

                }

                //Show ads.
                if (!disableAds.value) {
                    AndroidView(factory = { context ->
                        AdView(context).apply {
                            setAdSize(AdSize.BANNER)
                            adUnitId = context.getString(R.string.bannerAdTwoID)
                            val adRequest = AdRequest.Builder().build()
                            loadAd(adRequest)
                        }
                    })
                }
            }
        }

        val systemPref = isSystemInDarkTheme()


        //App theme sheet.
        if (showAppThemeSheet) {
            AppThemeSheet(currentTheme = currentTheme, selectedTheme = {
                appPrefs.edit().putString("apptheme",
                it).apply()

                currentTheme = it
              updateThemePref(it, systemPref)
            }) {
                showAppThemeSheet = false
            }
        }

        //Feedback dialog.
        if (showFeedbackDialog) {
            appUtils.BuildAlertDialog(
                title = "Give us your feedback",
                description = "Please give us your feedback on your app experience to our email. This will help us improve the app.",
                confirmEvent = {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.type = "plain/text"
                    intent.data = Uri.fromParts("mailto", "rebootingbrains@gmail.com", null)
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("rebootingbrains@gmail.com"))
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
                    intent.putExtra(Intent.EXTRA_TEXT, "Body")
                    context.startActivity(Intent.createChooser(intent, "Please select a mail app,"))

                    showFeedbackDialog = false
                },
                dismissEvent = { showFeedbackDialog = false }) {

            }
        }

        //Support sheet.
        if (showSupportSheet) {
            supportSheet.SupportSheetUI {
                showSupportSheet = false
            }
        }

        //Whats new sheet.
        if (showWhatsNewSheet) {
            WhatsNewSheet(context = context) {
                showWhatsNewSheet = false
            }
        }
    }
}