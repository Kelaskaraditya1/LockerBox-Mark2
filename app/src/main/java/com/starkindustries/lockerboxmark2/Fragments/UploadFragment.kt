package com.starkindustries.lockerboxmark2.Fragments
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.contentValuesOf
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.starkindustries.lockerboxmark2.Keys.Keys
import com.starkindustries.lockerboxmark2.Models.FileStructure
import com.starkindustries.lockerboxmark2.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    lateinit var mediaplayer:MediaPlayer
    lateinit var manager:AudioManager
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
        pdfCard.setOnClickListener()
        {
                val manager = parentFragmentManager
                val transaction:FragmentTransaction = manager.beginTransaction()
                transaction.replace(R.id.fragment_container,PdfContainerFragment())
                transaction.commit()
        }
        musicsCard.setOnClickListener()
        {
            val inetnt = Intent(Intent.ACTION_GET_CONTENT)
            inetnt.setType("audio/*")
            inetnt.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(inetnt,Keys.MUSIC_OPEN_REQ_CODE)

        }
        videosCard.setOnClickListener()
        {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("video/*")
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent,Keys.VIDEO_OPEN_REQ_CODE)
        }
            fileName.setText("No file selected yet.")
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
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dbRefrence=FirebaseDatabase.getInstance().reference
        val user = auth.currentUser
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
                    val fileStucture = FileStructure(getFileName(data?.data!!)!!,Keys.IMAGES,data?.data!!.toString().trim(),key?:"")
                    dbRefrence.child(auth.currentUser?.displayName.toString().trim()).child(auth.currentUser?.uid!!).child(Keys.IMAGES).child(key).setValue(fileStucture).addOnCompleteListener()
                    {if(it.isSuccessful)
                    {
                        storageRefrence=FirebaseStorage.getInstance().reference
                        childRefrence=storageRefrence.child(auth.currentUser?.displayName.toString().trim()+"/"+auth.currentUser?.uid!!+"/"+Keys.IMAGES+"/"+getFileName(data?.data!!))
                        childRefrence.putFile(data?.data!!).addOnCompleteListener()
                        {
                            if(it.isSuccessful)
                            {
                                Toast.makeText(context, "Image Uploaded to Db and CloudStorage successfully", Toast.LENGTH_SHORT).show()
                                fileName.setText("No file selected yet.")
                            }

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
        else if(requestCode==Keys.MUSIC_OPEN_REQ_CODE)
        {
            fileName.setText(getFileName(data?.data!!))
         viewButton.setOnClickListener()
         {
             var pause:AppCompatButton
             var play:AppCompatButton
             var stop:AppCompatButton
             var volumeSeekbar:SeekBar
             var musicSeekbar:SeekBar
             val dialog = Dialog(requireContext())
             dialog.setContentView(R.layout.music_player_container)
             pause=dialog.findViewById(R.id.pause)
             play=dialog.findViewById(R.id.play)
             stop=dialog.findViewById(R.id.stop)
             volumeSeekbar=dialog.findViewById(R.id.volumeSeekbar)
             musicSeekbar=dialog.findViewById(R.id.musicSeekbar)
             mediaplayer=MediaPlayer.create(requireContext(),data?.data)
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
             manager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
             var maxVol = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
             var curVol = manager.getStreamVolume(AudioManager.STREAM_MUSIC)
             volumeSeekbar.max=maxVol
             volumeSeekbar.setProgress(curVol)
             volumeSeekbar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
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
             musicSeekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
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
            uploadFilesButton.setOnClickListener()
            {
                val key = dbRefrence.child(user?.displayName.toString().trim()).child(user?.uid!!).child(Keys.MUSICS).push().key
                if(key!=null)
                {
                    val fileStructure = FileStructure(getFileName(data?.data!!)!!,Keys.MUSICS,data?.data!!.toString().trim(),key.toString().trim())
                    dbRefrence.child(user?.displayName.toString().trim()).child(user?.uid!!).child(Keys.MUSICS).child(key).setValue(fileStructure).addOnCompleteListener()
                    {
                        if(it.isSuccessful)
                        {
                            storageRefrence=FirebaseStorage.getInstance().reference
                            childRefrence=storageRefrence.child(user.displayName.toString().trim()+"/"+user.uid+"/"+Keys.MUSICS+"/"+getFileName(data?.data!!))
                            childRefrence.putFile(data?.data!!).addOnCompleteListener(){
                                if(it.isSuccessful)
                                {
                                    Toast.makeText(context, "Uploaded successfully!!", Toast.LENGTH_SHORT).show()
                                    fileName.setText("No file selected yet.")
                                }
                            }.addOnFailureListener(){
                                Toast.makeText(context, "Failed to upload in Cloud Storage", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnFailureListener(){
                        Toast.makeText(requireContext(), "Failed to upload in Real time Db", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else if(requestCode==Keys.VIDEO_OPEN_REQ_CODE)
        {
            fileName.setText(getFileName(data?.data!!))
            viewButton.setOnClickListener()
            {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.video_palyer_container)
                val player = ExoPlayer.Builder(requireContext()).build()
                val exoPlayer:PlayerView=dialog.findViewById(R.id.videoPlayer)
                exoPlayer.player=player
                val mediaItem = MediaItem.fromUri(data.data!!)
                player.addMediaItem(mediaItem)
                player.prepare()
                player.play()
                dialog.show()
            }
            uploadFilesButton.setOnClickListener()
            {
                val key = dbRefrence.child(user?.displayName!!).child(user?.uid!!).child(Keys.VIDEOS).push().key
                if(key!=null)
                {
                    val fileStructure = FileStructure(getFileName(data?.data!!)!!,Keys.VIDEOS,data?.data.toString().trim(),key.toString().trim())
                    dbRefrence.child(user?.displayName!!).child(user?.uid!!).child(Keys.VIDEOS).child(key).setValue(fileStructure).addOnCompleteListener(){
                        if(it.isSuccessful)
                        {
                            storageRefrence=FirebaseStorage.getInstance().reference
                            childRefrence=storageRefrence.child(user.displayName.toString().trim()+"/"+user.uid+"/"+Keys.VIDEOS+"/"+getFileName(data?.data!!))
                            childRefrence.putFile(data?.data!!).addOnCompleteListener(){
                                if(it.isSuccessful)
                                {
                                    Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                                    fileName.setText("No file selected yet.")
                                }
                            }.addOnFailureListener(){
                                Toast.makeText(context, "Failed to upload video in Cloud Storage", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnFailureListener(){
                        Toast.makeText(context, "Failed to uploaded in Real time database", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}