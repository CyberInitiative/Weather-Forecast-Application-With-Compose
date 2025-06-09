package com.example.weathercompose.domain.usecase.settings

import com.example.weathercompose.data.datastore.AppSettings

class SetAllowedToShowWidgetAlarmDialogState(
    private val appSettings: AppSettings
) {

    suspend operator fun invoke(allowedToShowWidgetAlarmDialogState: Boolean) {
        appSettings.setAllowedToShowWidgetAlarmDialogState(
            isAllowedToShowWidgetAlarmDialog = allowedToShowWidgetAlarmDialogState
        )
    }
}