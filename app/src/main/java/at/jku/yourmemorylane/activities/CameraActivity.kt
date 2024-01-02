package at.jku.yourmemorylane.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Chronometer
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.MirrorMode
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.ExperimentalPersistentRecording
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import at.jku.yourmemorylane.R
import at.jku.yourmemorylane.databinding.ActivityCameraBinding
import at.jku.yourmemorylane.db.entities.Media
import at.jku.yourmemorylane.db.entities.Type
import at.jku.yourmemorylane.viewmodels.CameraViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.common.util.concurrent.ListenableFuture
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutionException


class CameraActivity : AppCompatActivity() {
    private enum class CameraMode {
        Picture,
        Video
    }

    private lateinit var viewModel: CameraViewModel
    private var memoryId: Long = -1
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var useCaseGroup: UseCaseGroup
    private val DOUBLE_CLICK_TIME_DELTA: Long = 300L
    private var lastClickTime: Long = System.currentTimeMillis()
    private var cameraMode: CameraMode = CameraMode.Picture


    private lateinit var binding: ActivityCameraBinding
    private var cameraOrientation = CameraSelector.LENS_FACING_BACK;
    // This property is only valid between onCreateView and
    // onDestroyView.
    private lateinit var previewView: PreviewView;
    private lateinit var flipButton: FloatingActionButton;
    private lateinit var cameraActionButton: FloatingActionButton;
    private lateinit var imageCapture: ImageCapture;
    private lateinit var cameraModeButton: FloatingActionButton;
    private lateinit var videoCapture: VideoCapture<Recorder>;
    private lateinit var timerDisplay: Chronometer
    private var recordingStopped:Long =0
    private var recording: Recording? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryId = intent.getLongExtra("memoryId",-1)
        viewModel = ViewModelProvider(this)[CameraViewModel::class.java]

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCameraProviderListener()
        val root: View = binding.root
        timerDisplay = binding.timerDisplay
        timerDisplay.isVisible = false
        previewView = binding.previewView
        previewView.setOnClickListener {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                switchCamera()
            }
            lastClickTime = clickTime
        }
        flipButton=binding.flip
        flipButton.setOnClickListener {
            switchCamera()
        }
        cameraActionButton=binding.cameraAction
        cameraActionButton.setOnClickListener {
            if(cameraMode == CameraMode.Picture){
                takePicture()

            }
            else{
                startVideo()
            }
        }
        cameraModeButton=binding.cameraMode
        cameraModeButton.setOnClickListener {
            cameraMode = if(cameraMode== CameraMode.Picture){
                timerDisplay.isVisible = true
                CameraMode.Video
            } else{
                timerDisplay.isVisible = false
                CameraMode.Picture
            }
            if(cameraMode == CameraMode.Picture){
                cameraActionButton.setImageResource(R.drawable.baseline_camera)
                cameraModeButton.setImageResource(R.drawable.baseline_video_camera)
            }
            else {
                cameraActionButton.setImageResource(R.drawable.baseline_video_camera)
                cameraModeButton.setImageResource(R.drawable.baseline_camera)
            }


        }
    }

    private fun switchCamera() {
        cameraOrientation = 1 - cameraOrientation

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraOrientation).build()
        if (recording != null)
            recording?.pause();
        cameraProvider.unbindAll()
        val camera: Camera = cameraProvider.bindToLifecycle(this, cameraSelector, useCaseGroup)
        if (recording != null) {
            recording?.resume()
        }
    }

    @OptIn(ExperimentalPersistentRecording::class) @SuppressLint("MissingPermission")
    private fun startVideo() {
        val videoCapture = this.videoCapture ?: return

        val curRecording = recording
        if (curRecording != null) {
            timerDisplay.stop()
            curRecording.stop()
            recording = null
            return
        }
        timerDisplay.base = SystemClock.elapsedRealtime()
        recordingStopped = 0
        timerDisplay.start()
        val name = SimpleDateFormat("dd_MM_yyyy_hh_mmm", Locale.GERMANY)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/YourMemoryLane")
        }
        viewModel.insert(Media(memoryId=memoryId,Type.VIDEO, "file:///storage/emulated/0/Movies/YourMemoryLane/$name.mp4"))

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                // Enable Audio for recording
                asPersistentRecording()
                withAudioEnabled()
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                        cameraActionButton.setImageResource(R.drawable.baseline_record)
                        //flipButton.isEnabled = false
                    }
                    is VideoRecordEvent.Finalize -> {
                        Log.e(ContentValues.TAG, "Finalize  event")
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: ${recordEvent.outputResults.outputUri}"
                            Toast.makeText(this, "Video capture succeeded: ${recordEvent.outputResults.outputUri}", Toast.LENGTH_SHORT).show()

                            Log.d(ContentValues.TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(ContentValues.TAG, "Video capture ends with error: ${recordEvent.error}")
                            Toast.makeText(this, "Video capture ends with error: ${recordEvent.error}", Toast.LENGTH_SHORT).show()

                        }
                        cameraActionButton
                            .setImageResource(
                                R.drawable.baseline_video_camera
                            )
                        //flipButton.isEnabled =true
                    }
                }
            }    }

    private fun takePicture() {
        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat("dd_MM_yyyy_HH_mm", Locale.GERMANY)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/YourMemoryLane")
        }

        val metadata = ImageCapture.Metadata()
        metadata.isReversedHorizontal = cameraOrientation == CameraSelector.LENS_FACING_FRONT

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .setMetadata(metadata)
            .build()
        viewModel.insert(Media(memoryId=memoryId,Type.IMAGE, "file:///storage/emulated/0/Pictures/YourMemoryLane/$name.jpg"))
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(ContentValues.TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(ContentValues.TAG, msg)
                }
            }
        )    }

    @SuppressLint("MissingPermission")
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        setCameraProviderListener()
    }

    private fun setCameraProviderListener() {
        if (ContextCompat.checkSelfPermission(
                this as Context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this as Context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),1)
        }

        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindPreview()
            } catch (e: ExecutionException) {
                // No errors need to be handled for this Future
                // This should never be reached
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }
    private fun bindPreview() {

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraOrientation).build()
        val preview = Preview.Builder().build()
        imageCapture  = ImageCapture.Builder().build()
        val recorder = Recorder.Builder()
            .setQualitySelector(
                QualitySelector.from(
                    Quality.HIGHEST,
                    FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                )
            )
            .build()
        videoCapture = VideoCapture.Builder(recorder)
            .setMirrorMode(MirrorMode.MIRROR_MODE_ON_FRONT_ONLY)
            .build()

        preview.setSurfaceProvider(previewView.surfaceProvider)
        val viewPort = previewView.viewPort
        if (viewPort != null) {
            useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageCapture)
                .addUseCase(videoCapture)
                .setViewPort(viewPort)
                .build()
            cameraProvider.unbindAll()
            val camera: Camera = cameraProvider.bindToLifecycle(this, cameraSelector, useCaseGroup)
        }
    }
}