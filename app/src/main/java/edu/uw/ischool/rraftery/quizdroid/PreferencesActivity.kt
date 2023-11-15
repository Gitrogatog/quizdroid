package edu.uw.ischool.rraftery.quizdroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import edu.uw.ischool.rraftery.quizdroid.databinding.ActivityPreferencesBinding

class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
        val urlInput : EditText = this.findViewById<EditText>(R.id.urlEditText)
        val minutesInput : EditText = this.findViewById<EditText>(R.id.minutesEditText)
        val mainButton : Button = this.findViewById<Button>(R.id.btnMain)
        urlInput.setText((application as QuizApp).url)
        minutesInput.setText((application as QuizApp).minutes.toString())

        urlInput.addTextChangedListener {
            val input : String = it.toString()
            if(input.isNotEmpty()){
                (application as QuizApp).url = input
            }
        }
        minutesInput.addTextChangedListener {
            val input : String = it.toString()
            if(input.isNotEmpty() && input.toInt() > 0){
                val minutes : Int = input.toInt()
                (application as QuizApp).minutes = minutes
            }

        }
        mainButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

}