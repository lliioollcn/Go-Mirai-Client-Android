package cn.lliiooll.gmca.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cn.lliiooll.gmca.service.GMCService
import cn.lliiooll.gmca.ui.components.GMCStatus
import cn.lliiooll.gmca.ui.components.PTitleBar
import cn.lliiooll.gmca.ui.components.StatusBar
import cn.lliiooll.gmca.ui.theme.GoMiraiClientAndroidTheme
import cn.lliiooll.gmca.utils.GMCUtils
import cn.lliiooll.gmca.utils.checkAndroidVersion
import cn.lliiooll.gmca.utils.checkNotifyPermission
import cn.lliiooll.gmca.utils.checkPermissions
import cn.lliiooll.gmca.utils.isInBatteryWhiteList
import cn.lliiooll.gmca.utils.makeToastLong
import cn.lliiooll.gmca.utils.makeToastSort
import cn.lliiooll.gmca.utils.requestJoinBatteryWhiteList
import cn.lliiooll.gmca.utils.requestNotifyPermission
import cn.lliiooll.gmca.utils.requestPermissions

class MainActivity : ComponentActivity() {

    private val REQUEST_CODE = 0x3c
    private val NOTIFY_REQUEST_CODE = 0x3d
    private val BATTERT_REQUEST_CODE = 0x3e
    private val permissions = arrayListOf(
        Manifest.permission.RECEIVE_BOOT_COMPLETED,
        Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
        Manifest.permission.WAKE_LOCK,
        Manifest.permission.INTERNET
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkAndroidVersion(Build.VERSION_CODES.TIRAMISU)) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (checkAndroidVersion(Build.VERSION_CODES.P)) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE)
        }
        setContent {
            GoMiraiClientAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn(modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()) {
                        item {
                            StatusBar()
                        }
                        item {
                            PTitleBar()
                        }
                        item {
                            GMCStatus { initGMC() }
                        }
                    }
                }

            }
        }
    }

    private fun initGMC() {
        if (!this.checkPermissions(permissions)) {
            this.requestPermissions(REQUEST_CODE, permissions)
        } else if (!this.checkNotifyPermission()) {
            this.requestNotifyPermission(NOTIFY_REQUEST_CODE)
        } else {
            if (checkAndroidVersion(Build.VERSION_CODES.M)) {
                if (!this.isInBatteryWhiteList()) {
                    this.requestJoinBatteryWhiteList(BATTERT_REQUEST_CODE)
                } else {
                    startGMC()
                }
            } else {
                startGMC()
            }
        }

    }

    private fun startGMC() {
        if (!GMCUtils.isEnable()) {
            this.makeToastLong("GMC启动中，请稍后...")
            val intent = Intent(this, GMCService::class.java)
            if (checkAndroidVersion(Build.VERSION_CODES.O)) {
                this.startForegroundService(intent)
            } else {
                this.startService(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (this.checkPermissions(this.permissions)) {
                this.makeToastSort("权限获取失败，GMC将无法在后台运行")
            } else {
                initGMC()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NOTIFY_REQUEST_CODE) {
            if (!this.checkNotifyPermission()) {
                this.makeToastSort("通知权限获取失败，GMC将无法在后台运行")
            } else {
                initGMC()
            }
        }
        if (requestCode == BATTERT_REQUEST_CODE) {
            if (!this.isInBatteryWhiteList()) {
                this.makeToastSort("加入电池优化失败，GMC将无法在后台运行")
            } else {
                initGMC()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}