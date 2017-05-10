package xyz.moviseries.moviseries.models;

import java.util.List;

/**
 * Created by DARWIN on 10/5/2017.
 */

public class ViewSerie {
    private SerieScore serie;
    private List<SeasonSerie> seasons;
    private List<MEGAUrlSerie> mega_urls;


    public SerieScore getSerie() {
        return serie;
    }

    public List<SeasonSerie> getSeasons() {
        return seasons;
    }

    public List<MEGAUrlSerie> getMega_urls() {
        return mega_urls;
    }
}
