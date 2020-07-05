package ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.add_users_dialog_fragment.view.*
import kotlinx.android.synthetic.main.fragment_add_user.*
import ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.apapters.AddUserAdapter
import ua.geekhub.antosiukoleksii.fitchallenge.entity.Contact
import ua.geekhub.antosiukoleksii.fitchallenge.R


class DialogAddUsers(val listener: AddUserAdapter.AdapterCallback) : DialogFragment() {
    private val REQUEST_CODE_PERMISSION_READ_CONTACTS = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.add_users_dialog_fragment, null)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val permissionStatus =
            ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_CONTACTS)
        Log.e("permissionStatus", permissionStatus.toString())
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            readContacts()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CODE_PERMISSION_READ_CONTACTS
            )
        }

        view.btnYes.setOnClickListener {
            dismiss()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                readContacts()
            }
        }
    }

    private fun readContacts() {
        var contact = mutableListOf<Contact>()
        val resolver = activity!!.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (resolver != null) {
            while (resolver.moveToNext()) {
                val name: String =
                    resolver.getString(resolver.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val number =
                    resolver.getString(resolver.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ","")
                contact.add(
                    Contact(
                        name,
                        number
                    )
                )
                
            }

        }
        resolver?.close()
        contact.sortBy { Contact -> Contact.name }
        list_add_user.adapter =
            AddUserAdapter(
                contact as ArrayList<Contact>,
                listener
            )
    }
}

