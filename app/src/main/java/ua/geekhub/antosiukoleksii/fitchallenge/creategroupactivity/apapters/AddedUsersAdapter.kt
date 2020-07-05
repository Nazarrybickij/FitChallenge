package ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.apapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_add_user_rv.view.*
import ua.geekhub.antosiukoleksii.fitchallenge.entity.Contact
import ua.geekhub.antosiukoleksii.fitchallenge.R

class AddedUsersAdapter() :
    RecyclerView.Adapter<AddedUsersAdapter.ViewHolder>() {
    var values = mutableListOf<Contact>()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(values[position])
    }

    override fun getItemCount(): Int = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_user_rv, parent, false)

        return ViewHolder(itemView)
    }


   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener{
        init {
            itemView.setOnLongClickListener(this)
        }
        fun onBind(contact: Contact){
            itemView.name_user.text = contact.name
            itemView.phone_user.text = contact.phone
        }

        override fun onLongClick(v: View?): Boolean {
            values.removeAt(adapterPosition)
            notifyItemChanged(adapterPosition)
            notifyItemRangeRemoved(adapterPosition, 1)
           return true
        }

    }
}