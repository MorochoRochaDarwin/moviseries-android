package xyz.moviseries.moviseries.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import xyz.moviseries.moviseries.MovieQualities;
import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.models.Movie;
import xyz.moviseries.moviseries.models.Quality;

/**
 * Created by DARWIN on 6/5/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {
    private Context context;
    private ArrayList<MovieQualities> movies;

    public MoviesAdapter(Context context, ArrayList<MovieQualities> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieHolder(v);
    }

    private Bitmap bmp;

    @Override
    public void onBindViewHolder(final MovieHolder holder, int position) {
        final MovieQualities movie = movies.get(position);
        List<Quality> qualities = movie.getQualities();

        String tmp = qualities.get(0).getQuality();
        for (int i = 1; i < qualities.size(); i++) {
            tmp += " | " + qualities.get(i).getQuality();
        }

        holder.qualities.setText(tmp);


        try {
            Picasso.with(context)
                    .load(movie.getMovie().getCover())
                    .resize(351, 526)
                    .centerCrop()
                    .into(holder.cover);
        } catch (Exception e) {
            Log.i("apimoviseries", " err: " + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


    class MovieHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView qualities;

        public MovieHolder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.cover);
            qualities = (TextView) itemView.findViewById(R.id.qualities);
        }
    }
}
