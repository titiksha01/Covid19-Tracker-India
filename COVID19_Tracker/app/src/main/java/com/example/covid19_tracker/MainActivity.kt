package com.example.covid19_tracker

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.internal.bind.ArrayTypeAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val request = Request.Builder().url("https://api.covid19india.org/data.json").build()
        val api = OkHttpClient().newCall(request)

        GlobalScope.launch {
            val response= withContext(Dispatchers.IO) {api.execute()}
            val data = Gson().fromJson(response.body?.string(), Response::class.java)
            launch(Dispatchers.Main) {
                bindCombineData(data?.statewise?.get(0)!!)
                bindAllData(data.statewise)
            }
        }
    }

    private fun bindAllData(statewise: List<StatewiseItem?>) {
        list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, statewise.map{"${it!!.state} | ${it.confirmed} | ${it!!.active} | ${it.recovered} | ${it.deaths}"}
        )
    }


    private fun bindCombineData(statewiseItem: StatewiseItem){

        confirmedTv.text = statewiseItem.confirmed
        activeTv.text = statewiseItem.active
        deceasedTv.text = statewiseItem.deaths
        recoveredTv.text = statewiseItem.recovered
    }
}