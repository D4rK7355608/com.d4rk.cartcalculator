package com.d4rk.cartcalculator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.d4rk.cartcalculator.adapters.CartItemAdapter
import com.d4rk.cartcalculator.data.CartItem
import com.d4rk.cartcalculator.databinding.ActivityMainBinding
import com.d4rk.cartcalculator.ui.startup.StartupActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var cartListener: CartListener? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    private val cartItems = mutableListOf<CartItem>()
    private lateinit var cartItemAdapter: CartItemAdapter
    private var total: Double = 0.0
    private lateinit var appUpdateManager: AppUpdateManager
    private val requestUpdateCode = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        cartItemAdapter = CartItemAdapter(cartItems)
        prefs = getSharedPreferences("startup", MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val themeValues = resources.getStringArray(R.array.dark_mode_values)
        when (PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.key_theme), getString(R.string.default_value_theme))) {
            themeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            themeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            themeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            themeValues[3] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        }
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController by lazy {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
            navHostFragment.navController
        }
        val prefs = getSharedPreferences("app_usage", MODE_PRIVATE)
        val lastUsedTimestamp = prefs.getLong("last_used", 0)
        val currentTimestamp = System.currentTimeMillis()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (currentTimestamp - lastUsedTimestamp > 3 * 24 * 60 * 60 * 1000) {
            val channelId = "app_usage_channel"
            val channel = NotificationChannel(channelId, getString(R.string.app_usage_notifications), NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.notification_last_time_used_title))
                .setContentText(getString(R.string.summary_notification_last_time_used))
                .setAutoCancel(true)
            notificationManager.notify(0, builder.build())
        }
        prefs.edit().putLong("last_used", currentTimestamp).apply()
        val appUpdateInfoTask = AppUpdateManagerFactory.create(this).appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                val updateChannelId = "update_channel"
                val updateChannel = NotificationChannel(updateChannelId, getString(R.string.update_notifications), NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(updateChannel)
                val updateBuilder = NotificationCompat.Builder(this, updateChannelId)
                    .setSmallIcon(R.drawable.ic_notification_update)
                    .setContentTitle(getString(R.string.notification_update_title))
                    .setContentText(getString(R.string.summary_notification_update))
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this, 0, Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")), PendingIntent.FLAG_IMMUTABLE))
                notificationManager.notify(0, updateBuilder.build())
            }
        }
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_settings, R.id.nav_about), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.setGraph(R.navigation.mobile_navigation)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.add_to_cart -> {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_to_cart, null)
            MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val textView = findViewById<MaterialTextView>(R.id.text_view_empty)
                    textView.visibility = View.GONE
                    val listView = findViewById<RecyclerView>(R.id.recycler_view_cart)
                    listView.visibility = View.VISIBLE
                    val itemNameEditText = dialogView.findViewById<TextInputEditText>(R.id.edit_text_name)
                    val itemPriceEditText = dialogView.findViewById<TextInputEditText>(R.id.edit_text_price)
                    val itemQuantityEditText = dialogView.findViewById<TextInputEditText>(R.id.edit_text_quantity)
                    val itemName = itemNameEditText.text.toString()
                    val itemQuantity = itemQuantityEditText.text.toString().toIntOrNull() ?: 0
                    val itemPrice = itemPriceEditText.text.toString().toDoubleOrNull()
                    if (itemPrice != null) {
                        val newItem = CartItem(itemName, itemPrice, itemQuantity)
                        cartItems.add(newItem)
                        total += newItem.price
                        cartListener?.onCartUpdated(cartItems)
                    }
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            true
        }
        R.id.refresh -> {
            cartItems.clear()
            total = 0.00
            val textViewTotal = findViewById<MaterialTextView>(R.id.text_view_total)
            textViewTotal.text = getString(R.string.total_default_value)
            updateCartList()
            val listView = findViewById<RecyclerView>(R.id.recycler_view_cart)
            listView.visibility = View.GONE
            val textView = findViewById<MaterialTextView>(R.id.text_view_empty)
            textView.visibility = View.VISIBLE
            supportFragmentManager.popBackStack()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
    interface CartListener {
        fun onCartUpdated(cartItems: List<CartItem>)
    }
    fun setCartListener(listener: CartListener) {
        cartListener = listener
    }
    private fun updateCartList() {
        if (cartItems.isEmpty()) {
            findViewById<MaterialTextView>(R.id.text_view_empty).visibility = View.VISIBLE
        } else {
            findViewById<MaterialTextView>(R.id.text_view_empty).visibility = View.GONE
            cartItemAdapter.notifyItemInserted(cartItems.size - 1)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController by lazy {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
            navHostFragment.navController
        }
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java", ReplaceWith("moveTaskToBack(true)"))
    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.close)
            .setMessage(R.string.summary_close)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                super.onBackPressed()
                moveTaskToBack(true)
            }
            .setNegativeButton(android.R.string.no, null)
            .create()
            .show()
    }
    override fun onResume() {
        super.onResume()
        if (prefs.getBoolean("value", true)) {
            prefs.edit().putBoolean("value", false).apply()
            startActivity(Intent(this, StartupActivity::class.java))
        }
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val preferenceFirebaseKey = getString(R.string.key_firebase)
        val preferenceFirebase = sharedPreferences.getBoolean(preferenceFirebaseKey, true)
        if (!preferenceFirebase) {
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
        } else {
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
        }
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, requestUpdateCode)
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestUpdateCode) {
            when (resultCode) {
                RESULT_OK -> {
                }
                RESULT_CANCELED -> {
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                }
            }
        }
    }
}