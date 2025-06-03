package com.sun.ai.aifloat.presentation.ui.main

import androidx.compose.runtime.Immutable
import com.sun.ai.aifloat.domain.entity.Card
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class MainUiState(
    val cards: ImmutableList<Card> = persistentListOf(),
    val preferencesUiState: PreferencesUiState = PreferencesUiState()
)
