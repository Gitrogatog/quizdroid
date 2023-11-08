package edu.uw.ischool.rraftery.quizdroid

import android.util.Log

interface ITopicRepository {

    fun getTopic(topicIndex : Int) : Topic
    fun getTopicSet() : Array<Topic>
    fun getNumQuestions(topicIndex : Int) : Int
    fun hasRemainingQuestions(topicIndex : Int, questionIndex: Int) : Boolean
    fun getQuestion(topicIndex : Int, questionIndex : Int) : Quiz
}
class TopicRepoFromArray(private val topicSet : Array<Topic>) : ITopicRepository {
    override fun getTopic(topicIndex : Int) : Topic {
        return topicSet[topicIndex]
    }
    override fun getTopicSet() : Array<Topic> {
        return topicSet.copyOf()
    }
    override fun getNumQuestions(topicIndex : Int) : Int{
        return getTopic(topicIndex).questions.size
    }
    override fun hasRemainingQuestions(topicIndex : Int, questionIndex: Int) : Boolean {
        return questionIndex < getNumQuestions(topicIndex) - 1
    }
    override fun getQuestion(topicIndex : Int, questionIndex : Int) : Quiz {
        return getTopic(topicIndex).questions[questionIndex]
    }
}

class QuizState(val topicRepo : ITopicRepository) {
    var currentTopic : Int = 0
    var scoreArr = IntArray(0)
    fun getTopicSet() : Array<Topic> {
        return topicRepo.getTopicSet()
    }
    fun getTopic() : Topic {
        return topicRepo.getTopic(currentTopic)
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
    }
    fun chooseTopic(quizNum : Int){
        currentTopic = quizNum
        scoreArr = IntArray(getTopic().questions.count())
    }
}