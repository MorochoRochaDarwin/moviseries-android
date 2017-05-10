package xyz.moviseries.moviseries.models;

/**
 * Created by DARWIN on 6/5/2017.
 */

public class Serie {
    protected String serie_id;
    protected String serie_name;
    protected String year;
    protected String cover;
    protected String short_description;
    protected String created_at;


    public String getSerie_id() {
        return serie_id;
    }

    public String getSerie_name() {
        return serie_name;
    }

    public String getYear() {
        return year;
    }

    public String getCover() {
        return cover;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getCreated_at() {
        return created_at;
    }
}
