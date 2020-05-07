package com.briostrategies.adfone

import android.app.Application
import android.location.Location
import android.text.Editable
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import androidx.lifecycle.*
import com.briostrategies.adfone.PlacesApi.Companion.buildPlacesOptions
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val locationClient by lazy { LocationServices.getFusedLocationProviderClient(application) }
    private val dao by lazy { SearchesDatabase.getDatabase(application).searchesDao() }
    private val searchesData by lazy { dao.getAll() }

    val radiusData by lazy { MutableLiveData<Int>().apply { value = 10 } }

    val placesData by lazy {
        searchesData.switchMap { searches ->
            Logger.d(TAG, "Searches are updated in database: ${searches.size}")
            liveData {
                emit(searches.lastOrNull())
            }
        }
    }

    var keyword: Editable? = null

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

    fun search() {
        Logger.i(TAG, "New search initiated")
        viewModelScope.launch(Dispatchers.Main) {
            val location: Location? = suspendCoroutine { cont ->
                locationClient.lastLocation
                    .addOnSuccessListener { location -> cont.resume(location) }
                    .addOnFailureListener { cont.resume(null) }
            }
            Logger.d(TAG, "Location received: $location")

            val places = location?.let {
                PlacesApi.api.getPlaces(buildPlacesOptions(location, radius, keyword?.toString()))
            }
            Logger.d(TAG, "Places loaded: $places")

            places?.let {
                dao.insert(it)
                Logger.d(TAG, "Places stored in database")
            }
        }
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
        private const val DEFAULT_RADIUS = 16094
    }
}
