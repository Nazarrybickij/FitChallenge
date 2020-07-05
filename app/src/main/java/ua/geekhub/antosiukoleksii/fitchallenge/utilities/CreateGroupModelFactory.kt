package ua.geekhub.antosiukoleksii.fitchallenge.utilities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.viewmodel.CreateGroupViewModel

class CreateGroupModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateGroupViewModel() as T
    }
}