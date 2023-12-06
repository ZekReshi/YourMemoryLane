package at.jku.yourmemorylane.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.db.daos.MediaDao
import at.jku.yourmemorylane.db.daos.MemoryDao
import at.jku.yourmemorylane.db.entities.Media
import at.jku.yourmemorylane.db.entities.Memory

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val memoryDao: MemoryDao
    private val mediaDao: MediaDao
    private val memories: LiveData<List<Memory>>

    fun getMemories(): LiveData<List<Memory>> {
        return memories
    }

    fun getImagesByMemoryId(id: Int): LiveData<List<Media>> {
        return mediaDao.getMediaByMemoryIdAndType(id,"image")
    }

    init {
        memoryDao = AppDatabase.getInstance(application).memoryDao()
        mediaDao = AppDatabase.getInstance(application).mediaDao()
        memories = memoryDao.getAll()
    }

}