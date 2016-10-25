package lars.be.movieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.omertron.themoviedbapi.model.movie.MovieInfo;

public class MovieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent i = getIntent();
        MovieInfo movieInfo = (MovieInfo) i.getExtras().get("movieInfo");




    }
}
