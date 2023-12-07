package at.jku.yourmemorylane.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.db.daos.MediaDao
import at.jku.yourmemorylane.db.daos.MemoryDao
import at.jku.yourmemorylane.db.entities.Media
import at.jku.yourmemorylane.db.entities.Memory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val memoryDao: MemoryDao
    private val mediaDao: MediaDao
    private val memories: LiveData<List<Memory>>

    fun getMemories(): LiveData<List<Memory>> {
        return memories
    }

    fun getImagesByMemoryId(id: Long): LiveData<List<Media>> {
        return mediaDao.getMediaByMemoryIdAndType(id,"image")
    }

    fun insertMemory(long: Double, lat: Double): Memory {
        val memory = Memory("New Memory", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), long, lat)
        memory.id = memoryDao.insert(Memory("New Memory", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), long, lat))
        return memory
    }

    init {
        memoryDao = AppDatabase.getInstance(application).memoryDao()
        mediaDao = AppDatabase.getInstance(application).mediaDao()
        memories = memoryDao.getAll()
    }

}