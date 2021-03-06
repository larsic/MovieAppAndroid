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
import com.omertron.themoviedbapi.enumeration.SearchType;
import com.omertron.themoviedbapi.model.Genre;
import com.omertron.themoviedbapi.model.config.Configuration;
import com.omertron.themoviedbapi.model.credits.MediaCreditCast;
import com.omertron.themoviedbapi.model.discover.Discover;
import com.omertron.themoviedbapi.model.media.MediaCreditList;
import com.omertron.themoviedbapi.model.movie.MovieBasic;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.results.ResultList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    List<MediaCreditCast> cast = new ArrayList<>();
    List<Genre> genres;





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
            fetchGenres();
            fetchMovieInfo();

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

    private void fetchGenres() {

        FetchGenres fetchGenres = new FetchGenres();
        fetchGenres.execute();
    }

    public AppController() {
    }

    public void fetchMovies(String what) {
        FetchMovies fetchMovies = new FetchMovies(what);
        fetchMovies.execute();
    }

    public void fetchMovieInfo() {
        FetchMovieInfo fetchMovieInfo = new FetchMovieInfo();
        fetchMovieInfo.execute();
    }

    public void fetchOneMovie(int id){
        FetchUniqueMovieInfo fetchIt = new FetchUniqueMovieInfo();
        fetchIt.execute(id);
    }

    public void fetchCast(int id){
        FetchCastFromMovie fetchItAgain = new FetchCastFromMovie();
        fetchItAgain.execute(id);
    }

    public void searchMovieByTitle(String query){
        SearchMovieByTitle searchIt = new SearchMovieByTitle();
        searchIt.execute(query);
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

    public void fetchMovieByGenre(int which) {
        FetchMovieByGenre fetchIt = new FetchMovieByGenre();
        fetchIt.execute(which);
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

    private class FetchGenres extends AsyncTask<Void, Void, ResultList<Genre>>{

        @Override
        protected ResultList<Genre> doInBackground(Void... params) {
            try {
                return api.getGenreMovieList("en-US");
            } catch (MovieDbException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResultList<Genre> genreList) {
            super.onPostExecute(genreList);


            Log.v("Found", genreList.toString());
           genres = genreList.getResults();
        }
    }


    private class FetchMovieByGenre extends AsyncTask<Integer, Void, ResultList<MovieBasic>>{

        @Override
        protected ResultList<MovieBasic> doInBackground(Integer... params) {
            try {
                Discover discoverByGenre = new Discover();
//                discoverByGenre.language("en-US");
  //             discoverByGenre.sortBy(SortBy.PRIMARY_RELEASE_DATE_DESC);
//                Date today = new Date();
//               SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//               discoverByGenre.releaseDateLte(format.format(today));
               discoverByGenre.withGenres(""+genres.get(params[0]).getId());



                return api.getDiscoverMovies(discoverByGenre);
                //return api.getGenreMovies(genres.get(params [0]).getId(), "en-US", 1, false, false);
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

    private class FetchMovies extends AsyncTask<Object, Object, ResultList<MovieInfo>> {

        private String task;

        public FetchMovies(String task) {
            this.task = task;
        }

        @Override
        protected ResultList<MovieInfo> doInBackground(Object... params) {
            try {
                if(task.equals("top-rated")) {
                    return api.getTopRatedMovies(1, "en-US");
                }
                else if(task.equals("now-playing")) {
                    return api.getNowPlayingMovies(1, "en-US");
                }
                else {
                    return api.getUpcoming(1, "en-US");
                    }
                }
            catch (MovieDbException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResultList<MovieInfo> movieBasicResultList) {
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
                return api.getMovieInfo(params[0].intValue(), "en", "videos");
            } catch (MovieDbException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieInfo movieInfo) {
            System.out.println("moviedetail "+movieInfo);
            super.onPostExecute(movieInfo);
            // todo intent movieDetails, pass through movieInfo in extras
            Intent gogo = new Intent(getBaseContext(), MovieDetails.class);
            gogo.putExtra("movieInfo", movieInfo);
            gogo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(gogo);
        }
    }
    //cast
    private class FetchCastFromMovie extends AsyncTask<Integer, Void, MediaCreditList>{

        @Override
        protected MediaCreditList doInBackground(Integer... params) {
            try {

                return api.getMovieCredits(params[0].intValue());
            } catch (MovieDbException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MediaCreditList media) {
            super.onPostExecute(media);
            // todo intent movieDetails, pass through movieInfo in extras

            cast.clear();
            cast.addAll(media.getCast());
            notifyAllListeners();

        }
    }

    private class SearchMovieByTitle extends AsyncTask<String, Void, ResultList<MovieInfo>> {

        @Override
        protected ResultList<MovieInfo> doInBackground(String... params) {
            try {
                return api.searchMovie(params[0], 1, "en", false, null, null, SearchType.PHRASE);
                //return api.getMovieCredits(params[0].intValue());
            } catch (MovieDbException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResultList<MovieInfo> media) {
            super.onPostExecute(media);

            movieList.clear();
            movieList.addAll(media.getResults());
            notifyAllListeners();

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

    public List<Genre> getGenres() {
        return genres;
    }
}
