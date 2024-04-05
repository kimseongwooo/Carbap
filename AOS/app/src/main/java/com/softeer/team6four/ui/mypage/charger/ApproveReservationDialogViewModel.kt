package com.softeer.team6four.ui.mypage.charger

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softeer.team6four.data.Resource
import com.softeer.team6four.data.UserPreferencesRepository
import com.softeer.team6four.data.ReservationRepository
import com.softeer.team6four.data.remote.charger.model.ApproveReservationDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ApproveReservationDialogViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val reservationRepository: ReservationRepository
) : ViewModel() {
    private val _reservationId: MutableStateFlow<Int> = MutableStateFlow(0)
    val reservationId: StateFlow<Int> = _reservationId

    private val _reservationDetail: MutableStateFlow<ApproveReservationDetail> = MutableStateFlow(ApproveReservationDetail())
    val reservationDetail: StateFlow<ApproveReservationDetail> = _reservationDetail

    private val _updateResult = MutableStateFlow(false)
    val updateResult: StateFlow<Boolean> = _updateResult

    fun updateReservationId(reservationId: Int) {
        _reservationId.value = reservationId
    }

    fun updateReservationDetail(guestNickname: String, rentalDate: String, rentalTime: String, totalFee: Int) {
        _reservationDetail.value = ApproveReservationDetail(guestNickname, rentalDate, rentalTime, totalFee)
    }

    fun updateReservationState(reservationId: Int, stateType: String) {
        viewModelScope.launch {
            val accessToken = userPreferencesRepository.getAccessToken().first()
            val updateReservationStatusResult = reservationRepository.updateReservationStatus(
                accessToken = accessToken,
                reservationId = reservationId,
                status = stateType
            )

            updateReservationStatusResult.catch {
                Log.e("updateReservationStatus", "updateReservationState: $it")
            }.collect {
                when (it) {
                    is Resource.Success -> {
                        _updateResult.value = true
                        Log.i("updateReservationStatus", "updateReservationState: ${it.data}")
                    }

                    is Resource.Error -> {
                        Log.e("updateReservationStatus", "updateReservationState: ${it.message}")
                    }

                    else -> {
                        Log.e("updateReservationStatus", "updateReservationState: loading")
                    }
                }
            }
        }
    }

}