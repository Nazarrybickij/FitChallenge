package ua.geekhub.antosiukoleksii.fitchallenge.mainactivity.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_main_groups.view.*
import ua.geekhub.antosiukoleksii.fitchallenge.entity.MyGroup
import ua.geekhub.antosiukoleksii.fitchallenge.R
import android.text.format.DateFormat
import kotlin.collections.ArrayList

class GroupsAdapter(
    private val values: ArrayList<MyGroup>,
    private val callback: GroupsAdapter.AdapterCallback?
) :
    RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    interface AdapterCallback {
        fun onGroupClick(id: String)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(values[position])

    }

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_main_groups, parent, false)

        return ViewHolder(
            itemView
        )
    }


 inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        fun onBind(myGroup: MyGroup) {
            var startFormat = DateFormat.format("d MMM HH:mm", myGroup.startDate)
            var endFormat = DateFormat.format("d MMM HH:mm", myGroup.endDate)
            itemView.date.text = "Start: $startFormat / End: $endFormat"
            itemView.admin.text = "Creator: " + myGroup.admin
        }

        override fun onClick(v: View?) {
            callback?.onGroupClick(
                values[adapterPosition].id
            )
        }

    }
}