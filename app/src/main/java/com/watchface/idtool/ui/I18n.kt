package com.watchface.idtool.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text as M3Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/**
 * 轻量 i18n 层（仅保留简体中文）
 *
 * 界面层所有 Text 渲染前先经过 AppLocale.t() 翻译；
 * 应用固定为简体中文，不提供语言切换，其余语言词库已移除。
 *
 * · t(source)      普通词条：原样返回
 * · tf(pattern..)  带参词条：{0} {1} 占位符替换
 */
object AppLocale {

    /** 当前界面语言（固定简体中文） */
    var savedLang: String = "zh"
        private set

    /** 应用语言设置（仅保留中文：忽略传入值，强制简体中文） */
    fun apply(code: String) {
        savedLang = "zh"
    }

    /** 翻译入口：仅中文，原样返回 */
    fun t(source: String): String = source

    /** 带参翻译：{0} {1} 占位符替换 */
    fun tf(pattern: String, vararg args: Any?): String {
        var out = pattern
        args.forEachIndexed { i, v -> out = out.replace("{${i}}", v?.toString() ?: "") }
        return out
    }
}


/**
 * 包内 Text 包装器：渲染前自动走 AppLocale.t()。
 * 各界面文件不再 import material3.Text，直接使用本包装器。
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    M3Text(
        text = AppLocale.t(text),
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style
    )
}
