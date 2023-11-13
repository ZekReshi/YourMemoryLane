package at.jku.yourmemorylane.ui.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.db.daos.MediaDao
import at.jku.yourmemorylane.db.daos.MemoryDao
import at.jku.yourmemorylane.db.entities.Memory

class EditViewModel(application: Application) : AndroidViewModel(application) {

    private val memoryDao: MemoryDao = AppDatabase.getInstance(application).memoryDao()
    private val mediaDao: MediaDao = AppDatabase.getInstance(application).mediaDao()

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }

    var memory = MutableLiveData<Memory>()

    init {
    }

    companion object {
        private val TAG: String = EditViewModel::class.java.simpleName
    }

}
