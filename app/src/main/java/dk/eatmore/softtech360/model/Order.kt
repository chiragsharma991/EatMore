package dk.eatmore.softtech360.model

data class Order( val customSearch: List<CustomSearchItem>,
                  val status: Boolean  )


data class CustomSearchItem
(
                            val address: String = "",
                            val distance: String = "",
                            val discountAmount: String = "",
                            val paymentStatus: String = "",
                            val shippingType: String = "",
                            val rejectReason: String = "",
                            val discountType: String = "",
                            val asap: String = "",
                            val platform: String = "",
                            val orderDate: String = "",
                            val orderStatus: String = "",
                            val pickupDeliveryTime: String = "",
                            val paymentType: String = "",
                            val total: String = "",
                            val contactNo: String = "",
                            val orderMonthDate: String = "",
                            val acceptRejectTime: String = "",
                            val name: String = "",
                            val orderId: String = "",
                            val expectedTime: String = ""
)