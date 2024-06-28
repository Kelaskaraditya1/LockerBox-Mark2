package com.starkindustries.lockerboxmark2.Fragments

import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.starkindustries.lockerboxmark2.Keys.Keys
import com.starkindustries.lockerboxmark2.R
import kotlinx.coroutines.flow.combine

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UpdatePasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdatePasswordFragment : Fragment() {
    lateinit var auth:FirebaseAuth
    lateinit var password:TextInputEditText
    lateinit var confirmPassword:TextInputEditText
    lateinit var currentPassword:TextInputEditText
    lateinit var resetPasswordButton: AppCompatButton
     var passed1:Boolean?=false
    var passed2:Boolean?=false
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
        var view = inflater.inflate(R.layout.fragment_update_password, container, false)
        password=view.findViewById(R.id.resetPassword)
        confirmPassword=view.findViewById(R.id.resetConfirmPassword)
        resetPasswordButton=view.findViewById(R.id.changePasswordButton)
        auth=FirebaseAuth.getInstance()
        password.setOnTouchListener(object: View.OnTouchListener{
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                if(view!=null)
                {
                    if(event?.action== MotionEvent.ACTION_UP)
                    {
                        var selection= password.selectionEnd
                        if(event?.rawX!! >=(password.right-password.compoundDrawables[Keys.RIGHT].bounds.width()))
                        {
                            if(passed1!!)
                            {
                                password.transformationMethod= PasswordTransformationMethod.getInstance()
                                password.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.visibility_off,0)
                                passed1=false
                            }
                            else{
                                password.transformationMethod=
                                    HideReturnsTransformationMethod.getInstance()
                                password.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.visibility_on,0)
                                passed1=true
                            }
                            password.setSelection(selection)
                            return true
                        }
                    }
                }
                return false
            }
        })
        confirmPassword.setOnTouchListener(object: View.OnTouchListener{
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                if(view!=null)
                {
                    if(event?.action==MotionEvent.ACTION_UP)
                    {
                        var selection= confirmPassword.selectionEnd
                        if(event?.rawX!! >=(confirmPassword.right-confirmPassword.compoundDrawables[Keys.RIGHT].bounds.width()))
                        {
                            if(passed2!!)
                            {
                                confirmPassword.transformationMethod=PasswordTransformationMethod.getInstance()
                                confirmPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.visibility_off,0)
                                passed2=false
                            }
                            else{
                                confirmPassword.transformationMethod=HideReturnsTransformationMethod.getInstance()
                                confirmPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.visibility_on,0)
                                passed2=true
                            }
                            confirmPassword.setSelection(selection)
                            return true
                        }
                    }
                }
                return false
            }
        })
        resetPasswordButton.setOnClickListener()
        {
            if(TextUtils.isEmpty(password.text.toString().trim()))
            {
                password.setError("Field is Empty")
                return@setOnClickListener
            }
            else if(TextUtils.isEmpty(confirmPassword.text.toString().trim()))
            {
                confirmPassword.setError("Field is Empty")
                return@setOnClickListener
            }
            else if(!password.text.toString().trim().equals(confirmPassword.text.toString().trim()))
            {
                password.setError("Password and confirm Password should be same")
                confirmPassword.setError("Password and confirm Password should be same")
                return@setOnClickListener
            }
            else if(password.text.toString().trim().length<8)
            {
                password.setError("Password should be atleast of 8 Charecters")
                return@setOnClickListener
            }
            else if(confirmPassword.text.toString().trim().length<8)
            {
                confirmPassword.setError("Password should be atleast of 8 Charecters")
                return@setOnClickListener
            }
            val user = auth.currentUser
            user?.updatePassword(password.text.toString().trim())?.addOnCompleteListener(){
                if(it.isSuccessful)
                {
                    Toast.makeText(context, "Password Updated Sucessfully", Toast.LENGTH_SHORT).show()
                    val manager = parentFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.replace(R.id.fragment_container,ProfileFragment())
                    transaction.commit()
                }
            }?.addOnFailureListener(){
                Toast.makeText(context, " "+it.message.toString().trim(), Toast.LENGTH_SHORT).show()
            }
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
         * @return A new instance of fragment UpdatePasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UpdatePasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}