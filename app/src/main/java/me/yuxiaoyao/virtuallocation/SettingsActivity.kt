package me.yuxiaoyao.virtuallocation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<TextView>(R.id.currentVersion).text = getVersion()
        findViewById<TextView>(R.id.checkUpdate).setOnClickListener {
            val uri: Uri = Uri.parse("https://github.com/yu-xiaoyao/Virtuallocation/releases")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

    }


    private fun getVersion(): String {
        // 获取packagemanager的实例
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return packageInfo.versionName
    }
}