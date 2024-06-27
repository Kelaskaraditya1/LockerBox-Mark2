package com.starkindustries.lockerboxmark2.Activiity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Display.Mode
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.starkindustries.lockerboxmark2.Keys.Keys
import com.starkindustries.lockerboxmark2.R
import com.starkindustries.lockerboxmark2.databinding.ActivityLoginBinding
class LoginActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    var passed:Boolean=false
    lateinit var auth:FirebaseAuth
    internal lateinit var googleSignInClient: GoogleSignInClient
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_login)
        auth=FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_Id))
            .requestEmail()
            .build()
        sharedPreferences=getSharedPreferences(Keys.SHARED_PREFRENCE_NAME, MODE_PRIVATE)
        editor=sharedPreferences.edit()
        binding.password.setOnTouchListener(object: View.OnTouchListener{
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                if(view!=null)
                {
                    if(event?.action==MotionEvent.ACTION_UP)
                    {
                        var selection= binding.password.selectionEnd
                        if(event?.rawX!! >=(binding.password.right-binding.password.compoundDrawables[Keys.RIGHT].bounds.width()))
                        {
                            if(passed)
                            {
                             binding.password.transformationMethod=PasswordTransformationMethod.getInstance()
                             binding.password.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.visibility_off,0)
                             passed=false
                            }
                            else{
                                binding.password.transformationMethod=HideReturnsTransformationMethod.getInstance()
                                binding.password.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.visibility_on,0)
                                passed=true
                            }
                            binding.password.setSelection(selection)
                            return true
                        }
                    }
                }
                return false
            }
        })
        googleSignInClient=GoogleSignIn.getClient(this,gso)
        binding.googleSignButton.setOnClickListener()
        {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent,Keys.GOOGLE_SIGN_IN__ID)
        }
        binding.loginButton.setOnClickListener()
        {
            val view = this.currentFocus
            if(view!=null)
            {
                var manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(view.windowToken,0)
            }
            if(TextUtils.isEmpty(binding.username.text.toString().trim()))
            {
                binding.username.setError("Field is Empty")
                return@setOnClickListener
            }
            else if(TextUtils.isEmpty(binding.password.text.toString().trim()))
            {
                binding.password.setError("Field is Empty")
                return@setOnClickListener
            }
            else if(binding.password.text.toString().trim().length<8)
            {
                binding.password.setError("Password Length should be greater than 8 charecters")
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(binding.username.text.toString().trim(),binding.password.toString().trim()).addOnCompleteListener()
            {
                var user = auth.currentUser
                if(user!=null)
                {
                    user.let { 
                        for(profile in it.providerData)
                            Log.d("informationTag","display name:"+profile.displayName+" email:"+profile.email)
                    }
                }
                else Log.d("userNull","user is null")
                val intent = Intent(this,DashBoardActivity::class.java)
                editor.putBoolean(Keys.LOGIN_STATUS,true)
                editor.apply()
                startActivity(intent)
            }.addOnFailureListener()
            {
                Log.d("errorlistner"," "+it.message.toString())
            }
        }
        binding.signupButton.setOnClickListener()
        {
            var intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK)
        {
            if(requestCode==Keys.GOOGLE_SIGN_IN__ID)
            {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try
                {
                    val account = task.getResult(ApiException::class.java)
                    val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
                    auth.signInWithCredential(credentials).addOnCompleteListener()
                    {
                        if(it.isSuccessful)
                        {
                            sharedPreferences=getSharedPreferences(Keys.SHARED_PREFRENCE_NAME, MODE_PRIVATE)
                            editor=sharedPreferences.edit()
                            editor.putBoolean(Keys.LOGIN_STATUS,true)
                            editor.apply()
                            val intent = Intent(this,DashBoardActivity::class.java)
                        var user = auth.currentUser
                        user.let {
                            if (it != null) {
                                for(profile in it.providerData)
                                    Log.d("informationTag","display name:"+profile.displayName+" email:"+profile.email)
                            }
                        }
                            startActivity(intent)
                        }
                    }.addOnFailureListener()
                    {
                        Toast.makeText(this, "Failed to Signin", Toast.LENGTH_SHORT).show()
                    }
                }
                catch(e:Exception)
                {
                    Log.d("signinError"," "+e.message.toString().trim())
                }
            }
        }
    }
}