package ua.geekhub.antosiukoleksii.fitchallenge.services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ua.geekhub.antosiukoleksii.fitchallenge.entity.MyGroup
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class StepsIntentService : IntentService("StepsIntentService") {
    var myGroupList = mutableListOf<MyGroup>()
    val fitnessOptions: FitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .build()


    override fun onHandleIntent(intent: Intent?) {
        Log.e("Services","Run")
        getListGroup()
        setStepsInFirebase()
    }


    private fun getListGroup(){
        FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()).child("groups").addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var list = mutableListOf<MyGroup>()
                    for (dataSmap in dataSnapshot.children){
                        var data = dataSmap.value as HashMap<String,Any>
                        var id = data["id"] as String
                        var admin = data["admin"] as String
                        var startDate = data["startDate"] as Long
                        var endDate = data["endDate"] as Long
                        var myGroup = MyGroup(id,admin,startDate,endDate)
                        if (!list.contains(myGroup)){
                            list.add(myGroup)
                        }
                    }
                    myGroupList = list
                }
            })
    }

    private fun setStepsInFirebase() {
        var infinity = true
        while (infinity){
            for (element in myGroupList){
                var curentDate = Date()
                var date = Date(element.endDate)
                var datestart = Date(element.startDate)
                if(curentDate.before(date) && curentDate.after(datestart)){
                    countFit(element.id,element.startDate,element.endDate)
                }
            }
            TimeUnit.MINUTES.sleep(1)
        }


    }
    private fun countFit(id:String,start:Long,end:Long) {
        var result = 0
        val account = GoogleSignIn.getAccountForExtension(applicationContext, fitnessOptions)
        val cal = Calendar.getInstance()
        cal.time = Date(start)
        val startTime = cal.timeInMillis
        cal.time = Date(end)
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
                FirebaseDatabase.getInstance().reference.child("groups").child(id).child("steps").child(
                    FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()).setValue(result)
                Log.e("Result in Services",result.toString())
            }
    }
    private fun calcDS(ds: DataSet): Int {
        var result: Int = 0
        for (dp in ds.getDataPoints()) {
            for (field in dp.getDataType().getFields()) {
                result += dp.getValue(field).asInt()
            }
        }

        return result
    }

}
