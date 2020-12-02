package dev.ricknout.rugbyranker.news.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.animate
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.HorizontalSide
import dev.chrisbanes.accompanist.insets.ViewWindowInsetObserver
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.navigationBarsWidth
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import dev.chrisbanes.accompanist.insets.toPaddingValues
import dev.ricknout.rugbyranker.core.ui.RugbyRankerButton
import dev.ricknout.rugbyranker.core.ui.openDrawer
import dev.ricknout.rugbyranker.core.util.CustomTabUtils
import dev.ricknout.rugbyranker.core.util.DateUtils
import dev.ricknout.rugbyranker.news.R
import dev.ricknout.rugbyranker.news.model.News
import dev.ricknout.rugbyranker.news.model.Type
import dev.ricknout.rugbyranker.theme.ui.ThemeViewModel
import dev.ricknout.rugbyranker.theme.util.getCustomTabsIntentColorScheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private val args: NewsFragmentArgs by navArgs()

    private val type: Type by lazy { args.type }

    private val newsViewModel: NewsViewModel by lazy {
        when (type) {
            Type.TEXT -> activityViewModels<TextNewsViewModel>().value
        }
    }

    private val themeViewModel: ThemeViewModel by activityViewModels()

    private val transitionDuration by lazy {
        resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply { duration = transitionDuration }
        exitTransition = MaterialFadeThrough().apply { duration = transitionDuration }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        isTransitionGroup = true
        val observer = ViewWindowInsetObserver(this)
        val windowInsets = observer.start()
        setContent {
            Providers(AmbientWindowInsets provides windowInsets) {
                News(newsViewModel)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    // Previous swipe refresh UI setup
    /*val primaryColor = MaterialColors.getColor(binding.swipeRefreshLayout, R.attr.colorPrimary)
    val elevationOverlayProvider = ElevationOverlayProvider(requireContext())
    val surfaceColor = elevationOverlayProvider.compositeOverlayWithThemeSurfaceColorIfNeeded(
        resources.getDimension(R.dimen.elevation_swipe_refresh_layout)
    )
    binding.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(surfaceColor)
    binding.swipeRefreshLayout.setColorSchemeColors(primaryColor)
    binding.swipeRefreshLayout.setProgressViewOffset(
        true,
        binding.swipeRefreshLayout.progressViewStartOffset,
        binding.swipeRefreshLayout.progressViewEndOffset
    )
    binding.swipeRefreshLayout.setOnRefreshListener { adapter.refresh() }*/

    // Previous paging load state, retry UI, and progress indicator UI
    /*binding.recyclerView.adapter = adapter
    adapter.addLoadStateListener { combinedLoadStates ->
        when (combinedLoadStates.refresh) {
            is LoadState.Loading -> {
                binding.retry.isVisible = false
                if (adapter.itemCount == 0) binding.progressIndicator.show() else binding.progressIndicator.hide()
            }
            is LoadState.NotLoading -> {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.retry.isVisible = false
                binding.progressIndicator.hide()
            }
            is LoadState.Error -> {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.retry.isVisible = adapter.itemCount == 0
                binding.progressIndicator.hide()
                if (adapter.itemCount > 0) {
                    Snackbar.make(
                        binding.root,
                        R.string.failed_to_refresh_news,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    binding.retryButton.setOnClickListener { adapter.retry() }*/

    // TODO: Use LazyGrid over LazyColumn when available: https://issuetracker.google.com/issues/162213211
    // TODO: Implement swipe refresh UI
    @Composable
    @OptIn(ExperimentalMaterialApi::class)
    fun News(viewModel: NewsViewModel) {
        MdcTheme {
            val scaffoldState = rememberScaffoldState()
            val scope = rememberCoroutineScope()
            val lazyListState = rememberLazyListState()
            Scaffold(
                scaffoldState = scaffoldState,
                snackbarHost = { snackbarHostState ->
                    SnackbarHost(
                        hostState = snackbarHostState,
                        snackbar = { snackbarData ->
                            Snackbar(
                                snackbarData = snackbarData,
                                // Snackbar already applies padding of 12dp (16dp - 12dp = 4dp)
                                modifier = Modifier.padding(4.dp).navigationBarsPadding()
                            )
                        }
                    )
                },
                topBar = {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = animate(if (lazyListState.firstVisibleItemScrollOffset > 0f) 4.dp else 0.dp)
                    ) {
                        Row(modifier = Modifier.statusBarsPadding()) {
                            Spacer(Modifier.navigationBarsWidth(HorizontalSide.Left))
                            RugbyRankerButton(
                                onClick = { openDrawer() },
                                contentColor = MaterialTheme.colors.onSurface,
                                rippleColor = MaterialTheme.colors.onSurface
                            ) {
                                Icon(Icons.Default.Menu)
                            }
                            Spacer(Modifier.navigationBarsWidth(HorizontalSide.Right))
                        }
                    }
                },
                bodyContent = {
                    val lazyPagingItems = viewModel.news.collectAsLazyPagingItems()
                    NewsList(lazyPagingItems, lazyListState, scope, scaffoldState)
                }
            )
        }
    }

    @Composable
    @OptIn(ExperimentalMaterialApi::class)
    fun NewsList(
        lazyPagingItems: LazyPagingItems<News>,
        lazyListState: LazyListState,
        scope: CoroutineScope,
        scaffoldState: ScaffoldState
    ) {
        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                if (lazyPagingItems.itemCount == 0) {
                    Loading()
                }
            }
            is LoadState.NotLoading -> {
            }
            is LoadState.Error -> {
                if (lazyPagingItems.itemCount == 0) {
                    Retry(
                        onClick = {
                            lazyPagingItems.refresh()
                        }
                    )
                } else {
                    val message = stringResource(id = R.string.failed_to_refresh_news)
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
        LazyColumn(
            state = lazyListState,
            contentPadding = AmbientWindowInsets.current.navigationBars.toPaddingValues(top = false)
        ) {
            itemsIndexed(lazyPagingItems) { index: Int, value: News? ->
                val news = value ?: return@itemsIndexed
                val spanCount = integerResource(R.integer.span_count_grid)
                val row = floor(index / spanCount.toFloat()).toInt()
                NewsItem(news, spanCount, row, index) {
                    lifecycleScope.launch {
                        val theme = themeViewModel.theme.first()
                        withContext(Dispatchers.Main) {
                            CustomTabUtils.launchCustomTab(
                                requireContext(),
                                news.articleUrl,
                                theme.getCustomTabsIntentColorScheme()
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun NewsItem(
        news: News,
        spanCount: Int,
        row: Int,
        index: Int,
        onClick: () -> Unit
    ) {
        val color = when {
            (spanCount % 2 != 0 || row % 2 == 0) && index % 2 == 0 -> {
                MaterialTheme.colors.onSurface.copy(alpha = 0.05f)
            }
            (spanCount % 2 == 0 && row % 2 != 0) && index % 2 != 0 -> {
                MaterialTheme.colors.onSurface.copy(alpha = 0.05f)
            }
            else -> MaterialTheme.colors.surface
        }
        val interactionState = remember { InteractionState() }
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = color,
            contentColor = MaterialTheme.colors.onSurface,
            modifier = Modifier.clickable(
                onClick = onClick,
                interactionState = interactionState
            )
        ) {
            NewsContent(news)
        }
    }

    @Composable
    fun NewsContent(news: News) {
        Column {
            CoilImage(
                modifier = Modifier.height(224.dp),
                data = news.imageUrl ?: "",
                contentScale = ContentScale.Crop,
                fadeIn = true,
                loading = {
                    Box(Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            )
            if (news.subtitle != null) Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                text = news.subtitle,
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body1
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                text = news.title,
                style = MaterialTheme.typography.h6
            )
            Text(
                modifier = Modifier.padding(16.dp),
                text = news.summary,
                style = MaterialTheme.typography.body1
            )
            val isCurrentDay = DateUtils.isDayCurrentDay(news.timeMillis)
            val date = if (isCurrentDay) {
                stringResource(R.string.today)
            } else {
                DateUtils.getDate(DateUtils.DATE_FORMAT_D_MMM_YYYY, news.timeMillis)
            }
            Providers(
                AmbientContentAlpha provides ContentAlpha.medium,
                content = {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        text = date,
                        style = MaterialTheme.typography.body1
                    )
                }
            )
        }
    }

    @Composable
    fun Loading() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    @Composable
    fun Retry(onClick: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Providers(
                AmbientContentAlpha provides ContentAlpha.medium,
                content = {
                    Icon(
                        imageVector = vectorResource(id = R.drawable.ic_error),
                        modifier = Modifier
                            .preferredHeight(107.dp)
                            .padding(top = 16.dp, bottom = 16.dp)
                    )
                }
            )
            RugbyRankerButton(onClick = onClick) {
                Text(text = stringResource(id = R.string.retry))
            }
        }
    }
}
