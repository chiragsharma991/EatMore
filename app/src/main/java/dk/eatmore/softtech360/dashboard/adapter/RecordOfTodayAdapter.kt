package dk.eatmore.softtech360.dashboard.adapter

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.eatmore.softtech360.R
import dk.eatmore.softtech360.model.CustomSearchItem
import dk.eatmore.softtech360.utils.BaseFragment
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_order_list.*

class RecordOfTodayAdapter(private val mListOrder: ArrayList<CustomSearchItem?>, var fragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_ITEM = 1
    private val VIEW_LABEL = 0


    class MyViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {


        fun init(msg: CustomSearchItem) {

            var mcontext=row_order_no.context
            row_order_no.text="Order No."+msg.order_id
            row_order_date.text=msg.order_date
            row_order_name.text=msg.name
            row_order_address.text=msg.address
            row_order_distance.text=msg.distance
            row_order_phn.text="Phone "+msg.contact_no
            row_order_status.text=msg.order_status
        }
    }
    class LableViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {


        fun init(msg: CustomSearchItem) {


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
                    .inflate(R.layout.row_order_type, parent, false)
            vh = LableViewHolder(itemView)
        }

        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            var holder: MyViewHolder = holder
            holder.init(mListOrder[position]!!)

            /*holder.txt_status.setOnClickListener {
                if(filterList[position]?.OrderStatus == ACCEPTED){
                    (fragment as AdminDashboardHistoryFragment).openShipementActivity(""+filterList[position]!!.OrderId)
                }
            }*/
        } else if (holder is LableViewHolder) {
            var holder: LableViewHolder = holder
            holder.init(mListOrder[position]!!)

        }
    }

    override fun getItemCount(): Int {
        return mListOrder.size
    }

    override fun getItemViewType(position: Int): Int {
        return  VIEW_ITEM
    }


}