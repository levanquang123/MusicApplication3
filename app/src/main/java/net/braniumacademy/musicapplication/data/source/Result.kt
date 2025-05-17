package net.braniumacademy.musicapplication.data.source

import java.lang.Exception

sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Exception) : Result<T>()
}