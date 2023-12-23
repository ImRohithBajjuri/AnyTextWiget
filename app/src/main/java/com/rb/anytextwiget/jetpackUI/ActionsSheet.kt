package com.rb.anytextwiget.jetpackUI

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil.compose.rememberAsyncImagePainter
import com.rb.anytextwiget.ActionData
import com.rb.anytextwiget.AppUtils
import com.rb.anytextwiget.GradientData
import com.rb.anytextwiget.R
import com.rb.anytextwiget.databinding.AddNameLayoutBinding
import com.rb.anytextwiget.ui.theme.darkTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class ActionsSheet(var context: Context, var installedApps: MutableList<String>) {

    val appUtils = AppUtils()


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ActionsSheetUI(
        imagesList: SnapshotStateList<String>,
        currentActionData: ActionData,
        actionSelectedEvent: (actionData: ActionData) -> Unit,
        onDismiss: () -> Unit
    ) {
        val fontUtils = FontUtils()

        ModalBottomSheet(onDismissRequest = onDismiss) {
            Text(
                text = "Actions",
                fontFamily = fontUtils.openSans(FontWeight.Bold),
                fontSize = TextUnit(28f, TextUnitType.Sp),
                modifier = Modifier.padding(20.dp)
            )

            /*    Text(
                    text = "Simple Actions",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(
                        FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(10.dp, 30.dp, 10.dp, 10.dp)
                )

                //Simple actions.
                Card(
                    elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    //Wifi simple action.
                    TextButton(modifier = Modifier
                        .fillMaxWidth(),
                        onClick = {
                            val actionData = ActionData()
                            actionData.actionType = AppUtils.ACTIONS_SIMPLE
                            actionData.actionName = AppUtils.ACTION_WIFI
                            actionSelectedEvent(actionData)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_wifi_50),
                            contentDescription = "Wifi action",
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
                                text = "Wi-Fi",
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    "Opens Wi-Fi panel"
                                } else {
                                    "Toggles Wi-Fi on/off"
                                },
                                fontSize = TextUnit(16f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                        }

                        if (currentActionData.actionName == AppUtils.ACTION_WIFI) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                                contentDescription = "Selected simple action icon",
                                modifier = Modifier.padding(10.dp, 0.dp)
                            )
                        }

                    }



                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        //DnD simple action.
                        TextButton(modifier = Modifier
                            .fillMaxWidth(),
                            onClick = {
                                val actionData = ActionData()
                                actionData.actionType = AppUtils.ACTIONS_SIMPLE
                                actionData.actionName = AppUtils.ACTION_DONOTDISTURB
                                actionSelectedEvent(actionData)
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_round_do_not_disturb_on_50),
                                contentDescription = "Do not disturb action",
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
                                    text = "Do not disturb",
                                    fontSize = TextUnit(18f, TextUnitType.Sp),
                                    fontFamily = fontUtils.openSans(FontWeight.Normal),
                                    textAlign = TextAlign.Start,
                                )
                                Text(
                                    text = "Toggles do not disturb on/off",
                                    fontSize = TextUnit(16f, TextUnitType.Sp),
                                    fontFamily = fontUtils.openSans(FontWeight.Normal),
                                    textAlign = TextAlign.Start,
                                )
                            }

                            if (currentActionData.actionName == AppUtils.ACTION_DONOTDISTURB) {

                                Icon(
                                    painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                                    contentDescription = "Selected simple action icon",
                                    modifier = Modifier.padding(10.dp, 0.dp)
                                )
                            }
                        }

                        //Flashlight simple action.
                        TextButton(modifier = Modifier
                            .fillMaxWidth(),
                            onClick = {
                                val actionData = ActionData()
                                actionData.actionType = AppUtils.ACTIONS_SIMPLE
                                actionData.actionName = AppUtils.ACTION_FLASHLIGHT

                                actionSelectedEvent(actionData)
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_round_flashlight_on_50),
                                contentDescription = "Flashlight action",
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
                                    text = "Flashlight",
                                    fontSize = TextUnit(18f, TextUnitType.Sp),
                                    fontFamily = fontUtils.openSans(FontWeight.Normal),
                                    textAlign = TextAlign.Start,
                                )
                                Text(
                                    text = "Toggles flashlight on/off",
                                    fontSize = TextUnit(16f, TextUnitType.Sp),
                                    fontFamily = fontUtils.openSans(FontWeight.Normal),
                                    textAlign = TextAlign.Start,
                                )
                            }

                            if (currentActionData.actionName == AppUtils.ACTION_FLASHLIGHT) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                                    contentDescription = "Selected simple action icon",
                                    modifier = Modifier.padding(10.dp, 0.dp)
                                )
                            }

                        }


                    }


                    //Bluetooth simple action.
                    TextButton(modifier = Modifier
                        .fillMaxWidth(),
                        onClick = {
                            val actionData = ActionData()
                            actionData.actionType = AppUtils.ACTIONS_SIMPLE
                            actionData.actionName = AppUtils.ACTION_BLUETOOTH

                            actionSelectedEvent(actionData)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_bluetooth_50),
                            contentDescription = "Bluetooth action",
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
                                text = "Bluetooth",
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = "Toggles bluetooth on/off",
                                fontSize = TextUnit(16f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                        }

                        if (currentActionData.actionName == AppUtils.ACTION_BLUETOOTH) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                                contentDescription = "Selected simple action icon",
                                modifier = Modifier.padding(10.dp, 0.dp)
                            )
                        }

                    }

                    //Next image action.
                    if (imagesList.size > 1) {
                        TextButton(modifier = Modifier
                            .fillMaxWidth(),
                            onClick = {
                                val actionData = ActionData()
                                actionData.actionType = AppUtils.ACTIONS_SIMPLE
                                actionData.actionName = AppUtils.ACTION_NEXTIMAGE

                                actionSelectedEvent(actionData)
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_round_skip_next_50),
                                contentDescription = "Show next image action",
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
                                    text = "Next image",
                                    fontSize = TextUnit(18f, TextUnitType.Sp),
                                    fontFamily = fontUtils.openSans(FontWeight.Normal),
                                    textAlign = TextAlign.Start,
                                )
                                Text(
                                    text = "Shows next image",
                                    fontSize = TextUnit(16f, TextUnitType.Sp),
                                    fontFamily = fontUtils.openSans(FontWeight.Normal),
                                    textAlign = TextAlign.Start,
                                )
                            }

                            if (currentActionData.actionName == AppUtils.ACTION_NEXTIMAGE) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                                    contentDescription = "Selected simple action icon",
                                    modifier = Modifier.padding(10.dp, 0.dp)
                                )
                            }

                        }
                    }

                    //Link simple action.
                    TextButton(modifier = Modifier
                        .fillMaxWidth(),
                        onClick = {setLinkDialog("https://", darkTheme.value, actionSelectedEvent)}
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.rounded_open_in_browser_50dp),
                            contentDescription = "Open link action",
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
                                text = "Open link",
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = "Opens the specified link",
                                fontSize = TextUnit(16f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                        }

                        if (currentActionData.actionName == AppUtils.ACTION_OPEN_LINK) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                                contentDescription = "Selected simple action icon",
                                modifier = Modifier.padding(10.dp, 0.dp)
                            )
                        }

                    }

                    //Do nothing action.
                    TextButton(modifier = Modifier
                        .fillMaxWidth(),
                        onClick = {
                            val actionData = ActionData()
                            actionData.actionType = AppUtils.ACTIONS_SIMPLE
                            actionData.actionName = AppUtils.ACTION_NOTHING

                            actionSelectedEvent(actionData)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_do_disturb_alt_50),
                            contentDescription = "Do nothing action",
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
                                text = "Do nothing",
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = "Does nothing when clicked",
                                fontSize = TextUnit(16f, TextUnitType.Sp),
                                fontFamily = fontUtils.openSans(FontWeight.Normal),
                                textAlign = TextAlign.Start,
                            )
                        }

                        if (currentActionData.actionName == AppUtils.ACTION_NOTHING) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                                contentDescription = "Selected simple action icon",
                                modifier = Modifier.padding(10.dp, 0.dp)
                            )
                        }

                    }
                }*/



            Text(
                text = "App Actions",
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontFamily = fontUtils.openSans(
                    FontWeight.SemiBold
                ),
                modifier = Modifier.padding(10.dp, 30.dp, 10.dp, 10.dp)
            )
            //App actions.
            Card(
                elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                modifier = Modifier
                    .padding(10.dp, 0.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                LazyColumn(content = {
                    items(installedApps) {
                        this@LazyColumn.ActionItem(fontUtils = fontUtils, packageName = it) {
                            actionSelectedEvent(it)
                        }
                    }
                })
            }
        }
    }


    fun setLinkDialog(
        currentLink: String,
        isNight: Boolean,
        actionSelectedEvent: (actionData: ActionData) -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Set a link")

        val view = AddNameLayoutBinding.inflate(LayoutInflater.from(context))
        val editText = view.nameinput
        editText.setText(currentLink)
        editText.hint = "Enter a url/web link"
        editText.inputType = InputType.TYPE_TEXT_VARIATION_URI
        editText.setHintTextColor(ContextCompat.getColor(context, R.color.white))
        editText.background = ColorDrawable(Color.TRANSPARENT)


        //Set dark mode for edit text
        if (isNight) {
            editText.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            editText.setTextColor(ContextCompat.getColor(context, R.color.Black))
        }
        builder.setView(view.root)


        builder.setPositiveButton("set") { p0, p1 ->
            val actionData = ActionData()
            actionData.actionType = AppUtils.ACTIONS_SIMPLE
            actionData.actionName = AppUtils.ACTION_OPEN_LINK
            actionData.actionExtra = editText.text.toString()

            actionSelectedEvent(actionData)
        }

        builder.setNegativeButton(
            "cancel"
        ) { p0, p1 -> //Do nothing}
        }
        builder.show()
    }

    @Composable
    fun LazyListScope.ActionItem(fontUtils: FontUtils, packageName: String, actionSelectedEvent: (actionData: ActionData) -> Unit) {
        val appInfo =
            context.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val appName = context.packageManager.getApplicationLabel(appInfo)
        val appIcon = context.packageManager.getApplicationIcon(appInfo)

        val appIntents: SnapshotStateList<ActivityInfo> = mutableStateListOf()
        appIntents.addAll(
            context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_ACTIVITIES
            ).activities.asList()
        )

        var showActivities by remember {
            mutableStateOf(false)
        }


        val appImage = appIcon.toBitmap(
            width = AppUtils.dptopx(context, 35),
            height = AppUtils.dptopx(context, 35)
        )


        item {
            TextButton(onClick = {
                showActivities = !showActivities
            }, modifier = Modifier.fillMaxWidth()) {
                Image(
                    bitmap = appImage.asImageBitmap(),
                    contentDescription = "${appName} app icon",
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = appName.toString(),
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(FontWeight.Normal),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )

                FilledTonalIconButton(onClick = {showActivities = !showActivities}, modifier = Modifier
                    .padding(10.dp).size(24.dp)
                    .align(Alignment.CenterVertically)) {
                    Icon(imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = "Expand app actions icon", modifier = Modifier.size(18.dp))
                }
            }
        }


        items(appIntents) {
            AnimatedVisibility(
                visible = showActivities,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                TextButton(
                    onClick = {
                        val actionData = ActionData()
                        actionData.actionName = appName.toString()
                        actionData.actionType = AppUtils.ACTIONS_APP
                        actionData.appPackageName = it.name

                        actionSelectedEvent(actionData)}, modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Image(
                        bitmap = appImage.asImageBitmap(),
                        contentDescription = "${appName} app icon",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(10.dp)
                            .align(Alignment.CenterVertically)
                    )

                    Text(
                        text = it.name,
                        fontSize = TextUnit(16f, TextUnitType.Sp),
                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                    )
                }
            }

        }


    }

}