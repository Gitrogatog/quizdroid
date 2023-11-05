package edu.uw.ischool.rraftery.quizdroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val quizList : ListView = findViewById(R.id.quizList)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, (application as QuizApp).state.topicSet)
        // android.R represents Android's general resources
        quizList.adapter = adapter

        quizList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val state = (application as QuizApp).state
            state.chooseTopic(position)
            val intent = Intent(this, TopicActivity::class.java)
            startActivity(intent)
        }

    }
}