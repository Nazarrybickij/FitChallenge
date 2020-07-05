package ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.apapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.fragments.AddUserFragment
import ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.fragments.GroupSettingFragment

class ViewPagerFragmentStateAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val values = intArrayOf(
        1,
        2
    )

    override fun createFragment(position: Int): Fragment = AddUserFragment().apply {
        when (position) {
            0 -> return AddUserFragment()
            1 -> return GroupSettingFragment()
        }
        return AddUserFragment()
    }

    override fun getItemCount(): Int {
        return values.size
    }
}