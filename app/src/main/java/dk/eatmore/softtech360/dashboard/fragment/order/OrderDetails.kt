package dk.eatmore.softtech360.dashboard.fragment.order

import android.content.Context
import android.os.Bundle
import android.view.View
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.dashboard.main.MainActivity
import dk.eatmore.softtech360.model.CustomSearchItem
import dk.eatmore.softtech360.model.ProductDetails
import dk.eatmore.softtech360.rest.ApiCall
import dk.eatmore.softtech360.storage.PreferenceUtil
import dk.eatmore.softtech360.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_order_details.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progressbar.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.util.ArrayList
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.row_order_ingradient.view.*
import kotlinx.android.synthetic.main.row_order_product.view.*
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.view.Gravity
import android.widget.LinearLayout
import dk.eatmore.softtech360.model.DataItem
import kotlinx.android.synthetic.main.fragment_info_order.*
import kotlinx.android.synthetic.main.row_order_attribute.view.*


class  OrderDetails : BaseFragment(), View.OnClickListener {



    val mListOrderDetails = ArrayList<OrderDetails>()
    var customSearchItem : CustomSearchItem? = null

    companion object {
        val TAG = "OrderDetails"
        fun newInstance(customItems : CustomSearchItem): OrderDetails{

            val arg = Bundle()
            arg.putSerializable("MODEL",customItems)
            val fragment =OrderDetails()
            fragment.arguments=arg
            return fragment
        }


        }



    override fun getLayout(): Int {
        return R.layout.fragment_order_details
    }


    override fun onClick(v: View?) {
        when(v!!.id) {

            R.id.row_order_accept -> {

                val fragment = fragmentManager?.findFragmentByTag(OrderInfoFragment.TAG)
                if(fragment !=null){
                    (((fragment as OrderInfoFragment).viewpager.adapter as OrderInfoFragment.ViewPagerAdapter).mFragmentList.get(0) as RecordOfToday).performAction(2,customSearchItem!!)
                }
            }

            R.id.row_order_details -> {
                (getActivityBase() as MainActivity).pop()
            }

            R.id.row_order_reject -> {
                val fragment = fragmentManager?.findFragmentByTag(OrderInfoFragment.TAG)
                if(fragment !=null){
                    (((fragment as OrderInfoFragment).viewpager.adapter as OrderInfoFragment.ViewPagerAdapter).mFragmentList.get(0) as RecordOfToday).performAction(0,customSearchItem!!)

                }
            }
        }

    }


    override fun initView(view: View?, savedInstanceState: Bundle?) {
        r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")!!
        r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")!!
        customSearchItem = arguments?.getSerializable("MODEL") as CustomSearchItem
        initToolbar()
        fetchOrders(false, r_key, r_token,customSearchItem!!.order_id)

    }

    private fun setView(customSearchItem: CustomSearchItem) {

        // this is condition to hide/show accept/reject button .
        val currentDate = getCalculatedDate("yyyy-MM-dd", 0)
        if (customSearchItem!!.headerType == "mListNewOrder") {
            if (currentDate == customSearchItem.order_month_date) row_order_typebtn.visibility = View.VISIBLE else row_order_typebtn.visibility = View.GONE
        } else {
            row_order_typebtn.visibility = View.GONE
        }

        row_order_reject.setOnClickListener(this)
        row_order_accept.setOnClickListener(this)
        row_order_details.setOnClickListener(this)
        view_empty.visibility = View.GONE
        order_detail_view.visibility =View.GONE
    }



    private fun setPrice(data: List<DataItem>?) {
        if(data!!.get(0).order_total !=null){
            order_detail_subtotal.visibility = View.VISIBLE
            order_detail_subtotal_txt.text = data.get(0).order_total
        }else   order_detail_subtotal.visibility = View.GONE

        if(data.get(0).shipping_costs !=null){
            order_detail_ship.visibility = View.VISIBLE
            row_order_ship_txt.text = data.get(0).shipping_costs
        }else   order_detail_ship.visibility = View.GONE

        if(data.get(0).additional_charge !=null){
            order_detail_additional.visibility = View.VISIBLE
            row_order_additional_txt.text = data.get(0).additional_charge
        }else   order_detail_additional.visibility = View.GONE

        if(data.get(0).discount_amount !=null){
            order_detail_discount.visibility = View.VISIBLE
            row_order_discount_txt.text = data.get(0).discount_amount
        }else   order_detail_discount.visibility = View.GONE

        if(data.get(0).total_to_pay !=null){
            order_detail_total.visibility = View.VISIBLE
            order_detail_total_txt.text = data.get(0).total_to_pay
        }else   order_detail_total.visibility = View.GONE

        if(data.get(0).shipping_remark !=null){
            order_detail_shipping_remark.visibility = View.VISIBLE
            order_detail_shipping_remark.text = data.get(0).shipping_remark
        }else   order_detail_shipping_remark.visibility = View.GONE

        if(data.get(0).accept_reject_time !=null){
            order_detail_time.visibility = View.VISIBLE
            order_detail_time.text = data.get(0).accept_reject_time
        }else   order_detail_time.visibility = View.GONE

        if(data.get(0).order_status == "Accepted"){
            order_detail_reason.visibility = View.VISIBLE
            order_detail_reason.text = data.get(0).pickup_delivery_time
        }else {
            if(data.get(0).reject_reason != null){
                order_detail_reason.visibility = View.VISIBLE
                order_detail_reason.text = data.get(0).reject_reason
            }else  order_detail_reason.visibility = View.GONE

        }




    }


    private fun fetchOrders(b: Boolean, r_key: String, r_token: String, order_id: String) {
        progress_bar.visibility = View.VISIBLE
        callAPI(ApiCall.viewRecords(r_key, r_token,order_id), object : BaseFragment.OnApiCallInteraction {

            override fun <T> onSuccess(body: T?) {

                /*  val json= body as JsonObject
                            var gson = Gson()
                            var mMineUserEntity = gson?.fromJson(json, TestOrder::class.java)*/


                if ((body as ProductDetails).status) {

                    log(TAG,"status "+(body as ProductDetails).status)
                    val data =(body as ProductDetails).data
                    progress_bar.visibility = View.GONE
                    order_detail_view.visibility =View.VISIBLE
                    order_detail_date.text = data!!.get(0).order_date
                    order_detail_num.text = data!!.get(0).order_no
                    order_detail_rest.text = data!!.get(0).restaurant_id
                    order_detail_shipping.text = data!!.get(0).shipping
                    order_detail_expt_time.text = data!!.get(0).expected_time
                    order_detail_req.text = data!!.get(0).requirement
                    order_detail_fstname.text = data!!.get(0).first_name
                    order_detail_address.text = data!!.get(0).address
                    order_detail_phone.text = "Phone: "+data!!.get(0).telephone_no
                    order_detail_pre.text = "Previous order: "+data!!.get(0).previous_order
                    order_detail_payment_status.text = data!!.get(0).payment_status
                    order_detail_payment_method.text = if(data!!.get(0).paymethod == "1") "Online payment" else "Cash payment"
                    order_detail_accept.text =  data!!.get(0).order_status

                    setPrice(data)
                    order_detail_dynamic_container.removeAllViews()


                    /**
                     * @param:
                     *
                     * order_detail_dynamic_container =(Parent View) Home container when we add all layouts and show.
                     * view = (child view) all subview added in this view and after view will add into "order_detail_dynamic_container"
                     * view.row_product_order_main = (Sub child view) this is view , you can hide and visible of perticular one product.
                     */



                    for (i in 0..data.get(0).order_products_details!!.size -1){
                        log("order_products_details size","")

                        //--- product name ---
                        var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                        val view = inflater.inflate(R.layout.row_order_product, null)
                        view.row_order_product.text = data.get(0).order_products_details!!.get(i).quantity +"x"+
                                data.get(0).order_products_details!!.get(i).products.product_no+
                                data.get(0).order_products_details!!.get(i).products.p_name
                        view.row_order_product_price.text =data.get(0).order_products_details!!.get(i).p_price

                       // view.row_product_order_container.removeAllViewsInLayout()
                        view.row_product_order_main.setOnClickListener{
                            if(view.row_product_order_container.visibility == View.VISIBLE) view.row_product_order_container.visibility =View.GONE
                            else view.row_product_order_container.visibility =View.VISIBLE
                        }

                        // --- ingradient ---

                        if(data.get(0).order_products_details?.get(i)?.removed_ingredients?.size != null){

                            for (j in 0..data.get(0).order_products_details!!.get(i).removed_ingredients!!.size -1){

                                val view_ingradient = inflater.inflate(R.layout.row_order_ingradient, null)
                                view_ingradient.row_order_ingradient.text ="-"+data.get(0).order_products_details!!.get(i).removed_ingredients!!.get(j).ingredient_name
                                view.row_product_order_container.addView(view_ingradient)

                            }
                        }


                        // --- attributes ---

                        if(data.get(0).order_products_details!!.get(i).products.is_attributes == "1"){

                            // with extratoppings

                            for (k in 0..data.get(0).order_products_details!!.get(i).ordered_product_attributes!!.size -1){

                                // Add attribute Header---
                                val view_attribute = inflater.inflate(R.layout.row_order_attribute, null)
                                view_attribute.row_order_attribute.removeAllViewsInLayout()
                                val textView = AppCompatTextView(context)
                                var parms = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                parms.setMargins(0, 5, 0, 5)
                                textView.gravity = Gravity.LEFT
                                textView.setSingleLine()
                              //  textView.typeface = Typeface.DEFAULT_BOLD
                                textView.setTextAppearance(context,android.R.style.TextAppearance_Small)
                                textView.setTextColor(ContextCompat.getColor(context!!,R.color.black))
                                textView.text =data.get(0).order_products_details!!.get(i).ordered_product_attributes!!.get(k).attribute_value_name
                                textView.setLayoutParams(parms)
                                view_attribute.row_order_attribute.addView(textView)


                                // Add divider---
                                val divider = View(context)
                                parms = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                                divider.alpha = 0.2F
                                divider.setBackgroundColor(ContextCompat.getColor(context!!,R.color.border_gray))
                                divider.setLayoutParams(parms)
                                view_attribute.row_order_attribute.addView(divider)


                                // Add attribute of sizes---
                                for (l in 0..data.get(0).order_products_details!!.get(i).ordered_product_attributes!!.get(k).order_product_extra_topping_group!!. size-1){
                                    parms = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                    val textView = AppCompatTextView(context)
                                    textView.gravity = Gravity.LEFT
                                    textView.setSingleLine()
                                    //  textView.typeface = Typeface.DEFAULT_BOLD
                                    textView.setTextAppearance(context,android.R.style.TextAppearance_Small)
                                    textView.text ="+"+data.get(0).order_products_details!!.get(i).
                                            ordered_product_attributes!!.get(k).order_product_extra_topping_group!!.get(l).ingredient_name
                                    textView.setLayoutParams(parms)
                                    view_attribute.row_order_attribute.addView(textView)


                                }
                                view.row_product_order_container.addView(view_attribute)
                            }



                        }
                        else{

                            // Without extratoppings
                            val view_onlyextaratoppings = inflater.inflate(R.layout.row_order_attribute, null)
                            view_onlyextaratoppings.row_order_attribute.removeAllViewsInLayout()
                            // Add attribute of sizes---

                            for (l in 0..data.get(0).order_products_details!!.get(i).order_product_extra_topping_group!!. size-1){

                                val textView = AppCompatTextView(context)
                                val parms = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                parms.setMargins(0, 5, 0, 5)
                                textView.gravity = Gravity.LEFT
                                textView.setSingleLine()
                                //  textView.typeface = Typeface.DEFAULT_BOLD
                                textView.setTextAppearance(context,android.R.style.TextAppearance_Small)
                                textView.text ="+"+data.get(0).order_products_details!!.get(i).
                                        order_product_extra_topping_group!!.get(l).ingredient_name
                                textView.setLayoutParams(parms)
                                view_onlyextaratoppings.row_order_attribute.addView(textView)
                            }
                            view.row_product_order_container.addView(view_onlyextaratoppings)
                        }

                        // add all final layout to home container.
                        order_detail_dynamic_container.addView(view)

                    }



                } else {
                    progress_bar.visibility = View.GONE
                    order_detail_view.visibility =View.GONE
                    view_empty.visibility = View.VISIBLE
                    view_empty_txt_data.text =getString(R.string.no_data_available)

                }

            }

            override fun onFail(error: Int) {
                when (error) {
                    404 -> {
                        showSnackBar(getString(R.string.error_404))
                        log(RecordOfToday.TAG, "api call failed...")
                        view_empty_txt_data.text =getString(R.string.error_404)

                    }
                    100 -> {
                        showSnackBar(getString(R.string.internet_not_available))
                        view_empty_txt_data.text =getString(R.string.internet_not_available)
                    }

                }
                progress_bar.visibility = View.GONE
                order_detail_view.visibility =View.GONE
                view_empty.visibility = View.VISIBLE


            }

        })


    }

    private fun initToolbar() {

        img_toolbar_back.setImageResource(R.drawable.ic_back)
        txt_toolbar.text = "Orders"
        img_toolbar_back.setOnClickListener{
            log(TAG,"back press:")
            (getActivityBase() as MainActivity).pop()
        }
        setView(customSearchItem!!)

    }

    override fun handleBackButton(): Boolean {
        return false
    }


}


