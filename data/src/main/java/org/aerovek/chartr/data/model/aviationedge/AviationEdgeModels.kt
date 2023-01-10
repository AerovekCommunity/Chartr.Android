package org.aerovek.chartr.data.model.aviationedge

data class AEAutocomplete (
    val airportsByCities: List<AEAirport>,
    val cities: List<AECity>
)

data class AECity(
    val GMT: String,
    val codeIataCity: String,
    val codeIso2Country: String,
    val latitudeCity: Double,
    val longitudeCity: Double,
    val nameCity: String,
    val timezone: String
)

data class AEAirport(
    val GMT: String,
    val codeIataAirport: String,
    val codeIataCity: String,
    val codeIcaoAirport: String,
    val codeIso2Country: String,
    val latitudeAirport: Double,
    val longitudeAirport: Double,
    val nameAirport: String,
    val nameCountry: String,
    val phone: String,
    val timezone: String,
    val distance: Double?
)