package org.unibl.etf.mr.eventify.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.unibl.etf.mr.eventify.db.model.Category
import org.unibl.etf.mr.eventify.util.Constants


@Dao
interface CategoryDao {
    @Insert
    fun insert(category: Category)

    @Query("SELECT * FROM " + Constants.TABLE_NAME_CATEGORY)
     fun getAllCategories(): List<Category>

    @Query("DELETE FROM " + Constants.TABLE_NAME_CATEGORY)
     fun deleteAll()

    @Query("SELECT id FROM category WHERE name = :categoryName")
     fun getCategoryIdByName(categoryName: String): Long

    @Query("SELECT name FROM category WHERE id = :categoryId")
     fun getCategoryNameById(categoryId: Long):String
}
