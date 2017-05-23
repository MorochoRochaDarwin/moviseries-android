package xyz.moviseries.moviseries.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import xyz.moviseries.moviseries.MovieQualities;
import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.models.Quality;
import xyz.moviseries.moviseries.models.TopMovie;

/**
 * Created by DARWIN on 6/5/2017.
 */

public class TopMoviesAdapter extends RecyclerView.Adapter<TopMoviesAdapter.MovieHolder> {
    private Context context;
    private ArrayList<TopMovie> movies;

    public TopMoviesAdapter(Context context, ArrayList<TopMovie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_top_movie, parent, false);
        return new MovieHolder(v);
    }

    private Bitmap bmp;

    @Override
    public void onBindViewHolder(final MovieHolder holder, int position) {
        final TopMovie movie = movies.get(position);

        //holder.ratingBar.setRating(Float.parseFloat(movie.getScore()));

        float score = Float.parseFloat(movie.getScore());
        if (score >= 0 && score < 2) {
            holder.mSmileRating.setSelectedSmile(BaseRating.TERRIBLE);
        } else if (score >= 2 && score < 4) {
            holder.mSmileRating.setSelectedSmile(BaseRating.BAD);
        } else if (score >= 4 && score < 6) {
            holder.mSmileRating.setSelectedSmile(BaseRating.OKAY);
        } else if (score >= 6 && score < 8) {
            holder.mSmileRating.setSelectedSmile(BaseRating.GOOD);
        } else {
            holder.mSmileRating.setSelectedSmile(BaseRating.GREAT);
        }


        Uri uri = Uri.parse(movie.getCover());
        holder.cover.setImageURI(uri);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTopMovieListener.onClickTopMovie(movie);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


    class MovieHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView cover;
        SmileRating mSmileRating;

        public MovieHolder(View itemView) {
            super(itemView);
            cover = (SimpleDraweeView) itemView.findViewById(R.id.cover);
            mSmileRating = (SmileRating) itemView.findViewById(R.id.ratingView);

            mSmileRating.setNameForSmile(BaseRating.TERRIBLE, "Terrible");
            mSmileRating.setNameForSmile(BaseRating.BAD, "Mala");
            mSmileRating.setNameForSmile(BaseRating.OKAY, "Regular");
            mSmileRating.setNameForSmile(BaseRating.GOOD, "Buena");
            mSmileRating.setNameForSmile(BaseRating.GREAT, "Excelente");
        }
    }



    public  interface  OnTopMovieListener{
       void onClickTopMovie (TopMovie movie);
    }

    private OnTopMovieListener onTopMovieListener;

    public void setOnTopMovieListener(OnTopMovieListener onTopMovieListener) {
        this.onTopMovieListener = onTopMovieListener;
    }
}
