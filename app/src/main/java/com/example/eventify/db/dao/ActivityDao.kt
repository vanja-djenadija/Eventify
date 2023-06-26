package com.example.eventify.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.eventify.db.model.Activity
import com.example.eventify.util.Constants

@Dao
interface ActivityDao {
    @Insert
    fun insert(activity: Activity)

    @Update
    fun update(activity: Activity)

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ACTIVITY + " ORDER BY time ASC")
    fun getAllActivities(): List<Activity>

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ACTIVITY + " WHERE name LIKE '%' || :nameFilter || '%' ORDER BY time ASC")
    fun getActivitiesByName(nameFilter: String): List<Activity>
}
