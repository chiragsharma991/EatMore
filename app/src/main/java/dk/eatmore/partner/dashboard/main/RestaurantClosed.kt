package dk.eatmore.partner.dashboard.main

import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import dk.eatmore.partner.R
import dk.eatmore.partner.utils.BaseActivity
import dk.eatmore.partner.utils.Constants
import kotlinx.android.synthetic.main.restaurantclosed.*

class RestaurantClosed : BaseActivity(){



    companion object {

        val TAG = "RestaurantClosed"
        fun newInstance() : RestaurantClosed {
            return RestaurantClosed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurantclosed)
        initView(savedInstanceState)

    }

    private fun initView(savedInstanceState: Bundle?) {
        log(TAG,""+intent.extras.get(Constants.MESSAGE_TITLE)+" -- "+intent.extras.get(Constants.MESSAGE_DETAILS))
        msg_title.text=intent.extras.get(Constants.MESSAGE_TITLE).toString()
        msg_detail.text=intent.extras.get(Constants.MESSAGE_DETAILS).toString()
        fullScreen()

    }










    override fun onBackPressed() {
       // super.onBackPressed()
        //  finish()
    }


}