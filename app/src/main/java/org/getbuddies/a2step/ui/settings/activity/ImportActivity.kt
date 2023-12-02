package org.getbuddies.a2step.ui.settings.activity

import androidx.lifecycle.lifecycleScope
import com.google.gson.JsonArray
import com.google.gson.JsonParser
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
import org.getbuddies.a2step.ui.base.ViewBindingActivity

class ImportActivity : ViewBindingActivity<ActivityImportBinding>() {
    override fun getViewBinding(): ActivityImportBinding {
        return ActivityImportBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        mBinding.backButton.setOnClickListener { finish() }
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


    companion object {
        const val TAG = "ImportActivity"
    }
}