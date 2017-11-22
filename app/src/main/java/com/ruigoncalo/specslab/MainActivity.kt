package com.ruigoncalo.specslab

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainPresenter.View {

    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.attachView(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    fun checkPermission() =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun shouldShowRationale() =
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)

    fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
    }

    override fun showRationale() {
        status_text.text = "By knowing your location we can tell you how close happiness is!"
        button.text = "Tell me now"

        button.setOnClickListener { presenter.onRationaleButtonClicked() }
    }

    override fun showSettings() {
        status_text.text = "Happiness is not far away. Change your permissions to find out."
        button.text = "Open settings"
    }

    override fun showDistances(distance: String) {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.none { it == PackageManager.PERMISSION_DENIED }) {
            presenter.onPermissionGranted()
        } else {
            presenter.onPermissionDenied()
        }
    }
}
