package dk.eatmore.softtech360.dashboard.fragment


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonObject
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.dashboard.adapter.RecordOfTodayAdapter
import dk.eatmore.softtech360.model.CustomSearchItem
import dk.eatmore.softtech360.model.GetReason
import dk.eatmore.softtech360.model.OpeninghoursItem
import dk.eatmore.softtech360.model.Order
import dk.eatmore.softtech360.rest.ApiCall
import dk.eatmore.softtech360.storage.PreferenceUtil
import dk.eatmore.softtech360.utils.BaseFragment
import dk.eatmore.softtech360.utils.DialogUtils
import dk.eatmore.softtech360.utils.DialogUtils.createListDialog
import kotlinx.android.synthetic.main.fragment_record_of_today.*
import kotlinx.android.synthetic.main.layout_empty.*
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var r_key = ""
var r_token = ""


class RecordOfToday : BaseFragment() {

    val mListOrder = ArrayList<CustomSearchItem?>()
    var mAdapter: RecordOfTodayAdapter? = null
    val refFragment: RecordOfToday = this


    companion object {
        val TAG = "RecordOfToday"
        fun newInstance(): RecordOfToday {
            return RecordOfToday()
        }
    }


    fun callback() {

    }


    /***
     * @param id 2 > accept : 1 > Details : 0 > Reject
     */
    fun performAction(id: Int, orderId: String) {

        when (id) {
            0 -> {
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
                            createListDialog(activity, listOfreason, object : DialogUtils.OnDialogClickListener {
                                override fun onNegativeButtonClick() {
                                }
                                override fun onPositiveButtonClick(position: Int) {
                                    // submit reason and reject the order.
                                    log("or id ",list.get(position).or_id)
                                    callAPI(ApiCall.rejectOrders(r_key = r_key, r_token = r_token, order_no = orderId, reason = list.get(position).or_id, order_status = "rejected"), object : BaseFragment.OnApiCallInteraction {
                                        override fun <T> onSuccess(body: T?) {
                                            val json = body as JsonObject  // please be mind you are using jsonobject(Gson)
                                            if (json.get("status").asBoolean) {
                                                showSnackBar(getString(R.string.order_reject))
                                            }
                                        }
                                        override fun onFail() {
                                        }
                                    })
                                }

                            })

                        }
                    }


                    override fun onFail() {

                    }
                })
            }
            1 -> {
            }
            2 -> {
                //     createListDialog(activity)

            }
        }

    }


    override fun getLayout(): Int {
        return R.layout.fragment_record_of_today
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {

        r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")!!
        r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")!!
        var user = PreferenceUtil.getString(PreferenceUtil.USER_NAME, "")
        view_empty.visibility = View.GONE
        recycler_view.visibility = View.VISIBLE


        log(TAG, r_key.toString() + " " + r_token.toString() + " " + user)
        val currentDate = getCalculatedDate("yyyy-MM-dd", 0)

        callAPI(ApiCall.myOrder(currentDate, currentDate, r_key!!, r_token!!), object : BaseFragment.OnApiCallInteraction {

            override fun <T> onSuccess(body: T?) {

                /*  val json= body as JsonObject
                            var gson = Gson()
                            var mMineUserEntity = gson?.fromJson(json, TestOrder::class.java)*/


                if ((body as Order).status) {
                    var list: List<CustomSearchItem> = (body as Order).custom_search
                    val mListNewOrder = ArrayList<CustomSearchItem?>()
                    val mListAnsweredOrder = ArrayList<CustomSearchItem?>()
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
                    log(TAG, "after list size is: " + mListOrder.size)
                    mAdapter = RecordOfTodayAdapter(mListOrder, mListNewOrder, mListAnsweredOrder, refFragment)
                    recycler_view.layoutManager = LinearLayoutManager(getActivityBase())
                    recycler_view.adapter = mAdapter
                } else {
                    if ((body as Order).error == null) {
                        view_empty.visibility = View.VISIBLE
                        recycler_view.visibility = View.GONE
                    } else {
                        showSnackBar(getString(R.string.error_404))
                    }
                }

            }

            override fun onFail() {
                showSnackBar(getString(R.string.error_404))
                log(TAG, "api call failed...")

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
        log(RecordOfToday.TAG, "onCreate---")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        log(RecordOfToday.TAG, "onCreateView---")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPause() {
        log(RecordOfToday.TAG, "onPause---")
        super.onPause()
    }

    override fun onStop() {
        log(RecordOfToday.TAG, "onStop---")
        super.onStop()
    }

    override fun onDestroy() {
        log(RecordOfToday.TAG, "onDestroy---")
        super.onDestroy()
    }

    override fun onDestroyView() {
        log(RecordOfToday.TAG, "onDestroyView---")
        super.onDestroyView()
    }


    override fun handleBackButton(): Boolean {
        return true
    }


}


class Task(private val r_token: String, private val r_key: String)
