package edu.uw.ischool.rraftery.quizdroid

data class Topic(val topic : String, val description : String, val questions : Array<Quiz>){
    override fun toString(): String {
        return topic
    }
}