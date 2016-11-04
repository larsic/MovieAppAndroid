package lars.be.movieapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.model.config.Configuration;
import com.omertron.themoviedbapi.model.movie.MovieBasic;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;



/**
 * Created by brandonstark on 19/10/16.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> {


    private Context context;
    private List<MovieBasic> movieList;


    public MovieListAdapter(Context context, List<MovieBasic> movieList) {
        this.context = context;
        this.movieList = movieList;

    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        final MovieBasic currentMovie = movieList.get(position);
        holder.getTitleTextView().setText(currentMovie.getTitle());
        holder.getDescriptionTextView().setText(currentMovie.getOverview());
        holder.getUserRatingView().setText(MessageFormat.format(context.getString(R.string.voteAverage), currentMovie.getVoteAverage(), currentMovie.getVoteCount()));
        String rDate = currentMovie.getReleaseDate();

        if (rDate.length() > 3) {

            holder.getReleaseDateView().setText(MessageFormat.format(context.getString(R.string.releaseYear), rDate.substring(0, 4)));
        } else {
            holder.getReleaseDateView().setText("");
        }
        holder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getInstance().fetchOneMovie(currentMovie.getId());


            }
        });


        URL imageUrl = null;
        String pathIsEmpty = currentMovie.getPosterPath();
        imageUrl = AppController.getInstance().createImageUrl(pathIsEmpty, "w92");
        if(pathIsEmpty != null){
            ImageLoader.getInstance().displayImage(imageUrl.toString(), holder.getImageView());
        } else {
            holder.getImageView().setImageResource(R.drawable.ic_menu_camera);
        }







    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void insertMovie(int position, MovieBasic movie) {
        movieList.add(position, movie);
        notifyItemInserted(position);


    }

    public void removeMovie(MovieBasic movie) {
        int position = movieList.indexOf(movie);
        movieList.remove(position);
        notifyItemRemoved(position);
    }

 /*   public void animate(RecyclerView.ViewHolder viewHolder){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce_animation);
        viewHolder.itemView.setAnimation(animation);
    } */

}
