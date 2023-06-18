package com.example.eventify.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.R
import com.example.eventify.databinding.FragmentHomeBinding
import com.example.eventify.db.model.Activity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivityAdapter
    private val activities: ArrayList<Activity> = ArrayList()

    private val itemClickListener = object : ActivityAdapter.OnItemClickListener {
        override fun onItemClick(activity: Activity) {
            Toast.makeText(
                requireContext(), "Clicked on: ${activity.name}", Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* RecyclerView */
        if (activities.isEmpty()) {
            for (i in 0 until 30) {
                val item = Activity(0, "Odazak u nacionalni klub", "Opis", "Lokacija", "Datum", 0)
                activities.add(item)
            }
        }

        recyclerView = binding.recyclerViewActivities
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ActivityAdapter(activities, itemClickListener)
        recyclerView.adapter = adapter
        /* RecyclerView */

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}