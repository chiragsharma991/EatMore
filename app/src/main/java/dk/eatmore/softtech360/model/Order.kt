package dk.eatmore.softtech360.model

import com.google.gson.annotations.SerializedName




data class Order(
                     val custom_search: List<CustomSearchItem>,
                     val status: Boolean = false)




data class CustomSearchItem(

        var showOrderHeader: Boolean =false,
        var headerType: String ="",
        val address: String = "",
        val distance: String? = null,
        val discount_amount: String = "",
        val payment_status: String = "",
        val shipping_type: String = "",
        val reject_reason: String = "",
        val discount_type: String? = null,
        val asap: String = "",
        val platform: String = "",
        val order_date: String = "",
        val order_status: String = "",
        val pickup_delivery_time: String = "",
        val payment_type: String = "",
        val total: String = "",
        val contact_no: String = "",
        val order_month_date: String = "",
        val accept_reject_time: String = "",
        val name: String = "",
        val order_id: String = "",
        val expected_time: String = "")

/*

"order_id": "3522",
"payment_type": "1",
"accept_reject_time": "2018-04-14 14:48:42",
"address": "Sydney Sydney, 1060 K\u00f8benhavn K",
"platform": "Android",
"discount_type": null,
"discount_amount": "0.00",
"distance": null,
"expected_time": "2017-05-08 10:07:00",
"name": "Mital",
"order_date": "2017-05-08 09:22:59",
"order_status": "Canceled",
"payment_status": "Not Paid",
"reject_reason": "Address not possible",
"asap": "asap",
"shipping_type": "Delivery",
"contact_no": "9632147850",
"total": "0.00",
"order_month_date": "2017-05-08",
"pickup_delivery_time": "2017-05-08 10:22:00"*/
