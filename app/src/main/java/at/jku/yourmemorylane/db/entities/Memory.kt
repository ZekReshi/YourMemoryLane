package at.jku.yourmemorylane.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Memory(
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "date") var date: Date,
    @ColumnInfo(name = "longitude") var longitude: Double,
    @ColumnInfo(name = "latitude") var latitude: Double
){
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}