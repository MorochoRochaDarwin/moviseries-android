package xyz.moviseries.moviseries.models;

/**
 * Created by DARWIN on 6/5/2017.
 */

public class Movie {
    protected String movie_id;
    protected String name;
    protected String year;
    protected String cover;
    protected String trailer;
    protected String short_description;
    protected String created_at;
    protected String updated_at;

    public String getMovie_id() {
        return movie_id;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getCover() {
        return cover;
    }

    public String getTrailer() {
        return trailer;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
