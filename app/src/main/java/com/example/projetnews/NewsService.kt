package com.example.projetnews

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Interface définissant les requêtes d'API utilisées pour récupérer les actualités
interface NewsApiService {

    // Requête pour rechercher des sujets d'actualité en fonction d'une requête spécifique
    @GET("everything?language=fr&sortBy=relevancy&apiKey=28953a74f32a4c7bba65b6ab35862d20")
    fun search_subject(@Query("q") query: String): Call<ResultWrapper>

    // Requête pour rechercher des actualités d'un pays spécifique
    @GET("top-headlines?apiKey=28953a74f32a4c7bba65b6ab35862d20")
    fun search_coutry(@Query("country") query: String): Call<ResultWrapper>

    // Requête pour rechercher des actualités d'une catégorie spécifique
    @GET("top-headlines?language=fr&sortBy=popularity&apiKey=28953a74f32a4c7bba65b6ab35862d20")
    fun search_category(@Query("category") query: String): Call<ResultWrapper>

    // Requête pour rechercher des actualités à partir de sources spécifiques
    @GET("top-headlines?sortBy=popularity&apiKey=28953a74f32a4c7bba65b6ab35862d20")
    fun search_sources(@Query("sources") query: String): Call<ResultWrapper>
}
