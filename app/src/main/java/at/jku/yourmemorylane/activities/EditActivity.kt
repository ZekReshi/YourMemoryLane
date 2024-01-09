package at.jku.yourmemorylane.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import at.jku.yourmemorylane.R
import at.jku.yourmemorylane.adapters.MediaAdapter
import at.jku.yourmemorylane.databinding.ActivityEditBinding
import at.jku.yourmemorylane.db.Converters
import at.jku.yourmemorylane.db.entities.Media
import at.jku.yourmemorylane.db.entities.Memory
import at.jku.yourmemorylane.db.entities.Type
import at.jku.yourmemorylane.viewmodels.EditViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar


class EditActivity : AppCompatActivity() {

    private lateinit var mediaDetailActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityEditBinding
    private lateinit var editViewModel: EditViewModel
    private var edit = false

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
            edit = !edit

            if (edit) {
                binding.fabGallery.show()
                binding.fabRecordAudio.show()
                binding.fabTakePicture.show()
                binding.fabText.show()
                binding.fabEdit.setImageResource(R.drawable.baseline_check_24)
            }
            else {
                binding.fabGallery.hide()
                binding.fabRecordAudio.hide()
                binding.fabTakePicture.hide()
                binding.fabText.hide()
                binding.fabEdit.setImageResource(R.drawable.baseline_edit_note_24)
                saveMemory()
            }
            binding.editTextTitle.isEnabled = edit
            binding.editTextDate.isEnabled = edit
        }
        binding.fabGallery.hide()
        binding.fabRecordAudio.hide()
        binding.fabTakePicture.hide()
        binding.fabText.hide()
        binding.editTextTitle.isEnabled = false
        binding.editTextDate.isEnabled = false

        title = "Edit Memory"

        val memoryId = intent.getLongExtra(EXTRA_ID, -1)
        Log.d("EditActivity", memoryId.toString())
        editViewModel.initMemory(memoryId)

        editViewModel.getMemory().observe(this) {
            if (it != null) {
                binding.editTextTitle.setText(it.title)

                val dateFormat = SimpleDateFormat.getDateInstance()
                binding.editTextDate.setText(dateFormat.format(it.date))
            }
        }
        editViewModel.getMedia().observe(this) {
            mediaAdapter.submitList(it)
        }

        binding.fabRecordAudio.setOnClickListener{ startRecorder(memoryId) }
        binding.fabTakePicture.setOnClickListener{ startCamera(memoryId) }

        val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
                uris ->
            if (uris.isNotEmpty()) {
                uris.forEach {
                    val mimeType = contentResolver.getType(it)
                    contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)

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

        binding.fabText.setOnClickListener {
            val mediaId = editViewModel.insert(Media(memoryId, Type.TEXT, "Your Text Here"))

            val intent = Intent(applicationContext, MediaDetailActivity::class.java)
            intent.putExtra(MediaDetailActivity.EXTRA_ID, mediaId)

            mediaDetailActivityLauncher.launch(intent)
        }

        binding.editTextDate.isFocusable = false

        binding.editTextDate.setOnClickListener {

            val calendar = GregorianCalendar()
            calendar.time = editViewModel.getMemory().value!!.date

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    editViewModel.getMemory().value!!.date = calendar.time

                    val dateFormat = SimpleDateFormat.getDateInstance()
                    binding.editTextDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
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
        val memory = editViewModel.getMemory().value!!

        if (title.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Please insert a title and date", Toast.LENGTH_SHORT).show()
            return
        }

        memory.title = title

        editViewModel.update(memory)
    }

    companion object {
        const val EXTRA_ID = "at.jku.yourmemorylane.EXTRA_ID"
    }

}