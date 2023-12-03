package org.getbuddies.a2step.ui.settings.viewModel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.getbuddies.a2step.R
import org.getbuddies.a2step.db.DataBases
import org.getbuddies.a2step.db.totp.TotpDataBase
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.totp.RoughTotp
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class RoughTotpViewModel : ViewModel() {
    fun parseTotpFromIntent(context: Context, data: Intent?) {
        var canceled = false
        val alertDialog = MaterialAlertDialogBuilder(context)
            .setTitle("解析中")
            .setView(R.layout.dialog_migration_progress_indicator)
            .setNegativeButton("取消") { dialog, which ->
                canceled = true
                dialog.dismiss()
            }.create()
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                alertDialog.show()
            }
            data ?: let {
                Log.w(TAG, "parseTotpFromIntent: data is null")
                return@launch
            }
            // 处理选择的文件URI
            val selectedFileUri: Uri? = data.data
            selectedFileUri ?: return@launch
            val json: String = readJsonFromUri(selectedFileUri, context)
            val type = object : TypeToken<List<RoughTotp>>() {}
            val list: List<RoughTotp>? =
                GsonBuilder().create().fromJson<List<RoughTotp>>(json, type.type)
            if (canceled) {
                return@launch
            }
            list ?: let {
                Log.i(TAG, "parseTotpFromIntent: parsed list is null, end")
                return@launch
            }
            for (roughTotp: RoughTotp in list) {
                DataBases.get(TotpDataBase::class.java).totpDao().insert(Totp.create(roughTotp))
            }
            withContext(Dispatchers.Main) {
                alertDialog.dismiss()
                MaterialAlertDialogBuilder(context)
                    .setTitle("解析完成")
                    .setNegativeButton("确定") { dialog, which ->
                        dialog.dismiss()
                    }.create()
                    .show()
            }
        }
    }

    private fun readJsonFromUri(uri: Uri, context: Context): String {
        val stringBuilder = java.lang.StringBuilder()
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                inputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    companion object {
        const val TAG = "RoughTotpViewModel"
    }
}