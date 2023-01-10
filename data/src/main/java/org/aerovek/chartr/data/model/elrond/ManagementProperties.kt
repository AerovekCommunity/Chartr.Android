package org.aerovek.chartr.data.elrondsdk.model

enum class ManagementProperty(val serializedName: String) {
    CanMint("canMint"),
    CanBurn("canBurn"),
    CanPause("canPause"),
    CanFreeze("canFreeze"),
    CanWipe("canWipe"),
    CanAddSpecialRoles("canAddSpecialRoles"),
    CanChangeOwner("canChangeOwner"),
    CanUpgrade("canUpgrade")
}
