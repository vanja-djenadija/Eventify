package org.unibl.etf.mr.eventify.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.unibl.etf.mr.eventify.db.model.Image
import org.unibl.etf.mr.eventify.util.Constants

@Dao
interface ImageDao {
    @Insert
    fun insert(image: Image)

    @Query("SELECT * FROM " + Constants.TABLE_NAME_IMAGE + " WHERE activity_id = :activityId")
    fun getImagesForActivity(activityId: Long): List<Image>

    // Add other necessary queries as needed
}
