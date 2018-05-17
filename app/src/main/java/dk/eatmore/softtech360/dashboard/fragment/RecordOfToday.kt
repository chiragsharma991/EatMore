package dk.eatmore.softtech360.dashboard.fragment


import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_record_of_today.*
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RecordOfToday : BaseFragment() {

    var mListOrder = ArrayList<CustomSearchItem?>()
    var mAdapter : RecordOfTodayAdapter? = null
    val refFragment : RecordOfToday = this



    companion object {
        val TAG = "RecordOfToday"
        fun newInstance(): RecordOfToday {
            return RecordOfToday()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

    }




    override fun getLayout(): Int {
        return R.layout.fragment_record_of_today
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {

        var r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")
        var r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")
        log(TAG, r_key.toString() + " " + r_token.toString())


        callAPI(ApiCall.myOrder("2018-05-16", "2018-05-01", r_key!!.trim(), r_token!!.trim()), object : BaseFragment.OnApiCallInteraction {

            override fun <T> onSuccess(body: T?) {

                /*          val json= body as JsonObject
                          var gson = Gson()
                          var mMineUserEntity = gson?.fromJson(json, TestOrder::class.java)*/



                var list: List<CustomSearchItem> = (body as Order).custom_search
                log(TAG,"list size is: "+list.size)
                mListOrder.addAll(list)
                mAdapter = RecordOfTodayAdapter(mListOrder,refFragment)
                recycler_view.layoutManager = LinearLayoutManager(getActivityBase())
                recycler_view.adapter = mAdapter

            }

            override fun onFail() {
                showSnackBar(getString(R.string.error_404))
                log(TAG, "api call failed...")

            }

        })

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
