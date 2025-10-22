package com.example.install_referrer.sample

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.inetkr.install_referrer.InstallReferrer

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        findViewById<TextView>(R.id.packageName).text = packageName
        findViewById<TextView>(R.id.app_version).text =
            "App version ${packageInfo.versionName}+${getLongVersionCode(packageInfo)}, plugin ${
                InstallReferrer.getInstance(
                    this
                ).getVersion()
            }"
        InstallReferrer.getInstance(this).initialize(debug = true)
    }

    @Suppress("deprecation")
    private fun getLongVersionCode(info: PackageInfo): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode
        } else {
            info.versionCode.toLong()
        }
    }
}