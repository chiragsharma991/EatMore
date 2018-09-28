package dk.eatmore.partner.dashboard.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epson.epos2.printer.Printer
import dk.eatmore.partner.R
import dk.eatmore.partner.dashboard.fragment.order.*
import dk.eatmore.partner.dashboard.main.MainActivity
import dk.eatmore.partner.model.CustomSearchItem
import dk.eatmore.partner.model.DataItem
import dk.eatmore.partner.utils.ConversionUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_order_list.*
import java.text.SimpleDateFormat
import java.util.*

class RecordOfTodayAdapter(private val mListOrder: ArrayList<CustomSearchItem?>, private val mListNewOrder: ArrayList<CustomSearchItem?>,
                           private val mListAnsweredOrder: ArrayList<CustomSearchItem?>, var fragment: Fragment,  context: Context,val callback: AdapterListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener  {


    private val VIEW_ITEM = 1
    private val VIEW_LABEL = 0
    val context :Context =context
    private lateinit var data: List<DataItem>
    var mPrinter: Printer?=null
    lateinit var listner: AdapterListener





    class MyViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {


        private var pickup_delivery_time: Long = 0

        fun init(msg: CustomSearchItem, mListNewOrder: ArrayList<CustomSearchItem?>, mListAnsweredOrder: ArrayList<CustomSearchItem?>, context: Context) {

            val currentDate = ConversionUtils.getCalculatedDate("yyyy-MM-dd", 0)
            if (msg.showOrderHeader) row_order_header.visibility = View.VISIBLE
            else row_order_header.visibility = View.GONE
            Log.e("msg.headerType ",msg.headerType )

            if (msg.headerType == "mListNewOrder") {
                row_order_header_txt.text ="${context.getString(R.string.new_orders)} (${mListNewOrder.size})"
                if (currentDate == msg.order_month_date){
                    // if both date are equals:
                    row_order_typebtn.visibility = View.VISIBLE
                    row_order_upcoming_status_view.visibility = View.GONE
                    row_order_upcoming_view.visibility = View.VISIBLE
                    print_btn.visibility=View.GONE


                }else{
                    // date are not equal and not rejected/accepted
                    row_order_typebtn.visibility = View.GONE
                    row_order_upcoming_status_view.visibility = View.VISIBLE
                    row_order_upcoming_view.visibility = View.GONE
                    if(msg.order_status.capitalize().toUpperCase()=="ACCEPTED" || msg.order_status.capitalize().toUpperCase()=="REJECTED")
                        print_btn.visibility = View.VISIBLE
                     else
                    print_btn.visibility=View.GONE

                }

            } else {
                // previous order which accepted/rejected.
                row_order_header_txt.text = "${context.getString(R.string.answered_orders)} (${mListAnsweredOrder.size})"
                row_order_typebtn.visibility = View.GONE
                row_order_upcoming_status_view.visibility = View.VISIBLE
                row_order_upcoming_view.visibility = View.GONE
                if(msg.order_status.capitalize().toUpperCase()=="ACCEPTED" || msg.order_status.capitalize().toUpperCase()=="REJECTED")
                    print_btn.visibility = View.VISIBLE
                 else
                    print_btn.visibility=View.GONE

            }

            if(msg.pickup_delivery_time !=null) pickup_delivery_time = ConversionUtils.getCalculatedTime(msg.pickup_delivery_time!!, "yyyy-MM-dd HH:mm:ss")
            var mcontext = row_order_no.context
            row_order_no.text = "${context.getString(R.string.order_no)} ${ msg.order_id}"
            row_order_date.text = msg.order_date
            row_order_name.text = msg.name
            row_order_address.text = msg.address
            row_order_distancelbl.visibility = if (msg.distance !=null) View.VISIBLE else View.INVISIBLE
            row_order_distance.text = msg.distance
            row_order_phn.text = "${context.getString(R.string.phone)} " + msg.contact_no

            row_order_type.text = if(msg.shipping_type?.capitalize()?.toUpperCase() =="DELIVERY") context.getString(R.string.delivery) else context.getString(R.string.pick_up)
            row_order_total.text = msg.total

            val expectedTime = ConversionUtils.getCalculatedTime(msg.expected_time, "yyyy-MM-dd HH:mm:ss")
            val time = SimpleDateFormat("HH:mm").format(expectedTime)
            row_order_requirement.text = if(msg.asap?.capitalize()?.toUpperCase() =="ASAP")
                "${context.getString(R.string.asap)} (${time})"
            else
                "${context.getString(R.string.preorder)} (${time})"

            row_order_payment_status.text = if(msg.payment_status?.capitalize()?.toUpperCase() == "NOT PAID") context.getString(R.string.not_paid) else context.getString(R.string.paid)


            row_order_status.text = if (msg.order_status == "Accepted") "${context.getString(R.string.order_accept_txt)} ${SimpleDateFormat("HH:mm").format(pickup_delivery_time)}"
            else  if (msg.order_status == "Rejected") "${context.getString(R.string.reject)} ${if (msg.reject_reason !=null) ": "+msg.reject_reason else ""}"
            //else  if (msg.order_status == "Rejected") "${R.string.reject} ${if (msg.reject_reason !=null) ":"+msg.reject_reason else ""}"
            else msg.order_status
        }




    }

    class LableViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {


        fun init(msg: CustomSearchItem, mListNewOrder: ArrayList<CustomSearchItem?>, mListAnsweredOrder: ArrayList<CustomSearchItem?>) {

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var vh: RecyclerView.ViewHolder
        this.listner = callback
        if (viewType == VIEW_ITEM) {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_order_list, parent, false)
            vh = MyViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_order_product, parent, false)
            vh = LableViewHolder(itemView)
        }

        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            var holder: MyViewHolder = holder
            holder.init(mListOrder[position]!!, mListNewOrder, mListAnsweredOrder,context)
            holder.row_order_reject.setTag(position)
            holder.row_order_accept.setTag(position)
            holder.row_order_details.setTag(position)
            holder.row_order_cardview.setTag(position)

            holder.row_order_cardview.setOnClickListener(this)
            holder.row_order_reject.setOnClickListener(this)
            holder.row_order_accept.setOnClickListener(this)
            holder.row_order_details.setOnClickListener(this)

            holder.print_btn.setOnClickListener{

                listner.printOn(position)
            }


        } else if (holder is LableViewHolder) {
            var holder: LableViewHolder = holder
            holder.init(mListOrder[position]!!, mListNewOrder, mListAnsweredOrder)

        }
    }

    override fun getItemCount(): Int {
        return mListOrder.size
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_ITEM
    }

    fun getCalculatedDate(dateFormat: String, days: Int): String {
        val cal = Calendar.getInstance()
        val s = SimpleDateFormat(dateFormat)
        cal.add(Calendar.DAY_OF_YEAR, days)
        return s.format(Date(cal.timeInMillis))
    }


    override fun onClick(v: View?) {
        val position =v!!.getTag() as Int

        when(v!!.id) {

            R.id.row_order_cardview ->{
               //val fragment : OrderDetails.Companion

                (fragment.activity as MainActivity).addFragment(R.id.main_container_layout, OrderDetails.newInstance((mListOrder.get(position)) as CustomSearchItem), OrderDetails.TAG,false)
            }

            R.id.row_order_accept -> {
                // i am using 3 condition for only casting perpose.
                if(fragment is RecordOfToday)
                    (fragment as RecordOfToday).performAction(2,mListOrder.get(position)!!)
                if(fragment is RecordOfLast7Days)
                    (fragment as RecordOfLast7Days).performAction(2,mListOrder.get(position)!!)
                if(fragment is RecordOfLast30Days)
                    (fragment as RecordOfLast30Days).performAction(2,mListOrder.get(position)!!)
            }
            R.id.row_order_details -> {
                (fragment.activity as MainActivity).addFragment(R.id.main_container_layout, OrderDetails.newInstance((mListOrder.get(position)) as CustomSearchItem), OrderDetails.TAG,false)

            }
            R.id.row_order_reject -> {
                if(fragment is RecordOfToday)
                    (fragment as RecordOfToday).performAction(0,mListOrder.get(position)!!)
                if(fragment is RecordOfLast7Days)
                    (fragment as RecordOfLast7Days).performAction(0,mListOrder.get(position)!!)
                if(fragment is RecordOfLast30Days)
                    (fragment as RecordOfLast30Days).performAction(0,mListOrder.get(position)!!)
            }
        }

    }



    interface AdapterListener {
        fun printOn(parentPosition: Int)
    }

}