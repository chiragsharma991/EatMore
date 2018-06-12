package dk.eatmore.partner.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v7.widget.AppCompatButton
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import dk.eatmore.partner.R
import dk.eatmore.partner.dashboard.fragment.order.OrderDetails
import dk.eatmore.partner.dashboard.main.MainActivity
import dk.eatmore.partner.rest.ApiCall
import dk.eatmore.partner.rest.ApiClient
import dk.eatmore.partner.storage.PreferenceUtil
import dk.eatmore.partner.utils.AppLifecycleHandler
import dk.eatmore.partner.utils.BaseActivity
import dk.eatmore.partner.utils.BaseFragment
import dk.eatmore.partner.utils.Custom_data
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.layout_progressbar.*
import org.json.JSONObject
import java.util.regex.Pattern


class LoginActivity : BaseActivity(){



    private var edit: EditText? = null

    private var dialog: AlertDialog? =null


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


    @SuppressLint("MissingPermission")
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

         log_service_call_view.setOnClickListener{

             dialog = AlertDialog.Builder(this).setMessage("Do you want to call +45 88 82 65 43").setCancelable(true).setPositiveButton("yes") { dialogInterface, i ->
                 if(isPermissionGranted()){
                     val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "4588826543"))
                     startActivity(intent)
                 }
             }.setNegativeButton("no") { dialogInterface, i -> dialog!!.dismiss() }.show()
         }


    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {

                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "4588826543"))
                    startActivity(intent)
                  //  Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                }
                return
            }
        }

    }

    private fun loginAttempt() {

        progress_bar.visibility=View.VISIBLE
        callAPI(ApiCall.login(log_email_edt.text.toString(),log_pass_edt.text.toString(),"POS","Owner"),object : BaseFragment.OnApiCallInteraction{

            override fun <T> onSuccess(body: T?) {
                progress_bar.visibility= View.GONE
                val json= body as JsonObject  // please be mind you are using jsonobject(Gson)
                if (json.get("status").asBoolean){
                    showSnackBar(log_email_edt, json.get("msg").asString)
                    PreferenceUtil.putValue(PreferenceUtil.USER_NAME, json.getAsJsonObject("user_details").get("first_name").asString+" "+json.getAsJsonObject("user_details").get("last_name").asString)
                    PreferenceUtil.putValue(PreferenceUtil.USER_ID, json.getAsJsonObject("user_details").get("id").asString)
                    PreferenceUtil.putValue(PreferenceUtil.R_KEY, ""+json.get("r_key").asString)
                    PreferenceUtil.putValue(PreferenceUtil.R_TOKEN, ""+json.get("r_token").asString)
                    PreferenceUtil.putValue(PreferenceUtil.RESTAURANT_NAME, ""+json.getAsJsonObject("user_details").getAsJsonArray("restaurant_details").get(0).asJsonObject.get("restaurant_name").asString)
                    PreferenceUtil.putValue(PreferenceUtil.KEEP_SCREEN_ON, true)  // default wakeLock should be ON
                    PreferenceUtil.putValue(PreferenceUtil.KSTATUS, true)  // show close restaurant
                    PreferenceUtil.save()
                 //   Custom_data.setWalkLock(true,this@LoginActivity)
                    moveToDashboard()
                }else{
                    showSnackBar(log_email_edt, json.get("error").asString)
                }
            }

            override fun onFail(error : Int) {

                when(error){
                    404 ->{
                        progress_bar.visibility= View.GONE
                        showSnackBar(log_email_edt,getString(R.string.error_404))
                    }
                    100 ->{
                        progress_bar.visibility= View.GONE

                        showSnackBar(log_email_edt,getString(R.string.internet_not_available))
                    }
                }
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






