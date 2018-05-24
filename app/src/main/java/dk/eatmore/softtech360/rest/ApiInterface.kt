package dk.eatmore.softtech360.rest

import com.google.gson.JsonObject
import dk.eatmore.softtech360.dashboard.fragment.Task
import dk.eatmore.softtech360.model.GetReason
import dk.eatmore.softtech360.model.Order
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {



    @FormUrlEncoded
    @POST("User/user/login")
    fun setLogin(
            @Field("username") username: String ,
            @Field("password_hash") password_hash: String,
            @Field("type") type: String,
            @Field("login_type") login_type: String): Call<JsonObject>


    @POST("PosOrder/order/all-orders")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun myOrder(@Body  jsonObject: JsonObject): Call<Order>

    @FormUrlEncoded
    @POST("OrderRejectTemplate/order-reject-template/all_record")
    fun allRecords(
            @Field("r_token") r_token : String ,
            @Field("r_key") r_key: String
    ): Call<GetReason>


    @FormUrlEncoded
    @POST("PosOrder/order/accept_reject_arrival_order")
    fun rejectOrders(
            @Field("r_token") r_token: String ,
            @Field("r_key") r_key: String,
            @Field("order_no") order_no: String ,
            @Field("reason") reason: String,
            @Field("order_status") order_status: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("PosOrder/order/accept_reject_arrival_order")
    fun acceptOrders(
            @Field("r_token") r_token: String ,
            @Field("r_key") r_key: String,
            @Field("order_no") order_no: String ,
            @Field("pickup_delivery_time") pickup_delivery_time: String,
            @Field("order_status") order_status: String
    ): Call<JsonObject>


    @FormUrlEncoded
    @POST("OrderRejectTemplate/order-reject-template/create_record")
    fun createOrder(
            @Field("r_token") r_token: String ,
            @Field("r_key") r_key: String,
            @Field("reason") reason: String ,
            @Field("action_by") action_by: String
    ): Call<JsonObject>


    @FormUrlEncoded
    @POST("OrderRejectTemplate/order-reject-template/update_record")
    fun updateOrder(
            @Field("r_token") r_token: String ,
            @Field("r_key") r_key: String,
            @Field("reason") reason: String ,
            @Field("action_by") action_by: String,  // user id
            @Field("or_id") or_id: String
    ): Call<JsonObject>
}