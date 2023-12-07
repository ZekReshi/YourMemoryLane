package at.jku.yourmemorylane.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.db.daos.MediaDao
import at.jku.yourmemorylane.db.entities.Media

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private val mediaDao: MediaDao

    init {
        mediaDao = AppDatabase.getInstance(application).mediaDao()
    }

    fun insert(media: Media) {
        mediaDao.insert(media)
    }

}