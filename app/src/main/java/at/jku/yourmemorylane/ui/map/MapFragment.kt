package at.jku.yourmemorylane.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import at.jku.yourmemorylane.R
import at.jku.yourmemorylane.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(), OnMapReadyCallback{

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private var locationManager: LocationManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[MapViewModel::class.java]

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.
            findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root;
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val wahli = LatLng(48.2711809, 14.5278233)
        mMap.addMarker(MarkerOptions().position(wahli).title("Marker in R.i.d.R."))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(wahli, 15f))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}