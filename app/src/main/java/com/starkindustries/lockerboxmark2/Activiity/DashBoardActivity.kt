package com.starkindustries.lockerboxmark2.Activiity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.starkindustries.lockerboxmark2.Adapters.ViewPagerAdapte
import com.starkindustries.lockerboxmark2.Fragments.HomeFragment
import com.starkindustries.lockerboxmark2.Fragments.ProfileFragment
import com.starkindustries.lockerboxmark2.Fragments.UploadFragment
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
        setSupportActionBar(binding.toolBar)
        try
        {
            supportActionBar?.setTitle("Locker Box")
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
        auth=FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_Id))
            .requestEmail()
            .build()
        googleSignInClient= GoogleSignIn.getClient(this,gso)
//        binding.viewPager.adapter=ViewPagerAdapte(applicationContext,supportFragmentManager)
//        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.bottomNavigation.setOnNavigationItemSelectedListener(object:BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(p0: MenuItem): Boolean {
                when(p0.itemId)
                {
                    R.id.home->{
                        val manager = supportFragmentManager
                        val transaction=manager.beginTransaction()
                        transaction.replace(R.id.fragment_container,HomeFragment())
                        transaction.commit()
                    }
                    R.id.upload->{
                        val manager = supportFragmentManager
                        val transaction=manager.beginTransaction()
                        transaction.replace(R.id.fragment_container,UploadFragment())
                        transaction.commit()
                    }
                    R.id.profile->{
                        val manager = supportFragmentManager
                        val transaction=manager.beginTransaction()
                        transaction.replace(R.id.fragment_container,ProfileFragment())
                        transaction.commit()
                    }

                }
                return true
            }
        })
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(applicationContext).inflate(R.menu.toolbar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
        {
            val exit_dialog = AlertDialog.Builder(applicationContext)
                .setIcon(R.drawable.exit)
                .setTitle("Exit")
                .setMessage("Are you sure,you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes",object:DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        onBackPressed()
                    }
                })
                .setNegativeButton("No",object:DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {

                    }
                }).setNeutralButton("cancel",object:DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {

                    }
                })
            exit_dialog.show()
        }
        return true
    }
}



