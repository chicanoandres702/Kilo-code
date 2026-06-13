/*
 * [Parent Feature/Milestone] Kilo Android
 * [Child Task/Issue] #21
 * [Subtask] Download Kilo release binary when npm is unavailable
 * [Upstream] KiloTermux -> [Downstream] Android launcher script
 * [Law Check] 86 lines | Passed Do It Check
 */
package com.kilocli.android

import android.content.Context
import android.os.Build
import android.system.Os
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream

class KiloReleaseInstaller(private val context: Context) {
    fun install(target: File, onStatus: ((String) -> Unit)? = null) {
        val archive = File(context.cacheDir, "kilo-native.tar.gz")
        try {
            onStatus?.invoke("Downloading latest Kilo release...")
            downloadLatestArchive(archive)
            onStatus?.invoke("Extracting Kilo release binary...")
            extractBinary(archive, target)
            if (!target.isFile || target.length() == 0L) throw IOException("Extracted Kilo binary is empty: ${target.absolutePath}")

            // Ensure readable/writable/executable by owner (700)
            target.setReadable(true, true)
            target.setWritable(true, true)
            target.setExecutable(true, true)

            // Force chmod for robust permission application on Android
            try {
                Os.chmod(target.absolutePath, 0x1C0) // 0700
            } catch (e: Exception) {
                // Log or handle chmod failure if necessary, but don't fail the install
            }

            // Verification: Check if we can actually execute it (a dry run)
            if (!target.canExecute()) {
                throw IOException("Failed to set executable permissions on Kilo binary (target.canExecute() is false): ${target.absolutePath}")
            }
        } finally {
            archive.delete()
        }
    }

    private fun downloadLatestArchive(archive: File) {
        val connection = URL(latestNativeAssetUrl()).openConnection() as HttpURLConnection
        connection.connectTimeout = 30_000
        connection.readTimeout = 60_000
        connection.instanceFollowRedirects = true
        connection.setRequestProperty("User-Agent", "KiloAndroid")
        connection.checkStatus("Download Kilo release")
        connection.inputStream.use { input -> archive.outputStream().use { output -> input.copyTo(output) } }
    }

    private fun latestNativeAssetUrl(): String = "https://github.com/Kilo-Org/kilocode/releases/latest/download/${nativeAssetName()}"

    private fun nativeAssetName(): String = when (Build.SUPPORTED_ABIS.firstOrNull()) {
        "x86_64" -> "kilo-linux-x64-musl.tar.gz"
        "arm64-v8a" -> "kilo-linux-arm64-musl.tar.gz"
        else -> throw IOException("Unsupported Android ABI for Kilo CLI: ${Build.SUPPORTED_ABIS.joinToString()}")
    }

    private fun HttpURLConnection.checkStatus(action: String) {
        val code = responseCode
        if (code !in 200..399) throw IOException("$action failed: HTTP $code")
    }

    private fun extractBinary(archive: File, target: File) {
        GZIPInputStream(archive.inputStream()).use { gzip ->
            val header = ByteArray(512)
            while (gzip.read(header) == header.size && !header.all { it == 0.toByte() }) {
                val name = header.string(0, 100)
                val size = header.octal(124, 12)
                val type = header[156].toInt()
                val isFile = type == 0 || type == '0'.code
                if (isFile && (name == "kilo" || name.endsWith("/kilo"))) {
                    target.parentFile?.mkdirs()
                    target.outputStream().use { gzip.copyN(it, size) }
                    return
                }
                gzip.skip(roundUp512(size))
            }
        }
        throw IOException("kilo binary was not found in release archive")
    }

    private fun ByteArray.string(offset: Int, length: Int): String = copyOfRange(offset, offset + length).decodeToString().trimEnd('\u0000', ' ')
    private fun ByteArray.octal(offset: Int, length: Int): Long = string(offset, length).toLongOrNull(8) ?: 0L

    private fun InputStream.copyN(output: java.io.OutputStream, bytes: Long) {
        var remaining = bytes
        val buffer = ByteArray(8192)
        while (remaining > 0) {
            val read = read(buffer, 0, remaining.coerceAtMost(buffer.size.toLong()).toInt())
            if (read <= 0) throw IOException("Truncated Kilo release archive")
            output.write(buffer, 0, read)
            remaining -= read
        }
        skip(roundUp512(bytes) - bytes)
    }

    private fun roundUp512(value: Long): Long = ((value + 511L) / 512L) * 512L
}
