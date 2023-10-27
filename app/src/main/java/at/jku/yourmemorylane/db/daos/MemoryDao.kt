package at.jku.yourmemorylane.db.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import at.jku.yourmemorylane.db.entities.Memory

@Dao
interface MemoryDao {
    @Query("select * from memory")
    fun getAll(): List<Memory>

    @Query("select * from memory where id = :id")
    fun getAllById(id: Int): List<Memory>

    @Query("select * from memory where title like :title")
    fun getAllByTitle(title: String): List<Memory>

    @Insert
    fun insert(memory: Memory)

    @Delete
    fun delete(memory: Memory)
}