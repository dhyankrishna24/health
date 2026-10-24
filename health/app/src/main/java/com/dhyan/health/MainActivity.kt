package com.dhyan.health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.dhyan.health.data.api.FoodApiService
import com.dhyan.health.data.api.WikipediaApiService
import com.dhyan.health.data.health.HealthConnectManager
import com.dhyan.health.data.repository.HealthAndNutritionRepository
import com.dhyan.health.ui.FoodSearchScreen
import com.dhyan.health.ui.FoodSearchViewModel
import com.dhyan.health.ui.theme.CalorieTrackerTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- Retrofit & Network Setup ---
        val foodRetrofit = Retrofit.Builder()
            .baseUrl("https://api.edamam.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val wikiRetrofit = Retrofit.Builder()
            .baseUrl("https://en.wikipedia.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val foodApiService = foodRetrofit.create(FoodApiService::class.java)
        val wikipediaApiService = wikiRetrofit.create(WikipediaApiService::class.java)
        val healthConnectManager = HealthConnectManager(applicationContext)

        val repository = HealthAndNutritionRepository(
            foodApiService = foodApiService,
            wikipediaApiService = wikipediaApiService,
            healthConnectManager = healthConnectManager
        )

        val viewModel = FoodSearchViewModel(repository)

        setContent {
            CalorieTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FoodSearchScreen(viewModel = viewModel)
                }
            }
        }
    }
}
