package dk.eatmore.softtech360.testing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import dk.eatmore.softtech360.rest.ApiInterface;
import dk.eatmore.softtech360.model.UserDetail_model;
import dk.eatmore.softtech360.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Test extends AppCompatActivity {

    private String TAG=this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mathmetics();
    }

    private void mathmetics()
    {

        ApiInterface apiService =
                Test_two.getClient().create(ApiInterface.class);


        Call<List<UserDetail_model>> call = apiService.getTopRatedMovies();
        call.enqueue(new Callback<List<UserDetail_model>>() {
            @Override
            public void onResponse(Call<List<UserDetail_model>>call, Response<List<UserDetail_model>> response) {
                Log.e(TAG, "Number of movies received: " + response.body().size());
            }

            @Override
            public void onFailure(Call<List<UserDetail_model>>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });

    }


}
