package dk.eatmore.partner.dashboard.fragment.order


import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epson.epos2.printer.ReceiveListener
import com.google.gson.JsonObject
import dk.eatmore.partner.R
import dk.eatmore.partner.dashboard.adapter.RecordOfTodayAdapter
import dk.eatmore.partner.dashboard.main.MainActivity
import dk.eatmore.partner.fcm.FirebaseMessagingService
import dk.eatmore.partner.model.*
import dk.eatmore.partner.rest.ApiCall
import dk.eatmore.partner.storage.PreferenceUtil
import dk.eatmore.partner.utils.BaseFragment
import dk.eatmore.partner.utils.ConversionUtils
import dk.eatmore.partner.utils.DialogUtils
import dk.eatmore.partner.utils.DialogUtils.createListDialog
import dk.eatmore.partner.utils.NotificationUtil
import kotlinx.android.synthetic.main.fragment_record_of_today.*
import kotlinx.android.synthetic.main.layout_empty.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class RecordOfToday : Printercommand(), SwipeRefreshLayout.OnRefreshListener, ReceiveListener {

    val mListOrder = ArrayList<CustomSearchItem?>()
    val mListNewOrder = ArrayList<CustomSearchItem?>()
    val mListAnsweredOrder = ArrayList<CustomSearchItem?>()
    var r_key = ""
    var r_token = ""
    var mAdapter: RecordOfTodayAdapter? = null
    val refFragment: RecordOfToday = this
    private var dialog: AlertDialog? = null


    companion object {
        val TAG = "RecordOfToday"
        fun newInstance(): RecordOfToday {
            return RecordOfToday()
        }
    }


    fun callbackRefresh() {

        //NotificationUtil.fireNotification(context = activity!!.applicationContext ,channelId = FirebaseMessagingService.CHANEL_ID,channelName = FirebaseMessagingService.CHANEL_NAME,title = "Title",message = "Message")

        fetchOrders(false)


    }


    /***
     * @param id 2 > accept || 1 > Details || 0 > Reject ||
     */

    fun performAction(id: Int, model: CustomSearchItem) {

        when (id) {
            0 -> {
                rejectOrder(id, model)
            }
            1 -> {
                //                addFragment(R.id.main_container_layout, SettingInfoFragment.newInstance(), SettingInfoFragment.TAG)
          //  (getActivityBase() as MainActivity).addFragment(R.id.main_container_layout, OrderDetails.newInstance(),OrderDetails.TAG)
            }
            2 -> {
                acceptOrder(id, model)
            }

        }

    }



    fun acceptOrder(id: Int, model: CustomSearchItem) {
        // Create Alert dialog to select  Time slot.
        var timeIntervel = 0
        var expectedTime = ConversionUtils.getCalculatedTime(model.expected_time, "yyyy-MM-dd HH:mm:ss")
        // after getting expected time like in long form : make 9 list with that
        val list = ArrayList<String>()
        for (i in 0..8) {
            val mTime = expectedTime + timeIntervel * 60 * 1000
            val time = SimpleDateFormat("HH:mm").format(mTime)
            list.add(time + " ($timeIntervel Min)")
            timeIntervel += 15
        }
        val borderColor = ContextCompat.getColor(activity!!, R.color.green)


        createListDialog("${getString(R.string.expected_time)} ${ConversionUtils.getDateformat(model.expected_time, SimpleDateFormat("HH:mm"), "yyyy-MM-dd HH:mm:ss")}",
                activity, list, borderColor, object : DialogUtils.OnDialogClickListener {
            override fun onNegativeButtonClick() {
            }

            override fun onPositiveButtonClick(position: Int) {
                // Calculate which time slot has been selected and reformat the date and post :)
                var expectedTime = ConversionUtils.getCalculatedTime(model.expected_time, "yyyy-MM-dd HH:mm:ss")
                val mTime = expectedTime + position * 15 * 60 * 1000
                val time = SimpleDateFormat("HH:mm:ss").format(mTime)
                val date = SimpleDateFormat("yyyy-MM-dd").format(expectedTime)
                val pickup_delivery_time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ConversionUtils.getCalculatedTime(date + " " + time, "yyyy-MM-dd HH:mm:ss"))

                callAPI(ApiCall.acceptOrders(r_key = r_key, r_token = r_token,
                        order_no = model.order_id, pickup_delivery_time = pickup_delivery_time, order_status = "accepted"),
                        object : BaseFragment.OnApiCallInteraction {

                            override fun <T> onSuccess(body: T?) {
                                val json = body as JsonObject  // please be mind you are using jsonobject(Gson)
                                if (json.get("status").asBoolean) {
                                    val result = json.getAsJsonObject("data").get("order_status").asString + " " + json.get("msg").asString
                                 //   if(userVisibleHint) showSnackBar(result)
                                    showSnackBar(getString(R.string.order_accept))
                                    (parentFragment as OrderInfoFragment).performedStatusAction(0)
                                    // this condition is from details screen only (just finish fragment)
                                    val fragment = (parentFragment as OrderInfoFragment).fragmentManager?.findFragmentByTag(OrderDetails.TAG)
                                    if(fragment !=null){
                                        (getActivityBase() as MainActivity).pop()
                                        log(TAG,"check----"+model.order_status.capitalize().toUpperCase())
                                        fetchOrderDetails(r_key,r_token,model.order_id,true)
                                    }else{
                                        // call print
                                        log(TAG,"check----"+model.order_status.capitalize().toUpperCase())
                                        fetchOrderDetails(r_key,r_token,model.order_id,true)
                                    }

                                }
                            }

                            override fun onFail(error: Int) {
                                when (error) {
                                    404 -> {
                                     //   if(userVisibleHint)
                                        showSnackBar(getString(R.string.error_404))
                                        log(RecordOfLast30Days.TAG, "api call failed...")
                                    }
                                    100 -> {
                                     //   if(userVisibleHint)
                                        showSnackBar(getString(R.string.internet_not_available))
                                    }
                                }
                            }


                        })
            }

        })

    }

    fun rejectOrder(id: Int, model: CustomSearchItem) {

        callAPI(ApiCall.allRecords(r_key = r_key, r_token = r_token), object : BaseFragment.OnApiCallInteraction {
            override fun <T> onSuccess(body: T?) {
                // all list of reasons.
                if ((body as GetReason).status) {
                    var list: List<OpeninghoursItem> = (body as GetReason).Openinghours!!
                    val listOfreason = ArrayList<String>()
                    for (i in 0..list.size - 1) {
                        listOfreason.add(list.get(i).reason)
                    }
                    // Create Alert dialog to show reasons.
                    val borderColor = ContextCompat.getColor(activity!!, R.color.theme_color)
                    createListDialog(getString(R.string.select_reason), activity, listOfreason, borderColor, object : DialogUtils.OnDialogClickListener {
                        override fun onNegativeButtonClick() {
                        }

                        override fun onPositiveButtonClick(position: Int) {
                            // submit reason and reject the order.
                            log("or id ", list.get(position).or_id)
                            callAPI(ApiCall.rejectOrders(r_key = r_key, r_token = r_token, order_no = model.order_id, reason = list.get(position).or_id, order_status = "rejected"), object : BaseFragment.OnApiCallInteraction {
                                override fun <T> onSuccess(body: T?) {
                                    val json = body as JsonObject  // please be mind you are using jsonobject(Gson)
                                    if (json.get("status").asBoolean) {
                                        val result = json.getAsJsonObject("data").get("order_status").asString + " " + json.get("msg").asString
                                     //   if(userVisibleHint)
                                        showSnackBar(getString(R.string.order_reject))
                                        (parentFragment as OrderInfoFragment).performedStatusAction(0)


                                        // this condition is from details screen only (just finish fragment)
                                        val fragment = (parentFragment as OrderInfoFragment).fragmentManager?.findFragmentByTag(OrderDetails.TAG)
                                        if(fragment !=null){
                                            (getActivityBase() as MainActivity).pop()
                                            fetchOrderDetails(r_key,r_token,model.order_id,false)
                                        }else{
                                            fetchOrderDetails(r_key,r_token,model.order_id,false)
                                        }

                                    }
                                }

                                override fun onFail(error: Int) {
                                }
                            })
                        }

                    })

                }
            }


            override fun onFail(error: Int) {
                when (error) {
                    404 -> {
                     //   if(userVisibleHint)
                        showSnackBar(getString(R.string.error_404))
                        log(RecordOfLast30Days.TAG, "api call failed...")
                    }
                    100 -> {
                      //  if(userVisibleHint)
                        showSnackBar(getString(R.string.internet_not_available))
                    }
                }

            }
        })

    }


    override fun getLayout(): Int {
        return R.layout.fragment_record_of_today
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")!!
        r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")!!
        var user = PreferenceUtil.getString(PreferenceUtil.USER_NAME, "")
        view_empty.visibility = View.GONE
        recycler_view_0.visibility = View.VISIBLE
        swipeRefresh.setOnRefreshListener(this)
        swipeRefresh.setColorSchemeColors(ContextCompat.getColor(context!!,R.color.theme_color))
        /*swipeRefresh.post(Runnable {
            swipeRefresh.setRefreshing(true)
            fetchOrders(true)
        }
        )*/

        log(TAG, r_key.toString() + " " + r_token.toString() + " " + user)
        val currentDate = getCalculatedDate("yyyy-MM-dd", 0)
        fetchOrders(true)


    }



    override fun onRefresh() {
        view_empty.visibility = View.GONE
        recycler_view_0.visibility = View.VISIBLE
        (parentFragment as OrderInfoFragment).performedStatusAction(0)

    }

    private fun fetchOrders(setAdapter : Boolean) {

        view_empty.visibility = View.GONE
        (parentFragment as OrderInfoFragment).showPreogressBar(true)
        val currentDate = getCalculatedDate("yyyy-MM-dd", 0)
        callAPI(ApiCall.myOrder(currentDate, currentDate, r_key!!, r_token!!), object : BaseFragment.OnApiCallInteraction {

            override fun <T> onSuccess(body: T?) {

                if ((body as Order).status) {
                    mListNewOrder.clear()
                    mListAnsweredOrder.clear()
                    mListOrder.clear()
                    var list: List<CustomSearchItem> = (body as Order).custom_search
                    for (i in list.size - 1 downTo -1 + 1) {
                        val item: CustomSearchItem = list.get(i)
                        if (item.order_status == "Pending Restaurant" || item.order_status == "Pending Opening Restaurant") {
                            // new order list
                            if (!chekifAnyHeader(mListNewOrder)) {
                                item.showOrderHeader = true
                            }
                            item.headerType = "mListNewOrder"
                            mListNewOrder.add(item)
                        } else {
                            // answered order list
                            if (!chekifAnyHeader(mListAnsweredOrder)) {
                                item.showOrderHeader = true
                            }
                            item.headerType = "mListAnsweredOrder"
                            mListAnsweredOrder.add(item)
                        }
                    }
                    mListOrder.addAll(mListNewOrder)
                    mListOrder.addAll(mListAnsweredOrder)
                    // if adapter is true then list should be set.
                    if(setAdapter){
                        mAdapter = RecordOfTodayAdapter(mListOrder, mListNewOrder, mListAnsweredOrder, refFragment,context!!,object : RecordOfTodayAdapter.AdapterListener{
                            override fun printOn(position: Int) {
                                fetchOrderDetails(r_key,r_token,mListOrder[position]!!.order_id,mListOrder[position]!!.order_status.capitalize().toUpperCase()=="ACCEPTED")
                            }
                        })
                        recycler_view_0.layoutManager = LinearLayoutManager(getActivityBase())
                        recycler_view_0.adapter = mAdapter
                    }else{
                        if(mAdapter != null){
                            mAdapter!!.notifyDataSetChanged()
                        }else{
                            mAdapter = RecordOfTodayAdapter(mListOrder, mListNewOrder, mListAnsweredOrder, refFragment,context!!,object : RecordOfTodayAdapter.AdapterListener{
                                override fun printOn(position: Int) {
                                    fetchOrderDetails(r_key,r_token,mListOrder[position]!!.order_id,mListOrder[position]!!.order_status.capitalize().toUpperCase()=="ACCEPTED")
                                }
                            })
                            recycler_view_0.layoutManager = LinearLayoutManager(getActivityBase())
                            recycler_view_0.adapter = mAdapter
                        }
                    }
                    log(TAG, "after list size is: " + mListOrder.size)
                    (parentFragment as OrderInfoFragment).showPreogressBar(false)
                    swipeRefresh.setRefreshing(false)



                } else {
                   // if(userVisibleHint)
                    showSnackBar(getString(R.string.no_data_available))
                    view_empty.visibility = View.VISIBLE
                    view_empty_txt_data.text =getString(R.string.no_data_available)
                  //  recycler_view_0.visibility = View.GONE
                    (parentFragment as OrderInfoFragment).showPreogressBar(false)
                    swipeRefresh.setRefreshing(false)


                }

            }

            override fun onFail(error: Int) {
                when (error) {
                    404 -> {
                       // if(userVisibleHint)
                        showSnackBar(getString(R.string.error_404))
                        view_empty.visibility = View.VISIBLE
                        view_empty_txt_data.text =getString(R.string.error_404_text)
                      //  recycler_view_0.visibility = View.GONE
                        log(TAG, "api call failed...")
                    }
                    100 -> {
                     //   if(userVisibleHint)
                        showSnackBar(getString(R.string.internet_not_available))
                        view_empty.visibility = View.VISIBLE
                        view_empty_txt_data.text =getString(R.string.internet_not_available)
                     //   recycler_view_0.visibility = View.GONE

                    }

                }
                swipeRefresh.setRefreshing(false)
                (parentFragment as OrderInfoFragment).showPreogressBar(false)
            }

        })
    }

    fun chekifAnyHeader(list: ArrayList<CustomSearchItem?>): Boolean {

        for (i in 0..list.size - 1) {
            if (list.get(i)!!.showOrderHeader == true)
                return true
        }
        return false

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        log(TAG, "onCreate---")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        log(TAG, "onCreateView---")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPause() {
        log(TAG, "onPause---")
        super.onPause()
    }

    override fun onStop() {
        log(TAG, "onStop---")
        super.onStop()
    }

    override fun onDestroy() {
        log(TAG, "onDestroy---")
        super.onDestroy()
    }

    override fun onDestroyView() {
        log(TAG, "onDestroyView---")
        super.onDestroyView()
    }


    override fun handleBackButton(): Boolean {
        return true
    }


}


class Task(private val r_token: String, private val r_key: String)
