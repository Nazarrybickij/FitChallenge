package ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.viewmodel

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ua.geekhub.antosiukoleksii.fitchallenge.ChekNetwork
import ua.geekhub.antosiukoleksii.fitchallenge.entity.Contact
import ua.geekhub.antosiukoleksii.fitchallenge.entity.GroupEntity
import ua.geekhub.antosiukoleksii.fitchallenge.entity.MyGroup
import java.text.SimpleDateFormat
import java.util.*

class CreateGroupViewModel : ViewModel() {


    //Функция для валидации
    fun validationGroup(
        admin: String,
        startDate: String,
        endDate: String,
        users: List<String>
    ): Pair<GroupEntity, Int> {

        if (startDate.isNotEmpty() && endDate.isNotEmpty() && users.isNotEmpty() && admin.isNotEmpty()) {
            var dateStart =
                Date(SimpleDateFormat("HH:mm dd.MM.yyyy").parse(startDate).time)
            var dateEnd = Date(SimpleDateFormat("HH:mm dd.MM.yyyy").parse(endDate).time)
            if (dateStart.before(dateEnd)) {
                return Pair(GroupEntity(admin, dateStart.time, dateEnd.time, users), 1)
            }
        }
        return Pair(GroupEntity(admin, 0, 0, users), 0)
    }

    //Функция для выбора врямя
    fun showDateTimeDialog(date_time_in: EditText, context: Context) {
        val calendar: Calendar = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val timeSetListener =
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        val simpleDateFormat = SimpleDateFormat("HH:mm dd.MM.yyyy")
                        date_time_in.setText(simpleDateFormat.format(calendar.time))
                    }
                TimePickerDialog(
                    context,
                    timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                ).show()

            }
        DatePickerDialog(
            context,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
            show()
        }
    }

    fun createGroupInFirebase(
        contactList: MutableList<Contact>,
        start_time_edtext: String,
        end_time_edtext: String
    ): Int {
        val curentAdminNamber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()
        var listUsers = mutableListOf<String>()
        for (element in contactList) {
            listUsers.add(element.phone)
        }
        var groupEntity = validationGroup(
            curentAdminNamber,
            start_time_edtext,
            end_time_edtext,
            listUsers
        )
        if (groupEntity.second == 1 && ChekNetwork.isNetworkConnected) {
            var key = FirebaseDatabase.getInstance().reference.child("groups").push().key

            val myGroup = MyGroup(
                key.toString(),
                curentAdminNamber,
                groupEntity.first.startDate,
                groupEntity.first.endDate
            )

            var firebaseIsComplete = FirebaseDatabase.getInstance().reference.child("groups")
                .child(key.toString())
                .setValue(groupEntity.first)
                for (element in groupEntity.first.users) {
                    var phone = element
                    if (phone.length != 13 && phone[0].toString() != "+") {
                        if (phone.length == 12) {
                            phone = "+$phone"
                        }
                        if (phone.length == 10) {
                            phone = "+38$phone"
                        }
                    }
                    FirebaseDatabase.getInstance().reference.child("users").child(phone)
                        .child("groups")
                        .child(key.toString()).setValue(myGroup)
                }
                FirebaseDatabase.getInstance().reference.child("users")
                    .child(curentAdminNamber)
                    .child("groups").child(key.toString()).setValue(myGroup)
                return Activity.RESULT_OK

            }

        return Activity.RESULT_CANCELED
    }
}