package com.starkindustries.lockerboxmark2.Fragments
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.starkindustries.lockerboxmark2.Adapters.ImageListAdapter
import com.starkindustries.lockerboxmark2.Keys.Keys
import com.starkindustries.lockerboxmark2.Models.FileStructure
import com.starkindustries.lockerboxmark2.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [ImagesListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImagesListFragment : Fragment() ,ImageListAdapter.OnItemClickListner
{
    lateinit var auth:FirebaseAuth
    lateinit var user:FirebaseUser
    lateinit var dbRefrence:DatabaseReference
    lateinit var recyclerView:RecyclerView
    lateinit var storageReference: StorageReference
    lateinit var childReference: StorageReference
    lateinit var emptyTextview:AppCompatTextView
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_images_list, container, false)
        var ImageAdapter:ImageListAdapter
        auth=FirebaseAuth.getInstance()
        user=auth?.currentUser!!
        dbRefrence=FirebaseDatabase.getInstance().reference
        if(user!=null)
        {
            user.let {
                val keyRef=dbRefrence.child(user.displayName.toString().trim()).child(user.uid).child(Keys.IMAGES).addValueEventListener(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot)
                    {
                     val imagesList=ArrayList<FileStructure>()
                     for(snap in snapshot.children)
                     {
                         val imageRef = snap.getValue(FileStructure::class.java)
                         imageRef.let {
                             imagesList.add(it!!)
                         }
                     }
                        ImageAdapter = ImageListAdapter(requireContext(),imagesList,this@ImagesListFragment)
                        recyclerView=view.findViewById(R.id.imagesRecyclerView)
                        recyclerView.layoutManager=LinearLayoutManager(context)
                        emptyTextview=view.findViewById(R.id.emptyTextview)
                        if (imagesList.isEmpty()) {
                            recyclerView.visibility = View.GONE
                            emptyTextview.setVisibility(View.VISIBLE)
                        } else {
                            recyclerView.visibility = View.VISIBLE
                            emptyTextview.setVisibility(View.GONE)
                        }
                        recyclerView.adapter=ImageAdapter
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
         * @return A new instance of fragment ImagesListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImagesListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onRowClick(noteId: String)
    {
        if(user!=null)
        {
            val filePath = dbRefrence.child(user.displayName.toString().trim()).child(user.uid).child(Keys.IMAGES).child(noteId)
            filePath.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fileStructure:FileStructure = snapshot.getValue(FileStructure::class.java)!!
                    storageReference=FirebaseStorage.getInstance().reference
                    childReference=storageReference.child(user.displayName+"/"+user.uid+"/"+Keys.IMAGES+"/"+fileStructure.name.toString().trim())
                    childReference.downloadUrl.addOnCompleteListener()
                    {
                        val dialog = Dialog(requireContext())
                        dialog.setContentView(R.layout.image_viewer_container)
                        val imageContainer: AppCompatImageView = dialog.findViewById(R.id.image_viewer_container)
                        Picasso.get().load(it.result).into(imageContainer)
                        dialog.show()
                    }.addOnFailureListener(){
                        Toast.makeText(context, "Failed to Load image", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    override fun onRowLongClick(noteId: String)
    {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setIcon(R.drawable.delete)
        alertDialog.setTitle("Delete")
        alertDialog.setMessage("Are you sure,you want to Delete this image?")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Yes",object:DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val ref = dbRefrence.child(user.displayName.toString().trim()).child(user.uid).child(Keys.IMAGES).child(noteId).removeValue()
                Toast.makeText(context, "Image deleted sucessfully", Toast.LENGTH_SHORT).show()
            }
        })
        alertDialog.setNegativeButton("No",object:DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

            }
        })
        alertDialog.setNeutralButton("Cancel",object:DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

            }
        })
        alertDialog.show()
    }

}
