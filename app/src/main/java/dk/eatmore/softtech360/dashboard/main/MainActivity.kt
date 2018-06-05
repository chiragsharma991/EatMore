package dk.eatmore.softtech360.dashboard.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.WindowManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.JsonObject
import dk.eatmore.softtech360.storage.PreferenceUtil
import dk.eatmore.softtech360.utils.DialogUtils
import dk.eatmore.softtech360.activity.LoginActivity
import dk.eatmore.softtech360.dashboard.fragment.order.OrderInfoFragment
import dk.eatmore.softtech360.dashboard.fragment.setting.SettingInfoFragment
import dk.eatmore.softtech360.fcm.FirebaseInstanceIDService
import dk.eatmore.softtech360.rest.ApiClient
import dk.eatmore.softtech360.rest.ApiInterface
import dk.eatmore.softtech360.utils.Custom_data
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : BaseActivity(), View.OnClickListener  {




    var orderInfoFragment = OrderInfoFragment.newInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //   ButterKnife.bind(this@MainActivity)
        //  val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        if (PreferenceUtil.getString(PreferenceUtil.USER_NAME, "") != "") init(savedInstanceState)
        else{
            startActivity(Intent(this@MainActivity, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }



    }

    fun keepScreenOn(result : Boolean){
        log(TAG,"result ---"+result)

        if(result)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


    }

    override fun onStart() {
        super.onStart()
        log(TAG,"on start---")
        if (PreferenceUtil.getBoolean(PreferenceUtil.KEEP_SCREEN_ON, false)){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }


    }

    override fun onPause() {
        super.onPause()
        log(TAG,"on onPause---")
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    companion object {
        val TAG = "MainActivity"
        fun newInstance(): MainActivity {
            return MainActivity()
        }
    }


    fun init(savedInstancedState: Bundle?) {
        nav_main_order.setOnClickListener(this)
        nav_main_setting.setOnClickListener(this)
        nav_main_logout.setOnClickListener(this)
        val refreshedToken = PreferenceUtil.getString(PreferenceUtil.TOKEN, "")!!

        log(TAG,"refreshedToken= "+refreshedToken)

        supportFragmentManager.beginTransaction().replace(R.id.main_container_layout, orderInfoFragment, OrderInfoFragment.TAG).addToBackStack(TAG).commit()
        var mDrawerToggle = object : ActionBarDrawerToggle(this, drawer_layout, null, R.string.app_name, R.string.app_name) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                main_container_layout.translationX = slideOffset * 100
                drawer_layout?.bringChildToFront(drawerView)
                drawer_layout?.requestLayout()
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                hideKeyboard()
            }
        }
        sendfcmtocken()
        drawer_layout?.addDrawerListener(mDrawerToggle)
    }

    private fun sendfcmtocken() {

        if(!FirebaseInstanceIDService.shallIsendToken) return
        val r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")!!
        val r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")!!
        val user_id = PreferenceUtil.getString(PreferenceUtil.USER_ID, "")!!
        val refreshedToken = PreferenceUtil.getString(PreferenceUtil.TOKEN, "")!!
        val apiService = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val call = apiService.sendFcmToken(r_token = r_token,r_key = r_key,token = refreshedToken,device_type = "POS",user_id = user_id)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e(TAG,response.toString())
                FirebaseInstanceIDService.shallIsendToken =false
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                // Log error here since request failed
                Log.e(TAG, t.toString())
            }
        })


    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.nav_main_order -> {
                nav_main_order.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                nav_main_setting.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
                nav_main_logout.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
                isOpenDrawer(false)
                Handler().postDelayed({ onClickDrawer(0) }, 300)

            }
            R.id.nav_main_setting -> {
                nav_main_order.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
                nav_main_setting.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                nav_main_logout.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
                isOpenDrawer(false)
                Handler().postDelayed({ onClickDrawer(1) }, 300)


            }
            R.id.nav_main_logout -> {
                isOpenDrawer(false)
                Handler().postDelayed({ onClickDrawer(2) }, 300)

            }

        }
    }


    private fun onClickDrawer(position: Int) {
        when (position) {
            0 -> {
                popWithTag(TAG)
            }
            1 -> {
                addFragment(R.id.main_container_layout, SettingInfoFragment.newInstance(), SettingInfoFragment.TAG)
            }
            2 -> logOut()

        }
    }


    private fun logOut() {
        DialogUtils.openDialog(this, getString(R.string.are_you_sure_logout__),
                getString(R.string.logout), getString(R.string.cancel), object : DialogUtils.OnDialogClickListener {
            override fun onPositiveButtonClick(position: Int) {
                popAllFragment()
                PreferenceUtil.remove(PreferenceUtil.R_KEY)
                PreferenceUtil.remove(PreferenceUtil.USER_NAME)
                PreferenceUtil.remove(PreferenceUtil.R_TOKEN)
                PreferenceUtil.save()
                Custom_data.setWalkLock(false,this@MainActivity)
                startActivity(Intent(this@MainActivity, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()

            }

            override fun onNegativeButtonClick() {

            }
        })
    }


    fun isOpenDrawer(isOpen: Boolean) {
        if (isOpen) drawer_layout!!.openDrawer(GravityCompat.START) else drawer_layout!!.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
            drawer_layout!!.closeDrawer(GravityCompat.START)
        } else {
            /*  var pop = popFragment()
              if (!pop) {
                  if (doubleTapExit) {
                      finish()
                      overridePendingTransition(0, android.R.anim.fade_out)
                  } else {
                      doubleTapExit = true
                      showToast(getString(R.string.confirm_exit))
                      Handler().postDelayed({ doubleTapExit = false }, 2000)
                  }
              }*/
        }
    }

    private fun cherAllData() {


        /*     DialogUtils.openDialog(this, getString(R.string.are_you_sure_logout),
                     getString(R.string.logout), getString(R.string.cancel), object : DialogUtils.OnDialogClickListener {
                 override fun onPositiveButtonClick() {
                     popAllFragment()
                     PreferenceUtil.remove(PreferenceUtil.IS_LOGIN)
                     PreferenceUtil.remove(PreferenceUtil.USER_ID)
                     PreferenceUtil.remove(PreferenceUtil.USER_TYPE)
                     PreferenceUtil.remove(PreferenceUtil.PROFILE_URL)
                     PreferenceUtil.save()
                 }

                 override fun onNegativeButtonClick() {

                 }
             })*/
    }


}

interface test {
    //  100 > network not foune  : 404 > server error.
    fun onTest(error : Int)
}
