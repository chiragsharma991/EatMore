package dk.eatmore.softtech360.dashboard.fragment.order


import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonObject
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.dashboard.adapter.RecordOfTodayAdapter
import dk.eatmore.softtech360.dashboard.main.MainActivity
import dk.eatmore.softtech360.model.CustomSearchItem
import dk.eatmore.softtech360.model.GetReason
import dk.eatmore.softtech360.model.OpeninghoursItem
import dk.eatmore.softtech360.model.Order
import dk.eatmore.softtech360.rest.ApiCall
import dk.eatmore.softtech360.storage.PreferenceUtil
import dk.eatmore.softtech360.utils.BaseFragment
import dk.eatmore.softtech360.utils.DateCalculation
import dk.eatmore.softtech360.utils.DialogUtils
import dk.eatmore.softtech360.utils.DialogUtils.createListDialog
import kotlinx.android.synthetic.main.fragment_record_of_today.*
import kotlinx.android.synthetic.main.layout_empty.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var r_key = ""
var r_token = ""


class RecordOfToday : BaseFragment() {

    val mListOrder = ArrayList<CustomSearchItem?>()
    val mListNewOrder = ArrayList<CustomSearchItem?>()
    val mListAnsweredOrder = ArrayList<CustomSearchItem?>()

    var mAdapter: RecordOfTodayAdapter? = null
    val refFragment: RecordOfToday = this

    companion object {
        val TAG = "RecordOfToday"
        fun newInstance(): RecordOfToday {
            return RecordOfToday()
        }
    }


    fun callbackRefresh() {
        log(TAG, "callbackRefresh---")
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
        var expectedTime = DateCalculation.getCalculatedTime(model.expected_time, "yyyy-MM-dd HH:mm:ss")
        // after getting expected time like in long form : make 9 list with that
        val list = ArrayList<String>()
        for (i in 0..8) {
            val mTime = expectedTime + timeIntervel * 60 * 1000
            val time = SimpleDateFormat("HH:mm").format(mTime)
            list.add(time + " ($timeIntervel Min)")
            timeIntervel += 15
        }
        val borderColor = ContextCompat.getColor(activity!!, R.color.green)
        createListDialog("Expected Time: ${DateCalculation.getDateformat(model.expected_time, SimpleDateFormat("HH:mm"), "yyyy-MM-dd HH:mm:ss")}",
                activity, list, borderColor, object : DialogUtils.OnDialogClickListener {
            override fun onNegativeButtonClick() {
            }

            override fun onPositiveButtonClick(position: Int) {
                // Calculate which time slot has been selected and reformat the date and post :)
                var expectedTime = DateCalculation.getCalculatedTime(model.expected_time, "yyyy-MM-dd HH:mm:ss")
                val mTime = expectedTime + position * 15 * 60 * 1000
                val time = SimpleDateFormat("HH:mm:ss").format(mTime)
                val date = SimpleDateFormat("yyyy-MM-dd").format(expectedTime)
                val pickup_delivery_time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateCalculation.getCalculatedTime(date + " " + time, "yyyy-MM-dd HH:mm:ss"))

                callAPI(ApiCall.acceptOrders(r_key = r_key, r_token = r_token,
                        order_no = model.order_id, pickup_delivery_time = pickup_delivery_time, order_status = "accepted"),
                        object : BaseFragment.OnApiCallInteraction {

                            override fun <T> onSuccess(body: T?) {
                                val json = body as JsonObject  // please be mind you are using jsonobject(Gson)
                                if (json.get("status").asBoolean) {
                                    val result = json.getAsJsonObject("data").get("order_status").asString + " " + json.get("msg").asString
                                    showSnackBar(result)
                                    (parentFragment as OrderInfoFragment).performedStatusAction(0)
                                }
                            }

                            override fun onFail(error: Int) {
                                when (error) {
                                    404 -> {
                                        showSnackBar(getString(R.string.error_404))
                                        log(RecordOfLast30Days.TAG, "api call failed...")
                                    }
                                    100 -> {
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
                                        showSnackBar(result)
                                        (parentFragment as OrderInfoFragment).performedStatusAction(0)

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


        log(TAG, r_key.toString() + " " + r_token.toString() + " " + user)
        val currentDate = getCalculatedDate("yyyy-MM-dd", 0)

         fetchOrders(true)
    }

    fun fetchOrders(setAdapter : Boolean) {
        mListNewOrder.clear()
        mListAnsweredOrder.clear()
        mListOrder.clear()
        (parentFragment as OrderInfoFragment).showPreogressBar(true)
        val currentDate = getCalculatedDate("yyyy-MM-dd", 0)
        callAPI(ApiCall.myOrder(currentDate, currentDate, r_key!!, r_token!!), object : BaseFragment.OnApiCallInteraction {

            override fun <T> onSuccess(body: T?) {

                /*  val json= body as JsonObject
                            var gson = Gson()
                            var mMineUserEntity = gson?.fromJson(json, TestOrder::class.java)*/


                if ((body as Order).status) {
                    var list: List<CustomSearchItem> = (body as Order).custom_search
                    for (i in list.size - 1 downTo -1 + 1) {
                        val item: CustomSearchItem = list.get(i)
                        if (item.order_status == "Pending Restaurant" || item.order_status == "Pending Opening Restaurant") {
                            // new order list
                            if (!chekifAnyHeader(mListNewOrder)) {
                                item.headerType = "mListNewOrder"
                                item.showOrderHeader = true
                            }
                            mListNewOrder.add(item)
                        } else {
                            // answered order list
                            if (!chekifAnyHeader(mListAnsweredOrder)) {
                                item.headerType = "mListAnsweredOrder"
                                item.showOrderHeader = true
                            }
                            mListAnsweredOrder.add(item)
                        }
                    }
                    mListOrder.addAll(mListNewOrder)
                    mListOrder.addAll(mListAnsweredOrder)
                    // if adapter is true then list should be set.
                    if(setAdapter){
                        mAdapter = RecordOfTodayAdapter(mListOrder, mListNewOrder, mListAnsweredOrder, refFragment)
                        recycler_view_0.layoutManager = LinearLayoutManager(getActivityBase())
                        recycler_view_0.adapter = mAdapter
                    }else{
                        if(mAdapter != null){
                            mAdapter!!.notifyDataSetChanged()
                        }else{
                            mAdapter = RecordOfTodayAdapter(mListOrder, mListNewOrder, mListAnsweredOrder, refFragment)
                            recycler_view_0.layoutManager = LinearLayoutManager(getActivityBase())
                            recycler_view_0.adapter = mAdapter
                        }
                    }
                    log(TAG, "after list size is: " + mListOrder.size)
                    (parentFragment as OrderInfoFragment).showPreogressBar(false)

                } else {
                    if ((body as Order).error == null) {
                        view_empty.visibility = View.VISIBLE
                        recycler_view_0.visibility = View.GONE
                    } else {
                        showSnackBar(getString(R.string.error_404))
                    }
                    (parentFragment as OrderInfoFragment).showPreogressBar(false)

                }

            }

            override fun onFail(error: Int) {
                (parentFragment as OrderInfoFragment).showPreogressBar(false)
                when (error) {
                    404 -> {
                        showSnackBar(getString(R.string.error_404))
                        log(TAG, "api call failed...")
                    }
                    100 -> {
                        showSnackBar(getString(R.string.internet_not_available))

                    }

                }


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
