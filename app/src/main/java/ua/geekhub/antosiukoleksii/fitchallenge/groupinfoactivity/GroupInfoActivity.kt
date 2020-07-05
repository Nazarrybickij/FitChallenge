package ua.geekhub.antosiukoleksii.fitchallenge.groupinfoactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ua.geekhub.antosiukoleksii.fitchallenge.R
import ua.geekhub.antosiukoleksii.fitchallenge.groupinfoactivity.fragments.GroupInfoFragment

class GroupInfoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_info)
        var idGroup = intent.getStringExtra("id")
        val frag = GroupInfoFragment()
        val bundle = Bundle()
        bundle.putString("id",idGroup)
        frag.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.frgmCont,frag)
            .commit()
    }
}
