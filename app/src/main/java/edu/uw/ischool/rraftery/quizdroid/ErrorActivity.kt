package edu.uw.ischool.rraftery.quizdroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView

const val ERROR_TYPE : String = "ErrorType"
class ErrorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
        (application as QuizApp).currentActivity = this

        val errorDescription : TextView = findViewById(R.id.errorDescText)
        val retryBtn : Button = findViewById<Button>(R.id.btnRetry)
        val settingsBtn : Button = findViewById<Button>(R.id.btnSettings)
        val quitBtn : Button = findViewById<Button>(R.id.btnQuit)

        val errorType = intent.getIntExtra(ERROR_TYPE, 0)
        retryBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent)
        }
        when (errorType){
            0 -> {
                Log.e("ErrorActivity", "Warning: No error type passed!")
            }
            1 -> {
                //airplane mode error
                errorDescription.text = "Airplane mode is enabled. Please disable it before continuing."
                settingsBtn.text = "Go to Settings"
                settingsBtn.setOnClickListener {
                    val intent = Intent(Settings.ACTION_SETTINGS)
                    startActivity(intent)
                }
                quitBtn.isActivated = false
            }
            2 -> {
                //no signal error
                errorDescription.text = "No signal is detected. Unable to access website for quiz data."
                settingsBtn.isActivated = false
                quitBtn.setOnClickListener {
                    finishAffinity()
                }
            }
            3 -> {
                //questions download error
                errorDescription.text = "Failed to download questions. Please check that you inputted the website address correctly."
                settingsBtn.text = "Go to Preferences"
                settingsBtn.setOnClickListener {
                    val intent = Intent(this, PreferencesActivity::class.java)
                    startActivity(intent)
                }
                quitBtn.setOnClickListener {
                    finishAffinity()
                }
            }
            else ->{
                Log.e("ErrorActivity", "Warning: incorrect error type of $errorType passed!")
            }
        }


        //possible states:
        //1. airplane mode error
        // --return to main activity
        // --go to settings menu
        //2. no signal error
        // --return to main activity
        // --quit app
        //3. questions download error
        // --return to main activity
        // --go to preferences
        // --quit app
    }
}