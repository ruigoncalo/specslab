package com.ruigoncalo.specslab

import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class MainPresenter(private val runtimePermissionsDelegate: RuntimePermissionsDelegate,
                    private val distancesUseCase: DistancesUseCase) {

    private lateinit var view: View

    private val subscriptions by lazy { CompositeSubscription() }

    fun attachView(view: View) {
        this.view = view
    }

    fun start() {
        requestPermission()
    }

    fun onRationaleButtonClicked() {
        requestPermission()
    }

    fun onPermissionGranted() {
        getDistances()
    }

    fun onPermissionDenied() {
        subscriptions.add(
                runtimePermissionsDelegate.shouldShowRationale()
                        .subscribe { showRationale ->
                            if (showRationale) {
                                view.showRationale()
                            } else {
                                view.showSettings()
                            }
                        })
    }

    fun stop() {
        subscriptions.clear()
    }

    private fun requestPermission() {
        subscriptions.add(
                runtimePermissionsDelegate.checkPermission()
                        .subscribe { isGranted ->
                            if (isGranted) {
                                getDistances()
                            } else {
                                runtimePermissionsDelegate.requestPermissions()
                            }
                        })
    }

    private fun getDistances() {
        subscriptions.add(
                distancesUseCase.distances()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { distance -> view.showDistances(distance.toString()) }
        )
    }

    interface View {
        fun showRationale()
        fun showSettings()
        fun showDistances(distance: String)
    }
}