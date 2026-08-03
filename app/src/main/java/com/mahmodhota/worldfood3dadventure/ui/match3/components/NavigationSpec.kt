package com.mahmodhota.worldfood3dadventure.ui.match3.components

enum class NavTab(
    val key: String,
    val label: String
) {
    Profile("profile", "Profile"),
    Rewards("rewards", "Rewards"),
    Passport("book", "Passport"),
    World("world", "World")
}

object NavigationSpec {
    /**
     * Official fixed bottom-nav order across all screens.
     */
    val orderedTabs: List<NavTab> = listOf(
        NavTab.Profile,
        NavTab.Rewards,
        NavTab.Passport,
        NavTab.World
    )
}

