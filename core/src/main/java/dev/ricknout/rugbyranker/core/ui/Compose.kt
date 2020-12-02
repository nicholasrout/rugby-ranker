package dev.ricknout.rugbyranker.core.ui

import androidx.compose.foundation.AmbientIndication
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSizeConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.ripple.rememberRippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
            indication = rememberRippleIndication(color = rippleColor)
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
                content = content
            )
        }
    }
}
