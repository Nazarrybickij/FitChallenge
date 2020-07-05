package ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_add_user.*
import ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.apapters.AddUserAdapter
import ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.apapters.AddedUsersAdapter
import ua.geekhub.antosiukoleksii.fitchallenge.entity.Contact
import ua.geekhub.antosiukoleksii.fitchallenge.R


class AddUserFragment : Fragment() {
    var adapter =
        AddedUsersAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val listener = object:
            AddUserAdapter.AdapterCallback {
            override fun onUserClick(contact: Contact) {
                for (element in adapter.values){
                    if(element.phone == contact.phone) {
                        return
                    }
                }
                adapter.values.add(contact)
                adapter.values.sortBy { Contact -> Contact.name }
                list_add_user.adapter = adapter
            }

        }
        var dialogFrag:DialogFragment =
            DialogAddUsers(
                listener
            )

        add_users_button.setOnClickListener {
            Log.e("zap","zapusk")
            dialogFrag.setTargetFragment(this,1)
            dialogFrag.show(fragmentManager!!,"addUser")
            list_add_user.adapter = adapter

        }


    }


}