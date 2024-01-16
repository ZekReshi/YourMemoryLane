package at.jku.yourmemorylane.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.db.daos.MediaDao
import at.jku.yourmemorylane.db.daos.MemoryDao
import at.jku.yourmemorylane.db.entities.Media
import at.jku.yourmemorylane.db.entities.Memory
import at.jku.yourmemorylane.db.entities.Type
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val memoryDao: MemoryDao
    private val mediaDao: MediaDao
    private val memories: LiveData<List<Memory>>

    fun getMemories(): LiveData<List<Memory>> {
        return memories
    }

    fun getImagesByMemoryId(id: Long): LiveData<List<Media>> {
        return mediaDao.getMediaByMemoryIdAndType(id, Type.IMAGE)
    }

    fun insertMemory(long: Double, lat: Double): Memory {
        val memory = Memory("New Memory", Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), long, lat)
        memory.id = memoryDao.insert(memory)
        return memory
    }

    init {
        memoryDao = AppDatabase.getInstance(application).memoryDao()
        mediaDao = AppDatabase.getInstance(application).mediaDao()
        memories = memoryDao.getAll()
    }

}