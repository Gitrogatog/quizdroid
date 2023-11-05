package edu.uw.ischool.rraftery.quizdroid

import android.util.Log

class TopicState(val topicSet : Array<Topic>) {
    var currentTopic : Int = 0
//    var currentQuestion : Int = 0
    var scoreArr = IntArray(0)
    fun getTopic() : Topic {
        return topicSet[currentTopic]
    }
    fun getNumQuestions() : Int{
        return getTopic().questions.size
    }
    fun hasRemainingQuestions(questionIndex: Int) : Boolean {
        return questionIndex < getNumQuestions() - 1
    }
    fun getQuestion(index : Int) : Quiz {
        return getTopic().questions[index]
    }
//    fun advanceQuiz() : Boolean {
//        currentQuestion++
//        return currentQuestion >= getTopic().questions.size
//    }
    fun getScore(index : Int) : Int {
        var score : Int = 0
        for(i in 0..index){
            score += scoreArr[i]
        }
        return score
    }
    fun selectAnswer(ansIndex : Int, questionIndex : Int) {
        if(ansIndex == getQuestion(questionIndex).correctIndex){
            scoreArr[questionIndex] = 1
        }
        else{
            scoreArr[questionIndex] = 0
        }
        Log.i("TopicState", "Ans: ${ansIndex}, Index: ${questionIndex}. Current score: ${getScore(questionIndex)}")
    }
    fun chooseTopic(quizNum : Int){
        currentTopic = quizNum
//        currentQuestion = 0
        scoreArr = IntArray(getTopic().questions.count())
    }
}