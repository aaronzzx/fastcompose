package com.aaron.compose.paging

object PagingConfigDefaults {

    var DefaultPageSize = 15
    var DefaultMaxPage = Int.MAX_VALUE
    var DefaultInitialSize = DefaultPageSize
    var DefaultPrefetchDistance = 1
    var DefaultMaxSize = Int.MAX_VALUE
    var DefaultEnablePlaceholders = true
    var DefaultEnableLoadMore = true
    var DefaultRequestTimeMillis = 500L
}

data class AppPagingConfig(
    val pageSize: Int = PagingConfigDefaults.DefaultPageSize,
    val maxPage: Int = PagingConfigDefaults.DefaultMaxPage,
    val initialLoadSize: Int = PagingConfigDefaults.DefaultInitialSize,
    val prefetchDistance: Int = PagingConfigDefaults.DefaultPrefetchDistance,
    val maxSize: Int = PagingConfigDefaults.DefaultMaxSize,
    val enablePlaceholders: Boolean = PagingConfigDefaults.DefaultEnablePlaceholders,
    val enableLoadMore: Boolean = PagingConfigDefaults.DefaultEnableLoadMore,
    val minRequestTimeMillis: Long = PagingConfigDefaults.DefaultRequestTimeMillis
)
