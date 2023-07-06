package org.unibl.etf.mr.eventify

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import org.unibl.etf.mr.eventify.R
import org.unibl.etf.mr.eventify.databinding.ActivityMainBinding
import org.unibl.etf.mr.eventify.db.EventifyDatabase
import org.unibl.etf.mr.eventify.db.model.Activity
import org.unibl.etf.mr.eventify.db.model.Category
import org.unibl.etf.mr.eventify.util.NotificationHelper
import org.unibl.etf.mr.eventify.util.NotificationOption
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 111
    private lateinit var binding: ActivityMainBinding
    private lateinit var eventifyDatabase: EventifyDatabase
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_calendar, R.id.navigation_home, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        eventifyDatabase = EventifyDatabase.getInstance(applicationContext)
        populateDatabase()

        // Language
        setLocale(baseContext)

        // Notifications
        notificationHelper = NotificationHelper(this)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val option: NotificationOption = when (sharedPreferences.getString("notification", "2")) {
            "0" -> NotificationOption.OFF
            "1" -> NotificationOption.HOUR_BEFORE
            "2" -> NotificationOption.DAY_BEFORE
            "3" -> NotificationOption.WEEK_BEFORE
            else -> NotificationOption.OFF // Set a default value for invalid preferences
        }
        if (option !== NotificationOption.OFF) {
            if (hasNotificationPermission()) {
                sendNotification(option)
            } else {
                requestNotificationPermission()
            }
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                val optionString = sharedPreferences.getString(
                    resources.getString(R.string.notification_preference_key),
                    resources.getString(R.string.notification_preference_default)
                )
                val option: NotificationOption =
                    NotificationOption.valueOf(optionString!!.uppercase())
                if (option !== NotificationOption.OFF) {
                    sendNotification(option)
                }
            } else {
                // Permission denied, handle accordingly
            }
        }
    }

    private fun sendNotification(option: NotificationOption) {
        val upcomingActivities = getUpcomingActivities(option)
        for (activity in upcomingActivities) {
            val title = activity.name
            val message = activity.time
            notificationHelper.showNotification(title, message.toString())
        }
    }

    private fun populateDatabase() {
        val categoryDao = eventifyDatabase.getCategoryDao()
        CoroutineScope(Dispatchers.IO).launch {
            if (eventifyDatabase.getCategoryDao().getAllCategories().isEmpty()) {
                categoryDao.insert(Category(1, "Work"))
                categoryDao.insert(Category(2, "Travel"))
                categoryDao.insert(Category(3, "Leisure"))
            }
        }
    }

    private fun setLocale(context: Context) {
        val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        val language = sharedPreferences.getString(
            "language",
            context.resources.getString(R.string.language_preference_default)
        )
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config: Configuration = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    override fun attachBaseContext(base: Context) {
        setLocale(base)
        super.attachBaseContext(base)
    }

    private fun getUpcomingActivities(option: NotificationOption): List<Activity> {

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val calendar = Calendar.getInstance()
        val startDate = Calendar.getInstance().time
        when (option) {
            NotificationOption.HOUR_BEFORE -> calendar.add(Calendar.HOUR, 1)
            NotificationOption.DAY_BEFORE -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            NotificationOption.WEEK_BEFORE -> calendar.add(Calendar.DAY_OF_YEAR, 7)
            else -> {}
        }
        val endDate = calendar.time
        val upcomingActivites: List<Activity> =
            eventifyDatabase.getActivityDao().getActivitiesByDateRange(
                format.format(startDate),
                format.format(endDate)
            )
        return upcomingActivites
    }
}