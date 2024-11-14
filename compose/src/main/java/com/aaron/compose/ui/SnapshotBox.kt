package com.aaron.compose.ui

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap

@Composable
fun SnapshotBox(
    modifier: Modifier = Modifier,
    state: SnapshotBoxState = rememberSnapshotBoxState(),
    content: @Composable BoxScope.() -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ComposeView(context).apply {
                id = View.generateViewId()
                layoutParams = ViewGroup.LayoutParams(-2, -2)
            }
        }
    ) {
        it.setContent {
            val localView = LocalView.current
            DisposableEffect(state, localView) {
                state.localView = localView
                onDispose {
                    state.localView = null
                }
            }
            Box {
                content()
            }
        }
    }
}

@Composable
fun rememberSnapshotBoxState(): SnapshotBoxState {
    return remember {
        SnapshotBoxState()
    }
}

@Stable
class SnapshotBoxState {

    internal var localView: View? by mutableStateOf(null)

    val readyToDraw: Boolean by derivedStateOf { localView != null }

    fun drawToBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
        val localView = checkNotNull(localView)
        return localView.drawToBitmap(config)
    }
}