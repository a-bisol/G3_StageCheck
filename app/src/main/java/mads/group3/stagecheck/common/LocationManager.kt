package mads.group3.stagecheck.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mads.group3.stagecheck.common.repositories.LocationRepository
import mads.group3.stagecheck.models.LocationData
import java.util.Locale

object LocationManager {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        isInitialized = true
        fetchLocation(context)
    }

    fun fetchLocation(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!hasLocationPermissions(context)) {
                    return@launch
                }

                val lastLocation = fusedLocationClient.lastLocation.await()
                if (lastLocation != null) {
                    val locationData =
                        reverseGeocode(context, lastLocation.latitude, lastLocation.longitude)
                    LocationRepository.updateLocation(
                        locationData.city,
                        locationData.state,
                        locationData.country
                    )
                    return@launch
                }
            } catch (e: Exception) {
                e.message?.let { Log.e("LocationManager - fetchLocation", it) }
                LocationRepository.updateLocation("Toronto", "ON", "CA")
            } catch (e: SecurityException) {
                e.message?.let { Log.e("LocationManager - fetchLocation", it) }
                LocationRepository.updateLocation("Toronto", "ON", "CA")
            }
        }
    }

    fun refreshLocation(context: Context) {
        fetchLocation(context)
    }

    private fun hasLocationPermissions(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun reverseGeocode(context: Context, lat: Double, lng: Double): LocationData {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = android.location.Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.subAdminArea ?: "Unknown"
                    val state =
                        StateCodeMapper.getCode(address.adminArea) ?: address.adminArea ?: "Unknown"
                    val country = address.countryCode ?: "Unknown"
                    LocationData(city, state, country)
                } else {
                    LocationData("Toronto", "ON", "CA")
                }
            } catch (_: Exception) {
                LocationData("Toronto", "ON", "CA")
            }
        }
    }
}