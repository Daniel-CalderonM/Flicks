package com.example.calderond.flicks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calderond.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static com.example.calderond.flicks.MovieListActivity.API_BASE_URL;

public class MovieDetailsActivity extends AppCompatActivity {

    // the movie to display
    Movie movie;
    String videoKey="";

    // the view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    Button btn ;
    //instance fields
    AsyncHttpClient client;
    String url = API_BASE_URL + "/movie/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        client =  new AsyncHttpClient();
        btn = (Button) findViewById(R.id.btnSubmit);
        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTrailer();
            }
        });
        // resolve the view objects
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

    }
    public void getTrailer(){
        //Gettign the trailer
        url= API_BASE_URL + "/movie/";
        url+=Integer.toString(movie.getId())+"/videos"+"?api_key="+getString(R.string.api_key);
        Log.i("INof",url);
        client.get(url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray videoJsonResults = null; // gets the results array
                try {
                    videoJsonResults = response.getJSONArray("results");
                    JSONObject result = videoJsonResults.getJSONObject(0); // get the first json object for video
                    // STORE the KEY for later use
                    videoKey = result.getString("key");
                    //Toast.makeText(getApplicationContext(), "second"+videoKey,Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getBaseContext(), MovieTrailerActivity.class);
                    //Toast.makeText(getApplicationContext(),"first"+videoKey,Toast.LENGTH_LONG).show();
                    i.putExtra("YOUTUBE_ID",videoKey);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Null",Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(),"Wrong error",Toast.LENGTH_LONG).show();
            }
        });
    }

}