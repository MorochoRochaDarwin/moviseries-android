package xyz.moviseries.moviseries.models;

/**
 * Created by DARWIN on 6/5/2017.
 */

public class Movie {
    private String movie_id;
    private String name;
    private String year;
    private String cover;
    private String trailer;
    private String short_description;
    private String created_at;
    private String updated_at;

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
