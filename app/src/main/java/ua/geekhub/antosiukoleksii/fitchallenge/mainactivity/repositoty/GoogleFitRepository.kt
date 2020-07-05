package ua.geekhub.antosiukoleksii.fitchallenge.mainactivity.repositoty

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import java.util.concurrent.TimeUnit


class GoogleFitRepository {
    var resSteps = MutableLiveData<Int>()
    var resDistance = MutableLiveData<Int>()
    var resCalories = MutableLiveData<Int>()
    var resByTime = MutableLiveData<Int>()
    val fitnessOptions: FitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
        .build()

    fun getStepsCount(context: Context, startTime: Long, endTime: Long): MutableLiveData<Int> {
        var result: Int = 0
        val readRequest = DataReadRequest.Builder()
            .aggregate(
                DataType.TYPE_STEP_COUNT_DELTA,
                DataType.AGGREGATE_STEP_COUNT_DELTA
            )
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.MINUTES)
            .build()
        val account = GoogleSignIn.getAccountForExtension(context, fitnessOptions)
        Fitness.getHistoryClient(context, account)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                for (b in response.buckets) {
                    for (ds in b.dataSets) {
                        result += calcDS(ds)
                    }
                }
                resByTime.value = result
            }
        return resByTime
    }

    fun getStepsDay(context: Context): MutableLiveData<Int> {
        val account = GoogleSignIn.getAccountForExtension(context, fitnessOptions)
        Fitness.getHistoryClient(context, account).readDailyTotal(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .addOnSuccessListener { response ->
                val totalSteps: Long =
                    if (response.isEmpty) 0 else response.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt().toLong()
                resSteps.value = totalSteps.toInt()

            }

        return resSteps
    }
    fun getDistanceDay(context: Context): MutableLiveData<Int> {
        val account = GoogleSignIn.getAccountForExtension(context, fitnessOptions)
        Fitness.getHistoryClient(context, account).readDailyTotal(DataType.AGGREGATE_DISTANCE_DELTA)
            .addOnSuccessListener { response ->
                val totalDistance =
                    if (response.isEmpty) 0 else response.getDataPoints().get(0).getValue(Field.FIELD_DISTANCE).asFloat().toInt()
                resDistance.value = totalDistance

            }

        return resDistance
    }
    fun getCaloriesDay(context: Context): MutableLiveData<Int> {
        val account = GoogleSignIn.getAccountForExtension(context, fitnessOptions)
        Fitness.getHistoryClient(context, account).readDailyTotal(DataType.AGGREGATE_CALORIES_EXPENDED)
            .addOnSuccessListener { response ->
                val totalCalories =
                    if (response.isEmpty) 0 else response.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat().toInt()
                resCalories.value = totalCalories

            }

        return resCalories
    }

    private fun calcDS(ds: DataSet): Int {
        var result: Int = 0
        for (dp in ds.getDataPoints()) {
            for (field in dp.getDataType().getFields()) {
                result += dp.getValue(field).asInt()
            }
/*            val dateFormat: DateFormat = DateFormat.getDateInstance()
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