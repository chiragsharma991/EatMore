package dk.eatmore.softtech360.dashboard.main

import android.os.Bundle
import android.view.View
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import dk.eatmore.softtech360.dashboard.fragment.*
import dk.eatmore.softtech360.storage.PreferenceUtil
import dk.eatmore.softtech360.utils.DialogUtils


class MainActivity : BaseActivity() {



    var orderInfoFragment = OrderInfoFragment.newInstance()



    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    companion object {
        val TAG ="MainActivity"
        fun newInstance(): MainActivity{
            return MainActivity()
        }
    }

    override fun init(savedInstancedState: Bundle?) {
        supportFragmentManager.beginTransaction().replace(R.id.main_container_layout, orderInfoFragment, OrderInfoFragment.TAG).commit()

        var mDrawerToggle = object : ActionBarDrawerToggle(this, drawer_layout, null, R.string.app_name, R.string.app_name) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                log(TAG, ""+slideOffset)
                main_container_layout.translationX = slideOffset * 500
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