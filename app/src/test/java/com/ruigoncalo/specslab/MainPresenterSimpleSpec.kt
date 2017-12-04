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
import rx.Observable

class MainPresenterSimpleSpec : Spek({

    val distancesUseCase: DistancesUseCase = mock()
    val runtimePermissionDelegate: PermissionsManager = mock()
    val view: MainPresenter.View = mock()

    val tested by memoized {
        MainPresenter(runtimePermissionDelegate, distancesUseCase)
                .apply { attachView(view) }
    }

    beforeEachTest {
        whenever(runtimePermissionDelegate.checkPermission()).thenReturn(Observable.just(true))
        whenever(runtimePermissionDelegate.shouldShowRationale()).thenReturn(Observable.just(true))
        whenever(distancesUseCase.distances()).thenReturn(Observable.just(1))
    }

    afterEachTest {
        reset(distancesUseCase)
        reset(view)
    }

    describe("start") {
        beforeEachTest {
            tested.start()
        }

        it("should check permission") {
            verify(runtimePermissionDelegate).checkPermission()
        }
    }

    context("permission is granted on start") {
        beforeEachTest {
            whenever(runtimePermissionDelegate.checkPermission()).thenReturn(Observable.just(true))
            tested.start()
        }

        it("should get distances") {
            verify(distancesUseCase).distances()
        }
    }

    context("permission is denied on start") {
        beforeEachTest {
            whenever(runtimePermissionDelegate.checkPermission()).thenReturn(Observable.just(false))
            tested.start()
        }

        it("should request permission") {
            verify(runtimePermissionDelegate).requestPermissions()
        }
    }

    describe("request permission") {

        given("permission granted") {
            beforeEachTest {
                tested.onPermissionGranted()
            }

            it("should get distances") {
                verify(distancesUseCase).distances()
            }
        }

        given("permission denied") {
            beforeEachTest {
                tested.onPermissionDenied()
            }

            it("should ask for rationale") {
                verify(runtimePermissionDelegate).shouldShowRationale()
            }
        }
    }

    describe("permission denied") {

        context("on positive reply for rationale") {
            beforeEachTest {
                whenever(runtimePermissionDelegate.shouldShowRationale()).thenReturn(Observable.just(true))
                tested.onPermissionDenied()
            }

            it("should show rationale") {
                verify(view).showRationale()
            }
        }

        context("on negative reply for rationale") {
            beforeEachTest {
                whenever(runtimePermissionDelegate.shouldShowRationale()).thenReturn(Observable.just(false))
                tested.onPermissionDenied()
            }

            it("should show settings") {
                verify(view).showSettings()
            }
        }
    }
})