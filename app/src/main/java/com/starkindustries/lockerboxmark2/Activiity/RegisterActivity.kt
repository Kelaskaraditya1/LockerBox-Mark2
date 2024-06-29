package com.starkindustries.lockerboxmark2.Activiity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.starkindustries.lockerboxmark2.Keys.Keys
import com.starkindustries.lockerboxmark2.R
import com.starkindustries.lockerboxmark2.databinding.ActivityMainBinding
import com.starkindustries.lockerboxmark2.databinding.ActivityRegisterBinding
class RegisterActivity : AppCompatActivity() {
    lateinit var binding:ActivityRegisterBinding
    var passed=false
    lateinit var auth:FirebaseAuth
    lateinit var user:FirebaseUser
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_register)
        auth=FirebaseAuth.getInstance()
        binding.registerPassword.setOnTouchListener(object: View.OnTouchListener{
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                if(view!=null)
                {
                    if(event?.action== MotionEvent.ACTION_UP)
                    {
                        var selection= binding.registerPassword.selectionEnd
                        if(event?.rawX!! >=(binding.registerPassword.right-binding.registerPassword.compoundDrawables[Keys.RIGHT].bounds.width()))
                        {
                            if(passed)
                            {
                                binding.registerPassword.transformationMethod= PasswordTransformationMethod.getInstance()
                                binding.registerPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.visibility_off,0)
                                passed=false
                            }
                            else{
                                binding.registerPassword.transformationMethod=
                                    HideReturnsTransformationMethod.getInstance()
                                binding.registerPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.visibility_on,0)
                                passed=true
                            }
                            binding.registerPassword.setSelection(selection)
                            return true
                        }
                    }
                }
                return false
            }
        })
        binding.registerProfileImage.setOnClickListener()
        {
            val gallery = Intent(Intent.ACTION_PICK)
            gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(gallery,Keys.GALLERY_REQ_CODE)
        }
        binding.registerButton.setOnClickListener()
        {
            val view = this.currentFocus
            if(view!=null)
            {
                var manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(view.windowToken,0)
            }
            if(TextUtils.isEmpty(binding.registerName.text.toString().trim()))
            {
                binding.registerName.setError("Field is Empty")
                return@setOnClickListener
            }
            else if(TextUtils.isEmpty(binding.registerEmail.text.toString().trim()))
            {
                binding.registerEmail.setError("Field is Empty")
                return@setOnClickListener
            }
            else if(TextUtils.isEmpty(binding.registerUsername.text.toString().trim()))
            {
                binding.registerUsername.setError("Field is Empty")
                return@setOnClickListener
            }
            else if(TextUtils.isEmpty(binding.registerPhoneNo.text.toString().trim()))
            {
                binding.registerPhoneNo.setError("Field is Empty")
                return@setOnClickListener
            }
            else if(binding.registerPhoneNo.text.toString().trim().length<10)
            {
                binding.registerPhoneNo.setError("Phone no length should be at least 10")
                return@setOnClickListener
            }
            else if(TextUtils.isEmpty(binding.registerPassword.text.toString().trim()))
            {
                binding.registerPassword.setError("Field is Empty")
                return@setOnClickListener
            }
            else if(binding.registerPassword.text.toString().trim().length<8)
            {
                binding.registerPassword.setError("Password length should be at least 8 charecters.")
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(binding.registerEmail.text.toString().trim(),binding.registerPassword.text.toString().trim()).
            addOnCompleteListener {
                if(it.isSuccessful)
                {
                    user= auth.currentUser!!
                    val update = UserProfileChangeRequest.Builder()
                        .setDisplayName(binding.registerUsername.text.toString().trim())
                        .setPhotoUri(binding.registerProfileImage.getTag() as Uri)
                        .build()
                    user.updateProfile(update).addOnCompleteListener()
                    {
                        Toast.makeText(applicationContext, "User Created Successfully", Toast.LENGTH_SHORT).show()
                        sharedPreferences=getSharedPreferences(Keys.SHARED_PREFRENCE_NAME, MODE_PRIVATE)
                        editor=sharedPreferences.edit()
                        editor.putBoolean(Keys.LOGIN_STATUS,true)
                        editor.apply()
                        var intent= Intent(this,DashBoardActivity::class.java)
                        startActivity(intent)
                        getUserProfile()
                    }.addOnFailureListener()
                    {
                        Toast.makeText(applicationContext, "Check your internet connection", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener()
                {
                    Toast.makeText(applicationContext, "Failed to signup!!Try again Later", Toast.LENGTH_SHORT).show()
                }
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
            if(requestCode==Keys.GALLERY_REQ_CODE)
                binding.registerProfileImage.setImageURI(data?.data)
                binding.registerProfileImage.setTag(data?.data)
        }
    }
    fun getUserProfile()
    {
        user= auth.currentUser!!
        try
        {
            if(user!=null)
            {
             user.let {
                 for(profile in it.providerData)
                     Log.d("iamgeuri","The email is:"+profile.email)
             }
            }
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
}