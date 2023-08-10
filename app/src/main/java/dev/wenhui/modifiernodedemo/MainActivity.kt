package dev.wenhui.modifiernodedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.wenhui.modifiernodedemo.ui.theme.ModifierNodeDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen() {
    var selectedMenu by remember { mutableStateOf(MenuItem.COMPOSITION_LOCAL) }
    ModifierNodeDemoTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = selectedMenu.label) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        DropDownMenuActionButton {
                            if (it != selectedMenu) {
                                selectedMenu = it
                            }
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                val screenModifier = Modifier.fillMaxSize()
                when (selectedMenu) {
                    MenuItem.COMPOSITION_LOCAL -> {
                        CompositionLocalDemoScreen(modifier = screenModifier)
                    }

                    MenuItem.OBSERVER_NODE -> {
                        ObserverModifierNodeDemoScreen(modifier = screenModifier)
                    }

                    MenuItem.BOUNCE_PRESS -> {
                        BouncePressDemoScreen(modifier = screenModifier)
                    }

                    MenuItem.HANDLE_GESTURE -> {
                        HandleGestureScreen(modifier = screenModifier)
                    }
                }
            }
        }


    }
}

private enum class MenuItem(val label: String) {
    COMPOSITION_LOCAL("Composition local demo"),
    OBSERVER_NODE("Observer node demo"),
    BOUNCE_PRESS("Bounce press demo"),
    HANDLE_GESTURE("Handle gesture demo")
}

@Composable
private fun DropDownMenuActionButton(
    modifier: Modifier = Modifier,
    onMenuClick: (MenuItem) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        IconButton(
            onClick = { menuExpanded = true },
        ) {
            Icon(
                Icons.Default.Menu,
                contentDescription = null
            )
        }

        DropDownMenus(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            onMenuClick = onMenuClick
        )
    }

}

@Composable
private fun DropDownMenus(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onMenuClick: (MenuItem) -> Unit
) {
    DropdownMenu(
        expanded = expanded, onDismissRequest = onDismissRequest
    ) {
        MenuItem.values().forEach { menu ->
            DropdownMenuItem(text = {
                Text(text = menu.label)
            }, onClick = {
                onDismissRequest()
                onMenuClick(menu)
            })
        }
    }
}

