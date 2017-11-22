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
import rx.subjects.PublishSubject

class MainPresenterSpec : Spek({

    val distancesUseCase: DistancesUseCase = mock()
    val runtimePermissionDelegate: RuntimePermissionsDelegate = mock()
    val view: MainPresenter.View = mock()

    val tested by memoized {
        MainPresenter(runtimePermissionDelegate, distancesUseCase)
                .apply { attachView(view) }
    }

    var permissionPubSub: PublishSubject<Boolean> = PublishSubject.create()
    var showRationalePubSub: PublishSubject<Boolean> = PublishSubject.create()
    var distancesPubSub: PublishSubject<Int> = PublishSubject.create()

    beforeEachTest {
        whenever(runtimePermissionDelegate.checkPermission()).thenReturn(permissionPubSub)
        whenever(runtimePermissionDelegate.shouldShowRationale()).thenReturn(showRationalePubSub)
        whenever(distancesUseCase.distances()).thenReturn(distancesPubSub)
    }

    afterEachTest {
        reset(distancesUseCase)
        reset(view)

        permissionPubSub = PublishSubject.create()
        showRationalePubSub = PublishSubject.create()
        distancesPubSub = PublishSubject.create()
    }

    describe("start") {
        beforeEachTest {
            tested.start()
        }

        it("should check permission") {
            verify(runtimePermissionDelegate).checkPermission()
        }

        context("permission is granted") {
            beforeEachTest {
                permissionPubSub.onNext(true)
            }

            it("should get distances") {
                verify(distancesUseCase).distances()
            }
        }

        context("permission is denied") {
            beforeEachTest {
                permissionPubSub.onNext(false)
            }

            it("should request permission") {
                verify(runtimePermissionDelegate).requestPermissions()
            }

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

                on("positive reply") {
                    showRationalePubSub.onNext(true)

                    it("should show rationale") {
                        verify(view).showRationale()
                    }
                }

                on("negative reply") {
                    showRationalePubSub.onNext(false)

                    it("should show settings") {
                        verify(view).showSettings()
                    }
                }
            }
        }
    }

})