package dk.eatmore.softtech360.rest

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

 internal object ApiClient{

    val BASE_URL = "https://simplifiedcoding.net/demos/"
    private var retrofit: Retrofit? = null


    fun getClient(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit
    }


}



