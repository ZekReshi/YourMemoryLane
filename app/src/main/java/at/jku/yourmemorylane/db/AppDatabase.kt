package at.jku.yourmemorylane.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import at.jku.yourmemorylane.db.daos.MediaDao
import at.jku.yourmemorylane.db.daos.MemoryDao
import at.jku.yourmemorylane.db.entities.Media
import at.jku.yourmemorylane.db.entities.Memory
import kotlinx.coroutines.coroutineScope


@Database(entities = [Memory::class, Media::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    abstract fun mediaDao(): MediaDao

    companion object {
        private lateinit var instance: AppDatabase

        fun getInstance(context: Context): AppDatabase {
            if (!this::instance.isInitialized) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "memories-db"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance
        }
    }
}