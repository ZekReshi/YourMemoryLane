package at.jku.yourmemorylane.db

import androidx.room.TypeConverter
import at.jku.yourmemorylane.db.entities.Type

class Converters {

    @TypeConverter
    fun toType(value: String) = enumValueOf<Type>(value)

    @TypeConverter
    fun fromType(value: Type) = value.name
}