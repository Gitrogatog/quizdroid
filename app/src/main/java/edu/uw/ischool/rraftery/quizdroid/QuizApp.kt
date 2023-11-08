package edu.uw.ischool.rraftery.quizdroid

import android.app.Application
import android.util.Log

class QuizApp : Application() {
    private val topicRepo : ITopicRepository = TopicRepoFromArray(HardcodedQuizSet().getHardcodedTopicSet())
    val state : QuizState = QuizState(topicRepo)

    override fun onCreate() {
        super.onCreate()
        Log.i("QuizApp", "QuizApp has been created!")
    }
}

class HardcodedQuizSet(){
    var MathQuiz : Topic = Topic("Math", "Mathematical quiz", "Test your computation skills against this set of math problems!", arrayOf<Quiz>(
        Quiz("What is 2 + 2?", arrayOf<String>("4", "5", "6", "7"), 0),
        Quiz("What is 5 to the power of 3?", arrayOf<String>("15", "25", "125", "625"), 2),
        Quiz("Which of these are prime?", arrayOf<String>("1", "12", "13", "27"), 2),
        Quiz("What is the sum of all numbers from 1 to 100?", arrayOf<String>("5000", "5050", "4000", "101"), 1)
    ))
    var PhysicsQuiz : Topic = Topic("Physics", "Physics quiz", "See how you measure up with this Physics quiz!", arrayOf<Quiz>(
        Quiz("What is the acceleration due to gravity in meters per second squared?", arrayOf<String>("9.8", "20", "10", "4.9"), 0),
        Quiz("Which of these hit Newton on the head?", arrayOf<String>("Ham Sandwich", "Anvil", "Boomerang", "Apple"), 3),
        Quiz("Which is heavier: 1 kg of steel or 1 kg of feathers?", arrayOf<String>("Steel", "Feathers", "Neither", "I don't get it"), 2)
    ))
    var MarvelQuiz : Topic = Topic("Marvel","Marvel Comics quiz",  "Hope you read the Marvel comics, because none of these are in the movies!", arrayOf<Quiz>(
        Quiz("Who is Superman's secret identity?", arrayOf<String>("Peter Parker", "Clark Kent", "Walter White", "Me"), 1),
        Quiz("Which of these characters has beaten Batman in a fight?", arrayOf<String>("Superman", "Spiderman", "Morbius", "Big Wheel"), 2)
    ))
    var PokemonQuiz : Topic = Topic("Pokemon","Pocket Monsters quiz",  "How many of these little gremlins do you know?", arrayOf<Quiz>(
        Quiz("Which of the following types is Steel weak to?", arrayOf<String>("Water", "Electric", "Ground", "Flying"), 2),
        Quiz("What color are Raichu's cheeks?", arrayOf<String>("Red", "Blue", "Orange", "Yellow"), 3),
        Quiz("What type combination is Solrock?", arrayOf<String>("Rock/Psychic", "Rock/Flying", "Fire/Psychic", "Fire/Ground"), 0),
        Quiz("Which Pokemon has the highest HP stat in the game?", arrayOf<String>("Snorlax", "Blissey", "Guzzlord", "Wailord"), 1),
        Quiz("Which of these moves has a glitch associated with them in the original Pokemon Red and Blue?", arrayOf<String>("Hyper Beam", "Psywave", "Dig", "All of the above"), 3),
    ))

    fun getHardcodedTopicSet() : Array<Topic> {
        return arrayOf<Topic>(MathQuiz, PhysicsQuiz, MarvelQuiz, PokemonQuiz)
    }
}