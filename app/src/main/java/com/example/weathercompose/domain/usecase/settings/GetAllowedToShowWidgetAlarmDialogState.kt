package com.example.weathercompose.domain.usecase.settings

import com.example.weathercompose.data.datastore.AppSettings
import kotlinx.coroutines.flow.Flow

class GetAllowedToShowWidgetAlarmDialogState(
    private val appSettings: AppSettings,
) {

    operator fun invoke(): Flow<Boolean> {
        return appSettings.allowedToShowWidgetAlarmDialogState
    }
}