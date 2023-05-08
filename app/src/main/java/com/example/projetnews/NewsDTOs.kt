package com.example.projetnews

import com.google.gson.annotations.SerializedName

data class ResultWrapper(
    val status: String,
    val total: String,
    val articles: List<Article>
)

data class Article(
    @SerializedName("source") val source: Source,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("description") val description: String,
    @SerializedName("url") val url: String,
    @SerializedName("urlToImage") val urlToImage: String,
    @SerializedName("publishedAt") val publishedAt: String
)

data class Source(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

