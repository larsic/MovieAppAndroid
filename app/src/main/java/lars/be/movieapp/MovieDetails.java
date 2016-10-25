package lars.be.movieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.omertron.themoviedbapi.model.movie.MovieInfo;

import java.net.URL;

import static lars.be.movieapp.R.id.imageView;

public class MovieDetails extends AppCompatActivity {

    private ImageView imageViewDetails;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent i = getIntent();
        MovieInfo movieInfo = (MovieInfo) i.getExtras().get("movieInfo");

        imageViewDetails = (ImageView) findViewById(R.id.imageViewDetails);

        URL imageUrl = null;

        imageUrl = AppController.getInstance().createImageUrl(movieInfo.getBackdropPath(), "w500");
        ImageLoader.getInstance().displayImage(imageUrl.toString(), imageViewDetails);


    }
}
