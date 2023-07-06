package org.unibl.etf.mr.eventify.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.unibl.etf.mr.eventify.databinding.FragmentCalendarBinding
import org.unibl.etf.mr.eventify.db.EventifyDatabase
import org.unibl.etf.mr.eventify.db.model.Activity
import org.unibl.etf.mr.eventify.ui.ActivityDetailsActivity
import org.unibl.etf.mr.eventify.ui.AddActivity
import org.unibl.etf.mr.eventify.ui.home.ActivityAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private var selectedDate: String =
        SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
    private lateinit var textViewNoActivities: TextView

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this)[CalendarViewModel::class.java]

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* CalendarView */
        val calendarView: CalendarView = binding.calendarView
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            Log.i("ETF", selectedDate)
            refreshActivities(selectedDate)
        }

        /* No activities TextView */
        textViewNoActivities = binding.textViewNoActivities

        /* RecyclerView */
        recyclerView = binding.recyclerViewCalendarActivities
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ActivityAdapter(activities, itemClickListener, requireContext())
        recyclerView.adapter = adapter

        /* Add activity button listener */
        binding.fabAddActivityCalendar.setOnClickListener {
            val intent = Intent(requireContext(), AddActivity::class.java)
            startActivity(intent)
        }


        return root
    }

    override fun onStart() {
        super.onStart()
        refreshActivities(selectedDate)
    }

    private fun refreshActivities(date: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val activityDao = EventifyDatabase.getInstance(requireContext()).getActivityDao()
            activities = activityDao.getAllActivitiesByDate(date) as ArrayList<Activity>
            adapter.updateData(activities)
            // Check if the activity list is empty
            if (activities.isEmpty()) {
                recyclerView.visibility = View.GONE
                textViewNoActivities.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                textViewNoActivities.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}