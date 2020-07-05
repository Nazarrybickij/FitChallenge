package ua.geekhub.antosiukoleksii.fitchallenge.phoneauth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import kotlinx.android.synthetic.main.activity_phone.*
import ua.geekhub.antosiukoleksii.fitchallenge.MainActivity
import ua.geekhub.antosiukoleksii.fitchallenge.R
import java.util.concurrent.TimeUnit


class PhoneAuthActivity : AppCompatActivity(), View.OnClickListener {
    var mPhoneNumberField: EditText? = null
    var mVerificationField: EditText? = null
    private var mAuth: FirebaseAuth? = null
    private var mResendToken: ForceResendingToken? = null
    private var mCallbacks: OnVerificationStateChangedCallbacks? = null
    var mVerificationId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
        mPhoneNumberField = findViewById<View>(R.id.field_phone_number) as EditText
        mVerificationField =
            findViewById<View>(R.id.field_verification_code) as EditText

        button_start_verification.setOnClickListener(this)
        button_verify_phone.setOnClickListener(this)
        button_resend.setOnClickListener(this)
        mAuth = FirebaseAuth.getInstance()
        mCallbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(
                    TAG,
                    "onVerificationCompleted:$credential"
                )
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    mPhoneNumberField!!.error = "Invalid phone number."
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(this@PhoneAuthActivity, "Quota exceeded.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                mVerificationId = verificationId
                mResendToken = token
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    Log.d(
                        TAG,
                        "signInWithCredential:success"
                    )
                    val user = task.result!!.user
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Log.w(
                        TAG,
                        "signInWithCredential:failure",
                        task.exception
                    )
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        mVerificationField!!.error = "Invalid code."
                    }
                }
            }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            mCallbacks!!
        )
    }

    private fun verifyPhoneNumberWithCode(
        verificationId: String?,
        code: String
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: ForceResendingToken?
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,  // Phone number to verify
            60,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            this,  // Activity (for callback binding)
            mCallbacks!!,  // OnVerificationStateChangedCallbacks
            token
        ) // ForceResendingToken from callbacks
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = mPhoneNumberField!!.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField!!.error = "Invalid phone number."
            return false
        }
        return true
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            startActivity(Intent(this@PhoneAuthActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_start_verification -> {
                if (!validatePhoneNumber()) {
                    return
                }
                var phone = mPhoneNumberField!!.text.toString()
                if (phone.length != 13 && phone[0].toString() != "+"){
                    if(phone.length == 12){
                        phone = "+$phone"
                    }
                    if (phone.length == 10 ){
                        phone = "+38$phone"
                    }
                }
                startPhoneNumberVerification(phone)
            }
            R.id.button_verify_phone -> {
                val code = mVerificationField!!.text.toString()
                if (TextUtils.isEmpty(code)) {
                    mVerificationField!!.error = "Cannot be empty."
                    return
                }
                verifyPhoneNumberWithCode(mVerificationId, code)
            }
            R.id.button_resend -> resendVerificationCode(
                mPhoneNumberField!!.text.toString(),
                mResendToken
            )
        }
    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
    }
}


