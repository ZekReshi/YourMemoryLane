package at.jku.yourmemorylane.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity
data class Memory(
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "longitude") var longitude: Int,
    @ColumnInfo(name = "latitude") var latitude: Int
){
    @PrimaryKey(autoGenerate = true) var id: Int =0
}