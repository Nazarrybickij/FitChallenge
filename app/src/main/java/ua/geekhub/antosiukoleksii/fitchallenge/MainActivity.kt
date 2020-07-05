package ua.geekhub.antosiukoleksii.fitchallenge

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Network
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.CreateGroupActivity
import ua.geekhub.antosiukoleksii.fitchallenge.entity.MyGroup
import ua.geekhub.antosiukoleksii.fitchallenge.groupinfoactivity.GroupInfoActivity
import ua.geekhub.antosiukoleksii.fitchallenge.mainactivity.adapters.GroupsAdapter
import ua.geekhub.antosiukoleksii.fitchallenge.mainactivity.viewmodel.MainViewModel
import ua.geekhub.antosiukoleksii.fitchallenge.phoneauth.PhoneAuthActivity
import ua.geekhub.antosiukoleksii.fitchallenge.services.StepsIntentService
import ua.geekhub.antosiukoleksii.fitchallenge.settingactivity.SettingActivity
import ua.geekhub.antosiukoleksii.fitchallenge.utilities.InjectorUtils
import ua.geekhub.antosiukoleksii.fitchallenge.workmaneger.RunWorkerSendStepsInFirebase
import java.util.*


class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 10
    private val REQUEST_CREATE_GROUP_ACTIVITY = 5
    private val SIGN_IN_REQUEST_CODE_FIREBASE = 15
    //переменна для вихода через 2 клика
    var singleBack = false
    lateinit var viewModel: MainViewModel

    override fun onResume() {
        super.onResume()
        if (!ChekNetwork.isNetworkConnected) Toast.makeText(applicationContext,"Network lost",Toast.LENGTH_LONG).show()
        bottom_nav_menu.selectedItemId = R.id.bottomNavigationHomeMenuId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ChekNetwork
        val factory = InjectorUtils.provideMainViewModelFactory()
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        FirebaseApp.initializeApp(this)
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivityForResult(
                Intent(this, PhoneAuthActivity::class.java),
                SIGN_IN_REQUEST_CODE_FIREBASE
            )
        } else {
            displayGroup()
            stepsDay()
            startService(Intent(this, StepsIntentService::class.java))
        }
        //ныжня панель навигации обработка кликов
        bottom_nav_menu.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavigationHomeMenuId -> {

                }
                R.id.bottomNavigationAddMenuId -> {
                    startActivityForResult(
                        Intent(this, CreateGroupActivity::class.java),
                        REQUEST_CREATE_GROUP_ACTIVITY,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                    )
                }
                R.id.bottomNavigationSettMenuId -> {
                    startActivity(
                        Intent(this, SettingActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                    )
                }
            }
            true
        }
    }

    private fun displayGroup() {
        val listener = object :
            GroupsAdapter.AdapterCallback {
            override fun onGroupClick(id: String) {
                val intent = Intent(this@MainActivity, GroupInfoActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            }

        }
        FirebaseDatabase.getInstance().reference.child("users")
            .child(FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()).child("groups")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var myGroupList = mutableListOf<MyGroup>()
                        for (dataSmap in dataSnapshot.children) {
                            var data = dataSmap.value as HashMap<String, Any>
                            var id = data["id"] as String
                            var admin = data["admin"] as String
                            var startDate = data["startDate"] as Long
                            var endDate = data["endDate"] as Long
                            myGroupList.add(MyGroup(id, admin, startDate, endDate))
                        }
                        RunWorkerSendStepsInFirebase().validationAndRunWork(
                            this@MainActivity,
                            myGroupList
                        )
                        myGroupList.sortBy { MyGroup -> MyGroup.startDate }
                        list_main_groups.adapter =
                            GroupsAdapter(myGroupList as ArrayList<MyGroup>, listener)
                    }
                })
    }

    private fun stepsDay() {
        val account = GoogleSignIn.getAccountForExtension(this, viewModel.fitRepo.fitnessOptions)
        var response = Fitness.getRecordingClient(this, account).subscribe(DataType.TYPE_ACTIVITY_SEGMENT)
        if (!GoogleSignIn.hasPermissions(account, viewModel.fitRepo.fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this,
                MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION,
                account,
                viewModel.fitRepo.fitnessOptions
            )
        } else {
            viewModel.getDay(this)
            viewModel.resSteps.observe(this, androidx.lifecycle.Observer {
                count_textview.text = it.toString()
                Log.i("fitRepo", it.toString())
            })
            viewModel.resDistance.observe(this, androidx.lifecycle.Observer {
                distance_textview.text = "$it M"
            })
            viewModel.resCalories.observe(this, androidx.lifecycle.Observer {
                cal_textview.text = "$it kcal"
            })
        }
    }




    override fun onBackPressed() {
        //виход через 2 клика
        if (singleBack) {
            finishAffinity()
            super.onBackPressed()
        }
        this.singleBack = true
        Toast.makeText(this, "Double Back to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { singleBack = false }, 2000)

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION) {
                stepsDay()
            }
            if (requestCode == REQUEST_CREATE_GROUP_ACTIVITY) {
                Toast.makeText(this, "Created successfully", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == SIGN_IN_REQUEST_CODE_FIREBASE) {
            if (resultCode == Activity.RESULT_OK) {
                displayGroup()
                stepsDay()
                Toast.makeText(this, "Enter successfully", Toast.LENGTH_SHORT).show()
            } else {
                finish()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)

    }

}

