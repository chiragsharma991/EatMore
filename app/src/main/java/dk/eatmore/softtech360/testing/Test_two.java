package dk.eatmore.softtech360.testing;

import android.util.Log;

import java.text.DateFormat;
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
     //   String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


      //  System.out.print( getCalculatedDate("yyyy-MM-dd", 0));
        //            "expected_time": "2018-05-22 16:45:00",

        Test_two test_two=new Test_two();
        test_two.test();
       // test();

    }


    public static String getCalculatedDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }

    public  static  void test(){

        try {
            System.out.println("Test--");
            Date date;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = formatter.parse("2018-05-22 16:45:00");
            //Subtracting 6 hours from selected time
            long time = date.getTime();

            String mDate = null;
            SimpleDateFormat formatterFullDate = new SimpleDateFormat("yyyy:MM:dd");
            mDate = formatterFullDate.format(time);
          //  Log.e("Date ",mDate );
            System.out.println("mDate--"+mDate);

            String mTime = null;
            SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm:ss");
            int addmin=0;
            for (int i = 0; i <9 ; i++) {
                long t = date.getTime()+addmin*60*1000;
                mTime = formatterTime.format(t);
                System.out.println("mTime--"+mTime+"("+addmin+" min )");
                addmin+=15;
            }
            formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            date = formatter.parse(mDate+" "+mTime);
            formatter.format(date);
            long t = date.getTime();
            String selcetedTime=formatter.format(t);
            System.out.println("Final Time--"+selcetedTime);





            //Log.e("Time ",mTime );

        } catch (Exception e) {
            //Log.e("Exception", "" + e.getMessage());
            System.out.print("Exception--"+e.getMessage());
        }
    }

}
