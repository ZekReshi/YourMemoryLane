package at.jku.yourmemorylane.db

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import at.jku.yourmemorylane.db.entities.Type
import java.util.Date

class Converters {

    @TypeConverter
    fun toType(value: String) = enumValueOf<Type>(value)

    @TypeConverter
    fun fromType(value: Type) = value.name

    @TypeConverter
    fun toDate(value: Long): Date = Date(value)

    @TypeConverter
    fun fromDate(value: Date): Long = value.time

}