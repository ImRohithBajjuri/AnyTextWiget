package com.rb.anytextwiget.jetpackUI

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BitmapCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rb.anytextwiget.ActionData
import com.rb.anytextwiget.AddWidgetDialog
import com.rb.anytextwiget.AppUtils
import com.rb.anytextwiget.AppWidgetData
import com.rb.anytextwiget.ColorData
import com.rb.anytextwiget.CreateWidgetActivity
import com.rb.anytextwiget.MainActivity
import com.rb.anytextwiget.R
import com.rb.anytextwiget.SettingsActivity
import com.rb.anytextwiget.TextShadowData
import com.rb.anytextwiget.WidgetData
import com.rb.anytextwiget.ui.theme.violet
import com.rb.anytextwiget.ui.theme.violetDark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jcodec.containers.mp4.BoxUtil
import org.jcodec.scale.BitmapUtil
import org.w3c.dom.Text
import java.io.IOException
import java.io.ObjectInputStream
import java.io.StreamCorruptedException
import java.util.UUID
import kotlin.coroutines.coroutineContext
import kotlin.math.roundToInt

class MainPage(var context: MainActivity, private var widgetsList: MutableList<WidgetData>) {

    private var fontUtils = FontUtils()

    lateinit var showImportSheet: MutableState<Boolean>

    private var createWidgetLauncher: ActivityResultLauncher<Intent> =
        (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }
    private var settingsLauncher =
        (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }

    var importLauncher =
        (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val uri = it.data!!.data!!
                val contentFileName = AppUtils.getContentFileName(context, uri)
                if (contentFileName.endsWith(".atw", false)) {

                    loadWidgetFromFile(uri)
                } else {
                    Toast.makeText(context, "Please select a '.atw' file", Toast.LENGTH_LONG).show()
                }
            }
        }

    private var createEditorLauncher =
        (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        }

    private val appUtils = AppUtils()

    lateinit var onLongClickWidgetData: WidgetData

    lateinit var importSheetWidgetData: AppWidgetData

    lateinit var showSortUI: MutableState<Boolean>

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainUI() {
        val scrollBehaviour =
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

        val lazyListState = rememberLazyListState()

        var showOptionsMenu by remember {
            mutableStateOf(false)
        }

        val uiWidgetsList = remember {
            mutableStateListOf<WidgetData>()
        }
        uiWidgetsList.addAll(widgetsList)

        var showPermissionDialog by remember {
            mutableStateOf(false)
        }

        val snackbarHostState = remember {
            SnackbarHostState()
        }

        var showHelpOptions by remember {
            mutableStateOf(false)
        }

        val scope = rememberCoroutineScope()

        showImportSheet = remember {
            mutableStateOf(false)
        }

        showSortUI = remember {
            mutableStateOf(false)
        }


        //Parent layout.
        Scaffold(
            topBar = {
                //Header.
                LargeTopAppBar(
                    title = {
                        Text(
                            text = "Your Widgets",
                            fontFamily = fontUtils.openSans(FontWeight.Black),
                            fontSize = TextUnit(28f, TextUnitType.Sp),
                            textAlign = TextAlign.Center
                        )
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background
                    ),
                    scrollBehavior = scrollBehaviour,
                    actions = {
                        Box(
                            modifier = Modifier.align(Alignment.CenterVertically),
                        ) {
                            //Options Button.
                            FilledIconButton(
                                onClick = {
                                    showOptionsMenu = true
                                },
                                shape = CircleShape,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.more_horiz_24dp),
                                    contentDescription = "More options button"
                                )
                            }


                            //Options Menu.
                            DropdownMenu(expanded = showOptionsMenu, onDismissRequest = {
                                showOptionsMenu = false
                            }) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Import Widget", fontFamily = fontUtils.openSans(
                                                FontWeight.SemiBold
                                            )
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.import_widget_menu_24dp),
                                            contentDescription = "Import widget icon"
                                        )
                                    },
                                    onClick = {
                                        showOptionsMenu = false
                                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                                        intent.setDataAndType(
                                            MediaStore.Files.getContentUri("external"),
                                            "application/*"
                                        )
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        intent.flags =
                                            Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                                        if (AppUtils.hasStoragePermission(context)) {
                                            importLauncher.launch(intent)
                                        } else {
                                            showPermissionDialog = true
                                        }
                                    })
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Export All Widgets",
                                            fontFamily = fontUtils.openSans(
                                                FontWeight.SemiBold
                                            )
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.export_widget_menu_24dp),
                                            contentDescription = "Export widget icon"
                                        )
                                    },
                                    onClick = {
                                        showOptionsMenu = false

                                        if (AppUtils.hasStoragePermission(context)) {
                                            if (widgetsList.isNotEmpty()) {
                                                /*CoroutineScope(Dispatchers.IO).launch {
                                                    context.saveAllWidgets()
                                                }*/
                                                exportAllWidgets(
                                                    snackbarHost = snackbarHostState,
                                                    scope = scope
                                                )

                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "No widgets to save",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            showPermissionDialog = true
                                        }
                                    })

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Sort", fontFamily = fontUtils.openSans(
                                                FontWeight.SemiBold
                                            )
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.sort_widgets_menu_24dp),
                                            contentDescription = "Sort widgets icon"
                                        )
                                    },
                                    onClick = {
                                        showOptionsMenu = false
                                        showSortUI.value = true
                                    })

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Refresh", fontFamily = fontUtils.openSans(
                                                FontWeight.SemiBold
                                            )
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.refresh_widgets_menu_24dp),
                                            contentDescription = "Refresh widgets icon"
                                        )
                                    },
                                    onClick = {
                                        showOptionsMenu = false
                                        if (uiWidgetsList.isNotEmpty()) {
                                            uiWidgetsList.clear()
                                            uiWidgetsList.addAll(widgetsList)
                                        }

                                        AppUtils.updateUIWidgets(context)
                                    })

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Settings", fontFamily = fontUtils.openSans(
                                                FontWeight.SemiBold
                                            )
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.settings_menu_24dp),
                                            contentDescription = "Settings icon"
                                        )
                                    },
                                    onClick = {
                                        showOptionsMenu = false
                                        val intent = Intent(context, SettingsActivity::class.java)
                                        settingsLauncher.launch(intent)
                                        if (Build.VERSION.SDK_INT >= 34) {
                                            (context as AppCompatActivity).overrideActivityTransition(
                                                Activity.OVERRIDE_TRANSITION_OPEN,
                                                R.anim.activity_open,
                                                R.anim.activity_pusher
                                            )
                                        } else {
                                            (context as AppCompatActivity).overridePendingTransition(
                                                R.anim.activity_open,
                                                R.anim.activity_pusher
                                            )
                                        }
                                    })

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Help", fontFamily = fontUtils.openSans(
                                                FontWeight.SemiBold
                                            )
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.help_menu_24dp),
                                            contentDescription = "Help icon"
                                        )
                                    },
                                    onClick = {
                                        showOptionsMenu = false
                                        showHelpOptions = true
                                    })

                            }
                        }
                    },
                )
            },
            floatingActionButton = {
                CreateWidgetFAB(toShow = lazyListState.isScrollingUp())
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            },
            modifier = Modifier
                .nestedScroll(scrollBehaviour.nestedScrollConnection),
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                if (uiWidgetsList.isEmpty()) {
                    TextButton(onClick = {
                        val intent = Intent(context, CreateWidgetActivity::class.java)
                        intent.putExtra("type", "create")
                        createWidgetLauncher.launch(intent)
                        if (Build.VERSION.SDK_INT >= 34) {
                            (context as AppCompatActivity).overrideActivityTransition(
                                Activity.OVERRIDE_TRANSITION_OPEN,
                                R.anim.activity_open,
                                R.anim.activity_pusher
                            )
                        } else {
                            (context as AppCompatActivity).overridePendingTransition(
                                R.anim.activity_open,
                                R.anim.activity_pusher
                            )
                        }
                    }, modifier = Modifier.align(Alignment.Center)) {
                        Icon(
                            imageVector = Icons.Rounded.AddCircle,
                            contentDescription = "Create a new widget",
                            modifier = Modifier.size(30.dp)
                        )

                        Text(
                            text = "Create a new widget", fontFamily = fontUtils.openSans(
                                FontWeight.SemiBold
                            ), fontSize = TextUnit(21f, TextUnitType.Sp)
                        )
                    }

                    //Show ads.
                    if (!disableAds.value) {
                        AndroidView(factory = { context ->
                            AdView(context).apply {
                                setAdSize(AdSize.BANNER)
                                adUnitId = context.getString(R.string.bannerAd1UnitID)
                                val adRequest = AdRequest.Builder().build()
                                loadAd(adRequest)
                            }
                        }, modifier = Modifier.align(Alignment.BottomCenter))
                    }
                    return@Scaffold
                } else {

                    //Widgets list.
                    WidgetsList(
                        scaffoldPadding = it.calculateTopPadding(),
                        lazyListState = lazyListState,
                        uiWidgetsList = uiWidgetsList,
                        snackbarHost = snackbarHostState
                    )

                    //Show ads.
                    if (disableAds.value) {
                        AndroidView(factory = { context ->
                            AdView(context).apply {
                                setAdSize(AdSize.BANNER)
                                adUnitId = context.getString(R.string.bannerAd1UnitID)
                                val adRequest = AdRequest.Builder().build()
                                loadAd(adRequest)
                            }
                        }, modifier = Modifier.align(Alignment.BottomCenter))
                    }
                }
            }

        }

        //Permission Dialog.
        if (showPermissionDialog) {
            appUtils.BuildAlertDialog(
                title = "Permission required!",
                description = "Storage permission is required for loading widgets from your device",
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


        //Help Options Sheet.
        if (showHelpOptions) {
            HelpOptionsSheet(context) {
                showHelpOptions = false
            }
        }


        //Import Sheet.
        if (showImportSheet.value) {
            ImportWidgetSheet(
                context = context,
                widgetData = importSheetWidgetData.widgetData!!,
                widgetsList = widgetsList,
                onAddClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Saving the widget, please wait...",
                            duration = SnackbarDuration.Indefinite
                        )

                        addWidgetToSavedWidgets(
                            appWidgetData = importSheetWidgetData,
                            scope,
                            snackbarHostState
                        )

                    }
                    showImportSheet.value = false
                },
                onDismiss = {
                    showImportSheet.value = false
                })

        }

    }

    @Composable
    fun WidgetsList(
        scaffoldPadding: Dp,
        lazyListState: LazyListState,
        uiWidgetsList: SnapshotStateList<WidgetData>,
        snackbarHost: SnackbarHostState
    ) {
        var showWidgetOptions by remember {
            mutableStateOf(false)
        }

        val scope = rememberCoroutineScope()


        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .padding(0.dp, scaffoldPadding, 0.dp, 0.dp)
                .fillMaxWidth(),
        ) {
            items(uiWidgetsList) {
                WidgetItem(widgetData = it, clickEvent = {
                    val intent = Intent(context, CreateWidgetActivity::class.java)
                    intent.putExtra("type", "edit")
                    intent.putExtra("currentdata", it)
                    createEditorLauncher.launch(intent)
                    if (Build.VERSION.SDK_INT >= 34) {
                        (context as AppCompatActivity).overrideActivityTransition(
                            Activity.OVERRIDE_TRANSITION_OPEN,
                            R.anim.activity_open,
                            R.anim.activity_pusher
                        )
                    } else {
                        (context as AppCompatActivity).overridePendingTransition(
                            R.anim.activity_open,
                            R.anim.activity_pusher
                        )
                    }
                }, longClickEvent = {
                    onLongClickWidgetData = it
                    showWidgetOptions = true
                })
            }
        }

        //Widget Options Sheet.
        if (showWidgetOptions) {
            WidgetOptionsSheet(
                context = context,
                widgetData = onLongClickWidgetData,
                optionSheetClickEvent = { event ->
                    //Dismiss the sheet.
                    showWidgetOptions = false
                    optionsSheetClickEvent(
                        event,
                        scope = scope,
                        snackbarHostState = snackbarHost,
                        widgetData = onLongClickWidgetData,
                        uiList = uiWidgetsList
                    )
                },
                onDismiss = { showWidgetOptions = false }
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun WidgetItem(widgetData: WidgetData, clickEvent: () -> Unit, longClickEvent: () -> Unit) {
        //Widget colour.
        var widgetColour = violet
        try {
            widgetColour =
                Color(android.graphics.Color.parseColor(widgetData.widgetBackgroundColor!!.colorHexCode))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Widget Corners.
        val widgetCorners = if (widgetData.widgetRoundCorners) {
            Dp(30f)
        } else {
            Dp(0f)
        }

        //Widget outline width
        val widgetOutlineWidth = if (widgetData.outlineEnabled) {
            Dp(widgetData.widgetOutlineWidth.toFloat())
        } else {
            Dp(0f)
        }

        //Widget outline colour.
        var widgetOutlineColour = violetDark
        try {
            widgetOutlineColour =
                Color(android.graphics.Color.parseColor(widgetData.widgetOutlineColor!!.colorHexCode))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Widget text colour
        var widgetTextColour = Color.White
        try {
            widgetTextColour =
                Color(android.graphics.Color.parseColor(widgetData.widgetTextColor!!.colorHexCode))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Widget text font family.
        var font = ResourcesCompat.getFont(
            context,
            R.font.open_sans_semibold
        )

        try {
            val info = widgetData.widgetFontInfo!!
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
        val horizontalGravity = when (widgetData.widgetTextHorizontalGravity!!.gravityValue) {
            Gravity.CENTER_HORIZONTAL -> Alignment.CenterHorizontally
            Gravity.START -> Alignment.Start
            Gravity.END -> Alignment.End
            else -> {
                Alignment.CenterHorizontally
            }
        }

        //Widget text vertical gravity.
        val verticalGravity = when (widgetData.widgetTextVerticalGravity!!.gravityValue) {
            Gravity.CENTER_VERTICAL -> Alignment.CenterVertically
            Gravity.TOP -> Alignment.Top
            Gravity.BOTTOM -> Alignment.Bottom
            else -> {
                Alignment.CenterVertically
            }
        }

        //Widget text shadow radius.
        val widgetTextShadowRadius = if (widgetData.textShadowEnabled) {
            widgetData.textShadowData!!.shadowRadius
        } else {
            0
        }

        //Widget text shadow  colour.
        var widgetTextShadowColour = Color.Black
        try {
            widgetTextShadowColour =
                Color(android.graphics.Color.parseColor(widgetData.textShadowData!!.shadowColor!!.colorHexCode))
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
                when (widgetData.widgetBackGroundType) {
                    "image" -> {
                        //Widget Image flipper.
                        WidgetImageFlipper(imagesList = widgetData.widgetMultiImageList!!)
                    }

                    "gradient" -> {
                        if (widgetData.widgetBackgroundGradient != null) {
                            //Gradient Image.
                            val sourceName =
                                "no_corners_" + widgetData.widgetBackgroundGradient!!.sourceName
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
                        }

                    }
                }

                //Widget text.
                Text(
                    text = widgetData.widgetText!!,
                    color = widgetTextColour,
                    fontSize = TextUnit(widgetData.widgetTextSize.toFloat(), TextUnitType.Sp),
                    fontFamily = FontFamily(typeface = font!!),
                    style = TextStyle(
                        shadow = Shadow(
                            color = widgetTextShadowColour,
                            offset = Offset(
                                widgetData.textShadowData!!.horizontalDir.toFloat(),
                                widgetData.textShadowData!!.verticalDir.toFloat()
                            ),
                            blurRadius = widgetTextShadowRadius.toFloat()
                        )
                    ),
                    modifier = Modifier
                        .wrapContentHeight(verticalGravity)
                        .wrapContentWidth(horizontalGravity)
                )
            }

            //Sorting UI.
            AnimatedVisibility(
                visible = showSortUI.value,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Sorting button",
                    modifier = Modifier
                        .size(30.dp)
                        .anchoredDraggable(
                            state = anchoredDraggableState,
                            orientation = Orientation.Vertical
                        )
                )
            }

        }


    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun WidgetImageFlipper(imagesList: MutableList<String>) {
        val pagerState = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f
        ) {
            imagesList.size
        }
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
        ) {
            var bitmap: Bitmap? = null
            try {
                bitmap =
                    AppUtils.getBitmapWithContentPath(context = context, path = imagesList[it], 2)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (bitmap != null) {
                Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Widget images")
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

    @Composable
    fun CreateWidgetFAB(toShow: Boolean) {
        AnimatedVisibility(
            visible = toShow,
            enter = slideInVertically { it * 2 },
            exit = slideOutVertically { it * 2 }) {

            ExtendedFloatingActionButton(
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                text = {
                    Text(
                        text = "CREATE",
                        fontFamily = fontUtils.openSans(FontWeight.Bold),
                        fontSize = TextUnit(18f, TextUnitType.Sp)
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_add_circle_24),
                        contentDescription = "Create widget button"
                    )
                },
                onClick = {
                    val intent = Intent(context, CreateWidgetActivity::class.java)
                    intent.putExtra("type", "create")
                    createWidgetLauncher.launch(intent)
                    if (Build.VERSION.SDK_INT >= 34) {
                        (context as AppCompatActivity).overrideActivityTransition(
                            Activity.OVERRIDE_TRANSITION_OPEN,
                            R.anim.activity_open,
                            R.anim.activity_pusher
                        )
                    } else {
                        (context as AppCompatActivity).overridePendingTransition(
                            R.anim.activity_open,
                            R.anim.activity_pusher
                        )
                    }
                },
            )
        }
    }

    @Composable
    private fun LazyListState.isScrollingUp(): Boolean {
        var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
        var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
        return remember(this) {
            derivedStateOf {
                if (previousIndex != firstVisibleItemIndex) {
                    previousIndex > firstVisibleItemIndex
                } else {
                    previousScrollOffset >= firstVisibleItemScrollOffset
                }.also {
                    previousIndex = firstVisibleItemIndex
                    previousScrollOffset = firstVisibleItemScrollOffset
                }
            }
        }.value
    }

    private fun exportAllWidgets(snackbarHost: SnackbarHostState, scope: CoroutineScope) {
        var count = 0
        var saveInterface: AppUtils.WidgetSaveInterface? = null

        scope.launch {
            //Dismiss current snackbar to show new.
            snackbarHost.currentSnackbarData?.dismiss()
            snackbarHost.showSnackbar(
                message = "Saving your widgets, please wait...",
                duration = SnackbarDuration.Indefinite
            )
        }



        saveInterface = object : AppUtils.WidgetSaveInterface {
            override fun widgetSaved(savedPath: String) {
                count++

                scope.launch {
                    //Dismiss current snackbar to show new.
                    snackbarHost.currentSnackbarData?.dismiss()
                    snackbarHost.showSnackbar(
                        message = "Saved $count of ${widgetsList.size} your widgets...",
                        duration = SnackbarDuration.Indefinite
                    )
                }

                if (count != widgetsList.size) {
                    CoroutineScope(Dispatchers.IO).launch {
                        AppUtils.saveWidgetToDevice(
                            context,
                            widgetsList[count],
                            saveInterface!!
                        )
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(3000)
                        scope.launch {
                            //Dismiss current snackbar to show new.
                            snackbarHost.currentSnackbarData?.dismiss()
                            snackbarHost.showSnackbar(
                                message = "Saved all your widgets at 'Downloads/Any text widget",
                                duration = SnackbarDuration.Long
                            )
                        }
                        delay(1000)
                        if (context.interstitalAd != null) {
                            context.interstitalAd!!.show(context)
                        }

                    }
                }

            }

            override fun widgetSaveFailed() {
                scope.launch {
                    //Dismiss current snackbar to show new.
                    snackbarHost.currentSnackbarData?.dismiss()
                    snackbarHost.showSnackbar(
                        message = "Unable to save your widgets to device...",
                        duration = SnackbarDuration.Long
                    )
                }
            }

        }

        CoroutineScope(Dispatchers.IO).launch {
            AppUtils.saveWidgetToDevice(context, widgetsList[0], saveInterface)
        }
    }


    private fun optionsSheetClickEvent(
        event: String,
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        widgetData: WidgetData,
        uiList: SnapshotStateList<WidgetData>
    ) {
        when (event) {
            SAVE_FILE_CLICK_EVENT -> {
                //Show a snackbar that widget is getting saved.
                scope.launch {
                    //Dismiss current snackbar to show new.
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState.showSnackbar(
                        message = "Saving your widget, please wait...",
                        duration = SnackbarDuration.Indefinite,
                        withDismissAction = true
                    )

                }
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        val saveInterface: AppUtils.WidgetSaveInterface
                        withContext(Dispatchers.Main) {
                            saveInterface = object : AppUtils.WidgetSaveInterface {
                                override fun widgetSaved(savedPath: String) {

                                    scope.launch {
                                        //Dismiss current snackbar to show new.
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Widget saved to device at  'Downloads/Any text widget'",
                                            duration = SnackbarDuration.Indefinite,
                                            actionLabel = "Share"
                                        )

                                        if (result == SnackbarResult.ActionPerformed) {
                                            val intent = Intent(Intent.ACTION_SEND)
                                            intent.type = "*/*"
                                            intent.putExtra(
                                                Intent.EXTRA_STREAM,
                                                Uri.parse(savedPath)
                                            )
                                            context.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Share via"
                                                )
                                            )
                                        }
                                    }

                                }

                                override fun widgetSaveFailed() {

                                }

                            }
                        }
                        AppUtils.saveWidgetToDevice(context, widgetData, saveInterface)
                    }
                } catch (e: IOException) {
                    scope.launch {
                        //Dismiss current snackbar to show new.
                        snackbarHostState.currentSnackbarData?.dismiss()
                        val result = snackbarHostState.showSnackbar(
                            message = "Unable to save widget to device...",
                            duration = SnackbarDuration.Long,
                        )
                    }
                    e.printStackTrace()
                }
            }

            SAVE_IMAGE_CLICK_EVENT -> {
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        val bitmap = AppUtils.makeWidgetImage(context, widgetData)
                        val name = AppUtils.uniqueContentNameGenerator("Widget")
                        val savedPath = AppUtils.saveImageBitmap(context, name, bitmap)

                        scope.launch {
                            //Dismiss current snackbar to show new.
                            snackbarHostState.currentSnackbarData?.dismiss()
                            val result = snackbarHostState.showSnackbar(
                                message = "Widget saved as image to device at  'Pictures/Any text widget'",
                                actionLabel = "Share",
                                duration = SnackbarDuration.Indefinite
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                val intent = Intent(Intent.ACTION_SEND)
                                intent.type = "*/*"
                                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(savedPath))
                                context.startActivity(Intent.createChooser(intent, "Share via"))
                            }
                        }
                    }
                } catch (e: IOException) {
                    scope.launch {
                        //Dismiss current snackbar to show new.
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            message = "Unable to save widget as image, try again later...",
                            duration = SnackbarDuration.Long
                        )
                    }
                    e.printStackTrace()
                }
            }

            CLONE_CLICK_EVENT -> {
                cloneWidget(context = context, widgetData = widgetData, uiList = uiList)
                //Show message and update the widgets list.
                scope.launch {
                    //Dismiss current snackbar to show new.
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(message = "Widget cloned!")
                }
            }

            DELETE_CLICK_EVENT -> {
                deleteWidget(context, widgetData.widgetID!!, uiList = uiList)
                scope.launch {
                    //Dismiss current snackbar to show new.
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(message = "Widget deleted!")
                }
            }

            SHARE_CLICK_EVENT -> {
                shareWidget(
                    scope = scope,
                    snackbarHost = snackbarHostState,
                    widgetData = widgetData
                )
            }
        }
    }

    fun cloneWidget(
        context: AppCompatActivity,
        widgetData: WidgetData,
        uiList: SnapshotStateList<WidgetData>
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            val clonedWidgetData = WidgetData()
            clonedWidgetData.widgetText = widgetData.widgetText
            clonedWidgetData.widgetTextColor = widgetData.widgetTextColor
            clonedWidgetData.widgetBackGroundType = widgetData.widgetBackGroundType
            clonedWidgetData.widgetBackgroundColor = widgetData.widgetBackgroundColor
            clonedWidgetData.widgetBackgroundImageUri = widgetData.widgetBackgroundImageUri
            clonedWidgetData.widgetTextSize = widgetData.widgetTextSize
            clonedWidgetData.widgetTextFontID = widgetData.widgetTextFontID
            clonedWidgetData.widgetRoundCorners = widgetData.widgetRoundCorners
            clonedWidgetData.widgetFontInfo = widgetData.widgetFontInfo
            clonedWidgetData.widgetMultiImageList = widgetData.widgetMultiImageList
            clonedWidgetData.widgetTextVerticalGravity = widgetData.widgetTextVerticalGravity
            clonedWidgetData.widgetTextHorizontalGravity = widgetData.widgetTextHorizontalGravity
            clonedWidgetData.widgetClickAction = widgetData.widgetClickAction
            clonedWidgetData.widgetID = UUID.randomUUID().toString()
            clonedWidgetData.outlineEnabled = widgetData.outlineEnabled
            clonedWidgetData.widgetOutlineColor = widgetData.widgetOutlineColor
            clonedWidgetData.widgetOutlineWidth = widgetData.widgetOutlineWidth
            clonedWidgetData.widgetBackgroundGradient = widgetData.widgetBackgroundGradient
            clonedWidgetData.textShadowEnabled = widgetData.textShadowEnabled
            clonedWidgetData.textShadowData = widgetData.textShadowData


            //Save to preferences.
            saveNewWidget(context, clonedWidgetData)

            //Add to UI list.
            uiList.add(clonedWidgetData)
        }
    }

    fun saveNewWidget(context: AppCompatActivity, widgetData: WidgetData) {
        val widgetsList = ArrayList<WidgetData>()

        val sharedPreferences = context.getSharedPreferences("widgetspref", Context.MODE_PRIVATE)

        //Get the saved widgets
        val savedWidgetsJSON = sharedPreferences.getString("savedwidgets", null)

        if (savedWidgetsJSON != null) {
            val savedWidgetsList = getSavedWidgetsFromJSON(savedWidgetsJSON)
            widgetsList.addAll(savedWidgetsList)
        }

        //Add the new widget data and save it to the pref
        widgetsList.add(widgetData)

        val json = getJSONFromWidgetDataList(widgetsList)

        sharedPreferences.edit().putString("savedwidgets", json).apply()


    }

    fun getSavedWidgetsFromJSON(json: String): MutableList<WidgetData> {
        val gson = Gson()
        val type = object : TypeToken<MutableList<WidgetData>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getJSONFromWidgetDataList(widgetsList: MutableList<WidgetData>): String {
        val gson = Gson()
        return gson.toJson(widgetsList)
    }

    fun deleteWidget(
        context: AppCompatActivity,
        widgetID: String,
        uiList: SnapshotStateList<WidgetData>
    ) {
        val widgetsList = ArrayList<WidgetData>()

        val sharedPreferences = context.getSharedPreferences("widgetspref", Context.MODE_PRIVATE)

        //Get the saved widgets
        val savedWidgetsJSON = sharedPreferences.getString("savedwidgets", null)

        if (savedWidgetsJSON != null) {
            val savedWidgetsList = getSavedWidgetsFromJSON(savedWidgetsJSON)
            widgetsList.addAll(savedWidgetsList)
        }

        //Delete the widget data and save it to the pref
        val iterator = widgetsList.iterator()
        while (iterator.hasNext()) {
            val data = iterator.next()
            if (data.widgetID == widgetID) {
                iterator.remove()
            }
        }


        val json = getJSONFromWidgetDataList(widgetsList)

        sharedPreferences.edit().putString("savedwidgets", json).apply()

        //Delete from UI.
        //Delete the widget from UI.
        val uiIterator = uiList.iterator()
        while (uiIterator.hasNext()) {
            val data = uiIterator.next()
            if (data.widgetID == widgetID) {
                uiIterator.remove()
            }
        }
    }

    fun shareWidget(
        scope: CoroutineScope,
        snackbarHost: SnackbarHostState,
        widgetData: WidgetData
    ) {
        try {
            scope.launch {
                //Dismiss current snackbar to show new.
                snackbarHost.currentSnackbarData?.dismiss()
                snackbarHost.showSnackbar(
                    message = "Preparing to share your widget, please wait...",
                    duration = SnackbarDuration.Indefinite
                )
            }

            CoroutineScope(Dispatchers.IO).launch {
                val saveInterface: AppUtils.WidgetSaveInterface
                withContext(Dispatchers.Main) {
                    saveInterface = object : AppUtils.WidgetSaveInterface {
                        override fun widgetSaved(savedPath: String) {
                            //Dismiss current snackbar.
                            snackbarHost.currentSnackbarData?.dismiss()
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "*/*"
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(savedPath))
                            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            context.startActivity(Intent.createChooser(intent, "Share via"))
                        }

                        override fun widgetSaveFailed() {
                            scope.launch {
                                //Dismiss current snackbar to show new.
                                snackbarHost.currentSnackbarData?.dismiss()
                                snackbarHost.showSnackbar(message = "Unable to share widget, try again later...")
                            }

                        }

                    }
                }
                AppUtils.saveWidgetToDevice(context, widgetData, saveInterface)
            }
        } catch (e: IOException) {
            scope.launch {
                //Dismiss current snackbar to show new.
                snackbarHost.currentSnackbarData?.dismiss()
                snackbarHost.showSnackbar(message = "Unable to share widget, try again later...")
            }
            e.printStackTrace()
        }
    }

    fun loadWidgetFromFile(uri: Uri) {
        try {
            //Get the widget data from the file uri
            val objectInputStream = ObjectInputStream(context.contentResolver.openInputStream(uri))
            val appWidgetData = objectInputStream.readObject() as AppWidgetData

            if (appWidgetData.widgetData!!.widgetBackGroundType.equals("image")) {
                if (appWidgetData.widgetData!!.widgetMultiImageList == null) {
                    val imageList: MutableList<String>
                    imageList = ArrayList()

                    val imageBytesList: MutableList<ByteArray>
                    imageBytesList = ArrayList()

                    if (appWidgetData.widgetData!!.widgetBackgroundImageUri != null) {
                        imageList.add(appWidgetData.widgetData!!.widgetBackgroundImageUri!!)
                        appWidgetData.widgetData!!.widgetMultiImageList = imageList
                    }

                    if (appWidgetData.ifBackgroundImageBytesList == null) {
                        if (appWidgetData.ifBackgroundImageBytes != null) {
                            imageBytesList.add(appWidgetData.ifBackgroundImageBytes!!)
                            appWidgetData.ifBackgroundImageBytesList = imageBytesList
                        }
                    }
                }
            }

            //Add action data if the click action data is null
            if (appWidgetData.widgetData!!.widgetClickAction == null) {
                val actionData = ActionData()
                actionData.actionType = AppUtils.ACTIONS_APP
                actionData.actionName = "Any Text Widget"
                actionData.appPackageName = "com.rb.anytextwiget"
                appWidgetData.widgetData!!.widgetClickAction = actionData
            }


            //Add widget font info source name default
            if (appWidgetData.widgetData!!.widgetFontInfo!!.sourceName == null) {
                appWidgetData.widgetData!!.widgetFontInfo!!.sourceName = "NA"
            }

            //Add text shadow boolean and default data
            if (appWidgetData.widgetData!!.textShadowEnabled == null) {
                appWidgetData.widgetData!!.textShadowEnabled = false
            }

            if (appWidgetData.widgetData!!.textShadowData == null) {
                val data = TextShadowData()
                appWidgetData.widgetData!!.textShadowData = data
            }

            //Set the imported data.
            importSheetWidgetData = appWidgetData
            showImportSheet.value = true

        } catch (e: StreamCorruptedException) {
            Toast.makeText(context, "File corrupted", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } catch (e: IOException) {
            Toast.makeText(context, "Unable to load the file...", Toast.LENGTH_LONG).show()

            e.printStackTrace()
        }
    }

    suspend fun addWidgetToSavedWidgets(
        appWidgetData: AppWidgetData,
        scope: CoroutineScope,
        snackbarHost: SnackbarHostState
    ) {
        //Save the image(s) to device if the widget has background image(s) and the image(s) is(are) not available on the device
        if (appWidgetData.widgetData!!.widgetBackGroundType == "image") {
            if (appWidgetData.widgetData!!.widgetMultiImageList == null) {
                if (!AppUtils.contentExists(
                        appWidgetData.widgetData!!.widgetBackgroundImageUri,
                        context
                    )
                ) {
                    val savedUri = AppUtils.saveImageBytes(
                        context,
                        AppUtils.uniqueContentNameGenerator("Image"),
                        appWidgetData.ifBackgroundImageBytes!!
                    )
                    appWidgetData.widgetData!!.widgetBackgroundImageUri = savedUri
                }
            } else {
                for (imageUri in appWidgetData.widgetData!!.widgetMultiImageList!!) {
                    val index = appWidgetData.widgetData!!.widgetMultiImageList!!.indexOf(imageUri)
                    if (!AppUtils.contentExists(imageUri, context)) {
                        val savedUri = AppUtils.saveImageBytes(
                            context,
                            AppUtils.uniqueContentNameGenerator("Image"),
                            appWidgetData.ifBackgroundImageBytesList!!.get(index)
                        )
                        appWidgetData.widgetData!!.widgetMultiImageList!!.set(index, savedUri)
                    }
                }
            }
        }

        saveWidget(appWidgetData)

        //Add the color to saved colors if it isn't available
        saveNewColor(appWidgetData)

        scope.launch {
            snackbarHost.currentSnackbarData?.dismiss()
            snackbarHost.showSnackbar(
                message = "Widget added to your saved list!",
                duration = SnackbarDuration.Long,
                withDismissAction = true
            )
        }
    }

    suspend fun saveNewColor(appWidgetData: AppWidgetData) {
        var isTextColorAlreadyAvailable = false
        var isBackgroundColorAlreadyAvailable = false

        val savedColors = AppUtils.getSavedColors(context)
        val defaultColors = AppUtils.getDefaultColors(context)
        val dataList = ArrayList<ColorData>()
        dataList.addAll(defaultColors)
        dataList.addAll(savedColors)

        for (data in dataList) {
            if (data.ID == appWidgetData.widgetData!!.widgetTextColor!!.ID) {
                isTextColorAlreadyAvailable = true
            }

            if (appWidgetData.widgetData!!.widgetBackGroundType == "color") {
                if (data.ID == appWidgetData.widgetData!!.widgetBackgroundColor!!.ID) {
                    isBackgroundColorAlreadyAvailable = true
                }
            }
        }

        if (!isTextColorAlreadyAvailable) {
            AppUtils.saveNewColor(context, appWidgetData.widgetData!!.widgetTextColor!!)
        }

        if (!isBackgroundColorAlreadyAvailable) {
            AppUtils.saveNewColor(context, appWidgetData.widgetData!!.widgetBackgroundColor!!)
        }
    }

    suspend fun saveWidget(appWidgetData: AppWidgetData) {
        val widgetsList = ArrayList<WidgetData>()

        val sharedPreferences =
            context.getSharedPreferences("widgetspref", AppCompatActivity.MODE_PRIVATE)

        //Get the saved widgets
        val savedWidgetsJSON = sharedPreferences.getString("savedwidgets", null)

        if (savedWidgetsJSON != null) {
            val savedWidgetsList = getSavedWidgetsFromJSON(savedWidgetsJSON)
            widgetsList.addAll(savedWidgetsList)
        }

        //Add outline color to old widgets
        if (appWidgetData.widgetData!!.widgetOutlineColor == null) {
            appWidgetData.widgetData!!.widgetOutlineColor = AppUtils.getDefaultColors(context)[0]
        }

        //Add the new widget data and save it to the pref
        widgetsList.add(appWidgetData.widgetData!!)

        val json = getJSONFromWidgetDataList(widgetsList)

        sharedPreferences.edit().putString("savedwidgets", json).apply()
    }
}

