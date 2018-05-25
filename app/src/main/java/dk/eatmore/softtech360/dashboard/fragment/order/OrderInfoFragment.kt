package dk.eatmore.softtech360.dashboard.fragment.order


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
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


    var adapter: ViewPagerAdapter? = null


    override fun getLayout(): Int {
        return R.layout.fragment_info_order
    }


    fun showPreogressBar(action : Boolean){
        if(action) progress_bar_toolbarView.visibility =View.VISIBLE
        else progress_bar_toolbarView.visibility =View.GONE
    }



    override fun initView(view: View?, savedInstanceState: Bundle?) {
        initToolbar()
        adapter = ViewPagerAdapter(childFragmentManager)
        adapter!!.addFragment(RecordOfToday(), "TODAY")
        adapter!!.addFragment(RecordOfLast7Days(), "LAST 7 DAYS")
        adapter!!.addFragment(RecordOfLast30Days(), "LAST 30 DAYS")
        viewpager.offscreenPageLimit=3
        viewpager.setAdapter(adapter)
        tabs.setupWithViewPager(viewpager)


    }

    /**@param id  (0 == Record of today) || (7 == Record of 7 days) || (30 == Record of 30 days)
     *
     * */


    fun performedStatusAction(id : Int){

           (adapter!!.mFragmentList.get(0) as RecordOfToday).callbackRefresh()
           (adapter!!.mFragmentList.get(1) as RecordOfLast7Days).callbackRefresh()
           (adapter!!.mFragmentList.get(2) as RecordOfLast30Days).callbackRefresh()



    }




    private fun initToolbar() {
        progress_bar_toolbarView.visibility =View.GONE
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
        fun newInstance() : OrderInfoFragment {
            return OrderInfoFragment()
        }

    }


    inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
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
