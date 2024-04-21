package com.netanel.adscarrouselcompose.model

import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("videoLinks")
    val videoLinks: List<String>,
    @SerializedName("photoLinks")
    val photoLinks: List<String>
)