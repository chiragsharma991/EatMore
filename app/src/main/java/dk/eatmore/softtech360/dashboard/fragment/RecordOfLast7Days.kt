package dk.eatmore.softtech360.dashboard.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.dashboard.adapter.RecordOfTodayAdapter
import dk.eatmore.softtech360.model.CustomSearchItem
import dk.eatmore.softtech360.model.Order
import dk.eatmore.softtech360.rest.ApiCall
import dk.eatmore.softtech360.storage.PreferenceUtil
import dk.eatmore.softtech360.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_record_of_last7_days.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class RecordOfLast7Days : BaseFragment() {

    var mListOrder = ArrayList<CustomSearchItem?>()
    var mAdapter: RecordOfTodayAdapter? = null
    val refFragment: RecordOfLast7Days = this

    companion object {
        val TAG = "RecordOfLast7Days"
        fun newInstance(): RecordOfLast7Days {
            return RecordOfLast7Days()
        }
    }


    override fun getLayout(): Int {
        return R.layout.fragment_record_of_last7_days
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {

        var r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")!!.replace("\"","")
        var r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")!!.replace("\"","")
        log(RecordOfToday.TAG, r_key.toString() + " " + r_token.toString())
        val currentDate=getCalculatedDate("yyyy-MM-dd",0)
        val last7th=getCalculatedDate("yyyy-MM-dd",-7)


        callAPI(ApiCall.myOrder(currentDate, last7th, r_key, r_token), object : BaseFragment.OnApiCallInteraction {

            override fun <T> onSuccess(body: T?) {
                var list: List<CustomSearchItem> = (body as Order).custom_search
                val mListNewOrder = ArrayList<CustomSearchItem?>()
                val mListAnsweredOrder = ArrayList<CustomSearchItem?>()

                if((body as Order).status){
                    for (i in list.size-1 downTo -1 +1){
                        val item : CustomSearchItem = list.get(i)
                        if (item.order_status == "Pending Restaurant" || item.order_status == "Pending Opening Restaurant") {
                            // new order list
                            if(!chekifAnyHeader(mListNewOrder)){
                                item.showOrderHeader=true
                            }
                            item.headerType="mListNewOrder"
                            mListNewOrder.add(item)
                        } else {
                            // answered order list
                            if(!chekifAnyHeader(mListAnsweredOrder)){
                                item.showOrderHeader=true
                            }
                            item.headerType="mListAnsweredOrder"
                            mListAnsweredOrder.add(item)
                        }
                    }
                    mListOrder.addAll(mListNewOrder)
                    mListOrder.addAll(mListAnsweredOrder)
                    mAdapter = RecordOfTodayAdapter(mListOrder,mListNewOrder,mListAnsweredOrder, refFragment)
                    recycler_view.layoutManager = LinearLayoutManager(getActivityBase())
                    recycler_view.adapter = mAdapter
                }else{

                }




            }

            override fun onFail() {
                showSnackBar(getString(R.string.error_404))
                log(RecordOfLast7Days.TAG, "api call failed...")

            }

        })


    }

    override fun handleBackButton(): Boolean {
        return true
    }

    /***
     * @param id 2 > accept : 1 > Details : 0 > Reject
     */
    fun performAction(id: Int, orderId : String) {

        when (id) {
            0 -> {
            }
            1 -> {
            }
            2 -> {
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        log(RecordOfLast7Days.TAG, "onCreate---")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        log(RecordOfLast7Days.TAG, "onCreateView---")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPause() {
        log(RecordOfLast7Days.TAG, "onPause---")
        super.onPause()
    }

    override fun onStop() {
        log(RecordOfLast7Days.TAG, "onStop---")
        super.onStop()
    }

    override fun onDestroy() {
        log(RecordOfLast7Days.TAG, "onDestroy---")
        super.onDestroy()
    }

    override fun onDestroyView() {
        log(RecordOfLast7Days.TAG, "onDestroyView---")
        super.onDestroyView()
    }

    fun chekifAnyHeader(list : java.util.ArrayList<CustomSearchItem?>) : Boolean{

        for (i in 0..list.size-1){
            if(list.get(i)!!.showOrderHeader==true)
                return true
        }
        return false

    }



}
