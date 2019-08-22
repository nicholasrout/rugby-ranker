package com.ricknout.rugbyranker.news.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo.State
import com.google.android.material.snackbar.Snackbar
import com.ricknout.rugbyranker.core.livedata.EventObserver
import com.ricknout.rugbyranker.core.ui.SpaceItemDecoration
import com.ricknout.rugbyranker.core.ui.dagger.DaggerAndroidXFragment
import com.ricknout.rugbyranker.core.util.CustomTabsUtils
import com.ricknout.rugbyranker.news.R
import com.ricknout.rugbyranker.theme.ui.ThemeViewModel
import com.ricknout.rugbyranker.theme.util.getCustomTabsIntentColorScheme
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment : DaggerAndroidXFragment(R.layout.fragment_news) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val newsViewModel: NewsViewModel by activityViewModels { viewModelFactory }

    private val themeViewModel: ThemeViewModel by activityViewModels { viewModelFactory }

    private lateinit var workerSnackBar: Snackbar
    private lateinit var refreshSnackBar: Snackbar

    private lateinit var worldRugbyNewsPagedListAdapter: WorldRugbyArticlePagedListAdapter
    private lateinit var spaceItemDecoration: SpaceItemDecoration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSnackbars()
        setupViewModel()
        setupSwipeRefreshLayout()
    }

    private fun setupRecyclerView() {
        spaceItemDecoration = SpaceItemDecoration(requireContext())
        newsRecyclerView.addItemDecoration(spaceItemDecoration, 0)
        worldRugbyNewsPagedListAdapter = WorldRugbyArticlePagedListAdapter { worldRugbyArticle ->
            CustomTabsUtils.launchCustomTab(requireContext(), worldRugbyArticle.articleUrl,
                    themeViewModel.getTheme().getCustomTabsIntentColorScheme())
        }
        newsRecyclerView.adapter = worldRugbyNewsPagedListAdapter
        newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                newsViewModel.onScroll(delta = dy)
            }
        })
    }

    private fun setupSnackbars() {
        workerSnackBar = Snackbar.make(root, "", Snackbar.LENGTH_INDEFINITE)
        refreshSnackBar = Snackbar.make(root, "", Snackbar.LENGTH_SHORT)
    }

    private fun setupViewModel() {
        newsViewModel.latestWorldRugbyNews.observe(viewLifecycleOwner, Observer { latestWorldRugbyNews ->
            worldRugbyNewsPagedListAdapter.submitList(latestWorldRugbyNews)
            val isEmpty = latestWorldRugbyNews?.isEmpty() ?: true
            progressBar.isVisible = isEmpty
        })
        newsViewModel.latestWorldRugbyNewsWorkInfos.observe(viewLifecycleOwner, Observer { workInfos ->
            val workInfo = workInfos?.firstOrNull() ?: return@Observer
            when (workInfo.state) {
                State.RUNNING -> {
                    swipeRefreshLayout.isEnabled = false
                    workerSnackBar.setText(R.string.snackbar_fetching_world_rugby_news)
                    workerSnackBar.show()
                }
                else -> {
                    swipeRefreshLayout.isEnabled = true
                    root.post { workerSnackBar.dismiss() }
                }
            }
        })
        newsViewModel.refreshingLatestWorldRugbyNews.observe(viewLifecycleOwner, Observer { refreshingLatestWorldRugbyNews ->
            swipeRefreshLayout.isRefreshing = refreshingLatestWorldRugbyNews
        })
        newsViewModel.scrollToTop.observe(viewLifecycleOwner, EventObserver {
            newsRecyclerView.smoothScrollToPosition(0)
            appBarLayout.setExpanded(true)
        })
    }

    private fun setupSwipeRefreshLayout() {
        val swipeRefreshColors = resources.getIntArray(R.array.colors_swipe_refresh)
        swipeRefreshLayout.setColorSchemeColors(*swipeRefreshColors)
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.color_surface)
        swipeRefreshLayout.setProgressViewOffset(true,
                swipeRefreshLayout.progressViewStartOffset + resources.getDimensionPixelSize(R.dimen.spacing_double),
                swipeRefreshLayout.progressViewEndOffset)
        swipeRefreshLayout.setOnRefreshListener {
            newsViewModel.refreshLatestWorldRugbyNews { success ->
                if (!success) {
                    refreshSnackBar.setText(R.string.snackbar_failed_to_refresh_world_rugby_news)
                    refreshSnackBar.show()
                }
            }
        }
    }
}