package lars.be.movieapp;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.config.Configuration;
import com.omertron.themoviedbapi.model.discover.Discover;
import com.omertron.themoviedbapi.model.movie.MovieBasic;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.results.ResultList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static lars.be.movieapp.R.id.recyclerView;

/**
 * Created by brandonstark on 25/10/16.
 */
public class AppController extends Application{


    private static AppController mInstance;
    private TheMovieDbApi api;
    private Configuration configuration;
    private List<MovieBasic> movieList = new ArrayList<>();
    private List<OnMovieListChangedListener> allListeners = new ArrayList<>();
    private MovieInfo movieDetails;


    public static AppController getInstance(){
        return mInstance;
    }

    public MovieInfo getMovieDetails() {
        return movieDetails;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        try{
            api = new TheMovieDbApi(getString(R.string.apiKey));
            FetchConfiguration fetchConfiguration =  new FetchConfiguration();
            fetchConfiguration.execute();
            FetchMovieInfo fetchMovieInfo = new FetchMovieInfo();
            fetchMovieInfo.execute();
        } catch (MovieDbException e) {
            e.printStackTrace();
            Log.e("TheMovieDBApi", "Error: "+ e.getMessage());
        }

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.ic_hourglass_empty_black_24dp)
                .showImageOnLoading(R.drawable.ic_hourglass_empty_black_24dp)
                .displayer(new FadeInBitmapDisplayer(500))
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .denyCacheImageMultipleSizesInMemory()
                .build();

        ImageLoader.getInstance().init(config);
    }

    public AppController() {
    }

    public void fetchOneMovie(int id){
        FetchUniqueMovieInfo fetchIt = new FetchUniqueMovieInfo();
        fetchIt.execute(id);
    }

    public List<MovieBasic> getMovieList() {
        return movieList;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public TheMovieDbApi getApi() {
        return api;
    }

    public interface OnMovieListChangedListener {
        void onMovieListChanged();
    }

    public void addOnMovieListChangedListener(OnMovieListChangedListener listener) {
        allListeners.add(listener);
    }
    public void removeOnMovieListChangedListener(OnMovieListChangedListener listener) {
        allListeners.remove(listener);
    }
    private void notifyAllListeners(){
        for(OnMovieListChangedListener listener: allListeners){
            listener.onMovieListChanged();
        }
    }

    private class FetchConfiguration extends AsyncTask<Void, Void, Configuration> {
        @Override
        protected Configuration doInBackground(Void... params){
            try {
                return api.getConfiguration();
            }catch (MovieDbException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Configuration configuration){
            AppController.this.configuration = configuration;

        }
    }


    private class FetchMovieInfo extends AsyncTask<Void, Void, ResultList<MovieBasic>>{

        @Override
        protected ResultList<MovieBasic> doInBackground(Void... params) {
            try {
                return api.getDiscoverMovies(new Discover());
            } catch (MovieDbException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResultList<MovieBasic> movieBasicResultList) {
            super.onPostExecute(movieBasicResultList);


            Log.v("Found", movieBasicResultList.toString());
            movieList.clear();
            movieList.addAll(movieBasicResultList.getResults());
            notifyAllListeners();
        }
    }

    private class FetchUniqueMovieInfo extends AsyncTask<Integer, Void, MovieInfo>{

        @Override
        protected MovieInfo doInBackground(Integer... params) {
            try {
                return api.getMovieInfo(params[0].intValue(), "en");
            } catch (MovieDbException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieInfo movieInfo) {
            super.onPostExecute(movieInfo);
            // todo intent movieDetails, pass through movieInfo in extras
            Intent gogo = new Intent(getBaseContext(), MovieDetails.class);
            gogo.putExtra("movieInfo", movieInfo);
        }
    }


    public URL createImageUrl(String imagePath, String imageSize){
        try {
            return configuration.createImageUrl(imagePath, imageSize);
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;

    }



}
