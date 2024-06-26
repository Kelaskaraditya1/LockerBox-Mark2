package com.starkindustries.lockerboxmark2.Activiity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.starkindustries.lockerboxmark2.R
import com.starkindustries.lockerboxmark2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)
        auth=FirebaseAuth.getInstance()
        var animation1=AnimationUtils.loadAnimation(applicationContext,R.anim.app_logo_animation)
        binding.apppLogo.startAnimation(animation1)
        var animaton2 = AnimationUtils.loadAnimation(applicationContext,R.anim.app_text_animation)
        binding.appText.startAnimation(animaton2)
        LongOperaiton().execute()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    open inner class LongOperaiton:AsyncTask<String?,Void?,String?>()
    {
        override fun doInBackground(vararg params: String?): String?
        {
            for(i in 0..2)
            {
                try
                {
                    Thread.sleep(350)
                }
                catch (e:Exception)
                {
                    Thread.interrupted()
                    e.printStackTrace()
                }
            }
            return "results"
        }
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            var inext = Intent(this@MainActivity,LoginActivity::class.java)
            startActivity(inext)
            finish()
        }

    }

}