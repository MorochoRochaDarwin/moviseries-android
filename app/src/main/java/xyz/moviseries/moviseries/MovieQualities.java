package xyz.moviseries.moviseries;

import java.util.List;

import xyz.moviseries.moviseries.models.Movie;
import xyz.moviseries.moviseries.models.Quality;

/**
 * Created by DARWIN on 6/5/2017.
 */

public class MovieQualities {
    private Movie movie;
    private List<Quality> qualities;


    public List<Quality> getQualities() {
        return qualities;
    }

    public Movie getMovie() {
        return movie;
    }
}
