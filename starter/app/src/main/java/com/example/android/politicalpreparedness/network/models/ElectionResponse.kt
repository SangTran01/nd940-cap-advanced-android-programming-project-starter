package com.example.android.politicalpreparedness.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ElectionResponse(
        @Json(name = "kind")
        val kind: String,
        @Json(name = "elections")
        val elections: List<Election>
)