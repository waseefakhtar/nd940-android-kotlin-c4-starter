package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.geofence.GeofenceErrorMessages
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

private const val GEOFENCE_RADIUS_IN_METERS = 100

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value

//            TODO: use the user entered reminder details to:
//             1) add a geofencing request
//             2) save the reminder to the local db
            val geofencingClient = LocationServices.getGeofencingClient(context !!)
            val reminder = ReminderDTO(title, description, location, latitude, longitude)

            add(context !!,  reminder, geofencingClient)
        }
    }

    fun add(context: Context,
            reminder: ReminderDTO,
            geofencingClient: GeofencingClient) {
        // 1
        val geofence = buildGeofence(reminder)
        if (geofence != null && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 2
            geofencingClient
                .addGeofences(buildGeofencingRequest(geofence), geofencePendingIntent)
                // 3
                .addOnSuccessListener {
                    _viewModel.validateAndSaveReminder(ReminderDataItem(
                        reminder.title,
                        reminder.description,
                        reminder.location,
                        reminder.latitude,
                        reminder.longitude,
                        reminder.id))
                }
                // 4
                .addOnFailureListener {
                    GeofenceErrorMessages.getErrorString(context, it)
                }
        }
    }

    private fun buildGeofence(reminder: ReminderDTO): Geofence? {
        val latitude = reminder.latitude
        val longitude = reminder.longitude
        val radius =  GEOFENCE_RADIUS_IN_METERS

        if (latitude != null && longitude != null && radius != null) {
            return Geofence.Builder()
                // 1
                .setRequestId(reminder.id)
                // 2
                .setCircularRegion(latitude, longitude, radius.toFloat())
                // 3
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                // 4
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }

        return null
    }

    private fun buildGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(0)
            .addGeofences(listOf(geofence))
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }
}