package com.softeer.team6four.ui.home

import android.text.Editable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.InfoWindow
import com.softeer.team6four.data.Resource
import com.softeer.team6four.data.UserPreferencesRepository
import com.softeer.team6four.data.ChargerRepository
import com.softeer.team6four.data.remote.charger.model.BottomSheetChargerModel
import com.softeer.team6four.data.remote.charger.model.ChargerDetailModel
import com.softeer.team6four.data.remote.charger.model.MapChargerModel
import com.softeer.team6four.data.FcmRepository
import com.softeer.team6four.data.GeoCodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val geoCodeRepository: GeoCodeRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val fcmRepository: FcmRepository,
    private val chargerRepository: ChargerRepository
) : ViewModel() {
    private var addressText: MutableStateFlow<String> = MutableStateFlow("")

    private var _nickname: MutableStateFlow<String> = MutableStateFlow("닉네임")
    val nickname = _nickname

    private var _userLatLng: MutableStateFlow<LatLng> =
        MutableStateFlow(LatLng(0.toDouble(), 0.toDouble()))
    private val userLatLng: StateFlow<LatLng> = _userLatLng

    private var _searchAddressLatLng = MutableStateFlow(LatLng(0.toDouble(), 0.toDouble()))
    val searchAddressLatLng: StateFlow<LatLng> = _searchAddressLatLng

    private var _searchMarkerLatLng = MutableStateFlow(LatLng(0.toDouble(), 0.toDouble()))
    private val searchMarkerLatLng: StateFlow<LatLng> = _searchMarkerLatLng

    private var _mapChargerList: MutableStateFlow<List<MapChargerModel>> = MutableStateFlow(
        emptyList()
    )
    val mapChargerList: StateFlow<List<MapChargerModel>> = _mapChargerList

    private var _listSize: MutableStateFlow<Int> = MutableStateFlow(0)
    val listSize: StateFlow<Int> = _listSize

    private var _bottomSheetChargerList: MutableStateFlow<List<BottomSheetChargerModel>> =
        MutableStateFlow(
            emptyList()
        )
    val bottomSheetChargerList: StateFlow<List<BottomSheetChargerModel>> = _bottomSheetChargerList

    private var _selectedChargerId: MutableStateFlow<Long> = MutableStateFlow(0)
    private val selectedChargerId: StateFlow<Long> = _selectedChargerId

    private var _selectedCharger: MutableStateFlow<ChargerDetailModel> = MutableStateFlow(
        ChargerDetailModel(
            address = "",
            chargerId = 0,
            chargerType = "",
            description = "",
            distance = 0.0,
            feePerHour = "",
            imageUrl = "",
            installType = "",
            nickname = "",
            speedType = ""
        )
    )
    val selectedCharger: StateFlow<ChargerDetailModel> = _selectedCharger

    private var _currentInfoWindows: MutableStateFlow<List<InfoWindow>> = MutableStateFlow(
        emptyList()
    )
    private val currentInfoWindows: StateFlow<List<InfoWindow>> = _currentInfoWindows

    init {
        updateNickname()
    }

    fun updateAddressText(address: Editable) {
        addressText.value = address.toString()
    }

    fun getCoordinate() {
        viewModelScope.launch {
            geoCodeRepository.getCoordinateResult(addressText.value).collect { latLngResult ->
                latLngResult.onSuccess { latLng ->
                    _searchAddressLatLng.value = LatLng(0.0, 0.0)
                    _searchAddressLatLng.value = latLng
                    updateSearchMarkerLatLng(latLng)
                }
            }
        }
    }

    fun updateSearchMarkerLatLng(latLng: LatLng) {
        _searchMarkerLatLng.value = latLng
    }

    fun sendFcmToken(fcmToken: String) {
        viewModelScope.launch {
            val accessToken = userPreferencesRepository.getAccessToken().first()
            val result = fcmRepository.postToken(accessToken, fcmToken)
            result.collect { resource ->
                if (resource is Resource.Error) {
                    Log.e("fcmTokenApi Error", resource.message)
                }
            }
        }
    }

    private fun updateNickname() {
        viewModelScope.launch {
            userPreferencesRepository.getNickname().collect { nickname ->
                if (nickname != "") {
                    _nickname.value = nickname
                }
            }
        }
    }

    fun fetchMapChargerList() {
        viewModelScope.launch {
            val token = userPreferencesRepository.getAccessToken().first()
            chargerRepository.fetchMapChargerModelList(
                token, searchMarkerLatLng.value.latitude, searchMarkerLatLng.value.longitude
            ).collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        Log.e("fetchMapChargerList", resource.message)
                    }

                    is Resource.Success -> {
                        _mapChargerList.value = resource.data
                    }

                    else -> {}
                }
            }
        }
    }

    fun fetchBottomSheetChargerList(type: String? = null) {
        viewModelScope.launch {
            val token = userPreferencesRepository.getAccessToken().first()

            chargerRepository.fetchBottomSheetChargerList(
                token,
                searchMarkerLatLng.value.latitude,
                searchMarkerLatLng.value.longitude,
                type
            ).collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        Log.e("fetchBottomChargerList", resource.message)
                    }

                    is Resource.Success -> {
                        _bottomSheetChargerList.value = resource.data
                        _listSize.value = bottomSheetChargerList.value.size
                    }

                    else -> {}
                }
            }
        }
    }

    fun clearInfoWindows() {
        currentInfoWindows.value.forEach { infoWindow ->
            infoWindow.close()
            infoWindow.map = null
        }
    }

    fun updateInfoWindows(infoWindows: List<InfoWindow>) {
        _currentInfoWindows.value = infoWindows
    }

    fun updateSelectedCharger(id: Long) {
        _selectedChargerId.value = id
        updateSelectedCharger()
    }

    fun updateUserLatLng(latitude: Double, longitude: Double) {
        _userLatLng.value = LatLng(latitude, longitude).toLatLng()
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.allClear()
            _nickname.value = ""
        }
    }

    fun updateSelectedCharger() {
        viewModelScope.launch {
            val token = userPreferencesRepository.getAccessToken().first()
            chargerRepository.fetchChargerDetail(
                token,
                selectedChargerId.value,
                userLatLng.value.latitude,
                userLatLng.value.longitude
            ).collect { resource ->
                if (resource is Resource.Success) {
                    _selectedCharger.value = resource.data
                } else if (resource is Resource.Error) {
                    Log.e("selectedChargerError", resource.message)
                }
            }
        }
    }
}