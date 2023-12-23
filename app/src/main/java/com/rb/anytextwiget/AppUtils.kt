package com.rb.anytextwiget

import android.Manifest
import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.database.CursorIndexOutOfBoundsException
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaMuxer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.DialogTitle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rb.anytextwiget.databinding.WidgetLayoutBinding
import com.rb.anytextwiget.jetpackUI.FontUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jcodec.api.SequenceEncoder
import org.jcodec.scale.BitmapUtil
import java.io.*
import java.lang.Exception
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AppUtils {
    interface WidgetSaveInterface {
        fun widgetSaved(savedPath: String)

        fun widgetSaveFailed()
    }

    interface MediaListener {
        //Image saving callbacks.
        fun onMediaSaved(savedPath: String)
        fun onMediaSaveProgress(progress: Int)
        fun onMediaSaveFailed(reason: String)
    }

    companion object {
        //All the available font names

        val aclonica: String = "Aclonica"

        val acme = "Acme"

        val aguafina_script = "Aguafina script"

        val akaya_telivigala = "Akaya telivigala"

        val akronim = "Akronim"

        val aldrich = "Aldrich"

        val alexBrush = "Alex brush"

        val allertaStencil = "Allerta stencil"

        val almendraSc = "Almendra sc"

        val anonymousPro = "Anonymous pro"

        val anton = "Anton"

        val architectsDaughter = "Architects daughter"

        val astloch = "Astloch"

        val atomicAge = "Atomic age"

        val audiowide = "Audiowide"

        val autourOne = "Autour one"


        val badScript = "Bad script"

        val baloo = "Baloo"

        val bangers = "Bangers"

        val barriecito = "Barriecito"

        val barrio = "Barrio"

        val basic: String = "Basic"

        val baumans = "Baumans"

        val bethEllen = "Beth ellen"

        val bitter: String = "Bitter"

        val blackOpsOne: String = "Black ops one"

        val brawler: String = "Brawler"

        val bungee = "Bungee"

        val bungeeHairline = "Bungee hairline"

        val bungeeInline = "Bungee inline"

        val bungeeOutline = "Bungee outline"

        val bungeeShade = "Bungee shade"

        val cabin: String = "Cabin"

        val caesarDressing = "Caesar dressing"

        val cairo: String = "Cairo"

        val calligraffitti = "Calligraffitti"

        val cambay: String = "Cambay"

        val carterOne = "Carter one"

        val caveat = "Caveat"

        val chakraPetch = "Chakra petch"

        val chelseaMarket: String = "Chelsea market"

        val cherryCreamSoda: String = "Cherry cream soda"

        val chewy = "Chewy"

        val cinzelDecorative = "Cinzel decorative"

        val coda = "Coda"

        val contrailOne = "Contrail one"

        val courgette = "Courgette"

        val coveredByYourGrace = "Covered by your grace"

        val creepster = "Creepster"

        val croissantOne = "Croissant one"

        val damion: String = "Damion"

        val daysOne: String = "Days one"

        val deliusUnicase: String = "Delius unicase"

        val diplomata = "Diplomata"

        val doppioOne = "Doppio one"

        val drSugiyama = "Dr Sugiyama"

        val droidSans: String = "Droid sans"

        val droidSansMono: String = "Droid sans mono"

        val droidSerif: String = "Droid serif"

        val eagleLake = "Eagle lake"

        val eater = "Eater"

        val electrolize: String = "Electrolize"

        val elsieSwashCaps = "Elsie swash caps"

        val engagement = "Engagement"

        val ewert = "Ewert"

        val fascinate = "Fascinate"

        val fascinateInline = "Fascinate inline"

        val fingerPaint: String = "Finger paint"

        val firaMono = "Fira mono"

        val firaSans = "Fira sans"

        val fontdinerSwanky = "Fontdiner swanky"

        val freckleFace = "Freckle one"

        val fruktur = "Fruktur"

        val fugazOne = "Fugaz one"


        val geostar = "Geostar"

        val geostarFill = "Geostar fill"

        val gravitasOne = "Gravitas one"

        val grenzeGotisch = "Grenze gotisch"

        val gruppo = "Gruppo"

        val gugi = "Gugi"


        val hachiMaruPop = "Hachi maru pop"

        val hanalei = "Hanalei"

        val hanaleiFill = "Hanalei fill"

        val happyMonkey = "Happy monkey"

        val herrVonMuellerhoff = "Herr Von Muellerhoff"

        val holtwoodOneSc = "Holtwood one sc"

        val homeMadeApple = "Home made apple"


        val iceberg = "Iceberg"

        val iceland = "Iceland"

        val imprima = "Imprima"

        val inconsolata = "Inconsolata"

        val irishGrover = "Irish grover"

        val italiana = "Italianna"

        val italianno = "Italianno"

        val jacquesFrancoisShadow = "Jacques francois shadow"

        val jimNightshade = "Jim nightshade"

        val jollyLodger = "Jolly lodger"

        val jotiOne = "Joti one"

        val jua = "Jua"

        val julee = "Julee"

        val jura = "Jura"

        val justAnotherHand = "Just another hand"

        val justMeAgainDownHere = "Just me again down here"

        val kalam: String = "Kalam"

        val kaushanScript = "Kaushan script"

        val kavivanar = "Kavivanar"

        val kavoon = "Kavoon"

        val kellySlab = "Kelly slab"

        val knewave = "Knewave"

        val kodchasan = "Kodchasan"

        val kottaOne = "Kotta one"

        val kranky = "Kranky"

        val kronaOne = "Krona one"


        val lacquer = "Lacquer"

        val leckerliOne: String = "Leckerli one"

        val lemon: String = "Lemon"

        val lemonoda: String = "Lemonoda"

        val luckiestGuy = "Luckiest guy"

        val markoOne = "Marko one"

        val martelSans = "Martel sans"

        val marvel = "Marvel"

        val mcLaren = "McLaren"

        val metalMania = "Metal mania"

        val michroma = "Michroma"

        val mogra = "Mogra"

        val montserratAlternates = "Monteserrat alternates"

        val mrsSheppards = "Mrs Sheppards"


        val nanumBrushScript = "Nanum brush script"

        val nanumPenScript = "Nanum pen script"

        val nerkoOne = "Nerko one"

        val neuton = "Neuton"

        val newRocker = "New rocker"

        val niconne = "Niconne"

        val norican = "Norican"

        val nosifer = "Nosifer"

        val notable = "Notable"

        val nothingYouCouldDo = "Nothing you could do"

        val novaOval = "Nova oval"


        val odibeeSans = "Odibee sans"

        val oi = "oi"

        val oleoScript = "Oleo script"

        val oleoScriptSwashCaps = "Oleo script swash caps"

        val openSans: String = "Open sans"

        val orbitron = "Orbitron"

        val oregano = "Oregano"

        val originalSurfer = "Original surfer"

        val overTheRainbow = "Over the rainbow"

        val oxygenMono = "Oxygen mono"


        val pacifico = "Pacifico"

        val pattaya = "Pattaya"

        val permanentMarker = "Permanent marker"

        val poppins = "Poppins"

        val pressStart2p = "Press start 2p"


        val quando = "Quando"

        val quantico = "Quantico"

        val quattrocento = "Quattrocento"

        val quattrocentoSans = "Quattrocento sans"

        val questrial = "Questrial"

        val quickSand = "Quick sand"

        val quintessential = "Quintessential"

        val qwigley = "Qwigley"


        val reggaeOne = "Reggae one"

        val revalia = "Revalia"

        val righteous = "Righteous"

        val roboto: String = "Roboto"

        val rockSalt = "Rock salt"

        val rockNRollOne = "RocknRoll one"

        val rowdies = "Rowdies"

        val rubikMonoOne = "Rubik mono one"


        val sancreek = "Sancreek"

        val sarina = "Sarina"

        val shojumaru = "Shojumaru"

        val shrikhand = "Shrikhand"

        val slackey = "Slackey"

        val sonsieOne = "Sonsie one"

        val spectralSc = "Spectral sc"

        val spicyRice = "Spicy rice"

        val spirax = "Spirax"

        val sreekrushnadevaraya = "Sree krushnadevaraya"

        val sriracha = "Sriracha"

        val srisakdi = "Srisakdi"

        val staatliches = "Staatliches"

        val stalemate = "Stalemate"

        val stalinistOne = "Stalinist one"

        val stardos_stencil = "Stardos stencil"

        val stick = "Stick"

        val suezOne = "Suez one"

        val sunshiney = "Sunshiney"

        val supermercadoOne = "Supermercado one"

        val swankyAndMooMoo = "Swanky and moo moo"

        val syncopate = "Syncopate"

        val syne = "Syne"

        val syneMono = "Syne mono"

        val syneTactile = "Syne tactile"


        val tangerine = "Tangerine"

        val theGirlNextDoor = "The girl next door"

        val titanOne = "Titan one"

        val tradeWinds = "Trade winds"

        val trainOne = "Train one"

        val trispace = "Trispace"

        val trocchi = "Trocchi"


        val ubuntu = "Ubuntu"

        val ultra = "Ultra"

        val uncialAntiqua = "Uncial antiqua"

        val underdog = "Underdog"

        val unicaOne = "Unica one"

        val unifrakturmaguntia = "Unifrakturmaguntia"

        val unkempt = "Unkempt"

        val unlock = "Unlock"


        val vampiroOne = "Vampiro one"

        val vastShadow = "Vasr shadow"

        val vibur = "Vibur"

        val vollkornSc = "Vollkron sc"

        val vt323 = "Vit323"


        val waitingForTheSunrise = "Waiting for the sunrise"

        val wallpoet = "Wallpoet"

        val walterTurncoat = "Walter turncoat"

        val warnes = "Warnes"

        val wellfleet = "Wellfleet"

        val wendyOne = "Wendy one"

        val workSans = "Work sans"

        val xanhMono = "Xanh mono"


        val yatraOne = "Yatra one"

        val yellowtail = "Yellowtail"

        val yesevaOne = "Yeseva one"

        val yesteryear = "Yesteryear"

        val yuseiMagic = "Yusei magic"


        val zcoolKuaile = "Zcool kuaile"

        val zcoolQingleHangyou = "Zcool qingle huangyou"

        val zcoolXiaowei = "Zcool xiaowei"

        val zeyada = "Zeyada"

        val zhiMangXing = "Zhi mang xing"

        val zillaSlabHighlight = "Zilla slab highlight"


        //Available font styles
        val normal: String = "normal"

        val light: String = "light"

        val italic: String = "italic"

        val medium: String = "medium"

        val semibold: String = "semibold"

        val bold: String = "bold"

        val extrabold: String = "extrabold"


        //App theme modes
        val LIGHT = "light"

        val DARK = "dark"

        val FOLLOW_SYSTEM = "system"

        //Action types
        val ACTIONS_SIMPLE = "simple"

        val ACTIONS_APP = "app"

        //Simple action types
        val ACTION_WIFI = "wifi"

        val ACTION_DONOTDISTURB = "do not disturb"

        val ACTION_FLASHLIGHT = "flashlight"

        val ACTION_BLUETOOTH = "bluetooth"

        val ACTION_NEXTIMAGE = "next image"

        val ACTION_OPEN_LINK = "open link"

        val ACTION_NOTHING = "nothing"


        fun getDefaultColorsFromJson(json: String): MutableList<ColorData> {
            val gson = Gson()
            val type = object : TypeToken<MutableList<ColorData>>() {}.type
            return gson.fromJson(json, type)
        }

        fun addDefaultColors(context: Context): MutableList<ColorData> {

            val dataList: MutableList<ColorData>
            dataList = ArrayList<ColorData>()

            val sharedPreferences = context.getSharedPreferences("colorspref", Context.MODE_PRIVATE)


            //Black
            val blackColorData = ColorData()
            blackColorData.colorName = "Black"
            blackColorData.colorHexCode = "#000000"
            blackColorData.ID = UUID.randomUUID().toString()
            dataList.add(blackColorData)

            //White
            val whiteColorData = ColorData()
            whiteColorData.colorName = "White"
            whiteColorData.colorHexCode = "#FFFFFF"
            whiteColorData.ID = UUID.randomUUID().toString()
            dataList.add(whiteColorData)

            //Grey
            val greyColorData = ColorData()
            greyColorData.colorName = "Grey"
            greyColorData.colorHexCode = "#9E9E9E"
            greyColorData.ID = UUID.randomUUID().toString()
            dataList.add(greyColorData)


            //Red
            val redColorData = ColorData()
            redColorData.colorName = "Red"
            redColorData.colorHexCode = "#D50000"
            redColorData.ID = UUID.randomUUID().toString()
            dataList.add(redColorData)


            //Blue
            val blueColorData = ColorData()
            blueColorData.colorName = "Blue"
            blueColorData.colorHexCode = "#0762E9"
            blueColorData.ID = UUID.randomUUID().toString()
            dataList.add(blueColorData)


            //Violet
            val violetColorData = ColorData()
            violetColorData.colorName = "Violet"
            violetColorData.colorHexCode = "#4600AF"
            violetColorData.ID = UUID.randomUUID().toString()
            dataList.add(violetColorData)

            //Transparent
            val transparentColorData = ColorData()
            transparentColorData.colorName = "Transparent"
            transparentColorData.colorHexCode = "#00FFFFFF"
            transparentColorData.ID = UUID.randomUUID().toString()
            dataList.add(transparentColorData)

            val gson = Gson()
            val json = gson.toJson(dataList)

            sharedPreferences.edit().putString("defaultcolors", json).apply()

            return dataList
        }

        fun sptopx(context: Context, toconvertvalue: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                toconvertvalue.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }

        fun dptopx(context: Context, toconvertvalue: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                toconvertvalue.toFloat(),
                context.resources.getDisplayMetrics()
            ).toInt()
        }


        @Deprecated("This fonts list is deprecated. Move to the new fonts list")
        suspend fun getFontsList(): MutableList<UIFontData> {
            val fontsList = ArrayList<UIFontData>()

            //Alphabetical order

            //Aldrich
            fontsList.add(UIFontData("Aldrich", R.font.aldrich))


            //Basic
            fontsList.add(UIFontData("Basic", R.font.basic))


            //Bitter
            fontsList.add(UIFontData("Bitter", R.font.bitter))
            fontsList.add(UIFontData("Bitter italic", R.font.bitter_italic))
            fontsList.add(UIFontData("Bitter bold", R.font.bitter_bold))


            //Black ops one
            fontsList.add(UIFontData("Black ops one", R.font.black_ops_one))


            //Brawler
            fontsList.add(UIFontData("Brawler", R.font.brawler))


            //Cabin
            fontsList.add(UIFontData("Cabin", R.font.cabin))
            fontsList.add(UIFontData("Cabin italic", R.font.cabin_italic))
            fontsList.add(UIFontData("Cabin semi-bold", R.font.cabin_semibold))
            fontsList.add(UIFontData("Cabin bold", R.font.cabin_bold))


            //Cairo
            fontsList.add(UIFontData("Cairo", R.font.cairo))
            fontsList.add(UIFontData("Cairo semi-bold", R.font.cairo_semibold))
            fontsList.add(UIFontData("Cairo bold", R.font.cairo_bold))


            //Cambay
            fontsList.add(UIFontData("Cambay", R.font.cambay))
            fontsList.add(UIFontData("Cambay italic", R.font.cambay_italic))
            fontsList.add(UIFontData("Cambay bold", R.font.cambay_bold))


            //Chelsea market
            fontsList.add(UIFontData("Chelsea market", R.font.chelsea_market))


            //Cherry cream soda
            fontsList.add(UIFontData("Cherry cream soda", R.font.cherry_cream_soda))


            //Damion
            fontsList.add(UIFontData("Damion", R.font.damion))


            //Days one
            fontsList.add(UIFontData("Days one", R.font.days_one))


            //Delius unicase
            fontsList.add(UIFontData("Delius unicase", R.font.delius_unicase))
            fontsList.add(UIFontData("Delius unicase bold", R.font.delius_unicase_bold))


            //Droid sans
            fontsList.add(UIFontData("Droid sans", R.font.droid_sans))
            fontsList.add(UIFontData("Droid sans bold", R.font.droid_sans_bold))


            //Droid sans mono
            fontsList.add(UIFontData("Droid sans mono", R.font.droid_sans_mono))


            //Droid serif
            fontsList.add(UIFontData("Droid serif", R.font.droid_serif))
            fontsList.add(UIFontData("Droid serif italic", R.font.droid_serif_italic))
            fontsList.add(UIFontData("Droid serif bold", R.font.droid_serif_bold))


            //Electrolize
            fontsList.add(UIFontData("Electrolize", R.font.electrolize))


            //Finger paint
            fontsList.add(UIFontData("Finger paint", R.font.finger_paint))


            //Kalam
            fontsList.add(UIFontData("Kalam", R.font.kalam))
            fontsList.add(UIFontData("Kalam bold", R.font.kalam_bold))


            //Leckerli one
            fontsList.add(UIFontData("leckerli one", R.font.leckerli_one))


            //Lemon
            fontsList.add(UIFontData("Lemon", R.font.lemon))


            //Lemonada
            fontsList.add(UIFontData("Lemonoda", R.font.lemonoda))
            fontsList.add(UIFontData("Lemonoda semi-bold", R.font.lemonoda_semibold))
            fontsList.add(UIFontData("Lemonoda bold", R.font.lemonoda_bold))


            //Open Sans
            fontsList.add(UIFontData("Open Sans", R.font.open_sans))
            fontsList.add(UIFontData("Open Sans italic", R.font.open_sans_italic))
            fontsList.add(UIFontData("Open Sans semi-bold", R.font.open_sans_semibold))
            fontsList.add(UIFontData("Open Sans bold", R.font.open_sans_bold))


            //Roboto
            fontsList.add(UIFontData("Roboto", R.font.roboto))
            fontsList.add(UIFontData("Roboto italic", R.font.roboto_italic))
            fontsList.add(UIFontData("Roboto medium", R.font.roboto_medium))
            fontsList.add(UIFontData("Roboto bold", R.font.roboto_bold))


            return fontsList
        }

        suspend fun getNewFontsList(context: Context): MutableList<FontItemData> {

            val sharedPreferences = context.getSharedPreferences("apppref", MODE_PRIVATE)


            val savedFontsList = sharedPreferences.getString("fontslist5", null)


            if (savedFontsList != null) {
                val gson = Gson()
                val type = object : TypeToken<MutableList<FontItemData>>() {}.type

                return gson.fromJson(savedFontsList, type) as MutableList<FontItemData>
            }


            val fontsList = ArrayList<FontItemData>()

            //Alphabetical order

            //Aclonica
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(aclonica, normal, "aclonica"),
                    null,
                    null,
                    null,
                    null

                )
            )

            //Acme
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(acme, normal, "acme"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Aguafina Script
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(aguafina_script, normal, "aguafina_script"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Akaya telivigala
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(akaya_telivigala, normal, "akaya_telivigala"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Akronim
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(akronim, normal, "akronim"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Aldrich
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(aldrich, normal, "aldrich"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Alex brush
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(alexBrush, normal, "alex_brush"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Allerta stencil
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(allertaStencil, normal, "allerta_stencil"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Almendra SC
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(almendraSc, normal, "almendra_sc"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Anonymous pro
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(anonymousPro, normal, "anonymous_pro"),
                    WidgetFontInfo("Anonymous pro italic", italic, "anonymous_pro_italic"),
                    null,
                    null,
                    WidgetFontInfo("Anonymous pro bold", bold, "anonymous_pro_bold")
                )
            )

            //Anton
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(anton, normal, "anton"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Architects daughter
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(architectsDaughter, normal, "architects_daughter"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Astloch
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(astloch, normal, "astloch"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Astloch bold", bold, "astloch_bold"),
                )
            )

            //Atomic age
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(atomicAge, normal, "atomic_age"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Audiowide
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(audiowide, normal, "audiowide"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Autour one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(autourOne, normal, "autour_one"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Bad Script
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(badScript, normal, "bad_script"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Baloo
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(baloo, normal, "baloo"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //bangers
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(bangers, normal, "bangers"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Barriecito
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(barriecito, normal, "barriecito"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Barrio
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(barrio, normal, "barrio"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Basic
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(basic, normal, "basic"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Baumans
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(baumans, normal, "baumans"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //beth ellen
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(bethEllen, normal, "beth_ellen"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Bitter
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(bitter, normal, "bitter"),
                    WidgetFontInfo("Bitter italic", italic, "bitter_italic"),
                    null,
                    null,
                    WidgetFontInfo("Bitter bold", bold, "bitter_bold")
                )
            )


            //Black ops one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(blackOpsOne, normal, "black_ops_one"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Brawler
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(brawler, normal, "brawler"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Bungee
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(bungee, normal, "bungee"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Bungee hairline
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(bungeeHairline, normal, "bungee_hairline"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Bungee inline
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(bungeeInline, normal, "bungee_inline"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Bungee Outline
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(bungeeOutline, normal, "bungee_outline"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Bungee shade
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(bungeeShade, normal, "bungee_shade"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Cabin
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(cabin, normal, "cabin"),
                    WidgetFontInfo("Cabin italic", italic, "cabin_italic"),
                    null,
                    WidgetFontInfo("Cabin semibold", semibold, "cabin_semibold"),
                    WidgetFontInfo("Cabin bold", bold, "cabin_bold")
                )
            )

            //Caesar dressing
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(caesarDressing, normal, "caesar_dressing"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Cairo
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(cairo, normal, "cairo"),
                    WidgetFontInfo("Cairo light", light, "cairo_light"),
                    null,
                    null,
                    WidgetFontInfo("Cairo semibold", semibold, "cairo_semibold"),
                    WidgetFontInfo("Cairo bold", bold, "cairo_bold"),
                    null
                )
            )

            //Calligraffitti
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(calligraffitti, normal, "calligraffitti"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Cambay
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(cambay, normal, "cambay"),
                    WidgetFontInfo("Cambay italic", italic, "cambay_italic"),
                    null,
                    null,
                    WidgetFontInfo("Cambay bold", bold, "cambay_bold")
                )
            )


            //Carter One
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(carterOne, normal, "carter_one"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Caveat
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(caveat, normal, "caveat"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Caveat bold", bold, "caveat_bold")
                )
            )


            //chakra petch
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(chakraPetch, normal, "chakra_petch"),
                    WidgetFontInfo("Chakra petch light", light, "chakra_petch_light"),
                    WidgetFontInfo("chakra petch italic", italic, "chakra_petch_italic"),
                    WidgetFontInfo("Chakra petch medium", medium, "chakra_petch_medium"),
                    WidgetFontInfo("Chakra petch semibold", semibold, "chakra_petch_semibold"),
                    WidgetFontInfo("Chakra petch bold", bold, "chakra_petch_bold"),
                    null
                )
            )


            //Chelsea market
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(chelseaMarket, normal, "chelsea_market"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Cherry cream soda
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(cherryCreamSoda, normal, "cherry_cream_soda"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Chewy
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(chewy, normal, "chewy"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Cinzel decorative
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(cinzelDecorative, normal, "cinzel_decorative"),
                    null,
                    null,
                    WidgetFontInfo(
                        "Cinzel decorative semibold",
                        semibold,
                        "cinzel_decorative_bold"
                    ),
                    WidgetFontInfo("Cinzel decorative bold", bold, "cinzel_decorative_black"),
                )
            )


            //Coda
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(coda, normal, "coda"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Coda bold", bold, "coda_bold")
                )
            )

            //Contrail one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(contrailOne, normal, "contrail_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Courgette
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(courgette, normal, "courgette"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Covered by your grace
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(coveredByYourGrace, normal, "covered_by_your_grace"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Creepster
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(creepster, normal, "creepster"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Croissant one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(croissantOne, normal, "croissant_one"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Damion
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(damion, normal, "daimon"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Days one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(daysOne, normal, "days_one"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Delius unicase
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(deliusUnicase, normal, "delius_unicase"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Delius unicase bold", bold, "delius_unicase_bold")
                )
            )

            //Diplomata
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(diplomata, normal, "diplomata"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Doppio one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(doppioOne, normal, "doppio_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Dr sugiyama
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(drSugiyama, normal, "dr_sugiyama"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Droid sans
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(droidSans, normal, "droid_sans"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Droid sans bold", bold, "droid_sans_bold")
                )
            )


            //Droid sans mono
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(droidSansMono, normal, "droid_sans_mono"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Droid serif
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(droidSerif, normal, "droid_serif"),
                    WidgetFontInfo("Droid serif italic", italic, "droid_serif_italic"),
                    null,
                    null,
                    WidgetFontInfo("Droid serif bold", bold, "droid_serif_bold")
                )
            )

            //Eagle lake
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(eagleLake, normal, "eagle_lake"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Eater
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(eater, normal, "eater"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Electrolize
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(electrolize, normal, "electrolize"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Elsie swash caps
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(elsieSwashCaps, normal, "elsie_swash_caps"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Engagement
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(engagement, normal, "engagement"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Ewert
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(ewert, normal, "ewert"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Fascinate
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(fascinate, normal, "fascinate"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Fascinate inline
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(fascinateInline, normal, "fascinate_inline"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Finger paint
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(fingerPaint, normal, "finger_paint"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Fira Mono
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(firaMono, normal, "fira_mono"),
                    null,
                    WidgetFontInfo("Fira mono medium", medium, "fira_mono_medium"),
                    null,
                    WidgetFontInfo("Fira mono bold", bold, "fira_mono_bold")
                )
            )


            //Fira Sans
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(firaSans, normal, "fira_sans"),
                    WidgetFontInfo("Fira sans light", light, "fira_sans_light"),
                    WidgetFontInfo("Fira sans italic", italic, "fira_sans_italic"),
                    WidgetFontInfo("Fira sans medium", medium, "fira_sans_medium"),
                    WidgetFontInfo("Fira sans semibold", semibold, "fira_sans_semibold"),
                    WidgetFontInfo("Fira sans bold", bold, "fira_sans_bold"),
                    WidgetFontInfo("Fira sans extrabold", extrabold, "fira_sans_extrabold")

                )

            )

            //Fontdiner swanky
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(fontdinerSwanky, normal, "fontdiner_swanky"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Freckle face
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(freckleFace, normal, "freckle_face"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Fruktur
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(fruktur, normal, "fruktur"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Fugaz One
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(fugazOne, normal, "fugaz_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Geostar
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(geostar, normal, "geostar"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Geostar fill
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(geostarFill, normal, "geostar_fill"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Gravitas One
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(gravitasOne, normal, "gravitas_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Grenze gotisch
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(grenzeGotisch, normal, "grenze_gotisch"),
                    null,
                    WidgetFontInfo("Grenze gotisch medium", medium, "grenze_gotisch_medium"),
                    WidgetFontInfo("Grenze gotisch semibold", semibold, "grenze_gotisch_semibold"),
                    WidgetFontInfo("Grenze gotisch bold", bold, "grenze_gotisch_black")
                )
            )


            //Gruppo
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(gruppo, normal, "gruppo"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //gugi
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(gugi, normal, "gugi"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Hachi maru pop
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(hachiMaruPop, normal, "hachi_maru_pop"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Hanalei
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(hanalei, normal, "hanalei"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Hanalei fill
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(hanaleiFill, normal, "hanalei_fill"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Happy Monkey
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(happyMonkey, normal, "happy_monkey"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Herr Von muellerhoff
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(herrVonMuellerhoff, normal, "herr_von_muellerhoff"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Holtwood one sc
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(holtwoodOneSc, normal, "holtwood_one_sc"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Home Made Apple
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(homeMadeApple, normal, "home_made_apple"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Iceberg
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(iceberg, normal, "iceberg"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Iceland
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(iceland, normal, "iceland"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Imprima
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(imprima, normal, "imprima"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Inconsolata
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(inconsolata, normal, "inconsolata"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Inconsolata bold", bold, "inconsolata_bold")
                )
            )

            //Irish Grover
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(irishGrover, normal, "irish_grover"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Italiana
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(italiana, normal, "italiana"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Italianno
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(italianno, normal, "italianno"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Jacques Francois Shadow
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(jacquesFrancoisShadow, normal, "jacques_francois_shadow"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Jim nightshade
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(jimNightshade, normal, "jim_nightshade"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Jolly lodger
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(jollyLodger, normal, "jolly_lodger"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Joti one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(jotiOne, normal, "joti_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Jua
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(jua, normal, "jua"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Julee
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(julee, normal, "julee"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Jura
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(jura, normal, "jura"),
                    WidgetFontInfo("Jura light", light, "jura_light"),
                    null,
                    WidgetFontInfo("Jura medium", medium, "jura_medium"),
                    WidgetFontInfo("Jura semibold", semibold, "jura_semibold"),
                    null,
                    null
                )
            )

            //Just another hand
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(justAnotherHand, normal, "just_another_hand"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Just me again down here
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(justMeAgainDownHere, normal, "just_me_again_down_here"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Kalam
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(kalam, normal, "kalam"), null, null, null,
                    WidgetFontInfo("Kalam bold", bold, "kalam_bold")
                )
            )


            //Kaushan Script
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(kaushanScript, normal, "kaushan_script"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Kalam
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(kavivanar, normal, "kavivanar"), null, null, null,
                    null
                )
            )

            //Kavoon
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(kavoon, normal, "kavoon"), null, null, null,
                    null
                )
            )

            //Kelly slab
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(kellySlab, normal, "kelly_slab"), null, null, null,
                    null
                )
            )

            //Knewave
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(knewave, normal, "knewave"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Kodchasan
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(kodchasan, normal, "kodchasan"),
                    WidgetFontInfo(kodchasan, italic, "kodchasan_semibold_italic"),
                    WidgetFontInfo(kodchasan, medium, "kodchasan_medium"),
                    WidgetFontInfo(kodchasan, semibold, "kodchasan_semibold"),
                    WidgetFontInfo(kodchasan, bold, "kodchasan_bold")
                )
            )

            //Kotta One
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(kottaOne, normal, "kotta_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Kranky
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(kranky, normal, "kranky"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Krona One
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(kronaOne, normal, "krona_one"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Lacquer
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(lacquer, normal, "lacquer"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Leckerli one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(leckerliOne, normal, "leckerli_one"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Lemon
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(lemon, normal, "lemon"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Lemonada
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(lemonoda, normal, "lemonoda"),
                    WidgetFontInfo("Lemonoda light", light, "lemonoda_light"),
                    null,
                    null,
                    WidgetFontInfo("lemonoda semibold", semibold, "lemonoda_semibold"),
                    WidgetFontInfo("lemonoda bold", bold, "lemonoda_bold"),
                    null
                )
            )

            //Luckiest Guy
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(luckiestGuy, normal, "luckiest_guy"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Marko One
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(markoOne, normal, "marko_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Martel Sans
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(martelSans, normal, "martel_sans"),
                    WidgetFontInfo("Martel sans light", light, "martel_sans_light"),
                    null,
                    null,
                    WidgetFontInfo("Martel sans semibold", semibold, "martel_sans_semibold"),
                    WidgetFontInfo("Martel sans bold", bold, "martel_sans_bold"),
                    WidgetFontInfo("Martel sans extrabold", extrabold, "martel_sans_extrabold")
                )
            )

            //Marvel
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(marvel, normal, "marvel"),
                    WidgetFontInfo("Marvel italic", italic, "marvel_italic"),
                    null,
                    null,
                    WidgetFontInfo("Marvel bold", bold, "marvel_bold")
                )
            )

            //McLaren
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(mcLaren, normal, "mclaren"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Metal Mania
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(metalMania, normal, "metal_mania"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Michroma
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(michroma, normal, "michroma"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Mogra
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(mogra, normal, "mogra"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Montserrat alternates
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(montserratAlternates, normal, "montserrat_alternates"),
                    WidgetFontInfo(
                        "Montserrat alternates light",
                        light,
                        "montserrat_alternates_light"
                    ),
                    WidgetFontInfo(
                        "Montserrat alternates italic",
                        italic,
                        "montserrat_alternates_italic"
                    ),
                    WidgetFontInfo(
                        "Montserrat alternates medium",
                        medium,
                        "montserrat_alternates_medium"
                    ),
                    WidgetFontInfo(
                        "Montserrat alternates semibold",
                        semibold,
                        "montserrat_alternates_semibold"
                    ),
                    WidgetFontInfo(
                        "Montserrat alternates bold",
                        bold,
                        "montserrat_alternates_bold"
                    ),
                    WidgetFontInfo(
                        "Montserrat alternates extrabold",
                        extrabold,
                        "montserrat_alternates_extrabold"
                    )
                )
            )


            //Mrs Sheppards
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(mrsSheppards, normal, "mrs_sheppards"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Nanum brush script
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(nanumBrushScript, normal, "nanum_brush_script"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Nanum pen script
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(nanumPenScript, normal, "nanum_pen_script"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Nereko one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(nerkoOne, normal, "nerko_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Neuton
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(neuton, normal, "neuton"),
                    WidgetFontInfo("Neuton italic", italic, "neuton_italic"),
                    null,
                    WidgetFontInfo("Neuton semibold", semibold, "neuton_bold"),
                    WidgetFontInfo("Neuton bold", bold, "neuton_extrabold")
                )
            )

            //New rocker
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(newRocker, normal, "new_rocker"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Niconne
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(niconne, normal, "niconne"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Norican
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(norican, normal, "norican"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Nosifer
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(nosifer, normal, "nosifer"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Notable
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(notable, normal, "notable"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Nothing you could do
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(nothingYouCouldDo, normal, "nothing_you_could_do"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Nova oval
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(novaOval, normal, "nova_oval"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Odibee sans
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(odibeeSans, normal, "odibee_sans"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Oi
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(oi, normal, "oi"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Oleo script
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(oleoScript, normal, "oleo_script"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Oleo script bold", bold, "oleo_script_bold")
                )
            )

            //Oleo script swash caps
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(oleoScriptSwashCaps, normal, "oleo_script_swash_caps"),
                    null,
                    null,
                    null,
                    WidgetFontInfo(
                        "Oleo script swash caps bold",
                        bold,
                        "oleo_script_swash_caps_bold"
                    )
                )
            )

            //Open Sans
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(openSans, normal, "open_sans"),
                    WidgetFontInfo("Open sans light", light, "open_sans_light"),
                    WidgetFontInfo("Open sans italic", italic, "open_sans_italic"),
                    null,
                    WidgetFontInfo("Open sans semibold", semibold, "open_sans_semibold"),
                    WidgetFontInfo("Open sans bold", bold, "open_sans_bold"),
                    WidgetFontInfo("Open sans extrabold", extrabold, "open_sans_extrabold")
                )
            )

            //Orbitron
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(orbitron, normal, "orbitron"),
                    null,
                    WidgetFontInfo("Orbitron medium", medium, "orbitron_medium"),
                    WidgetFontInfo("Orbitron semibold", semibold, "orbitron_bold"),
                    WidgetFontInfo("Orbitron bold", bold, "orbitron_black")
                )
            )

            //Oregano
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(oregano, normal, "oregano"),
                    WidgetFontInfo("Oregano italic", italic, "oregano_italic"),
                    null,
                    null,
                    null
                )
            )

            //Original surfer
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(originalSurfer, normal, "original_surfer"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Over the rainbow
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(overTheRainbow, normal, "over_the_rainbow"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Oxygen mono
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(oxygenMono, normal, "oxygen_mono"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Pacifico
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(pacifico, normal, "pacifico"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Pattaya
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(pattaya, normal, "pattaya"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Permanent Marker
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(permanentMarker, normal, "permanent_marker"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Poppins
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(poppins, normal, "poppins"),
                    WidgetFontInfo("Poppins light", light, "poppins_light"),
                    null,
                    WidgetFontInfo("Poppins medium", medium, "poppins_medium"),
                    WidgetFontInfo("Poppins semibold", semibold, "poppins_semibold"),
                    WidgetFontInfo("Poppins bold", bold, "poppins_bold"),
                    WidgetFontInfo("Poppins extrabold", extrabold, "poppins_extrabold")
                )
            )

            //Press Start 2p
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(pressStart2p, normal, "press_start_2p"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Quando
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(quando, normal, "quando"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Quantico
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(quantico, normal, "quantico"),
                    WidgetFontInfo("Quantico italic", italic, "quantico_italic"),
                    null,
                    null,
                    WidgetFontInfo("Quantico bold", bold, "quantico_bold")
                )
            )

            //Quattrocento
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(quattrocento, normal, "quattrocento"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Quattrocento bold", bold, "quattrocento_bold")
                )
            )

            //Quattrocento sans
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(quattrocentoSans, normal, "quattrocento_sans"),
                    WidgetFontInfo("Quattrocento sans italic", italic, "quattrocento_sans_italic"),
                    null,
                    null,
                    WidgetFontInfo("Quattrocento sans bold", bold, "quattrocento_sans_bold")
                )
            )

            //Questrial
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(questrial, normal, "questrial"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Quicksand
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(quickSand, normal, "quicksand"),
                    WidgetFontInfo("Quicksand light", light, "quicksand_light"),
                    null,
                    WidgetFontInfo("Quicksand medium", normal, "quicksand_medium"),
                    null,
                    WidgetFontInfo("Quicksand bold", bold, "quicksand_bold"),
                    null
                )
            )

            //Quintessential
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(quintessential, normal, "quintessential"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Qeigley
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(qwigley, normal, "qwigley"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Reggae one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(reggaeOne, normal, "reggae_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Revelia
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(revalia, normal, "revalia"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Righteous
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(righteous, normal, "righteous"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Roboto
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(roboto, normal, "roboto"),
                    WidgetFontInfo("Roboto light", light, "roboto_light"),
                    WidgetFontInfo("Roboto italic", italic, "roboto_italic"),
                    WidgetFontInfo("Roboto medium", medium, "roboto_medium"),
                    null,
                    WidgetFontInfo("Roboto bold", bold, "roboto_bold"),
                    WidgetFontInfo("Roboto extrabold", extrabold, "roboto_extrabold")
                )
            )

            //Rock Salt
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(rockSalt, normal, "rock_salt"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //RocknRoll one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(rockNRollOne, normal, "rocknroll_one"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Rowdies
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(rowdies, normal, "rowdies"),
                    WidgetFontInfo("Rowdies light", light, "rowdies_light"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Rowdies bold", bold, "rowdies_bold"),
                    null
                )
            )

            //Rubik mono one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(rubikMonoOne, normal, "rubik_mono_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Sancreek
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(sancreek, normal, "sancreek"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Sarina
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(sarina, normal, "sarina"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Shojumaru
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(shojumaru, normal, "shojumaru"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Shrikhand
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(shrikhand, normal, "shrikhand"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Slackey
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(slackey, normal, "slackey"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Sonsie one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(sonsieOne, normal, "sonsie_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Spectral sc
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(spectralSc, normal, "spectral_sc"),
                    WidgetFontInfo("Spectral Sc light", light, "spectral_sc_light"),
                    WidgetFontInfo("Spectral Sc italic", italic, "spectral_sc_italic"),
                    WidgetFontInfo("Spectral Sc medium", medium, "spectral_sc_medium"),
                    WidgetFontInfo("Spectral Sc semibold", semibold, "spectral_sc_semibold"),
                    WidgetFontInfo("Spectral Sc bold", bold, "spectral_sc_bold"),
                    WidgetFontInfo("Spectral Sc extrabold", extrabold, "spectral_sc_extrabold")
                )
            )

            //Spicy rice
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(spicyRice, normal, "spicy_rice"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //spirax
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(spirax, normal, "spirax"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Sree krushnadevaraya
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(sreekrushnadevaraya, normal, "sree_krushnadevaraya"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Sriracha
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(sriracha, normal, "sriracha"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Srisakdi
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(srisakdi, normal, "srisakdi"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Srisakdi bold", bold, "srisakdi_bold")
                )
            )

            //Staatliches
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(staatliches, normal, "staatliches"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Stalemate
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(stalemate, normal, "stalemate"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Stalinist one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(stalinistOne, normal, "stalinist_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Stardos_stencil
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(stardos_stencil, normal, "stardos_stencil"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Stick
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(stick, normal, "stick"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Suez one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(suezOne, normal, "suez_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Sunshiney
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(sunshiney, normal, "sunshiney"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Supermercado one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(supermercadoOne, normal, "supermercado_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Swanky and moo moo
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(swankyAndMooMoo, normal, "swanky_and_moo_moo"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Syncopate
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(syncopate, normal, "syncopate"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Syncopate bold", normal, "syncopate_bold")
                )
            )

            //Syne
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(syne, normal, "syne"),
                    null,
                    null,
                    WidgetFontInfo("Syne medium", medium, "syne_medium"),
                    WidgetFontInfo("Syne semibold", semibold, "syne_semibold"),
                    WidgetFontInfo("Sune bold", bold, "syne_bold"),
                    WidgetFontInfo("Syne extrabold", extrabold, "syne_extrabold")
                )
            )

            //Syne mono
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(syneMono, normal, "syne_mono"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Syne tactile
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(syneTactile, normal, "syne_tactile"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Tangerine
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(tangerine, normal, "tangerine"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Tangerine bold", bold, "tangerine_bold")
                )
            )

            //The girl next door
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(theGirlNextDoor, normal, "the_girl_next_door"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Titan one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(titanOne, normal, "titan_one"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Trade Winds
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(tradeWinds, normal, "trade_winds"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Train one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(trainOne, normal, "train_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Trispace
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(trispace, normal, "trispace"),
                    WidgetFontInfo("Trispace light", light, "trispace_light"),
                    null,
                    WidgetFontInfo("Trispace medium", medium, "trispace_medium"),
                    WidgetFontInfo("Trispace semibold", semibold, "trispace_semibold"),
                    WidgetFontInfo("Trispace bold", bold, "trispace_bold"),
                    WidgetFontInfo("Trispace extrabold", extrabold, "trispace_extrabold")
                )
            )

            //Trocchi
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(trocchi, normal, "trocchi"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Ubuntu
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(ubuntu, normal, "ubuntu"),
                    WidgetFontInfo("Ubuntu light", light, "ubuntu_light"),
                    WidgetFontInfo("Ubuntu italic", italic, "ubuntu_italic"),
                    WidgetFontInfo("Ubuntu medium", semibold, "ubuntu_medium"),
                    null,
                    WidgetFontInfo("Ubuntu bold", bold, "ubuntu_bold"),
                    null
                )
            )

            //Ultra
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(ultra, normal, "ultra"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Uncial antiqua
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(uncialAntiqua, normal, "uncial_antiqua"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //underdog
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(underdog, normal, "underdog"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Unica one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(unicaOne, normal, "unica_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Unifrakturmaguntia
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(unifrakturmaguntia, normal, "unifrakturmaguntia"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Unkempt
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(unkempt, normal, "unkempt"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Unkempt bold", bold, "unkempt_bold")
                )
            )

            //unlock
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(unlock, normal, "unlock"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Vampiro One
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(vampiroOne, normal, "vampiro_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Vast shadow
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(vastShadow, normal, "vast_shadow"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Vibur
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(vibur, normal, "vibur"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Vollkorn
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(vollkornSc, normal, "vollkorn_sc"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Vollkorn sc semibold", semibold, "vollkorn_sc_semibold"),
                    WidgetFontInfo("Vollkorn sc bold", bold, "vollkorn_sc_bold"),
                    WidgetFontInfo("Vollkorn sc extrabold", extrabold, "vollkorn_sc_black")
                )
            )

            //Vt323
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(vt323, normal, "vt323"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Waiting for the sunrise
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(waitingForTheSunrise, normal, "waiting_for_the_sunrise"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Wallpoet
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(wallpoet, normal, "wallpoet"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Walter Turncoat
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(walterTurncoat, normal, "walter_turncoat"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Warnes
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(warnes, normal, "warnes"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Wellfleet
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(wellfleet, normal, "wellfleet"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Wendy one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(wendyOne, normal, "wendy_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Work sans
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(workSans, normal, "work_sans"),
                    WidgetFontInfo("Work sans light", light, "work_sans_light"),
                    null,
                    WidgetFontInfo("Work sans medium", medium, "work_sans_medium"),
                    WidgetFontInfo("Work sans semibold", semibold, "work_sans_semibold"),
                    WidgetFontInfo("Work sans bold", bold, "work_sans_extrabold"),
                    WidgetFontInfo("Work sans extrabold", extrabold, "work_sans_black")
                )
            )


            //xanh mono
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(xanhMono, normal, "xanh_mono"),
                    WidgetFontInfo("Xanh mono italic", italic, "xanh_mono_italic"),
                    null,
                    null,
                    null
                )
            )


            //Yatra one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(yatraOne, normal, "yatra_one"),
                    null,
                    null,
                    null,
                    null
                )
            )


            //Yellowtail
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(yellowtail, normal, "yellowtail"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Yeseva one
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(yesevaOne, normal, "yeseva_one"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Yesteryear
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(yesteryear, normal, "yesteryear"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Yusei magic
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(yuseiMagic, normal, "yusei_magic"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //ZcoolKualie
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(zcoolKuaile, normal, "zcool_kualie"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //ZcoolQingleHuangyou
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(zcoolQingleHangyou, normal, "zcool_qingle_huangyou"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //ZcoolXiaowei
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(zcoolXiaowei, normal, "zcool_xiaowei"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //Zeyada
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(zeyada, normal, "zeyada"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //ZhiMangXing
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(zhiMangXing, normal, "zhiMangXing"),
                    null,
                    null,
                    null,
                    null
                )
            )

            //ZillaSlabHighlight
            fontsList.add(
                FontItemData(
                    WidgetFontInfo(zillaSlabHighlight, normal, "zilla_slab_highlight"),
                    null,
                    null,
                    null,
                    WidgetFontInfo("Zilla slab highlight bold", bold, "zilla_slab_highlight_bold")
                )
            )


            val gson = Gson()
            val fontsJson = gson.toJson(fontsList)
            sharedPreferences.edit().putString("fontslist5", fontsJson).apply()

            return fontsList
        }

        fun showSnackbar(context: Context, text: String, view: View, isDark: Boolean): Snackbar {
            val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
            snackbar.view.background =
                ContextCompat.getDrawable(context, R.drawable.snackbar_background)
            if (isDark) {
                snackbar.setTextColor(ContextCompat.getColor(context, R.color.Black))
                snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.Black))
                snackbar.view.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            } else {
                snackbar.setTextColor(ContextCompat.getColor(context, R.color.white))
                snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.white))
                snackbar.view.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.darkGrey))
            }
            return snackbar
        }

        fun drawableToBitmap(drawable: Drawable): Bitmap? {
            var bitmap: Bitmap? = null
            if (drawable is BitmapDrawable) {
                val bitmapDrawable = drawable
                if (bitmapDrawable.bitmap != null) {
                    return bitmapDrawable.bitmap
                }
            }
            bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
            drawable.draw(canvas)
            return bitmap
        }

        fun saveImageBitmap(context: Context, imageName: String, bitmap: Bitmap): String? {
            //Create the content values for saving image;
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "$imageName.jpeg")
            contentValues.put(MediaStore.Images.Media.TITLE, imageName)
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES.toString() + "/Any Text Widget"
                )
                contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 1)
            } else {
                val file = File(
                    Environment.getExternalStorageDirectory()
                        .toString() + "/" + Environment.DIRECTORY_PICTURES + "/Any Text Widget"
                )
                if (!file.exists()) {
                    file.mkdirs()
                }
                contentValues.put(
                    MediaStore.Images.Media.DATA,
                    Environment.getExternalStorageDirectory()
                        .toString() + "/" + Environment.DIRECTORY_PICTURES + "/Any Text Widget/" + imageName + ".jpeg"
                )
            }
            val externalUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            //Insert the content values using the content resolver and get the saved uri;
            val contentResolver = context.contentResolver
            var savedUri: Uri? = contentResolver.insert(externalUri, contentValues)

            //Save the image data to the saved uri;
            var outputStream: OutputStream? = null

            //Check if saved uri is valid and then proceed to save the data;
            try {
                if (savedUri == null) {
                    throw IOException("no image")
                } else {
                    outputStream = contentResolver.openOutputStream(savedUri)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)
                }
            } catch (e: IOException) {
                //Remove the saved uri in case if saving data to it has failed;
                if (savedUri != null) {
                    contentResolver.delete(savedUri, null, null)
                    savedUri = null
                }
                e.printStackTrace()
                return "No image"
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.flush()
                        outputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                //After saving data change IS_PENDING to 0 so it is available to read for other mediums;
                if (savedUri != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(savedUri, contentValues, null, null)
                    }
                }
            }
            return savedUri.toString()
        }

        fun updateUIWidgets(context: Context) {
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(
                    context,
                    YourWidget::class.java
                )
            )


            val intent = Intent(context, YourWidget::class.java)
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

            context.sendBroadcast(intent)
        }

        fun getBitmapWithContentPath(context: Context, path: String?, sampleSize: Int): Bitmap? {
            val cachePath = "${context.cacheDir}/${path}"
            var bitmap: Bitmap? = null
            val inputStream: InputStream = FileInputStream(File(cachePath))
            val bitmapOptions: BitmapFactory.Options = BitmapFactory.Options()
            bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565
            bitmapOptions.inSampleSize = sampleSize

            val nonRotatedBitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions)!!

            bitmap = checkOrientationAndGetBitmap(inputStream, nonRotatedBitmap)
            return bitmap
        }

        suspend fun saveImageToCache(
            context: Context,
            bitmap: Bitmap,
            listener: WidgetSaveInterface
        ) {
            //Write the bitmap to cache.
            val endPath = "AnyTextWidget/" + AppUtils.uniqueContentNameGenerator("Image") + ".png"
            val cachePath =
                context.cacheDir.toString() + "/" + endPath
            val folder = File("${context.cacheDir}/AnyTextWidget/")
            if (!folder.exists()) {
                folder.mkdir()
            }

            val file = File(cachePath)
            var os: FileOutputStream? = null
            withContext(Dispatchers.IO) {
                try {
                    os = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os!!)
                    withContext(Dispatchers.Main) {
                        listener.widgetSaved(endPath)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    os!!.flush()
                    os!!.close()
                }
            }
        }

        private fun checkOrientationAndGetBitmap(
            inputStream: InputStream,
            bitmap: Bitmap
        ): Bitmap? {
            var ei: ExifInterface? = null
            var rotatedBitmap: Bitmap? = null
            try {
                ei = ExifInterface(inputStream)
                val orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(
                        bitmap,
                        180f
                    )

                    ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(
                        bitmap,
                        270f
                    )

                    ExifInterface.ORIENTATION_NORMAL -> {
                        rotatedBitmap = bitmap
                        return rotatedBitmap
                    }

                    else -> {
                        rotatedBitmap = bitmap
                        return rotatedBitmap
                    }
                }
                bitmap.recycle()
                return rotatedBitmap
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return rotatedBitmap
        }

        fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }

        /*        suspend fun getUpdatedFont(
                    fontsList: MutableList<FontItemData>,
                    name: String,
                    style: String
                ): UIFontData {
                    for (data in fontsList) {
                        if (data.fontName.equals(name)) {
                            if (style.equals(normal)) {
                                return data.normalUIFontData!!
                            }
                            if (style.equals(italic)) {
                                return data.italicUIFontData!!
                            }
                            if (style.equals(medium)) {
                                return data.mediumUIFontData!!
                            }
                            if (style.equals(semibold)) {
                                return data.semiboldUIFontData!!
                            }
                            if (style.equals(bold)) {
                                return data.boldUIFontData!!
                            }
                        }
                    }
                    return UIFontData(openSans, R.font.open_sans)
                }

                suspend fun createWidgetInfoWithID(
                    fontsList: MutableList<FontItemData>,
                    fontID: Int
                ): WidgetFontInfo {
                    var widgetFontInfo = WidgetFontInfo()
                    for (data in fontsList) {
                        if (fontID == data.normalUIFontData!!.id) {
                            widgetFontInfo.fontName = data.fontName
                            widgetFontInfo.fontStyle = normal
                            return widgetFontInfo
                        }

                        if (data.italicUIFontData != null) {
                            if (fontID == data.italicUIFontData!!.id) {
                                widgetFontInfo.fontName = data.fontName
                                widgetFontInfo.fontStyle = italic
                                return widgetFontInfo
                            }
                        }

                        if (data.mediumUIFontData != null) {
                            if (fontID == data.mediumUIFontData!!.id) {
                                widgetFontInfo.fontName = data.fontName
                                widgetFontInfo.fontStyle = medium
                                return widgetFontInfo
                            }
                        }

                        if (data.semiboldUIFontData != null) {
                            if (fontID == data.semiboldUIFontData!!.id) {
                                widgetFontInfo.fontName = data.fontName
                                widgetFontInfo.fontStyle = semibold
                                return widgetFontInfo
                            }
                        }

                        if (data.boldUIFontData != null) {
                            if (fontID == data.boldUIFontData!!.id) {
                                widgetFontInfo.fontName = data.fontName
                                widgetFontInfo.fontStyle = bold
                                return widgetFontInfo
                            }
                        }
                    }
                    return widgetFontInfo
                }*/

        suspend fun calculateSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            val height = options.outHeight
            val width = options.outWidth

            var inSampleSize = 1

            //Check if the image width and height are higher than required
            if (height > reqHeight || width > reqWidth) {
                val halfHeight: Int = height / 2
                val halfWidth: Int = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

        suspend fun saveWidgetToDevice(
            context: Context,
            widgetData: WidgetData,
            saveInterface: WidgetSaveInterface
        ) {

            val widgetName = uniqueContentNameGenerator("Widget")

            val appWidgetData = AppWidgetData()
            appWidgetData.widgetData = widgetData

            val imageByteList: MutableList<ByteArray>
            imageByteList = ArrayList()
            if (appWidgetData.widgetData!!.widgetBackGroundType.equals("image")) {
                if (widgetData.widgetMultiImageList != null) {
                    for (imageUri in widgetData.widgetMultiImageList!!) {
                        val bitmap = getBitmapWithContentPath(context, imageUri, 1)
                        val arrayOutputStream = ByteArrayOutputStream()
                        if (bitmap != null) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayOutputStream)
                            val byteArray = arrayOutputStream.toByteArray()
                            appWidgetData.ifBackgroundImageBytes = byteArray
                            imageByteList.add(byteArray)
                            appWidgetData.ifBackgroundImageBytesList = imageByteList
                            bitmap.recycle()
                        }
                    }
                }


            }

            delay(2000)

            //Create the content values for saving widget;
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$widgetName.atw")
            contentValues.put(MediaStore.MediaColumns.TITLE, widgetName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/atw")
            contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/Any text widget"
                )
                contentValues.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1)
            } else {
                val file = File(
                    Environment.getExternalStorageDirectory()
                        .toString() + "/" + Environment.DIRECTORY_DOWNLOADS + "/Any text widget"
                )
                if (!file.exists()) {
                    file.mkdirs()
                }
                contentValues.put(
                    MediaStore.MediaColumns.DATA,
                    Environment.getExternalStorageDirectory()
                        .toString() + "/" + Environment.DIRECTORY_DOWNLOADS + "/Any text widget/" + widgetName + ".atw"
                )
            }

            val externalUri = MediaStore.Files.getContentUri("external")

            //Insert the content values using the content resolver and get the saved uri;
            val contentResolver: ContentResolver = context.contentResolver

            val savedUri = contentResolver.insert(externalUri, contentValues)


            //Save the image data to the saved uri;
            var outputStream: ObjectOutputStream? = null


            //Check if saved uri is valid and then proceed to save the data;
            try {
                if (savedUri == null) {
                    throw IOException("Unable to download widget")
                } else {
                    outputStream = ObjectOutputStream(contentResolver.openOutputStream(savedUri))
                    outputStream.writeObject(appWidgetData)
                }
            } catch (e: IOException) {
                //Remove the saved uri in case if saving data to it has failed;
                if (savedUri != null) {
                    contentResolver.delete(savedUri, null, null)
                }
                e.printStackTrace()
                saveInterface.widgetSaveFailed()
                return
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.flush()
                        outputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                //After saving data, change IS_PENDING to 0 so it is available to read for other mediums;
                if (savedUri != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                        contentResolver.update(savedUri, contentValues, null, null)
                    }
                }
            }

            saveInterface.widgetSaved(savedUri.toString())
        }

        fun uniqueContentNameGenerator(name: String): String {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                Date()
            )
            return name + "_" + timeStamp
        }

        fun getContentFileName(context: Context, uri: Uri): String {
            var name = "content file name"
            try {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                if (cursor != null) {
                    val index = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    name = cursor.getString(index)
                    cursor.close()
                }
            } catch (e: CursorIndexOutOfBoundsException) {
                e.printStackTrace()
            }
            return name
        }

        fun getSavedColors(context: Context): MutableList<ColorData> {

            val sharedPreferences = context.getSharedPreferences("colorspref", Context.MODE_PRIVATE)

            val savedColors = sharedPreferences.getString("savedcolors", null)

            if (savedColors != null) {
                val gson = Gson()
                val type = object : TypeToken<MutableList<ColorData>>() {}.type

                return gson.fromJson(savedColors, type)
            } else {
                return ArrayList<ColorData>()
            }
        }

        fun getDefaultColors(context: Context): MutableList<ColorData> {
            val dataList = ArrayList<ColorData>()
            val sharedPreferences = context.getSharedPreferences("colorspref", Context.MODE_PRIVATE)
            val defaultColorsJSON = sharedPreferences.getString("defaultcolors", null)
            if (defaultColorsJSON == null) {
                //Add the default colors to shared preferences;
                dataList.addAll(addDefaultColors(context))
            } else {
                val defaultColors = getDefaultColorsFromJson(defaultColorsJSON)
                dataList.addAll(defaultColors)
            }

            return dataList
        }

        suspend fun saveNewColor(context: Context, data: ColorData) {
            //Check if the added new color is a valid color
            try {
                Color.parseColor(data.colorHexCode)
            } catch (e: IllegalArgumentException) {
                data.colorHexCode = "#000000"
                e.printStackTrace()
            }

            val sharedPreferences = context.getSharedPreferences("colorspref", Context.MODE_PRIVATE)

            val dataList = ArrayList<ColorData>()

            dataList.addAll(getSavedColors(context))

            dataList.add(data)

            //Save back the updated list
            val gson = Gson()
            val json = gson.toJson(dataList)
            sharedPreferences.edit().putString("savedcolors", json).apply()
        }

        suspend fun saveImageBytes(
            context: Context,
            imageName: String,
            byteArray: ByteArray
        ): String {
            //Create the content values for saving image;
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "$imageName.jpeg")
            contentValues.put(MediaStore.Images.Media.TITLE, imageName)
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/Any text widget images"
                )
                contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 1)
            } else {
                val file = File(
                    Environment.getExternalStorageDirectory()
                        .toString() + "/" + Environment.DIRECTORY_PICTURES + "/Any text widget images"
                )
                if (!file.exists()) {
                    file.mkdirs()
                }
                contentValues.put(
                    MediaStore.Images.Media.DATA,
                    Environment.getExternalStorageDirectory()
                        .toString() + "/" + Environment.DIRECTORY_PICTURES + "/Any text widget images/" + imageName + ".jpeg"
                )
            }
            val externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            //Insert the content values using the content resolver and get the saved uri;
            val contentResolver = context.contentResolver
            var savedUri = contentResolver.insert(externalUri, contentValues)

            //Save the image data to the saved uri;
            var outputStream: OutputStream? = null

            //Check if saved uri is valid and then proceed to save the data;
            try {
                if (savedUri == null) {
                    throw IOException("unable to save image")
                } else {
                    outputStream = contentResolver.openOutputStream(savedUri)
                    outputStream!!.write(byteArray)
                }
            } catch (e: IOException) {
                //Remove the saved uri in case if saving data to it has failed;
                if (savedUri != null) {
                    contentResolver.delete(savedUri, null, null)
                    savedUri = null
                }
                e.printStackTrace()
                return "unable to save image"
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.flush()
                        outputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                //After saving data change IS_PENDING to 0 so it is available to read for other mediums;
                if (savedUri != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(savedUri, contentValues, null, null)
                    }
                }
            }
            return savedUri.toString()
        }

        fun contentExists(uri: String?, context: Context): Boolean {
            return File(uri!!).exists()
        }

        fun updateSingleUIWidget(context: Context, widgetUIID: Int) {
            val intent = Intent(context, YourWidget::class.java)
            intent.action = "android.appwidget.action.APPWIDGET_UPDATE"
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(widgetUIID))
            context.sendBroadcast(intent)

            val appWidgetManager = AppWidgetManager.getInstance(context)

        }

        suspend fun getInstalledApps(context: Context): MutableList<String> {
            val packages = context.packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            val filteredList = ArrayList<String>()
            for (pkg in packages) {
                if (context.packageManager.getLaunchIntentForPackage(pkg.packageName) != null) {
                    filteredList.add(pkg.packageName)
                }
            }
            return filteredList
        }

        fun getSavedWidgets(json: String): MutableList<WidgetData> {
            val gson = Gson()
            val type = object : TypeToken<MutableList<WidgetData>>() {}.type
            return gson.fromJson(json, type)
        }

        suspend fun getWidgetDataWithID(context: Context, id: String): WidgetData {
            var requiredData: WidgetData? = null
            val sharedPref = context.getSharedPreferences("widgetspref", MODE_PRIVATE)

            val dataList: MutableList<WidgetData> = ArrayList()

            val savedWidgetsJSON = sharedPref.getString("savedwidgets", null)
            if (savedWidgetsJSON != null) {
                val savedWidgetsList = getSavedWidgets(savedWidgetsJSON)
                dataList.addAll(savedWidgetsList)
            }

            for (data in dataList) {
                if (data.widgetID == id) {
                    requiredData = data
                    break
                }
            }

            return requiredData!!
        }

        suspend fun makeWidgetImage(
            context: Context,
            widgetData: WidgetData
        ): Bitmap {
            //Create the image
            val view = WidgetLayoutBinding.inflate(LayoutInflater.from(context))

            //set the text
            view.widgetText.text = widgetData.widgetText

            //Set the text color
            if (widgetData.widgetTextColor?.colorHexCode != null) {
                try {
                    view.widgetText.setTextColor(
                        ColorStateList.valueOf(
                            Color.parseColor(
                                widgetData.widgetTextColor?.colorHexCode
                            )
                        )
                    )
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }

            //Set the text size
            view.widgetText.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                widgetData.widgetTextSize.toFloat()
            )

            //Set the text font
            try {
                if (widgetData.widgetFontInfo!!.sourceName != "NA") {
                    val id = context.resources.getIdentifier(
                        widgetData.widgetFontInfo!!.sourceName,
                        "font",
                        context.packageName
                    )
                    val typeFace = ResourcesCompat.getFont(context, id)
                    view.widgetText.typeface = typeFace
                } else {
                    val fontText: String = if (widgetData.widgetFontInfo!!.fontStyle == "normal") {
                        widgetData.widgetFontInfo!!.fontName.lowercase()
                            .replace(" ", "_", true)
                    } else {
                        widgetData.widgetFontInfo!!.fontName.lowercase()
                            .replace(
                                " ",
                                "_",
                                true
                            ) + "_" + widgetData.widgetFontInfo!!.fontStyle.lowercase()
                    }


                    val id = context.resources.getIdentifier(fontText, "font", context.packageName)
                    val typeFace2 = ResourcesCompat.getFont(context, id)
                    view.widgetText.typeface = typeFace2
                }
            } catch (e: Resources.NotFoundException) {
                view.widgetText.setTypeface(
                    ResourcesCompat.getFont(
                        context,
                        R.font.open_sans_semibold
                    )
                )

                e.printStackTrace()
            }

            //Set the background color
            if (widgetData.widgetBackGroundType.equals("color")) {
                try {

                    view.widgetcard.setCardBackgroundColor(
                        ColorStateList.valueOf(
                            Color.parseColor(
                                widgetData.widgetBackgroundColor!!.colorHexCode
                            )
                        )
                    )

                } catch (e: IllegalArgumentException) {
                    view.widgetcard.setCardBackgroundColor(
                        ResourcesCompat.getColor(
                            context.resources,
                            R.color.white,
                            null
                        )
                    )
                    e.printStackTrace()
                }
            }

            //Set the background gradient
            if (widgetData.widgetBackGroundType.equals("gradient")) {
                if (widgetData.widgetBackgroundGradient != null) {
                    view.widgetGradientBgr.visibility = View.VISIBLE
                    try {
                        val sourceName =
                            "no_corners_" + widgetData.widgetBackgroundGradient!!.sourceName

                        val gradient = context.resources.getIdentifier(
                            sourceName,
                            "drawable",
                            context.packageName
                        )

                        val drawable = ContextCompat.getDrawable(context, gradient)

                        view.widgetGradientBgr.setImageDrawable(drawable)
                    } catch (e: Resources.NotFoundException) {
                        e.printStackTrace()
                    }
                }
            }

            //Set the corners
            view.widgetcard.radius = AppUtils.dptopx(context, 30).toFloat()
            if (widgetData.widgetRoundCorners != null) {
                if (widgetData.widgetRoundCorners) {
                    view.widgetcard.radius = AppUtils.dptopx(context, 30).toFloat()
                } else {
                    view.widgetcard.radius = 0f
                }
            }


            //Set the gravities
            val verticalGravity: Int
            val horizontalGravity: Int
            if (widgetData.widgetTextVerticalGravity != null) {
                verticalGravity = widgetData.widgetTextVerticalGravity!!.gravityValue
            } else {
                verticalGravity = Gravity.CENTER_VERTICAL
            }

            if (widgetData.widgetTextHorizontalGravity != null) {
                horizontalGravity = widgetData.widgetTextHorizontalGravity!!.gravityValue
            } else {
                horizontalGravity = Gravity.CENTER_HORIZONTAL
            }

            view.widgetText.gravity = verticalGravity or horizontalGravity


            //Set the outline and it's color
            view.widgetcard.strokeWidth = 0
            if (widgetData.outlineEnabled) {
                view.widgetcard.strokeWidth =
                    AppUtils.dptopx(context, widgetData.widgetOutlineWidth)

                try {
                    view.widgetcard.setStrokeColor(
                        ColorStateList.valueOf(
                            Color.parseColor(
                                widgetData.widgetOutlineColor!!.colorHexCode
                            )
                        )
                    )
                } catch (e: IllegalArgumentException) {
                    view.widgetcard.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#000000")))
                    e.printStackTrace()
                }

            }


            //Set the text shadow
            if (widgetData.textShadowEnabled) {
                if (widgetData.textShadowData != null) {
                    try {
                        view.widgetText.setShadowLayer(
                            dptopx(context, widgetData.textShadowData!!.shadowRadius).toFloat(),
                            dptopx(context, widgetData.textShadowData!!.horizontalDir).toFloat(),
                            dptopx(context, widgetData.textShadowData!!.verticalDir).toFloat(),
                            Color.parseColor(widgetData.textShadowData!!.shadowColor!!.colorHexCode)
                        )
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    }

                }
            }


            //Build the image
            view.widgetcard.measure(
                View.MeasureSpec.makeMeasureSpec(
                    context.resources.displayMetrics.widthPixels,
                    View.MeasureSpec.EXACTLY
                ), View.MeasureSpec.makeMeasureSpec(dptopx(context, 200), View.MeasureSpec.EXACTLY)
            )

            view.widgetcard.layout(
                0,
                0,
                view.widgetcard.measuredWidth,
                view.widgetcard.measuredHeight
            )
            val bitmap = Bitmap.createBitmap(
                view.widgetcard.width,
                view.widgetcard.height,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)

            view.widgetcard.draw(canvas)

            return bitmap
        }


        fun getGradientsFromJson(json: String): MutableList<GradientData> {
            val gson = Gson()
            val type = object : TypeToken<MutableList<GradientData>>() {}.type
            return gson.fromJson(json, type)
        }

        suspend fun makeDefaultGradients(context: Context): MutableList<GradientData> {

            val dataList = ArrayList<GradientData>()

            //Purple green
            val purpleGreen = GradientData()
            purpleGreen.name = "Purple green"
            purpleGreen.sourceName = "gradient_purple_green"
            purpleGreen.colorOne = "#6200EE"
            purpleGreen.colorTwo = "#09dc76"

            dataList.add(purpleGreen)


            //Blue white
            val blueWhite = GradientData()
            blueWhite.name = "Blue white"
            blueWhite.sourceName = "gradient_blue_white"
            blueWhite.colorOne = "#0762e9"
            blueWhite.colorTwo = "#ffffff"

            dataList.add(blueWhite)


            //Yellow black
            val yellowBlack = GradientData()
            yellowBlack.name = "Yellow black"
            yellowBlack.sourceName = "gradient_yellow_black"
            yellowBlack.colorOne = "#FFEB3B"
            yellowBlack.colorTwo = "#000000"

            dataList.add(yellowBlack)


            //Red white
            val redWhite = GradientData()
            redWhite.name = "Red white"
            redWhite.sourceName = "gradient_red_white"
            redWhite.colorOne = "#D50000"
            redWhite.colorTwo = "#ffffff"

            dataList.add(redWhite)


            //Blue hue
            val blueHue = GradientData()
            blueHue.name = "Blue hue"
            blueHue.sourceName = "gradient_blue_hue"
            blueHue.colorOne = "#0D47A1"
            blueHue.colorTwo = "#81D4FA"

            dataList.add(blueHue)


            //Black white
            val blackWhite = GradientData()
            blackWhite.name = "Black white"
            blackWhite.sourceName = "gradient_black_white"
            blackWhite.colorOne = "#000000"
            blackWhite.colorTwo = "#ffffff"

            dataList.add(blackWhite)


            //Pink purple
            val pinkPurple = GradientData()
            pinkPurple.name = "Pink purple"
            pinkPurple.sourceName = "gradient_pink_purple"
            pinkPurple.colorOne = "#FF80AB"
            pinkPurple.colorTwo = "#9a66f4"

            dataList.add(pinkPurple)


            //Teal purple
            val tealPurple = GradientData()
            tealPurple.name = "Teal purple"
            tealPurple.sourceName = "gradient_teal_purple"
            tealPurple.colorOne = "#64FFDA"
            tealPurple.colorTwo = "#9a66f4"

            dataList.add(tealPurple)


            //Orange green
            val orangeGreen = GradientData()
            orangeGreen.name = "Orange green"
            orangeGreen.sourceName = "gradient_orange_green"
            orangeGreen.colorOne = "#FF5722"
            orangeGreen.colorTwo = "#09dc76"

            dataList.add(orangeGreen)


            //Indigo yellow
            val indigoYellow = GradientData()
            indigoYellow.name = "Indigo yellow"
            indigoYellow.sourceName = "gradient_indigo_yellow"
            indigoYellow.colorOne = "#3F51B5"
            indigoYellow.colorTwo = "#FFEB3B"

            dataList.add(indigoYellow)


            //Lime blue
            val limeBlue = GradientData()
            limeBlue.name = "Lime blue"
            limeBlue.sourceName = "gradient_lime_blue"
            limeBlue.colorOne = "#64DD17"
            limeBlue.colorTwo = "#039BE5"
            dataList.add(limeBlue)


            //Blue purple
            val bluePurple = GradientData()
            bluePurple.name = "Blue purple"
            bluePurple.sourceName = "gradient_blue_purple"
            bluePurple.colorOne = "#0D47A1"
            bluePurple.colorTwo = "#9a66f4"
            dataList.add(bluePurple)


            //Browm black
            val brownBlack = GradientData()
            brownBlack.name = "Brown black"
            brownBlack.sourceName = "gradient_brown_black"
            brownBlack.colorOne = "#3E2723"
            brownBlack.colorTwo = "#000000"
            dataList.add(brownBlack)


            //Cyan pink
            val cyanPink = GradientData()
            cyanPink.name = "Cyan pink"
            cyanPink.sourceName = "gradient_cyan_pink"
            cyanPink.colorOne = "#00BCD4"
            cyanPink.colorTwo = "#FF80AB"
            dataList.add(cyanPink)


            //Gray black
            val grayBlack = GradientData()
            grayBlack.name = "Gray black"
            grayBlack.sourceName = "gradient_gray_black"
            grayBlack.colorOne = "#616161"
            grayBlack.colorTwo = "#000000"
            dataList.add(grayBlack)


            //Green white
            val greenWhite = GradientData()
            greenWhite.name = "Green white"
            greenWhite.sourceName = "gradient_green_white"
            greenWhite.colorOne = "#09dc76"
            greenWhite.colorTwo = "#ffffff"
            dataList.add(greenWhite)


            //Pink red
            val pinkRed = GradientData()
            pinkRed.name = "Pink red"
            pinkRed.sourceName = "gradient_pink_red"
            pinkRed.colorOne = "#FF80AB"
            pinkRed.colorTwo = "#D50000"
            dataList.add(pinkRed)


            //Purple hue
            val purpleHue = GradientData()
            purpleHue.name = "Purple hue"
            purpleHue.sourceName = "gradient_purple_hue"
            purpleHue.colorOne = "#6200EE"
            purpleHue.colorTwo = "#9a66f4"
            dataList.add(purpleHue)


            //Purple red
            val purpleRed = GradientData()
            purpleRed.name = "Purple red"
            purpleRed.sourceName = "gradient_purple_red"
            purpleRed.colorOne = "#6200EE"
            purpleRed.colorTwo = "#FD4949"
            dataList.add(purpleRed)


            //Yellow red
            val yellowRed = GradientData()
            yellowRed.name = "Yellow red"
            yellowRed.sourceName = "gradient_yellow_red"
            yellowRed.colorOne = "#FFEB3B"
            yellowRed.colorTwo = "#FD4949"
            dataList.add(yellowRed)


            //Pink white
            val pinkWhite = GradientData()
            pinkWhite.name = "Pink white"
            pinkWhite.sourceName = "gradient_pink_white"
            pinkWhite.colorOne = "#FF80AB"
            pinkWhite.colorTwo = "#FFFFFF"
            dataList.add(pinkWhite)


            //Yellow brown
            val yellowBrown = GradientData()
            yellowBrown.name = "Yellow brown"
            yellowBrown.sourceName = "gradient_yellow_brown"
            yellowBrown.colorOne = "#FFEB3B"
            yellowBrown.colorTwo = "#3E2723"
            dataList.add(yellowBrown)


            //Teal blue
            val tealBlue = GradientData()
            tealBlue.name = "Teal blue"
            tealBlue.sourceName = "gradient_teal_blue"
            tealBlue.colorOne = "#64FFDA"
            tealBlue.colorTwo = "#0D47A1"
            dataList.add(tealBlue)


            //Orange red
            val orangeRed = GradientData()
            orangeRed.name = "Orange red"
            orangeRed.sourceName = "gradient_orange_red"
            orangeRed.colorOne = "#FF5722"
            orangeRed.colorTwo = "#D50000"
            dataList.add(orangeRed)


            //Red hue
            val redHue = GradientData()
            redHue.name = "Red hue"
            redHue.sourceName = "gradient_red_hue"
            redHue.colorOne = "#D50000"
            redHue.colorTwo = "#FFCDD2"
            dataList.add(redHue)


            //Yellow blue
            val yellowBlue = GradientData()
            yellowBlue.name = "Yellow blue"
            yellowBlue.sourceName = "gradient_yellow_blue"
            yellowBlue.colorOne = "#FFEB3B"
            yellowBlue.colorTwo = "#0D47A1"
            dataList.add(yellowBlue)


            //Purple yellow
            val purpleYellow = GradientData()
            purpleYellow.name = "Purple yellow"
            purpleYellow.sourceName = "gradient_purple_yellow"
            purpleYellow.colorOne = "#6200EE"
            purpleYellow.colorTwo = "#FFEB3B"
            dataList.add(purpleYellow)


            //Green hue
            val greenHue = GradientData()
            greenHue.name = "Green hue"
            greenHue.sourceName = "gradient_green_hue"
            greenHue.colorOne = "#096639"
            greenHue.colorTwo = "#09dc76"
            dataList.add(greenHue)


            //Green hue
            val tealPink = GradientData()
            tealPink.name = "Teal pink"
            tealPink.sourceName = "gradient_teal_pink"
            tealPink.colorOne = "#64FFDA"
            tealPink.colorTwo = "#FF80AB"
            dataList.add(tealPink)


            //Grey hue
            val greyHue = GradientData()
            greyHue.name = "${context.getString(R.string.grey)} hue"
            greyHue.sourceName = "gradient_grey_hue"
            greyHue.colorOne = "#212121"
            greyHue.colorTwo = "#9E9E9E"
            dataList.add(greyHue)


            //Teal White.
            val tealWhite = GradientData()
            tealWhite.name = "Teal white"
            tealWhite.sourceName = "gradient_teal_white"
            tealWhite.colorOne = "#64FFDA"
            tealWhite.colorTwo = "#FFFFFF"
            dataList.add(tealWhite)

            //Green Red.
            val greenRed = GradientData()
            greenRed.name = "Green red"
            greenRed.sourceName = "gradient_green_red"
            greenRed.colorOne = "#09dc76"
            greenRed.colorTwo = "#D50000"
            dataList.add(greenRed)

            //Green Blue.
            val greenBlue = GradientData()
            greenBlue.name = "Green blue"
            greenBlue.sourceName = "gradient_green_blue"
            greenBlue.colorOne = "#09dc76"
            greenBlue.colorTwo = "#0D47A1"
            dataList.add(greenBlue)

            //Yellow orange.
            val yellowOrange = GradientData()
            yellowOrange.name = "Yellow orange"
            yellowOrange.sourceName = "gradient_yellow_orange"
            yellowOrange.colorOne = "#FFEB3B"
            yellowOrange.colorTwo = "#FF5722"
            dataList.add(yellowOrange)

            //Yellow pink.
            val yellowPink = GradientData()
            yellowPink.name = "Yellow pink"
            yellowPink.sourceName = "gradient_yellow_pink"
            yellowPink.colorOne = "#FFEB3B"
            yellowPink.colorTwo = "#FF80AB"
            dataList.add(yellowPink)

            //Amber white.
            val amberWhite = GradientData()
            amberWhite.name = "Amber white"
            amberWhite.sourceName = "gradient_amber_white"
            amberWhite.colorOne = "#FFB300"
            amberWhite.colorTwo = "#FFFFFF"
            dataList.add(amberWhite)

            //Red black.
            val redBlack = GradientData()
            redBlack.name = "Red black"
            redBlack.sourceName = "gradient_red_black"
            redBlack.colorOne = "#D50000"
            redBlack.colorTwo = "#000000"
            dataList.add(redBlack)

            //Orange pink.
            val orangePink = GradientData()
            orangePink.name = "Orange pink"
            orangePink.sourceName = "gradient_orange_pink"
            orangePink.colorOne = "#FF5722"
            orangePink.colorTwo = "#FF80AB"
            dataList.add(orangePink)

            //Pink blue.
            val pinkBlue = GradientData()
            pinkBlue.name = "Pink blue"
            pinkBlue.sourceName = "gradient_pink_blue"
            pinkBlue.colorOne = "#FF80AB"
            pinkBlue.colorTwo = "#0D47A1"
            dataList.add(pinkBlue)

            //Amber green.
            val amberGreen = GradientData()
            amberGreen.name = "Amber green"
            amberGreen.sourceName = "gradient_amber_green"
            amberGreen.colorOne = "#FFB300"
            amberGreen.colorTwo = "#09dc76"
            dataList.add(amberGreen)

            //Peach Blue.
            dataList.add(GradientData("Peach Blue", "gradient_peach_blue", "#EEA47F", "#0D47A1"))

            //Peach Red.
            dataList.add(GradientData("Peach Red", "gradient_peach_red", "#EEA47F", "#e21313"))

            //Peach Green.
            dataList.add(GradientData("Peach Green", "gradient_peach_green", "#EEA47F", "#09dc76"))

            //Cyan Red.
            dataList.add(GradientData("Cyan Red", "gradient_cyan_red", "#00BCD4", "#D50000"))

            //Cyan Green.
            dataList.add(GradientData("Cyan Green", "gradient_cyan_green", "#00BCD4", "#09b964"))

            //Cyan Purple.
            dataList.add(GradientData("Cyan Purple", "gradient_cyan_purple", "#00BCD4", "#9355ef"))

            //Pastel green Dark green.
            dataList.add(
                GradientData(
                    "Pastel green Dark green",
                    "gradient_pastelgreen_darkgreen",
                    "#CAFFBF",
                    "#096639"
                )
            )

            //Pastel green Yellow
            dataList.add(
                GradientData(
                    "Pastel green Yellow",
                    "gradient_pastelgreen_yellow",
                    "#CAFFBF",
                    "#FFEB3B"
                )
            )

            //Pastel green Blue
            dataList.add(
                GradientData(
                    "Pastel green Blue",
                    "gradient_pastelgreen_blue",
                    "#CAFFBF",
                    "#FFEB3B"
                )
            )

            //Pastel pink Purple
            dataList.add(
                GradientData(
                    "Pastel pink Purple",
                    "gradient_pastelpink_purple",
                    "#FFADAD",
                    "#9355ef"
                )
            )

            saveGradients(context, dataList)

            return dataList
        }

        suspend fun getGradients(context: Context): MutableList<GradientData> {
            val dataList = ArrayList<GradientData>()
            val sharedPreferences =
                context.getSharedPreferences("gradientspref", Context.MODE_PRIVATE)
            val gradientsJSON = sharedPreferences.getString("gradients9", null)

            if (gradientsJSON == null) {
                //Add the default colors to shared preferences;
                dataList.addAll(makeDefaultGradients(context))
            } else {
                val gradients = getGradientsFromJson(gradientsJSON)
                dataList.addAll(gradients)
            }

            return dataList
        }

        suspend fun saveGradients(context: Context, gradientsList: MutableList<GradientData>) {
            val sharedPreferences = context.getSharedPreferences("gradientspref", MODE_PRIVATE)
            val gson = Gson()
            val json = gson.toJson(gradientsList)

            sharedPreferences.edit().putString("gradients9", json).apply()
        }

        suspend fun updateGradient(context: Context, gradientData: GradientData) {
            val gradientsList = getGradients(context)

            //Update by matching the id
            val iterator = gradientsList.iterator()
            while (iterator.hasNext()) {
                val data = iterator.next()
                val index = gradientsList.indexOf(data)

                if (data.sourceName == gradientData.sourceName) {
                    gradientsList[index] = gradientData

                    //Save the updated gradients list
                    saveGradients(context, gradientsList)
                }
            }
        }

        suspend fun addNewGradient(context: Context, gradientData: GradientData) {
            val gradientsList = getGradients(context)

            gradientsList.add(gradientData)

            //Save the updated list
            saveGradients(context, gradientsList)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        suspend fun getMaterialYouColors(context: Context): MutableList<ColorData> {
            val dataList = ArrayList<ColorData>()
            val materialYouOne = "Material you accent one"
            val materialYouTwo = "Material you accent two"
            val materialYouThree = "Material you accent three"

            val colorData1 = ColorData()
            colorData1.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent1_0
                )
            )
            colorData1.colorName = "$materialYouOne 0"
            colorData1.ID = "system_accent1_0"
            dataList.add(colorData1)


            val colorData2 = ColorData()
            colorData2.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent1_10
                )
            )
            colorData2.colorName = "$materialYouOne 10"
            colorData2.ID = "system_accent1_10"
            dataList.add(colorData2)


            val colorData3 = ColorData()
            colorData3.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent1_50
                )
            )
            colorData3.colorName = "$materialYouOne 50"
            colorData3.ID = "system_accent1_50"
            dataList.add(colorData3)


            val colorData4 = ColorData()
            colorData4.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent1_100
                )
            )
            colorData4.colorName = "$materialYouOne 100"
            colorData4.ID = "system_accent1_100"
            dataList.add(colorData4)

            val colorData6 = ColorData()
            colorData6.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent1_200
                )
            )
            colorData6.colorName = "$materialYouOne 200"
            colorData6.ID = "system_accent1_200"
            dataList.add(colorData6)


            val colorData7 = ColorData()
            colorData7.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent1_300
                )
            )
            colorData7.colorName = "$materialYouOne 300"
            colorData7.ID = "system_accent1_300"
            dataList.add(colorData7)


            val colorData8 = ColorData()
            colorData8.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent1_400
                )
            )
            colorData8.colorName = "$materialYouOne 400"
            colorData8.ID = "system_accent1_400"
            dataList.add(colorData8)


            val colorData9 = ColorData()
            colorData9.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent1_500
                )
            )
            colorData9.colorName = "$materialYouOne 500"
            colorData9.ID = "system_accent1_500"
            dataList.add(colorData9)


            val colorData10 = ColorData()
            colorData10.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent1_600
                )
            )
            colorData10.colorName = "$materialYouOne 600"
            colorData10.ID = "system_accent1_600"
            dataList.add(colorData10)


            val colorData11 = ColorData()
            colorData11.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent1_700
                )
            )
            colorData11.colorName = "$materialYouOne 700"
            colorData11.ID = "system_accent1_700"
            dataList.add(colorData11)

            val colorData12 = ColorData()
            colorData12.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent1_800
                )
            )
            colorData12.colorName = "$materialYouOne 800"
            colorData12.ID = "system_accent1_800"
            dataList.add(colorData12)


            val colorData13 = ColorData()
            colorData13.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent1_900
                )
            )
            colorData13.colorName = "$materialYouOne 900"
            colorData13.ID = "system_accent1_900"
            dataList.add(colorData13)


            val colorData14 = ColorData()
            colorData14.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent2_0
                )
            )
            colorData14.colorName = "$materialYouTwo 0"
            colorData14.ID = "system_accent2_0"
            dataList.add(colorData14)


            val colorData15 = ColorData()
            colorData15.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent2_10
                )
            )
            colorData15.colorName = "$materialYouTwo 10"
            colorData15.ID = "system_accent2_10"
            dataList.add(colorData15)


            val colorData16 = ColorData()
            colorData16.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent2_50
                )
            )
            colorData16.colorName = "$materialYouTwo 50"
            colorData16.ID = "system_accent2_50"
            dataList.add(colorData16)


            val colorData17 = ColorData()
            colorData17.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent2_100
                )
            )
            colorData17.colorName = "$materialYouTwo 100"
            colorData17.ID = "system_accent2_100"
            dataList.add(colorData17)

            val colorData18 = ColorData()
            colorData18.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent2_200
                )
            )
            colorData18.colorName = "$materialYouTwo 200"
            colorData18.ID = "system_accent2_200"
            dataList.add(colorData18)


            val colorData19 = ColorData()
            colorData19.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent2_300
                )
            )
            colorData19.colorName = "$materialYouTwo 300"
            colorData19.ID = "system_accent2_300"
            dataList.add(colorData19)


            val colorData20 = ColorData()
            colorData20.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent2_400
                )
            )
            colorData20.colorName = "$materialYouTwo 400"
            colorData20.ID = "system_accent2_400"
            dataList.add(colorData20)


            val colorData21 = ColorData()
            colorData21.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent2_500
                )
            )
            colorData21.colorName = "$materialYouTwo 500"
            colorData21.ID = "system_accent2_500"
            dataList.add(colorData21)


            val colorData22 = ColorData()
            colorData22.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent2_600
                )
            )
            colorData22.colorName = "$materialYouTwo 600"
            colorData22.ID = "system_accent2_600"
            dataList.add(colorData22)


            val colorData23 = ColorData()
            colorData23.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent2_700
                )
            )
            colorData23.colorName = "$materialYouTwo 700"
            colorData23.ID = "system_accent2_700"
            dataList.add(colorData23)

            val colorData24 = ColorData()
            colorData24.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent2_800
                )
            )
            colorData24.colorName = "$materialYouTwo 800"
            colorData24.ID = "system_accent2_800"
            dataList.add(colorData24)


            val colorData25 = ColorData()
            colorData25.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent2_900
                )
            )
            colorData25.colorName = "$materialYouTwo 900"
            colorData25.ID = "system_accent2_900"
            dataList.add(colorData25)


            val colorData26 = ColorData()
            colorData26.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent3_0
                )
            )
            colorData26.colorName = "$materialYouThree 0"
            colorData26.ID = "system_accent3_0"
            dataList.add(colorData26)


            val colorData27 = ColorData()
            colorData27.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent3_10
                )
            )
            colorData27.colorName = "$materialYouThree 10"
            colorData27.ID = "system_accent3_10"
            dataList.add(colorData27)


            val colorData28 = ColorData()
            colorData28.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent3_50
                )
            )
            colorData28.colorName = "$materialYouThree 50"
            colorData28.ID = "system_accent3_50"
            dataList.add(colorData28)


            val colorData29 = ColorData()
            colorData29.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent3_100
                )
            )
            colorData29.colorName = "$materialYouThree 100"
            colorData29.ID = "system_accent3_100"
            dataList.add(colorData29)

            val colorData30 = ColorData()
            colorData30.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent3_200
                )
            )
            colorData30.colorName = "$materialYouThree 200"
            colorData30.ID = "system_accent3_200"
            dataList.add(colorData30)


            val colorData31 = ColorData()
            colorData31.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent3_300
                )
            )
            colorData31.colorName = "$materialYouThree 300"
            colorData31.ID = "system_accent3_300"
            dataList.add(colorData31)


            val colorData32 = ColorData()
            colorData32.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent3_400
                )
            )
            colorData32.colorName = "$materialYouThree 400"
            colorData32.ID = "system_accent3_400"
            dataList.add(colorData32)


            val colorData33 = ColorData()
            colorData33.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent3_500
                )
            )
            colorData33.colorName = "$materialYouThree 500"
            colorData33.ID = "system_accent3_500"
            dataList.add(colorData33)


            val colorData34 = ColorData()
            colorData34.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent3_600
                )
            )
            colorData34.colorName = "$materialYouThree 600"
            colorData34.ID = "system_accent3_600"
            dataList.add(colorData34)


            val colorData35 = ColorData()
            colorData35.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent3_700
                )
            )
            colorData35.colorName = "$materialYouThree 700"
            colorData35.ID = "system_accent3_700"
            dataList.add(colorData35)

            val colorData36 = ColorData()
            colorData36.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent3_800
                )
            )
            colorData36.colorName = "$materialYouThree 800"
            colorData36.ID = "system_accent3_800"
            dataList.add(colorData36)


            val colorData37 = ColorData()
            colorData37.colorHexCode = "#" + Integer.toHexString(
                ContextCompat.getColor(
                    context,
                    android.R.color.system_accent3_900
                )
            )
            colorData37.colorName = "$materialYouThree 900"
            colorData37.ID = "system_accent3_900"
            dataList.add(colorData37)

            return dataList
        }

        fun hasStoragePermission(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED)
            } else {
                (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED)
            }
        }

        fun buildStoragePermission(context: Context): AlertDialog.Builder {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Permission required!")
            builder.setMessage("Storage permission is required for accessing the required files")
            builder.setPositiveButton(
                "GIVE",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(
                            context as AppCompatActivity, arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES
                            ), 67
                        )
                    } else {
                        ActivityCompat.requestPermissions(
                            context as AppCompatActivity, arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ), 67
                        )
                    }


                })
            return builder
        }


        suspend fun saveWidgetAsMp4(
            context: Context,
            fileName: String,
            bitmapList: MutableList<Bitmap>,
            audioPath: String,
            mediaListener: MediaListener
        ) {
            var saveProgress = 0
            //Save video directly to user's folder if there's no audio or mux if there's any.
            if (File(audioPath).exists()) {
                val tempVideoCachePath =
                    context.cacheDir.toString() + "/" + "Any Text Widget/" + fileName + ".mp4"
                val cacheFolder = File("${context.cacheDir}/Any Text Widget/")
                if (!cacheFolder.exists()) {
                    cacheFolder.mkdir()

                    saveProgress += 10
                    mediaListener.onMediaSaveProgress(saveProgress)
                }

                val tempVideoCache = File(tempVideoCachePath)
                if (!tempVideoCache.exists()) {
                    withContext(Dispatchers.IO) {
                        try {
                            tempVideoCache.createNewFile()
                            saveProgress += 10
                            mediaListener.onMediaSaveProgress(saveProgress)
                        } catch (e: IOException) {
                            mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                            Log.e("mp4Error", "error", e)
                            e.printStackTrace()
                            return@withContext
                        }
                    }
                } else {
                    saveProgress += 10
                    mediaListener.onMediaSaveProgress(saveProgress)
                }

                val sequenceEncoder = SequenceEncoder.createSequenceEncoder(tempVideoCache, 1)
                try {
                    bitmapList.forEach {
                        sequenceEncoder.encodeNativeFrame(BitmapUtil.fromBitmap(it))
                    }
                } catch (e: Exception) {
                    mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                    Log.e("mp4Error", "error", e)
                    e.printStackTrace()
                    return
                }
                sequenceEncoder.finish()
                saveProgress += 30
                mediaListener.onMediaSaveProgress(saveProgress)

                //Start muxing.
                muxAudioAndVideo(
                    tempVideoCachePath,
                    audioPath,
                    fileName,
                    saveProgress,
                    mediaListener
                )
            } else {
                val videoPath = Environment.getExternalStorageDirectory()
                    .toString() + "/" + Environment.DIRECTORY_MOVIES + "/Any Text Widget/" + fileName + ".mp4"

                val videoFolder =
                    File("${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_MOVIES}/Any Text Widget/")
                if (!videoFolder.exists()) {
                    videoFolder.mkdir()
                }

                val videoFile = File(videoPath)
                if (!videoFile.exists()) {
                    withContext(Dispatchers.IO) {
                        try {
                            videoFile.createNewFile()
                        } catch (e: IOException) {
                            mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                            e.printStackTrace()
                            return@withContext
                        }
                    }
                }

                saveProgress += 50
                mediaListener.onMediaSaveProgress(saveProgress)
                val sequenceEncoder = SequenceEncoder.createSequenceEncoder(videoFile, 1)

                bitmapList.forEachIndexed { index, bitmap ->
                    try {
                        sequenceEncoder.encodeNativeFrame(BitmapUtil.fromBitmap(bitmap))


                        if (index == bitmapList.size - 1) {
                            sequenceEncoder.finish()
                            mediaListener.onMediaSaved(videoPath)
                        } else {
                            saveProgress += 10
                            mediaListener.onMediaSaveProgress(saveProgress)
                        }
                    } catch (e: Exception) {
                        mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                        e.printStackTrace()

                    }
                }
            }
        }

        @SuppressLint("WrongConstant")
        private suspend fun muxAudioAndVideo(
            videoFile: String,
            audioFile: String,
            fileName: String,
            saveProgress: Int,
            mediaListener: MediaListener
        ) {
            var progress = saveProgress
            val videoExtractor = MediaExtractor()
            try {
                videoExtractor.setDataSource(videoFile)
            } catch (e: IOException) {
                mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                e.printStackTrace()
                return
            }
            videoExtractor.selectTrack(0)
            val videoFormat = videoExtractor.getTrackFormat(0)
            progress += 5
            mediaListener.onMediaSaveProgress(progress)

            val audioExtractor = MediaExtractor()
            try {
                audioExtractor.setDataSource(audioFile)
            } catch (e: IOException) {
                mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                e.printStackTrace()
                return
            }
            audioExtractor.selectTrack(0)
            val audioFormat = audioExtractor.getTrackFormat(0)
            progress += 5
            mediaListener.onMediaSaveProgress(progress)

            //Set the output file.
            val outputPath = Environment.getExternalStorageDirectory()
                .toString() + "/" + Environment.DIRECTORY_MOVIES + "/Any Text Widget/" + fileName + ".mp4"

            //Setup the muxer.
            val muxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            var videoIndex = 0
            var audioIndex = 0
            try {
                videoIndex = muxer.addTrack(videoFormat)
                audioIndex = muxer.addTrack(audioFormat)
            } catch (e: Exception) {
                mediaListener.onMediaSaveFailed("Unable to save the video, try again later")
                e.printStackTrace()


                return
            }

            muxer.start()
            progress += 10
            mediaListener.onMediaSaveProgress(progress)

            //Create byte size and buffer info.
            val byteSize = 1024 * 1024
            val buffer = ByteBuffer.allocate(byteSize)
            val bufferInfo = MediaCodec.BufferInfo()

            //Mux Video.
            while (true) {
                val vidByte = videoExtractor.readSampleData(buffer, 0)

                if (vidByte > 0) {
                    bufferInfo.presentationTimeUs = videoExtractor.sampleTime
                    bufferInfo.flags = videoExtractor.sampleFlags
                    bufferInfo.size = vidByte

                    try {
                        muxer.writeSampleData(videoIndex, buffer, bufferInfo)
                    } catch (e: IllegalArgumentException) {
                        mediaListener.onMediaSaveFailed("Unable to export the Video, try again later...")

                        muxer.stop()
                        muxer.release()

                        videoExtractor.release()
                        audioExtractor.release()

                        e.printStackTrace()
                        break
                    } catch (e: IllegalStateException) {
                        mediaListener.onMediaSaveFailed("Unable to export the Video, try again later...")

                        muxer.stop()
                        muxer.release()

                        videoExtractor.release()
                        audioExtractor.release()

                        e.printStackTrace()
                        break
                    }

                    videoExtractor.advance()
                } else {
                    progress += 15
                    mediaListener.onMediaSaveProgress(progress)
                    break
                }
            }

            //Mux audio.
            while (true) {
                val audioByte = audioExtractor.readSampleData(buffer, 0)

                if (audioByte > 0) {
                    bufferInfo.presentationTimeUs = audioExtractor.sampleTime
                    bufferInfo.flags = audioExtractor.sampleFlags
                    bufferInfo.size = audioByte

                    try {
                        muxer.writeSampleData(audioIndex, buffer, bufferInfo)
                    } catch (e: IllegalArgumentException) {
                        mediaListener.onMediaSaveFailed("Unable to export the Video, try again later...")

                        muxer.stop()
                        muxer.release()

                        videoExtractor.release()
                        audioExtractor.release()


                        e.printStackTrace()
                        break
                    } catch (e: IllegalStateException) {
                        mediaListener.onMediaSaveFailed("Unable to export the Video, try again later...")

                        muxer.stop()
                        muxer.release()

                        videoExtractor.release()
                        audioExtractor.release()

                        e.printStackTrace()

                        break
                    }
                    audioExtractor.advance()

                } else {
                    progress += 15
                    mediaListener.onMediaSaveProgress(progress)
                    break
                }
            }

            try {
                muxer.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            muxer.release()

            videoExtractor.release()
            audioExtractor.release()

            mediaListener.onMediaSaved(outputPath)
        }

        fun removeColor(context: Context, colorData: ColorData) {
            val currentSavedColors=ArrayList<ColorData>()

            val sharedPreferences = context.getSharedPreferences(
                "colorspref",
                Context.MODE_PRIVATE
            )
            //Get the current saved colors and add them to a list
            val savedColorsJSON= sharedPreferences.getString("savedcolors", null)
            if (savedColorsJSON!=null){
                currentSavedColors.addAll(getSavedColors(context = context))
            }

            if (!currentSavedColors.isEmpty()){
                val iterator=currentSavedColors.iterator()
                while (iterator.hasNext()){
                    val data=iterator.next()
                    if (data.ID==colorData.ID){
                        iterator.remove()

                        //Save back the updated list
                        val gson=Gson()
                        val json=gson.toJson(currentSavedColors)
                        sharedPreferences.edit().putString("savedcolors", json).apply()

                        break
                    }
                }
            }
        }

        fun saveTheNewColor(context: Context, data: ColorData){
            val sharedPreferences = context.getSharedPreferences(
                "colorspref",
                Context.MODE_PRIVATE
            )

            //Check if the added new color is a valid color
            try {
                Color.parseColor(data.colorHexCode)
            }
            catch (e: IllegalArgumentException){
                data.colorHexCode="#000000"
                e.printStackTrace()
            }


            val currentSavedColors=ArrayList<ColorData>()

            //Save the color to shared preferences
            //Get the current saved colors and add them to a list
            val savedColorsJSON= sharedPreferences.getString("savedcolors", null)
            if (savedColorsJSON!=null){
                currentSavedColors.addAll(getSavedColors(context))
            }

            //Add the new color
            currentSavedColors.add(data)

            //Save back the updated list
            val gson=Gson()
            val json=gson.toJson(currentSavedColors)
            sharedPreferences.edit().putString("savedcolors", json).apply()

        }

    }


    init {

    }

    @Composable
    fun BuildAlertDialog(title: String, description: String, confirmEvent: ()->Unit, dismissEvent: ()->Unit, onDismiss: ()->Unit) {
        val fontUtils = FontUtils()
        AlertDialog(
            title = {
                Text(
                    text = title,
                    fontFamily = fontUtils.openSans(FontWeight.SemiBold)
                )
            },
            text = {
                Text(
                    text = description,
                    fontFamily = fontUtils.openSans(FontWeight.Normal)
                )
            },
            onDismissRequest = onDismiss,
            confirmButton = { TextButton(onClick = confirmEvent){
                Text(text = "OK")
            } },
            dismissButton = { TextButton(onClick = dismissEvent){
                Text(text = "CANCEL")
            } })
    }
}