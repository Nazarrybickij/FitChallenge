package ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity


import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.activity_main.bottom_nav_menu
import ua.geekhub.antosiukoleksii.fitchallenge.ChekNetwork
import ua.geekhub.antosiukoleksii.fitchallenge.MainActivity
import ua.geekhub.antosiukoleksii.fitchallenge.R
import ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.apapters.ViewPagerFragmentStateAdapter
import ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.fragments.AddUserFragment
import ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.viewmodel.CreateGroupViewModel
import ua.geekhub.antosiukoleksii.fitchallenge.phoneauth.PhoneAuthActivity
import ua.geekhub.antosiukoleksii.fitchallenge.settingactivity.SettingActivity
import ua.geekhub.antosiukoleksii.fitchallenge.utilities.InjectorUtils


class CreateGroupActivity : AppCompatActivity() {
    private val SIGN_IN_REQUEST_CODE_FIREBASE = 15

    override fun onResume() {
        bottom_nav_menu.selectedItemId = R.id.bottomNavigationAddMenuId
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        if (!ChekNetwork.isNetworkConnected) Toast.makeText(applicationContext,"Network lost",Toast.LENGTH_LONG).show()
        val factory = InjectorUtils.provideCreateGroupViewModelFactory()
        val viewModel = ViewModelProvider(this, factory)
            .get(CreateGroupViewModel::class.java)

        FirebaseApp.initializeApp(this)
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivityForResult(
                Intent(this, PhoneAuthActivity::class.java),
                SIGN_IN_REQUEST_CODE_FIREBASE
            )
        }

        //установка тыпу едтиттекстов дататайм пикер
        start_time_edtext.inputType = InputType.TYPE_NULL
        end_time_edtext.inputType = InputType.TYPE_NULL

        //ныжня панель навигации обработка кликов
        bottom_nav_menu.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavigationHomeMenuId -> {
                    startActivity(Intent(this, MainActivity::class.java),ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                }
                R.id.bottomNavigationAddMenuId -> {


                }
                R.id.bottomNavigationSettMenuId -> {
                    startActivity(Intent(this, SettingActivity::class.java),ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                }
            }
            true
        }

        start_time_edtext.setOnClickListener {
            viewModel.showDateTimeDialog(start_time_edtext, this)
        }

        end_time_edtext.setOnClickListener {
            viewModel.showDateTimeDialog(end_time_edtext, this)
        }
        button_create_group.setOnClickListener {
            var viewpagerstate = view_pager.adapter as ViewPagerFragmentStateAdapter
            var id = viewpagerstate.getItemId(0)
            var frag = supportFragmentManager.findFragmentById(id.toInt()) as AddUserFragment
            var result = viewModel.createGroupInFirebase(frag.adapter.values,start_time_edtext.text.toString(),end_time_edtext.text.toString())
            if (result == Activity.RESULT_OK ){
                setResult(Activity.RESULT_OK)
                finish()
            }else{
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
        button_cancel_group.setOnClickListener {
            setResult(0)
            finish()
        }
        //Users and Setting view pager
        view_pager.adapter =
            ViewPagerFragmentStateAdapter(
                this
            )
        TabLayoutMediator(fragmentPestTabLayout, view_pager,
            TabLayoutMediator.TabConfigurationStrategy() { tab: TabLayout.Tab, i: Int ->
                when (i) {
                    0 -> tab.text = "User"
                    1 -> tab.text = "Setting"
                }

            }).attach()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE_FIREBASE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Enter successfully", Toast.LENGTH_SHORT).show()
            } else {
                finishAffinity()
            }
        }
    }
}
