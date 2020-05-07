package com.briostrategies.adfone

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken


data class Photo(
    @SerializedName("photo_reference") val reference: String,
    @SerializedName("height") val height: Int,
    @SerializedName("width") val width: Int
)

data class Place(
    @SerializedName("id") var id: String,
    @SerializedName("name") val name: String,
    @SerializedName("rating") val rating: String,
    @SerializedName("photos") val photos: List<Photo>
)

data class Status(
    @SerializedName("status") val status: String
)

@Entity(tableName = "searches")
data class Places(
    @SerializedName("results") val places: List<Place>
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

class PlacesConverter {
    @TypeConverter
    fun fromPlaceList(places: List<Place>): String {
        val type = object : TypeToken<List<Place>>() {}.type
        return Gson().toJson(places, type)
    }

    @TypeConverter
    fun toPlaceList(placesString: String): List<Place> {
        val type = object : TypeToken<List<Place>>() {}.type
        return Gson().fromJson(placesString, type)
    }
}