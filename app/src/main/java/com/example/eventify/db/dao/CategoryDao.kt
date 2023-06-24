package com.example.eventify.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.eventify.db.model.Category
import com.example.eventify.util.Constants


@Dao
interface CategoryDao {
    @Insert
     fun insert(category: Category)

    @Query("SELECT * FROM " + Constants.TABLE_NAME_CATEGORY)
    fun getAllCategories(): List<Category>
    @Query("DELETE FROM " + Constants.TABLE_NAME_CATEGORY)
    fun deleteAll()
    // Add other necessary queries as needed
}
