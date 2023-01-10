package org.aerovek.chartr.data.repository.elrond

import org.aerovek.chartr.data.model.elrond.esdt.EsdtProperties
import org.aerovek.chartr.data.model.elrond.esdt.EsdtSpecialRoles
import org.aerovek.chartr.data.model.elrond.esdt.EsdtToken

interface EsdtRepository {

    /** Get ESDT token details for the tokenId passed in */
    fun getTokenDetails(tokenId: String): EsdtToken

    /** Get all issued ESDT tokens */
    fun getAllEsdtIssued(): List<String>

    /** Get ESDT token properties */
    fun getEsdtProperties(tokenIdentifier: String): EsdtProperties

    /** Get special roles for a token */
    fun getEsdtSpecialRoles(tokenIdentifier: String): EsdtSpecialRoles?
}
