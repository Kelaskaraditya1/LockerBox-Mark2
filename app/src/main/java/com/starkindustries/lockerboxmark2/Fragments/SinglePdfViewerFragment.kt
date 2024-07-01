package com.starkindustries.lockerboxmark2.Fragments
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.starkindustries.lockerboxmark2.Keys.Keys
import com.starkindustries.lockerboxmark2.Models.FileStructure
import com.starkindustries.lockerboxmark2.R
import java.io.File
import java.net.URI
import java.net.URL
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [SinglePdfViewerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SinglePdfViewerFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var user:FirebaseUser
    lateinit var pdfContainer: PDFView
    lateinit var dbRefrence: DatabaseReference
    lateinit var storageReference: StorageReference
    lateinit var childReference: StorageReference
    lateinit var pdfuri:Uri
    lateinit var dashboardBurron:AppCompatButton
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
        val view =  inflater.inflate(R.layout.fragment_single_pdf_viewer, container, false)
        auth = FirebaseAuth.getInstance()
        pdfContainer= view.findViewById(R.id.singlePdfContainer)
        dbRefrence = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference
        user=auth.currentUser!!
        dashboardBurron=view.findViewById(R.id.dashboardButton)
        dashboardBurron.setOnClickListener(){
            val manager = parentFragmentManager
            val transaction = manager.beginTransaction()
            transaction.replace(R.id.fragment_container,DocumentsListFragment())
            transaction.commit()
        }
        val pdfRef = dbRefrence.child(user.displayName.toString().trim()).child(user.uid).child(Keys.PDFS).child(arguments?.getString(Keys.NOTE_ID).toString().trim()).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val pdfFile = snapshot.getValue(FileStructure::class.java)
                childReference=storageReference.child(user.displayName+"/"+user.uid+"/"+Keys.PDFS+"/"+pdfFile?.name.toString().trim())
                childReference.downloadUrl.addOnCompleteListener(){
                    val localFile = File.createTempFile("tempFile", "pdf")
                    childReference.getFile(localFile).addOnSuccessListener {
                        // Local temp file has been created
                        pdfContainer.fromFile(localFile)
                            .spacing(18)
                            .defaultPage(0)
                            .enableAnnotationRendering(false)
                            .enableDoubletap(true)
                            .load()
                    }.addOnFailureListener {
                        // Handle any errors
                        Log.e("PDF Download Error", it.message.toString())
                    }
                }.addOnFailureListener(){
                    Toast.makeText(requireContext(), "Failed to Load Pdf", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
//        val launchPdf = registerForActivityResult(ActivityResultContracts.GetContent())
//        {
//            pdfContainer.fromUri()
//                .spacing(18)
//                .defaultPage(0)
//                .enableAnnotationRendering(false)
//                .enableDoubletap(true)
//                .load()
//            pdfContainer.useBestQuality(true)
//            pdfContainer.fitToWidth()
//        }
//        launchPdf.launch("application/pdf")
        return view
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SinglePdfViewerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SinglePdfViewerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}