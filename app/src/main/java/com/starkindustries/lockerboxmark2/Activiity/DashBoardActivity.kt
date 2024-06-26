package com.starkindustries.lockerboxmark2.Activiity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.starkindustries.lockerboxmark2.R
import com.starkindustries.lockerboxmark2.databinding.ActivityDashBoardBinding

class DashBoardActivity : AppCompatActivity() {
    lateinit var binding:ActivityDashBoardBinding
    lateinit var auth:FirebaseAuth
    internal lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash_board)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_dash_board)
        auth=FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_Id))
            .requestEmail()
            .build()
        googleSignInClient= GoogleSignIn.getClient(this,gso)
        binding.logout.setOnClickListener()
        {
            auth.signOut()
            googleSignInClient.signOut().addOnCompleteListener()
            {
                if(it.isSuccessful)
                {
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                }
            }.addOnFailureListener()
            {
                Toast.makeText(applicationContext, "Failed to Signout", Toast.LENGTH_SHORT).show()
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}