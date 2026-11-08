package com.hpwnr.apk

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var input: EditText
    private lateinit var output: TextView
    private lateinit var modeSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        input = findViewById(R.id.inputLink)
        output = findViewById(R.id.outputText)
        modeSpinner = findViewById(R.id.modeSpinner)

        val modes = arrayOf(
            "Decrypt (auto)",
            "Encrypt → crypt5",
            "Encrypt → crypt4",
            "Encrypt → crypt3",
            "Encrypt → v2raytun crypt4",
            "Inspect link",
            "Unwrap / Resolve"
        )
        modeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, modes)

        findViewById<Button>(R.id.btnRun).setOnClickListener { runAction() }
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            input.setText("")
            output.text = "Output cleared"
        }
        findViewById<Button>(R.id.btnPaste).setOnClickListener {
            val clip = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val text = clip.primaryClip?.getItemAt(0)?.text?.toString()
            if (!text.isNullOrBlank()) input.setText(text)
        }
    }

    private fun runAction() {
        val link = input.text.toString().trim()
        if (link.isEmpty()) {
            output.text = "Paste a link or plaintext first"
            return
        }

        val mode = modeSpinner.selectedItemPosition
        output.text = when (mode) {
            0 -> decryptAuto(link)
            1 -> encryptStub(link, "crypt5")
            2 -> encryptStub(link, "crypt4")
            3 -> encryptStub(link, "crypt3")
            4 -> encryptStub(link, "v2-crypt4")
            5 -> inspect(link)
            6 -> unwrap(link)
            else -> "Unknown mode"
        }
    }

    private fun decryptAuto(s: String): String {
        return when {
            s.startsWith("happ://crypt5") ->
                "[Decrypt crypt5]\nDetected Happ crypt5 link.\n\nFull RSA+ChaCha engine pending native port.\nLength: ${s.length}\n\nTip: use Termux binary for real decrypt until engine is wired."
            s.startsWith("happ://crypt4") || s.startsWith("happ://crypt3") ||
            s.startsWith("happ://crypt2") || s.startsWith("happ://crypt") ->
                "[Decrypt Happ]\nDetected Happ encrypted link.\nMode auto-detected from prefix.\n\nFull decrypt coming next."
            s.startsWith("v2raytun://crypt") ->
                "[Decrypt v2RayTun]\nDetected v2raytun encrypted link.\n\nFull RSA decrypt pending."
            s.startsWith("happ://add") || s.startsWith("v2raytun://import") ||
            s.startsWith("incy://") ->
                "[Wrapper]\nDeeplink / open-in wrapper detected.\nWill resolve then decrypt."
            s.startsWith("http://") || s.startsWith("https://") ->
                "[HTTP URL]\nSubscription URL detected.\nFetch + AES body decrypt needs network permission + engine."
            else -> "Unknown format.\nSupported: happ://crypt*, v2raytun://crypt*, wrappers, http(s)"
        }
    }

    private fun encryptStub(plain: String, mode: String): String {
        return "[Encrypt → $mode]\nPlaintext length: ${plain.length}\n\nWould produce:\n$mode://... (stub)\n\nFull encrypt engine next push."
    }

    private fun inspect(s: String): String {
        val sb = StringBuilder()
        sb.appendLine("[Inspect]")
        sb.appendLine("Length: ${s.length}")
        sb.appendLine("Prefix: ${s.take(24)}...")
        when {
            s.startsWith("happ://crypt5") -> sb.appendLine("Kind: Happ crypt5")
            s.startsWith("happ://crypt") -> sb.appendLine("Kind: Happ crypt (legacy)")
            s.startsWith("v2raytun://crypt") -> sb.appendLine("Kind: v2RayTun")
            s.startsWith("happ://") -> sb.appendLine("Kind: Happ wrapper")
            s.startsWith("v2raytun://") -> sb.appendLine("Kind: v2RayTun wrapper")
            s.startsWith("http") -> sb.appendLine("Kind: HTTP subscription")
            else -> sb.appendLine("Kind: Unknown")
        }
        return sb.toString()
    }

    private fun unwrap(s: String): String {
        return "[Unwrap]\nWould resolve redirects / embedded links.\n\nInput: ${s.take(80)}..."
    }
}
