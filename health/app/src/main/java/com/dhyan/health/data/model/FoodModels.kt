package com.dhyan.health.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data classes representing responses from Nutritional APIs (e.g. Edamam / Nutritionix).
 */
data class FoodSearchResponse(
    @SerializedName("hints") val hints: List<FoodHint>? = emptyList(),
    @SerializedName("parsed") val parsed: List<FoodHint>? = emptyList()
)

data class FoodHint(
    @SerializedName("food") val food: FoodItem
)

data class FoodItem(
    @SerializedName("foodId") val foodId: String,
    @SerializedName("label") val label: String,
    @SerializedName("knownAs") val knownAs: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("nutrients") val nutrients: Nutrients? = null
)

data class Nutrients(
    @SerializedName("ENERC_KCAL") val calories: Double? = 0.0,
    @SerializedName("FASAT") val saturatedFat: Double? = 0.0,
    @SerializedName("FATRN") val transFat: Double? = 0.0,
    @SerializedName("FIBTG") val dietaryFiber: Double? = 0.0,
    @SerializedName("PROCNT") val protein: Double? = 0.0,
    @SerializedName("CHOCDF") val carbs: Double? = 0.0
)

/**
 * Data class representing Wikipedia / Wikidata Educational Summary response.
 */
data class WikipediaSummaryResponse(
    @SerializedName("title") val title: String? = null,
    @SerializedName("displaytitle") val displayTitle: String? = null,
    @SerializedName("extract") val extract: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("thumbnail") val thumbnail: WikiThumbnail? = null
)

data class WikiThumbnail(
    @SerializedName("source") val source: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)

/**
 * Clean UI Domain Model combining nutrition stats.
 */
data class FoodNutritionInfo(
    val foodId: String,
    val name: String,
    val category: String,
    val imageUrl: String?,
    val calories: Double,
    val saturatedFatGrams: Double,
    val transFatGrams: Double,
    val dietaryFiberGrams: Double
)

/**
 * Combined response payload holding nutrition search results and contextual Wikipedia educational insight.
 */
data class CombinedSearchResult(
    val foodItems: List<FoodNutritionInfo>,
    val wikiInsight: WikipediaSummaryResponse?
)
