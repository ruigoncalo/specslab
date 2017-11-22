package com.ruigoncalo.specslab

import org.jetbrains.spek.api.dsl.Pending
import org.jetbrains.spek.api.dsl.SpecBody
import rx.Scheduler
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers

inline fun SpecBody.rxGroup(description: String, pending: Pending = Pending.No, crossinline body: SpecBody.() -> Unit) {
    group(description, pending) {

        val rxAndroidSchedulersHook = object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler(): Scheduler {
                return Schedulers.immediate()
            }
        }

        beforeEachTest {
            RxAndroidPlugins.getInstance().registerSchedulersHook(rxAndroidSchedulersHook)
            RxJavaHooks.setOnIOScheduler { Schedulers.immediate() }
            RxJavaHooks.setOnComputationScheduler { Schedulers.immediate() }
            RxJavaHooks.setOnNewThreadScheduler { Schedulers.immediate() }
        }

        body()

        afterEachTest {
            RxAndroidPlugins.getInstance().reset()
            RxJavaHooks.reset()
        }
    }
}