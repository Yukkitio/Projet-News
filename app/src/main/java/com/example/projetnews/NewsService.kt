package com.example.projetnews

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("everything?apiKey=28953a74f32a4c7bba65b6ab35862d20")
    fun search_subject(@Query("q") query: String): Call<ResultWrapper>
    @GET("top-headlines?apiKey=28953a74f32a4c7bba65b6ab35862d20")
    fun search_coutry(@Query("country") query: String): Call<ResultWrapper>
}