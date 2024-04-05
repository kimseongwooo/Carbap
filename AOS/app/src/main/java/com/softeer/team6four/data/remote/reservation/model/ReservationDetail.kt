package com.softeer.team6four.data.remote.reservation.model

data class ReservationDetail(
    val reservationId: Int = 0,
    val guestNickname: String = "",
    val rentalDate: String = "",
    val rentalTime: String = "",
    val totalFee: Int = 0
)