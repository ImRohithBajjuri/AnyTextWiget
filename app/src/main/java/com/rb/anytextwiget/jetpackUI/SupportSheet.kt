package com.rb.anytextwiget.jetpackUI

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.rb.anytextwiget.AppUtils
import com.rb.anytextwiget.HelpInfo
import com.rb.anytextwiget.R

class SupportSheet(var context: AppCompatActivity) {

    lateinit var billingClient: BillingClient
    val fontUtils = FontUtils()


    init {

        val purchaseUpdateListener = PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                completePurchase(purchases[0])
                Toast.makeText(
                    context,
                    "Thank you for your support, it will help the app a lot. I'll do my best to keep the app updated.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        billingClient = BillingClient.newBuilder(context).enablePendingPurchases()
            .setListener(purchaseUpdateListener)
            .build()
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SupportSheetUI(onDismiss: () -> Unit) {

        ModalBottomSheet(onDismissRequest = onDismiss) {
            Text(
                text = "Support the app in your way",
                fontFamily = fontUtils.openSans(FontWeight.Bold),
                fontSize = TextUnit(28f, TextUnitType.Sp),
                modifier = Modifier.padding(20.dp)
            )


            TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                var failCount = 0

                //Start the connection
                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(p0: BillingResult) {
                        if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                            getCoffeeDetails()
                            failCount = 0
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        if (failCount <= 3) {
                            billingClient.startConnection(this)
                        } else {
                            Toast.makeText(
                                context,
                                "Unable to make transaction now, please try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        failCount++
                    }

                })
            }, contentPadding = PaddingValues(15.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_coffee_50),
                    contentDescription = "Buy coffee icon",
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = "Buy me a coffee (1\$)",
                    fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    modifier = Modifier.weight(1f)
                )
            }

            TextButton(modifier = Modifier.fillMaxWidth(), onClick = {

                var failCount = 0

                //Start the connection
                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(p0: BillingResult) {
                        if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                            getSnackDetails()
                            failCount = 0
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        if (failCount <= 3) {
                            billingClient.startConnection(this)
                        } else {
                            Toast.makeText(
                                context,
                                "Unable to make transaction now, please try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        failCount++
                    }

                })
            }, contentPadding = PaddingValues(15.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_fastfood_50),
                    contentDescription = "Buy snack icon",
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = "Buy me a snack (3\$)",
                    fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    modifier = Modifier.weight(1f)
                )
            }

            TextButton(modifier = Modifier.fillMaxWidth(), onClick = {

                var failCount = 0

                //Start the connection
                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(p0: BillingResult) {
                        if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                            getDrinkDetails()
                            failCount = 0
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        if (failCount <= 3) {
                            billingClient.startConnection(this)
                        } else {
                            Toast.makeText(
                                context,
                                "Unable to make transaction now, please try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        failCount++
                    }

                })
            }, contentPadding = PaddingValues(15.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_local_bar_50),
                    contentDescription = "Buy drink icon",
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = "Buy me a drink (5\$)",
                    fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    modifier = Modifier.weight(1f)
                )
            }

            TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                if (!disableAds.value) {
                    val adRequest = AdRequest.Builder().build()

                    InterstitialAd.load(
                        context,
                        context.getString(R.string.interstitialAdOneID),
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(p0: InterstitialAd) {
                                p0.show(context)
                                p0.fullScreenContentCallback =
                                    object : FullScreenContentCallback() {
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
                }

            }, contentPadding = PaddingValues(15.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_tv_50),
                    contentDescription = "Watch Ad icon",
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = "Support by watching Ad",
                    fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    modifier = Modifier.weight(1f)
                )
            }

            //Show ads.
            if (!disableAds.value) {
                AndroidView(factory = { context ->
                    AdView(context).apply {
                        setAdSize(AdSize.BANNER)
                        adUnitId = context.getString(R.string.bannerAdFourId)
                        val adRequest = AdRequest.Builder().build()
                        loadAd(adRequest)
                    }
                })
            }
        }
    }


    fun getCoffeeDetails() {
        /*
                val skuDetailsList = ArrayList<String>()
                skuDetailsList.add("atw_buy_a_coffee_943")

                val params = SkuDetailsParams.newBuilder().setType(BillingClient.SkuType.INAPP).setSkusList(skuDetailsList).build()

                CoroutineScope(Dispatchers.IO).launch {
                    billingClient.querySkuDetailsAsync(params
                    ) { p0, p1 ->
                        if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                            launchPurchase(p1!![0])
                        }
                    }
                }
        */

        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("atw_buy_a_coffee_943")
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult,
                                                                            productDetailsList ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                launchPurchase(productDetailsList[0])
            }
        }
    }

    fun getSnackDetails() {
        /*
                val skuDetailsList = ArrayList<String>()
                skuDetailsList.add("atw_buy_a_snack")

                val params = SkuDetailsParams.newBuilder().setType(BillingClient.SkuType.INAPP).setSkusList(skuDetailsList).build()

                CoroutineScope(Dispatchers.IO).launch {
                    billingClient.querySkuDetailsAsync(params
                    ) { p0, p1 ->
                        if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                            launchPurchase(p1!![0])
                        }
                    }
                }
        */

        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("atw_buy_a_snack")
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult,
                                                                            productDetailsList ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                launchPurchase(productDetailsList[0])
            }
        }
    }

    fun getDrinkDetails() {
        /*  val skuDetailsList = ArrayList<String>()
          skuDetailsList.add("atw_buy_a_drink_943")

          val params = SkuDetailsParams.newBuilder().setType(BillingClient.SkuType.INAPP).setSkusList(skuDetailsList).build()

          CoroutineScope(Dispatchers.IO).launch {
              billingClient.querySkuDetailsAsync(params
              ) { p0, p1 ->
                  if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                      launchPurchase(p1!![0])
                  }
              }
          }*/

        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("atw_buy_a_drink_943")
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult,
                                                                            productDetailsList ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                launchPurchase(productDetailsList[0])
            }
        }
    }

    fun launchPurchase(productDetails: ProductDetails) {
        /*
                val flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
                val responseCode = billingClient.launchBillingFlow((contexT as AppCompatActivity), flowParams).responseCode

                if (responseCode != BillingClient.BillingResponseCode.OK){
                    val responseCode2 = billingClient.launchBillingFlow((contexT as AppCompatActivity), flowParams).responseCode

                    if (responseCode2 != BillingClient.BillingResponseCode.OK){
                        val responseCode3 = billingClient.launchBillingFlow((contexT as AppCompatActivity), flowParams).responseCode

                        if (responseCode3 != BillingClient.BillingResponseCode.OK){
                            Toast.makeText(contexT, "Unable to continue with purchase, please try again later", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        */

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(context, billingFlowParams).responseCode

        if (billingResult != BillingClient.BillingResponseCode.OK) {
            val billingResult2 = billingClient.launchBillingFlow(
                context as AppCompatActivity,
                billingFlowParams
            ).responseCode

            if (billingResult2 != BillingClient.BillingResponseCode.OK) {
                val billingResult3 = billingClient.launchBillingFlow(
                    context as AppCompatActivity,
                    billingFlowParams
                ).responseCode

                if (billingResult3 != BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(
                        context,
                        "Unable to continue with purchase, please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    fun completePurchase(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient.consumeAsync(
            consumeParams
        ) { p0, p1 -> }
    }
}

