package com.example.talentara.data.model.result

sealed class Results<out R> private constructor(){
    data class Success<out T>(val data: T): Results<T>()
    data class Error(val error: String): Results<Nothing>()
    data object Loading: Results<Nothing>()
}