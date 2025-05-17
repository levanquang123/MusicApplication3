package net.braniumacademy.musicapplication.data.source.remote

import com.google.gson.annotations.SerializedName

data class PagingParam(
    @SerializedName("offset") val offset: Int,
    @SerializedName("limit") val limit: Int
)
