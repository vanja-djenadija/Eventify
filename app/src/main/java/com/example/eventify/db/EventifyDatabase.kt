package com.example.eventify.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.eventify.db.dao.ActivityDao
import com.example.eventify.db.dao.CategoryDao
import com.example.eventify.db.dao.ImageDao
import com.example.eventify.db.model.Activity
import com.example.eventify.db.model.Category
import com.example.eventify.db.model.Image
import com.example.eventify.util.Constants
import com.example.eventify.util.DateRoomConverter

@Database(entities = [Activity::class, Category::class, Image::class], version = 4)
@TypeConverters(DateRoomConverter::class)
abstract class EventifyDatabase : RoomDatabase() {

    abstract fun getActivityDao(): ActivityDao

    abstract fun getCategoryDao(): CategoryDao

    abstract fun getImageDao(): ImageDao

    companion object {
        @Volatile
        private var eventifyDB: EventifyDatabase? = null

        fun getInstance(context: Context): EventifyDatabase {
            return eventifyDB ?: synchronized(this) {
                val instance = buildDatabaseInstance(context)
                eventifyDB = instance
                instance
            }
        }

        private fun buildDatabaseInstance(context: Context): EventifyDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                EventifyDatabase::class.java,
                Constants.DB_NAME
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    fun cleanUp() {
        eventifyDB = null
    }
}
