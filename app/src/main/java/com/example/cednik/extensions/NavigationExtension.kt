package com.example.cednik.extensions

import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController

fun <T> NavController.getValue(key: String): MutableLiveData<T>? {
    return this.currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
}

fun <T> NavController.removeValue(key: String) {
    this.currentBackStackEntry
        ?.savedStateHandle
        ?.remove<T>(key)


}


