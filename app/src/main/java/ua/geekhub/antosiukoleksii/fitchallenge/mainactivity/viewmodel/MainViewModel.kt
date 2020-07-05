package ua.geekhub.antosiukoleksii.fitchallenge.mainactivity.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.geekhub.antosiukoleksii.fitchallenge.ChekNetwork
import ua.geekhub.antosiukoleksii.fitchallenge.mainactivity.repositoty.GoogleFitRepository
import java.util.*

class MainViewModel : ViewModel() {
    var timer = Timer()
    val fitRepo: GoogleFitRepository = GoogleFitRepository()
    var resSteps = MutableLiveData<Int>()
    var resDistance = MutableLiveData<Int>()
    var resCalories = MutableLiveData<Int>()
    var resByTime = MutableLiveData<Int>()
    var boolInit = false

    fun accessGoogleFit(context: Context):MutableLiveData<Int> {
        val cal = Calendar.getInstance()
        cal.time = Date()
        val endTime = cal.timeInMillis
        cal.add(Calendar.HOUR, -24)
        val startTime = cal.timeInMillis
        return fitRepo.getStepsCount(context, startTime, endTime)
    }
    fun getDay(context: Context) {
        resSteps = fitRepo.getStepsDay(context)
        resDistance = fitRepo.getDistanceDay(context)
        resCalories = fitRepo.getCaloriesDay(context)
        getDayInfinity(context)
    }

    fun getDayInfinity(context: Context){
        if (!boolInit){
            timer.scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() {
                        Log.i("run","Run")
                        fitRepo.getStepsDay(context)
                        fitRepo.getDistanceDay(context)
                        fitRepo.getCaloriesDay(context)
                    }
                },
                2000, 15000
            )
        }
        boolInit = true
    }

    override fun onCleared() {
        timer.cancel()
        super.onCleared()
    }

}