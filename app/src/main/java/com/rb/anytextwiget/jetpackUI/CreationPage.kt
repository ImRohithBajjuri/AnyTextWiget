package com.rb.anytextwiget.jetpackUI

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import coil.compose.rememberAsyncImagePainter
import com.bumptech.glide.Glide
import com.rb.anytextwiget.ActionData
import com.rb.anytextwiget.AppUtils
import com.rb.anytextwiget.ColorData
import com.rb.anytextwiget.GradientAdapter
import com.rb.anytextwiget.R
import com.rb.anytextwiget.TextGravityData
import com.rb.anytextwiget.TextGravitySelectionSheet
import com.rb.anytextwiget.WidgetData
import com.rb.anytextwiget.WidgetFontInfo
import com.rb.anytextwiget.ui.theme.violet
import com.rb.anytextwiget.ui.theme.violetDark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class CreationPage(
    var context: AppCompatActivity,
    var currentData: WidgetData,
    var imageLauncher: ActivityResultLauncher<Intent>
) {

    val fontUtils = FontUtils()

    val SHEET_FOR_TEXT = "textColour"
    val SHEET_FOR_BGR = "widgetBackground"
    val SHEET_FOR_SHADOW = "textShadow"
    val SHEET_FOR_OUTLINE = "widgetOutline"


    var textShadowEnabled = mutableStateOf(currentData.textShadowEnabled)

    var textShadowRadius = mutableIntStateOf(currentData.textShadowData!!.shadowRadius)

    var textHorzDir = mutableIntStateOf(currentData.textShadowData!!.horizontalDir)

    var textVerDir = mutableIntStateOf(currentData.textShadowData!!.verticalDir)

    var textPadding = mutableIntStateOf(currentData.textPadding)

    val backgroundTypes: MutableList<String> =
        arrayListOf(context.getString(R.string.color).replace("c", "C"), "Gradient", "Image")

    var gradientImage = mutableIntStateOf(R.drawable.no_corners_gradient_cyan_purple)

    var currentBgrTab = mutableIntStateOf(
        when (currentData.widgetBackGroundType) {
            "solid" -> 0
            "gradient" -> 1
            "image" -> 2
            else -> {
                0
            }
        }
    )

    var imagesList = mutableStateListOf<String>()

    val appUtils = AppUtils()

    val outlineEnabled = mutableStateOf(currentData.outlineEnabled)

    val outlineWidth = mutableIntStateOf(currentData.widgetOutlineWidth)


    lateinit var actionsSheet: ActionsSheet

    init {
        imagesList.addAll(currentData.widgetMultiImageList!!)

        CoroutineScope(Dispatchers.IO).launch {
            //Get the apps list
            actionsSheet = ActionsSheet(context, AppUtils.getInstalledApps(context))
        }
    }

    @SuppressLint("DiscouragedApi")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CreationPageUI() {
        val snackbarHostState = remember {
            SnackbarHostState()
        }


        val scrollBehaviour =
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())


        val scope = rememberCoroutineScope()

        var widgetText by remember {
            mutableStateOf(currentData.widgetText!!)
        }

        var widgetTextColour by remember {
            mutableStateOf(currentData.widgetTextColor!!.colorHexCode!!)
        }

        var widgetTextSize by remember {
            mutableFloatStateOf(currentData.widgetTextSize.toFloat())
        }

        var showColoursSheet by remember {
            mutableStateOf(false)
        }

        var showFontsSheet by remember {
            mutableStateOf(false)
        }

        var colourSheetFor by remember {
            mutableStateOf(SHEET_FOR_TEXT)
        }

        var showHorzGravitySheet by remember {
            mutableStateOf(false)
        }

        var showVertGravitySheet by remember {
            mutableStateOf(false)
        }

        var horzGravityIcon by remember {
            mutableIntStateOf(R.drawable.ic_round_vertical_align_bottom_50)
        }

        var horzGravityIconRotation by remember {
            mutableFloatStateOf(0f)

        }

        var vertGravityIcon by remember {
            mutableIntStateOf(R.drawable.ic_round_vertical_align_bottom_50)
        }

        var vertGravityIconRotation by remember {
            mutableFloatStateOf(0f)

        }

        when (currentData.widgetTextHorizontalGravity!!.gravityName) {
            "Start" -> {
                horzGravityIcon = R.drawable.ic_round_vertical_align_bottom_50
                horzGravityIconRotation = 90f
            }

            "Center" -> {
                horzGravityIcon = R.drawable.ic_round_vertical_align_center_50
                horzGravityIconRotation = 0f
            }

            "End" -> {
                horzGravityIcon = R.drawable.ic_round_vertical_align_bottom_50
                horzGravityIconRotation = -90f
            }
        }


        when (currentData.widgetTextVerticalGravity!!.gravityName) {
            "Top" -> {
                vertGravityIcon = R.drawable.ic_round_vertical_align_bottom_50
                vertGravityIconRotation = 180f
            }

            "Center" -> {
                vertGravityIcon = R.drawable.ic_round_vertical_align_center_50
                vertGravityIconRotation = 0f
            }

            "Bottom" -> {
                vertGravityIcon = R.drawable.ic_round_vertical_align_bottom_50
                vertGravityIconRotation = 0f
            }
        }


        //Get the gradient
        LaunchedEffect(Dispatchers.IO) {
            val sourceName = "no_corners_" + currentData.widgetBackgroundGradient!!.sourceName

            try {
                gradientImage.intValue =
                    context.resources.getIdentifier(sourceName, "drawable", "com.rb.anytextwiget")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        var font by remember {
            mutableStateOf(
                ResourcesCompat.getFont(
                    context,
                    R.font.open_sans_semibold
                )
            )
        }

        try {
            val id = context.resources.getIdentifier(
                currentData.widgetFontInfo!!.sourceName,
                "font",
                context.packageName
            )

            font = ResourcesCompat.getFont(context, id)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        var showGradientsSheet by remember {
            mutableStateOf(false)
        }

        var showPermissionDialog by remember {
            mutableStateOf(false)
        }

        var showActionsSheet by remember {
            mutableStateOf(false)
        }

        var simpleActionIcon by remember {
            mutableIntStateOf(
                when(currentData.widgetClickAction!!.actionName) {
                    AppUtils.ACTION_WIFI -> R.drawable.ic_round_wifi_50
                    AppUtils.ACTION_DONOTDISTURB -> R.drawable.ic_round_do_not_disturb_on_50
                    AppUtils.ACTION_FLASHLIGHT -> R.drawable.ic_round_flashlight_on_50
                    AppUtils.ACTION_BLUETOOTH -> R.drawable.ic_round_bluetooth_50
                    AppUtils.ACTION_NEXTIMAGE -> R.drawable.ic_round_skip_next_50
                    AppUtils.ACTION_OPEN_LINK -> R.drawable.rounded_open_in_browser_50dp
                    AppUtils.ACTION_NOTHING -> R.drawable.ic_baseline_do_disturb_alt_50
                    else -> {R.drawable.ic_round_touch_app}
                }
            )
        }


        Scaffold(topBar = {
            Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
                //Header.
                LargeTopAppBar(
                    title = {
                        Text(
                            text = "Create Widget",
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
                                contentDescription = "Close Creation"
                            )
                        }
                    },
                    scrollBehavior = scrollBehaviour,
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_round_check_24),
                                contentDescription = "Save Widget Button"
                            )
                        }
                    }
                )

                WidgetPreview(
                    clickEvent = {handleClick()},
                    longClickEvent = {})
            }
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

                //Text Input.
                Text(
                    text = "Text",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(
                        FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(10.dp)
                )
                TextField(value = widgetText,
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp),
                    onValueChange = { newText ->
                        widgetText = newText
                        currentData.widgetText = newText
                    })

                //Text Colour.
                Text(
                    text = "Text ${context.getString(R.string.color).replace("c", "C")}",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(
                        FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(10.dp, 30.dp, 10.dp, 10.dp)
                )
                Card(
                    elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    TextButton(modifier = Modifier
                        .fillMaxWidth(),
                        onClick = {
                            colourSheetFor = SHEET_FOR_TEXT
                            showColoursSheet = true
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_round_lens_24),
                            contentDescription = "Selected Text ${
                                context.getString(R.string.color).replace("c", "C")
                            }",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(10.dp),
                            colorFilter = ColorFilter.tint(
                                Color(
                                    android.graphics.Color.parseColor(
                                        currentData.widgetTextColor!!.colorHexCode
                                    )
                                )
                            )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(0.dp, 10.dp, 10.dp, 10.dp)
                        ) {
                            Text(
                                text = "Select text ${context.getString(R.string.color)}",
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = currentData.widgetTextColor!!.colorName!!,
                                fontSize = TextUnit(16f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                        }

                    }
                }


                //Text Size.
                Text(
                    text = "Text Size",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(
                        FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(10.dp, 30.dp, 10.dp, 10.dp)
                )
                Card(
                    elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    TextButton(modifier = Modifier
                        .fillMaxWidth(),
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_format_size_24),
                            contentDescription = "Text size icon",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(10.dp)
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(0.dp, 5.dp, 10.dp, 5.dp)
                        ) {
                            Text(
                                text = "Set the text size",
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )

                            Slider(
                                value = widgetTextSize,
                                valueRange = 0f..100f,
                                onValueChange = { changedFloat ->
                                    widgetTextSize = changedFloat
                                })
                        }

                    }
                }

                //Text Font.
                Text(
                    text = "Text Font",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(
                        FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(10.dp, 30.dp, 10.dp, 10.dp)
                )
                Card(
                    elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    TextButton(modifier = Modifier
                        .fillMaxWidth(),
                        onClick = { showFontsSheet = true }
                    ) {
                        Text(
                            text = "Aa",
                            fontSize = TextUnit(30f, TextUnitType.Sp),
                            fontFamily = FontFamily(typeface = font!!),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(10.dp)
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(0.dp, 10.dp, 10.dp, 10.dp)
                        ) {
                            Text(
                                text = "Select a font",
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = currentData.widgetFontInfo!!.fontName,
                                fontSize = TextUnit(16f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                        }
                    }
                }


                //Text Gravity.
                Text(
                    text = "Text Gravity",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(
                        FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(10.dp, 30.dp, 10.dp, 10.dp)
                )
                Card(
                    elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    TextButton(modifier = Modifier
                        .fillMaxWidth(),
                        onClick = {
                            showHorzGravitySheet = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = horzGravityIcon),
                            contentDescription = "Text horizontal gravity icon",
                            modifier = Modifier
                                .rotate(horzGravityIconRotation)
                                .align(Alignment.CenterVertically)
                                .padding(10.dp)
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(0.dp, 10.dp, 10.dp, 10.dp)
                        ) {
                            Text(
                                text = "Set horizontal gravity",
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = currentData.widgetTextHorizontalGravity!!.gravityName,
                                fontSize = TextUnit(16f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                        }

                    }

                    TextButton(modifier = Modifier
                        .fillMaxWidth(),
                        onClick = {
                            showVertGravitySheet = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = vertGravityIcon),
                            contentDescription = "Text vertical gravity icon",
                            modifier = Modifier
                                .rotate(vertGravityIconRotation)
                                .align(Alignment.CenterVertically)
                                .padding(10.dp)
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(0.dp, 10.dp, 10.dp, 10.dp)
                        ) {
                            Text(
                                text = "Set vertical gravity",
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = currentData.widgetTextVerticalGravity!!.gravityName,
                                fontSize = TextUnit(16f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                        }

                    }
                }


                //Text Shadow.
                Text(
                    text = "Text Shadow",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(
                        FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(10.dp, 30.dp, 10.dp, 10.dp)
                )
                Card(
                    elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                        if (textShadowEnabled.value) {
                            currentData.textShadowEnabled = false
                            textShadowEnabled.value = false
                        } else {
                            currentData.textShadowEnabled = true
                            textShadowEnabled.value = true
                        }
                    }) {
                        Text(
                            text = "Aa",
                            fontSize = TextUnit(30f, TextUnitType.Sp),
                            style = TextStyle(
                                shadow = Shadow(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    offset = Offset(
                                        1f,
                                        1f
                                    ),
                                    blurRadius = 20f
                                )
                            ),
                            fontFamily = fontUtils.openSans(
                                FontWeight.Bold
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(10.dp)
                        )

                        Text(
                            text = "Enable text shadow",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(FontWeight.Normal),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .weight(1f, fill = true)
                                .align(Alignment.CenterVertically)
                                .padding(0.dp, 10.dp, 10.dp, 10.dp),
                        )
                        Switch(checked = textShadowEnabled.value, modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp), onCheckedChange = { isChecked ->
                            textShadowEnabled.value = isChecked
                            currentData.textShadowEnabled = isChecked
                        })
                    }

                    AnimatedVisibility(
                        visible = textShadowEnabled.value,
                        enter = slideInVertically(),
                        exit = slideOutVertically()
                    ) {
                        Column {
                            TextButton(modifier = Modifier
                                .fillMaxWidth(),
                                onClick = {}
                            ) {
                                Text(
                                    text = textShadowRadius.intValue.toString(),
                                    fontSize = TextUnit(30f, TextUnitType.Sp),
                                    fontFamily = fontUtils.openSans(
                                        FontWeight.Bold
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(10.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(1f)
                                        .padding(0.dp, 10.dp, 10.dp, 10.dp)
                                ) {
                                    Text(
                                        text = "Set shadow radius",
                                        fontSize = TextUnit(18f, TextUnitType.Sp),
                                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                                        textAlign = TextAlign.Start,
                                    )

                                    Slider(
                                        value = textShadowRadius.intValue.toFloat(),
                                        valueRange = 0f..10f,
                                        onValueChange = {
                                            textShadowRadius.intValue = it.toInt()
                                            currentData.textShadowData!!.shadowRadius = it.toInt()
                                        })
                                }

                            }

                            TextButton(modifier = Modifier
                                .fillMaxWidth(),
                                onClick = {}
                            ) {
                                Text(
                                    text = textHorzDir.intValue.toString(),
                                    fontSize = TextUnit(30f, TextUnitType.Sp),
                                    fontFamily = fontUtils.openSans(
                                        FontWeight.Bold
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(10.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(1f)
                                        .padding(0.dp, 10.dp, 10.dp, 10.dp)
                                ) {
                                    Text(
                                        text = "Set horizontal direction",
                                        fontSize = TextUnit(18f, TextUnitType.Sp),
                                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                                        textAlign = TextAlign.Start,
                                    )
                                    Slider(
                                        value = textHorzDir.intValue.toFloat(),
                                        valueRange = -10f..10f,
                                        onValueChange = {
                                            textHorzDir.intValue = it.toInt()
                                            currentData.textShadowData!!.horizontalDir = it.toInt()
                                        })
                                }

                            }

                            TextButton(modifier = Modifier
                                .fillMaxWidth(),
                                onClick = {}
                            ) {
                                Text(
                                    text = textVerDir.intValue.toString(),
                                    fontSize = TextUnit(30f, TextUnitType.Sp),
                                    fontFamily = fontUtils.openSans(
                                        FontWeight.Bold
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(10.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(1f)
                                        .padding(0.dp, 10.dp, 10.dp, 10.dp)
                                ) {
                                    Text(
                                        text = "Set vertical direction",
                                        fontSize = TextUnit(18f, TextUnitType.Sp),
                                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                                        textAlign = TextAlign.Start,
                                    )
                                    Slider(
                                        value = textVerDir.intValue.toFloat(),
                                        valueRange = -10f..10f,
                                        onValueChange = {
                                            textVerDir.intValue = it.toInt()
                                            currentData.textShadowData!!.verticalDir = it.toInt()
                                        })
                                }
                            }

                            TextButton(modifier = Modifier
                                .fillMaxWidth(),
                                onClick = {
                                    colourSheetFor = SHEET_FOR_SHADOW
                                    showColoursSheet = true
                                }
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_round_lens_24),
                                    contentDescription = "Selected Text Shadow ${
                                        context.getString(R.string.color).replace("c", "C")
                                    }",
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(10.dp),
                                    colorFilter = ColorFilter.tint(
                                        Color(
                                            android.graphics.Color.parseColor(
                                                currentData.textShadowData!!.shadowColor!!.colorHexCode
                                            )
                                        )
                                    )
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(1f)
                                        .padding(0.dp, 10.dp, 10.dp, 10.dp)
                                ) {
                                    Text(
                                        text = "Select shadow ${context.getString(R.string.color)}",
                                        fontSize = TextUnit(18f, TextUnitType.Sp),
                                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        text = currentData.textShadowData!!.shadowColor!!.colorName!!,
                                        fontSize = TextUnit(16f, TextUnitType.Sp),
                                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                                        textAlign = TextAlign.Start,
                                    )
                                }

                            }
                        }
                    }
                }


                //Text Padding.
                /* Text(
                     text = "Text Padding",
                     fontSize = TextUnit(18f, TextUnitType.Sp),
                     fontFamily = fontUtils.openSans(
                         FontWeight.SemiBold
                     ),
                     modifier = Modifier.padding(10.dp, 30.dp, 10.dp, 10.dp)
                 )
                 Card(
                     elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                     modifier = Modifier
                         .padding(10.dp, 0.dp)
                         .fillMaxWidth(),
                     colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                 ) {
                     TextButton(modifier = Modifier
                         .fillMaxWidth(),
                         onClick = {}
                     ) {
                         Icon(
                             painter = painterResource(id = R.drawable.padding_black_50dp),
                             contentDescription = "Text padding icon",
                             modifier = Modifier
                                 .align(Alignment.CenterVertically)
                                 .padding(10.dp)
                         )
                         Column(
                             modifier = Modifier
                                 .align(Alignment.CenterVertically)
                                 .weight(1f)
                                 .padding(0.dp, 10.dp, 10.dp, 10.dp)
                         ) {
                             Text(
                                 text = "Set the text padding",
                                 fontSize = TextUnit(18f, TextUnitType.Sp),
                                 fontFamily = fontUtils.openSans(FontWeight.Normal),
                                 textAlign = TextAlign.Start,
                             )
                             *//*Slider(state = textSizeSliderState) {

                            }*//*
                        }

                    }
                }
*/

                //Widget Background.
                Text(
                    text = "Widget Background",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(
                        FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(10.dp, 30.dp, 10.dp, 10.dp)
                )


                Card(
                    elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    modifier = Modifier
                        .padding(10.dp, 5.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    PrimaryTabRow(
                        selectedTabIndex = currentBgrTab.intValue,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        divider = {},
                        modifier = Modifier
                            .padding(10.dp, 5.dp)
                    ) {
                        backgroundTypes.forEachIndexed { index, s ->
                            TextButton(onClick = { currentBgrTab.intValue = index }) {
                                Text(
                                    text = s, fontFamily = fontUtils.openSans(
                                        FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                }

                when (currentBgrTab.intValue) {
                    0 -> {
                        currentData.widgetBackGroundType = "solid"
                        Card(
                            elevation = CardDefaults.cardElevation(
                                0.dp,
                                0.dp,
                                0.dp,
                                0.dp,
                                0.dp,
                                0.dp
                            ),
                            modifier = Modifier
                                .padding(10.dp, 0.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            TextButton(modifier = Modifier
                                .fillMaxWidth(),
                                onClick = {
                                    colourSheetFor = SHEET_FOR_BGR
                                    showColoursSheet = true
                                }
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_round_lens_24),
                                    contentDescription = "Selected Background ${
                                        context.getString(R.string.color).replace("c", "C")
                                    }",
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(10.dp),
                                    colorFilter = ColorFilter.tint(
                                        color = Color(
                                            android.graphics.Color.parseColor(currentData.widgetBackgroundColor!!.colorHexCode)
                                        )
                                    )
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(1f)
                                        .padding(0.dp, 10.dp, 10.dp, 10.dp)
                                ) {
                                    Text(
                                        text = "Select background ${context.getString(R.string.color)}",
                                        fontSize = TextUnit(18f, TextUnitType.Sp),
                                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        text = currentData.widgetBackgroundColor!!.colorName!!,
                                        fontSize = TextUnit(16f, TextUnitType.Sp),
                                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                                        textAlign = TextAlign.Start,
                                    )
                                }

                            }
                        }
                    }

                    1 -> {
                        currentData.widgetBackGroundType = "gradient"

                        Card(
                            elevation = CardDefaults.cardElevation(
                                0.dp,
                                0.dp,
                                0.dp,
                                0.dp,
                                0.dp,
                                0.dp
                            ),
                            modifier = Modifier
                                .padding(10.dp, 0.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            TextButton(modifier = Modifier
                                .fillMaxWidth(),
                                onClick = { showGradientsSheet = true }
                            ) {

                                val gradientDrawable = ContextCompat.getDrawable(
                                    context,
                                    gradientImage.intValue
                                ) as GradientDrawable
                                val bitmap = Bitmap.createBitmap(
                                    50,
                                    50,
                                    Bitmap.Config.ARGB_8888
                                )
                                val canvas = Canvas(bitmap)
                                gradientDrawable.setBounds(0, 0, canvas.width, canvas.height)
                                gradientDrawable.draw(canvas)

                                AndroidView(factory = {
                                    ImageView(it).apply {
                                        layoutParams = FrameLayout.LayoutParams(
                                            AppUtils.dptopx(
                                                context,
                                                50
                                            ), AppUtils.dptopx(context, 50)
                                        )
                                        Glide.with(context).load(
                                            ContextCompat.getDrawable(
                                                context,
                                                gradientImage.intValue
                                            )
                                        ).circleCrop().into(this)
                                    }
                                }, update = {
                                    Glide.with(context).load(
                                        ContextCompat.getDrawable(
                                            context,
                                            gradientImage.intValue
                                        )
                                    ).circleCrop().into(it)
                                }, modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(10.dp)
                                )

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(1f)
                                        .padding(0.dp, 10.dp, 10.dp, 10.dp)
                                ) {
                                    Text(
                                        text = "Select background gradient",
                                        fontSize = TextUnit(18f, TextUnitType.Sp),
                                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        text = currentData.widgetBackgroundGradient!!.name,
                                        fontSize = TextUnit(16f, TextUnitType.Sp),
                                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                                        textAlign = TextAlign.Start,
                                    )
                                }

                            }
                        }
                    }

                    2 -> {
                        currentData.widgetBackGroundType = "image"

                        Column {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                WidgetImageFlipper()
                            }

                            if (imagesList.size < 5) {
                                Button(
                                    onClick = {
                                        if (AppUtils.hasStoragePermission(context)) {
                                            val intent = Intent(Intent.ACTION_PICK)
                                            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                                            imageLauncher.launch(intent)
                                        } else {
                                            showPermissionDialog = true
                                        }
                                    }, contentPadding = PaddingValues(15.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp, 15.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.image_24dp),
                                        contentDescription = "Add a new image button icon",
                                        modifier = Modifier.padding(10.dp, 0.dp)
                                    )

                                    Text(
                                        text = "Add a new image",
                                        fontFamily = fontUtils.openSans(FontWeight.SemiBold),
                                        fontSize = TextUnit(18f, TextUnitType.Sp),
                                    )
                                }
                            }
                        }
                    }
                }


                //Widget Outline.
                Text(
                    text = "Widget Outline",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(
                        FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(10.dp, 30.dp, 10.dp, 10.dp)
                )
                Card(
                    elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                        if (outlineEnabled.value) {
                            currentData.outlineEnabled = false
                            outlineEnabled.value = false
                        } else {
                            currentData.outlineEnabled = true
                            outlineEnabled.value = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_rect_50dp),
                            contentDescription = "Widget outline icon",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(10.dp)
                        )


                        Text(
                            text = "Enable widget outline",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(FontWeight.Normal),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .weight(1f, fill = true)
                                .align(Alignment.CenterVertically)
                                .padding(0.dp, 10.dp, 10.dp, 10.dp),
                        )
                        Switch(checked = outlineEnabled.value, modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp), onCheckedChange = { isChecked ->
                            currentData.outlineEnabled = isChecked
                            outlineEnabled.value = isChecked
                        })
                    }

                    AnimatedVisibility(
                        visible = outlineEnabled.value, enter = slideInVertically(),
                        exit = slideOutVertically()
                    ) {
                        Column {
                            TextButton(modifier = Modifier
                                .fillMaxWidth(),
                                onClick = {}
                            ) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        , contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.outline_rect_50dp),
                                        contentDescription = "Widget outline width icon",
                                        modifier = Modifier
                                            .padding(10.dp)
                                    )
                                    Text(
                                        text = outlineWidth.intValue.toString(),
                                        fontSize = TextUnit(28f, TextUnitType.Sp),
                                        fontFamily = fontUtils.openSans(FontWeight.Bold),
                                        textAlign = TextAlign.Center,
                                    )

                                }
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(1f)
                                        .padding(0.dp, 10.dp, 10.dp, 10.dp)
                                ) {
                                    Text(
                                        text = "Set the outline width",
                                        fontSize = TextUnit(18f, TextUnitType.Sp),
                                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                                        textAlign = TextAlign.Start,
                                    )
                                    Slider(
                                        value = outlineWidth.intValue.toFloat(),
                                        valueRange = 0f..10f,
                                        onValueChange = {
                                            currentData.widgetOutlineWidth = it.toInt()
                                            outlineWidth.intValue = it.toInt()
                                        })
                                }

                            }

                            TextButton(modifier = Modifier
                                .fillMaxWidth(),
                                onClick = {colourSheetFor = SHEET_FOR_OUTLINE
                                showColoursSheet = true}
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_round_lens_24),
                                    contentDescription = "Selected Outline ${
                                        context.getString(R.string.color).replace("c", "C")
                                    }",
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(10.dp),
                                    colorFilter = ColorFilter.tint(
                                        Color(
                                            android.graphics.Color.parseColor(
                                                currentData.widgetOutlineColor!!.colorHexCode!!
                                            )
                                        )
                                    )
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(1f)
                                        .padding(0.dp, 10.dp, 10.dp, 10.dp)
                                ) {
                                    Text(
                                        text = "Select Outline ${context.getString(R.string.color)}",
                                        fontSize = TextUnit(18f, TextUnitType.Sp),
                                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        text = currentData.widgetOutlineColor!!.colorName!!,
                                        fontSize = TextUnit(16f, TextUnitType.Sp),
                                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                                        textAlign = TextAlign.Start,
                                    )
                                }

                            }
                        }
                    }

                }


                //Widget Click.
                Text(
                    text = "Click Action",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(
                        FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(10.dp, 30.dp, 10.dp, 10.dp)
                )
                Card(
                    elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    TextButton(modifier = Modifier
                        .fillMaxWidth(),
                        onClick = {showActionsSheet = true}
                    ) {
                        Icon(
                            painter = painterResource(id = simpleActionIcon),
                            contentDescription = "Selected Click Action",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(10.dp)
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(0.dp, 10.dp, 10.dp, 10.dp)
                        ) {
                            Text(
                                text = "Select a click action",
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = currentData.widgetClickAction!!.actionName,
                                fontSize = TextUnit(16f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                        }

                    }
                }


                //Others.
                Text(
                    text = "Others",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(
                        FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(10.dp, 30.dp, 10.dp, 10.dp)
                )
                Card(
                    elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    TextButton(modifier = Modifier.fillMaxWidth(), onClick = {

                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_rounded_corner_24),
                            contentDescription = "Widget rounded corners icon",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(10.dp)
                        )

                        Text(
                            text = "Enable widget rounded corners",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(FontWeight.Normal),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .weight(1f, fill = true)
                                .align(Alignment.CenterVertically)
                                .padding(0.dp, 10.dp, 10.dp, 10.dp),
                        )
                        Switch(checked = true, modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp), onCheckedChange = { isChecked ->
                        })
                    }
                }
            }
        }


        //Data Updates...
        if (widgetTextSize >= 0f) {
            currentData.widgetTextSize = widgetTextSize.toInt()
        }


        //Sheets.
        if (showColoursSheet) {
            ColoursSheet(context).ColoursSheetUI(colourSelectedEvent = { colourData: ColorData ->
                when (colourSheetFor) {
                    SHEET_FOR_TEXT -> currentData.widgetTextColor = colourData
                    SHEET_FOR_SHADOW -> currentData.textShadowData!!.shadowColor = colourData
                    SHEET_FOR_BGR -> currentData.widgetBackgroundColor = colourData
                    SHEET_FOR_OUTLINE -> currentData.widgetOutlineColor = colourData
                }
                showColoursSheet = false
            }) {
                showColoursSheet = false
            }
        }

        if (showFontsSheet) {
            FontsSheet(
                currentFontData = currentData.widgetFontInfo!!,
                fontSelectedEvent = { fontInfo: WidgetFontInfo ->
                    currentData.widgetFontInfo = fontInfo
                    try {
                        val id = context.resources.getIdentifier(
                            currentData.widgetFontInfo!!.sourceName,
                            "font",
                            context.packageName
                        )

                        font = ResourcesCompat.getFont(context, id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    showFontsSheet = false
                },
                onDismiss = {
                    showFontsSheet = false
                })
        }

        if (showHorzGravitySheet) {
            GravitySheet(
                gravitiesType = HORIZONTAL,
                gravitiesList = loadGravities(HORIZONTAL),
                currentGravity = currentData.widgetTextHorizontalGravity!!,
                gravitySelectedEvent = {
                    currentData.widgetTextHorizontalGravity = it
                    showHorzGravitySheet = false
                }
            ) {
                showHorzGravitySheet = false
            }
        }

        if (showVertGravitySheet) {
            GravitySheet(
                gravitiesType = VERTICAL,
                gravitiesList = loadGravities(VERTICAL),
                currentGravity = currentData.widgetTextVerticalGravity!!,
                gravitySelectedEvent = {
                    currentData.widgetTextVerticalGravity = it
                    showVertGravitySheet = false
                }
            ) {
                showVertGravitySheet = false
            }
        }

        if (showGradientsSheet) {
            GradientsSheet(
                currentGradient = currentData.widgetBackgroundGradient!!,
                gradientSelectedEvent = {
                    currentData.widgetBackgroundGradient = it

                    CoroutineScope(Dispatchers.IO).launch {
                        val sourceName =
                            "no_corners_" + currentData.widgetBackgroundGradient!!.sourceName

                        try {
                            gradientImage.intValue =
                                context.resources.getIdentifier(
                                    sourceName,
                                    "drawable",
                                    "com.rb.anytextwiget"
                                )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    showGradientsSheet = false
                }) {
                showGradientsSheet = false
            }
        }

        if (showActionsSheet) {
           actionsSheet.ActionsSheetUI(
                imagesList = imagesList,
                currentActionData = currentData.widgetClickAction!!,
                actionSelectedEvent = {
                    currentData.widgetClickAction = it
                    showActionsSheet = false
                }
            ) {
                showActionsSheet = false
            }
        }


        //Dialogs.
        if (showPermissionDialog) {
            appUtils.BuildAlertDialog(
                title = "Permission required!",
                description = "Storage permission is required for loading images from your device",
                confirmEvent = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(
                            context, arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.ACCESS_MEDIA_LOCATION
                            ), 103
                        )
                    } else {
                        ActivityCompat.requestPermissions(
                            context, arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ), 103
                        )
                    }

                    showPermissionDialog = false
                },
                dismissEvent = { showPermissionDialog = false }) {

            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun WidgetPreview(
        clickEvent: () -> Unit,
        longClickEvent: () -> Unit
    ) {
        //Widget colour.
        var widgetColour = violet
        try {
            widgetColour =
                Color(android.graphics.Color.parseColor(currentData.widgetBackgroundColor!!.colorHexCode))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Widget Corners.
        val widgetCorners = if (currentData.widgetRoundCorners) {
            Dp(30f)
        } else {
            Dp(0f)
        }

        //Widget outline width
        val widgetOutlineWidth = if (outlineEnabled.value) {
            outlineWidth.intValue.dp
        } else {
            0.dp
        }

        //Widget outline colour.
        var widgetOutlineColour = violetDark
        try {
            widgetOutlineColour =
                Color(android.graphics.Color.parseColor(currentData.widgetOutlineColor!!.colorHexCode))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Widget text colour
        var widgetTextColour = Color.White
        try {
            widgetTextColour =
                Color(android.graphics.Color.parseColor(currentData.widgetTextColor!!.colorHexCode))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Widget text font family.
        var font = ResourcesCompat.getFont(
            context,
            R.font.open_sans_semibold
        )

        try {
            val info = currentData.widgetFontInfo!!
            font = if (info.sourceName != "NA") {
                ResourcesCompat.getFont(
                    context,
                    context.resources.getIdentifier(info.sourceName, "font", context.packageName)
                )!!
            } else {
                val fontText: String = if (info.fontStyle == "normal") {
                    info.fontName.lowercase()
                        .replace(" ", "_", true)
                } else {
                    info.fontName.lowercase()
                        .replace(" ", "_", true) + "_" + info.fontStyle.lowercase()
                }
                ResourcesCompat.getFont(
                    context,
                    context.resources.getIdentifier(fontText, "font", context.packageName)
                )!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Widget text horizontal gravity.
        val horizontalGravity = when (currentData.widgetTextHorizontalGravity!!.gravityValue) {
            Gravity.CENTER_HORIZONTAL -> Alignment.CenterHorizontally
            Gravity.START -> Alignment.Start
            Gravity.END -> Alignment.End
            else -> {
                Alignment.CenterHorizontally
            }
        }

        //Widget text vertical gravity.
        val verticalGravity = when (currentData.widgetTextVerticalGravity!!.gravityValue) {
            Gravity.CENTER_VERTICAL -> Alignment.CenterVertically
            Gravity.TOP -> Alignment.Top
            Gravity.BOTTOM -> Alignment.Bottom
            else -> {
                Alignment.CenterVertically
            }
        }

        //Widget text shadow radius.
        val widgetTextShadowRadius = if (textShadowEnabled.value) {
            textShadowRadius.intValue
        } else {
            0
        }

        //Widget text shadow directions.
        val widgetTextHorzDir = if (textShadowEnabled.value) {
            textHorzDir.intValue
        } else {
            0
        }
        val widgetTextVerDir = if (textShadowEnabled.value) {
            textVerDir.intValue
        } else {
            0
        }


        //Widget text shadow  colour.
        var widgetTextShadowColour = Color.Black
        try {
            widgetTextShadowColour =
                Color(android.graphics.Color.parseColor(currentData.textShadowData!!.shadowColor!!.colorHexCode))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val scope = rememberCoroutineScope()

        val density = LocalDensity.current.density

        val anchoredDraggableState = remember {
            AnchoredDraggableState(
                initialValue = DragAnchors.START,
                positionalThreshold = { totalDistance: Float -> totalDistance * 0.5f },
                velocityThreshold = { with(density) { 100.dp.value } }, animationSpec = tween()
            ).apply {
                updateAnchors(
                    DraggableAnchors {
                        DragAnchors.START at 0f
                        DragAnchors.END at 500f
                    },
                )

            }
        }

        //Widget Card and sorter UI.
        Row {
            //Widget Card.
            Surface(
                color = widgetColour,
                shape = RoundedCornerShape(widgetCorners),
                border = BorderStroke(widgetOutlineWidth, widgetOutlineColour),
                modifier = Modifier
                    .weight(1f, true)
                    .height(Dp(200f))
                    .padding(Dp(10f))
                    .offset {
                        IntOffset(
                            x = 0, y = anchoredDraggableState
                                .requireOffset()
                                .roundToInt()
                        )
                    }
                    .combinedClickable(
                        onLongClick = longClickEvent,
                        onClick = clickEvent,
                        onClickLabel = "Opens Editor",
                        onDoubleClick = null,
                        onLongClickLabel = "Opens option sheet"
                    ),
            ) {
                //Widget background content.
                when (currentBgrTab.intValue) {
                    2 -> {
                        //Widget Image flipper.
                        WidgetImageFlipper()
                    }

                    1 -> {
                        if (currentData.widgetBackgroundGradient != null) {
                            //Gradient Image.
                            val sourceName =
                                "no_corners_" + currentData.widgetBackgroundGradient!!.sourceName
                            val gradient =
                                context.resources.getIdentifier(
                                    sourceName,
                                    "drawable",
                                    context.packageName
                                )
                            val bitmap = BitmapFactory.decodeResource(context.resources, gradient)
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Widget gradient background",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            AndroidView(factory = {
                                ImageView(it).apply {
                                    setImageResource(gradient)
                                }
                            }, update = {
                                it.setImageResource(gradient)
                            }, modifier = Modifier.fillMaxSize())
                        }

                    }
                }

                //Widget text.
                Text(
                    text = currentData.widgetText!!,
                    color = widgetTextColour,
                    fontSize = TextUnit(currentData.widgetTextSize.toFloat(), TextUnitType.Sp),
                    fontFamily = FontFamily(typeface = font!!),
                    style = TextStyle(
                        shadow = Shadow(
                            color = widgetTextShadowColour,
                            offset = Offset(
                                widgetTextHorzDir.toFloat(),
                                widgetTextVerDir.toFloat()
                            ),
                            blurRadius = widgetTextShadowRadius.toFloat()
                        )
                    ),
                    modifier = Modifier
                        .wrapContentHeight(verticalGravity)
                        .wrapContentWidth(horizontalGravity)
                )
            }

        }


    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun WidgetImageFlipper() {
        val pagerState = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f
        ) {
            imagesList.size
        }

        val scope = rememberCoroutineScope()
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true,
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (!pagerState.isScrollInProgress) {
                        scope.launch {
                            if (pagerState.currentPage == pagerState.pageCount - 1) {
                                pagerState.animateScrollToPage(0)
                            } else {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                }

        ) {
            var bitmap: Bitmap? = null
            try {
                bitmap =
                    AppUtils.getBitmapWithContentPath(context = context, path = imagesList[it], 2)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Widget images",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        LaunchedEffect(key1 = Unit, block = {
            repeat(times = Int.MAX_VALUE, action = {
                delay(5000)
                val toScrollPage = if (pagerState.canScrollForward) {
                    pagerState.currentPage + 1
                } else {
                    0
                }
                pagerState.animateScrollToPage(toScrollPage)
            })
        })
    }


    fun loadGravities(type: String): MutableList<TextGravityData> {
        val dataList = ArrayList<TextGravityData>()
        if (type == TextGravitySelectionSheet.VERTICAL) {
            dataList.add(TextGravityData("Top", Gravity.TOP))
            dataList.add(TextGravityData("Center", Gravity.CENTER_VERTICAL))
            dataList.add(TextGravityData("Bottom", Gravity.BOTTOM))
        } else {
            dataList.add(TextGravityData("Start", Gravity.START))
            dataList.add(TextGravityData("Center", Gravity.CENTER_HORIZONTAL))
            dataList.add(TextGravityData("End", Gravity.END))
        }

        return dataList
    }

    fun handleClick() {
        if (currentData.widgetClickAction!!.actionType == AppUtils.ACTIONS_SIMPLE) {
            when (currentData.widgetClickAction!!.actionName) {
                AppUtils.ACTION_WIFI -> {
                    toggleWifi()
                }

                AppUtils.ACTION_BLUETOOTH -> {
                    toggleBlueTooth()
                }

                AppUtils.ACTION_DONOTDISTURB -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        toggleDnd()
                    }
                }

                AppUtils.ACTION_FLASHLIGHT -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        toggleFlashlight()
                    }
                }

                AppUtils.ACTION_NEXTIMAGE -> {
                    toggleNextImage()
                }

                AppUtils.ACTION_OPEN_LINK -> {
                    openLink()
                }

            }
        } else {
            if (currentData.widgetClickAction!!.appPackageName == "com.rb.anytextwiget") {
                Toast.makeText(context, "Opens this app", Toast.LENGTH_SHORT).show()
            } else {
                openApp()
            }
        }
    }

    fun toggleWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            panelIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(panelIntent)
        } else {
            val wifiManager = context.applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
            if (wifiManager.isWifiEnabled) {
                wifiManager.setWifiEnabled(false)
            } else {
                wifiManager.setWifiEnabled(true)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun toggleBlueTooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled) {
                bluetoothAdapter.disable()
            } else {
                bluetoothAdapter.enable()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun toggleDnd() {
        val notificationManager =
            context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

        // Check if the notification policy access has been granted for the app.
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            askDndAccess()
        } else {
            if (notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_NONE) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
            } else {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun toggleFlashlight() {
        val cameraManager = context.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager

        cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {

            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                cameraManager.unregisterTorchCallback(this)
                if (!enabled) {
                    cameraManager.setTorchMode(cameraManager.cameraIdList[0], true)
                } else {
                    cameraManager.setTorchMode(cameraManager.cameraIdList[0], false)
                }

                super.onTorchModeChanged(cameraId, enabled)
            }
        }, Handler())
    }

    fun toggleNextImage() {


    }

    fun openLink() {
        if (currentData.widgetClickAction!!.actionExtra.trim().isNotEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(currentData.widgetClickAction!!.actionExtra)
                context.startActivity(intent)
            } catch (e: java.lang.Exception) {
                Toast.makeText(
                    context,
                    "Unable to open this link. Please enter a proper one",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }

        }

    }

    fun openApp() {
        val intent =
            context.packageManager.getLaunchIntentForPackage(currentData.widgetClickAction!!.appPackageName)
        if (intent != null) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                "${currentData.widgetClickAction!!.actionName} is not available for click",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun askDndAccess() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Permission required...")
        builder.setMessage("App required Do Not Disturb access in order for this toggle action to work")
        builder.setPositiveButton("Give", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                context.startActivity(intent)
            }

        })
        builder.show()
    }
}