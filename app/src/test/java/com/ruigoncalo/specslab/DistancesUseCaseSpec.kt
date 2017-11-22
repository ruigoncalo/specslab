package com.ruigoncalo.specslab

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.whenever
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import rx.observers.TestSubscriber
import rx.subjects.PublishSubject

class DistancesUseCaseSpec : Spek({

    val distanceProvider: DistanceProvider = mock()
    val tested by memoized { DistancesUseCase(distanceProvider) }

    var distanceSubject: PublishSubject<List<Int>> = PublishSubject.create()
    var unitSubject: PublishSubject<Int> = PublishSubject.create()

    var testSubscriber: TestSubscriber<Int> = TestSubscriber()

    describe("a gps system") {

        beforeEachTest {
            distanceSubject = PublishSubject.create()
            unitSubject = PublishSubject.create()
            testSubscriber = TestSubscriber()

            whenever(distanceProvider.distances()).thenReturn(distanceSubject)
            whenever(distanceProvider.unit()).thenReturn(unitSubject)

            tested.distances().subscribe(testSubscriber)
        }

        afterEachTest {
            reset(distanceProvider)
        }

        context("distance update of 2 km") {
            beforeEachTest {
                distanceSubject.onNext(listOf(2, 1))
                unitSubject.onNext(0)
            }

            it("should not emit values") {
                testSubscriber.assertNoValues()
            }

            on("change units to miles") {
                unitSubject.onNext(1)

                it("should not emit values") {
                    testSubscriber.assertNoValues()
                }
            }
        }

        context("distance update of 3 km") {
            beforeEachTest {
                distanceSubject.onNext(listOf(3, 2))
                unitSubject.onNext(0)
            }

            it("should emit 3 as first value") {
                testSubscriber.assertValues(3)
            }

            context("distance update of 21 km") {
                beforeEachTest {
                    distanceSubject.onNext(listOf(21, 13))
                }

                it("should emit 12 as second value") {
                    // (3 + 21) / 2 = 12
                    testSubscriber.assertValues(3, 12)
                }

                context("distance update of 10 km") {
                    beforeEachTest {
                        distanceSubject.onNext(listOf(10, 6))
                    }

                    it("should emit 11 as third value") {
                        // (12 + 11) / 2 = 11
                        testSubscriber.assertValues(3, 12, 11)
                    }
                }
            }
        }
    }
})