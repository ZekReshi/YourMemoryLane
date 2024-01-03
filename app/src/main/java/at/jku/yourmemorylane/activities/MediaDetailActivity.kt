package at.jku.yourmemorylane.activities

import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import at.jku.yourmemorylane.databinding.ActivityImageDetailBinding
import at.jku.yourmemorylane.databinding.ActivityTextDetailBinding
import at.jku.yourmemorylane.databinding.ActivityVideoDetailBinding
import at.jku.yourmemorylane.db.entities.Type
import at.jku.yourmemorylane.viewmodels.MediaDetailViewModel
import com.bumptech.glide.Glide

class MediaDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mediaDetailViewModel = ViewModelProvider(this)[MediaDetailViewModel::class.java]

        val id = intent.getLongExtra(EXTRA_ID, -1)

        mediaDetailViewModel.initMedia(id)
        mediaDetailViewModel.getMedia().observe(this) {
            if (it == null) {
                return@observe
            }
            when (it.type) {
                Type.IMAGE -> {
                    val binding = ActivityImageDetailBinding.inflate(layoutInflater)
                    setContentView(binding.root)

                    Glide.with(applicationContext)
                        .load(it.path.toUri())
                        .into(binding.ivImageDetail)

                    binding.fabDeleteImage.setOnClickListener {
                        mediaDetailViewModel.delete()

                        finish()
                    }
                }
                Type.VIDEO -> {
                    val binding = ActivityVideoDetailBinding.inflate(layoutInflater)
                    setContentView(binding.root)

                    val mediaController = MediaController(this)
                    mediaController.setAnchorView(binding.vvVideoDetail)
                    binding.vvVideoDetail.setMediaController(mediaController)

                    binding.vvVideoDetail.setVideoURI(it.path.toUri())
                    binding.vvVideoDetail.start()

                    binding.fabDeleteVideo.setOnClickListener {
                        mediaDetailViewModel.delete()

                        finish()
                    }
                }
                Type.TEXT -> {
                    val binding = ActivityTextDetailBinding.inflate(layoutInflater)
                    setContentView(binding.root)

                    binding.etTextDetail.setText(it.path)

                    binding.fabDeleteText.setOnClickListener {
                        mediaDetailViewModel.delete()

                        finish()
                    }

                    binding.fabSaveText.setOnClickListener {
                        mediaDetailViewModel.getMedia().value!!.path = binding.etTextDetail.text.toString()
                        mediaDetailViewModel.update()

                        finish()
                    }
                }

                else -> {}
            }
        }
    }

    companion object {
        const val EXTRA_ID = "at.jku.yourmemorylane.EXTRA_ID"
    }

}