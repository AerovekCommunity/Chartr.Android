package org.aerovek.chartr.data.model.elrond.esdt

import org.aerovek.chartr.data.model.elrond.address.Address

data class EsdtSpecialRoles(
    val addresses: Map<Address, List<EsdtSpecialRole>>
)
