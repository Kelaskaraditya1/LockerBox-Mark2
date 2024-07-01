package com.starkindustries.lockerboxmark2.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.starkindustries.lockerboxmark2.Models.FileStructure
import com.starkindustries.lockerboxmark2.R
import java.io.File

class PdfListAdapter(var context_: Context,var pdfList_:ArrayList<FileStructure>,var itemClickListner: OnItemClickListner):RecyclerView.Adapter<PdfListAdapter.ViewHolder>()
{
    interface OnItemClickListner{
        fun onRowClick(noteId:String)
        fun onRowLongClick(noteId:String)
    }
    lateinit var context:Context
    lateinit var pdfList:ArrayList<FileStructure>
    init {
        this.context=context_
        this.pdfList=pdfList_
    }
    inner class ViewHolder(var view:View):RecyclerView.ViewHolder(view)
    {
        lateinit var pdfName:AppCompatTextView
        lateinit var rowCard:CardView
        init {
            pdfName=view.findViewById(R.id.pdfName)
            rowCard=view.findViewById(R.id.pdfRowCard)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.pdf_viewer_row,parent,false)
        var viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return pdfList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.pdfName.setText(pdfList.get(position).name)
        val fileStructure:FileStructure = pdfList.get(position)
        holder.rowCard.setOnClickListener()
        {
            itemClickListner.onRowClick(fileStructure.noteId)
        }
        holder.rowCard.setOnLongClickListener(object:View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                itemClickListner.onRowLongClick(fileStructure.noteId)
                return true
            }
        })
    }
}