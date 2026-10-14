package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.SocietyNavTab

@Composable
fun SocietyBottomNav(
    selectedTab: SocietyNavTab,
    onTabSelected: (SocietyNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("society_bottom_nav")
    ) {
        NavigationBarItem(
            selected = selectedTab == SocietyNavTab.NOTICES,
            onClick = { onTabSelected(SocietyNavTab.NOTICES) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == SocietyNavTab.NOTICES) Icons.Filled.Campaign else Icons.Outlined.Campaign,
                    contentDescription = "Notices"
                )
            },
            label = { Text("Notices") },
            modifier = Modifier.testTag("nav_tab_notices")
        )

        NavigationBarItem(
            selected = selectedTab == SocietyNavTab.COMPLAINTS,
            onClick = { onTabSelected(SocietyNavTab.COMPLAINTS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == SocietyNavTab.COMPLAINTS) Icons.Filled.Build else Icons.Outlined.Build,
                    contentDescription = "Complaints"
                )
            },
            label = { Text("Requests") },
            modifier = Modifier.testTag("nav_tab_complaints")
        )

        NavigationBarItem(
            selected = selectedTab == SocietyNavTab.VISITORS,
            onClick = { onTabSelected(SocietyNavTab.VISITORS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == SocietyNavTab.VISITORS) Icons.Filled.Badge else Icons.Outlined.Badge,
                    contentDescription = "Visitors"
                )
            },
            label = { Text("Visitors") },
            modifier = Modifier.testTag("nav_tab_visitors")
        )

        NavigationBarItem(
            selected = selectedTab == SocietyNavTab.PAYMENTS,
            onClick = { onTabSelected(SocietyNavTab.PAYMENTS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == SocietyNavTab.PAYMENTS) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                    contentDescription = "Dues"
                )
            },
            label = { Text("Dues") },
            modifier = Modifier.testTag("nav_tab_payments")
        )

        NavigationBarItem(
            selected = selectedTab == SocietyNavTab.DIRECTORY,
            onClick = { onTabSelected(SocietyNavTab.DIRECTORY) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == SocietyNavTab.DIRECTORY) Icons.Filled.Contacts else Icons.Outlined.Contacts,
                    contentDescription = "Directory"
                )
            },
            label = { Text("Directory") },
            modifier = Modifier.testTag("nav_tab_directory")
        )
    }
}
