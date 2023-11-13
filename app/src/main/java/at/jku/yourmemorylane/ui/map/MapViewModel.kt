package at.jku.yourmemorylane.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.db.daos.MemoryDao
import at.jku.yourmemorylane.db.entities.Memory

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val memoryDao: MemoryDao
    private val memories: LiveData<List<Memory>>

    fun getMemories(): LiveData<List<Memory>> {
        return memories
    }

    init {
        memoryDao = AppDatabase.getInstance(application).memoryDao()
        memories = memoryDao.getAll()
    }

}