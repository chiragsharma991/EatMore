package dk.eatmore.softtech360.dashboard.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import dk.eatmore.softtech360.rest.ApiInterface
import dk.eatmore.softtech360.model.UserDetail_model
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.dashboard.fragment.RecordOfLast30Days
import dk.eatmore.softtech360.dashboard.fragment.RecordOfLast7Days
import dk.eatmore.softtech360.dashboard.fragment.RecordOfToday
import dk.eatmore.softtech360.rest.ApiClient
import dk.eatmore.softtech360.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.support.v4.app.FragmentPagerAdapter



class MainActivity : BaseActivity() {
    private val tag = this.javaClass.simpleName


    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun init(savedInstancedState: Bundle?) {
        setSupportActionBar(toolbar)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(RecordOfToday(), "Today")
        adapter.addFragment(RecordOfLast7Days(), "Last 7 days")
        adapter.addFragment(RecordOfLast30Days(), "Last 30 days")
        viewpager.setAdapter(adapter)
        tabs.setupWithViewPager(viewpager)


    }


    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList.get(position)
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            log(tag,title)
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList.get(position)
        }
    }



}