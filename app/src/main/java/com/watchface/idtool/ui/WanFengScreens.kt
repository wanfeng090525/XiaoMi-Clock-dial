package com.watchface.idtool.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.provider.Settings
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.ShieldMoon
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
 import com.watchface.idtool.AppSettings
 import com.watchface.idtool.BuildConfig
 import com.watchface.idtool.ClickSound
 import com.watchface.idtool.HitokotoApi
import com.watchface.idtool.ImportedFile
import com.watchface.idtool.LogKeyExtractor
import com.watchface.idtool.MainViewModel
import com.watchface.idtool.PermissionStatus
import com.watchface.idtool.RecordStore
import com.watchface.idtool.UiState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tag
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import com.watchface.idtool.WatchfaceParser
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import com.watchface.idtool.WatchfaceRecord
import kotlin.math.sin
import android.app.Activity
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Gradient
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.mutableFloatStateOf
import com.watchface.idtool.BgMode
import com.watchface.idtool.SagAuthManager
import kotlin.math.roundToInt

// ====================================================================
// 所有界面集中于此文件（对齐 Lua 版 wanfeng.* 声明式布局风格）：
//   1. WelcomeScreen  主页：快捷操作 / 权限状态 / 密钥提取 / 已导入文件
//   2. ModifyScreen   修改页：文件信息 / 新 ID / 名称 / 保存导出
//   3. HistoryScreen  记录页：修改记录列表
//   4. SettingsScreen 设置页：WanFeng.Menu 顶部页签多页面
// 控件统一走 WanFeng.* 工厂，视觉与旧实现一致。
// ====================================================================


/**
 * 首页（控制中心式布局）
 *
 * 结构（自上而下）：
 *   1. 品牌栏      Logo + 标题/版本 + 设置
 *   2. 权限胶囊卡  状态图标 + 提示 + 刷新（参考图 WLAN/蓝牙开关形态）
 *   3. 快捷操作    2×2 玻璃瓷砖网格（参考图圆形开关阵列）
 *   4. 已导入文件  列表
 */
@Composable
fun WelcomeScreen(
    viewModel: MainViewModel,
    state: UiState,
    onNavigateToModify: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current

    // 首页直接选择文件 → 加载并跳转修改页
    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = try {
                context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
                }
            } catch (_: Exception) {
                null
            } ?: it.toString().substringAfterLast("/").let { name ->
                java.net.URLDecoder.decode(name, "UTF-8")
            }
            viewModel.loadFile(it, fileName)
            onNavigateToModify()
        }
    }

    // 密钥提取ZIP文件选择器 — 选择后自动提取
    val zipPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val path = try {
                val tempFile = File(context.cacheDir, "extract_${System.currentTimeMillis()}.zip")
                context.contentResolver.openInputStream(it)?.use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                }
                tempFile.absolutePath
            } catch (_: Exception) {
                null
            }
            if (path != null) {
                viewModel.setExtractZipPath(path)
                // 选择完成后自动触发提取，无需再手动点击
                viewModel.extractLogKey()
            }
        }
    }

    // 所有文件访问权限授权页返回后：刷新状态 + 自动扫描
    val manageStorageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 先刷新权限胶囊（会识别 FILE 状态）
        viewModel.checkPermissionStatus()
        if (viewModel.hasManageExternalStorage()) {
            viewModel.showToast("文件权限已授予，正在扫描…")
            viewModel.quickImportSmart()
        } else {
            viewModel.showToast("未授予「所有文件访问权限」，可改用 Root/Shizuku")
            viewModel.showNoPermissionDialog()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // ============ 品牌栏 ============
        StaggeredItem(index = 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LogoBadge(size = 44.dp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "表盘 ID 工具",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.2.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "WATCHFACE ID TOOL · v${BuildConfig.VERSION_NAME}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // 设置入口移至底部导航分离式圆钮
            }
        }

        Spacer(Modifier.height(22.dp))

        // ============ 一言引用（Hitokoto API） ============
        StaggeredItem(index = 1) {
            HitokotoBar()
        }

        Spacer(Modifier.height(18.dp))

        // ============ 权限状态胶囊卡 ============
        StaggeredItem(index = 2) {
            PermissionStatusCard(
                status = state.permissionStatus,
                onRefresh = { viewModel.checkPermissionStatus() }
            )
        }

        Spacer(Modifier.height(14.dp))

        // ============ 快捷操作 2×2 瓷砖网格 + 密钥提取入口 ============
        StaggeredItem(index = 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickTile(
                    icon = Icons.Default.FolderOpen,
                    title = "一键导入",
                    subtitle = "表盘目录批量导入",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        // 智能导入：有 Root/Shizuku 直接扫；有文件管理权限用零宽空格静默扫；
                        // 都没有则跳转「所有文件访问权限」授权页（使用系统设置页）
                        val needRequestPermission = viewModel.quickImportSmart()
                        if (needRequestPermission) {
                            viewModel.showToast("请点击「允许」授予所有文件管理权限，以扫描表盘文件")
                            try {
                                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                manageStorageLauncher.launch(intent)
                            } catch (e: Exception) {
                                // 降级方案：跳转到通用文件访问设置页
                                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                                manageStorageLauncher.launch(intent)
                            }
                        }
                    }
                )
                QuickTile(
                    icon = Icons.Default.Watch,
                    title = "选择文件",
                    subtitle = "手动选取 .bin 文件",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (!viewModel.requireLogin()) return@QuickTile
                        filePicker.launch(arrayOf("*/*"))
                    }
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        StaggeredItem(index = 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickTile(
                    icon = Icons.Default.Build,
                    title = "修改表盘",
                    subtitle = "ID 与名称修改",
                    tint = AppColors.successAdaptive(),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (!viewModel.requireLogin()) return@QuickTile
                        onNavigateToModify()
                    }
                )
                QuickTile(
                    icon = Icons.Default.History,
                    title = "修改记录",
                    subtitle = if (state.records.isEmpty()) "暂无记录"
                    else AppLocale.tf("{0} 条记录", state.records.size),
                    tint = AppColors.warning,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToHistory
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        // 密钥提取快捷瓷砖（独立一行）
        StaggeredItem(index = 5) {
            KeyExtractionTile(
                state = state,
                zipPicker = zipPicker,
                onExtract = { viewModel.extractLogKey() },
                onClear = { viewModel.clearExtractResult() }
            )
        }

        // ============ 已导入的表盘文件 ============
        AnimatedVisibility(
            visible = state.importedFiles.isNotEmpty(),
            enter = fadeIn(tween(280)) + expandVertically(tween(320)),
            exit = fadeOut(tween(200)) + shrinkVertically(tween(240))
        ) {
            Column {
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppLocale.tf("已导入的表盘 · {0}", state.importedFiles.size),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.3.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                    GlassIconButton(
                        icon = Icons.Default.Delete,
                        contentDescription = AppLocale.t("清除"),
                        tint = AppColors.dangerAdaptive(),
                        size = 30.dp,
                        tintTop = AppColors.danger.copy(alpha = 0.14f),
                        tintBottom = AppColors.danger.copy(alpha = 0.06f),
                        onClick = { viewModel.clearImportedFiles() }
                    )
                }
                Spacer(Modifier.height(8.dp))
                state.importedFiles.forEachIndexed { index, file ->
                    StaggeredItem(index = index + 5) {
                        ImportedFileCard(
                            file = file,
                            onClick = {
                                viewModel.loadImportedFile(file)
                                onNavigateToModify()
                            }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // ============ 密钥提取结果展示 ============
        if (state.isExtracting || state.extractResult != null) {
            Spacer(Modifier.height(20.dp))
            StaggeredItem(index = 100) {
                KeyExtractionResultCard(
                    result = state.extractResult,
                    isExtracting = state.isExtracting,
                    onClear = { viewModel.clearExtractResult() }
                )
            }
            if (state.isExtracting) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = 1f
                )
            }
        }

        Spacer(Modifier.height(100.dp))
    }

    // ============ 弹窗 ============
    if (state.showNoPermissionDialog) {
        NoPermissionDialog(
            onDismiss = { viewModel.dismissNoPermissionDialog() },
            onAuthorizeShizuku = {
                viewModel.dismissNoPermissionDialog()
                viewModel.requestShizukuPermission()
            }
        )
    }

    if (state.showAnnouncementDialog) {
        val config = state.cloudConfig
        if (config != null) {
            AnnouncementDialog(
                announcement = config.announcement,
                onDismiss = { viewModel.dismissAnnouncementDialog() }
            )
        }
    }

    // 版本更新弹窗：与公告完全分离，仅在公告关闭后展示
    if (state.showUpdateDialog && !state.showAnnouncementDialog) {
        val config = state.cloudConfig
        if (config != null) {
            UpdateDialog(
                latestVersion = config.latestVersion,
                onUpdate = { viewModel.startDownloadUpdate() },
                onDismiss = { viewModel.dismissUpdateDialog() }
            )
        }
    }

    if (state.showDownloadProgress) {
        DownloadProgressDialog(
            progress = state.downloadProgress,
            downloadedBytes = state.downloadDownloadedBytes,
            totalBytes = state.downloadTotalBytes,
            speedBytesPerSec = state.downloadSpeed,
            isDownloading = state.isDownloading,
            error = state.downloadError,
            onDismiss = { viewModel.dismissDownloadProgress() },
            onRetry = { viewModel.startDownloadUpdate() },
            onOpenBrowser = { viewModel.openDownloadInBrowser() },
            onCancel = { viewModel.cancelDownloadUpdate() }
        )
    }
}

/** 应用徽标：玻璃相框 + 弹簧入场 */
@Composable
private fun LogoBadge(size: androidx.compose.ui.unit.Dp = 44.dp) {
    var shown by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) { shown = true }
    val scale by animateFloatAsState(
        targetValue = if (shown) 1f else 0.3f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 420f),
        label = "logoScale"
    )
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .glassShadow(6.dp, RoundedCornerShape(size * 0.32f))
            .glass(
                RoundedCornerShape(size * 0.32f),
                rememberGlassColors()
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(size * 0.26f))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.16f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    ),
                    RoundedCornerShape(size * 0.26f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = com.watchface.idtool.R.drawable.ic_ximi_logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// ====================================================================
// 快捷操作瓷砖（参考图圆形开关的方形版：图标 + 标题 + 副标题）
// ====================================================================

@Composable
private fun QuickTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val tileContext = androidx.compose.ui.platform.LocalContext.current
    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

    Box(
        modifier = modifier
            .aspectRatio(1.55f)
            .glassShadow(8.dp, RoundedCornerShape(22.dp))
            .pressScale(interaction, pressedScale = 0.94f)
            .glass(RoundedCornerShape(22.dp), rememberGlassColors())
            .pressRipple(interaction, clipShape = RoundedCornerShape(22.dp), color = tint, intensity = 1.2f)
            .clickable(
                interactionSource = interaction,
                indication = null
            ) {
                if (AppSettings.soundEnabled) {
                    ClickSound.play(tileContext)
                    haptics.performHapticFeedback(
                        androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                    )
                }
                onClick()
            }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .glass(
                        RoundedCornerShape(12.dp),
                        rememberGlassColors()
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFFE9EBF4),
                    modifier = Modifier.size(17.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.2.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ====================================================================
// 权限状态胶囊卡（WLAN 开关同款形态）
// ====================================================================

private data class PermissionVisual(
    val icon: ImageVector,
    val tintTop: Color?,
    val tintBottom: Color?,
    val iconTint: Color,
    val title: String,
    val subtitle: String,
    val active: Boolean
)

// ====================================================================
// 时钟 · 一言条（Hitokoto API）
//
//   · 位置：品牌栏下方，权限卡上方（外间距比普通卡片更大）
//   · 结构（同一玻璃组件，上下两段）：
//       上段  大号时钟 HH:mm:ss（数字翻动动画）+ 右侧日期小字
//       中间  渐隐细玻璃分隔线
//       下段  竖向光棒 + 引文（打字机渐显 + 光标）+ 出处 + 刷新
//   · 时钟：精确到秒；冒号随秒呼吸闪烁；日期跟随界面语言
//   · 一言：每次打开 App 自动拉取刷新；文字逐字渐显，
//     显示完毕后出处淡入上浮；点按可手动换一句（重新渐显）
//   · 容错：断网/接口异常自动回退内置句子，永不空白
// ====================================================================

@Composable
private fun HitokotoBar() {
    var quote by remember { mutableStateOf<HitokotoApi.Hitokoto?>(null) }
    var refreshing by remember { mutableStateOf(false) }
    var revealCount by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    // 每次进入组合（即每次打开 App / 回到主页）自动拉取新句
    LaunchedEffect(Unit) {
        quote = HitokotoApi.fetch()
    }

    // 打字机渐显：新句到达后逐字展开
    LaunchedEffect(quote) {
        revealCount = 0
        val text = quote?.text.orEmpty()
        text.forEachIndexed { i, _ ->
            delay(26)
            revealCount = i + 1
        }
    }

    // 时钟：对齐秒边界刷新，精确到秒
    var nowSec by remember { mutableStateOf(System.currentTimeMillis() / 1000L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L - System.currentTimeMillis() % 1000L + 8L)
            nowSec = System.currentTimeMillis() / 1000L
        }
    }
    val cal = remember(nowSec) {
        Calendar.getInstance().apply { timeInMillis = nowSec * 1000L }
    }
    val hh = String.format(Locale.ROOT, "%02d", cal.get(Calendar.HOUR_OF_DAY))
    val mm = String.format(Locale.ROOT, "%02d", cal.get(Calendar.MINUTE))
    val ss = String.format(Locale.ROOT, "%02d", cal.get(Calendar.SECOND))
    // 日期仅在跨天时重算
    val dateText = remember(cal.get(Calendar.DAY_OF_YEAR), cal.get(Calendar.YEAR)) {
        formatDateLabel(cal)
    }

    val refresh: () -> Unit = {
        if (!refreshing && quote != null) {
            refreshing = true
            scope.launch {
                quote = HitokotoApi.fetch()
                refreshing = false
            }
        }
    }

    // 刷新图标旋转
    val spin by animateFloatAsState(
        targetValue = if (refreshing) 1f else 0f,
        animationSpec = tween(400),
        label = "hitokotoSpin"
    )
    val interaction = remember { MutableInteractionSource() }
    val hitokotoContext = androidx.compose.ui.platform.LocalContext.current
    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    val timeColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.92f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glass(
                RoundedCornerShape(20.dp),
                rememberGlassColors(
                    tintTop = Color.White.copy(alpha = 0.08f),
                    tintBottom = Color.White.copy(alpha = 0.03f)
                )
            )
            .clickable(interactionSource = interaction, indication = null) {
                if (AppSettings.soundEnabled) {
                    ClickSound.play(hitokotoContext)
                    haptics.performHapticFeedback(
                        androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                    )
                }
                refresh()
            }
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {
        // ---- 上段：大号时钟 + 右侧日期 ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedTimeSeg(hh, timeColor)
                AnimatedTimeColon(timeColor, nowSec)
                AnimatedTimeSeg(mm, timeColor)
                AnimatedTimeColon(timeColor, nowSec)
                AnimatedTimeSeg(ss, timeColor.copy(alpha = 0.62f))
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = dateText,
                fontSize = 11.sp,
                letterSpacing = 0.8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.60f),
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        Spacer(Modifier.height(13.dp))

        // ---- 中间：渐隐细玻璃分隔线 ----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.16f),
                            Color.White.copy(alpha = 0.16f),
                            Color.Transparent
                        )
                    ),
                    RoundedCornerShape(1.dp)
                )
        )

        Spacer(Modifier.height(13.dp))

        // ---- 下段：光棒 + 引文（打字机渐显）+ 刷新 ----
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 竖向光棒（引用视觉锚点）
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(34.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.0f),
                                Color.White.copy(alpha = 0.55f),
                                Color.White.copy(alpha = 0.0f)
                            )
                        ),
                        RoundedCornerShape(2.dp)
                    )
            )
            Spacer(Modifier.width(14.dp))

            // 引文 + 出处（高度自适应动画）
            Column(
                modifier = Modifier
                    .weight(1f)
                    .animateContentSize(tween(320))
            ) {
                val q = quote
                if (q == null) {
                    Text(
                        text = "…",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                } else {
                    val revealing = revealCount < q.text.length
                    // 渐显完成后文字由半亮平滑过渡到全亮
                    val textAlpha by animateFloatAsState(
                        targetValue = if (revealing) 0.65f else 0.85f,
                        animationSpec = tween(400),
                        label = "quoteAlpha"
                    )
                    val text = if (revealing) q.text.take(revealCount) + "▎" else q.text
                    Text(
                        text = text,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        letterSpacing = 0.2.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = textAlpha),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    // 出处：文字展开完毕后淡入上浮
                    AnimatedVisibility(
                        visible = !revealing && q.from.isNotBlank(),
                        enter = fadeIn(tween(420)) + slideInVertically(tween(420)) { it / 2 },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "—— ${q.from}",
                            fontSize = 11.sp,
                            letterSpacing = 0.4.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.62f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            // 刷新指示（加载中呼吸闪烁）
            val iconAlpha by animateFloatAsState(
                targetValue = if (refreshing) 0.25f else 0.45f,
                animationSpec = tween(300),
                label = "hitokotoIconAlpha"
            )
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = iconAlpha),
                modifier = Modifier
                    .size(17.dp)
                    .graphicsLayer { rotationZ = spin * 360f }
            )
        }
    }
}

/** 日期标签：跟随界面语言（中文「8月15日 · 周五」，其它语言本地化短格式） */
private fun formatDateLabel(cal: Calendar): String {
    val lang = AppLocale.savedLang
    val loc = if (lang == "system") Locale.getDefault()
    else Locale.forLanguageTag(lang.replace('_', '-'))
    return try {
        if (loc.language == "zh") {
            java.text.SimpleDateFormat("M月d日 · E", loc).format(cal.time)
        } else {
            java.text.SimpleDateFormat("MMM d · E", loc).format(cal.time)
        }
    } catch (_: Exception) {
        ""
    }
}

/** 时钟数字段：变化时旧数字上滑淡出、新数字自下滑入（翻牌质感） */
@Composable
private fun AnimatedTimeSeg(seg: String, color: Color) {
    AnimatedContent(
        targetState = seg,
        transitionSpec = {
            (slideInVertically(tween(340, easing = EaseOutCubic)) { it / 2 } +
                    fadeIn(tween(340)))
                .togetherWith(
                    slideOutVertically(tween(340, easing = EaseOutCubic)) { -it / 2 } +
                            fadeOut(tween(200))
                )
        },
        label = "timeSeg"
    ) { s ->
        Text(
            text = s,
            style = TextStyle(
                fontSize = 21.sp,
                fontWeight = FontWeight.Medium,
                fontFeatureSettings = "tnum",
                color = color,
                letterSpacing = 0.5.sp
            )
        )
    }
}

/** 时钟冒号：随秒呼吸闪烁，强化「走时」生命感 */
@Composable
private fun AnimatedTimeColon(color: Color, nowSec: Long) {
    val alpha by animateFloatAsState(
        targetValue = if (nowSec % 2 == 0L) 0.85f else 0.35f,
        animationSpec = tween(500),
        label = "colonBlink"
    )
    Text(
        text = ":",
        style = TextStyle(
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            fontFeatureSettings = "tnum",
            color = color.copy(alpha = alpha)
        ),
        modifier = Modifier.padding(horizontal = 1.dp)
    )
}

@Composable
private fun PermissionStatusCard(
    status: PermissionStatus,
    onRefresh: () -> Unit
) {
    val visual = when (status) {
        PermissionStatus.ROOT -> PermissionVisual(
            Icons.Default.Shield,
            null, null,
            AppColors.successAdaptive(), "Root 权限已激活", "可通过 su 命令提权操作", true
        )
        PermissionStatus.SHELL -> PermissionVisual(
            Icons.Outlined.ShieldMoon,
            null, null,
            AppColors.infoAdaptive(), "Shell 权限已激活", "Shizuku / ADB Shell 已就绪", true
        )
        PermissionStatus.FILE -> PermissionVisual(
            Icons.Default.FolderOpen,
            null, null,
            AppColors.successAdaptive(), "文件权限已激活", "已授予所有文件访问，可无 Root 导入", true
        )
        PermissionStatus.NONE -> PermissionVisual(
            Icons.Outlined.WarningAmber,
            null, null,
            AppColors.warning, "未激活权限", "可授权「所有文件访问」或使用 Root/Shizuku", false
        )
        PermissionStatus.CHECKING -> PermissionVisual(
            Icons.Default.Shield,
            null, null,
            MaterialTheme.colorScheme.onSurfaceVariant, "正在检测权限…", "请稍候", false
        )
    }

    val baseGlass = rememberGlassColors()
    val tintTop by animateColorAsState(
        visual.tintTop ?: baseGlass.tintTop,
        tween(450), label = "permBgTop"
    )
    val tintBottom by animateColorAsState(
        visual.tintBottom ?: baseGlass.tintBottom,
        tween(450), label = "permBgBottom"
    )

    GlassCard(
        shape = RoundedCornerShape(24.dp),
        tintTop = tintTop,
        tintBottom = tintBottom,
        contentPadding = 14.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 状态图标徽章：激活时白色呼吸光晕，未激活时静态
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .glow(
                        if (visual.active) Color.White.copy(alpha = 0.18f)
                        else Color.White.copy(alpha = 0.08f),
                        radiusFraction = 1.5f
                    )
                    .glass(CircleShape, rememberGlassColors()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    visual.icon,
                    contentDescription = null,
                    tint = if (visual.active) Color.White
                    else Color(0xFFB9C0D4),
                    modifier = Modifier.size(19.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        visual.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.2.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.width(7.dp))
                    // 状态点：激活 = 白色呼吸；未激活 = 暗灰缓慢待机呼吸
                    GlowDot(
                        color = if (visual.active) Color.White
                        else Color(0xFF6B7186),
                        dotSize = 7.dp
                    )
                }
                Spacer(Modifier.height(1.dp))
                Text(
                    visual.subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (status != PermissionStatus.CHECKING) {
                GlassIconButton(
                    icon = Icons.Default.Refresh,
                    contentDescription = AppLocale.t("刷新"),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = onRefresh,
                    size = 32.dp
                )
            }
        }
    }
}

// ====================================================================
// 已导入文件卡
// ====================================================================

@Composable
private fun ImportedFileCard(
    file: ImportedFile,
    onClick: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        contentPadding = 12.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .glass(
                        RoundedCornerShape(15.dp),
                        rememberGlassColors()
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Watch,
                    contentDescription = null,
                    tint = Color(0xFFE9EBF4),
                    modifier = Modifier.size(21.dp)
                )
            }
            Spacer(Modifier.width(11.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    file.fileName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "ID ${file.id}",
                        fontSize = 11.sp,
                        fontFamily = NumericFonts,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    if (file.name.isNotEmpty()) {
                        Text(
                            file.name,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(Modifier.height(1.dp))
                Text(
                    RecordStore.formatBytes(file.fileSize),
                    fontSize = 10.sp,
                    fontFamily = NumericFonts,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ====================================================================
// 无权限弹窗
// ====================================================================

@Composable
private fun NoPermissionDialog(
    onDismiss: () -> Unit,
    onAuthorizeShizuku: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        DialogEntranceWrapper {
            GlassCard(shape = RoundedCornerShape(28.dp), contentPadding = 24.dp) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .glow(AppColors.warning, radiusFraction = 1.6f)
                            .glass(CircleShape, rememberGlassColors()),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "权限不可用",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "一键导入需要 Root 或 Shell 权限。\n" +
                                "请通过 Shizuku 授权或确保设备已 Root。\n\n" +
                                "Shizuku: 安装并启动 Shizuku 服务后\n点击下方按钮授权",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )
                    Spacer(Modifier.height(20.dp))
                    GlassButton(
                        text = "授权 Shizuku",
                        icon = Icons.Outlined.ShieldMoon,
                        onClick = onAuthorizeShizuku,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    GlassButton(
                        text = "知道了",
                        onClick = onDismiss,
                        style = GlassButtonStyle.Glass,
                        shimmer = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ====================================================================
// 公告弹窗（纯公告内容，与版本更新完全分离）
// ====================================================================

@Composable
internal fun AnnouncementDialog(
    announcement: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        DialogEntranceWrapper {
            GlassCard(shape = RoundedCornerShape(28.dp), contentPadding = 24.dp) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .glow(Color(0xFFE9EBF4).copy(alpha = 0.30f), radiusFraction = 1.6f)
                            .glass(CircleShape, rememberGlassColors()),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Public,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "公告",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = announcement,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )

                    Spacer(Modifier.height(18.dp))
                    GlassButton(
                        text = "知道了",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ====================================================================
// 版本更新弹窗（独立于公告：版本对比 + 下载）
// ====================================================================

@Composable
internal fun UpdateDialog(
    latestVersion: String,
    onUpdate: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        DialogEntranceWrapper {
            GlassCard(shape = RoundedCornerShape(28.dp), contentPadding = 24.dp) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .glow(Color(0xFFD9DEEB).copy(alpha = 0.30f), radiusFraction = 1.6f)
                            .glass(CircleShape, rememberGlassColors()),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "发现新版本",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(12.dp))
                    GlassCard(
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = 12.dp
                    ) {
                        Column {
                            Text(
                                text = AppLocale.tf("当前版本  v{0}", BuildConfig.VERSION_NAME),
                                fontSize = 12.sp,
                                fontFamily = NumericFonts,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = AppLocale.tf("最新版本  v{0}", latestVersion),
                                fontSize = 12.sp,
                                fontFamily = NumericFonts,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    GlassButton(
                        text = "下载更新",
                        icon = Icons.Default.Download,
                        onClick = onUpdate,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    GlassButton(
                        text = "稍后再说",
                        onClick = onDismiss,
                        style = GlassButtonStyle.Glass,
                        shimmer = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ====================================================================
// 下载进度弹窗
// ====================================================================

@Composable
internal fun DownloadProgressDialog(
    progress: Int,
    downloadedBytes: Long,
    totalBytes: Long,
    speedBytesPerSec: Long,
    isDownloading: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    onOpenBrowser: () -> Unit,
    onCancel: () -> Unit
) {
    val hasError = error != null

    Dialog(onDismissRequest = { if (!isDownloading) onDismiss() else onCancel() }) {
        DialogEntranceWrapper {
            GlassCard(shape = RoundedCornerShape(28.dp), contentPadding = 24.dp) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (hasError) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .glow(AppColors.danger, radiusFraction = 1.6f)
                                .glass(CircleShape, rememberGlassColors()),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.WarningAmber,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "下载失败",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = error ?: "",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(20.dp))
                        GlassButton(
                            text = "重试",
                            onClick = onRetry,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        GlassButton(
                            text = "使用浏览器下载",
                            onClick = onOpenBrowser,
                            style = GlassButtonStyle.Glass,
                            shimmer = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        GlassButton(
                            text = "取消",
                            onClick = onDismiss,
                            style = GlassButtonStyle.Glass,
                            shimmer = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .glow(AppColors.infoAdaptive(), radiusFraction = 1.6f)
                                .glass(CircleShape, rememberGlassColors()),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDownloading) {
                                LiquidDotsLoader(dotSize = 8.dp)
                            } else {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = if (isDownloading) "正在下载更新" else "下载完成",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (isDownloading) {
                            Spacer(Modifier.height(14.dp))
                            val animatedProgress by animateFloatAsState(
                                targetValue = progress.coerceIn(0, 100) / 100f,
                                animationSpec = tween(280),
                                label = "dlProgress"
                            )
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(9.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.12f),
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = formatBytes(downloadedBytes),
                                    fontSize = 11.sp,
                                    fontFamily = NumericFonts,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (speedBytesPerSec > 0) {
                                    Text(
                                        text = "${formatBytes(speedBytesPerSec)}/s",
                                        fontSize = 11.sp,
                                        fontFamily = NumericFonts,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = if (totalBytes > 0) "$progress%" else formatBytes(downloadedBytes),
                                    fontSize = 11.sp,
                                    fontFamily = NumericFonts,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (totalBytes > 0) formatBytes(totalBytes) else "未知大小",
                                    fontSize = 11.sp,
                                    fontFamily = NumericFonts,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (speedBytesPerSec > 0 && totalBytes > 0 && progress < 100) {
                                val remainSec = (totalBytes - downloadedBytes) / speedBytesPerSec
                                if (remainSec > 0) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = AppLocale.tf("预计剩余 {0}", formatDuration(remainSec)),
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(Modifier.height(14.dp))
                            GlassButton(
                                text = "取消下载",
                                onClick = onCancel,
                                style = GlassButtonStyle.Glass,
                                shimmer = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "正在调起安装界面…",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/** 弹窗入场动画包装 */
@Composable
internal fun DialogEntranceWrapper(content: @Composable () -> Unit) {
    var shown by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) { shown = true }
    AnimatedVisibility(
        visible = shown,
        enter = fadeIn(tween(180)) + androidx.compose.animation.scaleIn(
            initialScale = 0.85f,
            animationSpec = spring(dampingRatio = 0.72f, stiffness = 480f)
        ),
        exit = fadeOut(tween(150))
    ) {
        content()
    }
}

// ====================================================================
// 密钥提取瓷砖 — 首页快捷入口
//
//   · 点击选 ZIP → 自动解压提取密钥
//   · 提取中显示进度脉冲
//   · 有结果时右上角显示清除按钮
// ====================================================================

@Composable
private fun KeyExtractionTile(
    state: UiState,
    zipPicker: androidx.activity.compose.ManagedActivityResultLauncher<String, Uri?>,
    onExtract: () -> Unit,
    onClear: () -> Unit
) {
    val clickContext = androidx.compose.ui.platform.LocalContext.current
    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isExtracting = state.isExtracting

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassShadow(8.dp, RoundedCornerShape(22.dp))
            .pressScale(interaction, pressedScale = 0.96f)
            .glass(RoundedCornerShape(22.dp), rememberGlassColors())
            .pressRipple(interaction, clipShape = RoundedCornerShape(22.dp), color = Color.White, intensity = 1f)
            .clickable(
                interactionSource = interaction,
                indication = null
            ) {
                if (AppSettings.soundEnabled) {
                    ClickSound.play(clickContext)
                    haptics.performHapticFeedback(
                        androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                    )
                }
                if (!isExtracting) {
                    if (state.extractResult != null) {
                        onClear()
                    }
                    zipPicker.launch("*/*")
                }
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧图标
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .glass(
                        RoundedCornerShape(13.dp),
                        rememberGlassColors()
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isExtracting) {
                    // 提取中：旋转刷新图标
                    val rotation by androidx.compose.animation.core.animateFloatAsState(
                        targetValue = 360f,
                        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                            animation = androidx.compose.animation.core.tween(800, easing = androidx.compose.animation.core.LinearEasing)
                        ),
                        label = "extractSpin"
                    )
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color(0xFFE9EBF4),
                        modifier = Modifier
                            .size(18.dp)
                            .graphicsLayer { rotationZ = rotation }
                    )
                } else {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFFE9EBF4),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // 中间文字
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isExtracting) "正在提取 Token…" else "提取 Token",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.2.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = when {
                        isExtracting -> "解压并扫描日志文件中"
                        state.extractResult?.success == true -> {
                            val tokenCount = state.extractResult.tokens.size.let {
                                if (it > 0) it else state.extractResult.authKeys["token"]?.size ?: 0
                            }
                            if (tokenCount > 0) "已提取 $tokenCount 个 Token · 点击重新选择"
                            else "未找到 Token · 点击重新选择"
                        }
                        state.extractResult?.success == false -> "提取失败 · 点击重试"
                        else -> "从日志 ZIP 中提取 Token + 设备名"
                    },
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 右侧清除按钮（有结果时）
            if (state.extractResult != null && !isExtracting) {
                GlassIconButton(
                    icon = Icons.Default.Delete,
                    contentDescription = AppLocale.t("清除"),
                    tint = AppColors.dangerAdaptive(),
                    size = 28.dp,
                    tintTop = AppColors.danger.copy(alpha = 0.14f),
                    tintBottom = AppColors.danger.copy(alpha = 0.06f),
                    onClick = { onClear() }
                )
            } else if (!isExtracting) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ====================================================================
// 密钥提取结果卡片 — 仅展示 Token + 设备名称
//
//   · 从日志 ZIP 中提取 Token，并显示关联设备名
//   · 每个值可单独点击一键复制
// ====================================================================

@Composable
private fun KeyExtractionResultCard(
    result: LogKeyExtractor.ExtractResult?,
    isExtracting: Boolean,
    onClear: () -> Unit
) {
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    var copiedKey by remember { mutableStateOf<String?>(null) }

    // 优先使用带设备信息的 tokens；兼容旧结构
    val tokenInfos: List<LogKeyExtractor.TokenInfo> = when {
        result == null -> emptyList()
        result.tokens.isNotEmpty() -> result.tokens
        else -> (result.authKeys["token"] ?: emptyList()).map {
            LogKeyExtractor.TokenInfo(token = it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassShadow(8.dp, RoundedCornerShape(22.dp))
            .glass(RoundedCornerShape(22.dp), rememberGlassColors())
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFFE9EBF4),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Token 提取结果",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.2.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (result != null) {
                    GlassIconButton(
                        icon = Icons.Default.Delete,
                        contentDescription = "清除",
                        tint = AppColors.dangerAdaptive(),
                        size = 28.dp,
                        onClick = { onClear(); copiedKey = null }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Color.White.copy(alpha = 0.08f))
            )
            Spacer(Modifier.height(12.dp))

            if (isExtracting) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val rotation by androidx.compose.animation.core.animateFloatAsState(
                        targetValue = 360f,
                        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                            animation = androidx.compose.animation.core.tween(
                                800,
                                easing = androidx.compose.animation.core.LinearEasing
                            )
                        ),
                        label = "resultSpin"
                    )
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(16.dp)
                            .graphicsLayer { rotationZ = rotation }
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "正在扫描日志文件…",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (result == null) {
                Text(
                    text = "暂无结果",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (!result.success) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.WarningAmber,
                        contentDescription = null,
                        tint = AppColors.warning,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = result.errorMessage ?: "提取失败",
                        fontSize = 12.sp,
                        color = AppColors.warning
                    )
                }
            } else {
                Text(
                    text = AppLocale.tf(
                        "扫描 {0} 个文件（共 {1} 个）",
                        result.scannedFilesCount,
                        result.rawFilesCount
                    ),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                if (tokenInfos.isNotEmpty()) {
                    // 只展示优先级最高的当前 Token（主日志命中优先），历史 bak 中的旧值不展示
                    val primary = tokenInfos.first()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFE9EBF4), CircleShape)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Token",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.3.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    KeyRow(
                        value = primary.token,
                        deviceName = primary.deviceName,
                        model = primary.model,
                        isCopied = copiedKey == primary.token,
                         onCopy = {
                             clipboardManager.setText(
                                 androidx.compose.ui.text.AnnotatedString(primary.token)
                             )
                             copiedKey = primary.token
                         }
                    )
                } else {
                    Text(
                        text = "未在日志中找到 Token",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/** 单个 Token 行：设备名 + 等宽 Token + 复制按钮 */
@Composable
private fun KeyRow(
    value: String,
    deviceName: String = "",
    model: String = "",
    isCopied: Boolean,
    onCopy: () -> Unit
) {
    val clickContext = androidx.compose.ui.platform.LocalContext.current
    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

    // 只展示设备名称，不拼接型号
    val subtitle = when {
        deviceName.isNotEmpty() -> deviceName.trim()
        model.isNotEmpty() -> model.trim()
        else -> ""
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .glass(
                RoundedCornerShape(10.dp),
                rememberGlassColors()
            )
            .clickable(
                interactionSource = interaction,
                indication = null
            ) {
                if (AppSettings.soundEnabled) {
                    ClickSound.play(clickContext)
                    haptics.performHapticFeedback(
                        androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                    )
                }
                onCopy()
            }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // 设备名称（提取结果处重点展示）
            if (subtitle.isNotEmpty()) {
                Text(
                    text = "设备：$subtitle",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF7CFBA7),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
            } else {
                Text(
                    text = "设备：未知",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
            }
            Text(
                text = value,
                fontSize = 11.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(6.dp))
        Icon(
            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
            contentDescription = if (isCopied) "已复制" else "复制",
            tint = if (isCopied) Color(0xFF7CFBA7) else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
    }
}


// ====================================================================
// ---- 以下为 ModifyScreen.kt 内容 ----
// ====================================================================


/**
 * 修改页：液态玻璃重构版
 *
 * 结构：
 *   1. 文件拖放区（大玻璃卡，加载成功后染绿 + 对勾徽章弹出）
 *   2. 文件信息卡（ID / 名称 / 大小 / 文件名）
 *   3. 新 ID 设置卡（玻璃输入框 + 实时校验指示 + 模式胶囊 + 骰子生成）
 *   4. 名称修改卡（保持 / 自定义 胶囊 + 展开输入框）
 *   5. 保存按钮（渐变玻璃主按钮 + 微光扫过）
 *   6. 快捷操作网格（复制原/新 ID、重置、历史）
 */
@Composable
fun ModifyScreen(
    viewModel: MainViewModel,
    state: UiState,
    onNavigateToHistory: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var localToast by remember { mutableStateOf<String?>(null) }

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = try {
                context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
                }
            } catch (_: Exception) {
                null
            } ?: it.toString().substringAfterLast("/").let { name ->
                java.net.URLDecoder.decode(name, "UTF-8")
            }
            viewModel.loadFile(it, fileName)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // ============ 标题 ============
        StaggeredItem(index = 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBadge(icon = Icons.Default.Tag, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "修改表盘",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "修改 ID 与名称，导出到 Download",  // 走 Text 包装器翻译
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ============ 文件选择区 ============
        StaggeredItem(index = 1) {
            FileDropCard(
                fileName = state.originalFileName,
                hasFile = state.fileInfo != null,
                onClick = {
                    if (viewModel.requireLogin()) filePicker.launch(arrayOf("*/*"))
                }
            )
        }

        // ============ 已加载：全部功能卡片（展开动画） ============
        AnimatedVisibility(
            visible = state.fileInfo != null,
            enter = fadeIn(tween(300)) + expandVertically(
                tween(360, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(tween(200)) + shrinkVertically(tween(240))
        ) {
            val info = state.fileInfo ?: return@AnimatedVisibility

            Column {
                Spacer(Modifier.height(14.dp))

                // ---- 文件信息卡 ----
                StaggeredItem(index = 2) {
                    SectionCard(
                        icon = Icons.Default.Description,
                        title = "文件信息",
                        tint = MaterialTheme.colorScheme.primary
                    ) {
                        InfoRow("表盘 ID", info.id, mono = true, highlight = true)
                        InfoDivider()
                        InfoRow("表盘名称", info.name.ifEmpty { "(空)" })
                        InfoDivider()
                        InfoRow("文件大小", RecordStore.formatBytes(info.size))
                        InfoDivider()
                        InfoRow("文件名", state.originalFileName, maxLines = true)
                    }
                }

                Spacer(Modifier.height(14.dp))

                // ---- 设置新 ID ----
                StaggeredItem(index = 3) {
                    SectionCard(
                        icon = Icons.Default.Tag,
                        title = "设置新 ID",
                        tint = MaterialTheme.colorScheme.secondary
                    ) {
                        GlassTextField(
                            value = state.newId,
                            onValueChange = { viewModel.setNewId(it) },
                            placeholder = "输入 9 或 12 位纯数字",
                            keyboardType = KeyboardType.Number,
                            mono = true,
                            maxLength = 12
                        )

                        // 实时校验指示（图标 + 文案随状态切换）
                        val error = WatchfaceParser.validateId(state.newId)
                        val valid = state.newId.isNotEmpty() && error == null
                        IdValidationHint(id = state.newId, valid = valid)

                        Spacer(Modifier.height(12.dp))

                        // 位数模式胶囊
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "random" to "随机 9/12",
                                "9" to "9 位",
                                "12" to "12 位"
                            ).forEach { (mode, label) ->
                                GlassChip(
                                    text = label,
                                    selected = state.selectedMode == mode,
                                    onClick = { viewModel.selectMode(mode) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // 骰子生成
                        GlassButton(
                            text = "生成随机 ID",
                            icon = Icons.Default.Casino,
                            onClick = { viewModel.generateRandomId() },
                            style = GlassButtonStyle.Glass,
                            height = 42.dp,
                            shimmer = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // ---- 修改表盘名称 ----
                StaggeredItem(index = 4) {
                    SectionCard(
                        icon = Icons.Default.Label,
                        title = "表盘名称",
                        tint = AppColors.infoAdaptive()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "keep" to "保持原名",
                                "custom" to "自定义名称"
                            ).forEach { (mode, label) ->
                                GlassChip(
                                    text = label,
                                    selected = state.nameMode == mode,
                                    onClick = { viewModel.selectNameMode(mode) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // 自定义名称输入框（展开动画）
                        AnimatedVisibility(
                            visible = state.nameMode == "custom",
                            enter = fadeIn(tween(280)) + expandVertically(
                                tween(320, easing = FastOutSlowInEasing)
                            ),
                            exit = fadeOut(tween(200)) + shrinkVertically(tween(220))
                        ) {
                            Column {
                                Spacer(Modifier.height(12.dp))
                                GlassTextField(
                                    value = state.customName,
                                    onValueChange = { viewModel.setCustomName(it) },
                                    placeholder = "输入新的表盘名称",
                                    maxLength = 30
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "名称将写入表盘文件，留空则清除名称",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // ---- 保存按钮 ----
                StaggeredItem(index = 5) {
                    GlassButton(
                        text = "保存修改",
                        icon = Icons.Default.Save,
                        onClick = { viewModel.saveFile() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                }

                Spacer(Modifier.height(14.dp))

                // ---- 快捷操作 ----
                StaggeredItem(index = 6) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GlassToolCard(
                                icon = Icons.Default.ContentCopy,
                                title = "复制原 ID",
                                subtitle = info.id,
                                iconTint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(info.id))
                                    localToast = "原 ID 已复制"
                                }
                            )
                            GlassToolCard(
                                icon = Icons.Default.ContentCopy,
                                title = "复制新 ID",
                                subtitle = state.newId.ifEmpty { "尚未生成" },
                                iconTint = AppColors.successAdaptive(),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (state.newId.isNotEmpty()) {
                                        clipboardManager.setText(AnnotatedString(state.newId))
                                        localToast = "新 ID 已复制"
                                    } else {
                                        localToast = "暂无新 ID，请先生成"
                                    }
                                }
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GlassToolCard(
                                icon = Icons.Default.Refresh,
                                title = "重新选择",
                                subtitle = "清除当前状态",
                                iconTint = AppColors.warning,
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.resetAll() }
                            )
                            GlassToolCard(
                                icon = Icons.Default.History,
                                title = "修改记录",
                                subtitle = "查看历史",
                                iconTint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToHistory
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(110.dp))
    }

    localToast?.let { msg ->
        ToastMessage(message = msg, onFinished = { localToast = null })
    }
}

// ====================================================================
// 文件选择大卡（加载后染绿 + 徽章弹出）
// ====================================================================

@Composable
private fun FileDropCard(
    fileName: String,
    hasFile: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val interaction = remember { MutableInteractionSource() }

    val success = AppColors.successAdaptive()
    val borderSpec by animateColorAsState(
        targetValue = if (hasFile) success else MaterialTheme.colorScheme.primary,
        animationSpec = tween(400),
        label = "dropBorder"
    )
    val colors = rememberGlassColors(
        tintTop = if (hasFile) success.copy(alpha = 0.16f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
        tintBottom = if (hasFile) success.copy(alpha = 0.06f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
    )
    val shape = RoundedCornerShape(22.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassShadow(8.dp, shape)
            .pressScale(interaction, pressedScale = 0.97f)
            .glass(shape, colors)
            .drawBehind {
                // 呼吸描边：加载后为绿色，未加载为蓝色
                val outline = shape.createOutline(size, layoutDirection, this)
                drawOutline(
                    outline = outline,
                    brush = Brush.linearGradient(
                        listOf(
                            borderSpec.copy(alpha = 0.75f),
                            borderSpec.copy(alpha = 0.25f)
                        )
                    ),
                    style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            .pressRipple(interaction, clipShape = shape, color = if (hasFile) Color(0xFFE9EBF4) else Color(0xFFD9DEEB), intensity = 1.15f)
            .clickable(interactionSource = interaction, indication = null) {
                if (AppSettings.soundEnabled) {
                    ClickSound.play(context)
                }
                onClick()
            }
            .padding(vertical = 22.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 图标容器：加载后弹出对勾徽章
            Box {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .glow(borderSpec.copy(alpha = 0.30f), radiusFraction = 1.5f)
                        .glass(CircleShape, rememberGlassColors()),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.FolderOpen,
                        contentDescription = "选择文件",
                        // 跟随主题，避免深浅色/缩放后发灰发虚
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }
                // 加载成功徽章（弹簧弹出）
                androidx.compose.animation.AnimatedVisibility(
                    visible = hasFile,
                    modifier = Modifier.align(Alignment.TopEnd),
                    enter = scaleIn(
                        initialScale = 0.2f,
                        animationSpec = spring(dampingRatio = 0.35f, stiffness = 600f)
                    ) + fadeIn(tween(150)),
                    exit = fadeOut(tween(120))
                ) {
                    Box(
                        modifier = Modifier
                            .size(19.dp)
                            .glow(Color.White.copy(alpha = 0.30f), radiusFraction = 1.5f)
                            .glass(
                                CircleShape,
                                rememberGlassColors(
                                    tintTop = Color.White.copy(alpha = 0.30f),
                                    tintBottom = Color.White.copy(alpha = 0.14f)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFFF3F5FA),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            AnimatedContent(
                targetState = fileName to hasFile,
                transitionSpec = {
                    (fadeIn(tween(260)) + slideInHorizontally(tween(300)) { it / 6 }) togetherWith
                            (fadeOut(tween(160)) + slideOutHorizontally(tween(220)) { -it / 6 })
                },
                label = "dropText"
            ) { (name, loaded) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (loaded) {
                        Text(
                            text = name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = success,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = "已加载 · 点击重新选择",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "点击选择表盘文件",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = "支持 .bin 表盘文件",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ====================================================================
// 分区卡片：图标 + 标题 + 玻璃内容区
// ====================================================================

@Composable
private fun SectionCard(
    icon: ImageVector,
    title: String,
    tint: Color,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    GlassCard(shape = RoundedCornerShape(22.dp), contentPadding = 16.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(icon = icon, tint = tint, size = 30.dp, iconSize = 15.dp)
            Spacer(Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(Modifier.height(13.dp))
        content()
    }
}

// ====================================================================
// 信息行
// ====================================================================

@Composable
private fun InfoRow(
    label: String,
    value: String,
    mono: Boolean = false,
    highlight: Boolean = false,
    maxLines: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = if (mono) NumericFonts else null,
            color = if (highlight) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            maxLines = if (maxLines) 1 else Int.MAX_VALUE,
            overflow = if (maxLines) TextOverflow.Ellipsis else TextOverflow.Clip,
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(start = 14.dp)
        )
    }
}

@Composable
private fun InfoDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(vertical = 0.dp)
            .glass(
                RoundedCornerShape(1.dp),
                rememberGlassColors(
                    tintTop = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                    tintBottom = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.20f)
                )
            )
    )
}

// ====================================================================
// 玻璃输入框
// ====================================================================

@Composable
private fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    mono: Boolean = false,
    maxLength: Int = 100
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val primary = MaterialTheme.colorScheme.primary
    val borderColor by animateColorAsState(
        targetValue = if (focused) primary else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(240),
        label = "fieldBorder"
    )
    val shape = RoundedCornerShape(14.dp)
    val colors = rememberGlassColors(
        tintTop = if (focused) primary.copy(alpha = 0.10f) else null,
        tintBottom = if (focused) primary.copy(alpha = 0.04f) else null
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .glassShadow(4.dp, shape)
            .glass(shape, colors)
            .drawBehind {
                val outline = shape.createOutline(size, layoutDirection, this)
                drawOutline(
                    outline = outline,
                    brush = Brush.linearGradient(
                        listOf(borderColor.copy(alpha = 0.8f), borderColor.copy(alpha = 0.3f))
                    ),
                    style = Stroke(width = if (focused) 1.6.dp.toPx() else 1.1.dp.toPx())
                )
            }
            .padding(horizontal = 14.dp, vertical = 13.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = if (mono) NumericFonts else AppFonts,
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(primary),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            interactionSource = interactionSource,
            decorationBox = { inner ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        inner()
                    }
                    // 字符计数（聚焦且有内容时显示）
                    AnimatedVisibility(visible = focused && value.isNotEmpty()) {
                        Text(
                            text = "${value.length}/$maxLength",
                            fontSize = 10.sp,
                            fontFamily = NumericFonts,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ====================================================================
// ID 校验提示（图标 + 文案随状态动画切换）
// ====================================================================

@Composable
private fun IdValidationHint(id: String, valid: Boolean) {
    AnimatedContent(
        targetState = when {
            id.isEmpty() -> null
            valid -> "✓"
            else -> "✗"
        },
        transitionSpec = {
            (fadeIn(tween(220)) + scaleIn(initialScale = 0.7f, animationSpec = spring(
                dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium
            ))) togetherWith fadeOut(tween(140))
        },
        label = "idHint",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) { mark ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            when (mark) {
                null -> {
                    Icon(
                        Icons.Default.Tag,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "仅支持 9 位或 12 位纯数字",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                "✓" -> {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFE9EBF4)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        AppLocale.tf("有效的 {0} 位 ID", id.length),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
                else -> {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFE9EBF4)
                    )
                    Spacer(Modifier.width(5.dp))
                    val error = WatchfaceParser.validateId(id)
                    Text(
                        error ?: "ID 无效",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.warning
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            if (id.isNotEmpty()) {
                Text(
                    text = AppLocale.tf("{0} 位", id.length),
                    fontSize = 11.sp,
                    fontFamily = NumericFonts,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ====================================================================
// 图标徽章
// ====================================================================

@Composable
internal fun IconBadge(
    icon: ImageVector,
    tint: Color,
    size: androidx.compose.ui.unit.Dp = 36.dp,
    iconSize: androidx.compose.ui.unit.Dp = 18.dp
) {
    // 图标无彩色规格：仅保留极淡柔光，容器与图标统一中性（圆形容器，避免图标后方出现方形底）
    Box(
        modifier = Modifier
            .size(size)
            .glow(Color.White.copy(alpha = 0.13f), radiusFraction = 1.4f)
            .glass(
                CircleShape,
                rememberGlassColors()
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFFE9EBF4),
            modifier = Modifier.size(iconSize)
        )
    }
}

// ====================================================================
// 快捷操作玻璃小卡
// ====================================================================

@Composable
internal fun GlassToolCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        onClick = {
            if (AppSettings.soundEnabled) {
            }
            onClick()
        },
        shape = RoundedCornerShape(18.dp),
        contentPadding = 12.dp,
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconBadge(icon = icon, tint = iconTint)
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


// ====================================================================
// ---- 以下为 HistoryScreen.kt 内容 ----
// ====================================================================


/**
 * 记录页：液态玻璃重构版
 *
 * - 悬浮标题栏（记录数徽章 + 导出 + 清空）
 * - 玻璃记录卡：ID 迁移动画（旧 ID 划线 → 箭头脉冲 → 新 ID 高亮）
 * - 今天徽章带呼吸绿点
 * - 空状态液态动画
 */
@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    state: UiState
) {
    val context = LocalContext.current
    val records = state.records
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ============ 标题栏 ============
            item {
                StaggeredItem(index = 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconBadge(
                            icon = Icons.Default.History,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "修改记录",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = if (records.isEmpty()) "暂无记录"
                                else AppLocale.tf("共 {0} 条记录", records.size),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (records.isNotEmpty()) {
                            // 导出文本
                            GlassIconButton(
                                icon = Icons.Default.IosShare,
                                contentDescription = "导出记录",
                                tint = MaterialTheme.colorScheme.primary,
                                size = 34.dp,
                                onClick = {
                                    val text = RecordStore.exportText(context)
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, text)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(intent, "导出修改记录")
                                    )
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            // 清空
                            GlassIconButton(
                                icon = Icons.Default.DeleteSweep,
                                contentDescription = "清空记录",
                                tint = AppColors.dangerAdaptive(),
                                size = 34.dp,
                                tintTop = AppColors.danger.copy(alpha = 0.12f),
                                tintBottom = AppColors.danger.copy(alpha = 0.06f),
                                onClick = {
                                    showClearAllDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // ============ 空状态 ============
            if (records.isEmpty()) {
                item {
                    StaggeredItem(index = 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyHistoryState()
                        }
                    }
                }
            } else {
                // ============ 记录卡片（交错入场） ============
                itemsIndexed(records, key = { _, r -> "${r.time}|${r.oldId}|${r.newId}" }) { index, record ->
                    StaggeredItem(index = index + 1) {
                        HistoryRecordCard(
                            record = record,
                            onOpen = {
                                val intent = viewModel.openFile(index)
                                if (intent != null) {
                                    context.startActivity(intent)
                                }
                            },
                            onShare = {
                                val intent = viewModel.shareFile(index)
                                if (intent != null) {
                                    context.startActivity(
                                        Intent.createChooser(intent, "分享文件")
                                    )
                                }
                            },
                            onDelete = {
                                showDeleteDialog = index
                            }
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(96.dp)) }
        }
    }

    // ============ 删除确认弹窗 ============
    showDeleteDialog?.let { idx ->
        ConfirmDialog(
            title = "删除记录",
            message = "确定要删除这条修改记录吗？",
            onConfirm = {
                viewModel.deleteRecord(idx, false)
                showDeleteDialog = null
            },
            onDismiss = { showDeleteDialog = null }
        )
    }

    // ============ 清空确认弹窗 ============
    if (showClearAllDialog) {
        ConfirmDialog(
            title = "清空全部记录",
            message = AppLocale.tf("将删除全部 {0} 条记录，该操作不可恢复。", records.size),
            onConfirm = {
                viewModel.clearAllRecords(false)
                showClearAllDialog = false
            },
            onDismiss = { showClearAllDialog = false }
        )
    }
}

// ====================================================================
// 记录卡片
// ====================================================================

@Composable
private fun HistoryRecordCard(
    record: WatchfaceRecord,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val today = record.isToday()
    val successColor = if (today) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onSurfaceVariant

    GlassCard(shape = RoundedCornerShape(20.dp), contentPadding = 14.dp) {
        // ---- 顶部：时间徽章 + 操作按钮 ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 时间胶囊（今天 = 绿色 + 呼吸点）
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (today) {
                    GlowDot(color = Color.White, dotSize = 6.dp)
                    Spacer(Modifier.width(6.dp))
                }
                Text(
                    text = record.displayDate(),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = NumericFonts,
                    color = successColor
                )
                if (today) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "今天",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                GlassIconButton(
                    icon = Icons.Default.OpenInNew,
                    contentDescription = AppLocale.t("打开文件"),
                    tint = MaterialTheme.colorScheme.primary,
                    size = 30.dp,
                    onClick = onOpen
                )
                GlassIconButton(
                    icon = Icons.Default.Share,
                    contentDescription = "分享文件",
                    tint = AppColors.successAdaptive(),
                    size = 30.dp,
                    onClick = onShare
                )
                GlassIconButton(
                    icon = Icons.Default.Delete,
                    contentDescription = "删除记录",
                    tint = AppColors.dangerAdaptive(),
                    size = 30.dp,
                    tintTop = AppColors.danger.copy(alpha = 0.12f),
                    tintBottom = AppColors.danger.copy(alpha = 0.06f),
                    onClick = onDelete
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ---- ID 迁移：旧 → 新 ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "原 ID",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    record.oldId,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = NumericFonts,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            PulsingArrow()

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "新 ID",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    record.newId,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = NumericFonts,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // ---- 名称变更（存在时展示） ----
        AnimatedVisibility(
            visible = record.nameChanged && record.oldName.isNotEmpty() && record.newName != record.oldName,
            enter = fadeIn(tween(280)),
            exit = fadeOut(tween(180))
        ) {
            Column {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = Color(0xFFE9EBF4),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        record.oldName,
                        fontSize = 11.sp,
                        fontFamily = NumericFonts,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = TextDecoration.LineThrough,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        record.newName.ifEmpty { "(空)" },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = NumericFonts,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ---- 底部：表盘信息 ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(22.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .glass(
                            RoundedCornerShape(7.dp),
                            rememberGlassColors()
                        )
                )
                Icon(
                    Icons.Default.Watch,
                    contentDescription = null,
                    tint = Color(0xFFE9EBF4),
                    modifier = Modifier.size(11.dp)
                )
            }
            Spacer(Modifier.width(7.dp))
            Text(
                text = record.newName.ifEmpty { record.oldName.ifEmpty { AppLocale.t("(未命名)") } },
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = RecordStore.formatBytes(record.fileSize),
                fontSize = 10.5.sp,
                fontFamily = NumericFonts,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ====================================================================
// 脉冲箭头（左右轻微往返 + 透明度呼吸）
// ====================================================================

@Composable
private fun PulsingArrow() {
    val transition = rememberInfiniteTransition(label = "arrow")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrowT"
    )
    Icon(
        Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = null,
        tint = Color(0xFFE9EBF4).copy(alpha = 0.35f + 0.65f * t),
        modifier = Modifier
            .size(18.dp)
            .graphicsLayer { translationX = 4f * sin(t * Math.PI.toFloat() * 2f) }
    )
}

// ====================================================================
// 空状态（浮动图标 + 液态呼吸）
// ====================================================================

@Composable
private fun EmptyHistoryState() {
    val transition = rememberInfiniteTransition(label = "empty")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emptyT"
    )
    val offsetY by animateFloatAsState(
        targetValue = 8f * sin(t * Math.PI.toFloat()),
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow),
        label = "emptyY"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.graphicsLayer { translationY = offsetY }
    ) {
        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            // 外圈呼吸光环
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .alpha(0.25f + 0.2f * t)
                    .glass(CircleShape, rememberGlassColors())
            )
            // 内圈玻璃
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .glass(CircleShape, rememberGlassColors()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    tint = Color(0xFFE9EBF4),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            "暂无修改记录",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "修改表盘 ID 后记录将显示在此",
            fontSize = 11.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}


// ====================================================================
// ---- 以下为 SettingsScreen.kt 内容 ----
// ====================================================================


/**
 * 设置页（深色玻璃 · ColorOS 控制中心风格）
 *
 * 布局对齐 Lua 版 wanfeng.menu：顶部页签 + 每页平铺声明式控件，
 * 一页一个 WanFeng.Page 代码块，一眼看懂该页有哪些开关 / 按钮 / 拉条。
 *
 * 页面：
 *   1. 账号    卡密登录状态 + 取消解锁
 *   2. 权限    Root / Shell / 文件访问 状态卡 + 重新检测 / Shizuku 授权
 *   3. 界面    显示密度拉条 + 音效 + 震动
 *   4. 外观    背景样式 / 背景颜色 + 雪花飘落
 *   5. 更新    检查更新 + 查看公告 + 启动时显示公告
 *   6. 关于    表盘 ID 工具 + Github 仓库
 *
 * 所有图标均置于透明玻璃容器中（玻璃容器 + 实心图标规格）。
 */
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    state: UiState
) {
    var showBgDialog by remember { mutableStateOf(false) }
    var showColorDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // 相册选图启动器（GetContent 走 SAF，无需任何存储权限）
    val bgPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && !AppSettings.setGalleryBackground(context, uri)) {
            android.widget.Toast.makeText(
                context,
                AppLocale.t("图片读取失败，请换一张试试"),
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // ============ 标题 ============
        Text(
            text = "设置",
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.3.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "",//SETTINGS
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.6.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        // ============ 多页面菜单（对应 Lua wanfeng.menu：顶部页签 + 每页平铺控件） ============
        WanFeng.Menu(
            pages = listOf(
                // ---- 账号页：卡密登录 ----
                WanFeng.Page("账号") {
                    LoginStatusSection()
                },

                // ---- 权限页：Root / Shell / 文件访问 ----
                WanFeng.Page("权限") {
                    PermissionSection(
                        status = state.permissionStatus,
                        onRefresh = { viewModel.checkPermissionStatus() },
                        onAuthorize = { viewModel.requestShizukuPermission() }
                    )
                },

                // ---- 界面页：显示密度 + 音效 + 震动 ----
                WanFeng.Page("界面") {
                    WanFeng.Group {
                        // 显示密度拉条：80% ~ 110%，实时百分比
                        var sliderValue by remember(AppSettings.densityFactor) {
                            mutableFloatStateOf(AppSettings.densityFactor)
                        }
                        WanFeng.Seek(
                            icon = Icons.Default.AspectRatio,
                            title = "显示密度",
                            subtitle = "80% ~ 110%，松手后界面重新加载",
                            valueLabel = "${(sliderValue * 100).roundToInt()}%",
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            onCommit = {
                                AppSettings.setDensityFactor(context, it)
                                // 密度在 attachBaseContext 生效，需重建 Activity
                                (context as? Activity)?.recreate()
                            },
                            valueRange = AppSettings.DENSITY_MIN..AppSettings.DENSITY_MAX,
                            steps = 29   // 每 1% 一档
                        )
                        WanFeng.Divider()
                        WanFeng.Switch(
                            icon = Icons.Default.MusicNote,
                            title = "点击音效",
                            subtitle = "按钮与开关点击时的声音反馈",
                            checked = AppSettings.soundEnabled,
                            onCheckedChange = { AppSettings.setSoundEnabled(context, it) }
                        )
                        WanFeng.Divider()
                        // 震动效果开关：独立于音效，不同控件触发不同震动节奏（适配按钮控件）
                        WanFeng.Switch(
                            icon = Icons.Default.Vibration,
                            title = "震动效果",
                            subtitle = "不同控件适配不同震动节奏",
                            checked = AppSettings.vibrationEnabled,
                            onCheckedChange = { AppSettings.setVibrationEnabled(context, it) }
                        )
                    }
                },

                // ---- 外观页：背景样式 + 雪花 ----
                WanFeng.Page("外观") {
                    val bgCfg = AppSettings.bgConfig
                    val snowOn = AppSettings.snowEnabled
                    WanFeng.Group {
                        WanFeng.Row(
                            icon = Icons.Default.Wallpaper,
                            iconTint = MaterialTheme.colorScheme.primary,
                            title = "背景样式",
                            subtitle = when (bgCfg.mode) {
                                BgMode.GALLERY -> "自定义图片"
                                BgMode.COLOR -> "纯色背景"
                                BgMode.LIQUID -> "液态动态"
                                else -> "液态动态"   // 原默认壁纸已移除，统一按液态动态展示
                            },
                            onClick = { showBgDialog = true }
                        )
                        if (bgCfg.mode == BgMode.COLOR) {
                            WanFeng.Divider()
                            WanFeng.Row(
                                icon = Icons.Default.Palette,
                                iconTint = MaterialTheme.colorScheme.secondary,
                                title = "背景颜色",
                                subtitle = "自定义纯色（保持界面可读的深色调）",
                                onClick = { showColorDialog = true }
                            )
                        }
                        WanFeng.Divider()
                        WanFeng.Switch(
                            icon = Icons.Default.AcUnit,
                            title = "雪花飘落",
                            subtitle = if (snowOn) "已开启全屏雪花特效" else "已关闭",
                            checked = snowOn,
                            onCheckedChange = { AppSettings.setSnowEnabled(context, it) }
                        )
                    }
                },

                // ---- 更新页：检查更新 + 公告 ----
                WanFeng.Page("更新") {
                    WanFeng.Group {
                        WanFeng.Row(
                            icon = Icons.Default.CloudDownload,
                            iconTint = AppColors.successAdaptive(),
                            title = "检查更新",
                            subtitle = AppLocale.tf("当前版本 v{0}", BuildConfig.VERSION_NAME),
                            onClick = { viewModel.checkCloudConfig("update") }
                        )
                        WanFeng.Divider()
                        WanFeng.Row(
                            icon = Icons.Default.Info,
                            iconTint = MaterialTheme.colorScheme.secondary,
                            title = "查看公告",
                            subtitle = if (state.cloudConfig != null) "有新公告" else "暂无公告",
                            onClick = { viewModel.checkCloudConfig("announce") }
                        )
                        WanFeng.Divider()
                        // 公告自动弹出开关：开 = 启动时弹公告；关 = 仅手动查看
                        WanFeng.Switch(
                            icon = Icons.Default.Notifications,
                            title = "启动时显示公告",
                            subtitle = "启动 App 时自动弹出新公告",
                            checked = AppSettings.announceAutoShow,
                            onCheckedChange = { AppSettings.setAnnounceAutoShow(context, it) }
                        )
                    }
                },

                // ---- 关于页：表盘 ID 工具 + Github 仓库 ----
                WanFeng.Page("关于") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 表盘 ID 工具：与 Github 按钮等宽并行，比例协调
                        GlassCard(modifier = Modifier.weight(1f), shape = RoundedCornerShape(22.dp)) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                WanFeng.IconBadge(Icons.Default.VerifiedUser, MaterialTheme.colorScheme.primary, size = 42.dp)
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = "表盘 ID 工具",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    text = "WATCHFACE ID TOOL · v${BuildConfig.VERSION_NAME}",
                                    fontSize = 9.sp,
                                    lineHeight = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        // Github 按钮：点击跳转开源仓库
                        GlassCard(
                            onClick = {
                                runCatching {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/wanfeng090525/XiaoMi-Clock-dial"))
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(id = com.watchface.idtool.R.drawable.ic_github),
                                    contentDescription = "Github 仓库",
                                    tint = Color(0xFFF3F5FA),
                                    modifier = Modifier.size(38.dp)
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = "Github 仓库",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    text = "查看开源仓库",
                                    fontSize = 9.sp,
                                    lineHeight = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            )
        )

        Spacer(Modifier.height(100.dp))
    }

    // ============ 弹窗 ============
    val dialogContext = LocalContext.current

    // 背景样式弹窗（相册图片 / 纯色 / 液态动态；默认壁纸已移除）
    if (showBgDialog) {
        BgStyleDialog(
            current = AppSettings.bgConfig.mode,
            onPickGallery = {
                showBgDialog = false
                bgPicker.launch("image/*")
            },
            onPickColor = {
                AppSettings.setBackground(dialogContext, BgMode.COLOR)
                showBgDialog = false
                showColorDialog = true
            },
            onPickLiquid = {
                AppSettings.setBackground(dialogContext, BgMode.LIQUID)
                showBgDialog = false
            },
            onDismiss = { showBgDialog = false }
        )
    }

    // 纯色背景调色弹窗（色相 / 饱和度 / 明度 + 快捷预设）
    if (showColorDialog) {
        ColorPickerDialog(
            initial = AppSettings.bgConfig.color,
            onApply = { argb ->
                AppSettings.setBackground(dialogContext, BgMode.COLOR, argb)
                showColorDialog = false
            },
            onDismiss = { showColorDialog = false }
        )
    }

    if (state.isCheckingCloud) {
        // 检查中弹窗：点击「检查更新 / 查看公告」后出现，8 秒超时自动关闭，可随时手动取消
        Dialog(onDismissRequest = { viewModel.cancelCloudCheck() }) {
            GlassCard(contentPadding = 22.dp) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlowDot(color = Color.White, dotSize = 10.dp)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "正在检查更新…",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "请稍候",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(18.dp))
                    WanFeng.Button(
                        text = "取消",
                        onClick = { viewModel.cancelCloudCheck() },
                        style = GlassButtonStyle.Glass,
                        modifier = Modifier.fillMaxWidth(),
                        shimmer = false
                    )
                }
            }
        }
    }

    if (state.showAnnouncementDialog) {
        val config = state.cloudConfig
        if (config != null) {
            AnnouncementDialog(
                announcement = config.announcement,
                onDismiss = { viewModel.dismissAnnouncementDialog() }
            )
        }
    }

    // 版本更新弹窗：与公告完全分离，仅在公告关闭后展示
    if (state.showUpdateDialog && !state.showAnnouncementDialog) {
        val config = state.cloudConfig
        if (config != null) {
            UpdateDialog(
                latestVersion = config.latestVersion,
                onUpdate = { viewModel.startDownloadUpdate() },
                onDismiss = { viewModel.dismissAnnouncementDialog() }
            )
        }
    }

    if (state.showDownloadProgress) {
        DownloadProgressDialog(
            progress = state.downloadProgress,
            downloadedBytes = state.downloadDownloadedBytes,
            totalBytes = state.downloadTotalBytes,
            speedBytesPerSec = state.downloadSpeed,
            isDownloading = state.isDownloading,
            error = state.downloadError,
            onDismiss = { viewModel.dismissDownloadProgress() },
            onRetry = { viewModel.startDownloadUpdate() },
            onOpenBrowser = { viewModel.openDownloadInBrowser() },
            onCancel = { viewModel.cancelDownloadUpdate() }
        )
    }
}

// ====================================================================
// 子组件（基础控件统一走 WanFeng.* 工厂，视觉与旧实现一致）
// ====================================================================

/** 卡密登录状态：未登录点击卡片输入卡密登录；已登录可取消解锁 */
@Composable
private fun LoginStatusSection() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    var tip by remember { mutableStateOf("") }
    var loggedIn by remember { mutableStateOf(SagAuthManager.isLoggedIn) }
    var endTime by remember { mutableStateOf(SagAuthManager.endTime) }
    var kamiMask by remember { mutableStateOf(SagAuthManager.currentKamiMasked) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var kamiInput by remember { mutableStateOf(SagAuthManager.loadSavedKami(context)) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(400)
            loggedIn = SagAuthManager.isLoggedIn
            endTime = SagAuthManager.endTime
            kamiMask = SagAuthManager.currentKamiMasked
        }
    }

    val visual = if (loggedIn) {
        SettingsPermVisual(
            Icons.Default.VerifiedUser,
            AppColors.successAdaptive(),
            "已登录",
            buildString {
                if (kamiMask.isNotEmpty()) append("卡密：$kamiMask  ")
                if (endTime.isNotEmpty()) append("到期：$endTime")
                else append("可使用全部功能")
            }
        )
    } else {
        SettingsPermVisual(
            Icons.Default.Lock,
            AppColors.warning,
            "未登录",
            "点击此处输入卡密登录"
        )
    }

    GlassCard(
        onClick = if (!loggedIn) {{ showLoginDialog = true }} else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WanFeng.IconBadge(visual.icon, visual.tint, size = 44.dp)
            Spacer(Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = visual.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (loggedIn) {
                        Spacer(Modifier.width(8.dp))
                        GlowDot(color = Color.White, dotSize = 7.dp)
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = visual.subtitle,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (tip.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = tip,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (loggedIn) {
            Spacer(Modifier.height(14.dp))
            WanFeng.Button(
                text = if (busy) "处理中…" else "取消解锁",
                onClick = {
                    if (busy) return@Button
                    busy = true
                    tip = ""
                    scope.launch {
                        val r = SagAuthManager.unbindKami(context)
                        busy = false
                        loggedIn = SagAuthManager.isLoggedIn
                        endTime = SagAuthManager.endTime
                        kamiMask = SagAuthManager.currentKamiMasked
                        tip = r.message
                    }
                },
                style = GlassButtonStyle.Danger,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showLoginDialog && !loggedIn) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { if (!busy) showLoginDialog = false }) {
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "卡密登录",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "输入授权卡密以解锁全部功能",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    androidx.compose.material3.OutlinedTextField(
                        value = kamiInput,
                        onValueChange = { kamiInput = it; tip = "" },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("卡密") },
                        placeholder = { Text("请输入卡密") },
                        enabled = !busy
                    )
                    if (tip.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = tip,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    WanFeng.Button(
                        text = if (busy) "登录中…" else "登录",
                        onClick = {
                            if (busy) return@Button
                            if (kamiInput.isBlank()) {
                                tip = "请输入卡密"
                                return@Button
                            }
                            busy = true
                            tip = ""
                            scope.launch {
                                val r = SagAuthManager.login(context, kamiInput)
                                busy = false
                                tip = r.message
                                if (r.success) {
                                    loggedIn = true
                                    endTime = SagAuthManager.endTime
                                    kamiMask = SagAuthManager.currentKamiMasked
                                    showLoginDialog = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/** 权限管理区块：状态卡 + 操作按钮 */
@Composable
private fun PermissionSection(
    status: PermissionStatus,
    onRefresh: () -> Unit,
    onAuthorize: () -> Unit
) {
    val visual = when (status) {
        PermissionStatus.ROOT -> SettingsPermVisual(
            Icons.Default.VerifiedUser, AppColors.successAdaptive(),
            "Root 权限可用", "可批量导入 / 直接写入系统目录"
        )
        PermissionStatus.SHELL -> SettingsPermVisual(
            Icons.Default.AdminPanelSettings, AppColors.infoAdaptive(),
            "Shell 权限可用", "通过 Shizuku / ADB 授权"
        )
        PermissionStatus.FILE -> SettingsPermVisual(
            Icons.Default.FolderOpen, AppColors.successAdaptive(),
            "文件权限可用", "已授予所有文件访问，可无 Root 导入"
        )
        PermissionStatus.NONE -> SettingsPermVisual(
            Icons.Default.Shield, AppColors.warning,
            "权限不可用", "可授权「所有文件访问」或 Root / Shizuku"
        )
        PermissionStatus.CHECKING -> SettingsPermVisual(
            Icons.Default.Security, MaterialTheme.colorScheme.onSurfaceVariant,
            "正在检测权限…", "请稍候"
        )
    }

    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WanFeng.IconBadge(visual.icon, visual.tint, size = 44.dp)
            Spacer(Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = visual.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (status == PermissionStatus.ROOT ||
                        status == PermissionStatus.SHELL ||
                        status == PermissionStatus.FILE
                    ) {
                        Spacer(Modifier.width(8.dp))
                        GlowDot(color = Color.White, dotSize = 7.dp)
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = visual.subtitle,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            WanFeng.Button(
                text = "重新检测",
                onClick = onRefresh,
                style = GlassButtonStyle.Glass,
                modifier = Modifier.weight(1f)
            )
            if (status == PermissionStatus.NONE) {
                WanFeng.Button(
                    text = "Shizuku 授权",
                    onClick = onAuthorize,
                    style = GlassButtonStyle.Primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private data class SettingsPermVisual(
    val icon: ImageVector,
    val tint: Color,
    val title: String,
    val subtitle: String
)

// ====================================================================
// 背景样式弹窗（相册图片 / 纯色 / 液态动态；默认壁纸已移除）
// ====================================================================

@Composable
private fun BgStyleDialog(
    current: String,
    onPickGallery: () -> Unit,
    onPickColor: () -> Unit,
    onPickLiquid: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        DialogEntranceWrapper {
            GlassCard(shape = RoundedCornerShape(28.dp), contentPadding = 18.dp) {
                Text(
                    text = "背景样式",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(14.dp))

                data class BgOption(
                    val mode: String,
                    val icon: ImageVector,
                    val title: String,
                    val subtitle: String,
                    val action: () -> Unit
                )

                listOf(
                    BgOption(BgMode.LIQUID, Icons.Default.Gradient, "液态动态", "渐变光斑动态背景（默认）", onPickLiquid),
                    BgOption(BgMode.GALLERY, Icons.Default.PhotoLibrary, "从相册选择", "自定义图片，自动适配屏幕比例", onPickGallery),
                    BgOption(BgMode.COLOR, Icons.Default.Palette, "纯色背景", "自定义颜色（深色调）", onPickColor)
                ).forEach { opt ->
                    val selected = current == opt.mode
                    GlassCard(
                        onClick = opt.action,
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = 13.dp,
                        haptic = false
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            WanFeng.IconBadge(opt.icon, MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = opt.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selected) Color.White
                                    else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = opt.subtitle,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (selected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFFE9EBF4),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

// ====================================================================
// 纯色背景调色弹窗（HSV 三通道 + 深色预设）
// 明度限制在 6% ~ 46%：保证玻璃卡片与白色文字的可读性
// ====================================================================

/** 快捷预设（深色调，均保证界面可读） */
private val BG_COLOR_PRESETS = listOf(
    0xFF0E1116,  // 石墨黑
    0xFF12172B,  // 午夜蓝
    0xFF101F1A,  // 松林绿
    0xFF1F1216,  // 酒红
    0xFF1A1226,  // 暗紫
    0xFF0E1E22,  // 深青
    0xFF16181F,  // 炭灰
    0xFF241A10   // 深咖
)

@Composable
private fun ColorPickerDialog(
    initial: Long,
    onApply: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    // 初始值 → HSV
    val initHsv = FloatArray(3).apply {
        android.graphics.Color.colorToHSV((initial and 0xFFFFFFFFL).toInt(), this)
    }
    var hue by remember { mutableFloatStateOf(initHsv[0]) }
    var sat by remember { mutableFloatStateOf(initHsv[1]) }
    var value by remember { mutableFloatStateOf(initHsv[2].coerceIn(0.06f, 0.46f)) }

    fun currentArgb(): Int {
        val hsv = floatArrayOf(hue, sat, value)
        return android.graphics.Color.HSVToColor(hsv)
    }

    Dialog(onDismissRequest = onDismiss) {
        DialogEntranceWrapper {
            GlassCard(shape = RoundedCornerShape(28.dp), contentPadding = 20.dp) {
                Text(
                    text = "背景颜色",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(14.dp))

                // 实时预览
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(
                            color = Color(currentArgb().toLong() and 0xFFFFFFFFL),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aa 预览文字 Preview",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE9EBF4)
                    )
                }

                Spacer(Modifier.height(14.dp))

                // 快捷预设
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BG_COLOR_PRESETS.forEach { preset ->
                        val argb = (preset and 0xFFFFFFFFL).toInt()
                        val selected = run {
                            val h = FloatArray(3).apply { android.graphics.Color.colorToHSV(argb, this) }
                            kotlin.math.abs(h[0] - hue) < 4f && kotlin.math.abs(h[1] - sat) < 0.06f
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(30.dp)
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) Color.White else Color.White.copy(alpha = 0.20f),
                                    shape = CircleShape
                                )
                                .background(color = Color(preset), shape = CircleShape)
                                .clickable {
                                    val h = FloatArray(3).apply { android.graphics.Color.colorToHSV(argb, this) }
                                    hue = h[0]; sat = h[1]; value = h[2]
                                }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // HSV 拉条（白色系规格与密度拉条一致）
                val sliderColors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White.copy(alpha = 0.9f),
                    inactiveTrackColor = Color.White.copy(alpha = 0.16f)
                )
                Text(AppLocale.tf("色调  {0}°", hue.roundToInt()), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Slider(
                    value = hue,
                    onValueChange = { hue = it },
                    valueRange = 0f..360f,
                    colors = sliderColors
                )
                Text(AppLocale.tf("饱和度  {0}%", (sat * 100).roundToInt()), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Slider(
                    value = sat,
                    onValueChange = { sat = it },
                    valueRange = 0f..1f,
                    colors = sliderColors
                )
                Text(AppLocale.tf("明度  {0}%（深色调保证可读）", (value * 100).roundToInt()), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Slider(
                    value = value,
                    onValueChange = { value = it },
                    valueRange = 0.06f..0.46f,
                    colors = sliderColors
                )

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    WanFeng.Button(
                        text = "取消",
                        onClick = onDismiss,
                        style = GlassButtonStyle.Glass,
                        shimmer = false,
                        modifier = Modifier.weight(1f)
                    )
                    WanFeng.Button(
                        text = "应用",
                        onClick = { onApply(currentArgb().toLong() and 0xFFFFFFFFL) },
                        style = GlassButtonStyle.Primary,
                        shimmer = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// ====================================================================
// 显示密度选择弹窗（已由内联拉条 WanFeng.Seek 取代）
// ====================================================================

