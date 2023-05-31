package com.example.projetnews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
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

class SearchFragment : Fragment() {
    private lateinit var dropDownType: Spinner
    private lateinit var dropDownResult: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter
    private lateinit var searchInput: SearchView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Récupération des vues à partir du layout
        dropDownType = view.findViewById(R.id.dropdown_type)
        dropDownResult = view.findViewById(R.id.dropdown_resultat)
        recyclerView = view.findViewById(R.id.recyclerView)
        searchInput = view.findViewById(R.id.SearchInput)
        adapter = NewsAdapter()

        // Configuration des listeners de l'adaptateur pour les événements de clic sur les articles
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

        // Configuration du listener pour la barre de recherche
        searchInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                dropDownType.visibility = View.VISIBLE
                dropDownResult.visibility = View.VISIBLE
                dropDownType.setSelection(0)
                dropDownResult.setSelection(0)
                loadData("Subject", query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                dropDownType.visibility = View.GONE
                dropDownResult.visibility = View.GONE
                return false
            }
        })

        // Configuration du spinner pour le type de recherche
        dropDownResult.visibility = View.GONE
        dropDownType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position).toString()
                val items = when (type) {
                    "Pays" -> resources.getStringArray(R.array.country_arrays)
                    "Categories" -> resources.getStringArray(R.array.category_arrays)
                    "Sources" -> resources.getStringArray(R.array.source_arrays)
                    else -> arrayOf()
                }

                dropDownResult.visibility = View.VISIBLE
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
                dropDownResult.adapter = adapter
                dropDownResult.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val value = parent?.getItemAtPosition(position).toString().lowercase()
                        // Vérifier si le type sélectionné est "Pays"
                        if (type == "Pays") {
                            // Si le type sélectionné est "Pays", on extrait la valeur entre parenthèses et la mettons en minuscules
                            val filteredValue = value.substringAfter("(").substringBefore(")").lowercase()
                            loadData(type, filteredValue)

                        } else if (type == "Sources") {
                            // Si le type sélectionné est "Sources", nous remplaçons les espaces par des tirets et mettons tout en minuscules
                            val filteredValue = value.replace("'", "").replace(" ", "-").lowercase()
                            loadData(type, filteredValue)

                        } else {
                            // Pour les autres types (Categories, etc.), nous utilisons directement la valeur d'origine
                            loadData(type, value)
                        }

                        searchInput.visibility = View.VISIBLE
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        return view
    }

    private fun loadData(requestType: String, valueData: String) {

        // Initialisation de Retrofit pour effectuer les appels à l'API
        val retrofit = Retrofit.Builder().baseUrl("https://newsapi.org/v2/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(NewsApiService::class.java)

        // Sélection de l'appel approprié en fonction du type de recherche
        val call = when (requestType) {
            "Subject" -> service.search_subject(valueData)
            "Pays" -> service.search_coutry(valueData)
            "Categories" -> service.search_category(valueData)
            "Sources" -> service.search_sources(valueData)
            else -> service.search_coutry("us")
        }

        // Exécution de l'appel asynchrone
        call.enqueue(object: Callback<ResultWrapper> {
            override fun onResponse(call: Call<ResultWrapper>, response: Response<ResultWrapper>) {
                if (response.isSuccessful) {
                    // Si la réponse est réussie, mettre à jour la liste des articles dans l'adaptateur
                    adapter.submitList(response.body()?.articles)
                } else {
                    // Gestion des erreurs HTTP
                    handleHttpError(response.code())
                }
            }
            override fun onFailure(call: Call<ResultWrapper>, t: Throwable) {
                // Gestion des erreurs lors de l'appel
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