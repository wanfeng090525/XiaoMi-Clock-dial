package com.watchface.idtool

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/**
 * 点击音效（SoundPool 低延迟播放）
 *
 * · 音源：res/raw/click.wav（合成的玻璃触击音，95ms · 四层合成：清脆高频
 *   起音 + 玻璃谐振 + 温暖低频 + 音高下滑细节）
 * · 音频通道：USAGE_MEDIA —— 跟随媒体音量，不受系统「触摸提示音」开关
 *   影响（此前用 SONIFICATION 通道在多数设备上被系统静音，导致只感
 *   觉到振动而听不到声音，正是用户反馈的问题）
 * · 预加载：MainActivity 启动时调用 ensureLoaded()，首次点击即可出声
 * · 失败重载：加载失败自动清空引用，下次点击重新尝试
 * · 开关：AppSettings.soundEnabled（设置页可切换，立即生效；关闭后
 *   同时静音并停止全部触感振动）
 */
object ClickSound {
    private var soundPool: SoundPool? = null
    private var soundId: Int = 0

    @Volatile
    private var loaded = false

    @Volatile
    private var lastPlayAt: Long = 0L

    /** 启动时预加载（MainActivity.onCreate 调用，首次点击零延迟） */
    fun ensureLoaded(context: Context) {
        if (soundPool != null) return
        try {
            val attrs = AudioAttributes.Builder()
                // 媒体通道：跟随媒体音量，绝大多数设备均有声
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val pool = SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(attrs)
                .build()
            pool.setOnLoadCompleteListener { _, _, status ->
                if (status == 0) {
                    loaded = true
                } else {
                    // 加载失败：清空引用，下次 play() 重新尝试加载
                    loaded = false
                    soundPool = null
                }
            }
            // 从 assets 加载音效，避免 R.raw 资源计入 dex 体积
            val afd = context.applicationContext.assets.openFd("sounds/click.wav")
            soundId = pool.load(afd, 1)
            soundPool = pool
        } catch (_: Exception) {
            soundPool = null
        }
    }

    /** 播放点击音（开关关闭时完全静默；40ms 节流防连点轰炸） */
    fun play(context: Context) {
        if (!AppSettings.soundEnabled) return
        try {
            if (soundPool == null || !loaded) {
                // 首次加载或加载失败，尝试重新加载
                soundPool?.release()
                soundPool = null
                ensureLoaded(context)
                // 重新加载中，本次不出声，下次点击生效
                return
            }
            val now = System.currentTimeMillis()
            if (now - lastPlayAt < 40L) return
            lastPlayAt = now
            soundPool?.play(soundId, 1.0f, 1.0f, 1, 0, 1f)
        } catch (_: Exception) {
            soundPool = null
            loaded = false
        }
    }
}