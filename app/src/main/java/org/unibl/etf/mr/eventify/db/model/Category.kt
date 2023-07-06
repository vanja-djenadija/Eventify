package org.unibl.etf.mr.eventify.db.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import org.unibl.etf.mr.eventify.util.Constants

@Entity(tableName = Constants.TABLE_NAME_CATEGORY)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)
