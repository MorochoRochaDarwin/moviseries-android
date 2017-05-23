package xyz.moviseries.moviseries.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import xyz.moviseries.moviseries.MovieQualities;
import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.custom_views.DMTextView;
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

        holder.name.setText(movie.getMovie().getName());

        List<Quality> qualities = movie.getQualities();

        String tmp = qualities.get(0).getQuality();
        for (int i = 1; i < qualities.size(); i++) {
            tmp += " | " + qualities.get(i).getQuality();
        }

        holder.qualities.setText(tmp);
        final String squalities = tmp;

        Uri uri = Uri.parse(movie.getMovie().getCover());
        holder.cover.setImageURI(uri);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movieOnclickListener.MovieOptionsClick(movie, squalities);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


    class MovieHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView cover;
        DMTextView description;
        TextView name, updated_at, qualities;
        LinearLayout item;

        public MovieHolder(View itemView) {
            super(itemView);
            cover = (SimpleDraweeView) itemView.findViewById(R.id.cover);
            name = (TextView) itemView.findViewById(R.id.name);
            qualities = (TextView) itemView.findViewById(R.id.qualities);
            item = (LinearLayout) itemView.findViewById(R.id.item);

        }
    }


    public interface MovieOnclickListener {
        void MovieOptionsClick(MovieQualities movie, String qualities);
    }


    private MovieOnclickListener movieOnclickListener;

    public void setMovieOnclickListener(MovieOnclickListener movieOnclickListener) {
        this.movieOnclickListener = movieOnclickListener;
    }
}
