package com.voxeet.think360.interfaces

sealed class Result<out T : Any>  {
    object InProgress : Result<Nothing>()
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}