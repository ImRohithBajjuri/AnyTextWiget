package com.rb.anytextwiget

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.widget.*
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentInformation.ConsentStatus
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rb.anytextwiget.databinding.ActivityMainBinding
import com.rb.anytextwiget.jetpackUI.MainPage
import com.rb.anytextwiget.ui.theme.AnyTextWigetTheme
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.StreamCorruptedException


class MainActivity : AppCompatActivity(), WidgetOptionsSheet.WidgetOptionsInterface,
    WidgetsAdapter.SortInterface {
    lateinit var dataList: MutableList<WidgetData>
    lateinit var adapter: WidgetsAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var fontsList: MutableList<FontItemData>
    var hasWidgetWithImage: Boolean = false
    val REQUEST_WIDGET_FILE = 79
    lateinit var themePreferences: SharedPreferences
    val REQUEST_SETTING = 93
    var isDark: Boolean = false
    lateinit var itemTouchHelper: ItemTouchHelper
    var isSorting: Boolean = false

    var interstitalAd: InterstitialAd? = null

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themePreferences = getSharedPreferences("apppref", MODE_PRIVATE)
        if (themePreferences.getString("apptheme", AppUtils.LIGHT)!! == AppUtils.LIGHT) {
            setTheme(R.style.AppTheme)
        }
        if (themePreferences.getString("apptheme", AppUtils.LIGHT)!! == AppUtils.DARK) {
            setTheme(R.style.AppThemeDark)
        }
        if (themePreferences.getString("apptheme", AppUtils.LIGHT)!! == AppUtils.FOLLOW_SYSTEM) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    setTheme(R.style.AppThemeDark)
                }

                Configuration.UI_MODE_NIGHT_NO -> {
                    setTheme(R.style.AppTheme)
                }
            }
        }

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))

        val isJetpackUI = true
        if (isJetpackUI) {

            dataList = ArrayList<WidgetData>()


            //Get the saved widgets
            sharedPreferences = getSharedPreferences("widgetspref", MODE_PRIVATE)
            val savedWidgetsJSON = sharedPreferences.getString("savedwidgets", null)

            if (savedWidgetsJSON != null) {
                val savedWidgetsList = getSavedWidgets(savedWidgetsJSON)
                dataList.addAll(savedWidgetsList)
            }
            val mainPage = MainPage(context = this, widgetsList = dataList)

            setContent {
                AnyTextWigetTheme() {
                  mainPage.MainUI()
                }
            }
            return
        }

        setContentView(binding.root)

        var count = themePreferences.getInt("opencount", 0)
        count++
        themePreferences.edit().putInt("opencount", count).apply()

        if (count == 3) {
            askRating()
        }

        if (count == 3 || count == 5 || count == 7 || count == 8 || count == 10) {
            themePreferences.edit().putBoolean("supportAdViewed2", false).apply()
        }


        val whatsNewPreference = getSharedPreferences("whatsNewPreference", MODE_PRIVATE)
        val showNewReleases = whatsNewPreference.getBoolean("show20", true)
        if (showNewReleases) {
            val newReleasesSheet = NewReleasesSheet()
            newReleasesSheet.show(supportFragmentManager, "useCaseOne")
            whatsNewPreference.edit().putBoolean("show20", false).apply()
        }

        //Adjust UI with app theme
        val appTheme = themePreferences.getString("apptheme", AppUtils.LIGHT)
        adjustTheme(appTheme!!)
        

        //Control ads
        setAds()

        //Load interstitial Ad
        loadInterstitalAd()






        binding.mainToolbar.inflateMenu(R.menu.main)
        binding.mainToolbarDark.inflateMenu(R.menu.main)

        binding.mainToolbar.overflowIcon!!.setTint(
            ResourcesCompat.getColor(
                resources,
                R.color.white,
                null
            )
        )
        binding.mainToolbarDark.overflowIcon!!.setTint(
            ResourcesCompat.getColor(
                resources,
                R.color.white,
                null
            )
        )

        binding.mainToolbar.setOnMenuItemClickListener { item ->
            if (item!!.itemId == R.id.settings) {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivityForResult(intent, REQUEST_SETTING)
                overridePendingTransition(R.anim.activity_open, R.anim.activity_stable)
            }

            if (item.itemId == R.id.importwidget) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.data = MediaStore.Files.getContentUri("external")
                intent.type = "application/*"
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.flags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                if (AppUtils.hasStoragePermission(this@MainActivity)) {
                    startActivityForResult(intent, REQUEST_WIDGET_FILE)
                } else {
                    askPermission2()
                }
            }

            if (item.itemId == R.id.sort) {
                if (dataList.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No widgets to sort", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    handleSort(toSort = true)
                }
            }

            if (item.itemId == R.id.help) {
                val helpSheet = HelpSheet()
                helpSheet.show(supportFragmentManager, "useCaseOne")
            }

            if (item.itemId == R.id.refresh) {
                if (dataList.isNotEmpty()) {
                    adapter.notifyDataSetChanged()
                }

                AppUtils.updateUIWidgets(this@MainActivity)

            }

            if (item.itemId == R.id.saveAll) {
                if (AppUtils.hasStoragePermission(this@MainActivity)) {
                    if (dataList.isNotEmpty()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            saveAllWidgets()
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "No widgets to save",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val builder = askStoragePermission()
                    builder.setPositiveButton("GIVE", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                ActivityCompat.requestPermissions(
                                    this@MainActivity, arrayOf(
                                        Manifest.permission.READ_MEDIA_IMAGES,
                                        Manifest.permission.ACCESS_MEDIA_LOCATION

                                    ), 545
                                )
                            } else {
                                ActivityCompat.requestPermissions(
                                    this@MainActivity, arrayOf(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ), 545
                                )
                            }
                        }
                    })
                    builder.show()
                }

            }

            if (item.itemId == R.id.loopVideo) {
                if (AppUtils.hasStoragePermission(this)) {
                    makeWidgetVideoLoop()
                } else {
                    val builder = AppUtils.buildStoragePermission(this)
                    builder.setMessage("Storage permission is required for creating a widget loop video...")
                    builder.show()
                }
            }
            true
        }
        binding.mainToolbarDark.setOnMenuItemClickListener { item ->
            if (item!!.itemId == R.id.settings) {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivityForResult(intent, REQUEST_SETTING)
                overridePendingTransition(R.anim.activity_open, R.anim.activity_stable)
            }

            if (item.itemId == R.id.importwidget) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.data = MediaStore.Files.getContentUri("external")
                intent.type = "application/*"
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.flags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                if (AppUtils.hasStoragePermission(this@MainActivity)) {
                    startActivityForResult(intent, REQUEST_WIDGET_FILE)
                } else {
                    askPermission2()
                }
            }

            if (item.itemId == R.id.sort) {
                if (dataList.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No widgets to sort", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    handleSort(toSort = true)
                }
            }

            if (item.itemId == R.id.help) {
                val helpSheet = HelpSheet()
                helpSheet.show(supportFragmentManager, "useCaseOne")
            }

            if (item.itemId == R.id.refresh) {
                if (dataList.isNotEmpty()) {
                    adapter.notifyDataSetChanged()
                }

                AppUtils.updateUIWidgets(applicationContext)

            }

            if (item.itemId == R.id.saveAll) {
                if (AppUtils.hasStoragePermission(this@MainActivity)) {
                    if (dataList.isNotEmpty()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            saveAllWidgets()
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "No widgets to save",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val builder = askStoragePermission()
                    builder.setPositiveButton("GIVE", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                ActivityCompat.requestPermissions(
                                    this@MainActivity, arrayOf(
                                        Manifest.permission.READ_MEDIA_IMAGES,
                                        Manifest.permission.ACCESS_MEDIA_LOCATION
                                    ), 545
                                )
                            } else {
                                ActivityCompat.requestPermissions(
                                    this@MainActivity, arrayOf(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ), 545
                                )
                            }
                        }
                    })
                    builder.show()
                }
            }

            if (item.itemId == R.id.loopVideo) {
                if (AppUtils.hasStoragePermission(this)) {
                    makeWidgetVideoLoop()
                } else {
                    val builder = AppUtils.buildStoragePermission(this)
                    builder.setMessage("Storage permission is required for creating a widget loop video...")
                    builder.show()
                }
            }
            true
        }




        dataList = ArrayList<WidgetData>()


        //Get the saved widgets
        sharedPreferences = getSharedPreferences("widgetspref", MODE_PRIVATE)
        val savedWidgetsJSON = sharedPreferences.getString("savedwidgets", null)

        if (savedWidgetsJSON != null) {
            val savedWidgetsList = getSavedWidgets(savedWidgetsJSON)
            dataList.addAll(savedWidgetsList)
        }

        if (dataList.isEmpty()) {
            binding.nowidgetsplaceholder.visibility = View.VISIBLE
        } else {
            binding.nowidgetsplaceholder.visibility = View.GONE
        }


        //Show dialog when user opens through atw file
        if (intent.action == "android.intent.action.VIEW") {
            if (AppUtils.hasStoragePermission(this)) {
                loadWidgetFromFile(intent.data!!)
            } else {
                askPermission2()
            }
        }


        //Ask storage permission if there is any widget with image background
        CoroutineScope(Dispatchers.IO).launch {
            for (data in dataList) {
                if (data.widgetBackGroundType == "image") {
                    if (!AppUtils.hasStoragePermission(this@MainActivity)) {
                        withContext(Dispatchers.Main) {
                            askPermission()
                        }
                        hasWidgetWithImage = true
                    }
                    break
                }
            }
        }


        //Add widget outline color for old widgets
        if (!dataList.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val iterator = dataList.iterator()
                while (iterator.hasNext()) {
                    val data = iterator.next()

                    /*      if (data.widgetFontInfo == null) {
                              val widgetFontInfo =
                                  AppUtils.createWidgetInfoWithID(fontsList, data.widgetTextFontID)
                              data.widgetFontInfo = widgetFontInfo

                              //Update the widgets on the home screen
                              updateUIWidget(data)
                          }*/

                    /*      if (data.widgetClickAction == null) {
                              val actionData = ActionData()
                              actionData.actionName = "Any Text Widget"
                              actionData.appPackageName = "com.rb.anytextwiget"
                              actionData.actionType = AppUtils.ACTIONS_APP
                              data.widgetClickAction = actionData
                          }*/

                    if (data.widgetOutlineColor == null) {
                        data.widgetOutlineColor = AppUtils.getDefaultColors(this@MainActivity)[0]
                    }

                }
                saveWidgets()
            }
        }


        val linearLayoutManager = LinearLayoutManager(applicationContext)
        binding.recy.layoutManager = linearLayoutManager
        adapter = WidgetsAdapter(this, dataList, 0, "main", this, this)
        if (!hasWidgetWithImage) {
            binding.recy.adapter = adapter
        }

        (binding.recy.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        //Update the first displayed widget manually to set it's typeface.
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            if (dataList.isNotEmpty()) {
                adapter.notifyItemChanged(0)
                adapter.notifyItemChanged(1)
                adapter.notifyItemChanged(2)
                adapter.notifyItemChanged(3)

            }
        }

        binding.addwidgetbutton.setOnClickListener {
            val animation = AnimUtils.blinkAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    val intent = Intent(applicationContext, CreateWidgetActivity::class.java)
                    intent.putExtra("type", "create")
                    startActivityForResult(intent, 35)
                    overridePendingTransition(R.anim.activity_open, R.anim.activity_stable)
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
            it.startAnimation(animation)
        }

        binding.addwidgetbutton2.setOnClickListener {
            val animation = AnimUtils.blinkAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    val intent = Intent(applicationContext, CreateWidgetActivity::class.java)
                    intent.putExtra("type", "create")
                    startActivityForResult(intent, 35)
                    overridePendingTransition(R.anim.activity_open, R.anim.activity_stable)
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
            it.startAnimation(animation)
        }

        binding.cancelSortButton.setOnClickListener {
            val animation = AnimUtils.blinkAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    handleSort(toSort = false)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(animation)
        }

        binding.cancelSortButton2.setOnClickListener {
            val animation = AnimUtils.blinkAnim(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    handleSort(toSort = false)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
            it.startAnimation(animation)
        }


        val itemTouchHelperCallBack =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    if (viewHolder.itemViewType != target.itemViewType) {
                        return false
                    }

                    val sortedItem = dataList.get(viewHolder.adapterPosition)
                    val movedItem = dataList.get(target.adapterPosition)

                    dataList.set(viewHolder.adapterPosition, movedItem)
                    dataList.set(target.adapterPosition, sortedItem)

                    adapter.notifyItemChanged(viewHolder.adapterPosition)
                    adapter.notifyItemChanged(target.adapterPosition)

                    adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)

                    //Save the updated list
                    CoroutineScope(Dispatchers.IO).launch {
                        saveWidgets()
                    }

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun isLongPressDragEnabled(): Boolean {
                    return false
                }


            }

        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)

//
//        val lan = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//
//        }
//
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "application/x-font-ttf"
//        intent.flags =
//            Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 35) {
            if (resultCode == RESULT_OK) {
                val savedWidgetsJSON = sharedPreferences.getString("savedwidgets", null)
                dataList.clear()
                dataList.addAll(getSavedWidgets(savedWidgetsJSON!!))
                adapter.notifyDataSetChanged()

                if (dataList.isEmpty()) {
                    binding.nowidgetsplaceholder.visibility = View.VISIBLE
                } else {
                    binding.nowidgetsplaceholder.visibility = View.GONE
                }

                AppUtils.showSnackbar(this, "New widget added!", binding.mainparent, isDark).show()
            }
        }

        if (requestCode == 36) {
            if (resultCode == RESULT_OK) {
                val widgetData = data!!.getSerializableExtra("widgetdata") as WidgetData
                widgetEdited(widgetData)
            }
        }

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                binding.recy.adapter = adapter
            }
        }

        if (requestCode == REQUEST_WIDGET_FILE) {
            if (resultCode == RESULT_OK) {
                val uri = data!!.data
                val contentFileName = AppUtils.getContentFileName(this, uri!!)
                if (contentFileName.endsWith(".atw", false)) {
                    loadWidgetFromFile(uri)
                } else {
                    Toast.makeText(this, "Please select a '.atw' file", Toast.LENGTH_SHORT).show()
                }

            }
        }

        if (requestCode == REQUEST_SETTING) {
            if (resultCode == RESULT_OK) {
                adjustTheme(themePreferences.getString("apptheme", AppUtils.LIGHT)!!)

                // Control ads
                setAds()

                //Load interstitial Ad
                loadInterstitalAd()
            }
        }
    }

    override fun widgetDeleted() {
        val savedWidgetsJSON = sharedPreferences.getString("savedwidgets", null)
        dataList.clear()
        dataList.addAll(getSavedWidgets(savedWidgetsJSON!!))
        adapter.notifyDataSetChanged()

        if (dataList.isEmpty()) {
            binding.nowidgetsplaceholder.visibility = View.VISIBLE
        } else {
            binding.nowidgetsplaceholder.visibility = View.GONE
        }

        AppUtils.showSnackbar(this, "Widget deleted", binding.mainparent, isDark).show()

    }

    override fun widgetSavedToDevice(savedUri: Uri) {
        val snackbar = AppUtils.showSnackbar(
            this,
            "Widget saved to device at  'Downloads/Any text widget'",
            binding.mainparent,
            isDark
        )
        snackbar.setAction("Share", object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "*/*"
                intent.putExtra(Intent.EXTRA_STREAM, savedUri)
                startActivity(Intent.createChooser(intent, "Share via"))
            }
        })
        snackbar.show()

        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            if (interstitalAd != null) {
                interstitalAd!!.show(this@MainActivity)
            }
        }
    }

    override fun widgetSavedAsImage(savedUri: Uri) {
        val snackbar = AppUtils.showSnackbar(
            this,
            "Widget saved as image to device at  'Pictures/Any text widget'",
            binding.mainparent,
            isDark
        )
        snackbar.setAction("Share", object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "*/*"
                intent.putExtra(Intent.EXTRA_STREAM, savedUri)
                startActivity(Intent.createChooser(intent, "Share via"))
            }
        })
        snackbar.show()

        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            if (interstitalAd != null) {
                interstitalAd!!.show(this@MainActivity)
            }
        }
    }

    override fun widgetSaveCancelled() {
        AppUtils.showSnackbar(
            this,
            "Unable to save widget to device...",
            binding.mainparent,
            isDark
        ).show()
    }

    override fun startSort(holder: WidgetsAdapter.ViewHolder) {
        itemTouchHelper.startDrag(holder)
    }

    override fun onBackPressed() {
        if (isSorting) {
            handleSort(toSort = false)
        } else {
            super.onBackPressed()
        }
    }

    fun widgetEdited(widgetData: WidgetData) {
        val savedWidgetsJSON = sharedPreferences.getString("savedwidgets", null)
        dataList.clear()
        dataList.addAll(getSavedWidgets(savedWidgetsJSON!!))
        adapter.notifyDataSetChanged()


        //Update all the widgets on the home screen having the current updated widget
        CoroutineScope(Dispatchers.IO).launch {
            updateUIWidget(widgetData)
        }


        AppUtils.showSnackbar(this, "Widget edited", binding.mainparent, isDark).show()

        recreate()

    }

    fun getSavedWidgets(json: String): MutableList<WidgetData> {
        val gson = Gson()
        val type = object : TypeToken<MutableList<WidgetData>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getSavedUIWidgets(json: String): MutableList<WidgetUIData> {
        val gson = Gson()
        val type = object : TypeToken<MutableList<WidgetUIData>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun saveEditedUIWidget(editedUIList: List<WidgetUIData>) {
        val sharedPreferences = getSharedPreferences("widgetspref", MODE_PRIVATE)

        val uiList = ArrayList<WidgetUIData>()

        //Add all UI widgets and save to shared preferences
        uiList.addAll(editedUIList)
        val gson = Gson()
        val savingJSON = gson.toJson(uiList)
        sharedPreferences.edit().putString("saveduiwidgets", savingJSON).apply()
    }

    suspend fun saveAllWidgets() {
        var count = 0
        var saveInterface: AppUtils.WidgetSaveInterface? = null

        AppUtils.showSnackbar(
            this@MainActivity,
            "Saving your widgets, please wait...",
            binding.mainparent,
            isDark
        ).show()

        saveInterface = object : AppUtils.WidgetSaveInterface {
            override fun widgetSaved(savedPath: String) {
                count++
                AppUtils.showSnackbar(
                    this@MainActivity,
                    "Saved $count of ${dataList.size} your widgets...",
                    binding.mainparent,
                    isDark
                ).show()

                if (count != dataList.size) {
                    CoroutineScope(Dispatchers.IO).launch {
                        AppUtils.saveWidgetToDevice(
                            this@MainActivity,
                            dataList[count],
                            saveInterface!!
                        )
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(3000)
                        AppUtils.showSnackbar(
                            this@MainActivity,
                            "Saved all your widgets at 'Downloads/Any text widget'",
                            binding.mainparent,
                            isDark
                        ).show()

                        delay(1000)
                        if (interstitalAd != null) {
                            interstitalAd!!.show(this@MainActivity)
                        }
                    }
                }

            }

            override fun widgetSaveFailed() {
                AppUtils.showSnackbar(
                    this@MainActivity,
                    "Unable to save your widgets to device...",
                    binding.mainparent,
                    isDark
                ).show()
            }

        }

        AppUtils.saveWidgetToDevice(this, dataList[0], saveInterface)
    }

    suspend fun saveWidgets() {
        val sharedPreferences = getSharedPreferences("widgetspref", MODE_PRIVATE)

        val gson = Gson()

        val json = gson.toJson(dataList)

        sharedPreferences.edit().putString("savedwidgets", json).apply()

    }


    suspend fun updateUIWidget(widgetData: WidgetData) {
        //Get the saved UI widgets
        val sharedPreferences = getSharedPreferences("widgetspref", MODE_PRIVATE)
        val uiList = ArrayList<WidgetUIData>()
        val savedUIWidgetsJSON = sharedPreferences.getString("saveduiwidgets", null)
        if (savedUIWidgetsJSON != null) {
            val savedUIWidgets = getSavedUIWidgets(savedUIWidgetsJSON)
            uiList.addAll(savedUIWidgets)
        }

        //Loop through the uiList and save and update all the ui widgets containing the updated widget
        for (uiData in uiList) {
            if (uiData.widgetData != null) {
                if (uiData.widgetData!!.widgetID == widgetData.widgetID) {
                    uiData.widgetData = widgetData

                    saveEditedUIWidget(uiList)

                    AppUtils.updateSingleUIWidget(applicationContext, uiData.widgetUIID)
                }
            }

        }
    }

    override fun widgetCloned() {
        val savedWidgetsJSON = sharedPreferences.getString("savedwidgets", null)
        dataList.clear()
        dataList.addAll(getSavedWidgets(savedWidgetsJSON!!))
        adapter.notifyDataSetChanged()

        AppUtils.showSnackbar(this, "Widget cloned!", binding.mainparent, isDark).show()

        if (dataList.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(300)
                binding.recy.smoothScrollToPosition(dataList.size - 1)
            }
        }

    }

    fun askPermission() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission required!")
        builder.setMessage("Storage permission is required for viewing widgets with image background")
        builder.setPositiveButton("GIVE", DialogInterface.OnClickListener { dialogInterface, i ->
//            ActivityCompat.requestPermissions(
//                this, arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ), 100
//            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.ACCESS_MEDIA_LOCATION
                    ), 100
                )
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 100
                )
            }

        })

        builder.show()
    }

    fun askPermission2() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission required!")
        builder.setMessage("Storage permission is required for loading widgets from your device")
        builder.setPositiveButton("GIVE", DialogInterface.OnClickListener { dialogInterface, i ->
//            ActivityCompat.requestPermissions(
//                this, arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ), 103
//            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.ACCESS_MEDIA_LOCATION
                    ), 103
                )
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 103
                )
            }
        })

        builder.show()
    }

    fun askStoragePermission(): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Storage permission required")
        builder.setMessage("storage permission is required for saving widgets to your device")
        return builder
    }

    fun loadWidgetFromFile(uri: Uri) {
        try {
            //Get the widget data from the file uri
            val objectInputStream = ObjectInputStream(contentResolver.openInputStream(uri))
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


            val addWidgetDialogInterface = object : AddWidgetDialog.AddWidgetDialogInterface {
                override fun newWidgetAdded() {
                    //Update the widgets list
                    val savedWidgetsJSON = sharedPreferences.getString("savedwidgets", null)
                    dataList.clear()
                    dataList.addAll(getSavedWidgets(savedWidgetsJSON!!))
                    adapter.notifyDataSetChanged()

                    if (dataList.isEmpty()) {
                        binding.nowidgetsplaceholder.visibility = View.VISIBLE
                    } else {
                        binding.nowidgetsplaceholder.visibility = View.GONE
                    }


                    AppUtils.showSnackbar(
                        this@MainActivity,
                        "New widget added!",
                        binding.mainparent,
                        isDark
                    ).show()
                }
            }
            val addWidgetDialog = AddWidgetDialog(appWidgetData, dataList, addWidgetDialogInterface)
            addWidgetDialog.show(supportFragmentManager, "addWidgetUseCaseOne")
        } catch (e: StreamCorruptedException) {
            AppUtils.showSnackbar(this, "File corrupted", binding.mainparent, isDark).show()
            e.printStackTrace()
        } catch (e: IOException) {
            AppUtils.showSnackbar(this, "Unable to load the file...", binding.mainparent, isDark)
                .show()
            e.printStackTrace()
        }
    }

    fun darkMode(isNight: Boolean) {
        isDark = isNight
        if (isNight) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

            binding.mainparent.setBackgroundColor(ContextCompat.getColor(this, R.color.Black))
            binding.mainToolbar.visibility = View.GONE
            binding.mainToolbarDark.visibility = View.VISIBLE


        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

            binding.mainparent.setBackgroundColor(ContextCompat.getColor(this, R.color.LightGrey3))
            binding.mainToolbar.visibility = View.VISIBLE
            binding.mainToolbarDark.visibility = View.GONE

        }
    }

    fun adjustTheme(appTheme: String) {
        if (appTheme == AppUtils.LIGHT) {
            darkMode(false)
        }
        if (appTheme == AppUtils.DARK) {
            darkMode(true)
        }
        if (appTheme == AppUtils.FOLLOW_SYSTEM) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> darkMode(true)

                Configuration.UI_MODE_NIGHT_NO -> darkMode(false)
            }
        }
    }

    fun handleSort(toSort: Boolean) {
        if (toSort) {
            //Remove the options menu
            for (i in 0..3) {
                binding.mainToolbar.menu.getItem(i).isVisible = false
                binding.mainToolbarDark.menu.getItem(i).isVisible = false
                binding.mainToolbarDark.invalidate()
            }

            //Show the cancel sort button
            binding.cancelSortButton.visibility = View.VISIBLE
            binding.cancelSortButton2.visibility = View.VISIBLE

            //Remove the add widget button
            binding.addwidgetbutton.visibility = View.GONE
            binding.addwidgetbutton2.visibility = View.GONE

            //Show the sorting interface
            WidgetsAdapter.isSorting = true
            CoroutineScope(Dispatchers.Main).launch {
                for (i in 0 until dataList.size) {
                    adapter.notifyItemChanged(i)
                }
            }

            isSorting = true

            //Attach item touch helper
            itemTouchHelper.attachToRecyclerView(binding.recy)

            binding.mainToolbarDark.title = "Sort widgets"
            binding.mainToolbar.title = "Sort widgets"

        } else {
            //Show the options menu
            for (i in 0..3) {
                binding.mainToolbar.menu.getItem(i).isVisible = true
                binding.mainToolbarDark.menu.getItem(i).isVisible = true
            }

            //Remove the cancel sort button
            binding.cancelSortButton.visibility = View.GONE
            binding.cancelSortButton2.visibility = View.GONE

            //Show the add widget button
            binding.addwidgetbutton.visibility = View.VISIBLE
            binding.addwidgetbutton2.visibility = View.VISIBLE

            //Remove the sorting interface
            WidgetsAdapter.isSorting = false
            CoroutineScope(Dispatchers.Main).launch {
                for (i in 0 until dataList.size) {
                    adapter.notifyItemChanged(i)
                }
            }

            isSorting = false

            //Remove item touch helper
            itemTouchHelper.attachToRecyclerView(null)

            TransitionManager.beginDelayedTransition(binding.mainToolbarDark)
            TransitionManager.beginDelayedTransition(binding.mainToolbar)

            binding.mainToolbarDark.title = "Your widgets"
            binding.mainToolbar.title = "Your widgets"
        }
    }

    fun askRating() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Like the app?")
        builder.setMessage("Please give us a review if you enjoy using the app")
        builder.setPositiveButton("sure", DialogInterface.OnClickListener { dialogInterface, i ->
            val appurl =
                Uri.parse("https://play.google.com/store/apps/details?id=com.rb.anytextwiget")
            val intent = Intent(Intent.ACTION_VIEW, appurl)
            startActivity(intent)
        })
        builder.setNegativeButton("nope", DialogInterface.OnClickListener { dialogInterface, i ->

        })
        builder.show()


    }

    fun setAds() {
        if (!themePreferences.getBoolean("disableads", false)) {
            MobileAds.initialize(this) {
                val adRequest = AdRequest.Builder().build()
                binding.bannerad1.loadAd(adRequest)

            }


            binding.bannerad1.visibility = View.VISIBLE
        } else {
            binding.bannerad1.visibility = View.GONE
        }
    }

    fun loadInterstitalAd() {
        if (!themePreferences.getBoolean("disableads", false)) {
            val adRequest = AdRequest.Builder().build()

            InterstitialAd.load(
                this,
                getString(R.string.interstitialAdOneID),
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(p0: InterstitialAd) {
                        interstitalAd = p0
                        interstitalAd!!.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                    super.onAdFailedToShowFullScreenContent(p0)
                                }

                                override fun onAdShowedFullScreenContent() {
                                    interstitalAd = null
                                }

                                override fun onAdDismissedFullScreenContent() {
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
    }


    fun textBitmap(font: Int, size: Int, color: Int, text: String): Bitmap {
        val textView = TextView(this)
        textView.setTypeface(ResourcesCompat.getFont(this, R.font.open_sans_semibold))
        try {
            textView.setTypeface(ResourcesCompat.getFont(this, font))

        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
        textView.setText(text)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
        textView.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    R.color.Black
                )
            )
        )

        try {
            textView.setTextColor(ColorStateList.valueOf(color))
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        textView.gravity = Gravity.CENTER
        textView.includeFontPadding = false



        textView.layoutParams = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )



        textView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        textView.layout(0, 0, textView.measuredWidth, textView.measuredHeight)
        val bitmap = Bitmap.createBitmap(textView.width, textView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        textView.draw(canvas)
        return bitmap
    }

    fun createWidgetBitmapList(): MutableList<Bitmap> {
        val bitmaps = ArrayList<Bitmap>()
        dataList.forEachIndexed { index, widgetData ->


            val view = binding.recy.getChildAt(index)



            val bitmap =
                Bitmap.createBitmap(
                    if (view.width.rem(2) == 0) {
                        view.width
                    } else {
                        view.width + 1
                    },
                    if (view.height.rem(2) == 0) {
                        view.height
                    } else {
                        view.height + 1
                    },
                    Bitmap.Config.ARGB_8888
                )

            val canvas = Canvas(bitmap)

            view.draw(canvas)

            bitmaps.add(bitmap)



        }
        return bitmaps
    }

    fun makeWidgetVideoLoop() {

        val snackbar =
            AppUtils.showSnackbar(this, "Looping your widgets, please wait", binding.recy, isDark)
        snackbar.duration = Snackbar.LENGTH_INDEFINITE
        snackbar.show()

        CoroutineScope(Dispatchers.IO).launch {

            val fileName = AppUtils.uniqueContentNameGenerator("Widget_Loop")
            AppUtils.saveWidgetAsMp4(
                this@MainActivity,
                fileName,
                createWidgetBitmapList(),
                "No Audio",
                object : AppUtils.MediaListener {
                    override fun onMediaSaved(savedPath: String) {


                        CoroutineScope(Dispatchers.Main).launch {
                            val sb = AppUtils.showSnackbar(
                                this@MainActivity,
                                "Widget loop saved at Movies/Any Text Widget",
                                binding.recy,
                                isDark
                            )
                            sb.setAction("open") {
                                val fileUri = FileProvider.getUriForFile(
                                    this@MainActivity,
                                    BuildConfig.APPLICATION_ID + ".provider", File(savedPath)
                                )
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setDataAndType(fileUri, "video/*")
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                startActivity(intent)
                            }
                            sb.duration = Snackbar.LENGTH_INDEFINITE
                            sb.show()

                            val adRequest = AdRequest.Builder().build()

                            InterstitialAd.load(
                                this@MainActivity,
                                getString(R.string.interstital2),
                                adRequest,
                                object : InterstitialAdLoadCallback() {
                                    override fun onAdLoaded(p0: InterstitialAd) {
                                        p0.show(this@MainActivity)
                                        p0.fullScreenContentCallback =
                                            object : FullScreenContentCallback() {
                                                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                                    super.onAdFailedToShowFullScreenContent(p0)
                                                }

                                                override fun onAdShowedFullScreenContent() {

                                                }

                                                override fun onAdDismissedFullScreenContent() {
                                                    Toast.makeText(
                                                        this@MainActivity,
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
                    }

                    override fun onMediaSaveProgress(progress: Int) {
                    }

                    override fun onMediaSaveFailed(reason: String) {
                        AppUtils.showSnackbar(
                            this@MainActivity,
                            "Unable to loop your widgets, try again later...",
                            binding.recy,
                            isDark
                        ).show()
                    }

                })
        }
    }

}