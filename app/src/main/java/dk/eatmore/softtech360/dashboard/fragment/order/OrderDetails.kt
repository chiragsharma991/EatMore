package dk.eatmore.softtech360.dashboard.fragment.order

import android.os.Bundle
import android.view.View
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.dashboard.main.MainActivity
import dk.eatmore.softtech360.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_settinginfo.*
import kotlinx.android.synthetic.main.layout_progressbar.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class  OrderDetails : BaseFragment(){

    companion object {
        val TAG = "OrderDetails"
        fun newInstance(): OrderDetails {
            return OrderDetails()
        }
    }


    override fun getLayout(): Int {
        return R.layout.fragment_order_details
    }



    override fun initView(view: View?, savedInstanceState: Bundle?) {
        initToolbar()
        (getActivityBase() as MainActivity).progress_bar.visibility = View.VISIBLE

    }

    private fun initToolbar() {
        img_toolbar_back.setImageResource(R.drawable.ic_back)
        txt_toolbar.text = "Orders"
        img_toolbar_back.setOnClickListener{
            log(TAG,"back press:")
            (getActivityBase() as MainActivity).pop()

        }

    }

    override fun handleBackButton(): Boolean {
        return false
    }


}