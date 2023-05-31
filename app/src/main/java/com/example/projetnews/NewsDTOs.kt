package com.example.projetnews

import com.google.gson.annotations.SerializedName

// Classe pour encapsuler la réponse de l'API et les articles associés
data class ResultWrapper(
    val status: String,            // Statut de la réponse de l'API
    val total: String,             // Nombre total d'articles
    val articles: List<Article>    // Liste des articles
)

// Classe pour représenter un article
data class Article(
    @SerializedName("source") val source: Source,               // Source de l'article
    @SerializedName("title") val title: String,                 // Titre de l'article
    @SerializedName("author") val author: String,               // Auteur de l'article
    @SerializedName("description") val description: String,     // Description de l'article
    @SerializedName("url") val url: String,                     // URL de l'article
    @SerializedName("urlToImage") val urlToImage: String,       // URL de l'image associée à l'article
    @SerializedName("publishedAt") val publishedAt: String      // Date de publication de l'article
)

// Classe pour représenter la source d'un article
data class Source(
    @SerializedName("id") val id: String,       // ID de la source
    @SerializedName("name") val name: String    // Nom de la source
)
