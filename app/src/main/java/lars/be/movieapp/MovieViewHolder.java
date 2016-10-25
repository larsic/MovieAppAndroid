package lars.be.movieapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by brandonstark on 19/10/16.
 */

public class MovieViewHolder extends RecyclerView.ViewHolder {

    private CardView cardView;
    private ImageView imageView;
    private TextView titleTextView;
    private  TextView descriptionTextView;
    private TextView userRatingView;
    private TextView releaseDateView;

    public MovieViewHolder(View view) {
        super(view);
        cardView = (CardView) view.findViewById(R.id.cardView);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        titleTextView = (TextView) itemView.findViewById(R.id.title);
        descriptionTextView = (TextView) itemView.findViewById(R.id.description);
        userRatingView = (TextView) itemView.findViewById(R.id.userRating);
        releaseDateView = (TextView) itemView.findViewById(R.id.releaseDate);
    }

    public TextView getReleaseDateView() {
        return releaseDateView;
    }

    public void setReleaseDateView(TextView releaseDateView) {
        this.releaseDateView = releaseDateView;
    }

    public CardView getCardView() {
        return cardView;
    }

    public void setCardView(CardView cardView) {
        this.cardView = cardView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

    public TextView getDescriptionTextView() {
        return descriptionTextView;
    }

    public void setDescriptionTextView(TextView descriptionTextView) {
        this.descriptionTextView = descriptionTextView;
    }

    public TextView getUserRatingView() {
        return userRatingView;
    }

    public void setUserRatingView(TextView userRatingView) {
        this.userRatingView = userRatingView;
    }
}
