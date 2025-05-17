package net.braniumacademy.musicapplication.data.model.artist

import com.google.gson.annotations.SerializedName

data class ArtistList(
    @SerializedName("artists") val artists: List<Artist>
)