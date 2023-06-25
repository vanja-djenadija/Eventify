package com.example.eventify

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.eventify.databinding.ActivityMainBinding
import com.example.eventify.db.EventifyDatabase
import com.example.eventify.db.model.Category
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var eventifyDatabase: EventifyDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
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
}