package com.baec23.ludwig.composable_components.togglable

import androidx.compose.ui.graphics.vector.ImageVector

data class ToggleableIconListItem(
    val name: String,
    val iconImageVector: ImageVector,
    val label: String? = null,
    val isToggled: Boolean = false,
) {
    fun doToggle(): ToggleableIconListItem {
        return this.copy(isToggled = !isToggled)
    }
}