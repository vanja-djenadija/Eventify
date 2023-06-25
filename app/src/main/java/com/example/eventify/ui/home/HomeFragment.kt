package com.example.eventify.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.AddActivity
import com.example.eventify.databinding.FragmentHomeBinding
import com.example.eventify.db.EventifyDatabase
import com.example.eventify.db.model.Activity
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
            val toast =
                Toast.makeText(
                    requireContext(),
                    "Clicked on: ${activity.name}",
                    Toast.LENGTH_SHORT
                )
            toast.setGravity(Gravity.TOP, 0, 0) // TODO Change position
            toast.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* RecyclerView */
        recyclerView = binding.recyclerViewActivities
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ActivityAdapter(activities, itemClickListener)
        recyclerView.adapter = adapter
        /* RecyclerView */

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

    fun addActivity(view: View) {
        val intent = Intent(requireContext(), AddActivity::class.java)
        startActivity(intent)
    }
}