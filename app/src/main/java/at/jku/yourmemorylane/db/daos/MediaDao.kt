package at.jku.yourmemorylane.db.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import at.jku.yourmemorylane.db.entities.Media
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("select * from media")
    fun getAll(): List<Media>

    @Insert
    fun insert(media: Media): Long

    @Delete
    fun delete(media: Media): Int

    @Delete
    fun deleteAll(media: List<Media>): Int

    @Query("select * from media where memoryId = :memoryId")
    fun getAllByMemoryId(memoryId: Long): LiveData<List<Media>>

    @Query("select * from media where memoryId = :memoryId and type LIKE '%' ||:type || '%'")
    fun getMediaByMemoryIdAndType(memoryId: Long, type:String): LiveData<List<Media>>

}