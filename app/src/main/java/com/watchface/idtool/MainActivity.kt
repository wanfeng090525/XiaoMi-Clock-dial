package com.watchface.idtool

import android.app.Activity
import android.content.Context
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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.watchface.idtool.ui.AppBackground
import com.watchface.idtool.ui.GlassFabButton
import com.watchface.idtool.ui.GlassNavTab
import com.watchface.idtool.ui.GlassNavBar
import com.watchface.idtool.ui.GlobalRippleOverlay
import com.watchface.idtool.ui.HistoryScreen
import com.watchface.idtool.ui.LoadingOverlay
import com.watchface.idtool.ui.ModifyScreen
import com.watchface.idtool.ui.ResultDialog
import com.watchface.idtool.ui.SettingsScreen
import com.watchface.idtool.ui.SnowfallLayer
import com.watchface.idtool.ui.ToastMessage
import com.watchface.idtool.ui.WatchFaceTheme
import com.watchface.idtool.ui.WelcomeScreen

class MainActivity : ComponentActivity() {

    /** DPI 密度缩放：在 Context 附加阶段生效 */
    override fun attachBaseContext(base: Context) {
        AppSettings.load(base)
        super.attachBaseContext(AppSettings.applyDensity(base))
    }

    override fun onResume() {
        super.onResume()
        if (VpnDetector.isVpnActive(this)) {
            android.widget.Toast.makeText(
                this,
                com.watchface.idtool.ui.AppLocale.t("检测到 VPN 连接，已退出应用"),
                android.widget.Toast.LENGTH_SHORT
            ).show()
            finishAffinity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // VPN 检测：检测到 VPN 连接时立即退出应用
        if (VpnDetector.isVpnActive(this)) {
            android.widget.Toast.makeText(
                this,
                com.watchface.idtool.ui.AppLocale.t("检测到 VPN 连接，已退出应用"),
                android.widget.Toast.LENGTH_SHORT
            ).show()
            finishAffinity()
            return
        }
        
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

/** 页面顺序（用于方向感知的转场动画）；settings 为分离卫星按钮入口 */
private val PAGES = listOf("home", "modify", "history", "settings")
private val NAV_TABS = listOf("home", "modify", "history")

@Composable
private fun AppContent() {
    val viewModel: MainViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    var currentPage by remember { mutableStateOf("home") }
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

    // 返回键：子页面先回主页；主页双击退出
    var lastBackAt by remember { mutableLongStateOf(0L) }
    BackHandler {
        if (currentPage != "home") {
            currentPage = "home"
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

    fun switchPage(page: String) {
        if (page == "history") viewModel.loadRecords()
        currentPage = page
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
            // 页面内容：方向感知的滑动 + 淡入淡出转场
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    val from = PAGES.indexOf(initialState).coerceAtLeast(0)
                    val to = PAGES.indexOf(targetState).coerceAtLeast(0)
                    val forward = to >= from
                    val enter = fadeIn(tween(320, easing = FastOutSlowInEasing)) +
                            slideInHorizontally(tween(340, easing = FastOutSlowInEasing)) {
                                if (forward) it / 4 else -it / 4
                            }
                    val exit = fadeOut(tween(200)) +
                            slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) {
                                if (forward) -it / 5 else it / 5
                            }
                    enter togetherWith exit
                },
                label = "pageTransition"
            ) { page ->
                Box(modifier = Modifier.fillMaxSize()) {
                    when (page) {
                        "home" -> WelcomeScreen(
                            viewModel = viewModel,
                            state = state,
                            onNavigateToModify = { currentPage = "modify" },
                            onNavigateToHistory = { switchPage("history") }
                        )
                        "modify" -> ModifyScreen(
                            viewModel = viewModel,
                            state = state,
                            onNavigateToHistory = { switchPage("history") }
                        )
                        "history" -> HistoryScreen(
                            viewModel = viewModel,
                            state = state
                        )
                        "settings" -> SettingsScreen(
                            viewModel = viewModel,
                            state = state
                        )
                    }
                }
            }

            // L2 悬浮玻璃导航：主胶囊 + 分离式设置圆钮（参考图"+"钮形态）
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassNavBar(
                    tabs = listOf(
                        GlassNavTab(Icons.Default.Home, "主页"),
                        GlassNavTab(Icons.Default.Build, "修改"),
                        GlassNavTab(Icons.Default.History, "记录")
                    ),
                    selected = NAV_TABS.indexOf(currentPage),
                    onSelect = { index -> switchPage(NAV_TABS[index]) }
                )
                Spacer(Modifier.width(12.dp))
                GlassFabButton(
                    icon = Icons.Default.Settings,
                    contentDescription = "设置",
                    selected = currentPage == "settings",
                    onClick = { switchPage("settings") }
                )
            }

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
