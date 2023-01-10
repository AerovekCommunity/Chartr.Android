package org.aerovek.chartr.data.model.elrond.esdt

import org.aerovek.chartr.data.model.elrond.account.AccountToken


internal data class GetAllEsdtResponse(
    val esdts: Map<String /* tokenIdentifier */, AccountToken>
)
