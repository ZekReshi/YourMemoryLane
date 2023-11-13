package at.jku.yourmemorylane.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.jku.yourmemorylane.databinding.ActivityEditBinding
import java.time.LocalDate


class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabSave.setOnClickListener { saveMemory() }

        if (intent.hasExtra(EXTRA_ID)) {
            title = "Edit Memory"
            binding.editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE))
            val date = intent.getStringExtra(EXTRA_DATE)!!
            try {
                binding.datePickerDate.updateDate(date.substring(6, 10).toInt(), date.substring(3, 5).toInt()-1, date.substring(0, 2).toInt())
                return
            }
            catch (_: NumberFormatException) { }
        }
        else {
            title = "Create Memory"
        }
        val date = LocalDate.now()
        binding.datePickerDate.updateDate(date.year, date.monthValue, date.dayOfMonth)
    }

    private fun saveMemory() {
        val title: String = binding.editTextTitle.text.toString()
        val date: String = "" + binding.datePickerDate.dayOfMonth + '.' +
                (binding.datePickerDate.month + 1) + '.' + binding.datePickerDate.year

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