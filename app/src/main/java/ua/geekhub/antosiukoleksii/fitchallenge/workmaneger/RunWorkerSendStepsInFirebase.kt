package ua.geekhub.antosiukoleksii.fitchallenge.workmaneger

import android.content.Context
import android.util.Log
import androidx.work.*
import ua.geekhub.antosiukoleksii.fitchallenge.entity.MyGroup
import java.util.*
import java.util.concurrent.TimeUnit


class RunWorkerSendStepsInFirebase {

    fun validationAndRunWork(context: Context, list: MutableList<MyGroup>){
        for (element in list){
            var curentDate = Date()
            var date = Date(element.endDate)
            var datestart = Date(element.startDate)
            if(curentDate.before(date) && curentDate.after(datestart)){
                Log.e("validTest Вок по Тегу",element.id)
                zapWork(context,element)
            }
        }
    }

    private fun zapWork(context: Context,myGroup: MyGroup) {
        val constraints: Constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val status = WorkManager.getInstance(context).getWorkInfosByTag(myGroup.id)
        var running = false
        var runningC = false
        var daley = myGroup.endDate - Date().time
        val workInfoList: List<WorkInfo> = status.get()
        for (workInfo in workInfoList) {
            val state = workInfo.state
            running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
            runningC = state == WorkInfo.State.ENQUEUED
        }
        Log.e("Запущен",runningC.toString())
        if (!running) {
            Log.e("Отменен",running.toString())
            val myData: Data = Data.Builder()
                .putLong("start", myGroup.startDate)
                .putLong("end", myGroup.endDate)
                .putString("id",myGroup.id)
                .build()
                val uploadWorkRequest =
                    OneTimeWorkRequestBuilder<SendStepsInFirebaseWorker>().setConstraints(constraints)
                        .setInputData(myData)
                        .setInitialDelay(daley,TimeUnit.MILLISECONDS)
                        .addTag(myGroup.id)
                        .build()
                WorkManager.getInstance(context).enqueue(uploadWorkRequest)
        }
    }

}