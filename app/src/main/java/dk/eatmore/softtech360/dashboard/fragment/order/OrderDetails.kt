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
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.notification_template_lines_media.*
import kotlinx.android.synthetic.main.row_order_attribute.view.*


class  OrderDetails : BaseFragment(){

   val mListOrderDetails = ArrayList<OrderDetails>()

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



    override fun initView(view: View?, savedInstanceState: Bundle?) {
        initToolbar()
        view_empty.visibility = View.GONE
        order_detail_view.visibility =View.GONE
        r_key = PreferenceUtil.getString(PreferenceUtil.R_KEY, "")!!
        r_token = PreferenceUtil.getString(PreferenceUtil.R_TOKEN, "")!!
        val customSearchItem = arguments!!.getSerializable("MODEL") as CustomSearchItem
        log(TAG,customSearchItem.order_id)
        fetchOrders(false, r_key, r_token,customSearchItem.order_id)


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
                    order_detail_rest.text = data!!.get(0).restaurant_name
                    order_detail_shipping.text = data!!.get(0).shipping
                    order_detail_expt_time.text = data!!.get(0).expected_time
                    order_detail_req.text = data!!.get(0).requirement
                    order_detail_dynamic_container.removeAllViews()


                    for (i in 0..data.get(0).order_products_details!!.size -1){
                        log("order_products_details size","")
                        //--- product name ---
                        var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                        var view = inflater.inflate(R.layout.row_order_product, null)
                        view.row_order_product.text = data.get(0).order_products_details!!.get(i).quantity +"x"+
                                data.get(0).order_products_details!!.get(i).products.product_no+
                                data.get(0).order_products_details!!.get(i).products.p_name
                        view.row_order_product_price.text =data.get(0).order_products_details!!.get(i).p_price
                        order_detail_dynamic_container.addView(view)

                        // --- ingradient ---

                        if(data.get(0).order_products_details?.get(i)?.removed_ingredients?.size != null){

                            for (j in 0..data.get(0).order_products_details!!.get(i).removed_ingredients!!.size -1){

                                view = inflater.inflate(R.layout.row_order_ingradient, null)
                                view.row_order_ingradient.text ="-"+data.get(0).order_products_details!!.get(i).removed_ingredients!!.get(j).ingredient_name
                                order_detail_dynamic_container.addView(view)

                            }
                        }

                        // --- attributes ---

                        if(data.get(0).order_products_details!!.get(i).products.is_attributes == "1"){

                            // with extratoppings

                            for (k in 0..data.get(0).order_products_details!!.get(i).ordered_product_attributes!!.size -1){

                                // Add attribute Header---
                                view = inflater.inflate(R.layout.row_order_attribute, null)
                                view.row_order_attribute.removeAllViewsInLayout()
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
                                view.row_order_attribute.addView(textView)
                                order_detail_dynamic_container.addView(view)


                                // Add divider---
                              /*  val divider = View(context)
                                parms = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                                divider.alpha = 0.2F
                                divider.setBackgroundColor(ContextCompat.getColor(context!!,R.color.border_gray))
                                view.row_order_attribute.addView(divider)
                                order_detail_dynamic_container.addView(view)*/


                                // Add attribute of sizes---
                                for (l in 0..data.get(0).order_products_details!!.get(i).ordered_product_attributes!!.get(k).order_product_extra_topping_group!!. size-1){
                                  //  view = inflater.inflate(R.layout.row_order_attribute, null)
                                  //  view.row_order_attribute.removeAllViewsInLayout()
                                    val textView = AppCompatTextView(context)
                                    textView.gravity = Gravity.LEFT
                                    textView.setSingleLine()
                                    //  textView.typeface = Typeface.DEFAULT_BOLD
                                    textView.setTextAppearance(context,android.R.style.TextAppearance_Small)
                                    textView.text ="+"+data.get(0).order_products_details!!.get(i).
                                            ordered_product_attributes!!.get(k).order_product_extra_topping_group!!.get(l).ingredient_name
                                    textView.setLayoutParams(parms)
                                    view.row_order_attribute.addView(textView)
                                    order_detail_dynamic_container.addView(view)


                                }



                            }


                        }
                        else{
                            // Without extratoppings

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
                                view.row_order_attribute.addView(textView)
                                order_detail_dynamic_container.addView(view)


                            }



                        }



                    }



                } else {
                    progress_bar.visibility = View.GONE
                    order_detail_view.visibility =View.GONE
                    view_empty.visibility = View.VISIBLE
                    view_empty_txt_data.text =getString(R.string.no_data_available)

                }

            }

            override fun onFail(error: Int) {
                (parentFragment as OrderInfoFragment).showPreogressBar(false)
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

    }

    override fun handleBackButton(): Boolean {
        return false
    }


}