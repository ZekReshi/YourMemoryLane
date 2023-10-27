package at.jku.yourmemorylane.db.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import at.jku.yourmemorylane.db.entities.Memory

@Dao
interface MediaDao {
    @Query("select * from media")
    fun getAll(): List<Memory>

    @Query("select * from media where memoryId = :memoryId")
    fun getAllByMemoryId(memoryId: Int)

    @Insert
    fun insert(memory: Memory)

    @Delete
    fun delete(memory: Memory)
}