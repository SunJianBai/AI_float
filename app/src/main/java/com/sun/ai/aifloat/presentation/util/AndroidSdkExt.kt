package com.sun.ai.aifloat.presentation.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.sun.ai.aifloat.presentation.ui.core.model.UiText

// Android SDK版本扩展工具
// 包含API等级判断、权限检查等实用方法
val isSdk34OrUp: Boolean // 检查是否为Android 14及以上
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

val isSdk33OrUp: Boolean // 检查是否为Android 13及以上
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

val Context.canDrawOverlays: Boolean // 检查是否具有悬浮窗权限
    get() = Settings.canDrawOverlays(this)

val Context.isNecessaryPermissionsAvailable: Boolean // 必要权限检查
    get() = (!isSdk33OrUp || isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS))
            /*
            Disable the speech to text until is ready.

            isPermissionGranted(Manifest.permission.RECORD_AUDIO)
            */

fun Context.isPermissionGranted(permission: String) = ContextCompat.checkSelfPermission(
    applicationContext,
    permission
) == PackageManager.PERMISSION_GRANTED // 检查指定权限是否授予


inline fun Context.toast(length: Int, text: () -> UiText) { // 快捷Toast显示方法
    Toast.makeText(
        applicationContext,
        text().asString(applicationContext),
        length
    ).show()
}