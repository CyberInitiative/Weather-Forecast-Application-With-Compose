package com.example.weathercompose.data.api

sealed class ResponseResult<T : Any> {
    data class Success<T : Any>(val data: T) : ResponseResult<T>()
    data class Error<T : Any>(val code: Int, val message: String?) : ResponseResult<T>()
    data class Exception<T : Any>(val throwable: Throwable) : ResponseResult<T>()
}