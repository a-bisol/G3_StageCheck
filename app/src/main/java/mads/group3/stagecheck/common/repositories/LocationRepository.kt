package mads.group3.stagecheck.common.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mads.group3.stagecheck.models.LocationData

object LocationRepository {
    private val _location = MutableStateFlow(LocationData())
    val location: StateFlow<LocationData> = _location.asStateFlow()

    fun updateLocation(city: String, state: String, country: String) {
        _location.value = LocationData(city, state, country)
    }
}