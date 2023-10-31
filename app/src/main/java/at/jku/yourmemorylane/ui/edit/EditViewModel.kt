package at.jku.yourmemorylane.ui.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.db.daos.MediaDao
import at.jku.yourmemorylane.db.daos.MemoryDao
import at.jku.yourmemorylane.db.entities.Memory

class EditViewModel(application: Application) : AndroidViewModel(application) {

    private val memoryDao: MemoryDao = AppDatabase.getInstance(application).memoryDao()
    private val mediaDao: MediaDao = AppDatabase.getInstance(application).mediaDao()
    private var memories: LiveData<List<Memory>> = memoryDao.getAll()

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    lateinit var memory: MutableLiveData<Memory>

    companion object {
        private val TAG: String = EditViewModel::class.java.simpleName
    }
}
