package edu.uw.ischool.rraftery.quizdroid

import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors


const val ALARM_ACTION = "edu.uw.ischool.rraftery.ALARM"
class QuizApp : Application() {
    private lateinit var topicRepo : ITopicRepository
        //TopicRepoFromArray(HardcodedQuizSet().getHardcodedTopicSet())
    lateinit var state : QuizState
//    val quizCreator : QuizSetFromString = QuizSetFromString()
    var isPingParamsDirty : Boolean = false
    var badWebAddress : Boolean = false
    var url : String = "tednewardsandbox.site44.com"
        set(value) {
            field = value
            isPingParamsDirty = true
            badWebAddress = false
        }
    var minutes : Int = 1
        set(value) {
            field = value
            isPingParamsDirty = true
        }

    lateinit var alarmManager: AlarmManager
    lateinit var repeatPing : PendingIntent
    var receiver : BroadcastReceiver? = null
    var hasRepeatPingEnabled : Boolean = false

//    val lifecycleListener : ActivityLifecycleListener = ActivityLifecycleListener()
    lateinit var currentActivity : Activity
    val networkAccesser : NetworkAccesser = NetworkAccesser(this)

    lateinit var quizSetGenerator : QuizSetFromJSON
    lateinit var quizFilePath : String
    var isTopicRepoDirty : Boolean = false
    var hasInitQuizData : Boolean = false

    override fun onCreate() {
        super.onCreate()
        Log.i("QuizApp", "QuizApp has been created!")
        quizFilePath = "${filesDir.toString()}/questions.json"
//        registerActivityLifecycleCallbacks(lifecycleListener)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        quizSetGenerator = QuizSetFromJSON(quizFilePath)
        topicRepo = TopicRepoFromArray(arrayOf<Topic>())
        state = QuizState(topicRepo)
        //updateRepo()
        //attemptUpdateState()

//        createRepeatAlarm()
    }

    fun createRepeatAlarm() {
        hasRepeatPingEnabled = true
        val activityThis = this

        if (receiver == null) {
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if(!badWebAddress){
                        Toast.makeText(activityThis, "Attempting to connect...", Toast.LENGTH_SHORT).show()
                        networkAccesser.runAccesser()
                    }

                }
            }
            val filter = IntentFilter(ALARM_ACTION)
            registerReceiver(receiver, filter)
        }

        // Create the PendingIntent
        val intent = Intent(ALARM_ACTION)
        repeatPing = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            60000 * minutes.toLong(),
            repeatPing)
    }
    fun cancelRepeatPing(){
        if(hasRepeatPingEnabled){
            alarmManager.cancel(repeatPing)
        }
    }
    fun updateRepo(){

        val topicArr : Array<Topic> = quizSetGenerator.getTopicSet()
        if(topicArr.size > 0){
            topicRepo = TopicRepoFromArray(topicArr)
            isTopicRepoDirty = true
            if(currentActivity is MainActivity){
                (currentActivity as MainActivity).reloadMainActivity()
            }
//            try {
//                val tempRepo : ITopicRepository = TopicRepoFromArray(quizSetGenerator.getTopicSet(quizSet))
//            }
//            catch (ioEx : IOException) {
//                Log.v("Application", "LOADING JSON ERROR: $ioEx")
//            }
//            finally {
//                topicRepo = TopicRepoFromArray(quizCreator.getTopicSet(quizSet))
//            }
        }
    }
    fun onEnterMain(){
        if(!hasInitQuizData){
            hasInitQuizData = true
            networkAccesser.runAccesser()
        }
        else{
            checkAddRepeatPing()
        }
        attemptUpdateState()
    }
    fun checkAddRepeatPing(){
        if(!hasRepeatPingEnabled){
            createRepeatAlarm()
        }
        else if(isPingParamsDirty){
            cancelRepeatPing()
            createRepeatAlarm()
        }
    }
    fun attemptUpdateState(){
        state = QuizState(topicRepo)
//        if(isPingParamsDirty){
//            cancelRepeatPing()
//            createRepeatAlarm()
//        }
    }
    fun airplaneModeError(){
        attemptSendToError(1)
    }
    fun signalError(){
        attemptSendToError(2)
    }
    fun downloadError(){
        badWebAddress = true
        attemptSendToError(3)
    }
    fun attemptSendToError(errorType : Int) {
        cancelRepeatPing()
        if(currentActivity is MainActivity){
            (currentActivity as MainActivity).sendUserToErrorActivity(errorType)
        }
    }
    fun updateQuizFile(textContent : String){
//        val cw = ContextWrapper(this)
//        val directory = cw.getDir("media", MODE_PRIVATE)
        FileWriter(quizFilePath) //externalCacheDir.toString() + "/questions.json"
            .use {
//                val jsonObj = JSONObject()
//                Log.v("MainActivity", "Writing JSON: $jsonObj")
                it.write(textContent)

                // at the end of this block, use() will call
                // .close() on the FileReader for us
            }
        updateRepo()
        logQuizFileContents()
    }
    private fun logQuizFileContents(){
        FileReader(quizFilePath)
            .use {
                val text = it.readText()
                Log.v("Quiz App", "Here is my contents:\n$text")

                // at the end of this block, use() will call
                // .close() on the FileReader for us
            }
    }
}

class ActivityLifecycleListener() : Application.ActivityLifecycleCallbacks{
    lateinit var activity : Activity
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        activity = p0
    }

    override fun onActivityStarted(p0: Activity) {}

    override fun onActivityResumed(p0: Activity) {}

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {}

}

class NetworkAccesser(val app: QuizApp){ //, val lifecycleListener: ActivityLifecycleListener

    fun runAccesser(){
        if (attemptConnect()){
            accessWebsite()
        }
    }
    fun isConnectedToInternet() : Boolean {
        val cm : ConnectivityManager = app.getSystemService(Application.CONNECTIVITY_SERVICE) as ConnectivityManager

        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }
    fun isAirplaneModeOn() : Boolean {
        return Settings.System.getInt(app.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0) != 0
    }
    fun attemptConnect() : Boolean{
        if (!isConnectedToInternet()){
            if(isAirplaneModeOn()) {
                //airplane mode is on
                app.currentActivity.runOnUiThread {
                    app.airplaneModeError()
                    Toast.makeText(app.currentActivity, "Airplane Mode is Enabled!", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                //user just isn't connected to the internet
                app.currentActivity.runOnUiThread {
                    app.signalError()
                    Toast.makeText(app.currentActivity, "You aren't connected to the internet!", Toast.LENGTH_SHORT).show()
                }
            }
            return false;
        }
        return true;
    }
    fun accessWebsite(){
        Log.v("MainActivity", "Sending HTTP request!")
        Toast.makeText(app, "Attempting to download JSON file...", Toast.LENGTH_SHORT).show()
        //runOnUIThread
        //takes in a block of code (lambda) to run on UI thread
        //Do run on UI thread on the side thread

        val executor : Executor = Executors.newSingleThreadExecutor()
        //val handler : Handler = Handler(mainLooper)
        executor.execute {
            // Everything in here is now on a different (non-UI)
            // thread and therefore safe to hit the network
            var receivedText : String = ""
            try{
                val url = URL("http", app.url, 80, "/questions.json")
                val urlConnection = url.openConnection() as HttpURLConnection
                val inputStream = urlConnection.getInputStream()
                val reader = InputStreamReader(inputStream)
                reader.use {
                    receivedText = it.readText()
                }
            }
            catch (ioEx : IOException) {
                Log.v("NetworkAccesser", "JSON ERROR: $ioEx")
                app.currentActivity.runOnUiThread {
                    app.downloadError()
                    Toast.makeText(app, "Failed to download JSON file", Toast.LENGTH_SHORT).show()
                }
            }
            finally {
                app.currentActivity.runOnUiThread {
                    app.updateQuizFile(receivedText)
                    Toast.makeText(app, "Successfully downloaded the JSON file!", Toast.LENGTH_SHORT).show()
//                    btnFetch.text = "FinTracked"
                    Log.v("MainActivity", "JSON Received!")

                }
            }
        }
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

class QuizSetFromString{
    fun getTopicSet(quizSet : String) : Array<Topic> {
        val topicArr : ArrayList<Topic> = ArrayList<Topic>()
        var jsonArr : JSONArray = JSONArray()
        jsonArr = JSONArray(quizSet)
        for (i in 0..<jsonArr.length()){
            val item = jsonArr[i]
            if(item is JSONObject){
                topicArr.add(getTopicFromJSON(item))
            }
        }
        return topicArr.toTypedArray()
    }
    private fun getTopicFromJSON(jsonObj : JSONObject) : Topic {
        var quizArr : ArrayList<Quiz> = ArrayList<Quiz>()
        var quizJSON = jsonObj["questions"]
        if(quizJSON is JSONArray){
            for (i in 0..<quizJSON.length()){
                val quizObj = quizJSON[i]
                if(quizObj is JSONObject){
                    val quiz : Quiz = getQuizFromJSON(quizObj)
                    quizArr.add(quiz)
                }
            }
        }
        return Topic(jsonObj["title"] as String, jsonObj["desc"] as String,
            jsonObj["desc"] as String, quizArr.toTypedArray())
    }
    private fun getQuizFromJSON(jsonObj : JSONObject) : Quiz {
        val questionsJSON : JSONArray = jsonObj["answers"] as JSONArray
        var questions : ArrayList<String> = ArrayList<String>()
        for(i in 0..<questionsJSON.length()){
            val question = questionsJSON[i]
            if(question is String){
                questions.add(question)
            }
        }
        return Quiz(jsonObj["text"] as String, questions.toTypedArray(), Math.max((jsonObj["answer"] as String).toInt() - 1, 0))
    }
}

class QuizSetFromJSON(private val filesDir : String){
    fun getTopicSet() : Array<Topic> {

        val topicArr : ArrayList<Topic> = ArrayList<Topic>()
        var jsonArr : JSONArray = JSONArray()
        try {
            val messagesReader = FileReader(filesDir)
            messagesReader.use {
                val buffer = it.readText()
                jsonArr = JSONArray(buffer)
                Log.v("Main Activity", "JSON in assets/ is: $jsonArr")

                // at the end of this block, use() will call
                // .close() on the FileReader for us
            }
        }
        catch (ioEx : IOException) {
            // Something went wrong IO-wise, notify the user
            //Toast.makeText(this, "Error: $ioEx", Toast.LENGTH_LONG).show()
            Log.v("Main Activity", "JSON ERROR: $ioEx")
        }
        for (i in 0..<jsonArr.length()){
            val item = jsonArr[i]
            if(item is JSONObject){
                topicArr.add(getTopicFromJSON(item))
            }
        }
        return topicArr.toTypedArray()
    }
    fun getTopicFromJSON(jsonObj : JSONObject) : Topic {
        var quizArr : ArrayList<Quiz> = ArrayList<Quiz>()
        var quizJSON = jsonObj["questions"]
        if(quizJSON is JSONArray){
            for (i in 0..<quizJSON.length()){
                val quizObj = quizJSON[i]
                if(quizObj is JSONObject){
                    val quiz : Quiz = getQuizFromJSON(quizObj)
                    quizArr.add(quiz)
                }
            }
        }
        return Topic(jsonObj["title"] as String, jsonObj["desc"] as String,
            jsonObj["desc"] as String, quizArr.toTypedArray())
    }
    fun getQuizFromJSON(jsonObj : JSONObject) : Quiz {
        val questionsJSON : JSONArray = jsonObj["answers"] as JSONArray
        var questions : ArrayList<String> = ArrayList<String>()
        for(i in 0..<questionsJSON.length()){
            val question = questionsJSON[i]
            if(question is String){
                questions.add(question)
            }
        }
        return Quiz(jsonObj["text"] as String, questions.toTypedArray(), Math.max((jsonObj["answer"] as String).toInt() - 1, 0))
    }
}