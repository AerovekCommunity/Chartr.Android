package org.aerovek.chartr.data.model.plaid

import com.google.gson.annotations.SerializedName

data class LinkTokenCreateRequest(
    @SerializedName("client_name")
    val clientName: String,
    @SerializedName("client_user_id")
    val userId: String
)

data class LinkTokenCreateResponse(
    val expiration: String,
    @SerializedName("link_token")
    val linkToken: String,
    @SerializedName("request_id")
    val requestId: String
)