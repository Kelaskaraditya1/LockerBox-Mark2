package com.starkindustries.lockerboxmark2.Fragments
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.get
import androidx.fragment.app.FragmentTransaction
import com.github.barteksc.pdfviewer.PDFView
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
 * Use the [PdfContainerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PdfContainerFragment : Fragment() {
    lateinit var auth:FirebaseAuth
    lateinit var pdfContainer:PDFView
    lateinit var uploadSecButton:AppCompatButton
    lateinit var dbRefrence:DatabaseReference
    lateinit var storageReference: StorageReference
    lateinit var childReference: StorageReference
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
        val view = inflater.inflate(R.layout.fragment_pdf_container, container, false)
        pdfContainer=view.findViewById(R.id.pdfContainer)
        uploadSecButton=view.findViewById(R.id.uploadSectionButton)
        auth=FirebaseAuth.getInstance()
        val launchpdf = registerForActivityResult(ActivityResultContracts.GetContent())
        {
                uri->
            uri.let {
                pdfContainer.setTag(it)
                pdfContainer.fromUri(it)
                    .spacing(12)
                    .defaultPage(0)
                    .enableAnnotationRendering(false)
                    .enableDoubletap(true)
                    .load()
                pdfContainer.fitToWidth()
                pdfContainer.useBestQuality(true)
            }
            uploadSecButton.setOnClickListener()
            {
                val manager = parentFragmentManager
                val transaction:FragmentTransaction = manager.beginTransaction()
                val bundle:Bundle = Bundle()
                bundle.apply {
                    putString(Keys.FILE_URI,pdfContainer.getTag().toString().trim())
                    putString(Keys.NAME,getFileName(pdfContainer.getTag() as Uri))
                    Log.d("FileName","Name: "+getFileName(pdfContainer.getTag() as Uri)+" "+"Uri: "+pdfContainer.getTag().toString().trim())
                    dbRefrence=FirebaseDatabase.getInstance().reference
                    val key = dbRefrence.child(auth.currentUser?.displayName.toString().trim()).child(auth.currentUser?.uid!!).child(Keys.PDFS).push().key
                    if(key!=null)
                    {
                        val fileStructure=FileStructure(getFileName(uri!!)!!,pdfContainer.getTag().toString().trim(),Keys.PDFS,key.toString().trim())
                        dbRefrence.child(auth.currentUser?.displayName.toString().trim()).child(auth.currentUser?.uid!!).child(Keys.PDFS).child(key).setValue(fileStructure).addOnCompleteListener()
                        {
                            storageReference= FirebaseStorage.getInstance().reference
                            childReference=storageReference.child(auth.currentUser?.displayName.toString().trim()+"/"+auth.currentUser?.uid!!+"/"+Keys.PDFS+"/"+getFileName(uri!!))
                            childReference.putFile(uri!!).addOnCompleteListener()
                            {
                                if(it.isSuccessful)
                                {
                                    Toast.makeText(context, "Document Uploaded to Db and CloudStorage successfully", Toast.LENGTH_SHORT).show()
                                    transaction.replace(R.id.fragment_container,UploadFragment())
                                    transaction.commit()
                                }
                            }.addOnFailureListener(){
                                Toast.makeText(context, "Failed to upload Document in CloudStore", Toast.LENGTH_SHORT).show()
                            }

                        }.addOnFailureListener(){
                            Toast.makeText(context, "Failed to upload filestructure in Realtime Db", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        launchpdf.launch("application/pdf")
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PdfContainerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PdfContainerFragment().apply {
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
}