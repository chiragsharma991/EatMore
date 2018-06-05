package dk.eatmore.softtech360.rest

import com.google.gson.JsonObject
import dk.eatmore.softtech360.model.GetReason
import dk.eatmore.softtech360.model.Order
import dk.eatmore.softtech360.model.ProductDetails
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {



    @FormUrlEncoded
    @POST("User/user/login")
    fun setLogin(
            @Field("username") username: String ,
            @Field("password_hash") password_hash: String,
            @Field("type") type: String,
            @Field("login_type") login_type: String,
            @Field("language") language: String
    ): Call<JsonObject>


    @POST("PosOrder/order/all-orders")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun myOrder(@Body  jsonObject: JsonObject): Call<Order>

    @FormUrlEncoded
    @POST("OrderRejectTemplate/order-reject-template/all_record")
    fun allRecords(
            @Field("r_token") r_token : String ,
            @Field("r_key") r_key: String,
            @Field("language") language: String

    ): Call<GetReason>


    @FormUrlEncoded
    @POST("PosOrder/order/accept_reject_arrival_order")
    fun rejectOrders(
            @Field("r_token") r_token: String ,
            @Field("r_key") r_key: String,
            @Field("order_no") order_no: String ,
            @Field("reason") reason: String,
            @Field("order_status") order_status: String,
            @Field("language") language: String

    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("PosOrder/order/accept_reject_arrival_order")
    fun acceptOrders(
            @Field("r_token") r_token: String ,
            @Field("r_key") r_key: String,
            @Field("order_no") order_no: String ,
            @Field("pickup_delivery_time") pickup_delivery_time: String,
            @Field("order_status") order_status: String,
            @Field("language") language: String

    ): Call<JsonObject>


    @FormUrlEncoded
    @POST("OrderRejectTemplate/order-reject-template/create_record")
    fun createOrder(
            @Field("r_token") r_token: String ,
            @Field("r_key") r_key: String,
            @Field("reason") reason: String ,
            @Field("action_by") action_by: String,
            @Field("language") language: String

    ): Call<JsonObject>


    @FormUrlEncoded
    @POST("OrderRejectTemplate/order-reject-template/update_record")
    fun updateOrder(
            @Field("r_token") r_token: String ,
            @Field("r_key") r_key: String,
            @Field("reason") reason: String ,
            @Field("action_by") action_by: String,  // user id
            @Field("or_id") or_id: String,
            @Field("language") language: String

    ): Call<JsonObject>


    @FormUrlEncoded
    @POST("PosOrder/order/latest_order_view_record")
    fun viewRecords(
            @Field("r_token") r_token: String ,
                @Field("r_key") r_key: String,
            @Field("order_no") reason: String,
            @Field("language") language: String

    ): Call<ProductDetails>

    @FormUrlEncoded
    @POST("User/user/device-token")
    fun sendFcmToken(
            @Field("r_token") r_token: String ,
            @Field("r_key") r_key: String,
            @Field("token") token: String,
            @Field("device_type") device_type: String,
            @Field("user_id") user_id: String

    ): Call<JsonObject>
}