package dk.eatmore.softtech360.testing;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Test_two {

    public static final String BASE_URL = "https://simplifiedcoding.net/demos/";
    private static Retrofit retrofit = null;


    public static void main(String[] args) {
        // this code will be run yyyy-MM-dd
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        System.out.print( getCalculatedDate("yyyy-MM-dd", 0));
       ;
        ;

        // Log.e("tag -----", "main: ");
    }


    public static String getCalculatedDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }

}
