package org.getbuddies.a2step.ui.settings.activity

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.getbuddies.a2step.databinding.ActivityImportBinding
import org.getbuddies.a2step.db.DataBases
import org.getbuddies.a2step.db.totp.TotpDao
import org.getbuddies.a2step.db.totp.TotpDataBase
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.extends.toast
import org.getbuddies.a2step.totp.Otpauth
import org.getbuddies.a2step.totp.RoughTotp
import org.getbuddies.a2step.ui.base.ViewBindingActivity
import org.getbuddies.a2step.ui.home.viewModel.TotpViewModel
import org.getbuddies.a2step.ui.settings.viewModel.RoughTotpViewModel
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class ImportActivity : ViewBindingActivity<ActivityImportBinding>() {
    private val mRoughTotpViewModel by lazy {
        ViewModelProvider(this)[RoughTotpViewModel::class.java]
    }

    override fun getViewBinding(): ActivityImportBinding {
        return ActivityImportBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        mBinding.backButton.setOnClickListener { finish() }
        mBinding.rlImportFromFile.setOnClickListener {
            pickupConfigFile()

        }
        mBinding.submitButton.setOnClickListener {
            val text = mBinding.secretEditText.text.toString().replace(" ", "")
            if (text.isEmpty()) {
                "xml内容不能为空".toast()
                return@setOnClickListener
            }
            lifecycleScope.launch(Dispatchers.IO) {
                var repeated = false
                val repeatedList = arrayListOf<Totp>()
                val jsonArray: JsonArray = JsonParser.parseString(text).asJsonArray
                for (jsonElement in jsonArray) {
                    if (jsonElement.isJsonObject) {
                        val jsonObject = jsonElement.asJsonObject
                        val uri = jsonObject.get("uri").asString
                        val totp = Otpauth.tryParseTotp(uri)
                        val totpDao: TotpDao = DataBases.get(TotpDataBase::class.java).totpDao()
                        val list = totpDao.query(totp.name, totp.account)
                        if (list.isNullOrEmpty()) {
                            repeatedList.add(totp)
                            repeated = true
                            continue
                        }
                        totpDao.insert(totp)
                    }
                }
                withContext(Dispatchers.Main) {
                    if (repeated) {
                        "不冲突项已添加".toast()
                    }
                }
            }

        }
    }

    private fun pickupConfigFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/json" // 所有文件类型

        startActivityForResult(intent, CODE_SELECT_JSON_FILE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === CODE_SELECT_JSON_FILE && resultCode === RESULT_OK) {
            mRoughTotpViewModel.parseTotpFromIntent(this, data)
        }
    }

    companion object {
        const val TAG = "ImportActivity"
        const val CODE_SELECT_JSON_FILE = 0x1101
    }
}