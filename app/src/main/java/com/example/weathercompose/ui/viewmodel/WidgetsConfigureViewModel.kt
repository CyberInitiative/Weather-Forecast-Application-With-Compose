package com.example.weathercompose.ui.viewmodel

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.domain.mapper.LocationItemMapper
import com.example.weathercompose.domain.usecase.location.FindAllLocationsUseCase
import com.example.weathercompose.domain.usecase.settings.GetAllowedToShowWidgetAlarmDialogState
import com.example.weathercompose.domain.usecase.settings.SetAllowedToShowWidgetAlarmDialogState
import com.example.weathercompose.ui.model.LocationOptionItem
import com.example.weathercompose.widget.PrefKeys
import com.example.weathercompose.widget.WidgetTemperatureUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WidgetsConfigureViewModel(
    private val getAllowedToShowWidgetAlarmDialogState: GetAllowedToShowWidgetAlarmDialogState,
    private val setAllowedToShowWidgetAlarmDialogState: SetAllowedToShowWidgetAlarmDialogState,
    private val findAllLocationsUseCase: FindAllLocationsUseCase,
    private val locationItemMapper: LocationItemMapper,
) : ViewModel() {
    private val _locationOptionsState = MutableStateFlow<List<LocationOptionItem>>(emptyList())
    val locationOptionsState: StateFlow<List<LocationOptionItem>>
        get() = _locationOptionsState.asStateFlow()

    private val _selectedLocationId = MutableStateFlow<Long?>(null)
    val selectedLocationId: StateFlow<Long?> get() = _selectedLocationId.asStateFlow()

    private val _selectedWidgetTemperatureUnit = MutableStateFlow(WidgetTemperatureUnit.CELSIUS)
    val selectedWidgetTemperatureUnit: StateFlow<WidgetTemperatureUnit>
        get() = _selectedWidgetTemperatureUnit

    init {
        viewModelScope.launch {
            _locationOptionsState.value = findAllLocationsUseCase()
                .map { location ->
                    locationItemMapper.mapToLocationOption(location)
                }.also { locations ->
                    if (locations.isNotEmpty()) {
                        _selectedLocationId.value = locations.first().id
                    }
                }
        }
    }

    suspend fun loadWidgetState(context: Context, glanceId: GlanceId) {
        val preferences = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
        preferences[PrefKeys.LOCATION_ID_KEY]?.let {
            _selectedLocationId.value = it
        }
        preferences[PrefKeys.TEMPERATURE_UNIT_KEY]?.let {
            _selectedWidgetTemperatureUnit.value = WidgetTemperatureUnit.valueOf(it)
        }
    }

    suspend fun saveWidgetState(context: Context, glanceId: GlanceId) {
        updateAppWidgetState(context, glanceId) { preferences ->
            preferences[PrefKeys.LOCATION_ID_KEY] = _selectedLocationId.value ?: 0L
            preferences[PrefKeys.TEMPERATURE_UNIT_KEY] = _selectedWidgetTemperatureUnit.value.name
        }
    }

    fun selectLocation(selectedLocationId: Long) {
        if (_selectedLocationId.value != selectedLocationId) {
            _selectedLocationId.value = selectedLocationId
        }
    }

    fun selectTemperatureUnt(widgetTemperatureUnit: WidgetTemperatureUnit) {
        if (_selectedWidgetTemperatureUnit.value != widgetTemperatureUnit) {
            _selectedWidgetTemperatureUnit.value = widgetTemperatureUnit
        }
    }

    fun getAllowedToShowWidgetAlarmDialogState(): Flow<Boolean> {
        return getAllowedToShowWidgetAlarmDialogState.invoke()
    }

    suspend fun setAllowedToShowWidgetAlarmDialogState(isAllowedToShowWidgetAlarmDialog: Boolean) {
        setAllowedToShowWidgetAlarmDialogState.invoke(
            allowedToShowWidgetAlarmDialogState = isAllowedToShowWidgetAlarmDialog
        )
    }
}