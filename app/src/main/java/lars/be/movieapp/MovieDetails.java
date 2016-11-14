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
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.omertron.themoviedbapi.model.credits.MediaCreditCast;
import com.omertron.themoviedbapi.model.media.Video;
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
    private TextView movieTagLine;


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
        movieTagLine = (TextView) findViewById(R.id.movieTagline);


        myGallery = (LinearLayout)findViewById(R.id.mygallery);

        URL imageUrl = null;
        String movieBackdropPath = movieInfo.getBackdropPath();
        imageUrl = AppController.getInstance().createImageUrl(movieBackdropPath, "w500");

        if(movieBackdropPath != null) {

            ImageLoader.getInstance().displayImage(imageUrl.toString(), imageViewDetails);
        } else {
            //ImageLoader.getInstance().displayImage(imageUrl.toString(), imageViewDetails);
            //holder.getImageView().setImageResource(R.drawable.ic_menu_camera);

            //NOT SHOWING ANY PIC
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setImageResource(R.drawable.ic_menu_camera);
        }



        if (movieInfo.getReleaseDate().length() > 3) {
            movieTitle.setText(movieInfo.getTitle() + " (" + movieInfo.getReleaseDate().substring(0, 4) + ")");
        }    else {
            movieTitle.setText(movieInfo.getTitle());
        }
        if(movieInfo.getTagline() != null && !movieInfo.getTagline().trim().equals("")) {
            movieTagLine.setText(movieInfo.getTagline());
        } else {
            movieTagLine.setTextSize(0);

        }

        movieDescription.setText(movieInfo.getOverview());

        movieRuntime.setText("Movie length: ");
        movieRuntimeData.setText(movieInfo.getRuntime()+ " minutes");

        movieScore.setText(("Movie score: "));
        movieScoreData.setText(movieInfo.getVoteAverage() + " (" + movieInfo.getVoteCount() + " votes)");


        List<Video> videos = movieInfo.getVideos();
        Video video = videos.get(0);


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
            myGallery.addView(insertCastPicture(cast));
        }
    }

    private View insertCastPicture(MediaCreditCast cast) {

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new LayoutParams(92, 120));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (cast.getArtworkPath() != null) {
            ImageLoader.getInstance().displayImage(AppController.getInstance().createImageUrl(cast.getArtworkPath(), "w92").toString(), imageView);
        } else {
            //ImageLoader.getInstance().displayImage(null, imageView);
            imageView.setImageResource(R.drawable.ic_menu_camera);
        }




        TextView castName = new TextView(this);
        castName.setText(cast.getName());

        castName.setTextSize(8);
        castName.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 1f));
        castName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        castName.setSingleLine(false);
        castName.setMinLines(2);

        linearLayout.addView(imageView);
        linearLayout.addView(castName);

        return linearLayout;
    }


}
