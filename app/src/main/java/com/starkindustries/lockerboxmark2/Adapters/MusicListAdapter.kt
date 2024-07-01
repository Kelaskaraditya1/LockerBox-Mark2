package com.starkindustries.lockerboxmark2.Adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.starkindustries.lockerboxmark2.Models.FileStructure
import com.starkindustries.lockerboxmark2.R
class MusicListAdapter(var context_:Context,var musicList_:ArrayList<FileStructure>,var itemClickListner: OnItemClickListner):RecyclerView.Adapter<MusicListAdapter.ViewHolder>()
{
    interface OnItemClickListner
    {
        fun onRowClicked(noteId:String)
        fun onRowLongClicked(noteId:String)
    }
    lateinit var context:Context
    lateinit var musicList:ArrayList<FileStructure>
    init {
        this.context=context_
        this.musicList=musicList_
    }
    inner class ViewHolder(var view: View):RecyclerView.ViewHolder(view)
    {
        lateinit var musicName:AppCompatTextView
        lateinit var musicCardRow:CardView
        init {
            musicName=view.findViewById(R.id.MusicName)
            musicCardRow=view.findViewById(R.id.musicRowCard)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(context).inflate(R.layout.music_row,parent,false)
        val viewHolder =ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.musicName.setText(musicList.get(position).name)
        val musicFile = musicList.get(position)
        holder.musicCardRow.setOnClickListener()
        {
            itemClickListner.onRowClicked(musicFile.noteId)
        }
        holder.musicCardRow.setOnLongClickListener(object :View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                itemClickListner.onRowLongClicked(musicFile.noteId)
                return true
            }

        })
    }
}