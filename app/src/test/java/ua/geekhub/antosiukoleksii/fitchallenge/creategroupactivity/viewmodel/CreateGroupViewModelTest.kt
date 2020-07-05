package ua.geekhub.antosiukoleksii.fitchallenge.creategroupactivity.viewmodel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito.*
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import ua.geekhub.antosiukoleksii.fitchallenge.entity.GroupEntity

@RunWith(PowerMockRunner::class)
@PowerMockRunnerDelegate(JUnit4::class)
@PrepareForTest(FirebaseDatabase::class, FirebaseAuth::class, CreateGroupViewModel::class)
@PowerMockIgnore("org.mockito.*", "org.robolectric.*", "android.*", "androidx.*")

class CreateGroupViewModelTest {


//    @Mock
//    lateinit var auth: FirebaseAuth
//    @Mock
//    lateinit var database: FirebaseDatabase
//
    private val createGroupViewModel by lazy {
        CreateGroupViewModel()
    }
//    @Mock
//    lateinit var mockedReference: DatabaseReference
//
//    @Before
//    fun setUp() {
//        mockStatic(CreateGroupViewModel::class.java)
//        mockStatic(FirebaseAuth::class.java)
//        doReturn(auth).whenever(FirebaseAuth.getInstance())
//        mockedReference = Mockito.mock(DatabaseReference::class.java)
//        database = Mockito.mock(FirebaseDatabase::class.java)
//        doReturn(mockedReference).whenever(database).reference
//        doReturn("1").whenever(mockedReference).key
//
//        mockStatic(FirebaseDatabase::class.java)
//        doReturn(database).whenever(FirebaseDatabase.getInstance())
//
//        MockitoAnnotations.initMocks(this)
//
//    }


    @Test
    fun validationGroupValidData() {
        var admin = "+380969905535"
        var startDate = "01:10 01.04.2020"
        var endDate = "02:10 01.04.2020"
        var list = mutableListOf("+380969905535", "+380969903535")
        var group = GroupEntity(admin, 1585692600000, 1585696200000, list)
        assertEquals(
            group,
            createGroupViewModel!!.validationGroup(admin, startDate, endDate, list).first
        )
    }

    @Test
    fun validationGroupINValidData() {
        var admin = "+380969905535"
        var startDate = "02:10 01.04.2020"
        var endDate = "01:10 01.04.2020"
        var list = mutableListOf<String>()
        assertEquals(
            0,
            createGroupViewModel!!.validationGroup(admin, startDate, endDate, list).second
        )
    }

    @Test
    fun createGroupInFirebase(){

    }


}