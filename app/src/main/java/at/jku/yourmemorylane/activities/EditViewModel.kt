package at.jku.yourmemorylane.activities

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.db.daos.MediaDao
import at.jku.yourmemorylane.db.daos.MemoryDao
import at.jku.yourmemorylane.db.entities.Media
import at.jku.yourmemorylane.db.entities.Memory


class EditViewModel(application: Application) : AndroidViewModel(application) {

    private val mediaDao: MediaDao
    private val memoryDao: MemoryDao
    private lateinit var media: LiveData<List<Media>>
    private lateinit var memory: Memory

    init {
        memoryDao = AppDatabase.getInstance(application).memoryDao()
        mediaDao = AppDatabase.getInstance(application).mediaDao()
    }

    fun getMedia(): LiveData<List<Media>> {
        return media;
    }

    fun initMemory(mem: Memory) {
        memory = mem
        media = mediaDao.getAllByMemoryId(mem.id)
    }

    fun getMemory(): Memory {
        return memory
    }

    fun update(memory: Memory) {
        memoryDao.update(memory)
    }

    fun delete(memory: Memory) {
        memoryDao.delete(memory)
    }

    fun insert(media: Media) {
        mediaDao.insert(media)
    }

    fun delete(media: List<Media>) {
        mediaDao.deleteAll(media)
    }

}