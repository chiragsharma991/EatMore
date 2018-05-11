package dk.eatmore.softtech360.rest

import com.google.gson.JsonObject
import dk.eatmore.softtech360.model.UserDetail_model
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiInterface {

    @GET("marvel")
    fun getTopRatedMovies(): Call<List<UserDetail_model>>?

    @FormUrlEncoded
    @POST("User/user/login")
    fun setLogin(
            @Field("username") username: String,
            @Field("password_hash") password_hash: String,
            @Field("type") type: String,
            @Field("login_type") login_type: String): Call<JsonObject>


}