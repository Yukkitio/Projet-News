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
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsEntity.url))
                startActivity(intent)
            }
            override fun onShareClicked(newsEntity: Article) {
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
        val countryList = resources.getStringArray(R.array.country_arrays).toList()
        val countryNamesList = mutableListOf<String>()
        for (country in countryList) {
            countryNamesList.add(country.substringBefore(" ("))
        }
        dropDownCountry.setSelection(countryNamesList.indexOf("France"))
        dropDownCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCountry = parent.getItemAtPosition(position).toString()
                val countryCode = selectedCountry.substringAfter("(").substringBefore(")").lowercase()
                loadData(countryCode)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        return view
    }
    private fun loadData(valueData: String) {
        val retrofit = Retrofit.Builder().baseUrl("https://newsapi.org/v2/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(NewsApiService::class.java)
        service.search_coutry(valueData).enqueue(object: Callback<ResultWrapper> {
            override fun onResponse(call: Call<ResultWrapper>, response: Response<ResultWrapper>) {
                if (response.isSuccessful) {
                    adapter.submitList(response.body()?.articles)
                } else {
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