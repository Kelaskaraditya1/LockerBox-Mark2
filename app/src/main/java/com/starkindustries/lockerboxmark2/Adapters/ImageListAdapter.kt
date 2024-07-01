package com.starkindustries.lockerboxmark2.Adapters
import android.app.Activity
import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.starkindustries.lockerboxmark2.Models.FileStructure
import com.starkindustries.lockerboxmark2.R
class ImageListAdapter(var context_:Context,var imagesList_:ArrayList<FileStructure>,var itemClickListner:OnItemClickListner):RecyclerView.Adapter<ImageListAdapter.ViewHolder>()
{
    interface OnItemClickListner{
     fun onRowClick(noteId:String)
     fun onRowLongClick(noteId:String)
    }
    lateinit var context:Context
    lateinit var imageList:ArrayList<FileStructure>
    init {
        this.context=context_
        this.imageList=imagesList_
    }
    inner class ViewHolder(var view: View):RecyclerView.ViewHolder(view)
    {
        lateinit var fileName:AppCompatTextView
        lateinit var imageRowCard:CardView
        init {
            fileName=view.findViewById(R.id.imageName)
            imageRowCard=view.findViewById(R.id.imageRowCard)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(context).inflate(R.layout.image_list_row,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }
    override fun getItemCount(): Int
    {
        return imageList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val fileStructure = imageList.get(position)
     holder.fileName.setText(imageList.get(position).name)
        holder.imageRowCard.setOnClickListener()
        {
            itemClickListner.onRowClick(fileStructure.noteId)
        }
        holder.imageRowCard.setOnLongClickListener(object :View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                itemClickListner.onRowLongClick(fileStructure.noteId)
                return true
            }
        })
    }
}