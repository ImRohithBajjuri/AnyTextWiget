package com.rb.anytextwiget.jetpackUI

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.rb.anytextwiget.AppUtils
import com.rb.anytextwiget.GradientData
import com.rb.anytextwiget.R
import com.rb.anytextwiget.TextGravityData
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradientsSheet(
    currentGradient: GradientData,
    gradientSelectedEvent: (gradientData: GradientData) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val fontUtils = FontUtils()

    val gradients = SnapshotStateList<GradientData>()

    LaunchedEffect(Dispatchers.Main){
        gradients.addAll(AppUtils.getGradients(context))
    }


    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            text = "Gradients",
            fontFamily = fontUtils.openSans(FontWeight.Bold),
            fontSize = TextUnit(28f, TextUnitType.Sp),
            modifier = Modifier.padding(20.dp)
        )

        LazyColumn() {
            items(gradients) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    GradientItem(fontUtils = fontUtils, gradientData = it, gradientSelectedEvent = gradientSelectedEvent)
                }
            }
        }

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun GradientItem(fontUtils: FontUtils, gradientData: GradientData, gradientSelectedEvent: (gradientData: GradientData) -> Unit) {

    val context = LocalContext.current

    var gradientImage by remember {
        mutableIntStateOf(R.drawable.no_corners_gradient_cyan_purple)
    }

    //Get the gradient
    LaunchedEffect(Dispatchers.IO) {
        val sourceName = "no_corners_" + gradientData.sourceName

        try {
            gradientImage =
                context.resources.getIdentifier(sourceName, "drawable", "com.rb.anytextwiget")
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

    }


    TextButton(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(15.dp),
        onClick = {gradientSelectedEvent(gradientData)}) {

        AndroidView(factory = {
            ImageView(it).apply {
                layoutParams = FrameLayout.LayoutParams(AppUtils.dptopx(context, 50), AppUtils.dptopx(context, 50))
                Glide.with(context).load(ContextCompat.getDrawable(context, gradientImage)).circleCrop().into(this)
            }
        }, update = {
            Glide.with(context).load(ContextCompat.getDrawable(context, gradientImage)).circleCrop().into(it)
        }, modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(10.dp, 0.dp))

        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        ) {
            Text(
                text = gradientData.name,
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontFamily = fontUtils.openSans(FontWeight.Normal),
                textAlign = TextAlign.Start,
            )
            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_lens_16),
                    contentDescription = gradientData.colorOne,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(10.dp, 0.dp, 5.dp, 0.dp),
                    colorFilter = ColorFilter.tint(color = Color(android.graphics.Color.parseColor(gradientData.colorOne)))
                )

                Text(
                    text = gradientData.colorOne,
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(FontWeight.Normal),
                    textAlign = TextAlign.Start,
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_lens_16),
                    contentDescription = gradientData.colorTwo,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(10.dp, 0.dp, 5.dp, 0.dp),
                    colorFilter = ColorFilter.tint(color = Color(android.graphics.Color.parseColor(gradientData.colorTwo)))
                )

                Text(
                    text = gradientData.colorTwo,
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    fontFamily = fontUtils.openSans(FontWeight.Normal),
                    textAlign = TextAlign.Start,
                )
            }

        }

    }
}
