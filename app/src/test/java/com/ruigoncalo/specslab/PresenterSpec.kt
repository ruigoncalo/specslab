package com.ruigoncalo.specslab

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import rx.Observable
import rx.subjects.PublishSubject

class PresenterSpec : Spek({

    val distancesProvider: DistanceProvider = mock()
    val permissionsManager: PermissionsManager = mock()
    val view: Presenter.View = mock()

    val tested by memoized {
        Presenter(permissionsManager, distancesProvider)
                .apply { attachView(view) }
    }

    val permissionPubSub: PublishSubject<Boolean> by memoized {  PublishSubject.create<Boolean>() }

    val requestPubSub: PublishSubject<Boolean> by memoized { PublishSubject.create<Boolean>() }

    rxGroup("Presenter") {

        beforeEachTest {
            whenever(permissionsManager.checkPermission()).thenReturn(permissionPubSub)
            whenever(permissionsManager.requestPermissions()).thenReturn(requestPubSub)
            whenever(distancesProvider.distances()).thenReturn(Observable.just(IntArray(10)))
        }

        afterEachTest {
            reset(distancesProvider)
            reset(view)
        }

        describe("start") {
            beforeEachTest {
                tested.start()
            }

            it("should check permission") {
                verify(permissionsManager).checkPermission()
            }

            context("permission is granted") {
                beforeEachTest {
                    permissionPubSub.onNext(true)
                }

                it("should get distances") {
                    verify(distancesProvider).distances()
                }
            }

            context("permission is denied") {
                beforeEachTest {
                    permissionPubSub.onNext(false)
                }

                it("should request permission") {
                    verify(permissionsManager).requestPermissions()
                }

                given("a permission request") {


                    on("permission granted") {
                        requestPubSub.onNext(true)

                        it("should get distances") {
                            verify(distancesProvider).distances()
                        }
                    }

                    on("permission denied") {
                        requestPubSub.onNext(true)

                        it("should show error") {
                            verify(view).showError()
                        }
                    }
                }
            }
        }
    }


})