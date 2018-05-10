package dk.eatmore.softtech360.rest

import dk.eatmore.softtech360.model.UserDetail_model
import retrofit2.Call
import retrofit2.http.GET


interface ApiInterface {

    @GET("marvel")
    fun getTopRatedMovies(): Call<List<UserDetail_model>>?;

}