package org.unibl.etf.mr.eventify.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import org.unibl.etf.mr.eventify.db.model.Activity
import org.unibl.etf.mr.eventify.util.Constants

@Dao
interface ActivityDao {
    @Insert
    fun insert(activity: Activity) : Long

    @Update
    fun update(activity: Activity)

    @Delete
    fun delete(activity: Activity)

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ACTIVITY + " ORDER BY time ASC")
    fun getAllActivities(): List<Activity>

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ACTIVITY + " WHERE name LIKE '%' || :nameFilter || '%' ORDER BY time ASC")
    fun getActivitiesByName(nameFilter: String): List<Activity>

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ACTIVITY + " WHERE time LIKE :date || '%' ORDER BY time ASC")
    fun getAllActivitiesByDate(date: String): List<Activity>

    @Transaction
    @Query("SELECT * FROM activity WHERE time >= :from AND time < :to")
    fun getActivitiesByDateRange(from: String, to: String): List<Activity>

}
