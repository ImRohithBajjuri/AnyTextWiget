package com.rb.anytextwiget

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.billingclient.api.*
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.rb.anytextwiget.databinding.FragmentSupportAppOptionsSheetBinding
import kotlinx.coroutines.*

class SupportAppOptionsSheet : BottomSheetDialogFragment() {
    lateinit var contexT: Context
    lateinit var binding: FragmentSupportAppOptionsSheetBinding

    lateinit var billingClient:BillingClient

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        contexT = requireActivity()
        val sharedPreferences=contexT.getSharedPreferences("apppref", Context.MODE_PRIVATE)
        val roundCorners=sharedPreferences.getBoolean("roundcorners", true)
        val appTheme=sharedPreferences.getString("apptheme",AppUtils.LIGHT)

        if (appTheme == AppUtils.LIGHT){
            adjustSheetStyle(false,roundCorners)
        }
        if (appTheme == AppUtils.DARK){
            adjustSheetStyle(true,roundCorners)
        }
        if (appTheme == AppUtils.FOLLOW_SYSTEM){
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> adjustSheetStyle(true,roundCorners)

                Configuration.UI_MODE_NIGHT_NO ->  adjustSheetStyle(false,roundCorners)
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contexT = requireActivity()

        // Inflate the layout for this fragment
        binding = FragmentSupportAppOptionsSheetBinding.inflate(inflater, container, false)

        val themePreferences=contexT.getSharedPreferences("apppref", Context.MODE_PRIVATE)

        //Adjust UI with theme
        adjustTheme(themePreferences.getString("apptheme",AppUtils.LIGHT)!!)

        setAds()

        val purchaseUpdateListener = PurchasesUpdatedListener{ billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                completePurchase(purchases[0])
                Toast.makeText(contexT, "Thank you for your support, it will help the app a lot. I'll do my best to keep the app updated.", Toast.LENGTH_LONG).show()
            }
        }

        //Initialize the billing client
        billingClient = BillingClient.newBuilder(contexT).enablePendingPurchases()
            .setListener(purchaseUpdateListener)
            .build()

        binding.buyCoffeeText.setOnClickListener {
            var failCount = 0

            //Start the connection
            billingClient.startConnection(object : BillingClientStateListener{
                override fun onBillingSetupFinished(p0: BillingResult) {
                    if (p0.responseCode == BillingClient.BillingResponseCode.OK){
                        getCoffeeDetails()
                        failCount = 0
                    }
                }

                override fun onBillingServiceDisconnected() {
                    if (failCount <= 3){
                        billingClient.startConnection(this)
                    }
                    else{
                        Toast.makeText(contexT, "Unable to make transaction now, please try again later", Toast.LENGTH_SHORT).show()
                    }
                    failCount++
                }

            })
        }

        binding.buySnackText.setOnClickListener {
            var failCount = 0

            //Start the connection
            billingClient.startConnection(object : BillingClientStateListener{
                override fun onBillingSetupFinished(p0: BillingResult) {
                    if (p0.responseCode == BillingClient.BillingResponseCode.OK){
                        getSnackDetails()
                        failCount = 0
                    }
                }

                override fun onBillingServiceDisconnected() {
                    if (failCount <= 3){
                        billingClient.startConnection(this)
                    }
                    else{
                        Toast.makeText(contexT, "Unable to make transaction now, please try again later", Toast.LENGTH_SHORT).show()
                    }
                    failCount++
                }

            })
        }

        binding.buyDrinkText.setOnClickListener {
            var failCount = 0

            //Start the connection
            billingClient.startConnection(object : BillingClientStateListener{
                override fun onBillingSetupFinished(p0: BillingResult) {
                    if (p0.responseCode == BillingClient.BillingResponseCode.OK){
                        getDrinkDetails()
                        failCount = 0
                    }
                }

                override fun onBillingServiceDisconnected() {
                    if (failCount <= 3){
                        billingClient.startConnection(this)
                    }
                    else{
                        Toast.makeText(contexT, "Unable to make transaction now, please try again later", Toast.LENGTH_SHORT).show()
                    }
                    failCount++
                }

            })
        }

        binding.watchAdText.setOnClickListener {
            val adRequest = AdRequest.Builder().build()

            InterstitialAd.load(contexT, getString(R.string.interstitialAdOneID), adRequest, object: InterstitialAdLoadCallback(){
                override fun onAdLoaded(p0: InterstitialAd) {
                    p0.show(requireActivity())
                    p0.fullScreenContentCallback = object: FullScreenContentCallback(){
                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            super.onAdFailedToShowFullScreenContent(p0)
                        }

                        override fun onAdShowedFullScreenContent() {

                        }

                        override fun onAdDismissedFullScreenContent() {
                            Toast.makeText(contexT, "Thank you for supporting the app!", Toast.LENGTH_SHORT).show()
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
        return binding.root
    }

    fun adjustSheetStyle(isNight:Boolean,roundCorners:Boolean){
        if (isNight){
            if (roundCorners){
                setStyle(STYLE_NORMAL,R.style.bottomSheetDialogStyleForFontsSheetDark)
            }
            else{
                setStyle(STYLE_NORMAL,R.style.noCornersBottomSheetDialogStyleForFontsSheetDark)
            }
        }
        else{
            if (roundCorners){
                setStyle(STYLE_NORMAL,R.style.bottomSheetDialogStyleForFontsSheet)
            }
            else{
                setStyle(STYLE_NORMAL,R.style.noCornersBottomSheetDialogStyleForFontsSheet)
            }
        }
    }

    fun darkMode(isNight:Boolean){
        if (isNight){
            binding.supportAppOptionsSheetHeader.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.buyCoffeeText.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.buySnackText.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.buyDrinkText.setTextColor(ContextCompat.getColor(contexT, R.color.white))
            binding.watchAdText.setTextColor(ContextCompat.getColor(contexT, R.color.white))


            binding.buyCoffeeText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_coffee_dark_50, 0, 0, 0)
            binding.buySnackText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_fastfood_dark_50, 0, 0, 0)
            binding.buyDrinkText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_local_bar_dark_50, 0, 0, 0)
            binding.watchAdText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_tv_dark_50, 0, 0, 0)


        }
        else{
            binding.supportAppOptionsSheetHeader.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.buyCoffeeText.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.buySnackText.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.buyDrinkText.setTextColor(ContextCompat.getColor(contexT, R.color.Black))
            binding.watchAdText.setTextColor(ContextCompat.getColor(contexT, R.color.Black))


            binding.buyCoffeeText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_coffee_50, 0, 0, 0)
            binding.buySnackText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_fastfood_50, 0, 0, 0)
            binding.buyDrinkText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_local_bar_50, 0, 0, 0)
            binding.watchAdText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_tv_50, 0, 0, 0)

        }
    }

    fun adjustTheme(appTheme:String){
        if (appTheme == AppUtils.LIGHT){
            darkMode(false)
        }
        if (appTheme == AppUtils.DARK){
            darkMode(true)
        }
        if (appTheme == AppUtils.FOLLOW_SYSTEM){
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> darkMode(true)

                Configuration.UI_MODE_NIGHT_NO -> darkMode(false)
            }
        }
    }

    fun getCoffeeDetails(){
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
                            .build()))
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult,
                productDetailsList ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                launchPurchase(productDetailsList[0])
            }
        }
    }

    fun getSnackDetails(){
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
                            .build()))
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult,
                productDetailsList ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                launchPurchase(productDetailsList[0])
            }
        }
    }

    fun getDrinkDetails(){
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
                            .build()))
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult,
                productDetailsList ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                launchPurchase(productDetailsList[0])
            }
        }
    }

    fun launchPurchase(productDetails: ProductDetails){
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

        val billingResult = billingClient.launchBillingFlow(context as AppCompatActivity, billingFlowParams).responseCode

        if (billingResult != BillingClient.BillingResponseCode.OK) {
            val billingResult2 = billingClient.launchBillingFlow(context as AppCompatActivity, billingFlowParams).responseCode

            if (billingResult2 != BillingClient.BillingResponseCode.OK) {
                val billingResult3 = billingClient.launchBillingFlow(context as AppCompatActivity, billingFlowParams).responseCode

                if (billingResult3 != BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(contexT, "Unable to continue with purchase, please try again later", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    fun completePurchase(purchase: Purchase){
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient.consumeAsync(consumeParams
        ) { p0, p1 -> }
    }

    fun setAds() {
        if (activity == null) {
            return
        }
        val themePreferences = requireActivity().getSharedPreferences("apppref",
            Context.MODE_PRIVATE
        )

        if (!themePreferences.getBoolean("disableads", false)) {
            MobileAds.initialize(requireActivity()) {
                val adRequest = AdRequest.Builder().build()
                binding.bannerad4.loadAd(adRequest)
            }



            binding.bannerad4.visibility = View.VISIBLE
        } else {
            binding.bannerad4.visibility = View.GONE
        }
    }
}