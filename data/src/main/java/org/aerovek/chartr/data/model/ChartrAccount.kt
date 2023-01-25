package org.aerovek.chartr.data.model

import com.google.gson.GsonBuilder

data class PilotCertificate(
    val title: String,
    val description: String,
    val type: String
)

data class PilotDetails(
    val firstName: String,
    val lastName: String,
    val email: String?,
    val certificates: String?,
    val qualifications: String?,
    val bio: String?,
    val flightTimeHours: String?,
    val ratingsExpirationDate: String?,
    val ratings: String?
)

data class AircraftDetails(
    val model: String?,
    val description: String?,
    val typeDesignator: String?,
    val wtc: String?,
    val manufacturerCode: String?,
    val engineType: String?,
    val engineCount: Int?,
    val seatCapacity: Int?,
    val weightLimit: Int?,
    val rangeInNauticalMiles: Int?,
    val specialEquipment: String?
)

data class ChartrBusinessProfile(
    var businessName: String,
    var country: String,
    var businessCategory: String,
    var searchTags: List<String>?,
    val pilotDetails: List<PilotDetails>?,
    val aircraftDetails: List<AircraftDetails>?
)

data class ChartrAccount(
    /** The user's wallet address */
    val id: String,
    /** Either personal or business */
    val accountType: String,
    /** User's unique username */
    val username: String,
    var recordVersion: Int,
    var timestamp: String,
    var email: String? = null,
    /** URL where this image is stored in Firebase storage */
    var profileImageUrl: String? = null,
    val businessProfile: ChartrBusinessProfile? = null
) {
    fun serialize(): String {
        val gson = GsonBuilder().disableHtmlEscaping().create()
        return gson.toJson(toMap())
    }

    fun toJsonArray(): String {
        val gson = GsonBuilder().disableHtmlEscaping().create()
        return gson.toJson(listOf(toMap()))
    }

    private fun toMap(): Map<String, Any> {
        return mutableMapOf<String, Any>().apply {
            put("id", id)
            put("accountType", accountType)
            put("username", username)
            put("recordVersion", recordVersion)
            if (!profileImageUrl.isNullOrEmpty()) {
                put("profileImageUrl", profileImageUrl ?: "")
            }
            if (businessProfile != null) {
                put("businessProfile", businessProfile)
            }
            if (!email.isNullOrEmpty()) {
                put("email", email ?: "")
            }
        }
    }
}
