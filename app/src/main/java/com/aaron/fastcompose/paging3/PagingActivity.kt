package com.aaron.fastcompose.paging3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.aaron.compose.base.BaseComposeActivity
import com.aaron.compose.ktx.clipToBackground
import com.aaron.compose.ktx.onClick
import com.aaron.compose.ui.refresh.SmartRefresh
import com.aaron.compose.ui.refresh.SmartRefreshType
import com.aaron.compose.ui.refresh.rememberSmartRefreshState
import com.aaron.fastcompose.R
import com.aaron.fastcompose.ui.theme.FastComposeTheme

/**
 * @author aaronzzxup@gmail.com
 * @since 2022/9/27
 */
class PagingActivity : BaseComposeActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        FastComposeTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    TopAppBar(
                        title = {
                            Text(text = "PagingActivity")
                        },
                        navigationIcon = {
                            IconButton(onClick = ::finishAfterTransition) {
                                Icon(
                                    painter = painterResource(R.drawable.back),
                                    contentDescription = null
                                )
                            }
                        }
                    )
                    SmartRefreshList()
                }
            }
        }
    }
}

@Composable
private fun SmartRefreshList(vm: PagingVM = viewModel()) {
    var refreshType by remember {
        mutableStateOf<SmartRefreshType>(SmartRefreshType.Idle)
    }
    val refreshState = rememberSmartRefreshState(type = refreshType)
    val listState = rememberLazyListState()
    val articles = vm.repos.collectAsLazyPagingItems()
    val loadState = articles.loadState

    val loadStateRefresh = loadState.refresh
    LaunchedEffect(loadStateRefresh) {
        when {
            refreshType is SmartRefreshType.Refreshing
                    && loadStateRefresh is LoadState.NotLoading -> {
                listState.scrollToItem(0)
                refreshType = SmartRefreshType.Success
            }
            refreshType is SmartRefreshType.Refreshing
                    && loadStateRefresh is LoadState.Error -> {
                refreshType = SmartRefreshType.Failure
            }
            refreshType is SmartRefreshType.Idle
                    && loadStateRefresh is LoadState.Loading
                    && !vm.init -> {
                refreshType = SmartRefreshType.Refreshing
            }
        }
    }
    LaunchedEffect(Unit) {
        vm.init = false
    }

    SmartRefresh(
        state = refreshState,
        onRefresh = {
            refreshType = SmartRefreshType.Refreshing
            articles.refresh()
        },
        onIdle = {
            refreshType = SmartRefreshType.Idle
        },
        indicatorHeight = 100.dp,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF0F0F0))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    count = articles.itemCount,
                    key = { index ->
                        articles.peek(index)?.id ?: index
                    }
                ) { index ->
                    val article = articles[index]
                    Box(
                        modifier = Modifier
//                            .animateItemPlacement()
                            .clipToBackground(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .onClick {
                                vm.deleteItem(article)
                            }
                            .fillMaxWidth()
                            .height(200.dp)
                            /*.placeholder(article == null, Color.White)*/,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index - ${article?.name}",
                            color = Color(0xFF333333),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                when {
                    loadState.append is LoadState.NotLoading
                            && !loadState.append.endOfPaginationReached
                            && articles.itemCount != 0 -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onClick(enableRipple = true) {
                                        vm.loadMore()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "点我加载更多",
                                    fontSize = 12.sp,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                    loadState.append is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "加载中...",
                                    fontSize = 12.sp,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .onClick(enableRipple = false) {
                                        articles.retry()
                                    }
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "加载失败，点我重试",
                                    fontSize = 12.sp,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                    loadState.append.endOfPaginationReached -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "已经到底啦",
                                    fontSize = 12.sp,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (articles.itemCount == 0) {
                if (loadState.refresh is LoadState.Loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else if (loadState.refresh is LoadState.Error) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(60.dp),
                            imageVector = Icons.Filled.Home,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}
