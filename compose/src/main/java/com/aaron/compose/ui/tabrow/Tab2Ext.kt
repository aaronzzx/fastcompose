package com.aaron.compose.ui.tabrow

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * 禁用 Ripple 的 Tab
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonRippleTab2(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor.copy(alpha = 0.5f),
    content: @Composable ColumnScope.() -> Unit
) {
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        Tab2(
            selected = selected,
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            interactionSource = interactionSource,
            selectedContentColor = selectedContentColor,
            unselectedContentColor = unselectedContentColor,
            content = content
        )
    }
}

/**
 * 禁用 Ripple 的 Tab
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonRippleTab2(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor.copy(alpha = 0.5f)
) {
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        Tab2(
            selected = selected,
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            text = text,
            icon = icon,
            interactionSource = interactionSource,
            selectedContentColor = selectedContentColor,
            unselectedContentColor = unselectedContentColor
        )
    }
}

/**
 * 禁用 Ripple 的 LeadingTab
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonRippleLeadingIconTab2(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor.copy(alpha = 0.5f),
    icon: @Composable (() -> Unit),
    text: @Composable (() -> Unit)
) {
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        LeadingIconTab2(
            selected = selected,
            onClick = onClick,
            text = text,
            icon = icon,
            modifier = modifier,
            enabled = enabled,
            interactionSource = interactionSource,
            selectedContentColor = selectedContentColor,
            unselectedContentColor = unselectedContentColor
        )
    }
}