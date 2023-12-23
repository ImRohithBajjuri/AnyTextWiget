package com.rb.anytextwiget.jetpackUI

import android.text.TextUtils
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.provider.FontsContractCompat.FontInfo
import com.rb.anytextwiget.AppUtils
import com.rb.anytextwiget.ColorData
import com.rb.anytextwiget.FontItemData
import com.rb.anytextwiget.R
import com.rb.anytextwiget.WidgetFontInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontsSheet(currentFontData: WidgetFontInfo, fontSelectedEvent: (fontInfo: WidgetFontInfo) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current

    val fontUtils = FontUtils()

    var searchQuery by remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()

    var showTrailing by remember {
        mutableStateOf(false)
    }

    val fontsList = SnapshotStateList<FontItemData>()

    val viewingFontsList = SnapshotStateList<FontItemData>()


    LaunchedEffect(Dispatchers.IO) {
        fontsList.addAll(AppUtils.getNewFontsList(context))
        viewingFontsList.addAll(fontsList)
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            text = "Fonts",
            fontFamily = fontUtils.openSans(FontWeight.Bold),
            fontSize = TextUnit(28f, TextUnitType.Sp),
            modifier = Modifier.padding(20.dp)
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                viewingFontsList.clear()
                if (!TextUtils.isEmpty(it.trim())) {
                    for (data in fontsList) {
                        if (data.normalInfo!!.fontName.trim().lowercase()
                                .startsWith(it.trim().lowercase())
                        ) {
                            viewingFontsList.add(data)
                        }
                    }
                } else {
                    viewingFontsList.addAll(fontsList)
                }
            },
            onSearch = {},
            active = false,
            onActiveChange = {
                showTrailing = it

                viewingFontsList.clear()
                if (!it) {
                    viewingFontsList.addAll(fontsList)
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search fonts icon"
                )
            },
            trailingIcon = {
                AnimatedVisibility(visible = showTrailing, enter = slideInHorizontally { it * 2 },
                    exit = slideOutHorizontally { it * 2 }) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "Search fonts icon"
                        )
                    }
                }
            },
            placeholder = {
                Text(
                    text = "Search fonts with name",
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(FontWeight.SemiBold)
                )
            },
            modifier = Modifier.padding(10.dp, 0.dp)
        ) {

        }

        LazyColumn() {
            items(viewingFontsList) {
                FontItem(it, currentFontData, fontSelectedEvent)
            }
        }


    }

}


@Composable
fun FontItem(fontItemData: FontItemData, currentFontData: WidgetFontInfo, fontSelectedEvent: (fontInfo: WidgetFontInfo) -> Unit) {
    val fontUtils = FontUtils()
    val context = LocalContext.current

    var font = ResourcesCompat.getFont(
        context,
        R.font.open_sans_semibold
    )

    try {
        val id = context.resources.getIdentifier(
            fontItemData.normalInfo!!.sourceName,
            "font",
            context.packageName
        )

        font = ResourcesCompat.getFont(context, id)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    Column {
        Text(
            text = fontItemData.normalInfo!!.fontName,
            fontSize = TextUnit(18f, TextUnitType.Sp),
            fontFamily = fontUtils.openSans(
                FontWeight.SemiBold
            ),
            modifier = Modifier.padding(10.dp, 20.dp, 10.dp, 5.dp)
        )

        Card(
            elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
            modifier = Modifier
                .padding(10.dp, 10.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        )
        {
            TextButton(
                onClick = {
                    fontSelectedEvent(fontItemData.normalInfo!!)
                },
                modifier = Modifier
                    .fillMaxWidth(),
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
                        text = fontItemData.normalInfo!!.fontName,
                        fontSize = TextUnit(18f, TextUnitType.Sp),
                        fontFamily = FontFamily(typeface = font!!),
                        textAlign = TextAlign.Start,
                    )
                    Text(
                        text = fontItemData.normalInfo!!.fontName,
                        fontSize = TextUnit(16f, TextUnitType.Sp),
                        fontFamily = fontUtils.openSans(FontWeight.Normal),
                        textAlign = TextAlign.Start,
                    )
                }

                if (fontItemData.normalInfo!!.sourceName == currentFontData.sourceName) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                        contentDescription = "Selected font icon",
                        modifier = Modifier.padding(10.dp, 0.dp)
                    )
                }

            }


            //Italic
            if (fontItemData.italicInfo != null) {
                TextButton(
                    onClick = {
                        fontSelectedEvent(fontItemData.italicInfo!!)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    var font = ResourcesCompat.getFont(
                        context,
                        R.font.open_sans_semibold
                    )

                    try {
                        val id = context.resources.getIdentifier(
                            fontItemData.italicInfo!!.sourceName,
                            "font",
                            context.packageName
                        )

                        font = ResourcesCompat.getFont(context, id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
                            text = fontItemData.italicInfo!!.fontName,
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = FontFamily(typeface = font!!),
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = fontItemData.italicInfo!!.fontName,
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(FontWeight.Normal),
                            textAlign = TextAlign.Start,
                        )
                    }

                    if (fontItemData.italicInfo!!.sourceName == currentFontData.sourceName) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                            contentDescription = "Selected font icon",
                            modifier = Modifier.padding(10.dp, 0.dp)
                        )
                    }
                }
            }

            //Medium
            if (fontItemData.mediumInfo != null) {
                TextButton(
                    onClick = {
                        fontSelectedEvent(fontItemData.mediumInfo!!)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    var font = ResourcesCompat.getFont(
                        context,
                        R.font.open_sans_semibold
                    )

                    try {
                        val id = context.resources.getIdentifier(
                            fontItemData.mediumInfo!!.sourceName,
                            "font",
                            context.packageName
                        )

                        font = ResourcesCompat.getFont(context, id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
                            text = fontItemData.mediumInfo!!.fontName,
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = FontFamily(typeface = font!!),
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = fontItemData.mediumInfo!!.fontName,
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(FontWeight.Normal),
                            textAlign = TextAlign.Start,
                        )
                    }

                    if (fontItemData.mediumInfo!!.sourceName == currentFontData.sourceName) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                            contentDescription = "Selected font icon",
                            modifier = Modifier.padding(10.dp, 0.dp)
                        )
                    }
                }
            }

            //Semibold
            if (fontItemData.semiboldInfo != null) {
                TextButton(
                    onClick = {
                        fontSelectedEvent(fontItemData.semiboldInfo!!)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    var font = ResourcesCompat.getFont(
                        context,
                        R.font.open_sans_semibold
                    )

                    try {
                        val id = context.resources.getIdentifier(
                            fontItemData.semiboldInfo!!.sourceName,
                            "font",
                            context.packageName
                        )

                        font = ResourcesCompat.getFont(context, id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
                            text = fontItemData.semiboldInfo!!.fontName,
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = FontFamily(typeface = font!!),
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = fontItemData.semiboldInfo!!.fontName,
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(FontWeight.Normal),
                            textAlign = TextAlign.Start,
                        )
                    }

                    if (fontItemData.semiboldInfo!!.sourceName == currentFontData.sourceName) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                            contentDescription = "Selected font icon",
                            modifier = Modifier.padding(10.dp, 0.dp)
                        )
                    }
                }
            }

            //Bold
            if (fontItemData.boldInfo != null) {
                TextButton(
                    onClick = {
                        fontSelectedEvent(fontItemData.boldInfo!!)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    var font = ResourcesCompat.getFont(
                        context,
                        R.font.open_sans_semibold
                    )

                    try {
                        val id = context.resources.getIdentifier(
                            fontItemData.boldInfo!!.sourceName,
                            "font",
                            context.packageName
                        )

                        font = ResourcesCompat.getFont(context, id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
                            text = fontItemData.boldInfo!!.fontName,
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = FontFamily(typeface = font!!),
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = fontItemData.boldInfo!!.fontName,
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(FontWeight.Normal),
                            textAlign = TextAlign.Start,
                        )
                    }

                    if (fontItemData.boldInfo!!.sourceName == currentFontData.sourceName) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                            contentDescription = "Selected font icon",
                            modifier = Modifier.padding(10.dp, 0.dp)
                        )
                    }
                }
            }

            //Light
            if (fontItemData.lightInfo != null) {
                TextButton(
                    onClick = {
                        fontSelectedEvent(fontItemData.lightInfo!!)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    var font = ResourcesCompat.getFont(
                        context,
                        R.font.open_sans_semibold
                    )

                    try {
                        val id = context.resources.getIdentifier(
                            fontItemData.lightInfo!!.sourceName,
                            "font",
                            context.packageName
                        )

                        font = ResourcesCompat.getFont(context, id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
                            text = fontItemData.lightInfo!!.fontName,
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = FontFamily(typeface = font!!),
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = fontItemData.lightInfo!!.fontName,
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(FontWeight.Normal),
                            textAlign = TextAlign.Start,
                        )
                    }

                    if (fontItemData.lightInfo!!.sourceName == currentFontData.sourceName) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                            contentDescription = "Selected font icon",
                            modifier = Modifier.padding(10.dp, 0.dp)
                        )
                    }
                }
            }

            //Extra bold
            if (fontItemData.extraBoldInfo != null) {
                TextButton(
                    onClick = {
                        fontSelectedEvent(fontItemData.extraBoldInfo!!)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    var font = ResourcesCompat.getFont(
                        context,
                        R.font.open_sans_semibold
                    )

                    try {
                        val id = context.resources.getIdentifier(
                            fontItemData.extraBoldInfo!!.sourceName,
                            "font",
                            context.packageName
                        )

                        font = ResourcesCompat.getFont(context, id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
                            text = fontItemData.extraBoldInfo!!.fontName,
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontFamily = FontFamily(typeface = font!!),
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = fontItemData.extraBoldInfo!!.fontName,
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                            fontFamily = fontUtils.openSans(FontWeight.Normal),
                            textAlign = TextAlign.Start,
                        )
                    }

                    if (fontItemData.extraBoldInfo!!.sourceName == currentFontData.sourceName) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_check_circle_24),
                            contentDescription = "Selected font icon",
                            modifier = Modifier.padding(10.dp, 0.dp)
                        )
                    }
                }
            }

        }
    }


}




