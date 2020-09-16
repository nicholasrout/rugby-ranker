package dev.ricknout.rugbyranker.info.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.IndicationAmbient
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSizeConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Surface
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.ricknout.rugbyranker.core.ui.openDrawer
import dev.ricknout.rugbyranker.core.util.CustomTabUtils
import dev.ricknout.rugbyranker.info.R
import dev.ricknout.rugbyranker.info.databinding.FragmentInfoBinding
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

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        binding.composeView.setContent {
            MdcTheme {
                Surface {
                    Column {
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
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupEdgeToEdge()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        binding.navigation.setOnClickListener { openDrawer() }
    }

    private fun setupEdgeToEdge() {
        binding.appBarLayout.applySystemWindowInsetsToPadding(left = true, top = true, right = true)
        binding.nestedScrollView.applySystemWindowInsetsToPadding(left = true, right = true, bottom = true)
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
        ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
            Text(
                text = stringResource(R.string.version, version ?: ""),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(16.dp)
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
    content: @Composable RowScope.() -> Unit
) {
    val interactionState = remember { InteractionState() }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = Color.Transparent,
        contentColor = MaterialTheme.colors.primary,
        modifier = modifier.clickable(
            onClick = onClick,
            interactionState = interactionState,
            indication = RippleIndication(color = MaterialTheme.colors.primary)
        )
    ) {
        ProvideTextStyle(
            value = MaterialTheme.typography.h6
        ) {
            Row(
                Modifier
                    .defaultMinSizeConstraints(minHeight = 56.dp)
                    .indication(interactionState, IndicationAmbient.current())
                    .padding(InnerPadding(16.dp)),
                horizontalArrangement = Arrangement.Start,
                verticalGravity = Alignment.CenterVertically,
                children = content
            )
        }
    }
}
