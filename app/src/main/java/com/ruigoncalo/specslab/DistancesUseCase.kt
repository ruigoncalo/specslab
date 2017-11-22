package com.ruigoncalo.specslab

import rx.Observable

open class DistancesUseCase(private val distanceProvider: DistanceProvider) {

    open fun distances(): Observable<Int> {
        return Observable.combineLatest(
                distanceProvider.distances()
                        .filter { value -> value[0] > 2 || value[1] > 1 }
                        .scan { accum, new -> listOf((accum[0] + new[0]) / 2, (accum[1] + new[1] / 2)) },
                distanceProvider.unit(),
                { distances, unit -> distances[unit] })
    }
}