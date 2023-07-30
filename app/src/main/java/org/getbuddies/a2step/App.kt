package org.getbuddies.a2step

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.tencent.mmkv.MMKV
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.getbuddies.a2step.worker.BackupWorker
import java.util.concurrent.TimeUnit


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        startWorkManager()
        initMMKV()
    }

    private fun startWorkManager() {
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