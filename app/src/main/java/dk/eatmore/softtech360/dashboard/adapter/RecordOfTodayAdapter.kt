package dk.eatmore.softtech360.dashboard.adapter

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.dashboard.fragment.order.OrderDetails
import dk.eatmore.softtech360.dashboard.fragment.order.RecordOfLast30Days
import dk.eatmore.softtech360.dashboard.fragment.order.RecordOfLast7Days
import dk.eatmore.softtech360.dashboard.fragment.order.RecordOfToday
import dk.eatmore.softtech360.dashboard.main.MainActivity
import dk.eatmore.softtech360.model.CustomSearchItem
import dk.eatmore.softtech360.testing.Test_two.getCalculatedDate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_order_list.*
import java.text.SimpleDateFormat
import java.util.*

class RecordOfTodayAdapter(private val mListOrder: ArrayList<CustomSearchItem?>, private val mListNewOrder: ArrayList<CustomSearchItem?>,
                           private val mListAnsweredOrder: ArrayList<CustomSearchItem?>, var fragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {


    private val VIEW_ITEM = 1
    private val VIEW_LABEL = 0


    class MyViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {



        fun init(msg: CustomSearchItem, mListNewOrder: ArrayList<CustomSearchItem?>, mListAnsweredOrder: ArrayList<CustomSearchItem?>) {

            val currentDate = getCalculatedDate("yyyy-MM-dd", 0)
            if (msg.showOrderHeader) row_order_header.visibility = View.VISIBLE
            else row_order_header.visibility = View.GONE
            Log.e("msg.headerType ",msg.headerType )
            if (msg.headerType == "mListNewOrder") {
                row_order_header_txt.text = "New Orders (${mListNewOrder.size})"
                if (currentDate == msg.order_month_date) row_order_typebtn.visibility = View.VISIBLE else row_order_typebtn.visibility = View.GONE
            } else {
                row_order_header_txt.text = "Answered Orders (${mListAnsweredOrder.size})"
                row_order_typebtn.visibility = View.GONE
            }

            var mcontext = row_order_no.context
            row_order_no.text = "Order No." + msg.order_id
            row_order_date.text = msg.order_date
            row_order_name.text = msg.name
            row_order_address.text = msg.address
            row_order_distance.text = msg.distance
            row_order_phn.text = "Phone " + msg.contact_no
            row_order_status.text = msg.order_status
        }


    }

    class LableViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {


        fun init(msg: CustomSearchItem, mListNewOrder: ArrayList<CustomSearchItem?>, mListAnsweredOrder: ArrayList<CustomSearchItem?>) {

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var vh: RecyclerView.ViewHolder
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
            holder.init(mListOrder[position]!!, mListNewOrder, mListAnsweredOrder)
            holder.row_order_reject.setTag(position)
            holder.row_order_accept.setTag(position)
            holder.row_order_details.setTag(position)
            holder.row_order_cardview.setTag(position)

            holder.row_order_cardview.setOnClickListener(this)
            holder.row_order_reject.setOnClickListener(this)
            holder.row_order_accept.setOnClickListener(this)
            holder.row_order_details.setOnClickListener(this)

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

                (fragment.activity as MainActivity).addFragment(R.id.main_container_layout, OrderDetails.newInstance((mListOrder.get(position)) as CustomSearchItem), OrderDetails.TAG)
            }

            R.id.row_order_accept -> {
                if(fragment is RecordOfToday)
                    (fragment as RecordOfToday).performAction(2,mListOrder.get(position)!!)
                if(fragment is RecordOfLast7Days)
                    (fragment as RecordOfLast7Days).performAction(2,mListOrder.get(position)!!)
                if(fragment is RecordOfLast30Days)
                    (fragment as RecordOfLast30Days).performAction(2,mListOrder.get(position)!!)
            }
            R.id.row_order_details -> {
                if(fragment is RecordOfToday)
                    (fragment as RecordOfToday).performAction(1,mListOrder.get(position)!!)
                if(fragment is RecordOfLast7Days)
                    (fragment as RecordOfLast7Days).performAction(1,mListOrder.get(position)!!)
                if(fragment is RecordOfLast30Days)
                    (fragment as RecordOfLast30Days).performAction(1,mListOrder.get(position)!!)
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


}