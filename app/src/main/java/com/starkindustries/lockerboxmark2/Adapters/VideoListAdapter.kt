package com.starkindustries.lockerboxmark2.Adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.starkindustries.lockerboxmark2.Models.FileStructure
import com.starkindustries.lockerboxmark2.R
class VideoListAdapter(var context_:Context,var videoList_:ArrayList<FileStructure>,var itemClickListner:OnItemClickListner):RecyclerView.Adapter<VideoListAdapter.Viewholder>()
{
    interface OnItemClickListner{
        fun onRowClick(noteId:String)
        fun onRowLongClick(noteId:String)
    }
    lateinit var context:Context
    lateinit var videoList:ArrayList<FileStructure>
    init{
        this.context=context_
        this.videoList=videoList_
    }
    inner class Viewholder(var view:View):RecyclerView.ViewHolder(view){
        lateinit var videoName:AppCompatTextView
        lateinit var videoRowCard:CardView
        init {
            videoName=view.findViewById(R.id.VideoName)
            videoRowCard=view.findViewById(R.id.videoRowCard)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_row,parent,false)
        val viewHolder = Viewholder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.videoName.setText(videoList.get(position).name.toString())
        val fileStructure = videoList.get(position)
        holder.videoRowCard.setOnClickListener(){
            itemClickListner.onRowClick(fileStructure.noteId)
        }
        holder.videoRowCard.setOnLongClickListener(object :View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                itemClickListner.onRowLongClick(fileStructure.noteId)
                return true
            }
        })
    }
}