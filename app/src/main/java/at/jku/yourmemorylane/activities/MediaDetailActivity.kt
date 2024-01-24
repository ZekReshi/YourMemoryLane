package at.jku.yourmemorylane.activities

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import at.jku.yourmemorylane.R
import at.jku.yourmemorylane.databinding.ActivityAudioDetailBinding
import at.jku.yourmemorylane.databinding.ActivityImageDetailBinding
import at.jku.yourmemorylane.databinding.ActivityTextDetailBinding
import at.jku.yourmemorylane.databinding.ActivityVideoDetailBinding
import at.jku.yourmemorylane.db.entities.Media
import at.jku.yourmemorylane.db.entities.Type
import at.jku.yourmemorylane.viewmodels.MediaDetailViewModel
import com.bumptech.glide.Glide
import kotlin.math.ceil

class MediaDetailActivity : AppCompatActivity() {

    private lateinit var media: Media;
    private var mediaController: MediaController? = null
    private var mediaPlayer: MediaPlayer? = null
    private var playing = false

    override fun onDestroy() {
        super.onDestroy()
        when (media.type) {
            Type.IMAGE -> {
            }
            Type.VIDEO -> {
                if (mediaController != null)
                    mediaController = null
            }
            Type.TEXT -> {
            }
            Type.AUDIO -> {
                if (mediaPlayer != null)
                    mediaPlayer?.stop()
                    mediaPlayer = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mediaDetailViewModel = ViewModelProvider(this)[MediaDetailViewModel::class.java]

        val id = intent.getLongExtra(EXTRA_ID, -1)

        mediaDetailViewModel.initMedia(id)
        mediaDetailViewModel.getMedia().observe(this) {
            if (it == null) {
                return@observe
            }
            media = it
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

                    mediaController = MediaController(this)
                    mediaController!!.setAnchorView(binding.vvVideoDetail)
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
                Type.AUDIO -> {
                    val binding = ActivityAudioDetailBinding.inflate(layoutInflater)
                    setContentView(binding.root)

                    mediaPlayer = MediaPlayer.create(this, media.path.toUri()).apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                                .build()
                        )
                        setOnCompletionListener {
                            binding.fabPlayDetail.performClick()
                            binding.chronTimerDetail.base = SystemClock.elapsedRealtime()
                        }
                        var minutes = (duration / 60000).toString()
                        if (minutes.length == 1) {
                            minutes = "0$minutes"
                        }
                        var seconds = ceil((duration / 1000f) % 60).toInt().toString()
                        if (seconds.length == 1) {
                            seconds = "0$seconds"
                        }
                        binding.chronTimerDetail.format = "%s/$minutes:$seconds"
                        binding.chronTimerDetail.base = SystemClock.elapsedRealtime()
                    }

                    binding.fabPlayDetail.setOnClickListener {
                        if (playing) {
                            mediaPlayer?.pause()
                            binding.chronTimerDetail.stop()
                            binding.fabPlayDetail.setImageResource(R.drawable.baseline_play_arrow)
                        }
                        else {
                            mediaPlayer?.start()
                            binding.chronTimerDetail.start()
                            binding.fabPlayDetail.setImageResource(R.drawable.baseline_pause_24)
                        }
                        playing = !playing
                    }

                    binding.fabRewindDetail.setOnClickListener {
                        mediaPlayer?.seekTo(0)
                        binding.chronTimerDetail.base = SystemClock.elapsedRealtime()
                    }

                    binding.fabDeleteAudio.setOnClickListener {
                        mediaDetailViewModel.delete()

                        finish()
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_ID = "at.jku.yourmemorylane.EXTRA_ID"
    }

}