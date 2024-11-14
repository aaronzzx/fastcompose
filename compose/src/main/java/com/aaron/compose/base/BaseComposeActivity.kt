package com.aaron.compose.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

/**
 * @author aaronzzxup@gmail.com
 * @since 2022/7/30
 */
abstract class BaseComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)
        setContent {
            MainContent()
        }
    }

    protected open fun init(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
    }

    @Composable
    protected open fun MainContent() {
        Content()
    }

    @Composable
    protected abstract fun Content()
}