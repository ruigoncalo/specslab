package com.ruigoncalo.specslab

import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class DistancePresenter constructor(private val distanceProvider: DistanceProvider) {

    private val subscriptions = CompositeSubscription()

    private lateinit var view: View

    fun attachView(view: View) {
        this.view = view
    }

    fun fetchDistances() {
        subscriptions.add(
                Observable.combineLatest(
                        distanceProvider.distances(),
                        distanceProvider.unit(),
                        { distances, unit -> distances[unit] })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Subscriber<Int>() {
                            override fun onNext(t: Int) {
                                view.displayDistance(t.toString())
                            }

                            override fun onError(e: Throwable) {
                                view.displayError()
                            }

                            override fun onCompleted() {
                                view.displayComplete()
                            }
                        })
        )
    }

    fun fetchDistancesFiltered() {
        subscriptions.add(
                Observable.combineLatest(
                        distanceProvider.distances(),
                        distanceProvider.unit(),
                        { distances, unit -> distances[unit] })
                        .filter { value -> value > 5 }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Subscriber<Int>() {
                            override fun onNext(t: Int) {
                                view.displayDistance(t.toString())
                            }

                            override fun onError(e: Throwable) {
                                view.displayError()
                            }

                            override fun onCompleted() {
                                view.displayComplete()
                            }
                        })
        )
    }

    fun fetchCombo() {
        subscriptions.add(
                Observable.zip(
                        distanceProvider.distances(),
                        distanceProvider.unit(),
                        { distances, unit -> distances[unit] })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Subscriber<Int>() {
                            override fun onNext(t: Int) {
                                view.displayDistance(t.toString())
                            }

                            override fun onError(e: Throwable) {
                                view.displayError()
                            }

                            override fun onCompleted() {
                                view.displayComplete()
                            }
                        })
        )
    }

    fun stop() {
        subscriptions.clear()
    }


    interface View {
        fun displayDistance(value: String)
        fun displayError()
        fun displayComplete()
    }
}