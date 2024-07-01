package com.starkindustries.lockerboxmark2.Fragments
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.starkindustries.lockerboxmark2.Adapters.MusicListAdapter
import com.starkindustries.lockerboxmark2.Keys.Keys
import com.starkindustries.lockerboxmark2.Models.FileStructure
import com.starkindustries.lockerboxmark2.R
import java.security.Key
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [MusicListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MusicListFragment : Fragment() ,MusicListAdapter.OnItemClickListner{
    lateinit var musicListRecyclerView:RecyclerView
    lateinit var musicEmptyTextView:AppCompatTextView
    lateinit var auth:FirebaseAuth
    lateinit var user:FirebaseUser
    lateinit var dbRefrence:DatabaseReference
    lateinit var storageReference: StorageReference
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
        val view =  inflater.inflate(R.layout.fragment_music_list, container, false)
        auth=FirebaseAuth.getInstance()
        musicEmptyTextView=view.findViewById(R.id.musicEmptyTextview)
        user=auth.currentUser!!
        if(user!=null)
        {
            user.let {
                dbRefrence=FirebaseDatabase.getInstance().reference
                dbRefrence.child(user.displayName.toString().trim()).child(user.uid).child(Keys.MUSICS).addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                     val list:ArrayList<FileStructure> = ArrayList<FileStructure>()
                     for(musicsnap in snapshot.children)
                     {
                         val music = musicsnap.getValue(FileStructure::class.java)
                         music.let { list.add(it!!)}
                     }
                        musicListRecyclerView=view.findViewById(R.id.musicRecyclerView)
                        musicListRecyclerView.layoutManager= LinearLayoutManager(context)
                        musicEmptyTextView=view.findViewById(R.id.musicEmptyTextview)
                        if (list.isEmpty()) {
                            musicListRecyclerView.visibility = View.GONE
                            musicEmptyTextView.setVisibility(View.VISIBLE)
                        } else {
                            musicListRecyclerView.visibility = View.VISIBLE
                            musicEmptyTextView.setVisibility(View.GONE)
                        }
                        musicListRecyclerView.adapter=MusicListAdapter(requireContext(),list,this@MusicListFragment)
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
         * @return A new instance of fragment MusicListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MusicListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onRowClicked(noteId: String) {
        dbRefrence = FirebaseDatabase.getInstance().reference
        user=FirebaseAuth.getInstance().currentUser!!
        if(user!=null)
        {
            user.let {
                dbRefrence.child(user.displayName.toString().trim()).child(user.uid).child(Keys.MUSICS).child(noteId).addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val fileStructure = snapshot.getValue(FileStructure::class.java)
                        storageReference=FirebaseStorage.getInstance().reference
                        childRefrence=storageReference.child(user.displayName+"/"+user.uid+"/"+Keys.MUSICS+"/"+fileStructure?.name)
                        childRefrence.downloadUrl.addOnCompleteListener(){
                            if(it.isSuccessful)
                            {
                                var pause: AppCompatButton
                                var play: AppCompatButton
                                var stop: AppCompatButton
                                var volumeSeekbar: SeekBar
                                var musicSeekbar: SeekBar
                                val dialog = Dialog(requireContext())
                                dialog.setContentView(R.layout.music_player_container)
                                pause=dialog.findViewById(R.id.pause)
                                play=dialog.findViewById(R.id.play)
                                stop=dialog.findViewById(R.id.stop)
                                volumeSeekbar=dialog.findViewById(R.id.volumeSeekbar)
                                musicSeekbar=dialog.findViewById(R.id.musicSeekbar)
                                val mediaplayer= MediaPlayer.create(requireContext(),it.result)
                                play.setOnClickListener()
                                {
                                    if((mediaplayer!=null)&&!(mediaplayer.isPlaying))
                                        mediaplayer.start()
                                }
                                pause.setOnClickListener()
                                {
                                    if((mediaplayer!=null)&&(mediaplayer.isPlaying))
                                        mediaplayer.pause()
                                }
                                stop.setOnClickListener()
                                {
                                    if((mediaplayer!=null)&&(mediaplayer.isPlaying))
                                        mediaplayer.stop()
                                }
                                val manager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                                var maxVol = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                var curVol = manager.getStreamVolume(AudioManager.STREAM_MUSIC)
                                volumeSeekbar.max=maxVol
                                volumeSeekbar.setProgress(curVol)
                                volumeSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                        manager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0)
                                    }
                                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                                        seekBar?.progress.let { manager.setStreamVolume(AudioManager.STREAM_MUSIC,it!!,0) }
                                    }

                                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                                        seekBar?.progress.let { manager.setStreamVolume(AudioManager.STREAM_MUSIC,it!!,0) }
                                    }
                                })
                                musicSeekbar.max=mediaplayer.duration
                                val handler = Handler()
                                handler.postDelayed(object:Runnable{
                                    override fun run() {
                                        musicSeekbar.setProgress(mediaplayer.currentPosition)
                                        handler.postDelayed(this,1000)
                                    }
                                },0)
                                musicSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                        if(fromUser)
                                            musicSeekbar.setProgress(progress)
                                    }

                                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                                        seekBar?.progress.let { mediaplayer.seekTo(it!!) }
                                    }

                                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                                        seekBar?.progress.let {mediaplayer.seekTo(it!!) }
                                    }

                                })
                                dialog.show()
                            }
                        }.addOnFailureListener(){
                            Toast.makeText(requireContext(), "Failed to load audio file", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }
    override fun onRowLongClicked(noteId: String) {
        auth=FirebaseAuth.getInstance()
        user=auth.currentUser!!
        if(user!=null)
        {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setIcon(R.drawable.delete)
            alertDialog.setTitle("Delete")
            alertDialog.setMessage("Are you sure,you want to Delete this audio?")
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Yes",object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dbRefrence=FirebaseDatabase.getInstance().reference
                    dbRefrence.child(user.displayName.toString().trim()).child(user.uid).child(Keys.MUSICS).child(noteId).removeValue().addOnCompleteListener(){
                        Toast.makeText(requireContext(), "Audio deleted Successfully", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener(){
                        Toast.makeText(requireContext(), "Failed to delete Audio", Toast.LENGTH_SHORT).show()
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