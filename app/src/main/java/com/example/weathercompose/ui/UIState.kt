package com.example.weathercompose.ui

sealed class UIState<T : Any> {
    class Loading<T : Any> : UIState<T>()
    class Empty<T: Any> : UIState<T>()
    data class Content<T : Any>(val data: T) : UIState<T>()
    data class Error<T : Any>(val message: String) : UIState<T>()
}