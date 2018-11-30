package dk.eatmore.partner.dashboard.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.View
import dk.eatmore.partner.R
import dk.eatmore.partner.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import dk.eatmore.partner.storage.PreferenceUtil
import dk.eatmore.partner.activity.LoginActivity
import dk.eatmore.partner.dashboard.fragment.order.OrderDetails
import dk.eatmore.partner.dashboard.fragment.order.OrderInfoFragment
import dk.eatmore.partner.dashboard.fragment.setting.AddPrinter
import dk.eatmore.partner.dashboard.fragment.setting.SettingInfoFragment
import dk.eatmore.partner.fcm.FirebaseInstanceIDService
import dk.eatmore.partner.model.ModelUtility
import dk.eatmore.partner.rest.ApiCall
import dk.eatmore.partner.rest.ApiClient
import dk.eatmore.partner.rest.ApiInterface
import dk.eatmore.partner.utils.BaseFragment
import dk.eatmore.partner.utils.Constants
import dk.eatmore.partner.utils.DialogUtils
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

    companion object {
        val TAG = "MainActivity"
        public var kShouldAllowCloseRestDay :Boolean=true
        fun newInstance(): MainActivity {
            return MainActivity()
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
        closedRestaurant()
        drawer_layout?.addDrawerListener(mDrawerToggle)

        //set version code/name
        var info: PackageInfo? = null
        val manager = getPackageManager()
        info = manager.getPackageInfo(getPackageName(), 0)
        version_name.setText("Version " + info!!.versionName + "." + info!!.versionCode)
    }


    private fun sendfcmtocken() {

        if(!FirebaseInstanceIDService.shallIsendToken) return
        val user_id = PreferenceUtil.getString(PreferenceUtil.USER_ID, "")!!
        val refreshedToken = PreferenceUtil.getString(PreferenceUtil.TOKEN, "")!!
        val apiService = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val call = apiService.sendFcmToken(r_token = r_token,r_key = r_key,token = refreshedToken,device_type = "POS",user_id = user_id,app = Constants.RESTAURANT_APP_ANDROID)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                log("response.body----",response.body().toString())
                val gson= Gson()
                val json=gson.toJson(response.body()) // convert body to normal json
                var convertedObject = gson.fromJson(json, JsonObject::class.java) // convert into Jsonobject
                log("response.convertedObject----",convertedObject.toString())

                if(convertedObject.has(Constants.WHOLE_SYSTEM)){
                    if(convertedObject.get(Constants.WHOLE_SYSTEM).isJsonNull){
                        FirebaseInstanceIDService.shallIsendToken =false
                    }else{
                        if((convertedObject.get(Constants.WHOLE_SYSTEM).asBoolean== true) || (convertedObject.get(Constants.RESTAURANT_APP_ANDROID).asBoolean== true) ){
                            val intent = Intent(this@MainActivity, RestaurantClosed::class.java)
                            val bundle = Bundle()
                            bundle.putString(Constants.MESSAGE_TITLE,convertedObject.get(Constants.MESSAGE_TITLE).asString)
                            bundle.putString(Constants.MESSAGE_DETAILS,convertedObject.get(Constants.MESSAGE_DETAILS).asString)
                            intent.putExtras(bundle)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }else{
                            FirebaseInstanceIDService.shallIsendToken =false
                        }
                    }
                }else{
                    FirebaseInstanceIDService.shallIsendToken =false
                }

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
                                nav_last_0_lbl.text = getString(R.string.nav_today )+" - ${ obj.no_of_orders} ${getString(R.string.orders)}"
                                nav_last_0_txt.text = obj.total_sales+" kr"
                            }
                            1 ->{
                                nav_last_7_lbl.text = getString(R.string.nav_last7 )+" - ${ obj.no_of_orders} ${getString(R.string.orders)}"
                                nav_last_7_txt.text = obj.total_sales+" kr"
                            }
                            2 ->{
                                nav_last_30_lbl.text = getString(R.string.nav_30days )+" - ${ obj.no_of_orders} ${getString(R.string.orders)}"
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

    fun closedRestaurant(){

        callAPI( ApiCall.closedRestaurant(r_key,r_token),object : BaseFragment.OnApiCallInteraction{

            override fun <T> onSuccess(body: T?) {
                val json= body as JsonObject  // please be mind you are using jsonobject(Gson)

                if (json.get("status").asBoolean && json.get("is_restaurant_closed").asBoolean) {

                    if(json.has("pre_order")){

                        if(json.get("pre_order").asBoolean) kShouldAllowCloseRestDay=true
                        else kShouldAllowCloseRestDay=false

                    }else{
                        kShouldAllowCloseRestDay=false

                    }

                }else{
                    kShouldAllowCloseRestDay=true  // show close restDay label
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
                nav_main_order.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                nav_main_setting.setBackgroundColor(0)
                nav_main_logout.setBackgroundColor(0)
                isOpenDrawer(false)
                Handler().postDelayed({ onClickDrawer(0) }, 300)

            }
            R.id.nav_main_setting -> {
                nav_main_order.setBackgroundColor(0)
                nav_main_setting.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                nav_main_logout.setBackgroundColor(0)
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
                addFragment(R.id.main_container_layout, SettingInfoFragment.newInstance(), SettingInfoFragment.TAG,false)
            }
            2 -> logOut()

        }
    }


    private fun logOut() {
        DialogUtils.openDialog(this,getString(R.string.are_you_sure_logout__),getString(R.string.logout),
                getString(R.string.logout), getString(R.string.cancel),ContextCompat.getColor(this,R.color.theme_color), object : DialogUtils.OnDialogClickListener {
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
                fragment.onbackPress()
            }
            else if (fragment  is SettingInfoFragment){

                // check if Added printer fragment is open/close
                if(!fragment.handleBackButton()){
                    if(fragment.set_general_view.visibility == View.VISIBLE) {
                        popWithTag(TAG)
                        nav_main_order.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
                        nav_main_setting.setBackgroundColor(0)
                        nav_main_logout.setBackgroundColor(0)
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
            }
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
                        val status: Boolean = false): ModelUtility()



data class OrderCounterItem(val label: String = "",
                            val total_sales: String = "",
                            val no_of_orders: String = "")



