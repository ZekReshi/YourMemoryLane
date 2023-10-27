package at.jku.yourmemorylane.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity
data class Memory(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String,
    //@ColumnInfo(name = "data") val dat: Date,
    @ColumnInfo(name = "longitude") val longitude: Int,
    @ColumnInfo(name = "latitude") val latitude: Int
)