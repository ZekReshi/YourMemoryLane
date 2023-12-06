package at.jku.yourmemorylane.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import at.jku.yourmemorylane.R
import at.jku.yourmemorylane.databinding.FragmentMapBinding
import at.jku.yourmemorylane.db.AppDatabase
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

        mapViewModel.getMemories().observe(viewLifecycleOwner) { memories ->memories.forEach{
            var markerOptions = MarkerOptions().
            position(LatLng(it.latitude, it.longitude)).
            title(it.title)

            mapViewModel.getImagesByMemoryId(it.id).observe(viewLifecycleOwner) {
                images ->
                if(images != null && images.size >0)
                {
                    val length = images.size
                    val randIndex = Random.nextInt(0,length)
                    val media = images[randIndex]
                    val inputStream: InputStream? = activity?.contentResolver?.openInputStream(media.path.toUri())
                    val drawable = Drawable.createFromStream(inputStream, media.path)


                    val bitmap = (drawable as BitmapDrawable).bitmap
// Scale it to 100 x 100
// Scale it to 100 x 100
                    val newDrawable: Drawable =
                        BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, 300, 300, true))
                    markerOptions
                        .icon(BitmapDescriptorFactory.fromBitmap((newDrawable as  BitmapDrawable).getBitmap()));
                }
                mMap.
                addMarker(markerOptions)
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
        };
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