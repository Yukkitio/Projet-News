package com.example.projetnews

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NewsAdapter : ListAdapter<Article, NewsAdapter.NewsViewHolder>(DIFF_UTIL_ITEM_CALLBACK) {
    // Déclaration d'un objet DiffUtil.ItemCallback pour gérer les mises à jour de la ListAdapter
    companion object {
        private val DIFF_UTIL_ITEM_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
                override fun areItemsTheSame(oldItem: Article, newItem: Article) = oldItem.source.id == newItem.source.id
                override fun areContentsTheSame(oldItem: Article, newItem: Article) = oldItem == newItem
            }
    }
    // Déclaration d'une interface pour écouter les événements de clic sur les éléments de la liste
    var listener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder =
        NewsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false))

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        // Récupération des vues du layout de l'élément de liste
        private val imageArticle = itemView.findViewById<ImageView>(R.id.imageView)
        private val titreArticle = itemView.findViewById<TextView>(R.id.titre_textView)
        private val descArticle = itemView.findViewById<TextView>(R.id.description_textView)
        private val sourceArticle = itemView.findViewById<TextView>(R.id.source_textView)
        private val dateArticle = itemView.findViewById<TextView>(R.id.date_textView)
        private val shareArticle = itemView.findViewById<ImageButton>(R.id.share_Button)

        init {
            // Gestion des clics sur l'élément de liste et le bouton de partage
            itemView.setOnClickListener {
                // Lorsque l'élément de liste est cliqué, déclencher l'événement onTodoItemClicked et transmettre l'entité d'article correspondante
                val newsEntity = getItem(adapterPosition)
                listener?.onTodoItemClicked(newsEntity)
            }

            shareArticle.setOnClickListener {
                // Lorsque le bouton de partage est cliqué, déclencher l'événement onShareClicked et transmettre l'entité d'article correspondante
                listener?.onShareClicked(getItem(adapterPosition))
            }
        }

        // Méthode pour lier les données de l'article à la vue de l'élément de liste
        @SuppressLint("SetTextI18n")
        fun bind(item: Article?){
            item?.let {
                if (it.urlToImage != null && it.urlToImage.isNotEmpty()) {
                    // Charger l'image de l'article à partir de l'URL en utilisant Picasso
                    Picasso.get().load(it.urlToImage).into(imageArticle)
                    imageArticle.visibility = View.VISIBLE
                } else {
                    imageArticle.visibility = View.GONE
                }
                titreArticle.text = it.title
                if (it.description != null && it.description.isNotEmpty()) {
                    descArticle.text = it.description
                    descArticle.visibility = View.VISIBLE
                } else {
                    descArticle.visibility = View.GONE
                }
                dateArticle.text = getElapsedTimeFromUTC(it.publishedAt)
                if (dateArticle.text == ""){
                    // Si la date n'est pas disponible, afficher uniquement le nom de la source
                    sourceArticle.text = it.source.name
                }else{
                    // Si la date est disponible, afficher le nom de la source suivi d'un séparateur
                    sourceArticle.text = it.source.name+"   -   "
                }
            }
        }

        // Méthode pour obtenir le temps écoulé à partir d'une date en UTC
        private fun getElapsedTimeFromUTC(utcDate: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            try {
                val date = inputFormat.parse(utcDate)
                val elapsedTime = System.currentTimeMillis() - date.time
                return when {
                    elapsedTime < TimeUnit.MINUTES.toMillis(1) -> "1min"
                    elapsedTime < TimeUnit.HOURS.toMillis(1) -> TimeUnit.MILLISECONDS.toMinutes(elapsedTime).toString() + "min"
                    elapsedTime < TimeUnit.DAYS.toMillis(1) -> TimeUnit.MILLISECONDS.toHours(elapsedTime).toString() + "h"
                    elapsedTime < TimeUnit.DAYS.toMillis(30) -> TimeUnit.MILLISECONDS.toDays(elapsedTime).toString() + "j"
                    elapsedTime < TimeUnit.DAYS.toMillis(365) -> TimeUnit.MILLISECONDS.toDays(elapsedTime).toString() + "mois"
                    else -> TimeUnit.MILLISECONDS.toDays(elapsedTime).toString() + "ans"
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return ""
        }
    }
    // Interface pour écouter les événements de clic sur les éléments de la liste
    interface OnClickListener {
        fun onTodoItemClicked(newsEntity: Article)
        fun onShareClicked(newsEntity: Article)
    }
}