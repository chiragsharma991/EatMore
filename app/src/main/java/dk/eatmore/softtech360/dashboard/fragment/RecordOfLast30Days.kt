package dk.eatmore.softtech360.dashboard.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.utils.BaseFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class RecordOfLast30Days : BaseFragment() {



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
    }

    override fun handleBackButton(): Boolean {
        return true
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        log(RecordOfToday.TAG,"onCreate---")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        log(RecordOfToday.TAG,"onCreateView---")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPause() {
        log(RecordOfToday.TAG,"onPause---")
        super.onPause()
    }

    override fun onStop() {
        log(RecordOfToday.TAG,"onStop---")
        super.onStop()
    }

    override fun onDestroy() {
        log(RecordOfToday.TAG,"onDestroy---")
        super.onDestroy()
    }

    override fun onDestroyView() {
        log(RecordOfToday.TAG,"onDestroyView---")
        super.onDestroyView()
    }


}
