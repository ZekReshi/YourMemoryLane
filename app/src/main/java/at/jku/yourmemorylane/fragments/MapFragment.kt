package at.jku.yourmemorylane.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import at.jku.yourmemorylane.R
import at.jku.yourmemorylane.activities.EditActivity
import at.jku.yourmemorylane.databinding.FragmentMapBinding
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.viewmodels.MapViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.InputStream
import kotlin.random.Random


class MapFragment : Fragment(), OnMapReadyCallback{

    private lateinit var mapViewModel: MapViewModel
    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private var addMemory = false
    private lateinit var editActivityLauncher: ActivityResultLauncher<Intent>
    private var markerToId: MutableMap<String, Long> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.
            findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        AppDatabase.getInstance(this.requireContext())

        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapViewModel =
            ViewModelProvider(this)[MapViewModel::class.java]

        _binding!!.addMemoryButton.setOnClickListener {
            addMemory = !addMemory
            _binding!!.addMemoryButton.setImageResource(if (addMemory) R.drawable.baseline_cancel_24 else R.drawable.baseline_pin_drop_24)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style))
        mMap.setOnMarkerClickListener {
            val intent = Intent(activity, EditActivity::class.java)
            intent.putExtra(EditActivity.EXTRA_ID, markerToId.getValue(it.id))

            editActivityLauncher.launch(intent)

            true
        }

        mMap.setOnMapClickListener {
            if (addMemory) {
                addMemory = false

                val memory = mapViewModel.insertMemory(it.longitude, it.latitude)

                val intent = Intent(activity, EditActivity::class.java)
                intent.putExtra(EditActivity.EXTRA_ID, memory.id)

                editActivityLauncher.launch(intent)
            }
        }

        mapViewModel.getMemories().observe(viewLifecycleOwner) { memories ->memories.forEach{
            val markerOptions = MarkerOptions().
            position(LatLng(it.latitude, it.longitude)).
            title(it.title)

            mapViewModel.getImagesByMemoryId(it.id).observe(viewLifecycleOwner) {
                images ->
                try {
                    if(images != null && images.isNotEmpty())
                    {
                        val length = images.size
                        val randIndex = Random.nextInt(0,length)
                        val media = images[randIndex]
                        val inputStream: InputStream? = activity?.contentResolver?.openInputStream(media.path.toUri())
                        val drawable = Drawable.createFromStream(inputStream, media.path)

                        val bitmap = (drawable as BitmapDrawable).bitmap

                        val src = Bitmap.createScaledBitmap(bitmap, 150, 150, true)
                        val newDrawable =
                            RoundedBitmapDrawableFactory.create(resources, src)
                        newDrawable.cornerRadius = src.width.coerceAtLeast(src.height) / 2.0f
                        markerOptions
                            .icon(BitmapDescriptorFactory.fromBitmap(newDrawable.toBitmap()))
                    }
                    else {
                        val drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_camera)
                        val bitmap = drawable!!.toBitmap()

                        val src = Bitmap.createScaledBitmap(bitmap, 150, 150, true)
                        val newDrawable =
                            RoundedBitmapDrawableFactory.create(resources, src)
                        newDrawable.cornerRadius = src.width.coerceAtLeast(src.height) / 2.0f
                        markerOptions
                            .icon(BitmapDescriptorFactory.fromBitmap(newDrawable.toBitmap()))
                    }
                } catch (_: SecurityException) {
                    Log.d("MapFragment", "Cannot access file")
                }
                val marker = mMap.addMarker(markerOptions)
                markerToId[marker!!.id] = it.id
            }
        } }


        if (ContextCompat.checkSelfPermission(
                context as Context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context as Context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
        else {
            mMap.isMyLocationEnabled = true
        }
    }

    @SuppressLint("MissingPermission")
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        mMap.isMyLocationEnabled=true;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}