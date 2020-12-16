package com.jason.moduletest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jason.wifimodule.WifiListAty
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_wifi.setOnClickListener {
            startActivity(Intent(this, WifiListAty::class.java))
        }
    }
}
