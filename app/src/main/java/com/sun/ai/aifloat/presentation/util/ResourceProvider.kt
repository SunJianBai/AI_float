package com.sun.ai.aifloat.presentation.util

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes

// 资源提供者
// 统一管理字符串资源获取
class ResourceProvider(private val context: Context) {

    fun getString(@StringRes stringRes: Int): String {
        return context.getString(stringRes)
    }

    fun getString(@StringRes stringRes: Int, vararg formatArgs: Any): String {
        return context.getString(stringRes, *formatArgs)
    }

    fun getColor(@ColorRes colorRes: Int): Int {
        return context.extractColor(colorRes)
    }

    fun getDrawable(@DrawableRes drawableRes: Int): Drawable? {
        return context.extractDrawable(drawableRes)
    }

    fun getFont(@FontRes fontRes: Int): Typeface? {
        return context.getFontCompat(fontRes)
    }

    fun getDimen(@DimenRes dimenRes: Int): Float {
        return context.extractDimen(dimenRes)
    }

    fun getDimenPixelSize(@DimenRes dimenRes: Int): Int {
        return context.extractDimenPixelSize(dimenRes)
    }
}