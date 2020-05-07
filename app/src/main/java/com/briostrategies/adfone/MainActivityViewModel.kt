package com.briostrategies.adfone

import android.location.Location
import android.text.Editable
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.Factory
import com.briostrategies.adfone.PlacesApi.Companion.buildPlacesOptions
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivityViewModel(private val locationClient: FusedLocationProviderClient) : ViewModel() {
    private val locationData = MutableLiveData<Location?>()

    val placesData = locationData.switchMap { location ->
        Logger.d(TAG, "Location is detected: $location")
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val places = location?.let {
                PlacesApi.api.getPlaces(buildPlacesOptions(location, radius, keyword?.toString()))
            }
            Logger.d(TAG, "Places loaded: $places")
            emit(places)
        }
    }

    var keyword: Editable? = null

    val radiusData = MutableLiveData<Int>().apply { value = 10 }
    var radius: Int = DEFAULT_RADIUS
        set(progress) {
            field = calculateNewRadius(progress)
            radiusData.value = field * 10 / (DEFAULT_RADIUS)
            Logger.d(TAG, "New search radius: $field meters")
        }

    // Calculates radius in meters as representation of 5, 10, 15, 20, 25, 30 miles and then capped by max value of 50 km
    @VisibleForTesting(otherwise = PRIVATE)
    fun calculateNewRadius(step: Int): Int {
        check(step >= 0) { "Step parameter should be positive" }
        return minOf(DEFAULT_RADIUS * (step + 1) / 2, 50_000)
    }

    fun update() {
        Logger.i(TAG, "New search initiated")
        viewModelScope.launch(Dispatchers.Main) {
            locationData.value = suspendCoroutine { cont ->
                locationClient.lastLocation
                    .addOnSuccessListener { location -> cont.resume(location) }
                    .addOnFailureListener { cont.resume(null) }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
        private const val DEFAULT_RADIUS = 16094
    }
}

@Suppress("UNCHECKED_CAST")
class MainActivityViewModelFactory(private val client: FusedLocationProviderClient) : Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            MainActivityViewModel(client) as T
        } else {
            throw IllegalArgumentException("Factory generates only MainActivityViewModel")
        }
}