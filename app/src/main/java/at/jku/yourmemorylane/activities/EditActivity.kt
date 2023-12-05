package at.jku.yourmemorylane.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import at.jku.yourmemorylane.adapters.MediaAdapter
import at.jku.yourmemorylane.databinding.ActivityEditBinding
import at.jku.yourmemorylane.db.entities.Media
import com.bumptech.glide.Glide
import java.util.Calendar


class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var editViewModel: EditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editViewModel = ViewModelProvider(this)[EditViewModel::class.java]

        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.mediaRecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val mediaAdapter = MediaAdapter()
        recyclerView.adapter = mediaAdapter

        binding.fabSave.setOnClickListener { saveMemory() }
        var day: Int
        var month: Int
        var year: Int
        if (intent.hasExtra(EXTRA_ID)) {
            title = "Edit Memory"

            val memoryId = intent.getIntExtra(EXTRA_ID, -1)
            binding.recordAudioButton.setOnClickListener{ startRecorder(memoryId) }
            binding.takePictureButton.setOnClickListener{ startCamera(memoryId)}
            editViewModel.initMedia(memoryId)
            editViewModel.getMedia().observe(this) {
                Log.d("EditActivity", "Media loaded: ${it.size}")
                it.forEach {
                    Log.d("EditActivity", "${it.id}, ${it.memoryId}, ${it.path.toUri()}")
                }
                mediaAdapter.submitList(it)
            }

            binding.editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE))
            val date = intent.getStringExtra(EXTRA_DATE)!!
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
                        val media = Media( memoryId, "image", it.toString())
                        editViewModel.insert(media)
                    }
                } else {
                    Log.d("EditActivity", "No media selected")
                }
            }

            binding.fabMedia.setOnClickListener {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            }
        }
        else {
            title = "Create Memory"

            binding.fabMedia.hide()

            val c = Calendar.getInstance()
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
            binding.editTextDate.setText("$day.$month.$year")
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

    private fun startRecorder(memoryId: Int) {
        val myIntent = Intent(this, RecorderActivity::class.java)
        myIntent.putExtra("memoryId", memoryId) //Optional parameters

        startActivity(myIntent)
    }

    private fun startCamera(memoryId: Int) {
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

        val data = Intent()
        data.putExtra(EXTRA_TITLE, title)
        data.putExtra(EXTRA_DATE, date)

        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_ID, id)
        }

        setResult(RESULT_OK, data)
        finish()
    }

    companion object {
        const val EXTRA_ID = "at.jku.yourmemorylane.EXTRA_ID"
        const val EXTRA_TITLE = "at.jku.yourmemorylane.EXTRA_TITLE"
        const val EXTRA_DATE = "at.jku.yourmemorylane.EXTRA_DATE"
    }

}