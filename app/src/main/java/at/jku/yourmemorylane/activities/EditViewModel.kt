package at.jku.yourmemorylane.activities

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.db.daos.MediaDao
import at.jku.yourmemorylane.db.entities.Media


class EditViewModel(application: Application) : AndroidViewModel(application) {

    private val mediaDao: MediaDao
    private lateinit var media: LiveData<List<Media>>

    init {
        mediaDao = AppDatabase.getInstance(application).mediaDao()
    }

    fun getMedia(): LiveData<List<Media>> {
        return media;
    }

    fun initMedia(memoryId: Int) {
        media = mediaDao.getAllByMemoryId(memoryId)
    }

    fun insert(media: Media) {
        mediaDao.insert(media)
    }

    fun delete(media: List<Media>) {
        mediaDao.deleteAll(media)
    }

}