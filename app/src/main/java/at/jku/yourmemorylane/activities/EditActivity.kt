package at.jku.yourmemorylane.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import at.jku.yourmemorylane.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}