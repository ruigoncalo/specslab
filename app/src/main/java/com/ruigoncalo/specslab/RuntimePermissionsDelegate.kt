package com.ruigoncalo.specslab

import rx.Observable

interface RuntimePermissionsDelegate {

    fun checkPermission(): Observable<Boolean>

    fun requestPermissions()

    fun shouldShowRationale(): Observable<Boolean>
}