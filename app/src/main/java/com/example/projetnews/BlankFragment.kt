package com.example.projetnews

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class BlankFragment : Fragment() {

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
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsEntity.url))
                startActivity(intent)
            }
            override fun onShareClicked(newsEntity: Article) {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Article URL", newsEntity.url)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "URL copier dans le presse-papier", Toast.LENGTH_LONG).show()
            }
        }
        recyclerView.adapter = adapter
        val countryList = resources.getStringArray(R.array.country_arrays).toList()
        val countryNamesList = mutableListOf<String>()
        for (country in countryList) {
            countryNamesList.add(country.substringBefore(" ("))
        }
        dropDownCountry.setSelection(countryNamesList.indexOf("France"))
        dropDownCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCountry = parent.getItemAtPosition(position).toString()
                val countryCode = selectedCountry.substringAfter("(").substringBefore(")").toLowerCase()

                // Maintenant vous pouvez utiliser countryCode comme argument pour loadData
                loadData(countryCode)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ne rien faire si rien n'est sélectionné
            }
        }

        return view
    }

    private fun loadData(dropValue: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(NewsApiService::class.java)
        service.search_coutry(dropValue)
            .enqueue(object: Callback<ResultWrapper> {
                override fun onResponse(call: Call<ResultWrapper>, response: Response<ResultWrapper>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            adapter.submitList(it.articles)
                        }
                    } else {
                        // gestion d'erreur pour le code d'erreur HTTP
                        val code = response.code()
                        when (code) {
                            400 -> Toast.makeText(requireContext(), "Bad Request", Toast.LENGTH_SHORT).show()
                            401 -> Toast.makeText(requireContext(), "Unauthorized", Toast.LENGTH_SHORT).show()
                            429 -> Toast.makeText(requireContext(), "Too Many Requests", Toast.LENGTH_SHORT).show()
                            500 -> Toast.makeText(requireContext(), "Internal Server Error", Toast.LENGTH_SHORT).show()
                            else -> Toast.makeText(requireContext(), "Error $code", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<ResultWrapper>, t: Throwable) {
                    // gestion d'erreur pour les exceptions lancées
                    when (t) {
                        is IOException -> Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
                        is HttpException -> {
                            when (val code = t.code()) {
                                400 -> Toast.makeText(requireContext(), "Bad Request", Toast.LENGTH_SHORT).show()
                                401 -> Toast.makeText(requireContext(), "Unauthorized", Toast.LENGTH_SHORT).show()
                                429 -> Toast.makeText(requireContext(), "Too Many Requests", Toast.LENGTH_SHORT).show()
                                500 -> Toast.makeText(requireContext(), "Internal Server Error", Toast.LENGTH_SHORT).show()
                                else -> Toast.makeText(requireContext(), "Error $code", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else -> Toast.makeText(requireContext(), "Error ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }
}

class BlankFragment2 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
}