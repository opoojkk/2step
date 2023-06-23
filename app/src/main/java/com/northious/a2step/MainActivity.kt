package com.northious.a2step

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.northious.a2step.databinding.ActivityMainBinding
import com.northious.a2step.totp.TotpGenerator

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.text.text = TotpGenerator.generateNow("TVOFVGUHCDU4HD4GFVRAIFFASDQFUGXKOLSVQEC7TQINV4JFESNUSB7KG7ENH64UQXMP7GQ77Y5KNQ63AJ6PGVHZXI34LR47LQTN6JQ")
    }
}