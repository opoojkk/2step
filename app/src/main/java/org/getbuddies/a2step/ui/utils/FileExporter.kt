package org.getbuddies.a2step.ui.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter

object FileExporter {

    fun exportToFile(context: Context, fileName: String, content: String) {
        // 获取外部存储器的目录
        val externalStorageDir = Environment.getExternalStorageDirectory()

        // 创建目标文件
        val targetFile = File(externalStorageDir, fileName)

        try {
            // 创建文件输出流
            val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 如果是Android 10及以上版本，使用MediaStore API
                val contentUri: Uri =
                    MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                }

                val uri: Uri? = context.contentResolver.insert(contentUri, contentValues)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)
                } else {
                    Log.e("FileExporter", "Failed to create content Uri")
                    return
                }
            } else {
                // Android 9及以下版本，直接使用FileOutputStream
                FileOutputStream(targetFile)
            }

            // 创建字符输出流
            val osw = OutputStreamWriter(fos)

            // 写入文件内容
            osw.write(content)

            // 关闭流
            osw.close()
            fos?.close()

            // 文件导出成功
            // 在实际应用中，你可能还需要处理一些异常和错误情况
        } catch (e: IOException) {
            e.printStackTrace()
            // 文件导出失败，处理异常
        }
    }
}
