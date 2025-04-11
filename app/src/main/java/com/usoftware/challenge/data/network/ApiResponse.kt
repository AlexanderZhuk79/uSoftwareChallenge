package com.usoftware.challenge.data.network


sealed interface ApiResponse<T> {
    data class Success<T>(val data: T): ApiResponse<T>
    data class Failure<T>(val exception: Exception): ApiResponse<T>
}


inline fun <reified T> apiCall(apiCall: () -> T): ApiResponse<T> =
    try {
        ApiResponse.Success(data = apiCall())
    } catch (e: Exception){
        ApiResponse.Failure(exception = CustomResponseException("Network error"))
    }


open class CustomResponseException(
    cachedResponseText: String
) : IllegalStateException(cachedResponseText)