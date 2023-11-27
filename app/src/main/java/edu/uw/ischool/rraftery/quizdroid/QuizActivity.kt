package edu.uw.ischool.rraftery.quizdroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity


class QuizActivity : AppCompatActivity() {
    lateinit var submitButton: Button
    var selectedAnswer : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        (application as QuizApp).currentActivity = this

        val quizIndex = intent.getIntExtra(QUIZ_INDEX, 0)
        val state = (application as QuizApp).state
        val question : Quiz = state.getQuestion(quizIndex)

        val qText : TextView = findViewById(R.id.questionText)
        submitButton = findViewById(R.id.btnSubmit)

        qText.text = question.question
        submitButton.isEnabled = false
        submitButton.setOnClickListener {
            state.selectAnswer(selectedAnswer, quizIndex)
            val intent = Intent(this, AnswerActivity::class.java)
            intent.putExtra(QUIZ_INDEX, quizIndex);
            intent.putExtra(ANS_INDEX, selectedAnswer)
            Log.i("QuizActivity", "Question: Quiz: ${quizIndex}, Answer: ${selectedAnswer}")
            startActivity(intent)
        }

        val rg : RadioGroup = findViewById(R.id.ansGroup) as RadioGroup
        for(i in 0..<question.answers.size) {
            addButton(rg, question, i)
        }

//        val callback = object : OnBackPressedCallback(
//            true // default to enabled
//        ) {
//            override fun handleOnBackPressed() {
//                if(quizIndex == 0){
//                    val intent = Intent(this, MainActivity::class.java)
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent)
//                }
//
//            }
//        }
        if(quizIndex > 0){
            this.onBackPressedDispatcher.addCallback(
                this, // LifecycleOwner
                ReturnToQuiz(this, quizIndex - 1)
            )
        }
        else{
            this.onBackPressedDispatcher.addCallback(
                this, // LifecycleOwner
                ReturnToMain(this)
            )
        }
    }

    fun onSelectAnswer(ansIndex : Int){
        selectedAnswer = ansIndex
        submitButton.isEnabled = true
    }

    fun addButton(group : RadioGroup, question : Quiz, ansIndex : Int){
        val radioButton = RadioButton(this)
        radioButton.text = question.answers[ansIndex]
        radioButton.textSize = 20f

        val params = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT

        )
        group.addView(radioButton, params)

        radioButton.setOnClickListener {
            onSelectAnswer(ansIndex)
        }

    }
}

class ReturnToMain(val context : Context) : OnBackPressedCallback(true){
    override fun handleOnBackPressed() {

        val intent = Intent(context, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent)
    }
}

class ReturnToQuiz(val context : Context, val quizIndex : Int) : OnBackPressedCallback(true){
    override fun handleOnBackPressed() {

        val intent = Intent(context, QuizActivity::class.java)
        intent.putExtra(QUIZ_INDEX, quizIndex)
        context.startActivity(intent)


    }
}