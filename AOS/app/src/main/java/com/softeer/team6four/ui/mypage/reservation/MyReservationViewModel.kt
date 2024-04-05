package com.softeer.team6four.ui.mypage.reservation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softeer.team6four.data.ReservationRepository
import com.softeer.team6four.data.Resource
import com.softeer.team6four.data.UserPreferencesRepository
import com.softeer.team6four.data.remote.reservation.model.ReservationInfoListModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyReservationViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val reservationRepository: ReservationRepository
) : ViewModel() {
    private var _myReservationHistory: MutableStateFlow<Resource<ReservationInfoListModel>> =
        MutableStateFlow(Resource.Loading())
    val myReservationHistory: StateFlow<Resource<ReservationInfoListModel>> = _myReservationHistory


    private var _sortType : MutableStateFlow<String> = MutableStateFlow("WAIT")
    val sortType : StateFlow<String> = _sortType

    fun fetchMyReservationHistory(lastReservationId: Int? = null) {
        viewModelScope.launch {
            val accessToken = userPreferencesRepository.getAccessToken().first()
            val myReservationHistoryData = reservationRepository.getMyReservationHistory(
                accessToken,
                sortType.value,
                lastReservationId
            )

            myReservationHistoryData.catch {
                Log.e("MyReservationViewModel", "getMyReservationHistory: $this")
            }.collect { reservationHistory ->
                _myReservationHistory.value = reservationHistory
            }
        }
    }

    fun updateSortType(type : String) {
        _sortType.value = type
    }
}