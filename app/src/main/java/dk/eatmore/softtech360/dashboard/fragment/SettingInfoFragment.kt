package dk.eatmore.softtech360.dashboard.fragment

import android.os.Bundle
import android.view.View
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.dashboard.main.MainActivity
import dk.eatmore.softtech360.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_info_order.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class SettingInfoFragment : BaseFragment(){

    override fun getLayout(): Int {
        return R.layout.fragment_settinginfo
    }


    override fun initView(view: View?, savedInstanceState: Bundle?) {
        initToolbar()
    }

    companion object {
        val TAG= "SET"
        fun newInstance() : SettingInfoFragment{
            return  SettingInfoFragment()
        }
    }





    private fun initToolbar() {

        txt_toolbar.text = "Settings"
        img_toolbar_back.setOnClickListener {
            var mainActivity = getActivityBase() as MainActivity
            mainActivity.isOpenDrawer(true)
        }

        /*if(PreferenceUtil.getBoolean(PreferenceUtil.IS_LOGIN, false))
            toolbar.inflateMenu(R.menu.home_menu)*/


    }

    override fun handleBackButton(): Boolean {
        return false

    }

}