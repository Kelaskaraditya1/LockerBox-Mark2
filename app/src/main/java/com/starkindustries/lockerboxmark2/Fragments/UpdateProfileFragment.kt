package com.starkindustries.lockerboxmark2.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.starkindustries.lockerboxmark2.Keys.Keys
import com.starkindustries.lockerboxmark2.R
import de.hdodenhof.circleimageview.CircleImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateProfileFragment : Fragment() {
    lateinit var auth:FirebaseAuth
    lateinit var updateProfileImageView:CircleImageView
    lateinit var updateProfileName:TextInputEditText
    lateinit var updateProfileUsername:TextInputEditText
    lateinit var updateProfilePhoneNo:TextInputEditText
    lateinit var updateButton:AppCompatButton
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
        var view = inflater.inflate(R.layout.fragment_update_profile, container, false)
        auth = FirebaseAuth.getInstance()
        updateProfileImageView=view.findViewById(R.id.updateProfileImageview)
        updateProfileName=view.findViewById(R.id.updateProfileName)
        updateProfileUsername=view.findViewById(R.id.updateProfileUsername)
        updateProfilePhoneNo=view.findViewById(R.id.updateProfilePhoneNo)
        updateButton=view.findViewById(R.id.updateButton)
        updateButton.setOnClickListener()
        {
            if(TextUtils.isEmpty(updateProfileName.text.toString().trim()))
            {
                updateProfileName.setError("Field is Empty")
                return@setOnClickListener
            }
            else if(TextUtils.isEmpty(updateProfileUsername.text.toString().trim()))
            {
                updateProfileUsername.setError("Field is Empty")
                return@setOnClickListener
            }
            else if(TextUtils.isEmpty(updateProfilePhoneNo.text.toString().trim()))
            {
                updateProfilePhoneNo.setError("Field is Empty")
                return@setOnClickListener
            }
            else if(updateProfilePhoneNo.text.toString().trim().length<10)
            {
                updateProfilePhoneNo.setError("Phone No should atleast of 10 digits")
                return@setOnClickListener
            }
            val user = auth.currentUser
            val update = UserProfileChangeRequest.Builder().setDisplayName(updateProfileUsername.text.toString().trim())
                .setPhotoUri(updateProfileImageView.getTag() as Uri).build()
            user?.updateProfile(update)?.addOnCompleteListener(){
                Toast.makeText(context, "Profile updated successfully!!", Toast.LENGTH_SHORT).show()
                val manager = parentFragmentManager
                val transaction=manager.beginTransaction()
                transaction.replace(R.id.fragment_container,ProfileFragment())
                transaction.commit()
            }?.addOnFailureListener {
                Toast.makeText(context, "Failed to update profile!!", Toast.LENGTH_SHORT).show()
            }
        }
        updateProfileImageView.setOnClickListener()
        {
            val gallery = Intent(Intent.ACTION_PICK)
            gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(gallery, Keys.GALLERY_REQ_CODE)
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
         * @return A new instance of fragment UpdateProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UpdateProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==Keys.GALLERY_REQ_CODE)
        {
            updateProfileImageView.setImageURI(data?.data!!)
            updateProfileImageView.setTag(data?.data!!)
        }
    }
}