package net.braniumacademy.musicapplication

interface ResultCallback<T> {
    fun onResult(result: T)
}