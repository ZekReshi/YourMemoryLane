package at.jku.yourmemorylane.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.db.daos.MemoryDao
import at.jku.yourmemorylane.db.entities.Memory


class ListViewModel(application: Application) : AndroidViewModel(application) {
    private val memoryDao: MemoryDao
    private val memories: LiveData<List<Memory>>

    fun getMemories(): LiveData<List<Memory>> {
        return memories
    }

    init {
        memoryDao = AppDatabase.getInstance(application).memoryDao()
        for (i in 1..10) {
            memoryDao.insert(Memory(0, "Memory $i", "24.12.2023", 0, 0))
        }
        memories = memoryDao.getAll()
    }

    fun insert(memory: Memory) {
        memoryDao.insert(memory)
    }

    fun update(memory: Memory) {
        memoryDao.update(memory)
    }

    fun delete(memory: Memory) {
        memoryDao.delete(memory)
    }

}