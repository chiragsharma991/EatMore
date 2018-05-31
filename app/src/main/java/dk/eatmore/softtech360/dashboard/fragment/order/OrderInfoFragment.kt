package dk.eatmore.softtech360.dashboard.fragment.order


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.BoringLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView

import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.R.color.theme_color
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
    private var count: Int = 1
    private var swipeAdapter: OrderInfoFragment.SwipeAdapter?=null


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
       // swipeView()


    }

    /**@param id  (0 == Record of today) || (7 == Record of 7 days) || (30 == Record of 30 days)
     *
     * */


    fun performedStatusAction(id : Int){

           (adapter!!.mFragmentList.get(0) as RecordOfToday).callbackRefresh()
           (adapter!!.mFragmentList.get(1) as RecordOfLast7Days).callbackRefresh()
           (adapter!!.mFragmentList.get(2) as RecordOfLast30Days).callbackRefresh()



    }


    private fun swipeView(){

        swipe_recycler.layoutManager = LinearLayoutManager(activity)
        swipeAdapter = SwipeAdapter()
        swipe_recycler.adapter = swipeAdapter

        // Swipe interface...
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(swipe_recycler)




    }

    internal var simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            if (swipeAdapter != null) {
                count = 0
                swipeShow = false
                swipeAdapter!!.notifyDataSetChanged()
            }
            //Remove swiped item from list and notify the RecyclerView
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                // Get RecyclerView item from the ViewHolder
                val itemView = viewHolder.itemView

                val p = Paint()
                p.color = ContextCompat.getColor(context!!,R.color.theme_color)
                if (dX > 0) {
                    /* Set your color for positive displacement */

                    // Draw Rect with varying right side, equal to displacement dX
                    c.drawRect(itemView.left.toFloat(), itemView.top.toFloat(), dX,
                            itemView.bottom.toFloat(), p)
                } else {
                    /* Set your color for negative displacement */

                    // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                    c.drawRect(itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), p)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
    }


    private inner class SwipeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val v = LayoutInflater.from(parent.context).inflate(R.layout.swipe_cart_item, parent, false)
            return SwipeHolder(v)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is SwipeHolder) {
                val vh = holder

            }
        }

        override fun getItemCount(): Int {
            return count
        }

        inner class SwipeHolder(v: View) : RecyclerView.ViewHolder(v) {



            init {

                swipe_card?.setOnClickListener { view ->


                }


            }
        }
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
       var swipeShow : Boolean =false
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
