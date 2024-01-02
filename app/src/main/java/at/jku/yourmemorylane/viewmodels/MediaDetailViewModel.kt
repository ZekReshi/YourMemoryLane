package at.jku.yourmemorylane.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.db.daos.MediaDao
import at.jku.yourmemorylane.db.entities.Media


class MediaDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val mediaDao: MediaDao
    private lateinit var media: LiveData<Media>

    init {
        mediaDao = AppDatabase.getInstance(application).mediaDao()
    }

    fun initMedia(mediaId: Long) {
        media = mediaDao.getById(mediaId)
    }

    fun getMedia(): LiveData<Media> {
        return media
    }

    fun delete() {
        mediaDao.delete(media.value!!)
    }

}