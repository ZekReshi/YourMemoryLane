package at.jku.yourmemorylane.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import at.jku.yourmemorylane.adapters.MediaAdapter
import at.jku.yourmemorylane.databinding.ActivityEditBinding
import at.jku.yourmemorylane.db.entities.Media
import at.jku.yourmemorylane.db.entities.Memory
import at.jku.yourmemorylane.db.entities.Type
import at.jku.yourmemorylane.viewmodels.EditViewModel
import java.util.Calendar


class EditActivity : AppCompatActivity() {

    private lateinit var mediaDetailActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityEditBinding
    private lateinit var editViewModel: EditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaDetailActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

        editViewModel = ViewModelProvider(this)[EditViewModel::class.java]

        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.mediaRecyclerView
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager

        val mediaAdapter = MediaAdapter()
        mediaAdapter.setOnItemClickListener(object : MediaAdapter.OnItemClickListener {
            override fun onItemClick(media: Media) {
                val intent = Intent(applicationContext, MediaDetailActivity::class.java)
                intent.putExtra(MediaDetailActivity.EXTRA_ID, media.id)

                mediaDetailActivityLauncher.launch(intent)
            }
        })
        recyclerView.adapter = mediaAdapter

        binding.fabEdit.setOnClickListener {
            binding.fabGallery.show()
            binding.fabRecordAudio.show()
            binding.fabTakePicture.show()
            binding.fabSave.show()
            binding.fabEdit.hide()
            binding.editTextTitle.isEnabled = true
            binding.editTextDate.isEnabled = true
        }
        binding.fabSave.setOnClickListener {
            binding.fabGallery.hide()
            binding.fabRecordAudio.hide()
            binding.fabTakePicture.hide()
            binding.fabSave.hide()
            binding.fabEdit.show()
            binding.editTextTitle.isEnabled = false
            binding.editTextDate.isEnabled = false
            saveMemory()
        }
        binding.fabSave.hide()
        binding.fabGallery.hide()
        binding.fabRecordAudio.hide()
        binding.fabTakePicture.hide()
        binding.editTextTitle.isEnabled = false
        binding.editTextDate.isEnabled = false

        title = "Edit Memory"

        val memoryId = intent.getLongExtra(EXTRA_ID, -1)
        val title = intent.getStringExtra(EXTRA_TITLE)!!
        val date = intent.getStringExtra(EXTRA_DATE)!!
        val longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, .0)
        val latitude = intent.getDoubleExtra(EXTRA_LATITUDE, .0)

        binding.fabRecordAudio.setOnClickListener{ startRecorder(memoryId) }
        binding.fabTakePicture.setOnClickListener{ startCamera(memoryId) }

        val memory = Memory(title, date, longitude, latitude)
        memory.id = memoryId
        editViewModel.initMemory(memory)
        editViewModel.getMedia().observe(this) {
            Log.d("EditActivity", "Media loaded: ${it.size}")
            it.forEach {
                Log.d("EditActivity", "${it.id}, ${it.memoryId}, ${it.path.toUri()}")
            }
            mediaAdapter.submitList(it) {
                layoutManager.invalidateSpanAssignments()
            }
        }

        binding.editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE))

        var day: Int
        var month: Int
        var year: Int
        try {
            day = date.substring(0, 2).toInt()
            month = date.substring(3, 5).toInt() - 1
            year = date.substring(6, 10).toInt()
            binding.editTextDate.setText(intent.getStringExtra(EXTRA_DATE))
        }
        catch (_: NumberFormatException) {
            val c = Calendar.getInstance()
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
            binding.editTextDate.setText("$day.$month.$year")
        }

        val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
                uris ->
            if (uris.isNotEmpty()) {
                Log.d("EditActivity", "Selected URIs: $uris")
                uris.forEach {
                    val mimeType = contentResolver.getType(it)

                    if (mimeType != null) {
                        val type: Type
                        if (mimeType.startsWith("image")) {
                            type = Type.IMAGE
                            val media = Media(memoryId, type, it.toString())
                            editViewModel.insert(media)
                        } else if (mimeType.startsWith("video")) {
                            type = Type.VIDEO
                            val media = Media(memoryId, type, it.toString())
                            editViewModel.insert(media)
                        }
                    }
                }
            } else {
                Log.d("EditActivity", "No media selected")
            }
        }

        binding.fabGallery.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }
        binding.editTextDate.isFocusable = false

        binding.editTextDate.setOnClickListener {

            val datePickerDialog = DatePickerDialog(
                this,
                { _, yearRes, monthOfYear, dayOfMonth ->
                    binding.editTextDate.setText("$dayOfMonth.${monthOfYear+1}.$yearRes")
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }
    }

    private fun startRecorder(memoryId: Long) {
        val myIntent = Intent(this, RecorderActivity::class.java)
        myIntent.putExtra("memoryId", memoryId) //Optional parameters

        startActivity(myIntent)
    }

    private fun startCamera(memoryId: Long) {
        val myIntent = Intent(this, CameraActivity::class.java)
        myIntent.putExtra("memoryId", memoryId) //Optional parameters

        startActivity(myIntent)
    }

    private fun saveMemory() {
        val title: String = binding.editTextTitle.text.toString()
        val date: String = binding.editTextDate.text.toString()

        if (title.trim { it <= ' ' }.isEmpty() || date.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Please insert a title and date", Toast.LENGTH_SHORT).show()
            return
        }

        val memory = editViewModel.getMemory()
        memory.title = title
        memory.date = date

        editViewModel.update(memory)
    }

    companion object {
        const val EXTRA_ID = "at.jku.yourmemorylane.EXTRA_ID"
        const val EXTRA_TITLE = "at.jku.yourmemorylane.EXTRA_TITLE"
        const val EXTRA_DATE = "at.jku.yourmemorylane.EXTRA_DATE"
        const val EXTRA_LONGITUDE = "at.jku.yourmemorylane.EXTRA_LONGITUDE"
        const val EXTRA_LATITUDE = "at.jku.yourmemorylane.EXTRA_LATITUDE"
    }

}