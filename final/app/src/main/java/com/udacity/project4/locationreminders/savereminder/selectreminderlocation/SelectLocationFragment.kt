package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject


private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private var mMap: GoogleMap? = null
    private var mLocationPermissionGranted: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

//        TODO: add the map setup implementation
        val mapFragment = childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        TODO: zoom to the user location after taking his permission

//        TODO: add style to the map
//        TODO: put a marker to location that the user selected


//        TODO: call this function after the user confirms on the selected location
        onLocationSelected()

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        // Add a marker in Sydney, Australia, and move the camera.
        // Add a marker in Sydney, Australia, and move the camera.
        /*val sydney = LatLng((-34).toDouble(), (151).toDouble())
        mMap?.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/

        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
    }

    private fun getLocationPermission() { /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(context !!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(activity !!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    private fun getDeviceLocation() { /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                val mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity as Activity)
                val locationResult = mFusedLocationProviderClient.lastLocation

                //locationResult.addOnCompleteListener(activity as Activity, object :)


                /*locationResult.addOnCompleteListener(
                    this,
                    object : OnCompleteListener<Any?> {
                        fun onComplete(task: Task) {
                            if (task.isSuccessful()) { // Set the map's camera position to the current location of the device.
                                mLastKnownLocation = task.getResult()
                                mMap!!.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(
                                            mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()
                                        ), DEFAULT_ZOOM
                                    )
                                )
                            } else {
                                Log.d("SelectLocationFragment", "Current location is null. Using defaults.")
                                Log.e("SelectLocationFragment", "Exception: %s", task.getException())
                                mMap!!.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        mDefaultLocation,
                                        DEFAULT_ZOOM
                                    )
                                )
                                mMap!!.uiSettings.isMyLocationButtonEnabled = false
                            }
                        }
                    })*/
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }

        updateLocationUI()
    }

    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                mMap!!.isMyLocationEnabled = true
                mMap!!.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap!!.isMyLocationEnabled = false
                mMap!!.uiSettings.isMyLocationButtonEnabled = false
                //mLastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }



    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            true
        }
        R.id.hybrid_map -> {
            true
        }
        R.id.satellite_map -> {
            true
        }
        R.id.terrain_map -> {
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
