package edu.uw.ischool.rraftery.quizdroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val prefButton : Button = findViewById(R.id.btnPrefs)
        val quizList : ListView = findViewById(R.id.quizList)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, (application as QuizApp).state.getTopicSet())
        // android.R represents Android's general resources
        quizList.adapter = adapter

        quizList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val state = (application as QuizApp).state
            state.chooseTopic(position)
            val intent = Intent(this, TopicActivity::class.java)
            startActivity(intent)
        }

        prefButton.setOnClickListener {
            val intent = Intent(this, PreferencesActivity::class.java)
            startActivity(intent)
        }

        //Log.i("Main Activity", filesDir.toString())

    }
}