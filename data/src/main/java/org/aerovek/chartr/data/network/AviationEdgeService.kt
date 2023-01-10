package org.aerovek.chartr.data.network

import org.aerovek.chartr.data.model.ResponseBase

interface AviationEdgeService {
    fun autocomplete(
        cityName: String?,
        airportName: String?,
        codeIataCity: String?,
        codeIataAirport: String?,
        countryCode: String?
    )
}

internal class AviationEdgeServiceImpl : AviationEdgeService {
    override fun autocomplete(
        cityName: String?,
        airportName: String?,
        codeIataCity: String?,
        codeIataAirport: String?,
        countryCode: String?
    ) {
    }

}