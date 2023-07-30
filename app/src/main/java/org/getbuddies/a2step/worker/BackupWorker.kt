package org.getbuddies.a2step.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tencent.mmkv.MMKV
import com.thegrizzlylabs.sardineandroid.DavResource
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.getbuddies.a2step.db.DataBases
import java.io.File

class BackupWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val sardine = OkHttpSardine()
                val webDavAccount = MMKV.defaultMMKV().getString("webDavAccount", "")
                val webDavPassword = MMKV.defaultMMKV().getString("webDavPassword", "")
                sardine.setCredentials(webDavAccount, webDavPassword)
                val dirs = sardine.list(JIANGUOYUN + PREFIX)
                if (!hasBackupDir(dirs)) {
                    sardine.createDirectory("$JIANGUOYUN$PREFIX$BACKUP_DIR")
                }
                val totpDBPath = applicationContext.getDatabasePath(DataBases.TOTP_DB_NAME)
                sardine.put(
                    "$JIANGUOYUN$PREFIX$BACKUP_DIR$PREFIX${DataBases.TOTP_DB_NAME}",
                    totpDBPath,
                    "application/octet-stream"
                )
            }
        }
        return Result.success()
    }

    private fun hasBackupDir(dirs: List<DavResource>): Boolean {
        for (dir in dirs) {
            if (dir.name == BACKUP_DIR) {
                return true
            }
        }
        return false
    }

    companion object {
        private const val TAG = "BackupWorker"
        private const val BACKUP_DIR = "a2step"
        private const val JIANGUOYUN = "https://dav.jianguoyun.com/dav"
        private const val JIANGUOYUN_NAME = "坚果云";

        private const val PREFIX = "/"
        private const val UPLOADED_FILE_NAME = "totp_db"
    }
}