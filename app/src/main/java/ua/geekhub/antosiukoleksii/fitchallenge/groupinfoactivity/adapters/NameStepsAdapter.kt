package ua.geekhub.antosiukoleksii.fitchallenge.groupinfoactivity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_name_steps.view.*
import ua.geekhub.antosiukoleksii.fitchallenge.R
import ua.geekhub.antosiukoleksii.fitchallenge.groupinfoactivity.entity.NameStepsEntity

class NameStepsAdapter(var value:List<NameStepsEntity>): RecyclerView.Adapter<NameStepsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(value[position])
    }

    override fun getItemCount() = value.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_name_steps, parent, false)

        return ViewHolder(itemView)
    }



   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

       fun onBind(nameStepsEntity: NameStepsEntity){
           itemView.name_user.text = nameStepsEntity.name
           itemView.steps_text.text = nameStepsEntity.steps
       }
    }

}