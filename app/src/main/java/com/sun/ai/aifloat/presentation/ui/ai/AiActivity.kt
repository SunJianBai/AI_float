package com.sun.ai.aifloat.presentation.ui.ai

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.sun.ai.aifloat.presentation.service.OverlayServiceManager
import com.sun.ai.aifloat.presentation.ui.core.theme.AiDictTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

 /**
 * AI对话界面
 * 功能：
 * - 作为AI对话功能的入口Activity
 * - 集成Jetpack Compose UI
 * - 管理悬浮窗服务
 * 技术栈：
 * - 使用Koin进行依赖注入
 * - 采用Jetpack Compose构建声明式UI
 */
class AiActivity : ComponentActivity() {

    // 使用Koin注入AiViewModel
    private val viewModel by viewModel<AiViewModel> { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置全屏显示
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        // 兼容系统窗口设置
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // 处理启动时传递的Extras参数
        viewModel.onExtrasRetrieved(intent.extras)
        
        // 初始化悬浮窗服务
        OverlayServiceManager(
            this,
            viewModel.windowServiceEnabledPreferenceState
        )
        
        // 设置Compose UI
        setContent {
            val viewModel = remember { viewModel }
            val onOutSideClicked = remember { { finish() } }
            // 应用主题并设置UI路由
            AiDictTheme {
                AiRoute(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel,
                    onOutSideClicked = onOutSideClicked
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // 处理新的Intent数据
        viewModel.onExtrasRetrieved(intent.extras)
    }
}