package com.example.cednik.extensions

fun Double.round(): String {
    return String.format("%.2f", this)
}