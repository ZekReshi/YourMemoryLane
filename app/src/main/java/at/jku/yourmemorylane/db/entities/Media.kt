package at.jku.yourmemorylane.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Media(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "memoryId") val memoryId: Int,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "path") val path: String
)
