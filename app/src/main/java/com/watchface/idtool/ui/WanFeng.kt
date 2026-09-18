package com.watchface.idtool.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watchface.idtool.AppSettings
import com.watchface.idtool.ClickSound
import com.watchface.idtool.SoundType

/**
 * 控件工厂 —— 对应 Lua 版悬浮窗脚本中的 wanfeng.* 组件体系
 *
 * 布局代码样式对齐 Lua 写法：页面 = 一组声明式控件调用，
 * 每个控件把「标题 + 状态 + 回调」作为参数传入（如 wanfeng.switch("名称", 开回调, 关回调)），
 * 视觉样式保持不变。
 *
 * 映射关系：
 *   WanFeng.Section ⇐ 页面分区标题
 *   WanFeng.Group   ⇐ 带渐变背景的分组容器
 *   WanFeng.Switch  ⇐ wanfeng.switch
 *   WanFeng.Row     ⇐ 可点击跳转行（带箭头）
 *   WanFeng.Button  ⇐ wanfeng.button
 *   WanFeng.Seek    ⇐ wanfeng.seek
 *   WanFeng.Divider ⇐ 分组分隔线
 */
object WanFeng {

    /**
     * 多页面菜单（对应 Lua 版 wanfeng.menu：顶部页签 + 单页内容）
     *
     * 默认在内部管理当前页签；如需从页面内容里跳页（如首页选择文件后跳到“修改”），
     * 可传入 [pageIndex] / [onPageChange] 由外部控制当前页。
     */
    @Composable
    fun Menu(
        pages: List<Page>,
        modifier: Modifier = Modifier,
        pageIndex: Int? = null,
        onPageChange: ((Int) -> Unit)? = null
    ) {
        val context = LocalContext.current
        val internalIndex = rememberSaveable { mutableIntStateOf(0) }
        val currentIndex = pageIndex ?: internalIndex.intValue
        Column(modifier = modifier) {
            // 顶部页签（玻璃分段控制条）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                pages.forEachIndexed { i, page ->
                    val selected = i == currentIndex
                    Text(
                        text = page.title,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selected) Color.White.copy(alpha = 0.14f)
                                else Color.White.copy(alpha = 0.05f)
                            )
                            .clickable {
                                if (AppSettings.soundEnabled) {
                                    ClickSound.play(context, SoundType.TOGGLE)
                                }
                                if (pageIndex != null) {
                                    onPageChange?.invoke(i)
                                } else {
                                    internalIndex.intValue = i
                                }
                            }
                            .padding(vertical = 10.dp)
                            .fillMaxWidth()
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            // 当前页内容（切换带轻量滑动过渡）
            AnimatedContent(
                targetState = currentIndex,
                transitionSpec = {
                    (fadeIn(tween(220)) + slideInHorizontally(tween(260)) { it / 8 })
                        .togetherWith(fadeOut(tween(140)))
                },
                label = "wanfengMenuPage"
            ) { idx ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    pages[idx].content()
                }
            }
        }
    }

    /** 页面（对应 wanfeng.menu 中一个 tab：标题 + 平铺控件列表） */
    class Page(
        val title: String,
        val content: @Composable () -> Unit
    )

    /** 分区标题（对应 Lua 页面里的分组标题） */
    @Composable
    fun Section(title: String) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.3.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 6.dp)
        )
    }

    /** 分组容器（对应 Lua 中带渐变背景的 LinearLayout 组） */
    @Composable
    fun Group(
        contentPadding: Dp = 6.dp,
        content: @Composable ColumnScope.() -> Unit
    ) {
        GlassCard(contentPadding = contentPadding) {
            content()
        }
    }

    /** 分隔线 */
    @Composable
    fun Divider() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 2.dp)
                .height(1.dp)
                .glass(
                    RoundedCornerShape(1.dp),
                    GlassColors(
                        tintTop = Color.White.copy(alpha = 0.06f),
                        tintBottom = Color.Transparent,
                        highlight = Color.Transparent,
                        rimBright = Color.Transparent,
                        rimDim = Color.Transparent
                    )
                )
        )
    }

    /** 开关（对应 wanfeng.switch：标题 + 状态 + 回调） */
    @Composable
    fun Switch(
        icon: ImageVector,
        title: String,
        subtitle: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
    ) {
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(icon, Color.Transparent)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = { next ->
                    if (AppSettings.soundEnabled) {
                        ClickSound.play(context, SoundType.TOGGLE)
                    }
                    onCheckedChange(next)
                },
                colors = SwitchDefaults.colors(
                    // 提亮玻璃轨道：半透明白 + 亮边环，浅色滑块（液态玻璃规格）
                    checkedTrackColor = Color.White.copy(alpha = 0.30f),
                    checkedThumbColor = Color(0xFFF3F5FA),
                    checkedBorderColor = Color.White.copy(alpha = 0.75f),
                    uncheckedTrackColor = Color.White.copy(alpha = 0.12f),
                    uncheckedThumbColor = Color(0xFF9AA1B5),
                    uncheckedBorderColor = Color.White.copy(alpha = 0.22f)
                )
            )
        }
    }

    /** 点击跳转行（带箭头，对应 wanfeng.button 行式） */
    @Composable
    fun Row(
        icon: ImageVector,
        iconTint: Color,
        title: String,
        subtitle: String,
        onClick: () -> Unit
    ) {
        GlassCard(
            onClick = onClick,
            shape = RoundedCornerShape(18.dp),
            contentPadding = 13.dp,
            haptic = false
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBadge(icon, iconTint)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

    /** 全宽按钮（对应 wanfeng.button 整行按钮） */
    @Composable
    fun Button(
        text: String,
        onClick: () -> Unit,
        style: GlassButtonStyle = GlassButtonStyle.Glass,
        modifier: Modifier = Modifier,
        shimmer: Boolean = false
    ) {
        GlassButton(
            text = text,
            onClick = onClick,
            style = style,
            modifier = modifier,
            shimmer = shimmer
        )
    }

    /** 滑块（对应 wanfeng.seek：标题 + 实时值 + 回调） */
    @Composable
    fun Seek(
        icon: ImageVector,
        title: String,
        subtitle: String,
        valueLabel: String,
        value: Float,
        onValueChange: (Float) -> Unit,
        onCommit: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int = 0
    ) {
        // 松手提交时取最新值，避免拖拽末段 recomposition 滞后导致提交旧值
        val latestValue by rememberUpdatedState(value)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(icon, MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.weight(1f))
                    // 实时值（保时捷工程数字）
                    Text(
                        text = valueLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = NumericFonts,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                GlassSlider(
                    value = value,
                    onValueChange = onValueChange,
                    onValueChangeFinished = { onCommit(latestValue) },
                    valueRange = valueRange,
                    steps = steps
                )
            }
        }
    }

    /** 透明玻璃图标容器（玻璃容器 + 实心中性图标，无彩色规格） */
    @Composable
    internal fun IconBadge(icon: ImageVector, tint: Color, size: Dp = 38.dp) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .glow(Color.White.copy(alpha = 0.15f), radiusFraction = 1.5f)
                .glass(CircleShape, rememberGlassColors()),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFFE9EBF4),
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}
