/*
 * [Parent Feature/Milestone] Kilo Android
 * [Child Task/Issue] #21
 * [Subtask] Download Kilo release binary when npm is unavailable
 * [Upstream] KiloTermux -> [Downstream] Android launcher script
 * [Law Check] 98 lines | Passed Do It Check
 */

package com.kilocli.android

import android.content.Context
import android.system.Os
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream

class KiloReleaseInstaller(private val context: Context) {
    fun install(target: File, onStatus: ((String) -> Unit)? = null) {
        val archive = File(context.cacheDir, "kilo-linux-arm64.tar.gz")
        try {
            onStatus?.invoke("Downloading latest Kilo release...")
            downloadLatestArchive(archive)
            onStatus?.invoke("Extracting Kilo release binary...")
            extractBinary(archive, target)
            if (!target.setReadable(true, false) || !target.setExecutable(true, false)) throw IOException("Unable to set executable permissions on Kilo binary: ${target.absolutePath}")
            try { Os.chmod(target.absolutePath, 0x1C0) } catch (_: Exception) {}
        } finally {
            archive.delete()
        }
    }

    private fun downloadLatestArchive(archive: File) {
        val url = URL(latestArm64AssetUrl())
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 30_000
        connection.readTimeout = 60_000
        connection.instanceFollowRedirects = true
        connection.inputStream.use { input -> archive.outputStream().use { output -> input.copyTo(output) } }
    }

    private fun latestArm64AssetUrl(): String {
        val json = fetchText("https://api.github.com/repos/Kilo-Org/kilocode/releases/latest")
        return Regex("\"browser_download_url\"\\s*:\\s*\"([^\"]*kilo-linux-arm64\\.tar\\.gz)\"")
            .find(json)?.groupValues?.get(1)
            ?: throw IOException("Missing kilo-linux-arm64.tar.gz in latest Kilo release")
    }

    private fun fetchText(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 30_000
        connection.readTimeout = 30_000
        connection.setRequestProperty("Accept", "application/vnd.github+json")
        connection.setRequestProperty("User-Agent", "KiloAndroid")
        return connection.inputStream.bufferedReader().use { it.readText() }
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

    private fun ByteArray.string(offset: Int, length: Int): String =
        copyOfRange(offset, offset + length).decodeToString().trimEnd('\u0000', ' ')

    private fun ByteArray.octal(offset: Int, length: Int): Long =
        string(offset, length).toLongOrNull(8) ?: 0L

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
