package com.starkindustries.lockerboxmark2.Fragments
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.starkindustries.lockerboxmark2.Adapters.PdfListAdapter
import com.starkindustries.lockerboxmark2.Keys.Keys
import com.starkindustries.lockerboxmark2.Models.FileStructure
import com.starkindustries.lockerboxmark2.R
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [DocumentsListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DocumentsListFragment : Fragment() ,PdfListAdapter.OnItemClickListner{
    lateinit var auth:FirebaseAuth
    lateinit var user:FirebaseUser
    lateinit var recyclerView:RecyclerView
    lateinit var pdfEmptyTextView:AppCompatTextView
    lateinit var dbRefrence:DatabaseReference
    lateinit var pdfList:ArrayList<FileStructure>
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
        val view = inflater.inflate(R.layout.fragment_documents_list, container, false)
        auth = FirebaseAuth.getInstance()
        dbRefrence=FirebaseDatabase.getInstance().reference
        recyclerView=view.findViewById(R.id.pdfRecyclerView)
        pdfEmptyTextView=view.findViewById(R.id.pdfEmptyTextview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        user = auth.currentUser!!
        pdfList= ArrayList<FileStructure>()
        if(user!=null)
        {
           user.let {
               val pdfRef = dbRefrence.child(user.displayName.toString().trim()).child(user.uid).child(Keys.PDFS).addValueEventListener(object:ValueEventListener{
                   override fun onDataChange(snapshot: DataSnapshot) {
                       for(pdfSnap in snapshot.children)
                       {
                           val pdfs = pdfSnap.getValue(FileStructure::class.java)
                           pdfs.let {
                               pdfList.add(it!!)
                           }
                       }
                       if (pdfList.isEmpty()) {
                           recyclerView.visibility = View.GONE
                           pdfEmptyTextView.setVisibility(View.VISIBLE)
                       } else {
                           recyclerView.visibility = View.VISIBLE
                           pdfEmptyTextView.setVisibility(View.GONE)
                       }
                       recyclerView.adapter=PdfListAdapter(requireContext(),pdfList,this@DocumentsListFragment)
                   }
                   override fun onCancelled(error: DatabaseError) {
                       TODO("Not yet implemented")
                   }

               })
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
         * @return A new instance of fragment DocumentsListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DocumentsListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onRowClick(noteId: String) {
     val manager = parentFragmentManager
     val transaction:FragmentTransaction = manager.beginTransaction()
        val bundle:Bundle = Bundle()
        bundle.apply {
            putString(Keys.NOTE_ID,noteId)
        }
     transaction.replace(R.id.fragment_container,SinglePdfViewerFragment()::class.java,bundle)
        transaction.commit()
    }
    override fun onRowLongClick(noteId: String) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setIcon(R.drawable.delete)
        alertDialog.setTitle("Delete")
        alertDialog.setMessage("Are you sure,you want to Delete this document?")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Yes",object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val ref = dbRefrence.child(user.displayName.toString().trim()).child(user.uid).child(Keys.PDFS).child(noteId).removeValue()
                Toast.makeText(context, "Item deleted sucessfully", Toast.LENGTH_SHORT).show()
            }
        })
        alertDialog.setNegativeButton("No",object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

            }
        })
        alertDialog.setNeutralButton("Cancel",object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

            }
        })
        alertDialog.show()
    }
}