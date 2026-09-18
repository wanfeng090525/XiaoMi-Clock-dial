package com.watchface.idtool

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt
import com.watchface.idtool.ui.AppBackground
import com.watchface.idtool.ui.GlobalRippleOverlay
import com.watchface.idtool.ui.LoadingOverlay
import com.watchface.idtool.ui.MainMenuScreen
import com.watchface.idtool.ui.ResultDialog
import com.watchface.idtool.ui.SnowfallLayer
import com.watchface.idtool.ui.ToastMessage
import com.watchface.idtool.ui.WatchFaceTheme

class MainActivity : ComponentActivity() {

    /** DPI 密度缩放：在 Context 附加阶段以一致的方式缩放 density/scaledDensity */
    override fun attachBaseContext(newBase: Context) {
        AppSettings.load(newBase)
        val factor = AppSettings.densityFactor
        super.attachBaseContext(
            if (factor == 1f) newBase else DensityScaledContext(newBase, factor)
        )
    }

    /**
     * 按系数缩放 densityDpi，并让 density / scaledDensity / fontScale 保持同步。
     * 仅改 densityDpi 会造成 sp 文字与 dp 布局比例不一致，导致增大密度后文字异常显示；
     * 这里显式统一三者的转换关系，保证文字与布局同步缩放。
     */
    private class DensityScaledContext(base: Context, private val factor: Float) :
        android.content.ContextWrapper(base) {

        private val scaledResources: Resources by lazy {
            val res = super.getResources()
            val dm = res.displayMetrics
            val baseDensity = dm.density
            // 保留系统字体缩放，避免破坏“文字大小”辅助功能设置
            val fontScale = if (baseDensity > 0f) dm.scaledDensity / baseDensity else 1f
            val targetDpi = (dm.densityDpi * factor).roundToInt()
            val newDensity = targetDpi / 160f

            val cfg = Configuration(res.configuration)
            cfg.densityDpi = targetDpi
            cfg.fontScale = fontScale

            val wrapped = base.createConfigurationContext(cfg).resources
            wrapped.displayMetrics.apply {
                this.density = newDensity
                this.scaledDensity = newDensity * fontScale
                this.densityDpi = targetDpi
            }
            wrapped
        }

        @Deprecated("Deprecated in Java")
        override fun getResources(): Resources = scaledResources
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 深色玻璃主题：状态栏/导航栏使用浅色图标
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        // 沉浸全屏：隐藏状态栏与导航栏（上滑临时呼出）
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        // 预加载点击音效（首次点击零延迟）
        ClickSound.ensureLoaded(this)

        setContent {
            WatchFaceTheme {
                AppContent()
            }
        }
    }
}

@Composable
private fun AppContent() {
    val viewModel: MainViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    // 0=主页 1=修改 2=记录 3=设置：四个界面集成在一个顶部页签菜单里（对应 Lua wanfeng.menu）
    var currentPage by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    // 恢复本地卡密登录态：首次启动后台异步验证，完成后自动登录
    androidx.compose.runtime.LaunchedEffect(Unit) {
        com.watchface.idtool.SagAuthManager.restoreSession(context)
    }

    // 观察会话恢复状态，用于显示启动加载遮罩
    val isRestoring by com.watchface.idtool.SagAuthManager.restoreState.collectAsState()
    val loggedIn by com.watchface.idtool.SagAuthManager.loginState.collectAsState()

    // ON_RESUME 时节流刷新权限状态（从 Shizuku 授权页返回后立即生效）
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPermissionOnResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 返回键：非主页先回主页；主页双击退出
    var lastBackAt by remember { mutableLongStateOf(0L) }
    BackHandler {
        if (currentPage != 0) {
            currentPage = 0
        } else {
            val now = System.currentTimeMillis()
            if (now - lastBackAt < 2000L) {
                (context as? Activity)?.finish()
            } else {
                lastBackAt = now
                Toast.makeText(context, com.watchface.idtool.ui.AppLocale.t("再按一次退出"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun switchPage(index: Int) {
        if (index == 2) viewModel.loadRecords()
        currentPage = index
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // L0 背景：自定义壁纸 / 纯色 / 液态动态（全屏铺满，含系统栏区域；
        //          图片 ContentScale.Crop 保持原比例居中裁剪，任意屏幕比例不变形）
        AppBackground()

        // L1 内容区：避开系统栏与输入法
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // 四个界面集成在一个界面：顶部页签（主页/修改/记录/设置）
            MainMenuScreen(
                viewModel = viewModel,
                state = state,
                pageIndex = currentPage,
                onPageChange = { switchPage(it) }
            )

            if (isRestoring || state.isLoading) {
                LoadingOverlay(if (isRestoring) "正在验证会话…" else state.loadingText)
            }

            state.resultMessage?.let { msg ->
                ResultDialog(
                    success = state.resultSuccess,
                    message = msg,
                    onDismiss = { viewModel.clearResult() }
                )
            }

            state.toastMessage?.let { toast ->
                ToastMessage(message = toast, onFinished = { viewModel.clearToast() })
            }
        }

        // L3 雪花前景：设置中可开关；覆盖在内容与导航之上
        if (com.watchface.idtool.AppSettings.snowEnabled) {
            SnowfallLayer()
        }

        // L4 全局点击光效：View 层监听 · 零拦截 · 最顶层绘制
        GlobalRippleOverlay()
    }
}
