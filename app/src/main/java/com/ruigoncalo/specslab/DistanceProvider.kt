package com.ruigoncalo.specslab

import rx.Observable

open class DistanceProvider {

    open fun distances(): Observable<IntArray> {
        return Observable.empty()
    }

    open fun unit(): Observable<Int> {
        return Observable.empty()
    }
}