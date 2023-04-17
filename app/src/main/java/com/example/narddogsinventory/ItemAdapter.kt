package com.example.narddogsinventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private val itemList : ArrayList<ActiveListing>,
        private val listener : OnItemClickListener
        ) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.inventory_item,
            parent, false)

        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
         val currentItem = itemList[position]

        holder.itemNum.text = currentItem.itemID.toString()
        holder.itemDesc.text = currentItem.itemDesc
        holder.category.text = currentItem.category

    }

    inner class ItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val itemNum : TextView = itemView.findViewById(R.id.itemID)
        val itemDesc : TextView = itemView.findViewById(R.id.itemDesc)
        val category : TextView = itemView.findViewById(R.id.category)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position : Int = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }

        }

    }

    interface OnItemClickListener {

        fun onItemClick(position: Int)
    }
}