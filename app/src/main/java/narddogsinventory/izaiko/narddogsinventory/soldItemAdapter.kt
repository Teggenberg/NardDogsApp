package com.izaiko.narddogsinventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SoldItemAdapter(private val itemList : ArrayList<SoldListing>,
                      private val listener : OnItemClickListener
) : RecyclerView.Adapter<SoldItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoldItemAdapter.ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.inventory_item,
            parent, false)

        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    override fun onBindViewHolder(holder: SoldItemAdapter.ItemViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.itemNum.text = currentItem.detail?.itemID.toString()
        holder.itemDesc.text = currentItem.detail?.itemDesc
        holder.category.text = currentItem.detail?.category

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