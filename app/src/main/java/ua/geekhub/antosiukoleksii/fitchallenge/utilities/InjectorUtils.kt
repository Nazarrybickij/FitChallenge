package ua.geekhub.antosiukoleksii.fitchallenge.utilities


object InjectorUtils {

    fun provideMainViewModelFactory(): MainViewModelFactory {
        return MainViewModelFactory()
    }

    fun provideCreateGroupViewModelFactory(): CreateGroupModelFactory {
        return CreateGroupModelFactory()
    }
}