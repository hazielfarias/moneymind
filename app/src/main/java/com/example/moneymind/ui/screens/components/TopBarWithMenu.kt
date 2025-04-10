package com.example.moneymind.ui.screens.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithMenu(
    drawerState: DrawerState,
    onProfileClick: () -> Unit,
    onBackupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    TopAppBar(
        title = {
            Text(
                text = "MoneyMind",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menu",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    drawerState: DrawerState,
    onProfileClick: () -> Unit,
    onBackupClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = modifier,
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = "Perfil do Usuário",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    selected = false,
                    onClick = {
                        onProfileClick()
                        onClose()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = "Perfil"
                        )
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = "Fazer Backup",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    selected = false,
                    onClick = {
                        onBackupClick()
                        onClose()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Backup"
                        )
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        },
        content = {}
    )
}