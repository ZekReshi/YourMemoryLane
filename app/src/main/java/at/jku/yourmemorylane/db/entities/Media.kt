package at.jku.yourmemorylane.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Media(
    @ColumnInfo(name = "memoryId") val memoryId: Long,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "path") val path: String
){
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
