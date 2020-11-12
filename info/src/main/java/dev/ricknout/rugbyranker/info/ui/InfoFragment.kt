package dev.ricknout.rugbyranker.info.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.animate
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.HorizontalSide
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsWidth
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import dev.chrisbanes.accompanist.insets.toPaddingValues
import dev.ricknout.rugbyranker.core.ui.RugbyRankerButton
import dev.ricknout.rugbyranker.core.ui.openDrawer
import dev.ricknout.rugbyranker.core.util.CustomTabUtils
import dev.ricknout.rugbyranker.info.R
import dev.ricknout.rugbyranker.theme.ui.ThemeViewModel
import dev.ricknout.rugbyranker.theme.util.getCustomTabsIntentColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class InfoFragment : Fragment() {

    private val infoViewModel: InfoViewModel by activityViewModels()

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
        setContent {
            Info()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    @Composable
    fun Info() {
        MdcTheme {
            ProvideWindowInsets {
                val scrollState = rememberScrollState()
                Scaffold(
                    topBar = {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = animate(if (scrollState.value > 0f) 4.dp else 0.dp)
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
                        ScrollableColumn(
                            scrollState = scrollState,
                            contentPadding = AmbientWindowInsets.current.navigationBars.toPaddingValues(top = false)
                        ) {
                            UrlButton(
                                text = stringResource(R.string.how_are_rankings_calculated),
                                url = RANKINGS_EXPLANATION_URL
                            )
                            ShareButton()
                            UrlButton(
                                text = stringResource(R.string.view_source_code),
                                url = GITHUB_URL
                            )
                            OssButton()
                            ThemeButton()
                            VersionText(infoViewModel = infoViewModel)
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun UrlButton(text: String, url: String) {
        RugbyRankerButton(
            onClick = {
                lifecycleScope.launch {
                    val theme = themeViewModel.theme.first()
                    withContext(Dispatchers.Main) {
                        CustomTabUtils.launchCustomTab(
                            requireContext(),
                            url,
                            theme.getCustomTabsIntentColorScheme()
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = text)
        }
    }

    @Composable
    fun ShareButton() {
        RugbyRankerButton(
            onClick = {
                val appName = getString(R.string.app_name)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_SUBJECT, requireContext().getString(R.string.share_subject, appName))
                    putExtra(Intent.EXTRA_TEXT, requireContext().getString(R.string.share_text, appName, PLAY_STORE_URL))
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(intent, requireContext().getString(R.string.share_title, appName)))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.share_this_app))
        }
    }

    @Composable
    fun OssButton() {
        RugbyRankerButton(
            onClick = {
                val intent = Intent(requireContext(), OssLicensesMenuActivity::class.java)
                startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.open_source_licenses))
        }
    }

    @Composable
    fun ThemeButton() {
        RugbyRankerButton(
            onClick = {
                lifecycleScope.launch {
                    val theme = themeViewModel.theme.first()
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(InfoFragmentDirections.infoToTheme(theme))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.choose_theme))
        }
    }

    @Composable
    fun VersionText(infoViewModel: InfoViewModel) {
        val version by infoViewModel.version.observeAsState()
        Providers(
            AmbientContentAlpha provides ContentAlpha.medium,
            children = {
                Text(
                    text = stringResource(R.string.version, version ?: ""),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.height(56.dp).padding(16.dp)
                )
            }
        )
    }

    companion object {
        private const val RANKINGS_EXPLANATION_URL = "https://www.world.rugby/rankings/explanation"
        private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.ricknout.rugbyranker"
        private const val GITHUB_URL = "https://github.com/ricknout/rugby-ranker"
    }
}
