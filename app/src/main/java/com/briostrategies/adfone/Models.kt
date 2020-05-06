package com.briostrategies.adfone

import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("photo_reference") var reference: String,
    @SerializedName("height") var height: Int,
    @SerializedName("width") var width: Int
)

data class Place(
    @SerializedName("id") var id: String,
    @SerializedName("name") val name: String,
    @SerializedName("rating") val rating: String,
    @SerializedName("photos") val photos: List<Photo>
)

data class Status(
    @SerializedName("status") var status: String
)

data class Places(
    @SerializedName("results") var places: List<Place>
)