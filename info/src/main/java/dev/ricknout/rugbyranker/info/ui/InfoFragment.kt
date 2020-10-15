package dev.ricknout.rugbyranker.info.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.animate
import androidx.compose.foundation.AmbientIndication
import androidx.compose.foundation.Icon
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSizeConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import dev.ricknout.rugbyranker.core.ui.openDrawer
import dev.ricknout.rugbyranker.core.util.CustomTabUtils
import dev.ricknout.rugbyranker.core.util.HorizontalSide
import dev.ricknout.rugbyranker.core.util.ProvideDisplayInsets
import dev.ricknout.rugbyranker.core.util.navigationBarWidth
import dev.ricknout.rugbyranker.core.util.navigationBarsHeight
import dev.ricknout.rugbyranker.core.util.statusBarsPadding
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
            ProvideDisplayInsets {
                val scrollState = rememberScrollState()
                Scaffold(
                    topBar = {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = animate(if (scrollState.value > 0f) 4.dp else 0.dp)
                        ) {
                            Row(modifier = Modifier.statusBarsPadding()) {
                                Spacer(Modifier.navigationBarWidth(HorizontalSide.Left))
                                RugbyRankerButton(
                                    onClick = { openDrawer() },
                                    contentColor = MaterialTheme.colors.onSurface,
                                    rippleColor = MaterialTheme.colors.onSurface
                                ) {
                                    Icon(Icons.Default.Menu)
                                }
                                Spacer(Modifier.navigationBarWidth(HorizontalSide.Right))
                            }
                        }
                    }
                ) {
                    ScrollableColumn(scrollState = scrollState) {
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
                        Spacer(Modifier.navigationBarsHeight())
                    }
                }
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
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
            Text(
                text = stringResource(R.string.version, version ?: ""),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.height(56.dp).padding(16.dp)
            )
        }
    }

    companion object {
        private const val RANKINGS_EXPLANATION_URL = "https://www.world.rugby/rankings/explanation"
        private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.ricknout.rugbyranker"
        private const val GITHUB_URL = "https://github.com/ricknout/rugby-ranker"
    }
}

@Composable
fun RugbyRankerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colors.primary,
    rippleColor: Color = MaterialTheme.colors.primary,
    content: @Composable RowScope.() -> Unit
) {
    val interactionState = remember { InteractionState() }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = Color.Transparent,
        contentColor = contentColor,
        modifier = modifier.clickable(
            onClick = onClick,
            interactionState = interactionState,
            indication = RippleIndication(color = rippleColor)
        )
    ) {
        ProvideTextStyle(
            value = MaterialTheme.typography.h6
        ) {
            Row(
                Modifier
                    .defaultMinSizeConstraints(minHeight = 56.dp)
                    .indication(interactionState, AmbientIndication.current())
                    .padding(PaddingValues(16.dp)),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                children = content
            )
        }
    }
}
