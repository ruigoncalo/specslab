package com.ruigoncalo.specslab

import rx.Observable

interface PermissionsManager {

    fun checkPermission(): Observable<Boolean>

    fun requestPermissions() : Observable<Boolean>

    fun shouldShowRationale(): Observable<Boolean>
}