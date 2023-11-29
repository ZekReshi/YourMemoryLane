package at.jku.yourmemorylane.db.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import at.jku.yourmemorylane.db.entities.Media

@Dao
interface MediaDao {
    @Query("select * from media")
    fun getAll(): List<Media>

    @Query("select * from media where memoryId = :memoryId")
    fun getAllByMemoryId(memoryId: Int): LiveData<List<Media>>

    @Insert
    fun insert(media: Media)

    @Delete
    fun delete(media: Media)

    @Delete
    fun deleteAll(media: List<Media>)
}