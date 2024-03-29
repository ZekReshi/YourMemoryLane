package at.jku.yourmemorylane.db.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import at.jku.yourmemorylane.db.entities.Media
import at.jku.yourmemorylane.db.entities.Type
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

    @Update
    fun update(media: Media): Int

    @Query("select * from media where id = :mediaId")
    fun getById(mediaId: Long): LiveData<Media>

    @Query("select * from media where memoryId = :memoryId")
    fun getAllByMemoryId(memoryId: Long): LiveData<List<Media>>

    @Query("select * from media where memoryId = :memoryId and type LIKE '%' ||:type || '%'")
    fun getMediaByMemoryIdAndType(memoryId: Long, type:String): List<Media>

    fun getMediaByMemoryIdAndType(memoryId: Long, type: Type): List<Media> {
        return getMediaByMemoryIdAndType(memoryId, type.name)
    }

    @Query("select * from media where memoryId = :memoryId and type IN ('IMAGE', 'VIDEO')")
    fun getVisualMediaByMemoryId(memoryId: Long): LiveData<List<Media>>

}