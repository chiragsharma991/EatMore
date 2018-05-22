package dk.eatmore.softtech360.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v7.widget.AppCompatButton
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.dashboard.main.MainActivity
import dk.eatmore.softtech360.rest.ApiCall
import dk.eatmore.softtech360.rest.ApiClient
import dk.eatmore.softtech360.storage.PreferenceUtil
import dk.eatmore.softtech360.utils.BaseActivity
import dk.eatmore.softtech360.utils.BaseFragment
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.layout_progressbar.*
import org.json.JSONObject
import java.util.regex.Pattern


class LoginActivity : BaseActivity(){





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init(savedInstanceState)


    }




    companion object {

        val TAG = "LoginActivity"
        fun newInstance(): LoginActivity {
            return LoginActivity()
        }
    }


    private var edit: EditText? = null

     fun init(savedInstancedState: Bundle?) {

        fullScreen()
        progress_bar.visibility = View.GONE

        log_login_btn.setOnClickListener{
          //startActivity(Intent(this,MainActivity::class.java))
            if(isValidate()) loginAttempt()
        }

        log_sign_btn.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(ApiClient.SIGNUP_URL)
            startActivity(i)
        }


    }
  /*  progress_bar.visibility = View.VISIBLE
    callAPI(btn_login, ApiCall.login(edt_email.text.toString(), edt_pass.text.toString(), ""+PreferenceUtil.getString(PreferenceUtil.TOKEN, "")), object : BaseFragment.OnApiCallInteraction {
        override fun <T> onSuccess(body: T?) {
            progress_bar.visibility = View.GONE

            var mUserData = body as UserData
            if (mUserData.IsSuccess) {

                PreferenceUtil.putValue(PreferenceUtil.IS_LOGIN, true)
                PreferenceUtil.putValue(PreferenceUtil.FULL_NAME, mUserData.UserName)
                PreferenceUtil.putValue(PreferenceUtil.USER_ID, mUserData.UserId)
                PreferenceUtil.putValue(PreferenceUtil.USER_TYPE, mUserData.UserType)
                PreferenceUtil.putValue(PreferenceUtil.PROFILE_URL, mUserData.ImagePath)
                PreferenceUtil.putValue(PreferenceUtil.MOBILE, mUserData.Mobile)
                PreferenceUtil.putValue(PreferenceUtil.EMAIL, mUserData.EmailId)
                PreferenceUtil.putValue(PreferenceUtil.ADDRESS, mUserData.Address)

                PreferenceUtil.putValue(PreferenceUtil.USER_NAME, edt_email.text.toString())
                PreferenceUtil.putValue(PreferenceUtil.PASS, edt_pass.text.toString())
                PreferenceUtil.putValue(PreferenceUtil.REMEMBER_ME, chkRemember.isChecked)
                PreferenceUtil.save()

                setResult(Activity.RESULT_OK)
                finish()
            }else
                showSnackBar(edt_email, ""+mUserData?.Message)
        }

        override fun onFail() {
            progress_bar.visibility = View.GONE
        }
    })*/


    private fun loginAttempt() {

        progress_bar.visibility=View.VISIBLE
        callAPI(progress_bar,log_login_btn,ApiCall.login(log_email_edt.text.toString(),log_pass_edt.text.toString(),"POS","Owner"),object : BaseFragment.OnApiCallInteraction{

            override fun <T> onSuccess(body: T?) {
                progress_bar.visibility= View.GONE
                val json= body as JsonObject  // please be mind you are using jsonobject(Gson)
                if (json.get("status").asBoolean){
                    showSnackBar(log_email_edt, json.get("msg").asString)
                    PreferenceUtil.putValue(PreferenceUtil.USER_NAME, json.getAsJsonObject("user_details").get("username").asString)
                    PreferenceUtil.putValue(PreferenceUtil.R_KEY, ""+json.get("r_key").asString)
                    PreferenceUtil.putValue(PreferenceUtil.R_TOKEN, ""+json.get("r_token").asString)
                    PreferenceUtil.save()
                    moveToDashboard()
                }else{
                    showSnackBar(log_email_edt, json.get("error").asString)
                }
            }

            override fun onFail() {
                progress_bar.visibility= View.GONE
                showSnackBar(log_email_edt,getString(R.string.error_404))
                log(TAG,"api call failed...")

            }

        })



    }

    private fun moveToDashboard() {
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1000)
    }



    fun isValidate(): Boolean {

        return when {
            TextUtils.isEmpty(log_email_edt.text.trim().toString()) -> {
                showSnackBar(log_email_edt, getString(R.string.login_val_email))

                false
            }
            !validMail(log_email_edt.text.trim().toString()) -> {
                showSnackBar(log_email_edt, getString(R.string.invalid_email))
                false
            }
            TextUtils.isEmpty(log_pass_edt.text.trim().toString()) -> {
                showSnackBar(log_pass_edt, getString(R.string.login_val_pass))
                false
            }
            else -> true
        }
    }

    fun validMail(email : String): Boolean{

        val EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher = pattern.matcher(email)
        return matcher.matches()

    }






}






