package com.ruigoncalo.specslab

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import rx.subjects.PublishSubject

class DistancePresenterSpec : Spek({

    val view: DistancePresenter.View = mock()
    val distanceProvider: DistanceProvider = mock()
    val tested by memoized {  DistancePresenter(distanceProvider) }

    var distanceSubject: PublishSubject<IntArray> = PublishSubject.create()
    var unitSubject: PublishSubject<Int> = PublishSubject.create()

    rxGroup("DistancePresenter") {
        beforeEachTest {
            distanceSubject = PublishSubject.create()
            unitSubject = PublishSubject.create()

            whenever(distanceProvider.distances()).thenReturn(distanceSubject)
            whenever(distanceProvider.unit()).thenReturn(unitSubject)

            tested.attachView(view)
        }

        afterEachTest {
            reset(view)
            reset(distanceProvider)

            tested.stop()
        }

        describe("fetching distances") {
            beforeEachTest {
                tested.fetchDistances()
            }

            context("first distance provided of 30 km") {
                beforeEachTest {
                    distanceSubject.onNext(intArrayOf(30, 19))
                    unitSubject.onNext(0)
                }

                it("should display 30") {
                    verify(view).displayDistance("30")
                }

                on("a distance update to 20 km") {
                    distanceSubject.onNext(intArrayOf(20, 12))
                    unitSubject.onCompleted()

                    it("should display 20") {
                        verify(view).displayDistance("20")
                    }

                    it("should not complete") {
                        verify(view, never()).displayComplete()
                    }
                }

                on("a unit update to miles") {
                    unitSubject.onNext(1)

                    it("should display 19") {
                        verify(view).displayDistance("19")
                    }

                    it("should not complete") {
                        verify(view, never()).displayComplete()
                    }
                }
            }

            context("just distance update") {
                beforeEachTest {
                    distanceSubject.onNext(intArrayOf(30, 19))
                }

                it("should not display 30") {
                    verify(view, never()).displayDistance("30")
                }

                it("should not display error") {
                    verify(view, never()).displayError()
                }

                it("should not display complete") {
                    verify(view, never()).displayComplete()
                }
            }
        }

        describe("fetching big distances") {
            beforeEachTest {
                tested.fetchDistancesFiltered()
            }

            context("first distance provided of 30 km") {
                beforeEachTest {
                    distanceSubject.onNext(intArrayOf(30, 19))
                    unitSubject.onNext(0)
                }

                it("should display 30") {
                    verify(view).displayDistance("30")
                }

                on("a distance update to 3 km") {
                    distanceSubject.onNext(intArrayOf(3, 2))

                    it("should not display 3") {
                        verify(view, never()).displayDistance("2")
                    }

                    it("should not complete") {
                        verify(view, never()).displayComplete()
                    }

                    it("should not display error") {
                        verify(view, never()).displayError()
                    }
                }
            }
        }

        describe("fetching combo distances") {
            beforeEachTest {
                tested.fetchCombo()
            }

            context("first distance provided of 30 km") {
                beforeEachTest {
                    distanceSubject.onNext(intArrayOf(30, 19))
                    unitSubject.onNext(0)
                    unitSubject.onCompleted()
                }

                it("should display 30") {
                    verify(view).displayDistance("30")
                }

                it("should complete") {
                    verify(view).displayComplete()
                }

                on("a distance update to 20 km") {
                    distanceSubject.onNext(intArrayOf(20, 12))

                    it("should not display 3") {
                        verify(view, never()).displayDistance("20")
                    }
                }
            }
        }
    }
})