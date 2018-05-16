package dk.eatmore.softtech360.dashboard.fragment


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonObject
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.model.Order
import dk.eatmore.softtech360.rest.ApiCall
import dk.eatmore.softtech360.storage.PreferenceUtil
import dk.eatmore.softtech360.utils.BaseFragment
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_progressbar.*
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RecordOfToday : BaseFragment() {



    companion object {
        val TAG = "RecordOfToday"
        fun newInstance(): RecordOfToday {
            return RecordOfToday()
        }
    }


    override fun getLayout(): Int {
        return R.layout.fragment_record_of_today
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {

        var r_key=PreferenceUtil.getString(PreferenceUtil.R_KEY,"")
        var r_token=PreferenceUtil.getString(PreferenceUtil.R_TOKEN,"")
        log(TAG,r_key.toString()+" "+r_token.toString())


        callAPI( ApiCall.myOrder("2018-05-16","2018-05-01" , r_key.toString() , r_token.toString()) , object : BaseFragment.OnApiCallInteraction {

            override fun <T> onSuccess(body: T?) {
                val order= body as JsonObject
                log(TAG, "response: "+order.toString())
            /*    if (order.status){

                } else {

                }*/
            }

            override fun onFail() {
                showSnackBar(getString(R.string.error_404))
                log(TAG,"api call failed...")

            }

        })

    }

    private fun setAdapter(){
    /*    mAdapter = AdminHistoryListAdapter(mListProduct, this)
        recycler_view.layoutManager = LinearLayoutManager(getActivityBase())
        recycler_view.adapter = mAdapter

        var scrollListener = object : EndlessRecyclerViewScrollListener(recycler_view.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                log(TAG, "page  $page , Total: $totalItemsCount")
                if (mEndPos <= totalItemsCount && mListProduct[mListProduct.size - 1] != null) {
                    mListProduct.add(null)
                    recycler_view.post(Runnable { mAdapter?.notifyItemInserted(mListProduct.size - 1) })
                    getHistory()
                }
            }
        }
        recycler_view.addOnScrollListener(scrollListener)*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        log(TAG,"onCreate---")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        log(TAG,"onCreateView---")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPause() {
        log(TAG,"onPause---")
        super.onPause()
    }

    override fun onStop() {
        log(TAG,"onStop---")
        super.onStop()
    }

    override fun onDestroy() {
        log(TAG,"onDestroy---")
        super.onDestroy()
    }

    override fun onDestroyView() {
        log(TAG,"onDestroyView---")
        super.onDestroyView()
    }



    override fun handleBackButton(): Boolean {
        return true
    }

}
