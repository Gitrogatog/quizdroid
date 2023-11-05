package edu.uw.ischool.rraftery.quizdroid

data class Quiz(val question : String, val answers : Array<String>, val correctIndex : Int){
    override fun toString(): String {
        return question
    }
}