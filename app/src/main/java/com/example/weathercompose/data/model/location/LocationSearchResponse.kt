package com.example.weathercompose.data.model.location

import com.google.gson.annotations.SerializedName


data class LocationSearchResponse(
    @SerializedName("admin1")
    val firstAdministrativeLevel: String?,
    @SerializedName("admin1_id")
    val firstAdministrativeLevelId: Int?,
    @SerializedName("admin2")
    val secondAdministrativeLevel: String?,
    @SerializedName("admin2_id")
    val secondAdministrativeLevelId: Int?,
    @SerializedName("admin3")
    val thirdAdministrativeLevel: String?,
    @SerializedName("admin3_id")
    val thirdAdministrativeLevelId: Int?,
    @SerializedName("admin4")
    val fourthAdministrativeLevel: String?,
    @SerializedName("admin4_id")
    val fourthAdministrativeLevelId: Int?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("country_code")
    val countryCode: String?,
    @SerializedName("country_id")
    val countryId: Int?,
    @SerializedName("elevation")
    val elevation: Double?,
    @SerializedName("feature_code")
    val featureCode: String?,
    @SerializedName("id")
    val id: Long?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("population")
    val population: Int?,
    @SerializedName("postcodes")
    val postcodes: List<String>?,
    @SerializedName("timezone")
    val timezone: String?
)