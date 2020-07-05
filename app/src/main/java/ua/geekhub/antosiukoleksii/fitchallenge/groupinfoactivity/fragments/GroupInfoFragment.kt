package ua.geekhub.antosiukoleksii.fitchallenge.groupinfoactivity.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_group_info.view.*
import ua.geekhub.antosiukoleksii.fitchallenge.ChekNetwork
import ua.geekhub.antosiukoleksii.fitchallenge.MyApplication
import ua.geekhub.antosiukoleksii.fitchallenge.R
import ua.geekhub.antosiukoleksii.fitchallenge.groupinfoactivity.adapters.NameStepsAdapter
import ua.geekhub.antosiukoleksii.fitchallenge.groupinfoactivity.entity.NameStepsEntity


class GroupInfoFragment : Fragment() {
    var id = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!ChekNetwork.isNetworkConnected) Toast.makeText(context,"Network lost",Toast.LENGTH_LONG).show()
        view.button_cancel_group.setOnClickListener {
            try {
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle("Confirm")
                builder.setMessage("Are you sure?")

                builder.setPositiveButton("YES",
                    DialogInterface.OnClickListener { dialog, which ->
                        WorkManager.getInstance(context!!).cancelAllWorkByTag(id!!)
                        FirebaseDatabase.getInstance().reference.child("users")
                            .child(FirebaseAuth.getInstance().currentUser?.phoneNumber.toString())
                            .child("groups").child(id!!).removeValue()
                        Toast.makeText(context,"You leave the group",Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    })

                builder.setNegativeButton("NO",
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })

                val alert: AlertDialog = builder.create()
                alert.show()
            } catch (e: Exception) {
                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
            }
        }

        FirebaseDatabase.getInstance().reference.child("groups").child(id.toString())
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val listNameSteps = mutableListOf<NameStepsEntity>()
                        var dataInfo = dataSnapshot.value as HashMap<String, Any>
                        var admin = dataInfo["admin"] as String
                        var startDate = dataInfo["startDate"] as Long
                        var endDate = dataInfo["endDate"] as Long
                        view.creator_text.text = "Creator $admin"
                        view.start_time_text.text =
                            "Start time: " + DateFormat.format("HH:mm dd.MM.yyyy", startDate)
                        view.end_time_text.text =
                            "End time: " + DateFormat.format("HH:mm dd.MM.yyyy", endDate)
                        var dataSnapSteps = dataSnapshot.child("steps")
                        for (dataSteps in dataSnapSteps.children) {
                            var name = dataSteps.key
                            var steps = dataSteps.value
                            listNameSteps.add(NameStepsEntity(name.toString(), steps.toString()))
                        }
                        var nameStepsAdapter = NameStepsAdapter(listNameSteps)
                        nameStepsAdapter.value.sortedBy { NameStepsEntity -> NameStepsEntity.steps.toInt() }
                        view.recycler_view.adapter = nameStepsAdapter
                        Log.e("Info", listNameSteps.toString())

                    }
                })
        view.back_button.setOnClickListener {
            activity?.finish()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        id = arguments?.getString("id", "0").toString()
        return inflater.inflate(R.layout.fragment_group_info, container, false)
    }

}

