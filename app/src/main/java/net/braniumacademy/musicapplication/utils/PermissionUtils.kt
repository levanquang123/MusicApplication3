package net.braniumacademy.musicapplication.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object PermissionUtils {
    private val _permissionAsked = MutableLiveData<Boolean>()
    private val _permissionGranted = MutableLiveData<Boolean>()
    var isRegistered: Boolean = false
    val permissionAsked: LiveData<Boolean> = _permissionAsked
    val permissionGranted: LiveData<Boolean> = _permissionGranted

    fun askPermission() {
        _permissionAsked.value = true
    }

    fun grantPermission() {
        _permissionGranted.value = true
    }
}