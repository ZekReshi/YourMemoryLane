package at.jku.yourmemorylane.db

import androidx.room.Database
import androidx.room.RoomDatabase
import at.jku.yourmemorylane.db.daos.MemoryDao
import at.jku.yourmemorylane.db.entities.Memory

@Database(entities = [Memory::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
}