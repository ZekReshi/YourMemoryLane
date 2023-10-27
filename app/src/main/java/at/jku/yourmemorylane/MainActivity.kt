package at.jku.yourmemorylane

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import at.jku.yourmemorylane.databinding.ActivityMainBinding
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.db.entities.Memory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "memories-db").allowMainThreadQueries()
            .build();
        val memoryDao = db.memoryDao();
        val memories: List<Memory> = memoryDao.getAll();
    }
}