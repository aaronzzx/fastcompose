package com.aaron.compose.ui

import android.view.Gravity
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
import androidx.activity.compose.BackHandler
import androidx.annotation.StyleRes
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.aaron.compose.R
import com.aaron.compose.ktx.findActivity
import com.blankj.utilcode.util.BarUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlin.math.roundToInt

/**
 * 应用内通知弹窗
 */
@Composable
fun NotificationDialog(
    onDismissRequest: () -> Unit,
    autoDismissTimeMillis: Long = 6000,
    @StyleRes animationRes: Int = R.style.NotificationDialogAnimation,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        val context = LocalContext.current
        BackHandler {
            // 暂时没有更好的调用替代，使用backDispatcher的话会无限走回这个block
            @Suppress("DEPRECATION")
            context.findActivity()?.onBackPressed()
        }

        val view = LocalView.current
        SideEffect {
            val window = (view.parent as? DialogWindowProvider)?.window ?: return@SideEffect
            window.addFlags(FLAG_NOT_TOUCH_MODAL or FLAG_NOT_FOCUSABLE)
            window.clearFlags(FLAG_DIM_BEHIND)
            window.attributes = window.attributes.also {
                it.width = WindowManager.LayoutParams.MATCH_PARENT
                it.gravity = Gravity.TOP
            }
            window.setWindowAnimations(animationRes)
        }

        val density = LocalDensity.current
        val decay = rememberSplineBasedDecay<Float>()
        val state = remember(density) {
            AnchoredDraggableState(
                initialValue = "show",
                positionalThreshold = { distance: Float -> distance * 0.5f },
                velocityThreshold = { density.run { 100.dp.toPx() } },
                snapAnimationSpec = tween(),
                decayAnimationSpec = decay
            )
        }

        val curOnDismissRequest by rememberUpdatedState(newValue = onDismissRequest)
        val interactionSource = remember { MutableInteractionSource() }
        if (autoDismissTimeMillis > 0) {
            val dragged by interactionSource.collectIsDraggedAsState()
            var countdownTimeMillis by remember(autoDismissTimeMillis) { mutableLongStateOf(autoDismissTimeMillis) }
            LaunchedEffect(dragged) {
                if (countdownTimeMillis > 0 && !dragged) {
                    val start = System.currentTimeMillis()
                    try {
                        delay(countdownTimeMillis)
                        curOnDismissRequest()
                    } catch (ignored: CancellationException) {
                        val end = System.currentTimeMillis()
                        val spendTime = end - start
                        val residue = countdownTimeMillis - spendTime
                        countdownTimeMillis = residue.coerceAtLeast(1000)
                    }
                }
            }
        }
        LaunchedEffect(key1 = state) {
            snapshotFlow { state.currentValue }
                .drop(1)
                .filter { it == "dismiss" }
                .collect {
                    curOnDismissRequest()
                }
        }

        Box(
            modifier = Modifier
                .onSizeChanged {
                    state.updateAnchors(
                        DraggableAnchors {
                            "show" at 0f
                            "dismiss" at -it.height.toFloat() - BarUtils.getStatusBarHeight()
                        }
                    )
                }
                .offset {
                    IntOffset(
                        x = 0,
                        y = state
                            .requireOffset()
                            .roundToInt(),
                    )
                }
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Vertical,
                    interactionSource = interactionSource
                )
        ) {
            content()
        }
    }
}

/**
 * 底部弹窗
 */
@Composable
fun BottomDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    @StyleRes animationRes: Int = R.style.BottomDialogAnimation,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = properties.dismissOnBackPress,
            dismissOnClickOutside = properties.dismissOnClickOutside,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        val view = LocalView.current
        SideEffect {
            val window = (view.parent as? DialogWindowProvider)?.window ?: return@SideEffect
            window.attributes = window.attributes.also {
                it.width = WindowManager.LayoutParams.MATCH_PARENT
                it.gravity = Gravity.BOTTOM
            }
            window.setWindowAnimations(animationRes)
        }
        content()
    }
}