package dk.eatmore.partner.model




data class ProductDetails(val msg: String = "",
                          val data: List<DataItem>?,
                          val status: Boolean = false): ModelUtility()


data class DataItem(val discount_amount: String? = null,
                    val smsOrderText: String = "",
                    val orderMonth: String = "",
                    val deviceType: String = "",
                    val upto_min_shipping: String? = null,
                    val order_status: String = "",
                    val shipping: String = "",
                    val orderYear: String = "",
                    val accept_reject_time: String? = null,
                    val txnfee: String = "",
                    val expected_time: String = "",
                    val txnid: String = "",
                    val isReset: String = "",
                    val ip: String = "",
                    val vat: String = "",
                    val reject_reason: String? = null,
                    val cardno: String = "",
                    val restaurant_name: String = "",
                    val paymentType: String = "",
                    val system: String = "",
                    val telephone_no: String = "",
                    val orderMonthDate: String = "",
                    val total_to_pay: String? = null,
                    val shipping_remark: String? = null,
                    val order_total: String? = null,
                    val order_no: String = "",
                    val order_products_details: List<OrderProductsDetailsItem>?,
                    val posDoorStepDiscount: String = "",
                    val acceptTc: String = "",
                    val minimumOrderPrice: String = "",
                    val distance: String = "",
                    val restaurant_id: String = "",
                    val notify: String = "",
                    val paymethod: String = "",
                    val isDeleted: String = "",
                    val shipping_costs: String? = null,
                    val first_name: String = "",
                    val discountId: String = "",
                    val additional_charge: String? = null,
                    val address: String = "",
                    val comments: String? = "",
                    val payment_status: String = "",
                    val lastName: String = "",
                    val requirement: String? = "",
                    val discountType: String = "",
                    val restaurant_site: String? = null,
                    val paymentModule: String = "",
                    val order_date: String = "",
                    val pickup_delivery_time: String = "",
                    val orderPlacedFrom: String = "",
                    val epayPaymentHistory: String = "",
                    val previous_order: String = "",
                    val sub_total: String? = null,
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












