package io.irfanshadikrishad.slotify.models

data class Slot(
    val id: String = "",
    val adminId:String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val bookedBy: String? = null
)
