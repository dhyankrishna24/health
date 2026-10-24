package com.dhyan.health.data.api

import com.dhyan.health.data.model.FoodSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit Interface for Food Nutrition API (e.g., Edamam Food Database API).
 */
interface FoodApiService {
    @GET("api/food-database/v2/parser")
    suspend fun searchFood(
        @Query("ingr") foodQuery: String,
        @Query("app_id") appId: String = "DEMO_APP_ID",
        @Query("app_key") appKey: String = "DEMO_APP_KEY"
    ): FoodSearchResponse
}
