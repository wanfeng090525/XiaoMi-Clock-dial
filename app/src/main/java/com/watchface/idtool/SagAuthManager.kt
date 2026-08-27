package com.watchface.idtool

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Base64
import com.watchface.idtool.weiyan.WeiyanVerify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.concurrent.atomic.AtomicReference
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 微验卡密登录 / 解绑 / 公告 / 更新
 *
 * 对外入口不变，底层调用独立文件 [WeiyanVerify]。
 */
object SagAuthManager {

    private const val PREF = "weiyan_auth_v1"
    private const val KEY_KAMI = "kami"
    private const val KEY_END = "end_time"

    @Volatile
    private var auth: WeiyanVerify? = null

    @Volatile
    var isLoggedIn: Boolean = false
        private set

    @Volatile
    var endTime: String = ""
        private set

    @Volatile
    var lastError: String = ""
        private set

    @Volatile
    var currentKamiMasked: String = ""
        private set

    private var currentKami: String = ""

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var restoreJob: Job? = null

    @Volatile
    private var restoring = false

    @Volatile
    var isRestoringSession: Boolean = false
        private set

    private val _restoreState = MutableStateFlow(false)
    val restoreState: StateFlow<Boolean> = _restoreState

    private val _loginState = MutableStateFlow(false)
    val loginState: StateFlow<Boolean> = _loginState

    private fun setLoginState(value: Boolean) {
        isLoggedIn = value
        _loginState.value = value
    }

    fun isConfigured(): Boolean = true

    @Synchronized
    fun init(context: Context? = null): Boolean {
        if (!SagConfig.ENABLED) return true
        if (auth != null) return true
        if (context == null) {
            lastError = "验证未初始化：缺少 Context"
            return false
        }
        return try {
            auth = WeiyanVerify(context.applicationContext)
            true
        } catch (e: Exception) {
            lastError = "微验初始化失败: ${e.message}"
            false
        }
    }

    fun ensureInit(context: Context): WeiyanVerify? {
        if (!init(context)) return null
        return auth
    }

    @SuppressLint("HardwareIds")
    fun getMachineCode(context: Context): String {
        val androidId = try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (_: Exception) {
            null
        }
        return md5((androidId ?: "unknown") + "|" + context.packageName).uppercase()
    }

    private fun md5(s: String): String {
        val dig = MessageDigest.getInstance("MD5").digest(s.toByteArray(Charsets.UTF_8))
        return dig.joinToString("") { "%02x".format(it) }
    }

    private fun maskKami(kami: String): String {
        if (kami.length <= 8) return "****"
        return kami.take(4) + "****" + kami.takeLast(4)
    }

    @SuppressLint("HardwareIds")
    private fun deriveKey(context: Context): ByteArray {
        val seed = (Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "id") + "|" + context.packageName + "|weiyan"
        return MessageDigest.getInstance("SHA-256").digest(seed.toByteArray(Charsets.UTF_8))
    }

    private fun encryptKami(context: Context, plain: String): String {
        val key = SecretKeySpec(deriveKey(context), "AES")
        val iv = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
        val enc = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        val out = ByteArray(iv.size + enc.size)
        System.arraycopy(iv, 0, out, 0, iv.size)
        System.arraycopy(enc, 0, out, iv.size, enc.size)
        return Base64.encodeToString(out, Base64.NO_WRAP)
    }

    private fun decryptKami(context: Context, blob: String): String? {
        return try {
            val all = Base64.decode(blob, Base64.NO_WRAP)
            if (all.size < 17) return null
            val iv = all.copyOfRange(0, 16)
            val data = all.copyOfRange(16, all.size)
            val key = SecretKeySpec(deriveKey(context), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
            String(cipher.doFinal(data), Charsets.UTF_8)
        } catch (_: Exception) {
            null
        }
    }

    data class AuthResult(
        val success: Boolean,
        val message: String,
        val endTime: String = "",
        val statecode: String = ""
    )

    private class PendingCallback {
        val notice = CompletableDeferred<String>()
        val update = CompletableDeferred<UpdateInfo>()
        val login = CompletableDeferred<AuthResult>()
        val unbind = CompletableDeferred<AuthResult>()
    }

    private data class UpdateInfo(
        val hasUpdate: Boolean,
        val version: String,
        val updateUrl: String,
        val updateShow: String
    )

    private val pendingRef = AtomicReference<PendingCallback?>(null)

    private fun installCallback(v: WeiyanVerify, pending: PendingCallback) {
        pendingRef.set(pending)
        v.setCallback(object : WeiyanVerify.AuthCallback {
            override fun onNotice(notice: String?) {
                pending.notice.complete(notice ?: "")
            }

            override fun onUpdateCheck(
                hasUpdate: Boolean,
                version: String?,
                updateUrl: String?,
                updateShow: String?
            ) {
                pending.update.complete(
                    UpdateInfo(hasUpdate, version ?: "", updateUrl ?: "", updateShow ?: "")
                )
            }

            override fun onLoginSuccessSingle(remainCount: Int) {
                pending.login.complete(
                    AuthResult(true, "登录成功，剩余次数：$remainCount", endTime = "次数卡($remainCount)")
                )
            }

            override fun onLoginSuccessTime(expireTime: String?, expireTimestamp: Long) {
                val et = expireTime ?: ""
                pending.login.complete(
                    AuthResult(true, "登录成功，到期：$et", endTime = et)
                )
            }

            override fun onLoginFailed(msg: String?) {
                pending.login.complete(AuthResult(false, msg ?: "登录失败"))
            }

            override fun onUnbind(success: Boolean, msg: String?, remainNum: String?) {
                val detail = buildString {
                    append(msg ?: if (success) "解绑成功" else "解绑失败")
                    if (!remainNum.isNullOrBlank()) append("（剩余解绑次数：$remainNum）")
                }
                pending.unbind.complete(AuthResult(success, detail))
            }

            override fun onError(apiName: String?, error: String?) {
                val name = apiName ?: ""
                val err = error ?: "未知错误"
                when {
                    name.contains("login", ignoreCase = true) ->
                        pending.login.complete(AuthResult(false, err))
                    name.contains("unbind", ignoreCase = true) ->
                        pending.unbind.complete(AuthResult(false, err))
                    name.contains("getNotice", ignoreCase = true) ->
                        pending.notice.complete("")
                    name.contains("checkUpdate", ignoreCase = true) ->
                        pending.update.complete(UpdateInfo(false, "", "", ""))
                    else -> {
                        if (!pending.login.isCompleted) {
                            pending.login.complete(AuthResult(false, "$name: $err"))
                        }
                        if (!pending.unbind.isCompleted) {
                            pending.unbind.complete(AuthResult(false, "$name: $err"))
                        }
                    }
                }
            }
        })
    }

    suspend fun restoreSession(context: Context) {
        if (!SagConfig.ENABLED) {
            setLoginState(true)
            isRestoringSession = false
            return
        }
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val kamiBlob = sp.getString(KEY_KAMI, "") ?: ""
        val kami = decryptKami(context, kamiBlob) ?: kamiBlob
        if (kami.isEmpty()) {
            isRestoringSession = false
            return
        }
        val result = login(context, kami, fromRestore = true)
        if (!result.success) {
            currentKami = ""
            endTime = ""
            currentKamiMasked = ""
            setLoginState(false)
            sp.edit().clear().apply()
        }
        isRestoringSession = false
        _restoreState.value = false
    }

    fun restoreSessionAsync(context: Context) {
        if (restoring) return
        restoring = true
        isRestoringSession = true
        _restoreState.value = true
        restoreJob?.cancel()
        restoreJob = scope.launch {
            try {
                restoreSession(context.applicationContext)
            } finally {
                restoring = false
                isRestoringSession = false
                _restoreState.value = false
            }
        }
    }

    fun startHeartbeat(appContext: Context) { /* 微验无心跳 */ }

    fun stopHeartbeat() { /* no-op */ }

    suspend fun login(context: Context, kami: String): AuthResult =
        login(context, kami, fromRestore = false)

    private suspend fun login(
        context: Context,
        kami: String,
        fromRestore: Boolean
    ): AuthResult = withContext(Dispatchers.IO) {
        if (!SagConfig.ENABLED) {
            setLoginState(true)
            return@withContext AuthResult(true, "验证已关闭（调试）")
        }
        if (!init(context)) {
            return@withContext AuthResult(false, lastError.ifBlank { "SDK 初始化失败" })
        }
        val card = kami.trim()
        if (card.isEmpty()) return@withContext AuthResult(false, "请输入卡密")

        val v = auth!!
        val pending = PendingCallback()
        installCallback(v, pending)

        try {
            v.login(card)
            val result = pending.login.await()
            if (result.success) {
                currentKami = card
                endTime = result.endTime
                currentKamiMasked = maskKami(card)
                setLoginState(true)
                lastError = ""
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
                    .putString(KEY_KAMI, encryptKami(context, card))
                    .putString(KEY_END, endTime)
                    .apply()
                result
            } else {
                lastError = result.message
                if (!fromRestore) setLoginState(false)
                AuthResult(false, lastError)
            }
        } catch (e: Exception) {
            lastError = e.message ?: "登录异常"
            AuthResult(false, lastError)
        }
    }

    suspend fun unbindKami(context: Context, kami: String? = null): AuthResult =
        withContext(Dispatchers.IO) {
            val card = (kami ?: currentKami).trim()

            if (!SagConfig.ENABLED) {
                logout(context)
                return@withContext AuthResult(true, "已退出登录")
            }

            if (card.isEmpty()) {
                logout(context)
                return@withContext AuthResult(true, "已退出登录")
            }

            if (!init(context)) {
                logout(context)
                return@withContext AuthResult(false, lastError.ifBlank { "SDK 初始化失败" })
            }

            val v = auth!!
            val pending = PendingCallback()
            installCallback(v, pending)

            try {
                v.unbind(card)
                val result = pending.unbind.await()
                logout(context)
                if (result.success) {
                    AuthResult(true, result.message.ifBlank { "已退出登录（设备已解绑）" })
                } else {
                    AuthResult(true, "已退出登录（${result.message}）")
                }
            } catch (e: Exception) {
                logout(context)
                AuthResult(true, "已退出登录（${e.message ?: "解绑异常"}）")
            }
        }

    fun logout(context: Context) {
        stopHeartbeat()
        setLoginState(false)
        currentKami = ""
        currentKamiMasked = ""
        endTime = ""
    }

    fun skipLogin() {
        isLoggedIn = true
        currentKamiMasked = "未登录"
    }

    fun loadSavedKami(context: Context): String {
        if (!SagConfig.REMEMBER_KAMI) return ""
        val blob = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_KAMI, "") ?: ""
        return decryptKami(context, blob) ?: blob
    }

    data class NoticeVersion(
        val notice: String,
        val latestVersion: String,
        val hasUpdate: Boolean,
        val updateLog: String = "",
        val updateUrl: String = ""
    )

    suspend fun fetchNoticeAndVersion(localVersion: String): NoticeVersion =
        withContext(Dispatchers.IO) {
            val v = auth
            if (v == null) {
                return@withContext NoticeVersion("", localVersion, false)
            }

            val pending = PendingCallback()
            installCallback(v, pending)

            var notice = ""
            var latest = ""
            var hasUpdate = false
            var uplog = ""
            var upurl = ""

            try {
                v.getNotice()
                notice = pending.notice.await()
            } catch (_: Exception) {
            }

            try {
                v.checkUpdate()
                val u = pending.update.await()
                latest = u.version
                uplog = u.updateShow
                upurl = unescapeHtmlEntities(u.updateUrl)
                hasUpdate = if (latest.isNotEmpty()) {
                    compareVersion(latest, localVersion) > 0
                } else {
                    u.hasUpdate
                }
            } catch (_: Exception) {
            }

            NoticeVersion(notice, latest, hasUpdate, uplog, upurl)
        }

    private fun compareVersion(a: String, b: String): Int {
        val pa = a.split(".", "-").mapNotNull { it.filter { c -> c.isDigit() }.toIntOrNull() }
        val pb = b.split(".", "-").mapNotNull { it.filter { c -> c.isDigit() }.toIntOrNull() }
        val n = maxOf(pa.size, pb.size)
        for (i in 0 until n) {
            val x = pa.getOrElse(i) { 0 }
            val y = pb.getOrElse(i) { 0 }
            if (x != y) return x.compareTo(y)
        }
        return 0
    }

    private fun unescapeHtmlEntities(s: String): String {
        if (s.isEmpty()) return s
        return s
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
    }
}
