package com.apptaura.planetdiscoveries.data

sealed class WorkResult<out R> {
    data class Success<out T>(val data: T): WorkResult<T>()
    data class Error(val exception: Exception): WorkResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}

val WorkResult<*>.succeeded
    get() = this is WorkResult.Success && data != null
