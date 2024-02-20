package com.softeer.team6four.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.softeer.team6four.data.Resource
import com.softeer.team6four.data.remote.reservation.dto.ChargerReservationListDto
import com.softeer.team6four.data.remote.reservation.dto.DateReservationInfoDto
import com.softeer.team6four.data.remote.reservation.dto.PaymentInfo
import com.softeer.team6four.data.remote.reservation.dto.ReservationConfirmationDto
import com.softeer.team6four.data.remote.reservation.dto.ReservationHistoryDto
import com.softeer.team6four.data.remote.reservation.dto.ReservationRequestDto
import com.softeer.team6four.data.remote.reservation.dto.VerificationDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReservationService {

    @GET("{carbobId}/daily")
    suspend fun getDateReservationInfo(
        @Header("Authorization") token: String,
        @Path("carbobId") carbobId: Int,
        @Path("date") date: String?
    ): Resource<DateReservationInfoDto>

    @PATCH("check/{reservationId}")
    suspend fun updateReservationState(
        @Header("Authorization") token: String,
        @Path("reservationId") reservationId: Long,
        @Header("stateType") stateType: String
    ): Resource<ReservationConfirmationDto>

    @POST("apply")
    suspend fun applyReservation(
        @Header("Authorization") token: String,
    ): Resource<ReservationRequestDto>

    @GET("verification")
    suspend fun getVerification(
        @Header("Authorization") token: String,
        @Header("cipher") cipher: String
    ): Resource<VerificationDto>

    @POST("fulfillment")
    suspend fun fulfillVerification(
        @Header("Authorization") token: String,
        @Field("reservationId") reservationId: String
    ): Resource<PaymentInfo>

    @GET("application/list")
    suspend fun getReservationHistory(
        @Header("Authorization") token: String,
        @Query("sortType") sortType: String,
        @Query("lastReservationId") lastReservationId: Int?
    ): Resource<ReservationHistoryDto>

    @GET("list/{carbobId}}")
    suspend fun getChargerReservation(
        @Header("Authorization") token: String,
        @Query("lastReservationId") lastReservationId: Int?
    ): Resource<ChargerReservationListDto>

    companion object {
        private const val BASE_URL = "http://13.125.3.169:8080/v1/reservation/"

        fun create(): ReservationService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(ReservationService::class.java)
        }
    }
}