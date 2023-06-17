package com.example.eventify.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.eventify.util.Constants

@Entity(
    tableName = Constants.TABLE_NAME_IMAGE,
    foreignKeys = [ForeignKey(
        entity = Activity::class,
        parentColumns = ["id"],
        childColumns = ["activity_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Image(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    @ColumnInfo(name = "activity_id", index = true) val activityId: Long
)
