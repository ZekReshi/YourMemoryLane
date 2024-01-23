package at.jku.yourmemorylane.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
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
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.io.InputStream
import kotlin.random.Random


class MapFragment : Fragment(), OnMapReadyCallback{

    private lateinit var mapViewModel: MapViewModel
    private lateinit var geofencingClient: GeofencingClient;
    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private var addMemory = false
    private lateinit var editActivityLauncher: ActivityResultLauncher<Intent>
    private var markerToId: MutableMap<String, Long> = HashMap()
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }


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
            val typedValue = TypedValue()
            val theme = requireContext().theme
            var color: Int =0;
            if(addMemory){
                theme.resolveAttribute(
                    com.google.android.material.R.attr.colorError,
                    typedValue,
                    true
                )
            }
            else {
                theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)

            }
            color = typedValue.data
            _binding!!.addMemoryButton.backgroundTintList = ColorStateList.valueOf(color);
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

            val images= mapViewModel.getImagesByMemoryId(it.id)
            try {
                if(images != null && images.isNotEmpty())
                {
                    val length = images.size
                    val randIndex = Random.nextInt(0,length)
                    val media = images[randIndex]
                    val inputStream: InputStream? = activity?.contentResolver?.openInputStream(media.path.toUri())
                    val drawable = Drawable.createFromStream(inputStream, media.path)

                        var bitmap = (drawable as BitmapDrawable).bitmap

                        if (bitmap.height > bitmap.width) {
                            val cut = (bitmap.height - bitmap.width) / 2
                            bitmap = Bitmap.createBitmap(bitmap, 0, cut, bitmap.width, bitmap.height - 2 * cut)
                        }
                        else if (bitmap.width > bitmap.height) {
                            val cut = (bitmap.width - bitmap.height) / 2
                            bitmap = Bitmap.createBitmap(bitmap, cut, 0, bitmap.width - 2 * cut, bitmap.height)
                        }

                        val src = Bitmap.createScaledBitmap(bitmap, 150, 150, true)
                        val newDrawable =
                            RoundedBitmapDrawableFactory.create(resources, src)
                        newDrawable.cornerRadius = src.width.coerceAtLeast(src.height) / 2.0f
                        markerOptions
                            .icon(BitmapDescriptorFactory.fromBitmap(newDrawable.toBitmap()))
                    }
                    else {
                        val drawable = AppCompatResources.getDrawable(requireContext(),
                            R.drawable.baseline_star_24
                        )
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
        }


        if (ContextCompat.checkSelfPermission(
                context as Context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context as Context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context as Context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION),1)
        }
        else {
            mMap.isMyLocationEnabled = true
            manageGeoFences()
        }
    }

    @SuppressLint("MissingPermission")
    private fun manageGeoFences() {
        geofencingClient = LocationServices.getGeofencingClient(this.activity as Activity)
       val geofenceList= mutableListOf<Geofence>()
        mapViewModel.getMemories().observe(viewLifecycleOwner) { memories ->  run {
            memories.forEach {

                var geofence = Geofence.Builder()
                    .setRequestId(it.id.toString())
                    .setCircularRegion(it.latitude, it.longitude, 500.0f)
                    .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_DWELL)
                    .setLoiteringDelay(20 * 1000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build()
                geofenceList.add(geofence)

            };
            if(!geofenceList.isEmpty()){
                Log.i("GEOFENCE",geofenceList.size.toString())

                var geofencingRequest = GeofencingRequest.Builder().apply {
                addGeofences(geofenceList)
                setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            }.build()
            geofencingClient?.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
                addOnSuccessListener {
                    // Geofences added
                    //
                    Log.i("GEOFENCE","success")

                }
                addOnFailureListener {
                    Log.i("GEOFENCE","error")
                    // Failed to add geofences
                    // ...
                }
            }}
        }
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
        manageGeoFences()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

