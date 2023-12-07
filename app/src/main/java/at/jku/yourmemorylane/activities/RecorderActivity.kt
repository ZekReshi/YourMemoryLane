package at.jku.yourmemorylane.activities

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Chronometer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import at.jku.yourmemorylane.R
import at.jku.yourmemorylane.databinding.ActivityRecorderBinding
import at.jku.yourmemorylane.db.entities.Media
import at.jku.yourmemorylane.ui.friends.FriendsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class RecorderActivity : AppCompatActivity() {

    private lateinit var viewModel: RecorderViewModel
    private var memoryId: Long =-1
    private lateinit var lastRecorded: String
    private var fileName:String? = null
    private var recordingIsPaused: Boolean = false
    private var mediaRecorder: MediaRecorder? = null;
    private lateinit var mediaPlayer: MediaPlayer;
    private var recordingStopped:Long =0;
    private lateinit var pauseButton: FloatingActionButton
    private lateinit var playButton: FloatingActionButton
    private lateinit var recordButton: FloatingActionButton
    private lateinit var timerDisplay: Chronometer
    private var _binding: ActivityRecorderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRecorderBinding.inflate(layoutInflater)
        memoryId = intent.getLongExtra("memoryId",-1)
        viewModel = ViewModelProvider(this)[RecorderViewModel::class.java]
        setContentView(binding.root)
        val root: View = binding.root

        timerDisplay = binding.timerDisplay
        recordButton = binding.recordButton;
        playButton = binding.playButton
        pauseButton = binding.pauseButton
        pauseButton.isEnabled = false
        playButton.isEnabled =false
        pauseButton.setImageResource(R.drawable.baseline_pause_24)
        recordButton.setOnClickListener{
            recordAudio()
        }
        pauseButton.setOnClickListener{
            pauseRecording();
        }
        playButton.setOnClickListener{
            playRecording()
        }
        playButton.setImageResource(R.drawable.baseline_headphones_24)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),1)
        }
    }

    private fun playRecording() {
        val uri = Uri.parse(lastRecorded)
        mediaPlayer = MediaPlayer.create(this,uri).apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build()
            )
            start()
        }
    }

    private fun pauseRecording() {
        if(mediaRecorder != null){
            recordingIsPaused = if(recordingIsPaused){
                pauseButton.setImageResource(R.drawable.baseline_pause_24)
                mediaRecorder!!.resume()
                timerDisplay.base =SystemClock.elapsedRealtime() + recordingStopped;
                timerDisplay.start()
                false;
            } else {
                pauseButton.setImageResource(R.drawable.baseline_play_arrow)
                mediaRecorder!!.pause()
                recordingStopped = timerDisplay.base -SystemClock.elapsedRealtime()
                timerDisplay.stop()
                true;
            }

        }
    }

    private fun recordAudio() {
        if(mediaRecorder != null){
            mediaRecorder!!.stop();
            mediaRecorder!!.reset();
            mediaRecorder!!.release();
            viewModel.insert(Media(memoryId=memoryId,"audio/m4a", "file://$fileName"))
            lastRecorded = fileName!!
            mediaRecorder = null;

            timerDisplay.stop()

            recordButton.setImageResource(R.drawable.baseline_mic_)
            pauseButton.setImageResource(R.drawable.baseline_pause_24)
            playButton.isEnabled =true
            pauseButton.isEnabled =false
        }
        else {
            timerDisplay.base = SystemClock.elapsedRealtime()
            recordingStopped = 0
            timerDisplay.start()
            playButton.isEnabled=false
            pauseButton.isEnabled=true
            pauseButton.setImageResource(R.drawable.baseline_pause_24)
            recordButton.setImageResource(R.drawable.baseline_record)
            mediaRecorder =
                MediaRecorder(this)
            mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
            mediaRecorder!!.setAudioSamplingRate(RECORDER_SAMPLE_RATE)



            val filepath = Environment.getExternalStorageDirectory().path
            val file = File(filepath, "Recordings/YourMemoryLane")

            if (!file.exists()) {
                file.mkdirs()
            }
            fileName = file.absolutePath+ File.separator+ SimpleDateFormat("dd_MM_yyyy_hh_mm", Locale.GERMANY)
                .format(System.currentTimeMillis())+".m4a"
            Log.i(ContentValues.TAG, fileName!!)



            mediaRecorder!!.setOutputFile(fileName)
            try {
                mediaRecorder!!.prepare()
                Log.i(ContentValues.TAG,"started recording")
            }
            catch (e: IllegalStateException) {
                // handle error
                e.message?.let { Log.i(ContentValues.TAG, it,e) }
            }
            catch (e: Exception) {
                // handle error
                e.message?.let { Log.i(ContentValues.TAG, it,e) }}
            mediaRecorder!!.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val RECORDER_SAMPLE_RATE = 44100
    }

}