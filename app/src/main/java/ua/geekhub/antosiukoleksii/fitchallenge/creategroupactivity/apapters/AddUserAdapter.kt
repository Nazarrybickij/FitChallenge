package ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.apapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_add_user_rv.view.*
import ua.geekhub.antosiukoleksii.fitchallenge.entity.Contact
import ua.geekhub.antosiukoleksii.fitchallenge.R


class AddUserAdapter(
    private val values: ArrayList<Contact>,
    private val callback: AdapterCallback?
) :
    RecyclerView.Adapter<AddUserAdapter.ViewHolder>() {

    interface AdapterCallback {
        fun onUserClick(contact: Contact)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(values[position])
    }

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_user_rv, parent, false)

        return ViewHolder(itemView)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        fun onBind(contact: Contact) {
            itemView.name_user.text = contact.name
            itemView.phone_user.text = contact.phone
        }

        override fun onClick(v: View?) {
            Toast.makeText(itemView.context,values[adapterPosition].name,Toast.LENGTH_SHORT).show()
            callback?.onUserClick(
                values[adapterPosition]
            )


        }

    }
}