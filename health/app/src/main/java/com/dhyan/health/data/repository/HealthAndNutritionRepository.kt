package com.dhyan.health.data.repository

import com.dhyan.health.data.api.FoodApiService
import com.dhyan.health.data.api.WikipediaApiService
import com.dhyan.health.data.health.HealthConnectManager
import com.dhyan.health.data.model.CombinedSearchResult
import com.dhyan.health.data.model.DailyActivityData
import com.dhyan.health.data.model.FoodNutritionInfo
import com.dhyan.health.data.model.WikipediaSummaryResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Repository responsible for concurrently orchestrating food nutrition queries and Wikipedia contextual insights,
 * as well as reading daily activity data from Health Connect.
 */
class HealthAndNutritionRepository(
    private val foodApiService: FoodApiService,
    private val wikipediaApiService: WikipediaApiService,
    private val healthConnectManager: HealthConnectManager
) {

    /**
     * Executes food search and Wikipedia summary fetching concurrently using Kotlin Coroutines (async / await).
     */
    suspend fun searchFoodAndContext(query: String): CombinedSearchResult = coroutineScope {
        val foodDeferred = async {
            try {
                foodApiService.searchFood(foodQuery = query)
            } catch (e: Exception) {
                null
            }
        }

        val wikiDeferred = async {
            try {
                wikipediaApiService.getSummary(title = query)
            } catch (e: Exception) {
                null
            }
        }

        val foodResponse = foodDeferred.await()
        val wikiResponse = wikiDeferred.await()

        val foodItems = foodResponse?.hints?.map { hint ->
            val food = hint.food
            val nutrients = food.nutrients
            FoodNutritionInfo(
                foodId = food.foodId,
                name = food.label,
                category = food.category ?: "General Food",
                imageUrl = food.image,
                calories = nutrients?.calories ?: 0.0,
                saturatedFatGrams = nutrients?.saturatedFat ?: 0.0,
                transFatGrams = nutrients?.transFat ?: 0.0,
                dietaryFiberGrams = nutrients?.dietaryFiber ?: 0.0
            )
        } ?: emptyList()

        CombinedSearchResult(
            foodItems = foodItems,
            wikiInsight = wikiResponse
        )
    }

    /**
     * Reads daily activity metrics from Health Connect.
     */
    suspend fun getDailyActivity(): DailyActivityData {
        return healthConnectManager.fetchDailyActivity()
    }
}
