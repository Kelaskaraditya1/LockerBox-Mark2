package com.starkindustries.lockerboxmark2.Activiity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.starkindustries.lockerboxmark2.R
import com.starkindustries.lockerboxmark2.databinding.ActivityForgotPasswordBinding
class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var binding:ActivityForgotPasswordBinding
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_forgot_password)
        auth=FirebaseAuth.getInstance()
        binding.forgotPasswordButton.setOnClickListener()
        {
            val user = auth.currentUser
            if(TextUtils.isEmpty(binding.forgotPassEmail.text.toString().trim()))
            {
                binding.forgotPassEmail.setError("Field is Empty")
                return@setOnClickListener
            }
          auth.sendPasswordResetEmail(binding.forgotPassEmail.text.toString().trim()).addOnCompleteListener(){
              if(it.isSuccessful) {
                  val view = this.currentFocus
                  if (view != null) {
                      val manager: InputMethodManager =
                          getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                      manager.hideSoftInputFromWindow(view.windowToken, 0)
                  }

                  Toast.makeText(
                      applicationContext,
                      "Reset Password Email sent Succcessfully",
                      Toast.LENGTH_SHORT
                  ).show()
                  val intent = Intent(this, LoginActivity::class.java)
                  startActivity(intent)
              }
          }.addOnFailureListener(){
              Toast.makeText(applicationContext, "Failed to sent Reset Password Email,Try again later.", Toast.LENGTH_SHORT).show()
          }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}