package com.starkindustries.lockerboxmark2.Fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
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
import com.starkindustries.lockerboxmark2.Adapters.VideoListAdapter
import com.starkindustries.lockerboxmark2.Keys.Keys
import com.starkindustries.lockerboxmark2.Models.FileStructure
import com.starkindustries.lockerboxmark2.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VideosListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VideosListFragment : Fragment(),VideoListAdapter.OnItemClickListner {
    lateinit var videoRecyclerView:RecyclerView
    lateinit var videoEmpty:AppCompatTextView
    lateinit var auth:FirebaseAuth
    lateinit var user:FirebaseUser
    lateinit var dbRefrence:DatabaseReference
    lateinit var storageRefrence:StorageReference
    lateinit var childRefrence:StorageReference
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
        val view = inflater.inflate(R.layout.fragment_videos_list, container, false)
        videoRecyclerView=view.findViewById(R.id.videoRecyclerView)
        videoEmpty=view.findViewById(R.id.videoEmptyTextview)
        auth=FirebaseAuth.getInstance()
        user=auth.currentUser!!
        if(user!=null)
        {
            user.let {
                val videoList:ArrayList<FileStructure> = ArrayList<FileStructure>()
                dbRefrence= FirebaseDatabase.getInstance().reference
                dbRefrence.child(user.displayName.toString().trim()).child(user.uid!!).child(Keys.VIDEOS).addValueEventListener(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                     for(videoSnap in snapshot.children)
                     {
                         val video = videoSnap.getValue(FileStructure::class.java)
                         video.let {
                             videoList.add(it!!)
                         }
                     }
                        if (videoList.isEmpty()) {
                            videoRecyclerView.visibility = View.GONE
                            videoEmpty.setVisibility(View.VISIBLE)
                        } else {
                            videoRecyclerView.visibility = View.VISIBLE
                            videoEmpty.setVisibility(View.GONE)
                        }
                        videoRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                        videoRecyclerView.adapter=VideoListAdapter(requireContext(),videoList,this@VideosListFragment)
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
         * @return A new instance of fragment VideosListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VideosListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onRowClick(noteId: String) {
        auth = FirebaseAuth.getInstance()
        user=auth.currentUser!!
        if(user!=null)
        {
         dbRefrence=FirebaseDatabase.getInstance().reference
         dbRefrence.child(user.displayName.toString().trim()).child(user.uid).child(Keys.VIDEOS).child(noteId).addListenerForSingleValueEvent(object:ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 val videoFile = snapshot.getValue(FileStructure::class.java)
                 storageRefrence=FirebaseStorage.getInstance().reference
                 childRefrence=storageRefrence.child(user.displayName+"/"+user.uid+"/"+Keys.VIDEOS+"/"+videoFile?.name.toString().trim())
                 childRefrence.downloadUrl.addOnCompleteListener(){
                     if(it.isSuccessful)
                     {
                         val dialog = Dialog(requireContext())
                         dialog.setContentView(R.layout.video_palyer_container)
                         val player = ExoPlayer.Builder(requireContext()).build()
                         val exoPlayer: PlayerView =dialog.findViewById(R.id.videoPlayer)
                         exoPlayer.player=player
                         val mediaItem = MediaItem.fromUri(it.result)
                         player.addMediaItem(mediaItem)
                         player.prepare()
                         player.play()
                         dialog.show()
                     }
                 }.addOnFailureListener(){
                     Toast.makeText(requireContext(), "Failed to Load videos", Toast.LENGTH_SHORT).show()
                 }
             }
             override fun onCancelled(error: DatabaseError) {
                 TODO("Not yet implemented")
             }

         })
        }
    }
    override fun onRowLongClick(noteId: String) {
        auth=FirebaseAuth.getInstance()
        user=auth.currentUser!!
        if(user!=null)
        {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setIcon(R.drawable.delete)
            alertDialog.setTitle("Delete")
            alertDialog.setMessage("Are you sure,you want to Delete this video?")
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Yes",object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dbRefrence=FirebaseDatabase.getInstance().reference
                    dbRefrence.child(user.displayName.toString().trim()).child(user.uid).child(Keys.VIDEOS).child(noteId).removeValue().addOnCompleteListener(){
                        Toast.makeText(requireContext(), "Video deleted Successfully", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener(){
                        Toast.makeText(requireContext(), "Failed to delete video", Toast.LENGTH_SHORT).show()
                    }
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
}