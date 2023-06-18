package com.example.eventify

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.databinding.ActivityMainBinding
import com.example.eventify.db.EventifyDatabase
import com.example.eventify.db.model.Activity
import com.example.eventify.db.model.Category
import com.example.eventify.ui.home.ActivityAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var eventifyDatabase: EventifyDatabase
    private var activities: ArrayList<Activity> = ArrayList()

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

        /* ======================================= Rad sa listom aktivnosti =================================*/
        fillList()
        // Step 1: Find the reference to the RecyclerView using its ID and assign it to a variable.
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewActivities)
        // Step 2: Create an instance of the EventAdapter and pass the necessary parameters.
        val itemClickListener = object : ActivityAdapter.OnItemClickListener {
            override fun onItemClick(activity: Activity) {
                Toast.makeText(
                    applicationContext,
                    "Clicked on: ${activity.name}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        val adapter = ActivityAdapter(activities, itemClickListener)

        // Step 3: Set the adapter to the RecyclerView.
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        /*  ======================================= Rad sa listom aktivnosti ================================= */


        eventifyDatabase = EventifyDatabase.getInstance(applicationContext)
        /*eventifyDatabase.getCategoryDao().insert(
            Category(0, "Work")
        )*/
        val categories = eventifyDatabase.getCategoryDao().getAllCategories()


        Log.i("ETF", categories.toString());
    }

    private fun fillList() {
        for (i in 0 until 50) {
            val item = Activity(0, "Naslov ${i + 1}", "Description", "Location", "Date", 0)
            activities.add(item)
        }
    }
}