package dk.eatmore.partner.dashboard.fragment.order


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.BoringLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView

import dk.eatmore.partner.R
import dk.eatmore.partner.R.color.theme_color
import dk.eatmore.partner.dashboard.main.MainActivity
import dk.eatmore.partner.utils.BaseFragment
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_info_order.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.swipe_cart_item.*

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
    private var count: Int = 0
    private var swipeAdapter: OrderInfoFragment.SwipeAdapter?=null
    val cont : OrderInfoFragment = this
    private var mediaPlayer: MediaPlayer?=null


    override fun getLayout(): Int {
        return R.layout.fragment_info_order
    }


    fun showPreogressBar(action : Boolean){
        if(action) progress_bar_toolbarView.visibility =View.VISIBLE
        else progress_bar_toolbarView.visibility =View.GONE
    }



    override fun initView(view: View?, savedInstanceState: Bundle?) {

        LocalBroadcastManager.getInstance(context!!).registerReceiver(mMessageReceiver,
                IntentFilter(SWIPE))
        initToolbar()
        mediaPlayer = MediaPlayer.create(context, R.raw.notification_bell)
        adapter = ViewPagerAdapter(childFragmentManager)
        adapter!!.addFragment(RecordOfToday(), getString(R.string.today))
        adapter!!.addFragment(RecordOfLast7Days(), getString(R.string.last_7_days))
        adapter!!.addFragment(RecordOfLast30Days(), getString(R.string.last_30_days))
        viewpager.offscreenPageLimit=3
        viewpager.setAdapter(adapter)
        tabs.setupWithViewPager(viewpager)
        swipeView()



    }

    /**@param id  (0 == Record of today) || (7 == Record of 7 days) || (30 == Record of 30 days || 100 == Other)
     *
     * */


    fun performedStatusAction(id : Int){

           (adapter!!.mFragmentList.get(0) as RecordOfToday).callbackRefresh()
           (adapter!!.mFragmentList.get(1) as RecordOfLast7Days).callbackRefresh()
           (adapter!!.mFragmentList.get(2) as RecordOfLast30Days).callbackRefresh()
           (activity as MainActivity).orderCounter()



    }

   /* private fun initShimmerEffect() {
        try {
            val shimmer = Shimmer()
            shimmer.setDuration(5200).setStartDelay(2000).start(shimmerText)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }*/


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
                swipeAdapter!!.notifyDataSetChanged()
                mediaPlayer!!.stop()
                performedStatusAction(100)

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
                    c.drawRect(itemView.left.toFloat(), itemView.top.toFloat(), 0f,
                            itemView.bottom.toFloat(), p)
                } else {
                    /* Set your color for negative displacement */

                    // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                    c.drawRect(itemView.right.toFloat() + 0f, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), p)
                }

                super.onChildDraw(null, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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
                val vh: SwipeHolder = holder

                val v :Vibrator  = context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                val shakeanimation = AnimationUtils.loadAnimation(context, R.anim.shake)
                vh.swipe_card_holder.startAnimation(shakeanimation)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE));
                }else{
                    //deprecated in API 26
                    v.vibrate(1000);
                }

                if(count > 0)
                    mediaPlayer!!.start()

                else
                    mediaPlayer!!.stop()




            }
        }

        override fun getItemCount(): Int {
            return count
        }

        inner class SwipeHolder( override val containerView: View?) : RecyclerView.ViewHolder(containerView),LayoutContainer {



            init {
                swipe_card_holder?.setOnClickListener { view ->
                    count = 0
                    swipeAdapter!!.notifyDataSetChanged()
                    mediaPlayer!!.stop()
                    performedStatusAction(100)

                }


            }
        }
    }


    private fun initToolbar() {
        progress_bar_toolbarView.visibility =View.GONE
        txt_toolbar.text = getString(R.string.orders_title)
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

       val SWIPE = "swipe_function"
       var swipeShow : Boolean =false
        val TAG= "OrderInfoFragment"
        fun newInstance() : OrderInfoFragment {
            return OrderInfoFragment()
        }

    }


    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            if (swipeAdapter != null) {
                count = 1
                swipeAdapter!!.notifyDataSetChanged()

            }
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


