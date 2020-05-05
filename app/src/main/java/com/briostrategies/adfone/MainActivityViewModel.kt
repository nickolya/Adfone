package com.briostrategies.adfone

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.briostrategies.adfone.PlacesApi.Companion.buildPlacesOptions
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val locationClient = LocationServices.getFusedLocationProviderClient(application)

    private val locationData = MutableLiveData<Location>()

    val placesData = locationData.switchMap { location ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(PlacesApi.api.getPlaces(buildPlacesOptions(location, radius)))
        }
    }

    var radius: Int = DEFAULT_RADIUS
        set(progress) {
            field = DEFAULT_RADIUS * (progress + 1) / 2
        }

    fun update() {
        viewModelScope.launch(Dispatchers.Main) {
            val location = getCurrentLocation()
            Log.d(TAG, "Location $location")
            if (location != null) {
                locationData.value = location
            }
        }
    }

    private suspend fun getCurrentLocation() = suspendCoroutine<Location?> { cont ->
        locationClient.lastLocation
            .addOnSuccessListener { location -> cont.resume(location) }
            .addOnFailureListener { error -> cont.resumeWithException(error) }
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
        private const val DEFAULT_RADIUS = 16093
    }
}