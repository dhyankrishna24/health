package com.dhyan.health.data.api

import com.dhyan.health.data.model.WikipediaSummaryResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit Interface for Wikipedia REST API (Summary endpoint).
 */
interface WikipediaApiService {
    @GET("api/rest_v1/page/summary/{title}")
    suspend fun getSummary(
        @Path("title") title: String
    ): WikipediaSummaryResponse
}
