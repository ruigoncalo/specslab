package com.ruigoncalo.specslab

import rx.Observable

fun <T> Observable<Boolean>.mapIf(bodyTrue: () -> Observable<T>, bodyFalse: () -> Observable<T>): Observable<T> {
    return this.flatMap {
        if (it) bodyTrue.invoke() else bodyFalse.invoke()
    }
}