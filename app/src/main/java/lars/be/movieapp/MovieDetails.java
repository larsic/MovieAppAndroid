package lars.be.movieapp;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.omertron.themoviedbapi.model.credits.MediaCreditCast;
import com.omertron.themoviedbapi.model.movie.MovieInfo;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static lars.be.movieapp.R.id.imageView;

public class MovieDetails extends AppCompatActivity implements AppController.OnMovieListChangedListener {

    private ImageView imageViewDetails;
    private TextView movieTitle;
    private TextView movieDescription;
    private TextView movieRuntime;
    private TextView movieRuntimeData;
    private TextView movieScore;
    private TextView movieScoreData;
    private LinearLayout myGallery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);


        Intent i = getIntent();
        MovieInfo movieInfo = (MovieInfo) i.getExtras().get("movieInfo");
        imageViewDetails = (ImageView) findViewById(R.id.imageViewDetails);
        movieTitle = (TextView) findViewById(R.id.movieTitle);
        movieDescription = (TextView) findViewById(R.id.movieDescription);
        movieRuntime = (TextView) findViewById(R.id.movieRuntime);
        movieRuntimeData = (TextView) findViewById(R.id.movieRuntimeData);
        movieScore= (TextView) findViewById(R.id.movieScore);
        movieScoreData = (TextView) findViewById(R.id.movieScoreData);


        myGallery = (LinearLayout)findViewById(R.id.mygallery);

        URL imageUrl = null;

        imageUrl = AppController.getInstance().createImageUrl(movieInfo.getBackdropPath(), "w500");
        ImageLoader.getInstance().displayImage(imageUrl.toString(), imageViewDetails);

        movieTitle.setText(movieInfo.getTitle()+" ("+ movieInfo.getReleaseDate().substring(0, 4) +")");
        movieDescription.setText(movieInfo.getOverview());

        movieRuntime.setText("Movie length: ");
        movieRuntimeData.setText(movieInfo.getRuntime()+ " minutes");

        movieScore.setText(("Movie score: "));
        movieScoreData.setText(movieInfo.getVoteAverage() + " (" + movieInfo.getVoteCount() + " votes)");



        AppController.getInstance().fetchCast(movieInfo.getId());



    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.getInstance().addOnMovieListChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.getInstance().removeOnMovieListChangedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println(item.getItemId());
        if(item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }

    @Override
    public void onMovieListChanged() {
        for (MediaCreditCast cast : AppController.getInstance().cast){
            myGallery.addView(insertCastPicture(cast.getArtworkPath()));
        }
    }

    private View insertCastPicture(String artworkPath) {
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new LayoutParams(92, 120));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (artworkPath != null) {
            ImageLoader.getInstance().displayImage(AppController.getInstance().createImageUrl(artworkPath, "w92").toString(), imageView);
        } else {
            ImageLoader.getInstance().displayImage(null, imageView);
        }
        return imageView;
    }


}
