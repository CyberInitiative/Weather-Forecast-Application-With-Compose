package com.example.weathercompose.data.api

sealed class ResponseResult<T : Any> {
    data class Success<T : Any>(val data: T) : ResponseResult<T>()
    data class Error<T : Any>(val code: Int, val message: String?) : ResponseResult<T>() {
        fun buildErrorMessage(): String {
            return buildString {
                append("Response failed with $code code. ")
                if (message != null) {
                    append("Error message: $message")
                }
            }
        }
    }

    data class Exception<T : Any>(val throwable: Throwable) : ResponseResult<T>() {
        fun buildExceptionMessage(): String {
            return if (throwable.message != null) {
                "Response failed with exception. Exception message: ${throwable.message}"
            } else {
                "Response failed with exception."
            }
        }
    }
}