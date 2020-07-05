package ua.geekhub.antosiukoleksii.fitchallenge.createGroup

import android.app.Activity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.fragments.AddUserFragment
import ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.viewmodel.CreateGroupViewModel
import ua.geekhub.antosiukoleksii.fitchallenge.entity.Contact

@RunWith(AndroidJUnit4ClassRunner::class)
class CreateGroupTest {

    private var viewmodel: CreateGroupViewModel? = null

    @Before
    fun setup() {
        viewmodel = CreateGroupViewModel()
    }
    @Test
    fun createGroupValidData(){
        var startDate = "01:10 01.04.2020"
        var endDate = "02:10 01.04.2020"
        var list = mutableListOf(Contact("test","+380969905535"))
            assertEquals(
                Activity.RESULT_OK,
            viewmodel!!.createGroupInFirebase(list,startDate,endDate)
        )
    }
    @Test
    fun createGroupInValidDataDate(){
        var startDate = "02:10 01.04.2020"
        var endDate = "01:10 01.04.2020"
        var list = mutableListOf(Contact("test","+380969905535"))
        assertEquals(
            Activity.RESULT_CANCELED,
            viewmodel!!.createGroupInFirebase(list,startDate,endDate)
        )
    }
    @Test
    fun createGroupInValidDataList(){
        var startDate = "02:10 01.04.2020"
        var endDate = "03:10 01.04.2020"
        var list = mutableListOf<Contact>()
        assertEquals(
            Activity.RESULT_CANCELED,
            viewmodel!!.createGroupInFirebase(list,startDate,endDate)
        )
    }
}