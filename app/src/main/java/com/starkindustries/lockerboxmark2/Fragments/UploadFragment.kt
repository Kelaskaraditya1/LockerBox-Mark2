package com.starkindustries.lockerboxmark2.Fragments
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.contentValuesOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.starkindustries.lockerboxmark2.Keys.Keys
import com.starkindustries.lockerboxmark2.Models.FileStructure
import com.starkindustries.lockerboxmark2.R
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UploadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadFragment : Fragment() {
    lateinit var auth:FirebaseAuth
    lateinit var dbRefrence:DatabaseReference
    lateinit var imagesCard:CardView
    lateinit var pdfCard:CardView
    lateinit var musicsCard:CardView
    lateinit var videosCard:CardView
    lateinit var viewButton: AppCompatButton
    lateinit var uploadFilesButton:AppCompatButton
    lateinit var storageRefrence:StorageReference
    lateinit var childRefrence:StorageReference
    lateinit var fileName:AppCompatTextView
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
        var view = inflater.inflate(R.layout.fragment_upload, container, false)
        auth=FirebaseAuth.getInstance()
        imagesCard=view.findViewById(R.id.imagesCard)
        musicsCard=view.findViewById(R.id.musicCard)
        videosCard=view.findViewById(R.id.videosCard)
        pdfCard=view.findViewById(R.id.docummentsCard)
        viewButton=view.findViewById(R.id.viewButton)
        uploadFilesButton=view.findViewById(R.id.uploadFileButton)
        fileName=view.findViewById(R.id.filename)
        imagesCard.setOnClickListener()
        {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, Keys.GALLERY_REQ_CODE)
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
         * @return A new instance of fragment UploadFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UploadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context?.contentResolver?.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && it.moveToFirst()) {
                    result = it.getString(nameIndex)
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==Keys.GALLERY_REQ_CODE)
        {
            fileName.setText(getFileName(data?.data!!))
            viewButton.setOnClickListener()
            {

                val imageViewerDialog = Dialog(requireContext())
                imageViewerDialog.setContentView(R.layout.image_viewer_container)
                var imageContainer:AppCompatImageView = imageViewerDialog.findViewById(R.id.image_viewer_container)
                imageContainer.setImageURI(data?.data!!)
                imageViewerDialog.show()
            }
            uploadFilesButton.setOnClickListener()
            {
                dbRefrence=FirebaseDatabase.getInstance().reference
                val key = dbRefrence.child(auth.currentUser?.displayName.toString().trim()).child(auth.currentUser?.uid!!).child(Keys.IMAGES).push().key
                if(key!=null)
                {
                    val fileStucture = FileStructure(getFileName(data?.data!!)!!,Keys.IMAGES,data?.data!!.toString().trim())
                    dbRefrence.child(auth.currentUser?.displayName.toString().trim()).child(auth.currentUser?.uid!!).child(Keys.IMAGES).child(key).setValue(fileStucture).addOnCompleteListener()
                    {if(it.isSuccessful)
                    {
                        storageRefrence=FirebaseStorage.getInstance().reference
                        childRefrence=storageRefrence.child(auth.currentUser?.displayName.toString().trim()+"/"+auth.currentUser?.uid!!+"/"+Keys.IMAGES+"/"+getFileName(data?.data!!))
                        childRefrence.putFile(data?.data!!).addOnCompleteListener()
                        {
                            if(it.isSuccessful)
                                Toast.makeText(context, "Image Uploaded to Db and CloudStorage successfully", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener(){
                            Toast.makeText(context, "Failed to upload image in CloudStore", Toast.LENGTH_SHORT).show()
                        }
                    }
                    }.addOnFailureListener {
                        Toast.makeText(context, "Failed to upload image in Real time DB", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}