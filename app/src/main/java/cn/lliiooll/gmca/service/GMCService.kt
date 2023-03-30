package cn.lliiooll.gmca.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import cn.lliiooll.gmca.utils.buildNotify
import cn.lliiooll.gmca.utils.checkOrMkdirs
import gmc_android.Gmc_android
import java.util.Calendar
import kotlin.concurrent.thread

class GMCService : Service() {

    private var lock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        val runDir = getExternalFilesDir("gmcRun")
        runDir?.checkOrMkdirs()
        Gmc_android.chdir(runDir?.absolutePath)
        Gmc_android.setLogger {
            //TODO: 日志转储
        }
        thread {
            Gmc_android.start()
        }
        startForeground(1, this.buildNotify("GMC", "GMC运行状态", "gmc_channel"))
    }

    override fun onDestroy() {
        if (this.lock != null) {
            if (this.lock?.isHeld!!) {
                this.lock?.release()
            }
            this.lock = null
        }
        stopForeground(true)
        super.onDestroy()
    }

    @Synchronized
    fun getLock() {
        if (this.lock == null) {
            val powerManager = getSystemService(PowerManager::class.java)
            this.lock =
                powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.javaClass.name)
            this.lock?.setReferenceCounted(true)
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            if (hour >= 23 || hour <= 6) {
                this.lock?.acquire(5000)
            } else {
                this.lock?.acquire(300000)
            }
        }
    }
}