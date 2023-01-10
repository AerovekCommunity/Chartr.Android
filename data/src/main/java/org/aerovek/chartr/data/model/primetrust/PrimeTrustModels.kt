package org.aerovek.chartr.data.model.primetrust

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName


//region $CreateUser

data class PTCreateUserData(
    val data: PTUser
)

data class PTUser(
    val type: String,
    val attributes: PTCreateUserAttributes
)

data class PTCreateUserAttributes(
    val email: String,
    val name: String,
    val password: String
)

data class PTCreateUserResponseData(
    val data: PTCreateUserResponse
)

data class PTCreateUserResponse(
    val type: String,
    val id: String,
    val attributes: PTCreateUserResponseAttributes,
    val links: PTCreateUserResponseLinks,
    val relationships: PTCreateUserResponseRelationships
)

data class PTCreateUserResponseAttributes(
    val claims: Any?,
    @SerializedName("created-at")
    val createdAt: String,
    val disabled: Boolean,
    val email: String,
    val mfaTypes: List<Any>?,
    val name: String,
    @SerializedName("updated-at")
    val updatedAt: String
)

data class PTCreateUserResponseLinks(
    val self: String
)

data class PTCreateUserResponseRelationships(
    val userGroups: PTCreateUserResponseUserGroups
)

data class PTCreateUserResponseUserGroups(
    val links: PTCreateUserResponseUserGroupsLinks
)

data class PTCreateUserResponseUserGroupsLinks(
    val related: String
)
//endregion

//region Create JWT
data class PTCreateJwtRequest(
    /** This should be in this date and time form 2022-06-02T12:12:48-07:00 */
    @SerializedName("expires-at")
    val expiresAt: String
)

data class PTCreateJwtResponse(
    val token: String
)
//endregion
