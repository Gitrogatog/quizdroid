package edu.uw.ischool.rraftery.quizdroid

data class Topic(val topic : String, val shortDesc : String, val longDesc : String, val questions : Array<Quiz>){
    override fun toString(): String {
        return "$topic\n$shortDesc"
    }
}