package org.unibl.etf.mr.eventify.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.unibl.etf.mr.eventify.util.Constants
import java.io.Serializable

@Entity(
    tableName = Constants.TABLE_NAME_ACTIVITY,
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.NO_ACTION // Watch out!
    )]
)
data class Activity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "category_id") val categoryId: Long
) : Serializable {
    companion object {
        fun createActivity(
            name: String,
            description: String,
            location: String,
            time: String,
            categoryId: Long
        ): Activity {
            return Activity(0, name, description, location, time, categoryId)
        }
    }
}
