package edu.uw.ischool.rraftery.quizdroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

const val QUIZ_INDEX = "QuizIndex"
const val ANS_INDEX = "AnsIndex"

class TopicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)
        val topicTitle : TextView = findViewById(R.id.topicTitle)
        val topicDesc : TextView = findViewById(R.id.topicDesc)
        val qNum : TextView = findViewById(R.id.qCount)
        val btnBegin : Button = findViewById(R.id.btnBegin)
        val state = (application as QuizApp).state
        val quiz = state.getTopic()
        topicTitle.text = quiz.topic
        topicDesc.text = quiz.description
        qNum.text = quiz.questions.size.toString()
        btnBegin.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra(QUIZ_INDEX, 0);
            startActivity(intent)
        }

    }
}