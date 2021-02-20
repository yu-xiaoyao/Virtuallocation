package me.yuxiaoyao.virtuallocation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import me.yuxiaoyao.virtuallocation.manager.VirtualLocationManager

private const val FINE_LOCATION_REQ_CODE = 0x1234

class CheckEnableActivity : AppCompatActivity() {

    private lateinit var virtualLocationManager: VirtualLocationManager

    private lateinit var virtualLocationFailed: RelativeLayout
    private lateinit var virtualLocationSuccess: RelativeLayout
    private lateinit var reCheckVirtualLocation: TextView
    private lateinit var goToSetting: TextView
    private val handler = Handler(Looper.myLooper()!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_enable)

        virtualLocationFailed = findViewById(R.id.virtualLocationFailed)
        virtualLocationSuccess = findViewById(R.id.virtualLocationSuccess)
        reCheckVirtualLocation = findViewById(R.id.reCheckVirtualLocation)
        goToSetting = findViewById(R.id.goToSetting)

        configViewListener()

        virtualLocationManager = VirtualLocationManager.get(this)

        val checkLocationPermission = checkLocationPermission()

        if (checkLocationPermission) {
            // 有定位的权限
            if (virtualLocationManager.checkEnableVirtualLocation()) {
                // 已开启模拟定位
                virtualLocationFailed.visibility = View.GONE
                checkLocationAndStart()
            } else {
                // 没有开启模拟定位
                virtualLocationSuccess.visibility = View.GONE
            }
        }
    }

    private fun configViewListener() {
        goToSetting.setOnClickListener {
            goToSetting()
        }
        reCheckVirtualLocation.setOnClickListener {
            if (checkLocationPermission()) {
                if (virtualLocationManager.checkEnableVirtualLocation()) {
                    virtualLocationFailed.visibility = View.GONE
                    checkLocationAndStart()
                } else {
                    virtualLocationSuccess.visibility = View.GONE
                    Toast.makeText(this, R.string.confirm_mock, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        virtualLocationManager.stopVirtualLocation()
    }

    private fun goToSetting() {
        val intent = Intent(Settings.ACTION_SETTINGS)
        startActivity(intent)
    }

    private fun checkLocationAndStart() {
        val checkLocationPermission = checkLocationPermission()
        if (checkLocationPermission) {
            handler.postDelayed({ startDelayActivity() }, 1000L)
        }
    }

    private fun startDelayActivity() {
        val fineLocation =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasFineLocationPermission = fineLocation == PackageManager.PERMISSION_GRANTED
        //  开启Activity
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(HAS_FINE_LOCATION, hasFineLocationPermission)
        startActivity(intent)
        finish()
    }

    private fun checkLocationPermission(): Boolean {
        val fineLocation =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), FINE_LOCATION_REQ_CODE
            )
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOCATION_REQ_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限申请成功
                if (virtualLocationManager.checkEnableVirtualLocation()) {
                    virtualLocationFailed.visibility = View.GONE
                    virtualLocationSuccess.visibility = View.VISIBLE
                    checkLocationAndStart()
                } else {
                    virtualLocationFailed.visibility = View.VISIBLE
                    virtualLocationSuccess.visibility = View.GONE
                }

            } else {
                Toast.makeText(this, "没有定位权限", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
