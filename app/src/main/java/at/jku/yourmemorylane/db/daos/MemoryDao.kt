package at.jku.yourmemorylane.db.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import at.jku.yourmemorylane.db.entities.Memory

@Dao
interface MemoryDao {
    @Query("select * from memory")
    fun getAll(): LiveData<List<Memory>>

    @Query("select * from memory where id = :id")
    fun getById(id: Long): LiveData<Memory>

    @Query("select * from memory where title like :title")
    fun getAllByTitle(title: String): LiveData<List<Memory>>
    @Insert
    fun insert(memory: Memory): Long

    @Update
    fun update(memory: Memory): Int

    @Delete
    fun delete(memory: Memory): Int
    @Query("select * from memory where id IN (:ids)")
    fun getByIds(ids: List<Long>): List<Memory>
}