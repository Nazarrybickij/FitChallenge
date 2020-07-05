package ua.geekhub.antosiukoleksii.fitchallenge.utilities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ua.geekhub.antosiukoleksii.fitchallenge.mainactivity.viewmodel.MainViewModel


class MainViewModelFactory()
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }
}