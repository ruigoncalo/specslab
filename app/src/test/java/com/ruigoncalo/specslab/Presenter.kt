package com.ruigoncalo.specslab

import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class Presenter(private val permissionsManager: PermissionsManager,
                private val distanceProvider: DistanceProvider) {

    private lateinit var view: View

    private val subscriptions by lazy { CompositeSubscription() }

    fun attachView(view: View) {
        this.view = view
    }

    fun start() {
        subscriptions.add(
                permissionsManager.checkPermission()
                        .subscribe { isGranted ->
                            if (isGranted) {
                                getDistances()
                            } else {
                                requestPermission()
                            }
                        })
    }

    private fun requestPermission() {
        subscriptions.add(
                permissionsManager.requestPermissions()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { isGranted ->
                            if (isGranted) {
                                getDistances()
                            } else {
                                view.showError()
                            }
                        }
        )
    }

    private fun getDistances() {
        subscriptions.add(
                distanceProvider.distances()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { distance -> view.showDistances(distance.toString()) }
        )
    }

    fun stop() {
        subscriptions.clear()
    }

    interface View {
        fun showDistances(distance: String)
        fun showError()
    }

}