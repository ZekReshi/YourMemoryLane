package at.jku.yourmemorylane.ui

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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import at.jku.yourmemorylane.R
import at.jku.yourmemorylane.databinding.ActivityRecorderBinding
import at.jku.yourmemorylane.databinding.FragmentFriendsBinding
import at.jku.yourmemorylane.ui.friends.FriendsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class RecorderActivity : AppCompatActivity() {

    private lateinit var lastRecorded: String
    private var fileName:String? = null
    private var recordingIsPaused: Boolean = false
    private var mediaRecorder: MediaRecorder? = null;
    private lateinit var mediaPlayer: MediaPlayer;

    private lateinit var pauseButton: FloatingActionButton
    private lateinit var playButton: FloatingActionButton
    private lateinit var recordButton: FloatingActionButton
    private lateinit var timerDisplay: TextView
    private var _binding: ActivityRecorderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        val friendsViewModel =
            ViewModelProvider(this).get(FriendsViewModel::class.java)

        _binding = ActivityRecorderBinding.inflate(layoutInflater)
        val root: View = binding.root

        timerDisplay = binding.timerDisplay
        /*friendsViewModel.text.observe(viewLifecycleOwner) {
            timerDisplay.text = it
        }*/
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
                false;
            } else {
                pauseButton.setImageResource(R.drawable.baseline_play_arrow)
                mediaRecorder!!.pause()
                true;
            }

        }
    }

    private fun recordAudio() {
        if(mediaRecorder != null){
            Log.i(ContentValues.TAG,"ending recording")

            mediaRecorder!!.stop();
            mediaRecorder!!.reset();
            mediaRecorder!!.release();
            lastRecorded = fileName!!
            mediaRecorder = null;

            recordButton.setImageResource(R.drawable.baseline_mic_)
            pauseButton.setImageResource(R.drawable.baseline_pause_24)
            playButton.isEnabled =true
            pauseButton.isEnabled =false
        }
        else {
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