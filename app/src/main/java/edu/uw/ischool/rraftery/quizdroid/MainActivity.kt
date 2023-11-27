package edu.uw.ischool.rraftery.quizdroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.net.HttpURLConnection
import java.net.URL



class MainActivity : AppCompatActivity() {

    private var url : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as QuizApp).currentActivity = this

        val prefButton : Button = findViewById(R.id.btnPrefs)
        val quizList : ListView = findViewById(R.id.quizList)
        val topicsTitle : TextView = findViewById(R.id.topicsTitleText)

        (application as QuizApp).onEnterMain()
        val topicsArray : Array<Topic> = (application as QuizApp).state.getTopicSet()
        if (topicsArray.size == 0){
            topicsTitle.text = "Loading Topics..."
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, topicsArray)
        // android.R represents Android's general resources
        quizList.adapter = adapter
//        url = (application as QuizApp).url
//        val btnFetch : Button = findViewById(R.id.btnFetch)

        quizList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val state = (application as QuizApp).state
            state.chooseTopic(position)
            val intent = Intent(this, TopicActivity::class.java)
            startActivity(intent)
        }

        prefButton.setOnClickListener {
            val intent = Intent(this, PreferencesActivity::class.java)
            startActivity(intent)
        }
//        btnFetch.setOnClickListener {
//            Log.v("MainActivity", "Sending HTTP request!")
//            //runOnUIThread
//            //takes in a block of code (lambda) to run on UI thread
//            //Do run on UI thread on the side thread
//
//            val mainActivity = this
//            val executor : Executor = Executors.newSingleThreadExecutor()
//            //val handler : Handler = Handler(mainLooper)
//            executor.execute {
//                // Everything in here is now on a different (non-UI)
//                // thread and therefore safe to hit the network
//                val url = URL("http", "tednewardsandbox.site44.com", 80, "/questions.json")
//                val urlConnection = url.openConnection() as HttpURLConnection
//                val inputStream = urlConnection.getInputStream()
//                val reader = InputStreamReader(inputStream)
//                reader.use {
//                    val text = it.readText()
//                    Log.v("MainActivity", text)
//
//                    mainActivity.runOnUiThread {
//                        Toast.makeText(this, "Got the JSON file!", Toast.LENGTH_LONG).show()
//                        btnFetch.text = "FinTracked"
//                        Log.v("MainActivity", "JSON Received!")
//                    }
//                }
//            }
//        }
        //Log.i("Main Activity", filesDir.toString())

    }
    fun reloadMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent)
    }
    fun sendUserToErrorActivity(errorType : Int){
        val intent = Intent(this, ErrorActivity::class.java)
        intent.putExtra(ERROR_TYPE,errorType);
        startActivity(intent)
    }
}