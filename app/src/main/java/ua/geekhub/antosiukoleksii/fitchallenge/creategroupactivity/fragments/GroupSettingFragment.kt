package ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_add_setting_group.*
import ua.geekhub.antosiukoleksii.fitchallenge.R


class GroupSettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_setting_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (radio_button_without.isChecked){
            purpose_edit_text.visibility = View.INVISIBLE
        }

        radio_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_button_without -> {
                    radio_button_with.isChecked = false
                    purpose_edit_text.visibility = View.INVISIBLE
                }
                R.id.radio_button_with -> {
                    radio_button_without.isChecked = false
                    purpose_edit_text.visibility = View.VISIBLE
                }

            }
        }
    }

}
