package com.starkindustries.lockerboxmark2.Fragments
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.starkindustries.lockerboxmark2.Activiity.LoginActivity
import com.starkindustries.lockerboxmark2.Keys.Keys
import com.starkindustries.lockerboxmark2.R
import de.hdodenhof.circleimageview.CircleImageView
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    lateinit var auth :FirebaseAuth
    lateinit var profileImage:CircleImageView
    lateinit var profileUsername:AppCompatTextView
    lateinit var logoutButton:LinearLayoutCompat
    internal lateinit var googleSignInClient: GoogleSignInClient
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    lateinit var editProfile:LinearLayoutCompat
    lateinit var updatePassword:LinearLayoutCompat
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        auth=FirebaseAuth.getInstance()

        profileUsername=view.findViewById(R.id.profileUserName)
        profileImage=view.findViewById(R.id.profileImage)
        logoutButton=view.findViewById(R.id.logout)
        updatePassword=view.findViewById(R.id.updatePassword)
        editProfile=view.findViewById(R.id.editProfile)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_Id))
            .requestEmail()
            .build()
        googleSignInClient= GoogleSignIn.getClient(requireContext(),gso)
        logoutButton.setOnClickListener()
        {googleSignInClient.signOut().addOnCompleteListener()
        {
            if(it.isSuccessful)
            {
                val intent = Intent(context, LoginActivity::class.java)
                sharedPreferences= context?.getSharedPreferences(Keys.SHARED_PREFRENCE_NAME, Context.MODE_PRIVATE)!!
                editor =sharedPreferences.edit()
                editor.putBoolean(Keys.LOGIN_STATUS,false)
                editor.apply()
                startActivity(intent)
                activity?.finish()
            }
        }.addOnFailureListener()
        {
            Toast.makeText(context, "Failed to Signout", Toast.LENGTH_SHORT).show()
        }
        }
        var user = auth.currentUser
        if(user!=null)
        {
           user.let {
               for(profile in it.providerData)
               {
                   val account = GoogleSignIn.getLastSignedInAccount(requireContext())
                   profileUsername.setText(account?.displayName.toString().trim())
                   Picasso.get().load(account?.photoUrl).into(profileImage)
               }
           }
        }
        updatePassword.setOnClickListener()
        {
            val manager = parentFragmentManager
            val transaction=manager.beginTransaction()
            transaction.replace(R.id.fragment_container,UpdatePasswordFragment())
            transaction.commit()
        }
        editProfile.setOnClickListener()
        {
            val manager = parentFragmentManager
            val transaction = manager.beginTransaction()
            transaction.replace(R.id.fragment_container,UpdateProfileFragment())
            transaction.commit()
        }
        return view
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}