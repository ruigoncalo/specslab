package com.ruigoncalo.specslab

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.After
import org.junit.Before
import org.junit.Test
import rx.Observable

class MainPresenterTest {

    private val distancesUseCase: DistancesUseCase = mock()
    private val runtimePermissionDelegate: RuntimePermissionsDelegate = mock()
    private val view: MainPresenter.View = mock()

    private val tested = MainPresenter(runtimePermissionDelegate, distancesUseCase)
            .apply { attachView(view) }

    @Before
    fun setup() {
        whenever(runtimePermissionDelegate.checkPermission()).thenReturn(Observable.just(true))
        whenever(runtimePermissionDelegate.shouldShowRationale()).thenReturn(Observable.just(true))
        whenever(distancesUseCase.distances()).thenReturn(Observable.just(1))
    }

    @After
    fun tearDown() {
        reset(distancesUseCase)
        reset(view)
    }

    @Test
    fun on_start_check_permission() {
        tested.start()

        verify(runtimePermissionDelegate).checkPermission()
    }

    @Test
    fun if_permission_granted_get_distances() {
        tested.start()

        verify(distancesUseCase).distances()
    }

    @Test
    fun if_permission_denied_request_permission() {
        whenever(runtimePermissionDelegate.checkPermission()).thenReturn(Observable.just(false))
        tested.start()

        verify(runtimePermissionDelegate).requestPermissions()
    }

    @Test
    fun if_granted_requested_permission_get_distances() {
        tested.onPermissionGranted()

        verify(distancesUseCase).distances()
    }

    @Test
    fun if_denied_requested_permission_ask_for_rationale() {
        tested.onPermissionDenied()

        verify(runtimePermissionDelegate).shouldShowRationale()
    }

    @Test
    fun on_positive_reply_for_rationale_show_rationale() {
        whenever(runtimePermissionDelegate.shouldShowRationale()).thenReturn(Observable.just(true))
        tested.onPermissionDenied()
        
        verify(view).showRationale()
    }

    @Test
    fun on_negative_reply_for_rationale_show_settings() {
        whenever(runtimePermissionDelegate.shouldShowRationale()).thenReturn(Observable.just(false))
        tested.onPermissionDenied()

        verify(view).showSettings()
    }

}