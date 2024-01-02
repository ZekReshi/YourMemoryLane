package at.jku.yourmemorylane.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import at.jku.yourmemorylane.db.Converters

@Entity
data class Media(
    @ColumnInfo(name = "memoryId") val memoryId: Long,
    @ColumnInfo(name = "type") @TypeConverters(Converters::class) val type: Type,
    @ColumnInfo(name = "path") val path: String
){
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
