package ua.geekhub.antosiukoleksii.fitchallenge.workmaneger

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import java.util.concurrent.TimeUnit

class SendStepsInFirebaseWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    val fitnessOptions: FitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .build()

    override fun doWork(): Result {
        loadCountInFit()
        return Result.success()
    }

    private fun loadCountInFit() {
        countFit()
    }

    private fun countFit() {
        var result = 0
        val account = GoogleSignIn.getAccountForExtension(applicationContext, fitnessOptions)
        val cal = Calendar.getInstance()
        cal.time = Date(inputData.getLong("start",0))
        val startTime = cal.timeInMillis
        cal.time = Date(inputData.getLong("end",0))
        val endTime = cal.timeInMillis
        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.MINUTES)
            .build()
        Fitness.getHistoryClient(applicationContext, account)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                for (b in response.buckets) {
                    for (ds in b.dataSets) {
                        result += calcDS(ds)
                    }
                }
                FirebaseDatabase.getInstance().reference.child("groups").child(inputData.getString("id").toString()).child("steps").child(
                    FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()).setValue(result)
                Log.e("Result",result.toString())
            }
    }
    private fun calcDS(ds: DataSet): Int {
        var result: Int = 0
        for (dp in ds.getDataPoints()) {
            for (field in dp.getDataType().getFields()) {
                result += dp.getValue(field).asInt()
            }
/*                        val dateFormat: DateFormat = DateFormat.getDateInstance()
    val timeFormat: DateFormat = DateFormat.getTimeInstance()
    Log.e("History", "Data point:")
    Log.e("History", "\tType: " + dp.getDataType().getName())
    Log.e(
        "History",
        "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)).toString() + " " + timeFormat.format(
            dp.getStartTime(TimeUnit.MILLISECONDS)
        )
    )
    Log.e(
        "History",
        "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)).toString() + " " + timeFormat.format(
            dp.getEndTime(TimeUnit.MILLISECONDS)
        )
    )
    for (field in dp.getDataType().getFields()) {
        Log.e(
            "History", "\tField: " + field.getName().toString() +
                    " Value: " + dp.getValue(field)
        )
    }*/
        }

        return result
    }
}