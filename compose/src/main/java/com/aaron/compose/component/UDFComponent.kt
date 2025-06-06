package com.aaron.compose.component

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.aaron.compose.base.Defaults
import com.aaron.compose.base.DefaultsTarget
import com.aaron.compose.ktx.collectAsStateWithLifecycle
import com.aaron.compose.ktx.findActivity
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

/**
 * 单向数据流组件（UDF: Unidirectional Data Flow）
 *
 * @param component 组件容器
 * @param activeState 在哪个生命周期激活
 * @param onBaseEvent 预置视图事件，如果外部拦截处理需要返回 true
 * @param onEvent 视图事件向上流动
 * @param content 视图状态向下流动
 */
@Composable
fun <UiState : Any, UiEvent : Any> UDFComponent(
    component: UDFComponent<UiState, UiEvent>,
    activeState: Lifecycle.State = UDFComponentDefaults.instance.activeState,
    onBaseEvent: suspend (baseEvent: Any) -> Boolean = UDFComponentDefaults.instance.onBaseEvent,
    onEvent: suspend (event: UiEvent) -> Unit,
    content: @Composable (state: UiState) -> Unit
) {
    UDFComponentDefaults.instance.UDFComponent(
        component = component,
        activeState = activeState,
        onBaseEvent = onBaseEvent,
        onEvent = onEvent,
        content = content
    )
}

/**
 * 单向数据流组件（UDF: Unidirectional Data Flow），将视图操作回调 ViewModel ，由 ViewModel 处理逻辑，视图状态稳定不可变，
 * 每次改变视图状态时需要重新生成一个新的 [UiState] 推送到视图
 *
 * [UiState] 表示视图状态，一般使用 data class 定义，方便使用 copy 函数，字段尽量定义在第一层，避免使用 copy 时过深
 *
 * [UiEvent] 表示一次性的视图事件，使用 sealed interface 或 sealed class 定义，方便在使用 when 时一键填充，且增加新的 sealed 子类时编译器能进行缺失提示
 */
@Stable
interface UDFComponent<UiState : Any, UiEvent : Any> {

    val state: StateFlow<UiState>

    val event: Flow<UiEvent>

    val baseEvent: Flow<Any>
}

sealed interface UiBaseEvent {

    sealed interface Toast : UiBaseEvent

    data class ResToast(val res: Int) : Toast

    data class StringToast(val text: String) : Toast

    data object Finish : UiBaseEvent
}

open class UDFComponentDefaults : Defaults() {

    companion object : DefaultsTarget<UDFComponentDefaults>(UDFComponentDefaults())

    open val activeState: Lifecycle.State = Lifecycle.State.STARTED

    val onBaseEvent: suspend (baseEvent: Any) -> Boolean = { false }

    @Composable
    open fun <UiState : Any, UiEvent : Any> UDFComponent(
        component: UDFComponent<UiState, UiEvent>,
        activeState: Lifecycle.State,
        onBaseEvent: suspend (baseEvent: Any) -> Boolean,
        onEvent: suspend (event: UiEvent) -> Unit,
        content: @Composable (state: UiState) -> Unit
    ) {
        UDFComponentImpl(
            component = component,
            activeState = activeState,
            onBaseEvent = onBaseEvent,
            onEvent = onEvent,
            content = content
        )
    }

    @Composable
    protected fun <UiState : Any, UiEvent : Any> UDFComponentImpl(
        component: UDFComponent<UiState, UiEvent>,
        activeState: Lifecycle.State,
        onBaseEvent: suspend (baseEvent: Any) -> Boolean,
        onEvent: suspend (event: UiEvent) -> Unit,
        content: @Composable (state: UiState) -> Unit
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val curOnBaseEvent by rememberUpdatedState(onBaseEvent)
        val curOnEvent by rememberUpdatedState(onEvent)
        LaunchedEffect(key1 = component) {
            launch {
                lifecycleOwner.repeatOnLifecycle(activeState) {
                    component.baseEvent.collect { baseEvent ->
                        launch {
                            if (baseEvent is UiBaseEvent && !curOnBaseEvent(baseEvent)) {
                                when (baseEvent) {
                                    UiBaseEvent.Finish -> {
                                        context.findActivity()?.finish()
                                    }
                                    is UiBaseEvent.ResToast -> {
                                        ToastUtils.showShort(baseEvent.res)
                                    }
                                    is UiBaseEvent.StringToast -> {
                                        ToastUtils.showShort(baseEvent.text)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            launch {
                lifecycleOwner.repeatOnLifecycle(activeState) {
                    component.event.collect { event ->
                        launch {
                            curOnEvent(event)
                        }
                    }
                }
            }
        }

        val state by component.state.collectAsStateWithLifecycle(minActiveState = activeState)
        content(state)
    }
}

fun <UiState : Any, UiEvent : Any> udfComponent(initialState: UiState): UDFComponent<UiState, UiEvent> {
    return object : UDFComponent<UiState, UiEvent> {
        override val state: StateFlow<UiState> = MutableStateFlow(initialState)
        override val event: Flow<UiEvent> = emptyFlow()
        override val baseEvent: Flow<Any> = emptyFlow()
    }
}