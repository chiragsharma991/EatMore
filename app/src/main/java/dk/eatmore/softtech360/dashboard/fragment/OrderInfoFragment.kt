package dk.eatmore.softtech360.dashboard.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.view.View

import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.dashboard.main.MainActivity
import dk.eatmore.softtech360.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_info_order.*
import kotlinx.android.synthetic.main.layout_toolbar.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class OrderInfoFragment : BaseFragment()  {


    override fun getLayout(): Int {
        return R.layout.fragment_info_order
    }


    fun orderInfo(){
        log("order info","----")
    }


    override fun initView(view: View?, savedInstanceState: Bundle?) {
        initToolbar()
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(RecordOfToday(), "TODAY")
        adapter.addFragment(RecordOfLast7Days(), "LAST 7 DAYS")
        adapter.addFragment(RecordOfLast30Days(), "LAST 30 DAYS")
        viewpager.offscreenPageLimit=3
        viewpager.setAdapter(adapter)
        tabs.setupWithViewPager(viewpager)

        (adapter.mFragmentList.get(0) as RecordOfToday).callback()

    }




    private fun initToolbar() {

        txt_toolbar.text = "My Orders"
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

    companion object {
        val TAG= "OrderInfoFragment"
        fun newInstance() : OrderInfoFragment{
            return  OrderInfoFragment()
        }

    }


    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
         val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList.get(position)
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList.get(position)
        }
    }




}
