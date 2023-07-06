package org.unibl.etf.mr.eventify.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.unibl.etf.mr.eventify.db.dao.ActivityDao
import org.unibl.etf.mr.eventify.db.dao.CategoryDao
import org.unibl.etf.mr.eventify.db.dao.ImageDao
import org.unibl.etf.mr.eventify.db.model.Activity
import org.unibl.etf.mr.eventify.db.model.Category
import org.unibl.etf.mr.eventify.db.model.Image
import org.unibl.etf.mr.eventify.util.Constants
import org.unibl.etf.mr.eventify.util.DateRoomConverter

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
