package dk.eatmore.softtech360.dashboard.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
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
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.JsonObject
import dk.eatmore.softtech360.R.string.phone
import dk.eatmore.softtech360.storage.PreferenceUtil
import dk.eatmore.softtech360.utils.DialogUtils
import dk.eatmore.softtech360.activity.LoginActivity
import dk.eatmore.softtech360.dashboard.fragment.order.OrderDetails
import dk.eatmore.softtech360.dashboard.fragment.order.OrderInfoFragment
import dk.eatmore.softtech360.dashboard.fragment.setting.SettingInfoFragment
import dk.eatmore.softtech360.fcm.FirebaseInstanceIDService
import dk.eatmore.softtech360.rest.ApiCall
import dk.eatmore.softtech360.rest.ApiClient
import dk.eatmore.softtech360.rest.ApiInterface
import dk.eatmore.softtech360.utils.BaseFragment
import dk.eatmore.softtech360.utils.Custom_data
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_info_order.*
import kotlinx.android.synthetic.main.fragment_settinginfo.*
import kotlinx.android.synthetic.main.layout_progressbar.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : BaseActivity(), View.OnClickListener  {




    var orderInfoFragment = OrderInfoFragment.newInstance()
    private var r_key: String =""
    private var r_token: String =""
    private var restaurant_name: String =""
    private var user_name: String =""


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
        r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")!!
        r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")!!
        restaurant_name = PreferenceUtil.getString(PreferenceUtil.RESTAURANT_NAME, "")!!
        user_name = PreferenceUtil.getString(PreferenceUtil.USER_NAME, "")!!
        val refreshedToken = PreferenceUtil.getString(PreferenceUtil.TOKEN, "")!!

        nav_txt_name.text=user_name
        nav_txt_rest.text=restaurant_name

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
        orderCounter()
        drawer_layout?.addDrawerListener(mDrawerToggle)
    }


    private fun sendfcmtocken() {

        if(!FirebaseInstanceIDService.shallIsendToken) return
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

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {

                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + OrderDetails.phone.trim({ it <= ' ' })))
                    startActivity(intent)
                //    Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                }
                return
            }
        }

    }

      fun orderCounter(){

        callAPI( ApiCall.orderCounter(r_key,r_token),object : BaseFragment.OnApiCallInteraction{

            override fun <T> onSuccess(body: T?) {
                val json= body as OrderCounter  // please be mind you are using jsonobject(Gson)

                if (json.status){
                    log(TAG, "status is"+json.status)
                    for (i in 0..json.order_counter!!.size-1){
                        val obj=json.order_counter.get(i)
                        when (i){
                            0 ->{
                                //nav_last_0_lbl.text = obj.label+" (${ obj.no_of_orders} orders)"
                                nav_last_0_txt.text = obj.total_sales+" kr"
                            }
                            1 ->{
                              //  nav_last_7_lbl.text = obj.label+" (${ obj.no_of_orders} orders)"
                                nav_last_7_txt.text = obj.total_sales+" kr"
                            }
                            2 ->{
                                //nav_last_30_lbl.text = obj.label+" (${ obj.no_of_orders} orders)"
                                nav_last_30_txt.text = obj.total_sales+" kr"
                            }
                        }
                    }
                }
            }

            override fun onFail(error : Int) {
                log(TAG,"error "+error)
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
            val fragment=this.supportFragmentManager.findFragmentById(R.id.main_container_layout)
            if(fragment  is OrderInfoFragment ){
                val selectedPosition=fragment.tabs.selectedTabPosition
                if(selectedPosition !=0)
                fragment.tabs.getTabAt(0)!!.select()

            }else if (fragment  is OrderDetails){
                popWithTag(TAG)

            }
            else if (fragment  is SettingInfoFragment){
                if(fragment.set_general_view.visibility == View.VISIBLE) {
                    popWithTag(TAG)
                    nav_main_order.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                    nav_main_setting.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
                    nav_main_logout.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
                }
                else
                {
                    fragment.toolbar.menu.clear()
                    fragment.img_toolbar_back.setImageResource(R.drawable.ic_menu)
                    fragment.txt_toolbar.text = getString(R.string.settings)
                    fragment.set_reason_view.visibility = View.GONE
                    fragment.set_general_view.visibility = View.VISIBLE
                    fragment.progress_bar.visibility = View.GONE
                }


            }
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


data class OrderCounter(val order_counter: List<OrderCounterItem>?,
                        val status: Boolean = false)



data class OrderCounterItem(val label: String = "",
                            val total_sales: String = "",
                            val no_of_orders: String = "")



