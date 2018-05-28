package dk.eatmore.softtech360.model




data class ProductDetails(val msg: String = "",
                          val data: List<DataItem>?,
                          val status: Boolean = false)


data class DataItem(val discountAmount: String = "",
                    val smsOrderText: String = "",
                    val orderMonth: String = "",
                    val deviceType: String = "",
                    val uptoMinShipping: String = "",
                    val orderStatus: String = "",
                    val shipping: String = "",
                    val orderYear: String = "",
                    val acceptRejectTime: String = "",
                    val txnfee: String = "",
                    val expected_time: String = "",
                    val txnid: String = "",
                    val isReset: String = "",
                    val ip: String = "",
                    val vat: String = "",
                    val rejectReason: String = "",
                    val cardno: String = "",
                    val restaurant_name: String = "",
                    val paymentType: String = "",
                    val system: String = "",
                    val telephoneNo: String = "",
                    val orderMonthDate: String = "",
                    val totalToPay: String = "",
                    val shippingRemark: String = "",
                    val orderTotal: String = "",
                    val order_no: String = "",
                    val order_products_details: List<OrderProductsDetailsItem>?,
                    val posDoorStepDiscount: String = "",
                    val acceptTc: String = "",
                    val minimumOrderPrice: String = "",
                    val distance: String = "",
                    val restaurantId: String = "",
                    val notify: String = "",
                    val paymethod: String = "",
                    val isDeleted: String = "",
                    val shippingCosts: String = "",
                    val firstName: String = "",
                    val discountId: String = "",
                    val additionalCharge: String = "",
                    val address: String = "",
                    val comments: String = "",
                    val paymentStatus: String = "",
                    val lastName: String = "",
                    val requirement: String = "",
                    val discountType: String = "",
                    val restaurantSite: String = "",
                    val paymentModule: String = "",
                    val order_date: String = "",
                    val pickupDeliveryTime: String = "",
                    val orderPlacedFrom: String = "",
                    val epayPaymentHistory: String = "",
                    val previousOrder: String = "",
                    val subTotal: String = "",
                    val customerId: String = "",
                    val postalCode: String = "")

data class OrderProductsDetailsItem(val orderNo: String = "",
                                    val quantity: String = "",
                                    val restaurantId: String = "",
                                    val ip: String = "",
                                    val opId: String = "",
                                    val discount: String = "",
                                    val p_price: String = "",
                                    val pDate: String = "",
                                    val cId: String = "",
                                    val products: Products,
                                    val removed_ingredients: List<RemovedIngredientsItem>? =null,
                                    val ordered_product_attributes: List<OrderedProductAttributesItem>? =null ,
                                    val order_product_extra_topping_group: List<OrderProductExtraToppingGroupItem>?,
                                    val customerId: String = "",
                                    val pId: String = "")

data class Products(val featured: String = "",
                    val restaurantId: String = "",
                    val pDesc: String = "",
                    val actionDt: String = "",
                    val published: String = "",
                    val p_price: String = "",
                    val product_no: String = "",
                    val actionBy: String = "",
                    val system: String = "",
                    val isDeleted: String = "",
                    val isActivated: String = "",
                    val p_name: String = "",
                    val pOrder: String = "",
                    val extraToppingGroup: String = "",
                    val cId: String = "",
                    val is_attributes: String = "",
                    val pImage: String = "",
                    val pId: String = "")

data class RemovedIngredientsItem(val iId: String = "",
                                  val restaurantId: String = "",
                                  val ingredient_name: String = "",
                                  val opiId: String = "",
                                  val opId: String = "",
                                  val customerId: String = "")

data class OrderedProductAttributesItem(val order_product_extra_topping_group: List<OrderProductExtraToppingGroupItem>?,
                                        val padId: String = "",
                                        val aPrice: String = "",
                                        val tmId: String = "",
                                        val restaurantId: String = "",
                                        val opId: String = "",
                                        val attributeName: String = "",
                                        val opaId: String = "",
                                        val customerId: String = "",
                                        val attribute_value_name: String = "")

data class OrderProductExtraToppingGroupItem(val tPrice: String = "",
                                             val tsgdId: String = "",
                                             val restaurantId: String = "",
                                             val ingredient_name: String = "",
                                             val opId: String = "",
                                             val opaId: String = "",
                                             val customerId: String = "",
                                             val optId: String = "")












