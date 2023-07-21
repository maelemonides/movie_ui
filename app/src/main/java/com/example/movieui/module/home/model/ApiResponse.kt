package com.example.movieui.module.home.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("results")
    val results: List<T>
)
