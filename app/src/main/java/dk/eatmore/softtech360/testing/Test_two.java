package dk.eatmore.softtech360.testing;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Test_two {

    public static final String BASE_URL = "https://simplifiedcoding.net/demos/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


}
