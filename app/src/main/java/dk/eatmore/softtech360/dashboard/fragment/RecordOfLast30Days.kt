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
import kotlinx.android.synthetic.main.fragment_record_of_last30_days.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class RecordOfLast30Days : BaseFragment() {
    var mListOrder = ArrayList<CustomSearchItem?>()
    var mAdapter : RecordOfTodayAdapter? = null
    val refFragment : RecordOfLast30Days = this





    companion object {
        val TAG = "RecordOfLast30Days"
        fun newInstance(): RecordOfLast30Days {
            return RecordOfLast30Days()
        }
    }


    override fun getLayout(): Int {
        return R.layout.fragment_record_of_last30_days
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        var r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")
        var r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")
        log(RecordOfLast30Days.TAG, r_key.toString() + " " + r_token.toString())


        callAPI(ApiCall.myOrder("2018-05-18", "2018-05-08", r_key.toString(), r_token.toString()), object : BaseFragment.OnApiCallInteraction {

            override fun <T> onSuccess(body: T?) {

                /*          val json= body as JsonObject
                          var gson = Gson()
                          var mMineUserEntity = gson?.fromJson(json, TestOrder::class.java)*/



     /*           var list: List<CustomSearchItem> = (body as Order).custom_search
                log(RecordOfLast30Days.TAG,"list size is: "+list.size)
                mListOrder.addAll(list)
                mAdapter = RecordOfTodayAdapter(mListOrder,refFragment)
                recycler_view.layoutManager = LinearLayoutManager(getActivityBase())
                recycler_view.adapter = mAdapter*/

            }

            override fun onFail() {
                showSnackBar(getString(R.string.error_404))
                log(RecordOfLast30Days.TAG, "api call failed...")

            }

        })

    }

    override fun handleBackButton(): Boolean {
        return true
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        log(RecordOfLast30Days.TAG,"onCreate---")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        log(RecordOfLast30Days.TAG,"onCreateView---")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPause() {
        log(RecordOfLast30Days.TAG,"onPause---")
        super.onPause()
    }

    override fun onStop() {
        log(RecordOfLast30Days.TAG,"onStop---")
        super.onStop()
    }

    override fun onDestroy() {
        log(RecordOfLast30Days.TAG,"onDestroy---")
        super.onDestroy()
    }

    override fun onDestroyView() {
        log(RecordOfLast30Days.TAG,"onDestroyView---")
        super.onDestroyView()
    }


}
