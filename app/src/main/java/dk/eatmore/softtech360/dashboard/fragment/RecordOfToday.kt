package dk.eatmore.softtech360.dashboard.fragment


import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.dashboard.adapter.RecordOfTodayAdapter
import dk.eatmore.softtech360.model.CustomSearchItem
import dk.eatmore.softtech360.rest.ApiCall
import dk.eatmore.softtech360.storage.PreferenceUtil
import dk.eatmore.softtech360.utils.BaseFragment

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RecordOfToday : BaseFragment() {

    val mListOrder = ArrayList<CustomSearchItem?>()
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

        var r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")!!.replace("\"","")
        var r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")
        log(TAG, r_key.toString() + " " + r_token.toString())
        val map= HashMap<String, Any>()
        map.put("r_token","w5oRqFiAXTBB3hwpixAORbg_BwUj0EMQ07042017114812")
        map.put("r_key","fcARlrbZFXYee1W6eYEIA0VRlw7MgV4o07042017114812")
        map.put("order_to","2018-05-18")
        map.put("order_from","2018-05-08")





        var k ="fcARlrbZFXYee1W6eYEIA0VRlw7MgV4o07042017114812"


        callAPI(ApiCall.myOrder("2018-05-18", "2018-05-08", r_key!!, r_token!!), object : BaseFragment.OnApiCallInteraction {

            override fun <T> onSuccess(body: T?) {

                        /*  val json= body as JsonObject
                          var gson = Gson()
                          var mMineUserEntity = gson?.fromJson(json, TestOrder::class.java)*/

                log(TAG,"response is ----"+body.toString())

             /*   var list: List<CustomSearchItem> = (body as Order).custom_search
          //      log(TAG,"list size is: "+list.size)
                val mListNewOrder = ArrayList<CustomSearchItem?>()
                val mListAnsweredOrder = ArrayList<CustomSearchItem?>()
                for (i in list.size-1 downTo -1 + 1)  {
                    val item : CustomSearchItem = list.get(i)

                    if (item.order_status == "Pending Restaurant" || item.order_status == "Pending Opening Restaurant") {
                        // new order list
                        if(!chekifAnyHeader(mListNewOrder)){
                            item.headerType="mListNewOrder"
                            item.showOrderHeader=true
                        }
                        mListNewOrder.add(item)


                    } else {
                        // answered order list
                        if(!chekifAnyHeader(mListAnsweredOrder)){
                            item.headerType="mListAnsweredOrder"
                            item.showOrderHeader=true
                        }
                        mListAnsweredOrder.add(item)

                    }

                }
                mListOrder.addAll(mListNewOrder)
                mListOrder.addAll(mListAnsweredOrder)
                log(TAG,"after list size is: "+mListOrder.size)
                mAdapter = RecordOfTodayAdapter(mListOrder,mListNewOrder,mListAnsweredOrder,refFragment)
                recycler_view.layoutManager = LinearLayoutManager(getActivityBase())
                recycler_view.adapter = mAdapter*/
            }

            override fun onFail() {
                showSnackBar(getString(R.string.error_404))
                log(TAG, "api call failed...")

            }

        })

    }

    fun chekifAnyHeader(list : ArrayList<CustomSearchItem?>) : Boolean{

        for (i in 0..list.size-1){
            if(list.get(i)!!.showOrderHeader==true)
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


class Task(private val r_token: String, private val r_key: String )
