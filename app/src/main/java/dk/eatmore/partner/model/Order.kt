package dk.eatmore.partner.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Order(
        val custom_search: List<CustomSearchItem>,val error: String = "",
        val status: Boolean = false)


data class CustomSearchItem(

        var showOrderHeader: Boolean = false,
        var headerType: String = "",
        val address: String = "",
        val distance: String? = null,
        val discount_amount: String = "",
        val payment_status: String? = "",
        val shipping_type: String? = "",
        val reject_reason: String? = null,
        val discount_type: String? = null,
        val asap: String? = "",
        val platform: String = "",
        val order_date: String = "",
        val order_status: String = "",
        val pickup_delivery_time: String? = null,
        val payment_type: String = "",
        val total: String = "",
        val contact_no: String = "",
        val order_month_date: String = "",
        val accept_reject_time: String = "",
        val name: String = "",
        val order_id: String = "",
        val expected_time: String = ""

) : Serializable

