package com.example.projetnews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NewsAdapter : ListAdapter<Article, NewsAdapter.NewsViewHolder>(DIFF_UTIL_ITEM_CALLBACK) {
    companion object {
        private val DIFF_UTIL_ITEM_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
                override fun areItemsTheSame(oldItem: Article, newItem: Article) = oldItem.source.id == newItem.source.id
                override fun areContentsTheSame(oldItem: Article, newItem: Article) = oldItem == newItem
            }
    }
    var listener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder =
        NewsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_item_v2, parent, false))

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val imageArticle = itemView.findViewById<ImageView>(R.id.imageView)
        private val titreArticle = itemView.findViewById<TextView>(R.id.titre_textView)
        private val descArticle = itemView.findViewById<TextView>(R.id.description_textView)
        private val sourceArticle = itemView.findViewById<TextView>(R.id.source_textView)
        private val dateArticle = itemView.findViewById<TextView>(R.id.date_textView)
        private val shareArticle = itemView.findViewById<ImageButton>(R.id.share_Button)

        init {
            itemView.setOnClickListener {
                // ajout d'un bouton sur la carte (toute le carte est un bouton)
                val newsEntity = getItem(adapterPosition)
                listener?.onTodoItemClicked(newsEntity)
            }

            shareArticle.setOnClickListener {
                // Clique sur le bonton share de la carte
                listener?.onShareClicked(getItem(adapterPosition))
            }
        }
        fun bind(item: Article?){
            item?.let {
                if (it.urlToImage != null && it.urlToImage.isNotEmpty()) {
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

                sourceArticle.text = it.source.name + ", "

                dateArticle.text = getTimePast(it.publishedAt)
            }
        }
    }

    // Tu creer tes fonctions pour les différents type d'appel que tu veux
    interface OnClickListener {
        fun onTodoItemClicked(newsEntity: Article)
        fun onShareClicked(newsEntity: Article)
    }
    fun getTimePast(dateString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.parse(dateString)
        val elapsedTime = Date().time - date.time
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
        val days = TimeUnit.MILLISECONDS.toDays(elapsedTime)
        return when {
            days > 1 -> "$days jours."
            days == 1L -> "1 jours."
            hours > 0 -> "$hours h"
            else -> "$minutes m."
        }
    }
}