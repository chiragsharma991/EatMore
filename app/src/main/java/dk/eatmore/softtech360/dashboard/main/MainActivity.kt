package dk.eatmore.softtech360.dashboard.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.View
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import dk.eatmore.softtech360.storage.PreferenceUtil
import dk.eatmore.softtech360.utils.DialogUtils
import dk.eatmore.softtech360.activity.LoginActivity
import dk.eatmore.softtech360.dashboard.fragment.order.OrderInfoFragment
import dk.eatmore.softtech360.dashboard.fragment.setting.SettingInfoFragment


class MainActivity : BaseActivity(), View.OnClickListener  {





    var orderInfoFragment = OrderInfoFragment.newInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
     //   ButterKnife.bind(this@MainActivity)
        //  val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        init(savedInstanceState)




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
        drawer_layout?.addDrawerListener(mDrawerToggle)
    }

    override fun onClick(v: View?) {

        when(v!!.id){
            R.id.nav_main_order ->{
                nav_main_order.setBackgroundColor(ContextCompat.getColor(this,R.color.green))
                nav_main_setting.setBackgroundColor(ContextCompat.getColor(this,R.color.black))
                nav_main_logout.setBackgroundColor(ContextCompat.getColor(this,R.color.black))
                isOpenDrawer(false)
                Handler().postDelayed({ onClickDrawer(0)},300)

            }
            R.id.nav_main_setting ->{
                nav_main_order.setBackgroundColor(ContextCompat.getColor(this,R.color.black))
                nav_main_setting.setBackgroundColor(ContextCompat.getColor(this,R.color.green))
                nav_main_logout.setBackgroundColor(ContextCompat.getColor(this,R.color.black))
                isOpenDrawer(false)
                Handler().postDelayed({ onClickDrawer(1)},300)


            }
            R.id.nav_main_logout ->{
                isOpenDrawer(false)
                Handler().postDelayed({ onClickDrawer(2)},300)

            }

        }
    }




    private fun onClickDrawer(position : Int){
        when(position){
            0 ->{
                popWithTag(TAG)
            }
            1 ->{
                addFragment(R.id.main_container_layout, SettingInfoFragment.newInstance(), SettingInfoFragment.TAG)
            }
            2 -> logOut()

        }
    }


    private fun logOut() {
        DialogUtils.openDialog(this, getString(R.string.are_you_sure_logout__),
                getString(R.string.logout), getString(R.string.cancel), object : DialogUtils.OnDialogClickListener {
            override fun onPositiveButtonClick(position : Int) {
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