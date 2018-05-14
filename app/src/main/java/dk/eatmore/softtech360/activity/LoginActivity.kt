package dk.eatmore.softtech360.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import com.google.gson.JsonObject
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.dashboard.main.MainActivity
import dk.eatmore.softtech360.rest.ApiCall
import dk.eatmore.softtech360.utils.BaseActivity
import dk.eatmore.softtech360.utils.BaseFragment
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.layout_progressbar.*

class LoginActivity : BaseActivity(){

     val tag = this.javaClass.simpleName


    override fun getLayout(): Int {
        return R.layout.activity_login
    }


    companion object {
        fun newInstance(): LoginActivity {
            return LoginActivity()
        }
    }


    private var edit: EditText? = null

    override fun init(savedInstancedState: Bundle?) {
        fullScreen()
        progress_bar.visibility = View.GONE
        log_login_btn.setOnClickListener{
          startActivity(Intent(this,MainActivity::class.java))
          //  if(isValidate()) loginAttempt()
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
        callAPI(log_login_btn,ApiCall.login(log_email_edt.text.toString(),log_pass_edt.text.toString(),"POS","Owner"),object : BaseFragment.OnApiCallInteraction{

            override fun <T> onSuccess(body: T?) {
                progress_bar.visibility= View.GONE
                var json= body as JsonObject
                log(tag,json.toString())
            }

            override fun onFail() {
                progress_bar.visibility= View.GONE
                log(tag,"api call failed...")

            }

        })



    }

    fun isValidate(): Boolean {

        return when {
            TextUtils.isEmpty(log_email_edt.text.trim().toString()) -> {
                showSnackBar(log_email_edt, getString(R.string.login_val_email))

                false
            }
            TextUtils.isEmpty(log_pass_edt.text.trim().toString()) -> {
                showSnackBar(log_pass_edt, getString(R.string.login_val_pass))
                false
            }
            else -> true
        }
    }

}






