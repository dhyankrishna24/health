package com.dhyan.health.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhyan.health.data.model.CombinedSearchResult
import com.dhyan.health.data.model.DailyActivityData
import com.dhyan.health.data.repository.HealthAndNutritionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FoodSearchUiState {
    data object Idle : FoodSearchUiState
    data object Loading : FoodSearchUiState
    data class Success(
        val result: CombinedSearchResult,
        val activityData: DailyActivityData
    ) : FoodSearchUiState
    data class Error(val message: String) : FoodSearchUiState
}

class FoodSearchViewModel(
    private val repository: HealthAndNutritionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FoodSearchUiState>(FoodSearchUiState.Idle)
    val uiState: StateFlow<FoodSearchUiState> = _uiState.asStateFlow()

    private val _activityData = MutableStateFlow(DailyActivityData())
    val activityData: StateFlow<DailyActivityData> = _activityData.asStateFlow()

    init {
        loadDailyActivity()
    }

    fun loadDailyActivity() {
        viewModelScope.launch {
            val activity = repository.getDailyActivity()
            _activityData.value = activity
        }
    }

    fun searchFood(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.value = FoodSearchUiState.Loading
            try {
                val searchResult = repository.searchFoodAndContext(query.trim())
                val activity = repository.getDailyActivity()
                _activityData.value = activity

                if (searchResult.foodItems.isEmpty() && searchResult.wikiInsight?.extract.isNullOrBlank()) {
                    _uiState.value = FoodSearchUiState.Error("No food nutrition or knowledge details found for '$query'.")
                } else {
                    _uiState.value = FoodSearchUiState.Success(
                        result = searchResult,
                        activityData = activity
                    )
                }
            } catch (e: Exception) {
                _uiState.value = FoodSearchUiState.Error(
                    e.localizedMessage ?: "An unexpected error occurred while searching."
                )
            }
        }
    }
}
