package org.getbuddies.a2step

import android.app.Application
import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.tencent.mmkv.MMKV
import org.getbuddies.a2step.consts.SyncSettingsMMKVs.KEY_SYNC_METHOD
import org.getbuddies.a2step.consts.SyncSettingsMMKVs.KEY_SYNC_SETTINGS
import org.getbuddies.a2step.consts.SyncSettingsMMKVs.VALUE_SYNC_METHOD_NONE
import org.getbuddies.a2step.worker.BackupWorker
import java.util.concurrent.TimeUnit


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        initMMKV()
        startWorkManager()
    }

    private fun startWorkManager() {
        val syncMethod =
            MMKV.mmkvWithID(KEY_SYNC_SETTINGS).getString(KEY_SYNC_METHOD, VALUE_SYNC_METHOD_NONE)
        if (syncMethod == VALUE_SYNC_METHOD_NONE) {
            return
        }
        // webdav is the only sync method now
        val workRequest: WorkRequest =
            PeriodicWorkRequestBuilder<BackupWorker>(90, TimeUnit.SECONDS)
                .build()
        WorkManager
            .getInstance(this)
            .enqueue(workRequest)
    }

    private fun initMMKV() {
        MMKV.initialize(this);
    }

    companion object {
        private lateinit var instance: App

        @JvmStatic
        fun get(): App {
            return instance
        }

        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }
}