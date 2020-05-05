package com.briostrategies.adfone

import android.location.Location
import android.net.Uri
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * Based on Google API https://developers.google.com/places/web-service/search#PlaceSearchRequests
 */
interface PlacesApi {

    @GET("maps/api/place/nearbysearch/json")
    suspend fun getPlaces(@QueryMap options: Map<String, String>): Places

//    https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU&key=YOUR_API_KEY

    companion object {

        private const val BASE_ADDRESS = "https://maps.googleapis.com/"
        private const val KEY = "AIzaSyA7AqFTyJC8-PjG0cQ-7LyRSX3GvkePmt8"

        private const val MAX_WIDTH = 400

        val api: PlacesApi by lazy {

            Retrofit.Builder()
                .baseUrl(BASE_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .apply {
                    if (BuildConfig.DEBUG) {
                        val loggingInterceptor = HttpLoggingInterceptor().apply {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                        val client =
                            OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
                        client(client)
                    }
                }
                .build()
                .create(PlacesApi::class.java)
        }

        fun buildPlacesOptions(
            location: Location,
            radius: Int,
            keyword: String? = null

        ) = mutableMapOf(
            "radius" to "$radius",
            "location" to String.format("%s,%s", location.latitude, location.longitude),
            "key" to KEY

        ).also { fields ->
            keyword?.takeIf { it.isNotBlank() }?.let {
                fields["keyword"] = it
            }
        }

        fun builsPhotoUri(photo: Photo) = Uri.parse(BASE_ADDRESS)
            .buildUpon()
            .appendEncodedPath("photo")
            .appendQueryParameter("photoreference", photo.reference)
            .appendQueryParameter("maxwidth", "${minOf(photo.width, MAX_WIDTH)}")
            .appendQueryParameter("key", KEY)
            .build()
    }
}
