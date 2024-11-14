package com.aaron.compose.base

/**
 * @author aaronzzxup@gmail.com
 * @since 2022/9/18
 */
interface BaseResult {

    val code: Int

    val msg: String?
}

interface BasePagingResult<E> : BaseResult {

    val source: List<E>
}