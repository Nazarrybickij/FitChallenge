package ua.geekhub.antosiukoleksii.fitchallenge.settingactivity

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_setting.bottom_nav_menu
import ua.geekhub.antosiukoleksii.fitchallenge.MainActivity
import ua.geekhub.antosiukoleksii.fitchallenge.R
import ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.CreateGroupActivity

class SettingActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        bottom_nav_menu.selectedItemId = R.id.bottomNavigationSettMenuId
        title = "Settings"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        title = "Settings"

        change_number.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,MainActivity::class.java),ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        bottom_nav_menu.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavigationHomeMenuId -> {
                    startActivity(Intent(this,MainActivity::class.java),ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                }
                R.id.bottomNavigationAddMenuId -> {
                    startActivityForResult(
                        Intent(this, CreateGroupActivity::class.java),
                        5,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                    )
                }
                R.id.bottomNavigationSettMenuId -> {
                }
            }
            true
        }
    }
}
