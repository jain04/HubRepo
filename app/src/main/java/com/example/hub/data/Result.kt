package com.example.hub.data

// Sealed class representing the result of an operation
sealed class Result<out T> {

    // Success state with data of type T
    data class Success<out T>(val data: T) : Result<T>()

    // Error state with an optional message
    data class Error(val message: String) : Result<Nothing>()
}
