package com.example.eventify.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.AddActivity
import com.example.eventify.databinding.FragmentHomeBinding
import com.example.eventify.db.EventifyDatabase
import com.example.eventify.db.model.Activity
import com.example.eventify.ui.ActivityDetailsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivityAdapter
    private var activities: ArrayList<Activity> = ArrayList()

    private val itemClickListener = object : ActivityAdapter.OnItemClickListener {
        override fun onItemClick(activity: Activity) {
            // Create an intent to start the ActivityDetailsActivity
            val intent = Intent(requireContext(), ActivityDetailsActivity::class.java)
            intent.putExtra("activity", activity)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* SearchView */
        val searchView = binding.activitySearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Call a method to perform filtering based on the new text
                filterActivitiesByName(newText)
                return true
            }
        })

        /* RecyclerView */
        recyclerView = binding.recyclerViewActivities
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ActivityAdapter(activities, itemClickListener, requireContext())
        recyclerView.adapter = adapter

        /* Add activity button listener */
        binding.fabAddActivity.setOnClickListener {
            val intent = Intent(requireContext(), AddActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        refreshActivities()
    }

    private fun filterActivitiesByName(query: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val activityDao = EventifyDatabase.getInstance(requireContext()).getActivityDao()
            activities = activityDao.getActivitiesByName(query) as ArrayList<Activity>
            adapter.updateData(activities)
        }
    }

    private fun refreshActivities() {
        CoroutineScope(Dispatchers.Main).launch {
            val activityDao = EventifyDatabase.getInstance(requireContext()).getActivityDao()
            activities = activityDao.getAllActivities() as ArrayList<Activity>
            adapter.updateData(activities)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}