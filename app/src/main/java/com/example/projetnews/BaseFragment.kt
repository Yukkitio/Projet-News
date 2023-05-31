package com.example.projetnews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class BaseFragment : Fragment() {

    private lateinit var dropDownCountry: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_base, container, false)
        dropDownCountry = view.findViewById(R.id.dropdown_spinner)
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = NewsAdapter()
        adapter.listener = object : NewsAdapter.OnClickListener {

            override fun onTodoItemClicked(newsEntity: Article) {
                // Lorsqu'un élément de la liste est cliqué, ouvrir le lien de l'article dans un navigateur externe
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsEntity.url))
                startActivity(intent)
            }
            override fun onShareClicked(newsEntity: Article) {
                // Lorsque le bouton de partage est cliqué, partager le lien de l'article
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Hey ! Regarde moi ce super article : " + newsEntity.url)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }
        recyclerView.adapter = adapter

        // Récupérer la liste des pays à partir des ressources et extraire les abréviations des pays
        val countryList = resources.getStringArray(R.array.country_arrays).toList()
        val countryNamesList = mutableListOf<String>()
        for (country in countryList) {
            countryNamesList.add(country.substringBefore(" ("))
        }

        // Sélectionner la France par défaut dans la liste déroulante
        dropDownCountry.setSelection(countryNamesList.indexOf("France"))

        dropDownCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Lorsqu'un item est sélectionné dans la liste déroulante, charger les données correspondantes
                val selectedCountry = parent.getItemAtPosition(position).toString()
                val countryCode = selectedCountry.substringAfter("(").substringBefore(")").lowercase()
                loadData(countryCode)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Aucune action lorsqu'aucun item n'est sélectionné
            }
        }
        return view
    }
    private fun loadData(valueData: String) {
        // Configurer Retrofit pour appeler l'API de News
        val retrofit = Retrofit.Builder().baseUrl("https://newsapi.org/v2/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(NewsApiService::class.java)

        // Appeler l'API pour rechercher les articles de l'item sélectionné
        service.search_coutry(valueData).enqueue(object: Callback<ResultWrapper> {
            override fun onResponse(call: Call<ResultWrapper>, response: Response<ResultWrapper>) {
                if (response.isSuccessful) {
                    // Si la réponse de l'API est réussie, mettre à jour la liste des articles dans l'adaptateur
                    adapter.submitList(response.body()?.articles)
                } else {
                    // En cas d'échec de l'appel à l'API, gérer l'erreur via la fonction handleHttpError
                    handleHttpError(response.code())
                }
            }
            override fun onFailure(call: Call<ResultWrapper>, t: Throwable) {
                handleError(t)
            }
        })
    }
    private fun handleHttpError(code: Int) {
        val message = when (code) {
            400 -> "Bad Request"
            401 -> "Unauthorized"
            429 -> "Too Many Requests"
            500 -> "Internal Server Error"
            else -> "Error $code"
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun handleError(t: Throwable) {
        val message = when (t) {
            is IOException -> "Network Error"
            is HttpException -> handleHttpError(t.code())
            else -> "Error ${t.message}"
        }
        Toast.makeText(requireContext(), message.toString(), Toast.LENGTH_SHORT).show()
    }
}