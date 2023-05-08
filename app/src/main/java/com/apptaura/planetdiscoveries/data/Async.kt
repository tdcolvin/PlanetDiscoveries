package com.apptaura.planetdiscoveries.data

sealed class Async<out T> {
    object Loading : Async<Nothing>()
    data class Completed<out T>(val data: T) : Async<T>()
}