package com.hpwnr.apk

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = findViewById<EditText>(R.id.inputLink)
        val btn = findViewById<Button>(R.id.btnDecrypt)
        val output = findViewById<TextView>(R.id.outputText)

        btn.setOnClickListener {
            val link = input.text.toString().trim()
            if (link.isEmpty()) {
                output.text = "Paste a happ:// or v2raytun:// link"
                return@setOnClickListener
            }
            // Placeholder decrypt — full crypto from hpwnr will be wired next
            output.text = decryptStub(link)
        }
    }

    private fun decryptStub(link: String): String {
        return when {
            link.startsWith("happ://crypt") || link.startsWith("v2raytun://crypt") ->
                "[STUB] Detected encrypted link.\n\nFull decrypt engine coming in next push.\n\nLink length: ${link.length}"
            link.startsWith("happ://") || link.startsWith("v2raytun://") ->
                "[STUB] Wrapper / deeplink detected.\nResolve + decrypt next."
            else -> "Unknown format. Expected happ://crypt... or v2raytun://crypt..."
        }
    }
}
