package edu.uw.ischool.rraftery.quizdroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class AnswerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)
        (application as QuizApp).currentActivity = this

        val quizIndex = intent.getIntExtra(QUIZ_INDEX, 0)
        val ansIndex = intent.getIntExtra(ANS_INDEX, 0)
        val state = (application as QuizApp).state
        val question : Quiz = state.getQuestion(quizIndex)
        val numQuestions : Int = state.getNumQuestions()

        val chosen : TextView = findViewById(R.id.chosenAns)
        val actual : TextView = findViewById(R.id.realAns)
        val scoreText : TextView = findViewById(R.id.scoreText)
        val btnNext : Button = findViewById(R.id.btnNext)

        Log.i("QuizActivity", "Answer: Quiz: ${quizIndex}, Answer: ${ansIndex}")

        val score : Int = state.getScore(quizIndex)
        chosen.text = question.answers[ansIndex]
        actual.text = question.answers[question.correctIndex]
        scoreText.text = "You have ${score} out of ${quizIndex + 1} correct"

        if(!state.hasRemainingQuestions(quizIndex)){
            btnNext.text = "Finish"
            btnNext.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent)
            }
        }
        else{
            btnNext.text = "Next"
            btnNext.setOnClickListener {
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra(QUIZ_INDEX, quizIndex + 1)
                startActivity(intent)
            }
        }

    }
}